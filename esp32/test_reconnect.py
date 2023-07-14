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

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

async def main():
  parser = argparse.ArgumentParser(
    prog = "Bluetooth LE reader",
    description = "Tool to read all characteristics of a Bluetooth LE server.")

  parser.add_argument("-n", "--runs", type=int, default=10, help="number of runs to do")
  parser.add_argument("address", help="MAC address of the BLE server")

  args = parser.parse_args()

  try:
    runs = args.runs
    if runs <= 0:
      runs = 1
    while runs > 0:
      runs -= 1
      logging.info(f"--- run started (remaining runs: {runs}) ---")

      client = BleakClient(args.address, timeout=1.0)
      await client.connect()
      await client.disconnect()

      logging.info("--- run complete ---")

  except KeyboardInterrupt:
    exit(0)

if __name__ == "__main__":
  asyncio.run(main())
