package com.example.cocktailmachine.ui.model.helper;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;

import org.json.JSONException;

import java.util.Random;

/**
 * @author Johanna Reidt
 * @created Do. 20.Jul 2023 - 14:36
 * @project CocktailMachine
 */
public class CocktailMachineCalibration {
    private static final String TAG = "CocktailMachineCalibr" ;
    private static boolean isDone = false;

    public static void start(Activity activity) {
        Log.v(TAG, "start");
        Dialog wait = GetDialog.loadingBluetooth(activity);
        wait.show();
        ExtraHandlingDB.loadForSetUp(activity);
        Log.v(TAG, "start: loaded db");
        wait.cancel();
        AdminRights.login(activity, activity.getLayoutInflater(), dialog -> {
            Log.v(TAG, "start: login canceling");

            Dialog wait_blue = GetDialog.loadingBluetooth(activity);
            wait_blue.show();
            AdminRights.initUser(activity, String.valueOf(new Random().nextInt()), new Postexecute() {
                @Override
                public void post() {
                    Log.v(TAG, "start: login init");
                    if(CocktailMachine.isCocktailMachineSet(activity)){
                        Log.v(TAG, "start: login isCocktailMachineSet");
                        isDone = true;
                        Log.v(CocktailMachineCalibration.TAG, "is set");
                        Toast.makeText(activity, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GetActivity.waitNotSet(activity);
                    /*
                    if(AdminRights.isAdmin()){
                        Log.v(TAG, "start: is admin");
                        wait_blue.cancel();
                        GetDialog.startAutomaticCalibration(activity);
                    }else{
                        Log.v(TAG, "start: is user");
                        wait_blue.cancel();
                    }
                     */
                }
            });
            //Pump.calibratePumpsAndTimes(activity);
            /*
            for(Pump p: Pump.getPumps()){
                GetDialog.setPumpIngredient(activity, p, true, true);
            }
             */

        });

    }

    /**
     * true if pumps in cocktailmachine calibrated and ready to mix
     * @author Johanna Reidt
     * @return
     */
    public static boolean isIsDone() {
        Log.v(TAG, "start: isIsDone");
        return isDone;
    }


    public static void askIsDone(Activity activity, Postexecute postexecute){
        Dialog wait = GetDialog.loadingBluetooth(activity);
        wait.show();

        if(Dummy.isDummy){
            wait.cancel();
            postexecute.post();
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, new Postexecute() {
                @Override
                public void post() {
                    wait.cancel();
                    postexecute.post();
                }
            });
        } catch (JSONException | InterruptedException e) {
            //throw new RuntimeException(e);
            GetDialog.errorStatus(activity, e);
        };
    }

    public static void setIsDone(boolean isDone) {
        Log.v(TAG, "start: setIsDone");
        CocktailMachineCalibration.isDone = isDone;
        if(isDone){
            Dummy.withSetCalibration = true;
        }
        Log.v(TAG, "start: isDone: "+isDone);
    }


}
