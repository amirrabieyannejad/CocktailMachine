# Supported commands

Messages are encoded as JSON maps and always follow the same format. 

For unencrypted messages, the format is:

    {"cmd": "the command name", "user": "the user id", "argument": "value", ...}

The "cmd" field is mandatory and contains the command name. Many messages require a "user" field, but some general commands don't. Potential arguments to the command are added as additional fields.

Encrypted messages wrap the command like this:

    {"msg": "encrypted JSON command", "user": "the user id"}

The key associated with the user id is used to decrypt the "msg" field, which then contains a normal command of the form `{"cmd": ...}`. 

## General Commands

### test: dummy command that does nothing
json example:

    {"cmd": "test"}

### init_user: introduce yourself as a new user and receive your user id
- name: str

json example:

    {"cmd": "init_user", "name": "test-user"}

    --> {"user": 100}

## User Commands

### reset: reset the machine so it's ready to make another drink
- user: User

json example:

    {"cmd": "reset", "user": 9650}

### make_recipe: make recipe by name
- user: User
- recipe: str

json example:

    {"cmd": "make_recipe", "user": 8858, "recipe": "radler"}

## Admin Commands

### add_liquid: add given liquid to glass
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_liquid", "user": 2737, "liquid": "water", "volume": 30}

### define_recipe: define new recipe
- user: User
- name: str
- liquids: List[Tuple[str, float]]

json example:

    {"cmd": "define_recipe", "user": 4310, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}

### add_pump: add given pump to device
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_pump", "user": 4315, "liquid": "water", "volume": 1000}

### calibrate_pumps: calibrate all pumps
- user: User

json example:

    {"cmd": "calibrate_pumps", "user": 8185}

### clean: clean machine
- user: User

json example:

    {"cmd": "clean", "user": 8162}

