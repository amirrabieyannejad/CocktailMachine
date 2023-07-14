# Menü
ist Main Activity und soll die Schnellwahlen zur Rezeptliste/-erstellungs, 
den Einstellungen und Login/Logout zu der Adminstratorensicht liefern 
mit passenden Symbolen.



- Show available Cocktails  
  + *Methode*: openRecipeList   
  + *verwendete Grafik*: ic_list
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Rezeptliste
- add recipe
  + *Methode*: openRecipeCreator   
  + *verwendete Grafik*: ic_list_add
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Rezepterstellungsfenster
- FillAnimation  
  + *Methode*: openGlassFillAnimationView   
  + *verwendete Grafik*: ic_list_add
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Test Activity
  + soll wieder rausgenommen werden
- swipe thing
  + *Methode*: openSingelCocktailView   
  + *verwendete Grafik*: ic_single_cocktail_choice_2
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Test Activity
  + soll wieder rausgenommen werden
- FindBluetooth
  + *Methode*: openDeviceScan   
  + *verwendete Grafik*: ic_find_bluetooth
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Seite zum Finden der Bluetoothverbindung 
- Settings
  + *Methode*: openSettings   
  + *verwendete Grafik*: ic_options
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Settings
- Grafik
  + *Methode*: openGrafik   
  + *verwendete Grafik*: ic_glass
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Test Activity
  + soll wieder rausgenommen werden
- TestSiteForBluetooth
  + *Methode*: testEnviroment   
  + *verwendete Grafik*: ic_bluetooth_test
  + *Sichtbarkeit*: immer
  + *Angeklickt*: öffnet Test Activity
  + soll wieder rausgenommen werden
- Logout
  + *Methode*: logout   
  + *verwendete Grafik*: ic_logout
  + *Sichtbarkeit*: sichtbar, wenn admin
  + *Angeklickt*: wird zum User
- Login
  + *Methode*: login   
  + *verwendete Grafik*: ic_login
  + *Sichtbarkeit*: sichtbar, wenn user
  + *Angeklickt*: öffnet Passwort Dialog. Wenn richtiges Passwort eingegeben wurde, wird zu Admin.
