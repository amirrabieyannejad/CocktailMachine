#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import Dict, Optional, List, Set
from enum import Enum

# server
import http.server

# utilities
import argparse
import logging
import random
import time
import uuid
from uuid import UUID

SERVER = "localhost"
PORT   = 8080
DELAY  = False

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

# TODO think about return codes / states
# TODO missing feature: abort recipe

# server model
##############

class Server():
  pumps:  List[Pump]
  queue:  List[Command]
  weight: float
  admins: Set[UUID]

  def __init__(self):
    self.pumps  = []
    self.queue  = []
    self.weight = 0
    self.admins = set()
    self.users  = Dict[UUID, str]

  def add_to_drink(self, liquid: str, volume: float):
    if volume <= 0:
      return

    for pump in self.pumps:
      if pump.liquid == liquid:
        used = pump.drain(volume)
        volume -= used
        self.weight += used

        if volume <= 0:
          return

    if volume > 0:
      raise Exception(f"couldn't find enough liquid: {volume} ml of {liquid}")

  def add_command(self, cmd: Command):
    self.queue.append(cmd)

  def process_queue(self):
    while self.queue:
      cmd = self.queue.pop(0)
      self.process(cmd)

  def process(self, cmd: Command):
    admin = cmd.user_id in self.admins
    # TODO
    pass

  def liquids(self) -> Dict[str, float]:
    ls: Dict[str, float] = {}
    for p in self.pumps:
      ls[p.liquid] += p.volume
    return ls

  def add_pump(self, pump: Pump):
    self.pumps.append(pump)

  def clean(self):
    self.weight = 0

  def add_admin(self, id: UUID):
    self.admins.add(id)

  def init_user(self, name: str) -> UUID:
    id = uuid.uuid1()
    self.users[id] = str
    return id

class Pump():
  liquid: str
  volume: float

  def __init__(self, liquid: str, volume: float):
    self.liquid = liquid
    self.volume = max(0, volume)

  def drain(self, volume: float) -> float:
    drained = max(0, self.volume - volume)
    self.volume = drained

    return drained

  def refill(self, volume: float):
    self.volume += volume

# supported commands
####################

class Command():
  user_name: str
  user_id:   UUID
  name:      str

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
