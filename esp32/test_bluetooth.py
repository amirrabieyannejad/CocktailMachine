#!/usr/bin/env python3

# bluetooth
from bleak import BleakClient

# utilities
import argparse
import asyncio
import json
import logging
import platform
import sys
import time

UUID_COMM_USER 	= "eb61e31a-f00b-335f-ad14-d654aac8353d"
UUID_COMM_ADMIN	= "41044979-6a5d-36be-b9f1-d4d49e3f5b73"

STATUS = {
  "base":     	"c0605c38-3f94-33f6-ace6-7a5504544a80",
  "state":    	"e9e4b3f2-fd3f-3b76-8688-088a0671843a",
  "liquids":  	"fc60afb0-2b00-3af2-877a-69ae6815ca2f",
  "pumps":    	"1a9a598a-17ce-3fcd-be03-40a48587d04e",
  "recipes":  	"9ede6e03-f89b-3e52-bb15-5c6c72605f6c",
  "cocktail": 	"7344136f-c552-3efc-b04f-a43793f16d43",
  "timestamp":	"586b5706-5856-34e1-ad17-94f840298816",
  "user":     	"2ce478ea-8d6f-30ba-9ac6-2389c8d5b172",
  "scale":    	"ff18f0ac-f039-4cd0-bee3-b546e3de5551",
  "error":    	"2e03aa0c-b25f-456a-a327-bd175771111a",
}

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

async def read_all_chars(client):
  logging.info("reading all chars...")

  for service in client.services:
    logging.info(f"  service: {service}")

    for char in service.characteristics:
      if "read" in char.properties:
        try:
          value = bytes(await client.read_gatt_char(char.uuid)).decode('utf-8')
          logging.info(f"  - read: {char.uuid} -> {value}")
        except Exception as e:
          logging.error(f"\t[Characteristic] {char} ({','.join(char.properties)}), Value: {e}")

async def read_status(client):
  logging.info("reading status...")

  for char, uuid in STATUS.items():
    try:
      value = bytes(await client.read_gatt_char(uuid)).decode('utf-8')
      logging.info(f"  - {char} -> {value}")
    except Exception as e:
      logging.error(f"\t{char} ({uuid}) -> {e}")

async def comm_msg(client, uuid, message, wait=None, wait_time=30.0):
  notification_received1 = asyncio.Event()
  notification_received2 = asyncio.Event()

  def notification_handler1(_sender, _data):
    notification_received1.set()
  def notification_handler2(_sender, _data):
    notification_received2.set()

  if wait:
    logging.info(f"waiting for «{wait}»")
    state_uuid = STATUS["state"]
    start = time.time()
    end = start + wait_time

    await client.start_notify(state_uuid, notification_handler1)

    prev = None
    while True:
      if time.time() > end:
        logging.info("(timeout, cancelling)")
        await client.stop_notify(state_uuid)
        break

      value = bytes(await client.read_gatt_char(state_uuid)).decode('utf8')
      if value != prev:
        logging.info(f"  is: {value}")
        prev = value

      if value == "\"" + wait + "\"":
        await client.stop_notify(state_uuid)
        break

      time.sleep(1)
      await notification_received1.wait()
      notification_received1.clear()

  await client.start_notify(uuid, notification_handler2)

  logging.info(f"message: {message.decode('utf8')}")
  await client.write_gatt_char(uuid, message)

  while True:
    await notification_received2.wait()
    value = bytes(await client.read_gatt_char(uuid)).decode('utf8')
    if value != "processing":
      logging.info(f"-> response: {value}")
      break

  await client.stop_notify(uuid)  # stop notifications

def j(obj):
  return json.dumps(obj, ensure_ascii=False).encode('utf8')

