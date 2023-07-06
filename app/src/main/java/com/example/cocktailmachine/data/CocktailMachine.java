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
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

import org.json.JSONException;
import org.json.JSONObject;

public class CocktailMachine {
    //TODO: AMIR


    //FOR SENDING TO Bluetooth

    /**
     ### tare_scale: tariert die Waage
     - user: User

     JSON-Beispiel:

     {"cmd": "tare_scale", "user": 0}
     */
    public static void tareScale(Activity activity){
        //TO DO: tareScale
        try {
            BluetoothSingleton.getInstance().adminTareScale();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     ### calibrate_scale: kalibriert die Waage
     - user: User
     - weight: float

     Das Gewicht wird in Milligramm angegeben.

     JSON-Beispiel:

     {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     */
    public static void calibrateScale(Activity activity, float weight){
        //TODO: calibrateScale
        try {
            BluetoothSingleton.getInstance().adminCalibrateScale(weight);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void calibrateScale(Activity activity){
        //TODO: calibrateScale
        //TODO get Weight with dialog

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
        //TODO get factor with dialog

    }

    public static void sendScale(Activity activity, Float factor){
        //TO DO: send scale factor
        try {
            BluetoothSingleton.getInstance().adminSetScaleFactor(factor);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }




    //For Bluetooth use
    public static void setCurrentCocktail(JSONObject jsonObject) {

    }
    public static void setCurrentUser(JSONObject jsonObject) {

    }


    public static void setLastChange(JSONObject jsonObject){

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

