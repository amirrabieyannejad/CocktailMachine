#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import ClassVar, Dict, Optional, List, Set, Any, Tuple

# datatypes
from abc import ABC, abstractmethod
import dataclasses
from dataclasses import dataclass
import enum
from enum import Enum, Flag
import json
import uuid
from uuid import UUID

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

# server attributes
###################

class Property(Flag):
  read   = enum.auto()
  write  = enum.auto()
  notify = enum.auto()

@dataclass
class Service():
  label: str
  characteristics: AsCharacteristics

@dataclass
class Characteristic():
  desc:  str
  value: str
  mode:  Property = Property.read | Property.notify

class AsCharacteristics(ABC):
  @abstractmethod
  def characteristics(self) -> List[Characteristic]:
    ...

@dataclass
class EnumCharacteristic(AsCharacteristics):
  name: str
  enum: Enum

  def characteristics(self) -> List[Characteristic]:
    return [Characteristic(self.name, self.enum.value)]

# server model
##############

class Server():
  pumps:  List[Pump]
  queue:  List[Command]
  weight: float
  admins: Set[UUID]
  users:  Dict[UUID, str]
  state:  ServerState

  def __init__(self):
    self.pumps  = []
    self.queue  = []
    self.weight = 0
    self.admins = set()
    self.users  = {}
    self.state  = ServerState.init

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
    admin = isinstance(cmd, UserCommand) and cmd.user in self.admins

    if isinstance(cmd, CmdTest):
      # simple test command that does nothing
      pass

    elif isinstance(cmd, CmdAddLiquid):
      self.add_to_drink(cmd.liquid, cmd.volume)

    else:
      raise Exception(f"unsupported command: {cmd}")

  def liquids(self) -> List[Liquid]:
    ls: Dict[str, Liquid] = {}
    for p in self.pumps:
      l = ls.get(p.liquid, Liquid(p.liquid, 0, 0))
      l.volume += p.volume
      l.pumps  += 1
      ls[p.liquid] = l

    return list(ls.values())

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

  def services(self) -> List[Service]:
    services = []

    for liquid in self.liquids():
      name = f"liquid-{liquid.name}"
      services.append(Service(name, liquid))

    services.append(Service("service-state", EnumCharacteristic("state", self.state)))

    return services

  def show_status(self):
    print("Services:")
    for s in self.services():
      print(f"  {s.label}:")
      # TODO
    #   for c in s.characteristics:
    #     print(f"  - {c.mode}:")

class ServerState(Enum):
  init    = "initializing"
  ready   = "ready"
  pumping = "pumping"
  refill  = "refill"
  stuck   = "stuck"

@dataclass
class Pump(AsCharacteristics):
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

  def characteristics(self) -> List[Characteristic]:
    return [Characteristic("liquid", self.liquid),
            Characteristic("volume", "%.1f" % self.volume)]

@dataclass
class Liquid(AsCharacteristics):
  name:   str
  volume: float
  pumps:  int

  def characteristics(self) -> List[Characteristic]:
    return [Characteristic("name",   self.name),
            Characteristic("volume", "%.1f" % self.volume),
            Characteristic("pumps",  "%d" % self.pumps)]

# commands
##########

# encode uuids as strings
class UUIDJSONEncoder(json.JSONEncoder):
  def default(self, o):
    if isinstance(o, UUID):
      return str(o)
    return super().default(o)

