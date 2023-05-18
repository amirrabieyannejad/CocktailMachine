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
}

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

async def read_all_chars(client):
  logging.info("reading all chars...")

  for service in client.services:
    logging.info(f"  service: {service}")

    for char in service.characteristics:
      if "read" in char.properties:
        try:
          value = bytes(await client.read_gatt_char(char.uuid))
          logging.info(f"  - read: {char.uuid} -> {value.decode('utf-8')}")
        except Exception as e:
          logging.error(f"\t[Characteristic] {char} ({','.join(char.properties)}), Value: {e}")

async def read_status(client):
  logging.info("reading status...")

  for char, uuid in STATUS.items():
    try:
      value = bytes(await client.read_gatt_char(uuid))
      logging.info(f"  - {char} -> {value.decode('utf-8')}")
    except Exception as e:
      logging.error(f"\t{char} ({uuid}) -> {e}")

async def comm_msg(client, uuid, message):
  notification_received = asyncio.Event()
  def notification_handler(_sender, _data):
    notification_received.set()

  await client.start_notify(uuid, notification_handler)

  logging.info(f"message: {message.decode('utf8')}")
  await client.write_gatt_char(uuid, message)

  await notification_received.wait()
  value = bytes(await client.read_gatt_char(uuid))
  logging.info(f"-> response: {value.decode('utf8')}")

  await client.stop_notify(uuid)  # stop notifications

def j(obj):
  return json.dumps(obj, ensure_ascii=False).encode('utf8')

async def test_run(client):
  async def user(obj):
    await comm_msg(client, UUID_COMM_USER, j(obj))

  async def admin(obj):
    await comm_msg(client, UUID_COMM_ADMIN, j(obj))

  # reset machine settings
  await admin({"cmd": "factory_reset", "user": 0})
  await read_status(client)

  # basic commands
  await user({"cmd": "test"})
  await user({"cmd": "init_user", "name": "test-user"})

  # admin commands
  await user({"cmd": "reset", "user": 0})
  await admin({"cmd": "calibrate_pumps", "user": 0})
  await admin({"cmd": "clean", "user": 0})

  # set up machine
  await admin({"cmd": "define_pump", "user": 0, "liquid": "water",    "volume": 1000, "slot": 1})
  await admin({"cmd": "define_pump", "user": 0, "liquid": "beer",     "volume": 2000, "slot": 2})
  await admin({"cmd": "define_pump", "user": 0, "liquid": "lemonade", "volume": 3000, "slot": 3})

  # define recipes
  await user({"cmd": "define_recipe", "user": 1, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]})
  await user({"cmd": "define_recipe", "user": 1, "name": "cheap beer", "liquids": [["beer", 250], ["water", 250]]})
  await user({"cmd": "edit_recipe", "user": 1, "name": "cheap beer", "liquids": [["beer", 100], ["water", 400]]})
  await admin({"cmd": "delete_recipe", "user": 0, "name": "cheap beer"})

  await read_status(client)

  # make recipes
  await user({"cmd": "make_recipe", "user": 1, "recipe": "radler"})
  await user({"cmd": "add_liquid", "user": 1, "liquid": "beer", "volume": 100})
  await user({"cmd": "reset", "user": 0})

  await user({"cmd": "make_recipe", "user": 1, "recipe": "radler"})
  await user({"cmd": "add_liquid", "user": 1, "liquid": "beer", "volume": 100})

  await read_status(client)

  # refill pump
  await user({"cmd": "add_liquid", "user": 1, "volume": 10, "liquid": "water"})
  await user({"cmd": "add_liquid", "user": 1, "volume": 10, "liquid": "beer"})
  await user({"cmd": "add_liquid", "user": 1, "volume": 10, "liquid": "water"})
  await admin({"cmd": "refill_pump", "user": 0, "volume": 5000, "slot": 1})
  await read_status(client)

  await read_all_chars(client)

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
