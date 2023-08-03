# Fehler-Rückgaben der Cocktail-Maschine

Mögliche Fehler, die vom ESP zurückgegeben werden können. (s. [Befehle.md]())

- `ok`: alles in Ordnung
- `processing`: Befehl wird geparset (kein Fehler; kommt nur, wenn der Wert zu früh ausgelesen wird)
- `unsupported`: Befehl noch nicht implementiert
- `unauthorized`: Befehl nur für Admin verfügbar
- `invalid json`: ungültiges JSON 
- `message too big`: JSON-Nachricht zu lang
- `missing arguments`: Argumente im JSON-Befehl fehlen
- `unknown command`: unbekannter Befehl
- `command missing even though it parsed right`: Befehl wurde im ESP fehlerhaft implementiert :)
- `wrong comm channel`: falscher Channel für den Befehl (Admin vs. User)
- `invalid pump slot`: ungültige Pumpe ausgewählt
- `invalid volume`: ungültige Menge (z.B. `-5`)
- `invalid weight`: ungültiges Gewicht (z.B. `-5`)
- `invalid times`: ungültige Zeit (z.B. `-5`)
- `insufficient amounts of liquid available`: nicht genug Flüssigkeit vorhanden
- `liquid unavailable`: Flüssigkeit fehlt im ESP
- `recipe not found`: unbekanntes Rezept
- `recipe already exists`: Rezept mit dem gleichen Namen existiert bereits
- `missing ingredients`: Zutaten fehlen im Rezept
- `invalid calibration data`: Kalibrierungs-Werte sind ungültig (z.B. 2x die gleichen Werte)

