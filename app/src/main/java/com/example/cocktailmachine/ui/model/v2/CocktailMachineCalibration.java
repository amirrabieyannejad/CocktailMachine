package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.widget.Toast;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.enums.AdminRights;

import java.util.Random;

/**
 * @author Johanna Reidt
 * @created Do. 20.Jul 2023 - 14:36
 * @project CocktailMachine
 */
public class CocktailMachineCalibration {
    public void start(Activity activity){
        AdminRights.initUser(activity, String.valueOf(new Random().nextInt()));
        if(CocktailMachine.isCocktailMachineSet(activity)){
            Toast.makeText(activity, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
            return;
        }
        GetDialog.setPumpNumber(activity);
        Pump.calibratePumps(activity);
    }

}
