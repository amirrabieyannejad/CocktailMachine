#!/usr/bin/env python3

# type annotations
from __future__ import annotations
from typing import ClassVar, Dict, Optional, List, Set, Any, Tuple, Union

# datatypes
from abc import ABC, abstractmethod
import dataclasses
from dataclasses import dataclass
import enum
from enum import Enum, Flag
import json

# bluetooth
from bless import (  # type: ignore
  BlessServer,
  BlessGATTCharacteristic,
  GATTCharacteristicProperties,
  GATTAttributePermissions
)
import asyncio

# utilities
import argparse
import logging
import platform
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
  # status
  values: Dict[str, Value]

  # users
  users:   Dict[User, str]
  admins:  Set[User]
  next_id: int

  # machine state
  state:   ServerState
  pumps:   List[Pump]
  recipes: List[Recipe]
  content: List[Tuple[Liquid, float]]

  # bluetooth
  ble:              Optional[BlessServer] = None
  trigger:          asyncio.Event = asyncio.Event()
  command_services: Dict[str, CommandService]
  characteristics:  Dict[BlessGATTCharacteristic, Union[Value, CommandService]]

  def __init__(self):
    # start in init state
    self.state = ServerState.init

    # hardcode user 0 as admin
    self.admins  = {User(0)}
    self.users   = {User(0): "admin"}
    self.next_id = 1

    # clear machine state
    self.pumps   = []
    self.recipes = []
    self.content = []

    self.values  = {
      "state":    ValState(self.state),
      "liquids":  ValLiquids(self.liquids()),
      "recipes":  ValRecipes(self.recipes),
      "cocktail": ValCocktail(self.content),
    }
    self.command_services = {
      "commands": CommandService("commands", "message", "response"),
    }

  def process(self, cmd: Command):
    logging.info(f"processing command: {cmd}")

    self.state = ServerState.processing

    ret, val = self.apply_command(cmd)
    self.update_values()
    self.return_value(ret, val)

    self.state = ServerState.ready
    self.update_values()

  def apply_command(self, cmd: Command) -> Tuple[ReturnCode, Dict[str, Any]]:
    is_admin = isinstance(cmd, UserCommand) and cmd.user in self.admins

    if not cmd.allowed(self.admins):
      return (ReturnCode.not_allowed, {})

    if isinstance(cmd, CmdTest):
      # simple test command that does nothing
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdInitUser):
      # initialize user
      user = self.init_user(cmd.name)
      return (ReturnCode.ok, {"user": user.id})

    elif isinstance(cmd, CmdReset):
      self.reset()
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdMakeRecipe):
      matches = [r for r in self.recipes if r.name == cmd.recipe]

      if len(matches) > 0:
        self.make_recipe(matches[0])
        return (ReturnCode.ok, {})
      else:
        return (ReturnCode.unknown_recipe, {})

    elif isinstance(cmd, CmdAddLiquid):
      self.make_recipe(Recipe("", [(Liquid(cmd.liquid), cmd.volume)]))
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdDefineRecipe):
      self.add_recipe(Recipe(cmd.name, [(Liquid(l), v) for (l, v) in cmd.liquids]))
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdAddPump):
      self.add_pump(Pump(Liquid(cmd.liquid), cmd.volume))
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdCalibratePumps):
      self.calibrate()
      return (ReturnCode.ok, {})

    elif isinstance(cmd, CmdClean):
      self.clean()
      return (ReturnCode.ok, {})

    else:
      return (ReturnCode.unknown, {})

  def return_value(self, code: ReturnCode, value: Dict[str, Any]):
    ret: str

    if len(value) == 0:
      ret = json.dumps(code.value)
    else:
      d = {"ret": code.value}
      d.update(value)
      ret = json.dumps(d)

    if self.ble:
      self.send_response(ret.encode("utf8"), self.command_services["commands"])

    logging.info(f"response: {ret}")

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

  def calibrate(self):
    print("calibrating... bzzzzz... done.")

  def clean(self):
    print("cleaning... whirrrr... done.")

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

  def update_values(self):
      self.values["state"].value    = self.state.value
      self.values["liquids"].value  = self.liquids()
      self.values["recipes"].value  = self.recipes
      self.values["cocktail"].value = self.content

      if self.ble:
        for name, value in self.values.items():
          uuid_service = str(value.uuid_service())
          uuid_char    = str(value.uuid_characteristic())
          new_value    = value.json()

          char = self.ble.get_characteristic(uuid_char)
          if char.value != new_value:
            char.value = new_value
            self.ble.update_value(uuid_service, uuid_char)

  def read_char(self, characteristic: BlessGATTCharacteristic, **kwargs) -> Any:
    try:
      service = self.characteristics[characteristic]

      if isinstance(service, Value):
        logging.info(f"read value {service.name}: {characteristic.value}")
        return characteristic.value

      elif isinstance(service, CommandService):
        logging.info(f"read response: {characteristic.value}")
        return characteristic.value

      else:
        raise Exception(f"unsupported type of characteristic: {characteristic}")

    except KeyError:
      # additional characteristics we don't really care about
      return characteristic.value

  def write_char(self, characteristic: BlessGATTCharacteristic, value: bytes, **kwargs):
    try:
      service = self.characteristics[characteristic]

      if isinstance(service, Value):
        dec = value.decode("utf8")
        logging.error(f"attempted to write status service: {service.name}: {dec}")
        characteristic.value = value

      elif isinstance(service, CommandService):
        dec = value.decode("utf8")
        characteristic.value = value
        logging.info(f"wrote message: {dec}")

        try:
          ret, cmd = Command.from_json(dec)
          if ret == ReturnCode.ok:
            if cmd:
              self.process(cmd)
          else:
            self.return_value(ret, {})
        except Exception as e:
          logging.exception(e)

      else:
        raise Exception(f"unsupported type of characteristic: {characteristic}")

    except KeyError:
      # additional characteristics we don't really care about
      dec = value.decode("utf8")
      characteristic.value = value
      logging.info(f"wrote: {dec}")

  async def start_bluetooth(self, loop):
    # start server
    self.ble = BlessServer(name=NAME, loop=loop)
    self.ble.read_request_func  = lambda char, **kwargs:        self.read_char(char, **kwargs)
    self.ble.write_request_func = lambda char, value, **kwargs: self.write_char(char, value, **kwargs)

    # build gatt
    gatt: Dict = {
      str(UUID): {
        str(gen_uuid("base")): {
          "Properties":  GATTCharacteristicProperties.read,
          "Permissions": GATTAttributePermissions.readable,
          "Value": None,
        }
      }
    }

    # status services
    for name, value in self.values.items():
      logging.info(f"adding status service: {name}")
      uuid_service = str(value.uuid_service())
      uuid_char    = str(value.uuid_characteristic())

      gatt[uuid_service] = {
        uuid_char: {
          "Properties": ( GATTCharacteristicProperties.read
                        | GATTCharacteristicProperties.notify),
          "Permissions": GATTAttributePermissions.readable,
          "Value": value.json(),
        }
      }

    # communication service
    for name, com in self.command_services.items():
      logging.info(f"adding communication service: {name}")
      uuid_service  = str(com.uuid_service())
      uuid_message  = str(com.uuid_message())
      uuid_response = str(com.uuid_response())

      gatt[uuid_service] = {
        uuid_message: {
          "Properties": GATTCharacteristicProperties.write,
          "Permissions": GATTAttributePermissions.writeable,
          "Value": None,
        },
        uuid_response: {
          "Properties": ( GATTCharacteristicProperties.read
                        | GATTCharacteristicProperties.notify),
          "Permissions": GATTAttributePermissions.readable,
          "Value": None,
        }
      }

    await self.ble.add_gatt(gatt)

    # assemble a map from characteristic to its internal class
    self.characteristics: Dict = {}

    # status services
    for value in self.values.values():
      uuid_char = str(value.uuid_characteristic())
      char = self.ble.get_characteristic(uuid_char)
      self.characteristics[char] = value

    for com in self.command_services.values():
      uuid_message  = str(com.uuid_message())
      uuid_response = str(com.uuid_response())

      char_message  = self.ble.get_characteristic(uuid_message)
      char_response = self.ble.get_characteristic(uuid_response)

      self.characteristics[char_message]  = com
      self.characteristics[char_response] = com

    # start server
    await self.ble.start()
    logging.info("bluetooth ready")

    # keep the server running unless there's an explicit exit
    await self.trigger.wait()

  async def stop_bluetooth(self, loop):
    if self.ble:
      await self.ble.stop()

  def send_response(self, response: bytes, com: CommandService):
    if self.ble:
      uuid_service  = str(com.uuid_service())
      uuid_response = str(com.uuid_response())

      char = self.ble.get_characteristic(uuid_response)
      char.value = response
      self.ble.update_value(uuid_service, uuid_response)

