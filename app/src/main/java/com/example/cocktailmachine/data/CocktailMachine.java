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

    static int time;
    static boolean dbChanged = false;
    static Recipe currentRecipe;
    static int currentUser = -1;


    //SCALE

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


    public static void abort(Activity activity){

        try {
            BluetoothSingleton.getInstance().abort();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @author Johanna Reidt
     * @param activity
     */
    public static void getLastChange(Activity activity){
        try {
            BluetoothSingleton.getInstance().adminReadLastChange();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * updateDBIfCanged
     * @author Johanna Reidt
     * @param activity
     */
    public static void updateRecipeListIfChanged(Activity activity){
        getLastChange(activity);
        if(dbChanged){
            try {
                BluetoothSingleton.getInstance().adminReadRecipesStatus();
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Recipe getCurrentCocktail(Activity activity){
        try {
            BluetoothSingleton.getInstance().adminReadCurrentCocktail();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentRecipe;
    }

    public static int getCurrentUser(Activity activity){
        try {
            BluetoothSingleton.getInstance().adminReadCurrentUser();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        return currentUser;
    }



    //For Bluetooth use
    public static void setCurrentCocktail(JSONObject jsonObject) {

    }
    public static void setCurrentUser(JSONObject jsonObject) {

    }


    public static void setLastChange(JSONObject jsonObject){
        int old_time = time;
        time = jsonObject.optInt("", -1);
        if(old_time<time){
            dbChanged = true;
        }
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
        try {
            BluetoothSingleton.getInstance().adminRestart();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     ### factory_reset: setzt alle Einstellungen zurück
     - user: User

     JSON-Beispiel:

     {"cmd": "factory_reset", "user": 0}


     */
    public static void factoryReset(Activity activity){
        //TODO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * TODO: find usage
     *
     * to be called if cocktail done and new Cocktail should be mixed
     * reset: reset the machine so that it can make a new cocktail
     * JSON-sample: {"cmd": "reset", "user": 9650}
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.

     * @author Johanna Reidt
     * @param activity
     */
    public static void reset(Activity activity){
        //TODO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

