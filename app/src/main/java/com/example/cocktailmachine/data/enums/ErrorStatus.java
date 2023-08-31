package com.example.cocktailmachine.data.enums;

import android.util.Log;

import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;

/**
 * @author Johanna Reidt
 * @created Do. 31.Aug 2023 - 14:35
 * @project CocktailMachine
 */
public class ErrorStatus {
    private static final String TAG = "ErrorStatus";
    static String error;

    public static void setError(String mesg){
        error = mesg;
    }

    public static String getError(){
        return error;
    }

    //Error
    public static String getErrorMessage(){
        Log.i(TAG, "getErrorMessage");
        try {
            BluetoothSingleton.getInstance().adminReadErrorStatus();
            return error;
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "getErrorMessage: errored");
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return "not";
    }
}
