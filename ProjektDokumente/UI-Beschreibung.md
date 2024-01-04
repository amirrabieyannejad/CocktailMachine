# UI-Beschreibung

In der UI gibt es grunsätzlich zwei Zustände:

- der **An**gemeldete

- der **Ab**gemeldet

## Activity: Keine Kalibrierung

Drei mögliche Aktionen: 

- anmelden 
  
  - falsche Passwort: zurück in die Activity
  
  - und einleiten der automatischen Kalibrierung: Dialog: Kalibrierung

- wähle eine andere Cocktailmachine aus: Cocktailmachinewahlactivity  öffnet.

- laden (Übreprüfen, ob ein Admin die Kalibrierung vorgenommen hat)
  
  - Bei Erfolg (Admin hat die Kalibrierung getätigt. ): Hauptmenü öffnet
  
  - Bei Misserfolg: Toast "Cocktailmaschine ist noch nicht bereit."

## Dialog: Anmelden

Dialogfenster: 

Passwort angeben ("admin"). Gelange in den angemeldeten Zustand

## Dialog: Automatische Kalibrierung

1. Gebe die Anzahl (k) der Pumpen an.
   
   1. Toast "Es lädt

2. Ok-Fenster: Schließe Wasser an die Pumpen.

3. Ok-Fenster: Glas ohne Flüssigkeit

4. Ok-Fenster: Glas mit 100ml Wassre

5. Ok-Fenster: Leere das Glas

6. Warte-Fenster: Pumpe läuft.  k=k-1
   
   1. zu 5.: falls k>0
   
   2. zu 7.

7. Dialog mit Liste von k 
   
   Pumpen pumpen sind mit slot nummer markiert und orange hinterlegt, wenn noch keine Flüssigkeit hinterlegt ist, und grün wenn eine Flüssigkeit hinterlegt ist

8. Wähle orangene Pumpe

9. Wähle Flüssigkeit

10. Gibt Volumen an: 
    
    1. zu 7.: wenn noch nicht alle Flüssigkeiten ausgewählt wurden
    
    2. zum Hauptmenü

## Activity: Das Hauptmenü:

| Symbol               | Nächste Activity      | Funktion                        | Sichtbar                        | ausgelöste Reaktion                                              |
|:-------------------- |:--------------------- |:------------------------------- | ------------------------------- | ---------------------------------------------------------------- |
| Liste                | ListActivity          | Liste der Rezepte               | immer                           | Aktivitätenwechsel                                               |
| Liste mit Plussymbol | AddActivity           | Erstellung eines neuen Rezepts  | immer                           | Aktivitätenwechsel                                               |
| Bluetooth mit Lupe   | BluetoothScanActivity | Suche eine neue Cocktailmachine | immer                           | Aktivitätenwechsel                                               |
| Zahnrad              | Settingsactivity      | Einstellungsoptionen            | immer                           | Aktivitätenwechsel                                               |
| Pfeil in eine Tür    | -                     | Adminstratorenanmeldung         | Nur im **ab**gemeldeten Zustand | Zeigen eines Anmeldedialogs, Wechsel in den angemeldeten Zustand |
| Pfeil aus einer Tür  | -                     | Adminstratorenabmeldung         | Nur im **an**gemeldeten Zustand | Wechsel in den abgemeldeten Zustand                              |

# 

# 

## Liste von Elementen

Titel: Rezept, Zutaten, Serviervorschläge, Pumpen

Liste: Skrollbar Liste von Namen (Bspw. Long Island Ice Tea)

Haus: Gehe zum Hauptmenü.

drehender Pfeil: Lade die Datenbank neu!

verfügbar Switch: zeige nur verfügbare Elemente 

#### Pumpen

Statt dem Namen der Pumpen sind die Namen der zugeordneten Zutaten sichtbar.

## Anzeigen einzelner Elemente

Für alle Elemente gilt: unter dem Titel ist ein Zeile mit drei Buttons. Der erste und linke Button ist ein Stift und öffnet die Änderungsseite für das Element. Der zweite und mittige Button ist ein drehender Pfeil. Er lädt die gesamte Datenbank neu und setzt die Seite neu auf. Der dritte und rechte Button ist eine Liste und öffnet die Liste der Elemente. Wird also eine Zutat angezeigt, so öffnet sich die Zutatenliste. Im weiteren wird von dieser Zeile als Menüzeile in der Einzelansicht gesprochen.

### Zutat

