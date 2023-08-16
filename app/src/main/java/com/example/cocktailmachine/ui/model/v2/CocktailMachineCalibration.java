package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.enums.AdminRights;

import org.json.JSONException;

import java.util.Random;

/**
 * @author Johanna Reidt
 * @created Do. 20.Jul 2023 - 14:36
 * @project CocktailMachine
 */
public class CocktailMachineCalibration {
    public static void start(Activity activity) throws JSONException, InterruptedException {
        AdminRights.login(activity, activity.getLayoutInflater(), dialog -> {});
        AdminRights.initUser(activity, String.valueOf(new Random().nextInt()));
        if(CocktailMachine.isCocktailMachineSet(activity)){
            Log.i("CocktailMachineCalibr", "isset");
            Toast.makeText(activity, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
            return;
        }
        GetDialog.setPumpNumber(activity);
        //Pump.calibratePumpsAndTimes(activity);
        for(Pump p: Pump.getPumps()){
            GetDialog.setPumpIngredient(activity, p, true, true);
        }
    }

}
