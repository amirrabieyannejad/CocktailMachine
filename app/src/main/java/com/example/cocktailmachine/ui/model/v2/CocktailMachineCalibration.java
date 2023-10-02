package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.db.Buffer;
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
        Buffer.loadForSetUp(activity);
        AdminRights.login(activity, activity.getLayoutInflater(), dialog -> {
            dialog.dismiss();
            try {
                AdminRights.initUser(activity, String.valueOf(new Random().nextInt()));
            } catch (JSONException | InterruptedException e) {
                //throw new RuntimeException(e);
                Log.e(CocktailMachineCalibration.TAG, e.toString());
                e.printStackTrace();
            }
            if(CocktailMachine.isCocktailMachineSet(activity)){
                isDone = true;
                Log.i(CocktailMachineCalibration.TAG, "is set");
                Toast.makeText(activity, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(AdminRights.isAdmin()) {
                GetDialog.startAutomaticCalibration(activity);
            }else{
                GetActivity.waitNotSet(activity);
            }
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
        return isDone;
    }

    public static void setIsDone(boolean isDone) {
        CocktailMachineCalibration.isDone = isDone;
    }
}
