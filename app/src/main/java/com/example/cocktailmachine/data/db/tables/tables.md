# DB Tabellen Übersicht

## Tables
ist ein Sammelbecken für alle Datenbanktabellen. 
Diese Klasse hat für jede Tabelle eine statisches Attribut.
Sie bietet außerdem je zwei Funktion alle Tabellen neu zu generieren und zu löschen. 
Der Unterschied liegt in der Variante:
- einmal wird eine SQL-DB gegeben und damit gelöscht
- (privat) einmal werden die Befehle als Liste von Strings ausgegeben
## BasicColumn
ist eine abstrakte Klasse, die die Standardfunktionen einer Datenbanktabelle bündelt.
