# ESP32 Simulator

- ein Simulator für den ESP32, der lokal ausgeführt werden kann und alle Funktionen unterstützt, damit das Protokoll entwickelt und getestet werden kann
- in Python geschrieben, damit die Entwicklung schneller ist
- Kommunikation über Bluetooth LE, HTTP oder STDIN im Terminal

## Dateien:

- simulator.py - ESP32-Simulator
- read_bluetooth.py - Test-Tool um alle Werte auf einem BLE-Server auszulesen
- test_simulator.py - Unit Tests
- requirements.txt - Python Dependencies

## Alle unterstützten Befehle des Protokolls auflisten
    ./simulation.py --commands

## Bluetooth Server starten

(braucht bluepy, siehe requirements.txt)

    ./simulation.py --bluetooth

## lokalen HTTP-Server starten
    ./simulation.py --web

## Befehle von STDIN einlesen
    ./simulation.py --stdin

## alle Werte eines Bluetooth-LE-Servers auflisten
     ./read_bluetooth.py [MAC des Servers]
   
z.B.:

    ./read_bluetooth.py "E8:31:CD:63:4F:12"

## Simulator testen
    mypy simulation.py
    pytest
