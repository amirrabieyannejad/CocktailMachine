package com.example.cocktailmachine.data.enums;

import android.util.Log;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;

public enum CalibrateStatus {
    /**
     * Kalibrierung
     *
     *     ready: Maschine ist bereit für eine Kalibrierung
     *     calibration empty container: Kalibrierung wartet auf ein leeres Gefäß. Es sollte calibration_add_empty ausgeführt werden.
     *     calibration known weight: Kalibrierung wartet auf ein Gewicht. Es sollte calibration_add_weight ausgeführt werden.
     *     calibration pumps: Kalibrierung pumpt Flüssigkeiten
     *     calibration calculation: Kalibrierung berechnet die Werte
     *     calibration done: Kalibrierung fertig. Es sollte calibration_finish ausgeführt werden.
     */
    not,
    ready,
    calibration_empty_container,
    calibration_known_weight,
    calibration_pumps,
    calibration_calculation,
    calibration_done
    ;

    private static final String TAG = "CalibrateStatus";
    private static CalibrateStatus status;


    public static void setStatus(String result) {
        status = valueStringOf(result);
    }
    public static void setStatus(CalibrateStatus result) {
        status = result;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case ready:return "ready";
            case calibration_empty_container:return "calibration empty container";
            case calibration_known_weight:return "calibration known weight";
            case calibration_pumps:return "calibration pumps";
            case calibration_calculation:return "calibration calculation";
            case calibration_done:return "calibration done";
        }
        return "not";
    }

    public static CalibrateStatus valueStringOf(String value) {
        switch (value) {
            case "ready":
                return ready;
            case "calibration empty container":
                return calibration_empty_container;
            case "calibration known weight":
                return calibration_known_weight;
            case "calibration pumps":
                return calibration_pumps;
            case "calibration calculation":
                return calibration_calculation;
            case "calibration done":
                return calibration_done;
        }
        return not;

    }




    public static CalibrateStatus getCurrent(Activity activity){
        if(!Dummy.isDummy){
            CocktailStatus.getCurrentStatus(activity);
        }
        Log.i(TAG, "getCurrent: "+status);
        return status;
    }

    public static CalibrateStatus getCurrent(Postexecute postexecute, Activity activity){
        if(!Dummy.isDummy) {
            CocktailStatus.getCurrentStatus(postexecute,activity);
        }else{
            postexecute.post();
        }
        return status;
    }
}
