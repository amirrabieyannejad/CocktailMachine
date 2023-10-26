package com.example.cocktailmachine.ui.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

/**
 *
 * @created Fr. 23.Jun 2023 - 16:16
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class MachineSettingsActivity extends AppCompatActivity {
    private static final String TAG = "MachineSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_machine_settings);

        //TO DO: bind bluetooth


    }

    //Calibration
    /**
     * go to pump settings
     * @param view
     * @author Johanna Reidt
     */
    public void calibratePump(View view) {
        Log.v(TAG, "calibratePump");
        //TO DO Go To Pump Settings
        //Toast.makeText(this,"calibratePump",Toast.LENGTH_SHORT).show();

        //TO DO: Pump.calibrate
        //Pump.calibratePumpsAndTimes(this);
        Intent intent = new Intent(this, PumpSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * go to scale settings
     * @param view
     * @author Johanna Reidt
     */
    public void calibrateScale(View view) {
        Log.v(TAG, "calibrateScale");
        //TO DO: open calibration Scale settings
        //Toast.makeText(this,"calibrateScale",Toast.LENGTH_SHORT).show();
        //CocktailMachine.calibrateScale(this);

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
        Log.v(TAG, "status");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Es lädt ...");
        CocktailStatus.getCurrentStatus(new Postexecute() {
            @Override
            public void post() {
                builder.setTitle("Cocktailmaschinenstatus");
                builder.setMessage(
                        CocktailStatus.getCurrentStatusMessage());
            }
        }, MachineSettingsActivity.this);
        builder.setNeutralButton("Fertig!", (dialog, which) -> {});
        builder.show();
    }

    /**
     * clean machine
     * @param view
     * @author Johanna Reidt
     */
    public void clean(View view) {
        Log.v(TAG, "clean");
        CocktailMachine.clean(this);
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
        Log.v(TAG, "restart");
        CocktailMachine.restart(this);
        Toast.makeText(this,"Die Cocktailmaschine wurde wieder gestartet!",Toast.LENGTH_SHORT).show();
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
        Log.v(TAG, "factoryReset");
        CocktailMachine.factoryReset(this);
        AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.User);
        Toast.makeText(this,"Die Cocktailmaschine wurde geresettet!",Toast.LENGTH_SHORT).show();
    }

}