Als Titel steht der Name der Zutat. Darunter kommt die Menüzeile. Unter der Menüzeile ist ein Gruppe von drei Sternen. Die Sterne haben die Farbe der Zutat. Darunter wird entweder angezeigt, dass die Zutat verfügbar oder nicht verfügbar ist. Dabei wird innerhalb einer Zeile links Ein grüner Punkt für verfügbar und dann neben der Text "verfügbar" angezeigt oder ein graues Kreuz und der Text "nicht verfügbar".

In der nächsten Zeile steht, ob die Zutat alkoholisch ist oder nicht. Dabei wird bei der Angabe alkoholisch eben der Text links angezeigt mit einem roten Warndreick auf der rechten Seite oder "nicht alkoholisch" mit einem grauen Warndreieck.

### Serviervorschlag

Als Titel steht der Name des Serviervorschlags. Darunter ist die Menüzeile und darunter die Beschreibung.

### Pumpe

Als Titel steht Slot und die Slotnummer. 

# Änder oder Hinzufügen

Für alle Elemente gilt:

Möchte man ein neues Element hinzufügen. Geht man in die entsprechende List und wählt den Floatingbutton inder rechten unteren Ecke mit dem Plussymbol aus.

Soll ein Element geändert werden, so wählt man in der Detailansich des zu ändernden Element in der Menüzeile den Stift, also das Symbol ganz rechts.

Jedes Element braucht einen Namen. Unter dem Titel gibt es ein Eingabefeld. Als Aufforderung steht in dem Feld in grauer Schrift "Füge einen Namen ein!". Wählt man das Feld aus, öffnet sich die Tastatur. Jetzt kann der Name eingegeben werden. Der eingegebene Name ist in schwarzer Schrift und nachdem ersten Einfügen eines Buchstabens ist die graue Aufforderung nicht mehr sichtbar. Handelt es sich hierbei allerdings um eine Änderung an einer bestehenden Element, dann ist zunächst die nich die Aufforderung zu sehen, sonder direkt der Name in schwarzer Schrift. Entfernt man alle Buchstaben in dem Feld, egal ob es sich um eine Änderung oder eine Neuerstellung handelt. Dann erscheint wieder dier graue Aufforderung.

Unter all den Eingabefeldern. Stehen zwei Button "Abbruch" und "Speichern". Wählt man den Abbruch gelangt man in die Liste der Elementen. Wählt man den Speichervorgang. So wird die Detailansicht der neuerstellten Zutat sichtbar.

## Zutat

Daraufhin öffnet sich die AddAktivität. Der Titel heißt "Zutat". 

Unter dem Namensfeld ist ein Switchbutton. Ist dieser aktiviert, steht direkt unter dem Button ein Feld "alkoholisch" mit einem roten Warndreieck davor. Ist ser Button nicht aktitviert, ist das Warndreick grau und es steht "nicht alkoholisch" da. Bei der Neuerstellung ist zunächst der Switchbutton im nicht aktivenn Zustand und das Feld mit dem Warndreick ist nicht sichtbar. Erst nachdem der Nutzende mit dem Switchbutton interagierte wird die Warndreickzeile sichtbar. Bei der Änderung ist das Warndreick direkt sichtbar und der Switchbutton ist im aktiven oder inaktiven Zustand je nachdem ob die zu ändernde Zutat alkoholisch oder nicht ist.

Unter der Alkoholgehaltabfrage steht eine weitere Aufforderung "Ändere die Farbe!". Sie steht in einem Button. Wählt man diesen aus öffnet sich ein Dialog, in dem eine Farbe ausgewählt werden kann. Dieser Dialog kann abgebrochen werden mit "Abbruch". Mit dem "Speichern" wird der Zutat eine Farbe zu geordnet. Handelt es sich eine Neuerstellung und das erste Anklicken der Farbauswahl, so ist eine zufällige Farbe hinterlegt. Wurde bereits eine Farbe ausgewählt oder es ist eine Änderung, dann ist die derzeit zugeordnete Farbe der Ausgangspunkt. Der Button ändert seine Farbe mit der ausgewählten Farbe. Ist noch keine ausgewählt worden bei der Neuerstellung, so hat der Button die Standardbuttonfarbe.

### Serviervorschlag

Als Titel steht "Serviervorschlag". Unter dem Namesfeld steht ein großes editierbares Textfeld. Dort steht in schwarz entweder die vorhandene Beschreibung bei einer Änderung oder in schwarz die eingegebene Beschreibung. Ist es eine Neuerstellung oder die gesamte Textfeldeingabe wurde entfernt (also die schwarzen Buchstaben), dann ist in grauen Buchstaben der Text "Füge eine Beschreibung ein." zu lesen, der so gleich wieder nicht sichtbar wird, sollte auch nur ein Buchstabe in diesem Feld eingegeben werden.

# Tests
