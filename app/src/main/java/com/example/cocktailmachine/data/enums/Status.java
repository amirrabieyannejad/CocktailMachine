package com.example.cocktailmachine.data.enums;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

public enum Status {
    //TODO: USE THIS AMIR
    /*
    - `init`: Maschine wird initialisiert
- `ready`: Maschine ist bereit einen Befehl auszuführen und wartet
- `mixing`: Maschine macht einen Cocktail
- `pumping`: Maschine pumpt Flüssigkeiten
- `cocktail done`: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte `reset` ausgeführt werden.
     */
    init, ready, mixing, pumping, cocktail_done,
    ;

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
     * @param activity
     * @return
     */
    public static Status getCurrentStatus(Activity activity) {
        //TODO: Bluetoothlegatt
        //BluetoothSingleton.getInstance().mBluetoothLeService;
        //TODO: AMIR
        return Status.ready;
    }

    /**
     * return current Status
     *
     * @param activity
     * @return
     */
    public static String getCurrentStatusMessage(Activity activity) {
        Status status = getCurrentStatus(activity);
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

    }


}
