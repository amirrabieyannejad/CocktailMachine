# automatische Kalibrierung

Die automatische Kalibrierung kalibriert die Waage und die Pumpen relativ selbstständig. Es muss hauptsächlich nur das Gefäß geleert werden.

Der aktuelle Zustand kann im Status `state` ausgelesen werden. Siehe [Zustände.md]() für eine Übersicht der Zustände.

Ablauf:

1. Kalibrierung mit `calibration_start` anfangen. 

2. Die Maschine braucht ein leeres Gefäß. Der Zustand ist `calibration empty container`. Sobald das Gefäß hingestellt wurde, kann mit dem Befehl `calibration_add_empty` weitergemacht werden.

3. Die Maschine tariert die Waage. Danach braucht sie ein bekanntes Gewicht (z.B. 100ml Wasser). Das Gefäß sollte gefüllt werden. Der Zustand ist `calibration known weight`. Sobald das Gewicht bereit ist, kann mit `calibration_add_weight` weitergemacht werden.

4. Die Maschine kalibriert die Waage und bereitet die Pumpen vor. Das Gefäß muss wieder geleert werden und es wird mit `calibration_add_empty` weitergemacht.

5. Es wird jetzt jede Pumpe zweimal gepumpt. Nach jedem Pumpen muss das Gefäß geleert werden. Der Zustand ist immer `calibration empty container`, wenn geleert werden muss. Danach wird mit `calibration_add_empty` weitergemacht.

6. Wenn alle Pumpen fertig sind, berechnet die Maschine die neuen Werte. Danach ist die Kalibrierung fertig und der Zustand ist `calibration done`. Das Gefäß kann entfernt werden. Der letzte Befehl ist `calibration_finish`.

# manuelle Kalibrierung

## Ablauf der Kalibrierung für eine Pumpe

1. Eine Messung kann durchgeführt werden, indem die Pumpe manuell mit dem Befehl `run_pump` angesteuert wird. Beispielsweise für 10s:

       {"cmd": "run_pump", "user": 0, "slot": 1, "time": 10000}
       

   Danach muss das Volumen, das insgesamt gepumpt wurde, gemessen werden.

2. Dann muss der Vorgang nochmal mit einer anderen Zeit wiederholt werden, z.B. für 20s:

       {"cmd": "run_pump", "user": 0, "slot": 1, "time": 20000}

3. Anschließend kann die Pumpe mit `calibrate_pumps` kalibriert werden. Wenn die Volumen z.B. 15mL und 20mL waren:

       {"cmd": "calibrate_pump", "user": 0, "slot": 1, "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}
       

   Die berechneten Werte werden im Debug-Log auch angezeigt.


4. Der Füllstand der Pumpe ist nach dem Kalibrieren üblicherweise in einem falschen Zustand. Am besten wird der Füllstand mit `refill_pump` zurückgesetzt.

5. Die Zeiten werden auf dem ESP gespeichert. 

6. Alternativ können die Werte auch direkt mittels `set_pump_times` gesetzt werden.

## Ablauf der Kalibrierung für die Waage

1. Der Befehl `tare_scale` tariert die Waage neu aus, wenn (außer dem Gefäß) nichts vorhanden ist. Die Tarierung wird auch automatisch vor jedem Rezept ausgeführt.

2. Die Waage kann mit einem bekannten Gewicht und dem Befehl `calibrate_scale` kalibriert werden. Wenn das Gewicht z.B. 100mg ist:

       {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}

3. Alternativ kann der Skalierungsfaktor auch manuell mit `set_scale_factor` gesetzt werden.
