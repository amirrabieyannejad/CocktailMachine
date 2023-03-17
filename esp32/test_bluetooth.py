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

UUID_STATUS         	= "0f7742d4-ea2d-43c1-9b98-bb4186be905d"
UUID_STATUS_BASE    	= "c0605c38-3f94-33f6-ace6-7a5504544a80"
UUID_STATUS_STATE   	= "e9e4b3f2-fd3f-3b76-8688-088a0671843a"
UUID_STATUS_LIQUIDS 	= "fc60afb0-2b00-3af2-877a-69ae6815ca2f"
UUID_STATUS_RECIPES 	= "9ede6e03-f89b-3e52-bb15-5c6c72605f6c"
UUID_STATUS_COCKTAIL	= "7344136f-c552-3efc-b04f-a43793f16d43"
UUID_COMM           	= "dad995d1-f228-38ec-8b0f-593953973406"
UUID_COMM_USER      	= "eb61e31a-f00b-335f-ad14-d654aac8353d"
UUID_COMM_ADMIN     	= "41044979-6a5d-36be-b9f1-d4d49e3f5b73"
UUID_NOTIFY         	= "00002902-0000-1000-8000-00805f9b34fb"

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

async def read_all_chars(client):
  logging.info("reading all chars...")

  for service in client.services:
    logging.info(f"  service: {service}")

    for char in service.characteristics:
      if "read" in char.properties:
        try:
          value = bytes(await client.read_gatt_char(char.uuid))
          logging.info(f"  - read: {char.uuid} -> {value})")
        except Exception as e:
          logging.error(f"\t[Characteristic] {char} ({','.join(char.properties)}), Value: {e}")

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

async def main():
  parser = argparse.ArgumentParser(
    prog = "Bluetooth LE reader",
    description = "Tool to read all characteristics of a Bluetooth LE server.")

  parser.add_argument("-s", "--sleep", type=float, default=1, help="how long to wait between reads (in seconds)")
  parser.add_argument("address", help="MAC address of the BLE server")

  args = parser.parse_args()

  try:
    while True:
      logging.info("--- scan started ---")
      async with BleakClient(args.address) as client:
        # basic commands
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "test"}))
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "init_user", "name": "test-user"}))

        # admin commands
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "reset", "user": 0}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "calibrate_pumps", "user": 0}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "clean", "user": 0}))

        # set up machine
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "define_pump", "user": 0, "liquid": "water",    "volume": 1000, "slot": 1}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "define_pump", "user": 0, "liquid": "beer",     "volume": 2000, "slot": 2}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "define_pump", "user": 0, "liquid": "lemonade", "volume": 3000, "slot": 3}))

        # define recipes
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "define_recipe", "user": 1, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}))
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "define_recipe", "user": 1, "name": "cheap beer", "liquids": [["beer", 250], ["water", 250]]}))
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "edit_recipe", "user": 1, "name": "cheap beer", "liquids": [["beer", 100], ["water", 400]]}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "delete_recipe", "user": 1, "name": "cheap beer"}))

        # make recipes
        await comm_msg(client, UUID_COMM_USER, j({"cmd": "make_recipe", "user": 1, "recipe": "radler"}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "add_liquid", "user": 0, "liquid": "beer", "volume": 100}))
        await comm_msg(client, UUID_COMM_ADMIN, j({"cmd": "reset", "user": 0}))

        await read_all_chars(client)

      logging.info("--- scan complete ---")
      time.sleep(args.sleep)

  except KeyboardInterrupt:
    exit(0)

if __name__ == "__main__":
  asyncio.run(main())
