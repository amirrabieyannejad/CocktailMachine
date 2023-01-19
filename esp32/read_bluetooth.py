#!/usr/bin/env python3

# bluetooth
from bleak import BleakClient

# utilities
import argparse
import asyncio
import logging
import platform
import sys
import time

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

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
        print("Services:")
        for service in client.services:
          logging.info(f"service: {service}")

            for char in service.characteristics:

            if "read" in char.properties:
              try:
                value = bytes(await client.read_gatt_char(char.uuid))
                logging.info(f"read: {char.uuid} -> {value})")
              except Exception as e:
                logging.error(f"\t[Characteristic] {char} ({','.join(char.properties)}), Value: {e}")

      logging.info("--- scan complete ---")
      time.sleep(args.sleep)

  except KeyboardInterrupt:
    pass

  finally:
    peripheral.disconnect()

if __name__ == "__main__":
  asyncio.run(main())
