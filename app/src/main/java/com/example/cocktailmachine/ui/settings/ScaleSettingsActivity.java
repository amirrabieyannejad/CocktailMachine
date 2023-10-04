package com.example.cocktailmachine.ui.settings;

import android.os.Bundle;

import com.example.cocktailmachine.data.CocktailMachine;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.cocktailmachine.databinding.ActivityScaleSettingsBinding;

/**
 *
 * @created Fr. 23.Jun 2023 - 16:18
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class ScaleSettingsActivity extends AppCompatActivity {

    //private AppBarConfiguration appBarConfiguration;
    private ActivityScaleSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScaleSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //TO DO: bind bluetooth

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

        CocktailMachine.calibrateScale(this);
        //CocktailMachine.tareScale(this);
        //CocktailMachine.scaleFactor(this);


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
        CocktailMachine.scaleFactor(this);
    }

}