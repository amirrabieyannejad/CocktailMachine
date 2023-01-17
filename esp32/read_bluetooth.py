#!/usr/bin/env python3

# bluetooth
from bluepy import btle
from bluepy.btle import Peripheral, Characteristic, UUID

# utilities
import argparse
import logging
import time

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

def main():
  parser = argparse.ArgumentParser(
    prog = "Bluetooth LE reader",
    description = "Tool to read all characteristics of a Bluetooth LE server.")

  parser.add_argument("-s", "--sleep", type=float, default=1, help="how long to wait between reads (in seconds)")
  parser.add_argument("address", help="MAC address of the BLE server")

  args = parser.parse_args()

  peripheral = Peripheral()

  try:
    peripheral.connect(args.address)

    while True:
      logging.info("--- scan started ---")
      services = peripheral.getServices()

      for service in services:
        logging.info(f"service: {service.uuid}")
        chars = service.getCharacteristics()

        for char in chars:
          if char.supportsRead():
            value = char.read().decode("ascii")
            logging.info(f"read: {char.uuid} -> {value})")

      logging.info("--- scan complete ---")
      time.sleep(args.sleep)

  except KeyboardInterrupt:
    pass

  finally:
    peripheral.disconnect()

if __name__ == "__main__":
  main()
