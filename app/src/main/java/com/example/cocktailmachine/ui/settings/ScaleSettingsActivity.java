package com.example.cocktailmachine.ui.settings;

import android.content.Context;
import android.os.Bundle;

import com.example.cocktailmachine.data.CocktailMachine;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cocktailmachine.databinding.ActivityScaleSettingsBinding;

import com.example.cocktailmachine.R;

/**
 *
 * @created Fr. 23.Jun 2023 - 16:18
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class ScaleSettingsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityScaleSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScaleSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //TODO: bind bluetooth

    }


    /**
     *     ### tare_scale: tariert die Waage
     *      - user: User
     *      JSON-Beispiel:
     *      {"cmd": "tare_scale", "user": 0}
     * @author Johanna Reidt
     * @param view
     */
    public void tareScale(View view){
        CocktailMachine.tareScale(this);
    }


    /**
     * ### calibrate_scale: kalibriert die Waage
     *      - user: User
     *      - weight: float
     *      Das Gewicht wird in Milligramm angegeben.
     *      JSON-Beispiel:
     *      {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     * @author Johanna Reidt
     * @param view
     */
    public void calibrateScale(View view){

        CocktailMachine.calibrateScale(this, 0);


    }


    /**
     *      ### set_scale_factor: setzt den Kalibrierungswert für die Waage
     *      - user: User
     *      - factor: float
     *      JSON-Beispiel:
     *      {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}
     * @author Johanna Reidt
     * @param view
     */
    public void setScaleFactor(View view){
        CocktailMachine.setScaleFactor(this);
    }

}