class ServerState(Enum):
  ready      = "ready"
  processing = "processing"
  pumping    = "pumping"
  cleaning   = "cleaning"
  refill     = "refill"
  stuck      = "stuck"
  init       = "starting up"

class ReturnCode(Enum):
  ok             = "ok"
  not_allowed    = "not allowed"
  unknown        = "unknown command"
  unknown_recipe = "unknown recipe"
  parse_error    = "parse error"
  missing_user   = "user missing"

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

  def uuid_service(self) -> uuid.UUID:
    return gen_uuid(f"status {self.__class__.name}")

  def uuid_characteristic(self) -> uuid.UUID:
    return gen_uuid(f"status {self.__class__.name} value")

  def json_value(self) -> Any:
    return self.value

  def json(self) -> bytes:
    return json.dumps(self.json_value(), ensure_ascii=False).encode('utf8')

@dataclass
class ValState(Value):
  value: ServerState
  name = "operation"

@dataclass
class ValRecipes(Value):
  value: List[Recipe]
  name = "recipes"

  def json_value(self) -> List[Dict[str, List[Tuple[str, float]]]]:
    return [dataclasses.asdict(r) for r in self.value]

@dataclass
class ValCocktail(Value):
  value: List[Tuple[Liquid, float]]
  name = "cocktail"

  def json_value(self) -> List[Tuple[str, float]]:
    return [(l.name, v) for (l, v) in self.value]

