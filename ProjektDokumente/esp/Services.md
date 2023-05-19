# Unterstützte Services

## Allgemein

- Name: Cocktail Machine ESP32
- UUID des Namens: c0605c38-3f94-33f6-ace6-7a5504544a80

Der ESP nutzt kein Pairing. Stattdessen wird jedem Benutzer eine User-ID zugewiesen, die dann bei den Befehlen mit angegeben werden muss. 

Um allgemeine Werte (z.B. den Zustand der Pumpen) auszulesen, reicht es, die entsprechenden Characteristics auszulesen. Die Characteristics unterstützen Notifications, damit sie nur neu ausgelesen werden müssen, wenn sie sich geändert haben. Die verfügbaren Services und Characteristics stehen im Abschnitt Status-Services.

Zur Kommunikation wird ein Kommunikations-Service benutzt. Der grundlegende Aufbau ist:

1. ein Write in die passende Characteristic, zu der eine Nachricht gesendet werden soll
2. der ESP sendet die Antwort mit Read+Notify zurück

Nachrichten sind dabei immer JSON-Objekte (in UTF8-Kodierung), üblicherweise ein Map. Das Kommunikationsprotokoll ist:

1. Der Client registriert, dass er eine Notification auf der Characteristic erhalten will.
2. Der Client schreibt die Nachricht in die Characteristic.
3. Wenn der Server die Nachricht erhalten und verarbeitet hat, schreibt er eine Antwort in die Characteristic. Der Client bekommt eine Notification und kann die Antwort auslesen.

Die meisten Nachrichten senden nur als Antwort, ob es einen Fehler gab, und wenn ja, welchen. Der genaue Aufbau der Nachrichten steht in [](Befehle.md).

Um mit dem ESP zu kommunizieren, muss ein Client zuerst eine User-ID erhalten. Die ID ist dauerhaft gültig und muss für einen Benutzer nur einmal registriert werden. Die ID funktioniert auch bei zukünftigen Verbindungen noch.

Um eine ID zu erhalten, sendet der Client den Befehl "init_user". Eine typische Nachricht ist:

    {"cmd": "init_user", "name": "Jane"}

Die Antwort ist dann:

    {"user": 100}

Die ID ist also 100 und kann dann in anderen Befehlen mit angegeben werden.

## Kommunikations-Service

- UUID des Service: dad995d1-f228-38ec-8b0f-593953973406
- UUID user:        eb61e31a-f00b-335f-ad14-d654aac8353d
- UUID admin:       41044979-6a5d-36be-b9f1-d4d49e3f5b73

## Status-Services

- UUID des Service: 0f7742d4-ea2d-43c1-9b98-bb4186be905d

### Pumpen
- UUID Characteristic: 1a9a598a-17ce-3fcd-be03-40a48587d04e

Wert: Map aller verfügbaren Pumpen und deren Füllstand

Beispiel:

    {"1": {"liquid": "lemonade", "volume": 200}}

### Flüssigkeiten
- UUID Characteristic: fc60afb0-2b00-3af2-877a-69ae6815ca2f

Wert: Map aller verfügbaren Flüssigkeiten und deren Menge

Beispiel:

    {"beer": 200, "lemonade": 2000, "orange juice": 2000}

### Zustand
- UUID Characteristic: e9e4b3f2-fd3f-3b76-8688-088a0671843a

Wert: Der aktuelle Zustand der Cocktail-Maschine und was sie macht.

Beispiel:

    "ready"

### Rezepte
- UUID Characteristic: 9ede6e03-f89b-3e52-bb15-5c6c72605f6c

Wert: alle gespeicherten Rezepte und deren Namen

Beispiel:

    [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]


### Cocktail
- UUID Characteristic: 7344136f-c552-3efc-b04f-a43793f16d43

Wert: Der Inhalt des aktuellen Cocktails, der gemischt wird.

Beispiel:

    [["beer", 250], ["lemonade", 250]]

### Aktueller Benutzer
- UUID Characteristic: 2ce478ea-8d6f-30ba-9ac6-2389c8d5b172

Wert: der aktuelle Benutzer, für den ein Cocktail gemacht wird 

Wenn kein Benutzer aktiv ist, ist der Wert `-1`.

Beispiel:

    5
    
### Letzte Ändererung
- UUID Characteristic: 586b5706-5856-34e1-ad17-94f840298816

Wert: Timestamp der letzten Änderung 

Wenn sich der Timestamp nicht geändert hat, sind die verfügbaren Rezepte und Zutaten noch die gleichen.

Der Timestamp ist ein interner Wert des ESP und hat keinen Bezug zur echten Zeit. 

Beispiel:

    275492