async def test_run(client):
  async def user(obj, rec_wait=None):
    await comm_msg(client, UUID_COMM_USER, j(obj), wait=rec_wait)

  async def admin(obj, cal_wait=None):
    await comm_msg(client, UUID_COMM_ADMIN, j(obj), wait=cal_wait)

  # reset machine settings
  await admin({"cmd": "factory_reset", "user": 0})
  await read_status(client)

  # basic commands
  await user({"cmd": "test"})
  await user({"cmd": "init_user", "name": "test-user"})

  # admin commands
  await admin({"cmd": "clean", "user": 0})

  # manually calibrate scale (with arbitrary values)
  await admin({"cmd": "tare_scale", "user": 0})
  await admin({"cmd": "calibrate_scale",  "user": 0, "weight": 100})
  await admin({"cmd": "set_scale_factor", "user": 0, "factor": 1.0})

  # set up pumps
  await admin({"cmd": "define_pumps", "user": 0, "liquid": "none", "volume": 0, "quantity": 2})
  await read_status(client)

  await admin({"cmd": "define_pump", "user": 0, "liquid": "water",    "volume": 1000, "slot": 1})
  await admin({"cmd": "define_pump", "user": 0, "liquid": "beer",     "volume": 2000, "slot": 2})
  await admin({"cmd": "define_pump", "user": 0, "liquid": "water"   , "volume": 0,    "slot": 7})
  await admin({"cmd": "edit_pump",   "user": 0, "liquid": "lemonade", "volume": 3000, "slot": 7})
  await read_status(client)

  # manually calibrate pumps
  await admin({"cmd": "run_pump", "user": 0, "slot": 1, "time": 10})
  await admin({"cmd": "run_pump", "user": 0, "slot": 2, "time": 10})
  await admin({"cmd": "run_pump", "user": 0, "slot": 7, "time": 10})
  await read_status(client)

  await admin({"cmd": "calibrate_pump", "user": 0, "slot": 1,
               "time1": 11, "volume1": 10.0,
               "time2": 21, "volume2": 20.0})
  await admin({"cmd": "set_pump_times", "user": 0, "slot": 2,
               "time_init": 1, "time_reverse": 1, "rate": 1.0})
  await admin({"cmd": "set_pump_times", "user": 0, "slot": 7,
               "time_init": 1, "time_reverse": 1, "rate": 1.0})

  # refill and reset pumps
  await admin({"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1})
  await admin({"cmd": "refill_pump", "user": 0, "volume": 2000, "slot": 2})
  await admin({"cmd": "refill_pump", "user": 0, "volume": 3000, "slot": 7})

  await admin({"cmd": "reset", "user": 0})
  await read_status(client)

  # define recipes
  await user({"cmd": "define_recipe", "user": 1, "name": "radler",     "ingredients": [["beer", 250], ["lemonade", 250]]})
  await user({"cmd": "define_recipe", "user": 1, "name": "cheap beer", "ingredients": [["beer", 250], ["water", 250]]})
  await user({"cmd": "edit_recipe",   "user": 1, "name": "cheap beer", "ingredients": [["beer", 100], ["water", 400]]})
  await user({"cmd": "delete_recipe", "user": 0, "name": "cheap beer"})

  # make recipes
  await user({"cmd": "queue_recipe",  "user": 1, "recipe": "radler"}, rec_wait="ready")
  await user({"cmd": "start_recipe",  "user": 1}, rec_wait="waiting for container")
  await user({"cmd": "take_cocktail", "user": 1}, rec_wait="cocktail done")

  await read_status(client)

  await user({"cmd": "queue_recipe",  "user": 1, "recipe": "radler"})
  await user({"cmd": "add_liquid",    "user": 1, "liquid": "beer", "volume": 100})
  await user({"cmd": "start_recipe",  "user": 1}, rec_wait="waiting for container")
  await user({"cmd": "take_cocktail", "user": 1}, rec_wait="cocktail done")

  await read_status(client)

  await user({"cmd": "queue_recipe",  "user": 1, "recipe": "radler"})
  await user({"cmd": "start_recipe",  "user": 1}, rec_wait="waiting for container")
  await user({"cmd": "add_liquid",    "user": 1, "liquid": "beer", "volume": 100})
  await user({"cmd": "take_cocktail", "user": 1}, rec_wait="cocktail done")

  await read_status(client)

  # this should fail
  await user({"cmd":  "add_liquid",  "user": 1, "liquid": "beer", "volume": 100})
  await admin({"cmd": "reset_error", "user": 0})
  await read_status(client)

  # automatically calibrate pumps
  if True:
    for n in range(1):
      # refill and reset pumps
      await admin({"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1})
      await admin({"cmd": "refill_pump", "user": 0, "volume": 2000, "slot": 2})
      await admin({"cmd": "refill_pump", "user": 0, "volume": 3000, "slot": 7})

      await admin({"cmd": "reset", "user": 0})
      await read_status(client)

      await admin({"cmd": "calibration_start",  "user": 0})
      await admin({"cmd": "calibration_cancel", "user": 0}) # try cancelling immediately

      await admin({"cmd": "calibration_start",      "user": 0})
      await admin({"cmd": "calibration_add_empty",  "user": 0},                 	cal_wait="calibration empty container")
      await admin({"cmd": "calibration_add_weight", "user": 0, "weight": 100.0},	cal_wait="calibration known weight")
      await admin({"cmd": "calibration_add_empty",  "user": 0},                 	cal_wait="calibration empty container")

      # reset the scale factor because this is just a basic test without any weights
      await admin({"cmd": "set_scale_factor", "user": 0, "factor": 1.0})

      for cal_pass in range(2):
        for pump in range(3):
          await admin({"cmd": "calibration_add_empty", "user": 0}, cal_wait="calibration empty container")

      await admin({"cmd": "calibration_finish", "user": 0}, cal_wait="calibration done")
      await read_status(client)

    await admin({"cmd": "reset", "user": 0})
    await admin({"cmd": "reset_error", "user": 0}) # assume calibration data is wrong
    await read_status(client)

  await read_all_chars(client)

  # reset machine again
  await admin({"cmd": "factory_reset", "user": 0})

async def main():
  parser = argparse.ArgumentParser(
    prog = "Bluetooth LE reader",
    description = "Tool to read all characteristics of a Bluetooth LE server.")

  parser.add_argument("-r", "--repeat", type=bool, help="repeated runs")
  parser.add_argument("-s", "--sleep", type=float, default=1, help="how long to wait between runs (in seconds)")
  parser.add_argument("address", help="MAC address of the BLE server")

  args = parser.parse_args()

  try:
    runs = 0
    while args.repeat or runs < 1:
      runs += 1
      logging.info(f"--- run started (#{runs}) ---")

      async with BleakClient(args.address) as client:
        await test_run(client)

      logging.info("--- run complete ---")
      time.sleep(args.sleep)

  except KeyboardInterrupt:
    exit(0)

if __name__ == "__main__":
  asyncio.run(main())
