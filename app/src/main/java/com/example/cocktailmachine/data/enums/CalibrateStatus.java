package com.example.cocktailmachine.data.enums;

import android.util.Log;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.Dummy;

public enum CalibrateStatus {
    /**
     * Kalibrierung
     * <p>
     * ready: Maschine ist bereit für eine Kalibrierung
     * calibration empty container: Kalibrierung wartet auf ein leeres Gefäß. Es sollte calibration_add_empty ausgeführt werden.
     * calibration known weight: Kalibrierung wartet auf ein Gewicht. Es sollte calibration_add_weight ausgeführt werden.
     * calibration pumps: Kalibrierung pumpt Flüssigkeiten
     * calibration calculation: Kalibrierung berechnet die Werte
     * calibration done: Kalibrierung fertig. Es sollte calibration_finish ausgeführt werden.
     */
    not,
    ready,
    calibration_empty_container,
    calibration_known_weight,
    calibration_pumps,
    calibration_calculation,
    calibration_done;

    private static final String TAG = "CalibrateStatus";
    private static CalibrateStatus status;


    public static void setStatus(String result) {
        Log.i(TAG,"setStatus: res: "+result );

        if(result == null){
            status = CalibrateStatus.not;
            Log.i(TAG,"setStatus: status: "+status );
            return;
        }
        //TO DO: figure out if only string or JSON Object and put in currentStatus
        result = result.replace("\"", "");
        result = result.replace("'", "");
        result = result.replace(" ", "_");
        status = valueStringOf(result);
        Log.i(TAG,"setStatus: status: "+status );
    }

    public static void setStatus(CalibrateStatus result) {
        status = result;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString().replace("_", " ");
        /*
        switch (this) {
            case ready:return "ready";
            case calibration_empty_container:return "calibration empty container";
            case calibration_known_weight:return "calibration known weight";
            case calibration_pumps:return "calibration pumps";
            case calibration_calculation:return "calibration calculation";
            case calibration_done:return "calibration done";
        }
        return "not";

         */
    }

    public static CalibrateStatus valueStringOf(String value) {
        if(value == null){
            return CalibrateStatus.not;
        }

        Log.i(TAG, "valueStringOf");
        String res = value.replace(" ", "_");
        //return super(res);
        try {
            return CalibrateStatus.valueOf(res);
        }catch (IllegalArgumentException e){
            //Log.i(TAG, "valueStringOf IllegalArgumentException");
            Log.e(TAG, "valueStringOf", e);
            //Log.getStackTraceString(e);
            return CalibrateStatus.not;
        }
        /*
        switch (res) {
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

         */

    }

    public static CalibrateStatus getCurrent(){
        Log.i(TAG, "getCurrent: "+status);
        return status;
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
            if(postexecute != null) {
                postexecute.post();
            }
        }
        return status;
    }

    public static boolean isReady(){
        return status.compareTo(CalibrateStatus.ready)==0;
    }
}
