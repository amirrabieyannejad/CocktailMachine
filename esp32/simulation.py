#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import NamedTuple, Optional, List, Set

# server
import http.server

# utilities
import argparse
import logging
import re
import time

SERVER = "localhost"
PORT   = 8080
DELAY  = False

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

# supported commands
####################

class Command():
  def name(self) -> str:
    cls   = self.__class__.__name__
    name  = re.sub(r"^Cmd", "", cls)
    parts = [part.lower() for part in re.sub(r"([A-Z])", r" \1", name).split()]
    return "_".join(parts)

class CmdPumpTime(Command):
  pump: int
  time: float

  def __init__(self, pump: int, time: float):
    self.pump = pump
    self.time = time

class CmdAddLiquid(Command):
  liquid: str
  ml: float

  def __init__(self, liquid: str, ml: float):
    self.liquid = liquid
    self.ml = ml

class CmdStatus(Command):
  pass

class CmdShowLiquids(Command):
  pass

class CmdAdminStatus(Command):
  pass

def parse_command(command: str) -> Optional[Command]:
  return None

# process commands
##################

def start_web_server(server: str, port: int):
  logging.info(f"starting web server on {server}:{port}")
  # TODO
  pass

def start_bluetooth():
  logging.info("starting bluetooth")
  # TODO
  pass

def read_commands():
  logging.info("starting to read commands on stdin")
  # TODO
  pass

# misc
######

def main():
  # parse arguments
  #################

  parser = argparse.ArgumentParser(
    prog = "Cocktail Machine ESP32 Simulator",
    description = "Simulates the functionality of the ESP32 as far as other apps are concerned.")

  parser.add_argument("-s", "--sleep",     action="store_true", help="add more realistic sleep delays after receiving commands?")
  parser.add_argument("-b", "--bluetooth", action="store_true", help="turn on bluetooth")
  parser.add_argument("-w", "--web",       action="store_true", help=f"turn on local web server on {SERVER}:{PORT}")

  args = parser.parse_args()

  if args.sleep:
    DELAY = True

  # process commands
  ##################

  if args.bluetooth:
    start_bluetooth()

  if args.web:
    start_web_server(SERVER, PORT)

  read_commands()

if __name__ == "__main__":
  main()
