# Ablauf der Kalibrierung für eine Pumpe

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

# Ablauf der Kalibrierung für die Waage

1. Der Befehl `tare_scale` tariert die Waage neu aus, wenn (außer dem Gefäß) nichts vorhanden ist. Die Tarierung wird auch automatisch vor jedem Rezept ausgeführt.

2. Die Waage kann mit einem bekannten Gewicht und dem Befehl `calibrate_scale` kalibriert werden. Wenn das Gewicht z.B. 100mg ist:

       {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}

3. Alternativ kann der Skalierungsfaktor auch manuell mit `set_scale_factor` gesetzt werden.
