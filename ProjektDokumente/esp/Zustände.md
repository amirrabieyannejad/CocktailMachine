# Zustände der Cocktail-Maschine

Der aktuelle Zustand kann im Status-Service ausgelesen werden. (s. [Services.md]())

## Rezepte

- `ready`: Maschine ist bereit einen Cocktail zu machen
- `waiting for container`: Maschine wartet auf ein leeres Gefäß
- `mixing`: Maschine macht einen Cocktail
- `pumping`: Maschine pumpt Flüssigkeiten
- `cocktail done`: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte `take_cocktail` ausgeführt werden.

## Kalibrierung

- `ready`: Maschine ist bereit für eine Kalibrierung
- `calibration empty container`: Kalibrierung wartet auf ein leeres Gefäß. Es sollte `calibration_add_empty` ausgeführt werden.
- `calibration known weight`: Kalibrierung wartet auf ein Gewicht. Es sollte `calibration_add_weight` ausgeführt werden.
- `calibration pumps`: Kalibrierung pumpt Flüssigkeiten
- `calibration calculation`: Kalibrierung berechnet die Werte
- `calibration done`: Kalibrierung fertig. Es sollte `calibration_finish` ausgeführt werden.



