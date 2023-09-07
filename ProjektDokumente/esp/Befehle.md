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

### test (USER): Dummy-Befehl, der nichts macht
JSON-Beispiel:

    {"cmd": "test"}

### init_user (USER): als neuer Benutzer registrieren und eine User-ID erhalten
- name: str

JSON-Beispiel:

    {"cmd": "init_user", "name": "test-user"}

    --> {"user": 100}

### reset (ADMIN): die Maschine zurücksetzen 
- user: User

Der Befehl entfernt den aktuellen Cocktail und setzt die Maschine wieder in den normalen Zustand zurück. Das ist nur beim Testen notwendig.

JSON-Beispiel:

    {"cmd": "reset", "user": 0}

### reset_error (ADMIN): gespeicherten Fehler zurücksetzen
- user: User

Der Befehl entfernt den aktuellen Fehler und macht im normalen Betrieb weiter. Das kommt meistens nur vor, wenn ein Rezept ein Problem hatte und die Maschine das Problem nicht beheben konnte.

JSON-Beispiel:

    {"cmd": "reset_error", "user": 0}

### clean (ADMIN): reinigt die Maschine
- user: User

JSON-Beispiel:

    {"cmd": "clean", "user": 0}

### restart (ADMIN): startet die Maschine neu 
- user: User

JSON-Beispiel:

    {"cmd": "restart", "user": 0}

### factory_reset (ADMIN): setzt alle Einstellungen zurück
- user: User

JSON-Beispiel:

    {"cmd": "factory_reset", "user": 0}

## Rezepte definieren

### define_recipe (USER): definiert ein neues Rezept
- user: User
- name: str
- ingredients: List[Tuple[str, float]]

JSON-Beispiel:

    {"cmd": "define_recipe", "user": 0, "name": "radler", "ingredients": [["beer", 250], ["lemonade", 250]]}

### edit_recipe (USER): editiert ein Rezept
- user: User
- name: str
- ingredients: List[Tuple[str, float]]

JSON-Beispiel:

    {"cmd": "edit_recipe", "user": 0, "name": "radler", "ingredients": [["beer", 250], ["lemonade", 250]]}

### delete_recipe (USER): löscht ein Rezept
- user: User
- name: str

JSON-Beispiel:

    {"cmd": "delete_recipe", "user": 0, "name": "radler"}

## Rezepte machen

Der Ablauf um ein Rezept zu machen ist:

1. Das Rezept mit `queue_recipe` in Auftrag geben. Das Rezept kommmt in die Warteschlange. Die aktuellen Benutzer in der Warteschlange können mit dem Status `user` ausgelesen werden.

2. Wenn die Maschine bereit ist, wird der Status `state` auf `waiting for container` gesetzt. Sobald ein Gefäß in der Maschine ist, kann das Rezept mit dem Befehl `start_recipe` gestartet werden.

3. Wenn das Rezept fertig ist, wird der Status `state` auf `cocktail done` gesetzt. Der Cocktail kann entnommen werden. Das Rezept wird mit `take_cocktail` beendet. 

4. Die Maschine beginnt dann mit dem nächsten Rezept in der Warteschlange.

### queue_recipe (USER): gibt ein Rezept in Auftrag
- user: User
- recipe: str

JSON-Beispiel:

    {"cmd": "queue_recipe", "user": 8858, "recipe": "radler"}

### start_recipe (USER): fängt das Rezept an, wenn die Maschine bereit ist
- user: User

JSON-Beispiel:

    {"cmd": "start_recipe", "user": 8858}

### cancel_recipe (USER): bricht das aktuelle Rezept ab
- user: User

JSON-Beispiel:

    {"cmd": "cancel_recipe", "user": 483}

### take_cocktail (USER): gibt Bescheid, dass der Cocktail entnommen wurde
- user: User

JSON-Beispiel:

    {"cmd": "take_cocktail", "user": 483}

### add_liquid (USER): fügt Flüssigkeit zum aktuellen Rezept hinzu
- user: User
- liquid: str
- volume: float

JSON-Beispiel:

    {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}

## Pumpen

### define_pump (ADMIN): fügt Pumpe zu ESP hinzu
- user: User
- liquid: str
- volume: float
- slot: int

JSON-Beispiel:

    {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}

### edit_pump (ADMIN): editiert eine Pumpe
- user: User
- liquid: str
- volume: float
- slot: int

Nur die Flüssigkeit und das Volumen werden angepasst. Die Kalibrierung bleibt erhalten.

JSON-Beispiel:

    {"cmd": "edit_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}

### refill_pump (ADMIN): füllt Pumpe auf
- user: User
- liquid: str
- slot: int

JSON-Beispiel:

    {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}

## automatische Kalibrierung

siehe [Kalibrierung.md]()

### calibration_start (ADMIN): Kalibrierung anfangen
- user: User

JSON-Beispiel:

    {"cmd": "calibration_start", "user": 0}

### calibration_cancel (ADMIN): Kalibrierung abbrechen
- user: User

JSON-Beispiel:

    {"cmd": "calibration_cancel", "user": 0}

### calibration_finish (ADMIN): Kalibrierung fertig 
- user: User

JSON-Beispiel:

    {"cmd": "calibration_finish", "user": 0}

### calibration_add_empty (ADMIN): leeres Gefäß ist bereit
- user: User

JSON-Beispiel:

    {"cmd": "calibration_add_empty", "user": 0}

### calibration_add_weight (ADMIN): Gefäß ist mit einer Menge Wasser gefüllt
- user: User
- weight: float (in Gramm)

JSON-Beispiel:

    {"cmd": "calibration_add_weight", "user": 0, "weight": 100.0}

## manuelle Kalibrierung

### run_pump (ADMIN): lässt die Pumpe für eine bestimmte Zeit laufen 
- user: User
- slot: int
- time: int

Die Zeit wird in Millisekunden angegeben.

JSON-Beispiel:

    {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}

### calibrate_pump (ADMIN): kalibriert die Pumpe mit vorhandenen Messwerten
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

### set_pump_times (ADMIN): setzt die Kalibrierungswerte für eine Pumpe
- user: User
- slot: int
- time_init: int
- time_reverse: int
- rate: float

`time_init` ist die Vorlaufzeit und `time_reverse` die Rücklaufzeit in Millisekunden. Normalerweise sollten diese Werte ähnlich oder gleich sein. Die Rate wird in mL/ms angegeben.

JSON-Beispiel:

    {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000, "rate": 1.0}

### tare_scale (ADMIN): tariert die Waage
- user: User

JSON-Beispiel:

    {"cmd": "tare_scale", "user": 0}

### calibrate_scale (ADMIN): kalibriert die Waage
- user: User
- weight: float

Das Gewicht wird in Milligramm angegeben.

JSON-Beispiel:

    {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}

### set_scale_factor (ADMIN): setzt den Kalibrierungswert für die Waage
- user: User
- factor: float

JSON-Beispiel:

    {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}


