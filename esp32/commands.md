# Supported commands

## General Commands

### test: dummy command that does nothing
json example:

    {"cmd": "test"}

### reset: reset the machine so it's ready to make another drink
json example:

    {"cmd": "reset"}

### init_user: introduce yourself as a new user and receive your user id
- name: str

json example:

    {"cmd": "init_user", "name": "test-user"}

    --> {"user": 100}

## User Commands

### make_recipe: make recipe by name
- user: User
- recipe: str

json example:

    {"cmd": "make_recipe", "user": 8839, "recipe": "radler"}

## Admin Commands

### add_liquid: add given liquid to glass
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_liquid", "user": 6745, "liquid": "water", "volume": 30}

### define_recipe: define new recipe
- user: User
- name: str
- liquids: List[Tuple[str, float]]

json example:

    {"cmd": "define_recipe", "user": 9971, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}

### add_pump: add given pump to device
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_pump", "user": 8710, "liquid": "water", "volume": 1000}

### calibrate_pumps: calibrate all pumps
- user: User

json example:

    {"cmd": "calibrate_pumps", "user": 9003}

### clean: clean machine
- user: User

json example:

    {"cmd": "clean", "user": 8138}

