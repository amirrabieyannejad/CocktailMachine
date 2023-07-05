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

### abort: bricht das aktuelle Rezept ab
- user: User

JSON-Beispiel:

    {"cmd": "abort", "user": 483}

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

### define_recipe: definiert ein neues Rezept
- user: User
- name: str
- ingredients: List[Tuple[str, float]]

JSON-Beispiel:

    {"cmd": "define_recipe", "user": 0, "name": "radler", "ingredients": [["beer", 250], ["lemonade", 250]]}

### edit_recipe: editiert ein Rezept
- user: User
- name: str
- ingredients: List[Tuple[str, float]]

JSON-Beispiel:

    {"cmd": "edit_recipe", "user": 0, "name": "radler", "ingredients": [["beer", 250], ["lemonade", 250]]}

### delete_recipe: löscht ein Rezept
- user: User
- name: str

JSON-Beispiel:

    {"cmd": "delete_recipe", "user": 0, "name": "radler"}

## Admin-Befehle

### define_pump: fügt Pumpe zu ESP hinzu
- user: User
- liquid: str
- volume: float
- slot: int

JSON-Beispiel:

    {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}

### refill_pump: füllt Pumpe auf
- user: User
- liquid: str
- slot: int

JSON-Beispiel:

    {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}

### run_pump: lässt die Pumpe für eine bestimmte Zeit laufen 
- user: User
- slot: int
- time: int

Die Zeit wird in Millisekunden angegeben.

JSON-Beispiel:

    {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}

### calibrate_pump: kalibriert die Pumpe mit vorhandenen Messwerten
- user: User
- slot: int
- time1: int
- time2: int
- volume1: float
- volume2: float

Zur Kalibrierung müssen zwei Messwerte vorliegen, bei denen die Pumpe für eine unterschiedliche Zeit gelaufen ist. Daraus wird dann der Vorlauf und die Pumprate berechnet.

Die Zeiten werden in Millisekunden und die Flüssigkeiten in Milliliter angegeben.

JSON-Beispiel:

    {"cmd": "calibrate_pump", "user": 0, "slot": 1, "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}

### set_pump_times: setzt die Kalibrierungswerte für eine Pumpe
- user: User
- slot: int
- time_init: int
- time_reverse: int
- rate: float

`time_init` ist die Vorlaufzeit und `time_init` die Rücklaufzeit in Millisekunden. Normalerweise sollten diese Werte ähnlich oder gleich sein. Die Rate wird in mL/ms angegeben.

JSON-Beispiel:

    {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000, "rate": 1.0}

### tare_scale: tariert die Waage
- user: User

JSON-Beispiel:

    {"cmd": "tare_scale", "user": 0}

### calibrate_scale: kalibriert die Waage
- user: User
- weight: float

Das Gewicht wird in Milligramm angegeben.

JSON-Beispiel:

    {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}

### set_scale_factor: setzt den Kalibrierungswert für die Waage
- user: User
- factor: float

JSON-Beispiel:

    {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}

### clean: reinigt die Maschine
- user: User

JSON-Beispiel:

    {"cmd": "clean", "user": 0}

### restart: startet die Maschine neu 
- user: User

JSON-Beispiel:

    {"cmd": "restart", "user": 0}

### factory_reset: setzt alle Einstellungen zurück
- user: User

JSON-Beispiel:

    {"cmd": "factory_reset", "user": 0}

