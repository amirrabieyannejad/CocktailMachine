# Übersicht

## AdminRights
- ist ein Singleton, weil es Daten für die ganze App speichert
- bietet 
  + User oder Admin
    * derzeitige Nutzerrechte
    * login/ logout und login Dialog
    * UserPrivilegeLevel Enum (User, Admin)
  + User init für Bluetooth
    * wird wahrscheinlich in den Bluetooth Service wandern
    * stellt json object mit cmd struktur bereit
    * initiert user
    * gibt Auskunft ob user initiiert ist

## Orientation
    - ???

## Status
- bündelt Bluetooth Status wie in Zustände.md beschrieben als Enum
  + `init`: Maschine wird initialisiert 
  + `ready`: Maschine ist bereit einen Befehl auszuführen und wartet
  + `mixing`: Maschine macht einen Cocktail
  + `pumping`: Maschine pumpt Flüssigkeiten
  + `cocktail done`: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte `reset` ausgeführt werden.
- enthält eine Funktion die mit Bluetooth den derzeitig Status abfragen soll


## DataBase
- Connection

- für alle Datenbanklelemente (DataBaseElement)
  + ID (LONG) get/set
  + Save (saving status/ needs update/delete)
  + isAvailable
  + makeNew um Neue zuerstellen als statische Funktion
  
- Pumpe (Pump)
  + minimales Pumpvolumen
  + Zutat
  + Volumen
  + Verbindung mit einer Zutat
  + Statics
    * get PumpList
    * get Pump by ID
    * makeNew, mit Zutatsnamen und Volumen oder ohne alles
    * DB
      - reload all
      - set up k empty pumps
    * Bluetooth
      - Liquid Status get/set {"beer": 200, "lemonade": 2000, "orange juice": 2000}
      - Pump Status get/set {"1": {"liquid": "lemonade", "volume": 200}}
      - clean: call cleaning with bluetooth
      - calibrate
      - sync: ask liquid status from cocktail machine and update
  + Funktionen für einzelne Pumpe
    * get/set minimales Pumpvolumen
    * get Zutat und Zutateninformation (Name, Id)
    * set Zutat mit Klasse oder id
    * set volume mit zusätzlicher Funktion "empty" um sie gleich auf 0 zu setzen
    * asMessage JSONObject {"beer": 200}
   
- Zutat (Ingredient)
  + Name
  + Fotos Urls
  + alkoholisch
  + Pumpe
  + Farbe
  + Statics
    * get Zutaten Liste alle/IDs/enthält String/ID/String/nur verfügbare
    * erstelle neue Zutat mit makeNew
    * suche nach Zutat oder erstelle neue mit ggb Namen
    * makeNew, mit Zutatsnamen und Volumen oder ohne alles
  + Funktionen für einzelne Zutaten
    * get/set für Name, Fotos, Pumpe, Farbe, alkoholisch
    * Pumpen funktionen (fill, empty)

- Serviervorschlag (Topic)
  + Name
  + Beschreibung
  + Statics
    * get Topic Liste alle/zugehörig zum Rezept
    * get Zutaten ID/Name
    * erstelle neue Zutat mit makeNew
    * makeNew, mit Namen und Beschreibung
  + Funktionen für einzelne Topics
    * get/set für Name, Beschreibung

- Rezept (Recipe)
  + Name
  + Zutaten(Ingredients)
  + Serviervorschläge(Topics)
  + Fotos Urls
  + alkoholisch
  + Statics
    * get Rezept Liste alle/IDs/enthält String/ID/String/nur verfügbare
    * erstelle neues Rezept mit makeNew
    * suche nach Zutat oder erstelle neue mit ggb Namen
    * makeNew, mit Zutatsnamen und Volumen oder ohne alles
    * Bluetooth
      * get/set Rezepte in JSON Format [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
      * sync hole alle Rezepte
      * send to be mixed
  + Funktionen für einzelne Zutaten
    * get/set/remove/add für Name, Fotos, Zutaten, Serviervorschläge, alkoholisch
    * asMessage JSONObject {"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}
    * reloadAvailability from db
  