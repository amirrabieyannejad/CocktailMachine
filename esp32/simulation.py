#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import Dict, Optional, List, Set, Any

# datatypes
import dataclasses
from dataclasses import dataclass
from enum import Enum
import json
import uuid
from uuid import UUID

# server
import http.server

# utilities
import argparse
import logging
import random
import re
import time

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
  users:  Dict[UUID, str]

  def __init__(self):
    self.pumps  = []
    self.queue  = []
    self.weight = 0
    self.admins = set()
    self.users  = {}

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
    admin = cmd.user in self.admins

    if isinstance(cmd, CmdTest):
      # simple test command that does nothing
      pass

    elif isinstance(cmd, CmdAddLiquid):
      self.add_to_drink(cmd.liquid, cmd.volume)

    else:
      raise Exception(f"unsupported command: {cmd}")

  def liquids(self) -> Dict[str, float]:
    ls: Dict[str, float] = {}
    for p in self.pumps:
      ls[p.liquid] = ls.get(p.liquid, 0) + p.volume
    return ls

  def add_pump(self, pump: Pump):
    self.pumps.append(pump)

  def clean(self):
    self.weight = 0

  def add_admin(self, id: UUID):
    self.admins.add(id)

  def init_user(self, name: str) -> UUID:
    id = uuid.uuid1()
    self.users[id] = name
    return id

  def ready(self) -> bool:
    return len(self.queue) == 0

class Pump():
  liquid: str
  volume: float

  def __init__(self, liquid: str, volume: float):
    self.liquid = liquid
    self.volume = max(0, volume)

  def drain(self, volume: float) -> float:
    diff = max(0, min(self.volume, volume))
    self.volume -= diff
    return diff

  def refill(self, volume: float):
    self.volume += volume

# supported commands
####################

# encode uuids as strings
class UUIDJSONEncoder(json.JSONEncoder):
  def default(self, o):
    if isinstance(o, UUID):
      return str(o)
    return super().default(o)

@dataclass(frozen=True)
class Command:
  user: UUID

  # generate the command name from the class name
  def name(self) -> str:
    cls   = self.__class__.__name__
    name  = re.sub(r"^Cmd", "", cls)
    parts = [part.lower() for part in re.sub(r"([A-Z])", r" \1", name).split()]
    return "_".join(parts)

  # encode commands as dicts and as json
  @property
  def __dict__(self):
    d = {"cmd": self.name()}
    d.update(dataclasses.asdict(self))
    return d

  @property
  def json(self):
    return json.dumps(self.__dict__, cls=UUIDJSONEncoder)

  # load commands from json
  @classmethod
  def from_json(cls, json_cmd: str) -> Command:
    d     = json.loads(json_cmd)
    name  = d.pop("cmd")
    parts = [part[0].upper() + part[1:] for part in re.sub(r"([_])", r" \1", name).split()]
    cmd   = "Cmd" + "".join(parts)

    try:
      cls = globals()[cmd]
    except:
      raise Exception(f"unsupported command: {cmd}")

    d["user"] = UUID(d["user"])

    return cls(**d)

@dataclass(frozen=True)
class CmdTest(Command):
  pass

@dataclass(frozen=True)
class CmdAddLiquid(Command):
  liquid: str
  volume: float

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
