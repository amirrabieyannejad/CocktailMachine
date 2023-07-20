package com.example.cocktailmachine.data;

import android.app.Activity;
import android.widget.Toast;

import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.ui.model.v2.GetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class CocktailMachine {
    //TO DO: AMIR


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
            Toast.makeText(activity, "Tarierung der Waage eingeleitet.",Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * send to esp with bluetooth
     ### calibrate_scale: kalibriert die Waage
     - user: User
     - weight: float

     Das Gewicht wird in Milligramm angegeben.

     JSON-Beispiel:

     {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     */
    public static void sendCalibrateScale(Activity activity, float weight){
        //TO DO: calibrateScale
        try {
            BluetoothSingleton.getInstance().adminCalibrateScale(weight);
            Toast.makeText(activity, "Kalibrierung der Waage mit Gewicht: "+weight+" g",Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * calibrate Scale with Dialog view
     * @author Johanna Reidt
     * @param activity
     */
    public static void calibrateScale(Activity activity){
        //TO DO: calibrateScale
        //TO DO get Weight with dialog
        GetDialog.calibrateScale(activity);

    }


    /**
     ### set_scale_factor: setzt den Kalibrierungswert für die Waage
     - user: User
     - factor: float

     JSON-Beispiel:

     {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}
     */
    public static void scaleFactor(Activity activity){
        //TO DO: setScaleFactor
        //TO DO get factor with dialog
        GetDialog.calibrateScaleFactor(activity);

    }

    public static void sendScaleFactor(Activity activity, Float factor){
        //TO DO: send scale factor
        try {
            BluetoothSingleton.getInstance().adminSetScaleFactor(factor);
            Toast.makeText(activity, "Skalierung der Waage mit Faktor: "+factor,Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
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


















    public static boolean isCocktailMachineSet(Activity activity){
        //TODO: add bluetooth /esp implementation
        Random r = new Random(42);
        return r.nextDouble() >= 0.5;
    }

    /**
     ### clean: reinigt die Maschine
     - user: User

     JSON-Beispiel:

     {"cmd": "clean", "user": 0}
     */
    public static void clean(Activity activity){
        //CocktailMachine.clean(activity);
        try {
            BluetoothSingleton.getInstance().adminClean();
            Toast.makeText(activity, "Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     ### restart: startet die Maschine neu
     - user: User

     JSON-Beispiel:

     {"cmd": "restart", "user": 0}
     */
    public static void restart(Activity activity){
        //TO DO: restart
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
        //TO DO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * TO DO: find usage
     *
     * to be called if cocktail done and new Cocktail should be mixed
     * reset: reset the machine so that it can make a new cocktail
     * JSON-sample: {"cmd": "reset", "user": 9650}
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.

     * @author Johanna Reidt
     * @param activity
     */
    /*
    public static void reset(Activity activity){
        //TO DO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

     */

}

