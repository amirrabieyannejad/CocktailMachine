# Unterstützte Services

## Allgemein

- UUID der Cocktail-Maschine: 0f7742d4-ea2d-43c1-9b98-bb4186be905d
- Name: Cocktail Machine ESP32 (Simulator)

Der ESP nutzt kein Pairing. Stattdessen wird jedem Benutzer eine User-ID zugewiesen, die dann bei den Befehlen mit angegeben werden muss. 

Um allgemeine Werte (z.B. den Zustand der Pumpen) auszulesen, reicht es, die entsprechenden Characteristics auszulesen. Die Characteristics unterstützen Notifications, damit sie nur neu ausgelesen werden müssen, wenn sie sich geändert haben. Die verfügbaren Services und Characteristics stehen im Abschnitt Status-Services.

Zur Kommunikation wird ein Kommunikations-Service benutzt. Der grundlegende Aufbau ist:

1. eine Write-Characteristic (message), auf der eine Nachricht gesendet werden kann
2. eine Read+Notify-Characteristic (response), auf der die Antwort vom ESP zurückgeschickt wird

Nachrichten sind dabei immer JSON-Objekte (in UTF8-Kodierung), üblicherweise ein Map. Das Kommunikationsprotokoll ist:

1. Der Client registriert, dass er eine Notification auf der response-Characteristic erhalten will.
2. Der Client schreibt die Nachricht in die message-Characteristic.
3. Wenn der Server die Nachricht erhalten und verarbeitet hat, schreibt er eine Antwort in die response-Characteristic. Der Client bekommt eine Notification und kann die Antwort auslesen.

Die meisten Nachrichten senden nur als Antwort, ob es einen Fehler gab, und wenn ja, welchen. Der genaue Aufbau der Nachrichten steht in [](Befehle.md).

Um mit dem ESP zu kommunizieren, muss ein Client zuerst eine User-ID erhalten. Die ID ist dauerhaft gültig und muss für einen Benutzer nur einmal registriert werden. Die ID funktioniert auch bei zukünftigen Verbindungen noch.

Um eine ID zu erhalten, sendet der Client den Befehl "init_user". Eine typische Nachricht ist:

    {"cmd": "init_user", "name": "Jane"}

Die Antwort ist dann:

    {"user": 100}

Die ID ist also 100 und kann dann in anderen Befehlen mit angegeben werden.

## Kommunikations-Service

- UUID des Service: dad995d1-f228-38ec-8b0f-593953973406
- UUID message:     eb61e31a-f00b-335f-ad14-d654aac8353d
- UUID response:    06dc28ef-79a4-3245-85ce-a6921e35529d

## Status-Services

### Flüssigkeiten
- UUID des Service:    17eed42a-f06b-3f58-9b26-60e78bccf857
- UUID Characteristic: fc60afb0-2b00-3af2-877a-69ae6815ca2f

Wert: Map aller verfügbaren Flüssigkeiten und deren Menge

Beispiel:

    {"beer": 200, "lemonade": 2000, "orange juice": 2000}

### Zustand
- UUID des Service:    addf5391-2030-3cf0-a64f-31d5156d7f00
- UUID Characteristic: e9e4b3f2-fd3f-3b76-8688-088a0671843a

Wert: Der aktuelle Zustand der Cocktail-Maschine und was sie macht.

Beispiel:

    "ready"

### Rezepte
- UUID des Service:    8f0aec28-5985-335e-baa2-8e03ce08b513
- UUID Characteristic: 9ede6e03-f89b-3e52-bb15-5c6c72605f6c

Wert: Alle gespeicherten Rezepte und deren Namen.

Beispiel:

    [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]


### Cocktail
- UUID des Service:    8a421a72-b9a0-342d-ab57-afa3d67149d1
- UUID Characteristic: 7344136f-c552-3efc-b04f-a43793f16d43

Wert: Der Inhalt des aktuellen Cocktails, der gemischt wird.

Beispiel:

    [["beer", 250], ["lemonade", 250]]
