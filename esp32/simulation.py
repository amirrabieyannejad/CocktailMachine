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

# utilities
import argparse
import logging
import random
import re
import textwrap
import time
import uuid

NAME = "Cocktail Machine ESP32 Simulator"
UUID = uuid.UUID("0f7742d4-ea2d-43c1-9b98-bb4186be905d")

logging.basicConfig(level=logging.INFO, format='%(levelname)s %(asctime)s: %(message)s', datefmt='%y-%m-%d %H:%M:%S')

# server model
##############

class Server():
  queue:   List[Command]
  values:  Dict[str, Value]
  state:   ServerState
  users:   Dict[User, str]
  admins:  Set[User]
  next_id: int

  pumps:   List[Pump]
  recipes: List[Recipe]
  content: List[Tuple[Liquid, float]]

  def __init__(self):
    # start in init state
    self.state   = ServerState.ready
    self.queue   = []
    self.values  = {
      "state":    ValState(ServerState.init),
      "liquids":  ValLiquids({}),
      "recipes":  ValRecipes([]),
      "cocktail": ValCocktail([]),
    }

    # hardcore user 0 as admin
    self.admins  = {User(0)}
    self.users   = {User(0): "admin"}
    self.next_id = 1

    self.pumps   = []
    self.recipes = []
    self.content = []

  def update_values(self):
      self.values["state"].value    = self.state
      self.values["liquids"].value  = self.liquids()
      self.values["recipes"].value  = self.recipes
      self.values["cocktail"].value = self.content

  def process_queue(self):
    self.state = ServerState.processing

    while self.queue:
      cmd      = self.queue.pop(0)
      ret, val = self.process(cmd)
      self.update_values()
      self.return_value(ret, val)

    self.state = ServerState.ready
    self.update_values()

  def process(self, cmd: Command) -> Tuple[ReturnCode, Dict[str, Any]]:
    is_admin = isinstance(cmd, UserCommand) and cmd.user in self.admins

    if not cmd.allowed(self.admins):
      return (ReturnCode.not_allowed, {})

    if isinstance(cmd, CmdTest):
      # simple test command that does nothing
      return (ReturnCode.ok, {})

    if isinstance(cmd, CmdInitUser):
      # initialize user
      user = self.init_user(cmd.name)
      return (ReturnCode.ok, {"user": user.id})

    else:
      raise Exception(f"unsupported command: {cmd}")

  def return_value(self, code: ReturnCode, value: Dict[str, Any]):
    ret: str

    if len(value) == 0:
      ret = json.dumps(code.value)
    else:
      d = {"ret": code.value}
      d.update(value)
      ret = json.dumps(d)

    print(f"--> {ret}")

  def make_recipe(self, recipe: Recipe) -> bool:
    if not recipe in self.recipes:
      return False

    # assemble a list of pumping instructions to make the recipe
    queue: List[Tuple[Pump, float]] = []

    for liquid, need in recipe.liquids:
      have = 0.0

      for pump in self.pumps:
        if pump.liquid == liquid:
          diff = max(pump.volume, need - have)
          if diff > 0:
            queue.append((pump, diff))
            have += diff

        if have >= need:
          break

      if have < need:
        return False

    # apply the pumping instructions
    for pump, vol in queue:
      used = pump.drain(vol)
      self.content.append((liquid, used))

    return True

  def reset(self):
    self.content = []
    self.state = ServerState.ready

  def add_admin(self, id: User):
    self.admins.add(id)

  def init_user(self, name: str) -> User:
    user = User(self.next_id)
    self.next_id += 1
    self.users[user] = name
    return user

  def add_pump(self, pump: Pump):
    self.pumps.append(pump)

  def liquids(self) -> Dict[Liquid, float]:
    ls: Dict[Liquid, float] = {}

    for p in self.pumps:
      ls[p.liquid] = ls.get(p.liquid, 0) + p.volume

    return ls

  def add_recipe(self, recipe: Recipe):
    self.recipes.append(recipe)

  def show_status(self):
    for name, val in self.values.items():
      print(f"{name}: {val.value}")

class ServerState(Enum):
  ready      = "ready"
  processing = "processing"
  pumping    = "pumping"
  cleaning   = "cleaning"
  refill     = "refill"
  stuck      = "stuck"
  init       = "starting up"

class ReturnCode(Enum):
  ok          = "ok"
  not_allowed = "not allowed"
  unknown     = "unknown command"
  parse_error = "parse error"

@dataclass(frozen=True)
class User():
  id: int

@dataclass
class Pump():
  liquid: Liquid
  volume: float

  def drain(self, volume: float) -> float:
    diff = max(0, min(self.volume, volume))
    self.volume -= diff
    return diff

  def refill(self, volume: float):
    self.volume += volume

@dataclass(frozen=True)
class Liquid():
  name: str

@dataclass(frozen=True)
class Recipe():
  name:    str
  liquids: List[Tuple[Liquid, float]]

#############################################################################

# status values
###############

@dataclass
class Value(ABC):
  name:    ClassVar[str]
  example: ClassVar[Dict[str, Any]] = {}
  value:   Any

  @classmethod
  def uuid_service(cls) -> uuid.UUID:
    return gen_uuid(f"status {cls.name}")

  @classmethod
  def uuid_characteristic(cls) -> uuid.UUID:
    return gen_uuid(f"status {cls.name} value")