@dataclass
class ValLiquids(Value):
  value: Dict[Liquid, float]
  name = "liquids"

  def json_value(self) -> Dict[str, float]:
    return dict([(l.name, v) for (l, v) in self.value.items()])

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
  def from_json(cls, json_cmd: str) -> Tuple[ReturnCode, Optional[Command]]:
    parsed = json.loads(json_cmd)
    name   = parsed.pop("cmd")
    parts  = [part[0].upper() + part[1:] for part in re.sub(r"([_])", " ", name).split()]
    full   = "Cmd" + "".join(parts)

    try:
      cmd_class = globals()[full]
    except:
      return (ReturnCode.unknown, None)

    if isinstance(cmd_class, UserCommand):
      try:
        parsed["user"] = User(parsed["user"])
      except KeyError:
        return (ReturnCode.missing_user, None)

    try:
      cmd = cmd_class(**parsed) # type: ignore # this is always a Command
    except:
      return (ReturnCode.parse_error, None)

    return (ReturnCode.ok, cmd)

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
    user = self.user
    if isinstance(self.user, int):
      user = User(self.user)
    return user in admins

@dataclass
class CommandService:
  name:     str
  message:  str
  response: str

  def uuid_service(self) -> uuid.UUID:
    return gen_uuid(f"{self.name}")

  def uuid_message(self) -> uuid.UUID:
    return gen_uuid(f"{self.name} {self.message}")

  def uuid_response(self) -> uuid.UUID:
    return gen_uuid(f"{self.name} {self.response}")

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

# misc
######

def read_command() -> Optional[Command]:
  read = input("> ")
  try:
    ret, cmd = Command.from_json(read)
    if ret != ReturnCode.ok:
      logging.error(f"error: {ret}")
    return cmd

  except Exception as e:
    logging.exception(e)
    return None

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
    logging.info("starting bluetooth")
    if platform.system() == "Windows":
      loop = asyncio.get_event_loop()
    else:
      loop = asyncio.new_event_loop()

    try:
      loop.run_until_complete(server.start_bluetooth(loop))
    except KeyboardInterrupt:
      loop.run_until_complete(server.stop_bluetooth(loop))
      exit(0)

  else:
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
