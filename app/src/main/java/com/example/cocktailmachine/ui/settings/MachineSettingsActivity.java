package com.example.cocktailmachine.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.enums.Status;

/**
 *
 * @created Fr. 23.Jun 2023 - 16:16
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class MachineSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_settings);
        //TODO: bind bluetooth

    }

    //Calibration
    /**
     * go to pump settings
     * @param view
     * @author Johanna Reidt
     */
    public void calibratePump(View view) {
        //TO DO Go To Pump Settings
        Toast.makeText(this,"calibratePump",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, PumpSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * go to scale settings
     * @param view
     * @author Johanna Reidt
     */
    public void calibrateScale(View view) {
        //TO DO: open calibration Scale settings
        Toast.makeText(this,"calibrateScale",Toast.LENGTH_SHORT).show();
        //Pump.calibrate(this);

        Intent intent = new Intent(this, ScaleSettingsActivity.class);
        startActivity(intent);
    }



    //STATS
    /**
     * Displays an Dialog View with current status of cocktail maschine
     * @param view
     * @author Johanna Reidt
     */
    public void status(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cocktailmaschinenstatus");
        builder.setMessage(Status.getCurrentStatusMessage(this));
        builder.setNeutralButton("Fertig!", (dialog, which) -> {});
        builder.show();
    }

    /**
     * clean machine
     * @param view
     * @author Johanna Reidt
     */
    public void clean(View view) {
        Pump.clean(this);
        Toast.makeText(this,"Reinigung gestartet!",Toast.LENGTH_SHORT).show();
    }


    //New Start
    /**
     * ### restart: startet die Maschine neu
     * - user: User
     *
     * JSON-Beispiel:
     *
     *     {"cmd": "restart", "user": 0}
     * @param view
     * @author Johanna Reidt
     */
    public void restart(View view) {
        CocktailMachine.restart(this);
    }

    /**
     * ### factory_reset: setzt alle Einstellungen zurück
     * - user: User
     *
     * JSON-Beispiel:
     *
     *     {"cmd": "factory_reset", "user": 0}
     * @param view
     * @author Johanna Reidt
     */
    public void factoryReset(View view) {
        CocktailMachine.factoryReset(this);
    }

}