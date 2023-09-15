package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Topic;
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
    private static boolean isDone = true;

    public static void start(Activity activity) {
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

    public static boolean isIsDone() {
        return isDone;
    }

    public static void setIsDone(boolean isDone) {
        CocktailMachineCalibration.isDone = isDone;
    }
}