@dataclass
class ValState(Value):
  value: ServerState
  name = "operation"

@dataclass
class ValRecipes(Value):
  value: List[Recipe]
  name = "recipes"

@dataclass
class ValCocktail(Value):
  value: List[Tuple[Liquid, float]]
  name = "cocktail"

@dataclass
class ValLiquids(Value):
  value: Dict[Liquid, float]
  name = "liquids"

# commands
##########

@dataclass(frozen=True)
class Command(ABC):
  # metadata
  desc:    ClassVar[str]  = "[generic superclass of commands]"
  example: ClassVar[Dict[str, Any]] = {}
  retval:  ClassVar[Dict[str, Any]] = {}

  def allowed(self, admins: Set[User]) -> bool:
    return True

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
  def dict(self) -> Dict[str, Any]:
    d: Dict[str, Any] = {}
    d["cmd"] = self.cmdname
    d.update(dataclasses.asdict(self))

    if isinstance(self, UserCommand):
      d["user"] = self.user.id

    return d

  def json(self):
    return json.dumps(self.dict())

  # load commands from json
  @classmethod
  def from_json(cls, json_cmd: str) -> Command:
    parsed = json.loads(json_cmd)
    name   = parsed.pop("cmd")
    parts  = [part[0].upper() + part[1:] for part in re.sub(r"([_])", " ", name).split()]
    full   = "Cmd" + "".join(parts)

    try:
      cmd = globals()[full]
    except:
      raise Exception(f"unsupported command: {name}")

    if isinstance(cmd, UserCommand):
      try:
        parsed["user"] = User(parsed["user"])
      except KeyError:
        raise Exception("missing argument: user")

    return cmd(**parsed) # type: ignore # this is always a Command

  @classmethod
  def description(cls) -> str:
    out = []
    out.append(f"### {cls.basename()}: {cls.desc}")

    fields = dataclasses.fields(cls)
    if fields:
      for field in fields:
        out.append(f"- {field.name}: {field.type}")
      out.append("")

    example_values = cls.example
    if [f for f in dataclasses.fields(cls) if f.name == "user"]:
      example_values["user"] = User(random.randint(1, 10000))

    example = cls(**example_values)

    out.append("JSON example:")
    out.append("")
    out.append(f"    {example.json()}")
    if cls.retval:
      out.append("")
      out.append(f"    --> {json.dumps(cls.retval)}")

    return "\n".join(out)

@dataclass(frozen=True)
class UserCommand(Command):
  # mandatory field for every authenticated command
  user:  User
  admin: ClassVar[bool] = False

  def allowed(self, admins: Set[User]) -> bool:
    if not self.__class__.admin:
      return True
    return self.user in admins

# supported commands
####################

@dataclass(frozen=True)
class CmdTest(Command):
  desc = "dummy command that does nothing"

@dataclass(frozen=True)
class CmdReset(UserCommand):
  desc = "reset the machine so it's ready to make another drink"

@dataclass(frozen=True)
class CmdMakeRecipe(UserCommand):
  recipe: str

  desc    = "make recipe by name"
  example = {"recipe": "radler"}

@dataclass(frozen=True)
class CmdAddLiquid(UserCommand):
  liquid: str
  volume: float

  admin   = True
  desc    = "add given liquid to glass"
  example = {"liquid": "water", "volume": 30}

@dataclass(frozen=True)
class CmdDefineRecipe(UserCommand):
  name: str
  liquids: List[Tuple[str, float]]

  admin   = True
  desc    = "define new recipe"
  example = {"name": "radler", "liquids": [("beer", 250), ("lemonade", 250)]}

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

  desc    = "introduce yourself as a new user and receive your user id"
  example = {"name": "test-user"}
  retval  = {"user": 100}

# ways to receive commands
##################

def start_bluetooth():
  logging.info("[TODO] starting bluetooth")

def read_command() -> Optional[Command]:
  read = input("> ")
  try:
    cmd = Command.from_json(read)
    return cmd

  except Exception as e:
    logging.exception(e)
    return None


# misc
######

def gen_uuid(name: str) -> uuid.UUID:
  return uuid.uuid3(UUID, name)

def print_commands(cmds: List[Any], title: str):
  print(f"## {title}")
  print()
  for i, cls in enumerate(cmds):
    print(cls.description())
    if i < len(cmds) - 1:
      print()
  print()

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
    print(f"# Supported commands")
    print()

    admin = [cmd for cmd in UserCommand.__subclasses__() if     cmd.admin]
    user  = [cmd for cmd in UserCommand.__subclasses__() if not cmd.admin]
    main  = [cmd for cmd in Command.__subclasses__() if not cmd is UserCommand]

    print_commands(main,  "General Commands")
    print_commands(user,  "User Commands")
    print_commands(admin, "Admin Commands")
    exit(0)

  # process commands
  ##################

  server = Server()
  server.reset()
  server.update_values()

  if args.bluetooth:
    start_bluetooth()

  server.show_status()
  logging.info("reading commands from STDIN")

  while True:
    try:
      cmd = read_command()
      if cmd:
        server.queue.append(cmd)

      server.process_queue()
      server.show_status()

    except (KeyboardInterrupt, EOFError):
      exit(0)

if __name__ == "__main__":
  main()
