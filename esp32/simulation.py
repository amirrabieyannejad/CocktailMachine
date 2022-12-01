#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import ClassVar, Dict, Optional, List, Set, Any

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
import textwrap
import time

NAME   = "Cocktail Machine ESP32 Simulator"
SERVER = "localhost"
PORT   = 8080

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

# commands
##########

# encode uuids as strings
class UUIDJSONEncoder(json.JSONEncoder):
  def default(self, o):
    if isinstance(o, UUID):
      return str(o)
    return super().default(o)

@dataclass(frozen=True)
class Command:
  # mandatory field for every command
  user: UUID

  # metadata
  desc:    ClassVar[str] = "[generic superclass of commands]"
  example: ClassVar[Dict[str, Any]] = {}

  # generate the command name from the class name
  @classmethod
  def basename(cls) -> str:
    name  = cls.__name__
    base  = re.sub(r"^Cmd", "", name)
    parts = [part.lower() for part in re.sub(r"([A-Z])", r" \1", base).split()]
    return "_".join(parts)

  @property
  def name(self) -> str:
    return self.__class__.basename()

  # encode commands as dicts and as json
  @property
  def dict(self):
    d = {"cmd": self.name}
    d.update(dataclasses.asdict(self))
    return d

  def json(self, indent=None):
    return json.dumps(self.dict, cls=UUIDJSONEncoder, indent=indent)

  # load commands from json
  @classmethod
  def from_json(cls, json_cmd: str) -> Command:
    parsed = json.loads(json_cmd)
    name   = parsed.pop("cmd")
    parts  = [part[0].upper() + part[1:] for part in re.sub(r"([_])", r" \1", name).split()]
    cmd    = "Cmd" + "".join(parts)

    try:
      cls = globals()[cmd]
    except:
      raise Exception(f"unsupported command: {cmd}")

    parsed["user"] = UUID(parsed["user"])

    return cls(**parsed)

  @classmethod
  def description(cls, indent: Optional[int] = None):
    out = []
    out.append(f"{cls.basename()}: {cls.desc}")

    for field in dataclasses.fields(cls):
      out.append(f"  - {field.name}: {field.type}")
    out.append("")

    out.append("  json example:")
    example_values = cls.example
    example_values["user"] = uuid.uuid1() # add random mandatory uuid
    example = cls(**example_values)

    if indent:
      out.append(textwrap.indent(example.json(indent=indent), " "*indent))
    else:
      out.append(f"    {example.json()}")

    return "\n".join(out)

# supported commands
####################

@dataclass(frozen=True)
class CmdTest(Command):
  desc = "dummy command that does nothing"

@dataclass(frozen=True)
class CmdAddLiquid(Command):
  desc = "add given liquid to glass"
  liquid: str
  volume: float

  example = {"liquid": "water", "volume": 30}

# ways to receive commands
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
    prog = NAME,
    description = "Simulates the functionality of the ESP32 as far as other apps are concerned.")

  parser.add_argument("-b", "--bluetooth", action="store_true", help=f"turn on bluetooth ({NAME})")
  parser.add_argument("-w", "--web",       action="store_true", help=f"turn on local web server on {SERVER}:{PORT}")
  parser.add_argument("-i", "--stdin",     action="store_true", help=f"read commands on STDIN")
  parser.add_argument("-l", "--commands",  action="store_true", help="list all supported commands and an example JSON encoding")

  args = parser.parse_args()

  if args.commands:
    subs = Command.__subclasses__()
    print(f"Supported commands ({len(subs)}):")
    print()
    for i, cls in enumerate(subs):
      print(cls.description())
      if i < len(subs) - 1:
        print()

  # process commands
  ##################

  if args.bluetooth:
    start_bluetooth()

  if args.web:
    start_web_server(SERVER, PORT)

  if args.stdin:
    read_commands()

if __name__ == "__main__":
  main()
