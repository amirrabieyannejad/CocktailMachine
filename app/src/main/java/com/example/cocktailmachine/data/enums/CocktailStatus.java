package com.example.cocktailmachine.data.enums;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public enum CocktailStatus {
    //TO DO: USE THIS AMIR
    /*
    - `init`: Maschine wird initialisiert
- `ready`: Maschine ist bereit einen Befehl auszuführen und wartet
- `mixing`: Maschine macht einen Cocktail
- `pumping`: Maschine pumpt Flüssigkeiten
- `cocktail done`: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte `reset` ausgeführt werden.
     */
    init, ready, mixing, pumping, cocktail_done, not;

    private static final String TAG = "CocktailStatus";
    static CocktailStatus currentState = CocktailStatus.not;

    @NonNull
    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }

    /**
     * return current Status
     *
     * @return
     */
    public static CocktailStatus getCurrentStatus(Postexecute postexecute,Activity activity) {
        //BluetoothSingleton blSingelton = BluetoothSingleton.getInstance();
        //blSingelton.connectGatt(activity);
        //TO DO: Bluetoothlegatt
        //BluetoothSingleton.getInstance().mBluetoothLeService;
        //TO DO: AMIR
        if(Dummy.isDummy){
            Log.i(TAG, "getCurrentStatus: dummy state: "+ CocktailStatus.currentState);
            postexecute.post();
            return CocktailStatus.currentState;
        }
        try {
            BluetoothSingleton.getInstance().adminReadState(activity, postexecute);
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.e(TAG, "getCurrentStatus");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            Log.e(TAG, "error", e);
        }
        return currentState;
    }

    /**
     * return current Status, from last fetch
     *
     * @return
     */
    public static CocktailStatus getCurrentStatus() {
        //BluetoothSingleton blSingelton = BluetoothSingleton.getInstance();
        //blSingelton.connectGatt(activity);
        //TO DO: Bluetoothlegatt
        //BluetoothSingleton.getInstance().mBluetoothLeService;
        //TO DO: AMIR
        return currentState;
    }
    /**
     * return current Status
     *
     * @return
     */
    public static CocktailStatus getCurrentStatus(Activity activity) {
        //BluetoothSingleton blSingelton = BluetoothSingleton.getInstance();
        //blSingelton.connectGatt(activity);
        //TO DO: Bluetoothlegatt
        //BluetoothSingleton.getInstance().mBluetoothLeService;
        //TO DO: AMIR
        if(Dummy.isDummy){
            return getCurrentStatus();
        }
        getCurrentStatus(new Postexecute() {
            @Override
            public void post() {}
        }, activity);

        return currentState;
    }

    /**
     * return current Status
     *
     * @return
     */
    public static String getCurrentStatusMessage() {
        //CocktailStatus status = getCurrentStatus(postexecute,activity);
        if(Dummy.isDummy){
            return "im VM-Machine-Modus";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Die Cocktailmaschine ");
        switch (CocktailStatus.getCurrentStatus()) {
            case init:
                builder.append("wird gerade initialisiert.");
                return builder.toString();
            case ready:
                builder.append("ist bereit Cocktails zu mischen.");
                return builder.toString();
            case mixing:
                builder.append("erstellt gerade einen Cocktail.");
                return builder.toString();
            case pumping:
                builder.append("pumpt gerade Flüssigkeiten.");
                return builder.toString();
            case cocktail_done:
                builder.append("hat einen fertigen Cocktail, der zur Abholung bereit steht.");
                return builder.toString();
                //return CalibrateStatus.
        }
        return "Error";
    }

    public  static void setStatus(JSONObject jsonObject) {
        //TO DO: figure out if only string or JSON Object and put in currentStatus
        setStatus(jsonObject.toString());
    }

    public  static void setStatus(String status) {
        //TO DO: figure out if only string or JSON Object and put in currentStatus
        status = status.replace("\"", "");
        status = status.replace("'", "");
        status = status.replace("_", " ");
        try {
            currentState = CocktailStatus.valueOf(status);
        }catch (IllegalArgumentException e){
            currentState = CocktailStatus.not;
        }

    }

    public static void setStatus(CocktailStatus status){
        currentState = status;
    }


}
