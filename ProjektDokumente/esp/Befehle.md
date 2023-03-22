# Unterstützte Befehle

Nachrichten werden als JSON-Maps (in UTF8) kodiert und folgen alle dem selben Format.

Für unverschlüsselte Nachrichten ist das Format:

    {"cmd": "der Befehl", "user": "die User-ID", "argument": "weitere Werte", ...}

Das "cmd"-Feld ist verpflichtend und enthält den Namen des Befehls. Die meisten Nachrichten benötigen auch ein "user"-Feld mit der User-ID. Eventuelle weitere Argumente werden als weitere Felder in der Map gesendet.

Für verschlüsselte Nachrichten wird die Nachricht in einen Wrapper gepackt:

    {"msg": "verschlüsselte JSON-Nachricht", "user": "die User-ID"}

Der Key zum Entschlüsseln ist mit der User-ID verbunden und wird benutzt, um die Nachricht in "msg" zu ver-/entschlüsseln. Die entschlüsselte Nachricht ist ein normaler Befehl der Form `{"cmd": ...}`. 

Momentan unterstützt der ESP noch keine verschlüsselten Nachricht.

## Allgemeine Befehle

### test: Dummy-Befehl, der nichts macht
JSON-Beispiel:

    {"cmd": "test"}

### init_user: als neuer Benutzer registrieren und eine User-ID erhalten
- name: str

JSON-Beispiel:

    {"cmd": "init_user", "name": "test-user"}

    --> {"user": 100}

## User-Befehle

### reset: die Maschine zurücksetzen, damit sie einen neuen Cocktail machen kann
- user: User

JSON-Beispiel:

    {"cmd": "reset", "user": 9650}

### make_recipe: mischt das Rezept
- user: User
- recipe: str

JSON-Beispiel:

    {"cmd": "make_recipe", "user": 8858, "recipe": "radler"}

### add_liquid: fügt Flüssigkeit zum Cocktail hinzu
- user: User
- liquid: str
- volume: float

JSON-Beispiel:

    {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}

## Admin-Befehle

### define_recipe: definiert ein neues Rezept oder ändert ein bestehendes Rezept
- user: User
- name: str
- liquids: List[Tuple[str, float]]

JSON-Beispiel:

    {"cmd": "define_recipe", "user": 0, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}

### define_pump: fügt Pumpe zu ESP hinzu
- user: User
- liquid: str
- volume: float
- slot: int

JSON-Beispiel:

    {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}

### calibrate_pumps: kalibriert alle Pumpen
- user: User

JSON-Beispiel:

    {"cmd": "calibrate_pumps", "user": 0}

### clean: reinigt die Maschine
- user: User

JSON-Beispiel:

    {"cmd": "clean", "user": 0}

### restart: startet die Maschine neu 
- user: User
- factory_reset: bool (optional)

Falls `factory_reset` auf `true` gesetzt wird, werden auch alle Einstellungen gelöscht.

JSON-Beispiel:

    {"cmd": "restart", "user": 0}
    {"cmd": "restart", "user": 0, "factory_reset": true}

