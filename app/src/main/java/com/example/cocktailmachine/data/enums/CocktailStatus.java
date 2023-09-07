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
    init, ready, mixing, pumping, cocktail_done,not
    ;

    private static final String TAG = "CocktailStatus";
    static CocktailStatus currentState = CocktailStatus.not;

    @NonNull
    @Override
    public String toString() {
        if (this.ordinal() == cocktail_done.ordinal()) {
            return "cocktail done";
        }
        return super.toString();
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
            BluetoothSingleton.getInstance().adminReadState(postexecute,activity);
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.e(TAG, "getCurrentStatus");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
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
        if(!Dummy.isDummy){
            getCurrentStatus(new Postexecute() {
                @Override
                public void post() {

                }
            }, activity);
        }
        return currentState;
    }

    /**
     * return current Status
     *
     * @return
     */
    public static String getCurrentStatusMessage(Postexecute postexecute,Activity activity) {
        CocktailStatus status = getCurrentStatus(postexecute,activity);
        StringBuilder builder = new StringBuilder();
        builder.append("Die Cocktailmaschine ");
        switch (status) {
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
        }
        return "Error";
    }

    public  static void setStatus(JSONObject jsonObject) {
        //TODO: figure out if only string or JSON Object and put in currentStatus
    }

    public  static void setStatus(String status) {
        //TODO: figure out if only string or JSON Object and put in currentStatus
        try {
            currentState = CocktailStatus.valueOf(status);
        }catch (IllegalArgumentException e){
            if(Objects.equals(status, "cocktail done")){
                currentState = CocktailStatus.cocktail_done;
            }else{
                currentState = CocktailStatus.ready;
            }

        }

    }


}
