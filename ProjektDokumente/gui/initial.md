# Schritt 1:
Bluetoothverbindung zum Esp, bzw. der Cocktailmaschine herstellen
    zuständige Klasse: *Device Scan Activity* im Ordner bluetoothgatt
    erstellt von: Amir

# Schritt 2: check 
Ist die Cocktailmaschine/ESP bereits kalibriert?
*CocktailMachine.isCocktailMachineSet*
-> __JA: Schritt 3__
-> __Nein: Schritt 2a__

## Schritt 2a: Kalibrierung
Dieser Schritt wird mit einem Dialogflow erledigt. Aufgerufen unter: *CocktailMachineCalibration.start*
  * Bitte erst Wasser beim ersten Durchgang : *GetDialog.startAutomaticCalibration*
  * Tarierung : *GetDialog.firstTaring*
  * Gefässe mit 100 ml Wasser : *GetDialog.getGlass*
  * Pumpenanzahl : *GetDialog.enterNumberOfPumps*
  * Automatische Kalibrierung : *GetDialog.firstTaring*
  * Warten auf fertig : *GetDialog.waitingAutomaticCalibration*
  * Angabe von Zutaten : *GetDialog.setIngredientsForPumps*
    * Zutat
    * Volumen
    * Minimales Volumen

# Schritt 3: Menuaufruf



# Allgemeine 

