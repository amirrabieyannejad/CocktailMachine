# GUI-Plan

Aussehen und Verhalten

- Start
  - Bluetoothverbindung herstellen
  - wähle Gerät
  - gebe gegebenenfalls Erlaubnis für die Bluetoothbenutzung
- Kalibrierung der Pumpe
  - bei Warnung
  - Pumpenerstellung
- Menu (nach Start //TODO: vielleicht Rezeptliste)
  - Login/Logout
  - Rezeptliste
  - Rezepterstellung
  - Bluetoothverbindungsaufbau
  - Einstellungen
- DB-Darstellungen
  - Listen von Objekten (Rezepte/Serviervorschläge/Zutaten/Pumpen)
    - Objekterstellung
    - Auswahl:
      - kurz: Obj.darstellung
      - lang: Obj.löschung -> Update Liste
  - Objektdarstellung
    - Editierfunktion (bei entsprechender Berechtigung)
    - Löschfkt. -> Obj.liste
    - Innerhalb der Objekte Verweise aufeinander
      - Rezepte->Zutat,Serviervorschlag
      - Zutat->Pumpe
      - Pumpe->Zutat
  - Objekterstellung -> Obj.darstellung
- Einstellungen
  - Main
  - Cocktailmachine
  - Pumpen
  - Waage