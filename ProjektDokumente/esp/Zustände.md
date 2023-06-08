# Zustände der Cocktail-Maschine

Der aktuelle Zustand kann im Status-Service ausgelesen werden. (s. [Services.md]())

- `init`: Maschine wird initialisiert
- `ready`: Maschine ist bereit einen Befehl auszuführen und wartet
- `mixing`: Maschine macht einen Cocktail
- `pumping`: Maschine pumpt Flüssigkeiten
- `cocktail done`: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte `reset` ausgeführt werden.

