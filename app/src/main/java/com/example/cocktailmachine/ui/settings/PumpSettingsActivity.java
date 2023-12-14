package com.example.cocktailmachine.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cocktailmachine.R;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

/**
 * Pumpenkalibrierung und Pumpenzeit
 * @created Fr. 23.Jun 2023 - 16:16
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class PumpSettingsActivity extends AppCompatActivity {

    private static final String TAG = "PumpSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump_settings);


        //TODO: bind bluetooth

    }


    /**
     * ### calibrate_pump: kalibriert die Pumpe mit vorhandenen Messwerten
     *      - user: User
     *      - slot: int
     *      - time1: int
     *      - time2: int
     *      - volume1: float
     *      - volume2: float
     *      Zur Kalibrierung müssen zwei Messwerte vorliegen, bei denen die Pumpe für eine unterschiedliche Zeit gelaufen ist. Daraus wird dann der Vorlauf und die Pumprate berechnet.
     *      Die Zeiten werden in Millisekunden und die Flüssigkeiten in Milliliter angegeben.
     *      JSON-Beispiel:
     *      {"cmd": "calibrate_pump", "user": 0, "slot": 1, "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}
     * @author Johanna Reidt
     * @param view
     */
    public void calibratePump(View view){
        Log.v(TAG, "calibratePump");

        //TO DO: CocktailMachine.calibratePump(this);
        Pump.calibratePumpsAndTimes(this);
    }


    /**
     *  ### set_pump_times: setzt die Kalibrierungswerte für eine Pumpe
     *      - user: User
     *      - slot: int
     *      - time_init: int
     *      - time_reverse: int
     *      - rate: float
     *      `time_init` ist die Vorlaufzeit und `time_init` die Rücklaufzeit in Millisekunden. Normalerweise sollten diese Werte ähnlich oder gleich sein. Die Rate wird in mL/ms angegeben.
     *      JSON-Beispiel:
     *      {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000, "rate": 1.0}
     *
     * @author Johanna Reidt
     * @param view
     */
    public void setPumpTimes(View view){
        Log.v(TAG, "setPumpTimes");

        //open alert dialog pick pump
        //Pump pump = Pump.getPump(0);
        //pump.sendPumpTimes(this, 0,0,0);
        GetDialog.calibrateAllPumpTimes(this);

    }


    public void automatic(View view){
        Log.v(TAG, "automatic");

        //open alert dialog pick pump
        //Pump pump = Pump.getPump(0);
        //pump.sendPumpTimes(this, 0,0,0);
        //GetDialog.startAutomaticCalibration(this);

        GetActivity.waitNotSet(this);

    }
}