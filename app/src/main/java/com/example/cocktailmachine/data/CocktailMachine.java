package com.example.cocktailmachine.data;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

public class CocktailMachine {
    /**
     *
     ### define_pump: fügt Pumpe zu ESP hinzu
     - user: User
     - liquid: str
     - volume: float
     - slot: int

     JSON-Beispiel:

     {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}
    */
    public static void definePump(Activity activity){
        //TODO: definePump
    }
    /**
     ### refill_pump: füllt Pumpe auf
     - user: User
     - liquid: str
     - slot: int

     JSON-Beispiel:

     {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}
     */
    public static void refillPump(Activity activity){
        //TODO: refillPump
    }

    /**
     ### run_pump: lässt die Pumpe für eine bestimmte Zeit laufen
     - user: User
     - slot: int
     - time: int

     Die Zeit wird in Millisekunden angegeben.

     JSON-Beispiel:

     {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}
     */
    public static void runPump(Activity activity){
        //TODO: runPump
    }
    /**
     ### calibrate_pump: kalibriert die Pumpe mit vorhandenen Messwerten
     - user: User
     - slot: int
     - time1: int
     - time2: int
     - volume1: float
     - volume2: float

     Zur Kalibrierung müssen zwei Messwerte vorliegen, bei denen die Pumpe für eine unterschiedliche Zeit gelaufen ist. Daraus wird dann der Vorlauf und die Pumprate berechnet.

     Die Zeiten werden in Millisekunden und die Flüssigkeiten in Milliliter angegeben.

     JSON-Beispiel:

     {"cmd": "calibrate_pump", "user": 0, "slot": 1, "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}
     */
    public static void calibratePump(Activity activity){
        //TODO: setPumpTimes
    }
    /**
     ### set_pump_times: setzt die Kalibrierungswerte für eine Pumpe
     - user: User
     - slot: int
     - time_init: int
     - time_reverse: int
     - rate: float

     `time_init` ist die Vorlaufzeit und `time_init` die Rücklaufzeit in Millisekunden. Normalerweise sollten diese Werte ähnlich oder gleich sein. Die Rate wird in mL/ms angegeben.

     JSON-Beispiel:

     {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000, "rate": 1.0}
     */
    public static void setPumpTimes(Activity activity){
        //TODO: setPumpTimes
    }


    /**
     ### tare_scale: tariert die Waage
     - user: User

     JSON-Beispiel:

     {"cmd": "tare_scale", "user": 0}
     */
    public static void tareScale(Activity activity){
        //TODO: tareScale
    }

    /**
     ### calibrate_scale: kalibriert die Waage
     - user: User
     - weight: float

     Das Gewicht wird in Milligramm angegeben.

     JSON-Beispiel:

     {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     */
    public static void calibrateScale(Activity activity){
        //TODO: calibrateScale
    }


    /**
     ### set_scale_factor: setzt den Kalibrierungswert für die Waage
     - user: User
     - factor: float

     JSON-Beispiel:

     {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}
     */
    public static void setScaleFactor(Activity activity){
        //TODO: setScaleFactor

    }

    public static void sendScale(Activity activity, Float factor){
        //TODO: send scale factor
    }




















    /**
     ### clean: reinigt die Maschine
     - user: User

     JSON-Beispiel:

     {"cmd": "clean", "user": 0}
     */
    public static void clean(Activity activity){
        Pump.clean(activity);
    }

    /**
     ### restart: startet die Maschine neu
     - user: User

     JSON-Beispiel:

     {"cmd": "restart", "user": 0}
     */
    public static void restart(Activity activity){
        //TODO: restart
    }

    /**
     ### factory_reset: setzt alle Einstellungen zurück
     - user: User

     JSON-Beispiel:

     {"cmd": "factory_reset", "user": 0}


     */
    public static void factoryReset(Activity activity){
        //TODO: factoryReset
    }
}
