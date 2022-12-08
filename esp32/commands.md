# Supported commands

## General Commands

### test: dummy command that does nothing
json example:

    {"cmd": "test"}

### init_user: introduce yourself as a new user and receive your user id
- name: str

json example:

    {"cmd": "init_user", "name": "test-user"}

## User Commands

### make_recipe: make recipe
- user: User
- recipe: str

json example:

    {"cmd": "make_recipe", "user": 165, "recipe": "radler"}

## Admin Commands

### add_liquid: add given liquid to glass
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_liquid", "user": 3217, "liquid": "water", "volume": 30}

### define_recipe: define new recipe
- user: User
- recipe: str
- liquids: List[Tuple[str, float]]

json example:

    {"cmd": "define_recipe", "user": 1949, "recipe": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}

### add_pump: add given pump to device
- user: User
- liquid: str
- volume: float

json example:

    {"cmd": "add_pump", "user": 8525, "liquid": "water", "volume": 1000}

### calibrate_pumps: calibrate all pumps
- user: User

json example:

    {"cmd": "calibrate_pumps", "user": 1214}

### clean: clean machine
- user: User

json example:

    {"cmd": "clean", "user": 8728}

