# Exceptions
## Access denied
wird geworfen wenn der User auf eine Funktion zugreift, die Adminrechte benötigt

## Datenbank
### NoSuchTableException
wird geworfen, wenn die aufgerufene Tabelle nicht exitiert
### NoSuchColumnException
wird geworfen, wenn die Spalte in der Tabelle nicht existiert
### NotInitializedDBException
wird geworfen, wenn die DB noch nicht mit einem Context initialisiert wurde
### NotInitializedDBException
wird geworfen, wenn die DB noch nicht mit einem Context initialisiert wurde


## Zutat und Pumpe
### MissingIngredientPumpException
wird geworfen, wenn die Verbindung zwischen Pumpe und Zutat fehlt.
### NewlyEmptyIngredientException
wird geworfen, wenn die Zutat frisch leer ist und benötigt Nachfüllen
- getter für Zutat




## Rezept
### NoSuchIngredientSettedException
wird geworfen, wenn die Zutat nicht im Rezept enthalten ist
### TooManyTimesSettedIngredientEcxception
wird geworfen, wenn es mehrer Volumenangaben zu einer Zutat in einem Rezept
### AlreadySetIngredient
wird geworfen, wenn die Zutat bereits im Rezept vorhanden ist