@dataclass(frozen=True)
class Command(ABC):
  # metadata
  desc:    ClassVar[str]  = "[generic superclass of commands]"
  admin:   ClassVar[bool] = False
  no_uuid: ClassVar[bool] = False
  example: ClassVar[Dict[str, Any]] = {}

  # generate the command name from the class name
  @classmethod
  def basename(cls) -> str:
    name  = cls.__name__
    base  = re.sub(r"^Cmd", "", name)
    parts = [part.lower() for part in re.sub(r"([A-Z])", r" \1", base).split()]
    return "_".join(parts)

  @property
  def cmdname(self) -> str:
    return self.__class__.basename()

  # encode commands as dicts and as json
  @property
  def dict(self):
    d = {"cmd": self.cmdname}
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
    full   = "Cmd" + "".join(parts)

    try:
      cmd = globals()[full]
    except:
      raise Exception(f"unsupported command: {cmd}")

    if isinstance(cmd, UserCommand):
      try:
        parsed["user"] = UUID(parsed["user"])
      except KeyError:
        raise Exception("missing argument: user")

    return cmd(**parsed) # type: ignore # this is always a Command

  @classmethod
  def description(cls, indent: Optional[int] = None):
    out = []
    if cls.admin:
      out.append(f"{cls.basename()} [ADMIN]: {cls.desc}")
    else:
      out.append(f"{cls.basename()}: {cls.desc}")

    fields = dataclasses.fields(cls)
    if fields:
      for field in fields:
        out.append(f"  - {field.name}: {field.type}")
      out.append("")

    out.append("  json example:")

    example_values = cls.example
    if [f for f in dataclasses.fields(cls) if f.name == "user"]:
      example_values["user"] = uuid.uuid1() # add random mandatory uuid

    example = cls(**example_values)

    if indent:
      out.append(textwrap.indent(example.json(indent=indent), " "*indent))
    else:
      out.append(f"    {example.json()}")

    return "\n".join(out)

@dataclass(frozen=True)
class UserCommand(Command):
  # mandatory field for every authenticated command
  user: UUID

# supported commands
####################

@dataclass(frozen=True)
class CmdTest(Command):
  desc = "dummy command that does nothing"

@dataclass(frozen=True)
class CmdAddLiquid(UserCommand):
  liquid: str
  volume: float

  admin   = True
  desc    = "add given liquid to glass"
  example = {"liquid": "water", "volume": 30}

@dataclass(frozen=True)
class CmdMakeRecipe(UserCommand):
  recipe: str

  desc    = "make recipe"
  example = {"recipe": "radler"}

@dataclass(frozen=True)
class CmdDefineRecipe(UserCommand):
  recipe: str
  liquids: List[Tuple[str, float]]

  admin   = True
  desc    = "define new recipe"
  example = {"recipe": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}

@dataclass(frozen=True)
class CmdAddPump(UserCommand):
  liquid: str
  volume: float

  admin   = True
  desc    = "add given pump to device"
  example = {"liquid": "water", "volume": 1000}

@dataclass(frozen=True)
class CmdCalibratePumps(UserCommand):
  desc  = "calibrate all pumps"
  admin = True

@dataclass(frozen=True)
class CmdClean(UserCommand):
  desc  = "clean machine"
  admin = True

@dataclass(frozen=True)
class CmdInitUser(Command):
  name: str

  desc    = "introduce yourself as a new user and receive your uuid"
  example = {"name": "test-user"}

# ways to receive commands
##################

def start_bluetooth():
  logging.info("[TODO] starting bluetooth")

def read_command() -> Optional[Command]:
  read = input("> ")
  try:
    cmd  = Command.from_json(read)
  except Exception as e:
    logging.error(e)
    return None

  return cmd

# misc
######

def main():
  # parse arguments
  #################

  parser = argparse.ArgumentParser(
    prog = NAME,
    description = "Simulates the functionality of the ESP32 as far as other apps are concerned.")

  parser.add_argument("-b", "--bluetooth", action="store_true", help=f"turn on bluetooth ({NAME})")
  parser.add_argument("-l", "--commands",  action="store_true", help="list all supported commands and an example JSON encoding")

  args = parser.parse_args()

  if args.commands:
    cmds = Command.__subclasses__() + UserCommand.__subclasses__()
    cmds.remove(UserCommand)

    print(f"Supported commands ({len(cmds)}):")
    print()

    for i, cls in enumerate(cmds):
      print(cls.description())
      if i < len(cmds) - 1:
        print()

    exit(0)

  # process commands
  ##################

  server = Server()

  if args.bluetooth:
    start_bluetooth()

  server.show_status()
  logging.info("reading commands from STDIN")

  while True:
    try:
      cmd = read_command()
      if cmd:
        server.process(cmd)
        server.show_status()

    except (KeyboardInterrupt, EOFError):
      exit(0)

if __name__ == "__main__":
  main()
