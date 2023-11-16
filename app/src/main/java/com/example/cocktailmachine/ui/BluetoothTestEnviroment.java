package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.Postexecute;

import org.json.JSONException;

public class BluetoothTestEnviroment extends AppCompatActivity {

    EditText editText;
    TextView textView;


    // Step1
    BluetoothSingleton singleton = BluetoothSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_enviroment);
        //initializieurng von Datenbank

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        // Step2: Only at the very first time Connect to Device
        //singleton.connectGatt(BluetoothTestEnviroment.this);

    }

    public void addUser(View view) throws JSONException,
            InterruptedException, NotInitializedDBException {
        String user = editText.getText().toString();

        // Automatische Kalibrierung
        //singleton.adminAutoCalibrateStart(BluetoothTestEnviroment.this);
        //singleton.adminAutoCalibrateAddEmpty(BluetoothTestEnviroment.this);
        //singleton.adminAutoCalibrateFinish(BluetoothTestEnviroment.this);

        // Get Current Status
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cocktailmaschinenstatus");
        builder.setMessage(CocktailStatus.getCurrentStatusMessage(new Postexecute() {
            @Override
            public void post() {
                builder.setMessage(CocktailStatus.getCurrentStatus().toString());
                builder.setNeutralButton("Fertig!", (dialog, which) -> {});
                builder.show();
            }
        },this));


         */
        // Waage Kalibrierung
        //singleton.adminManuelCalibrateTareScale(BluetoothTestEnviroment.this);
        //singleton.adminManuelCalibrateScale(100,BluetoothTestEnviroment.this);

        // Define Pumpen
        //singleton.adminDefinePump("water",1000,1,this);
        //singleton.adminDefinePump("water",2000,2,this);

        // manuelCalibration
       // singleton.adminManuelCalibrateRunPump(1,10000,
        //        BluetoothTestEnviroment.this);
      //  singleton.adminManuelCalibrateRunPump(1,20000,
      //          BluetoothTestEnviroment.this);
       // singleton.adminManuelCalibratePump(1,10000,20000,29.2, 87.5
        //,BluetoothTestEnviroment.this);
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PumpsStatus");
        singleton.adminReadPumpsStatus(new Postexecute() {
            @Override
            public void post() {
                try {
                    Pump.getPumpStatus();
                    builder.setMessage(Pump.getPumpStatus().toString());
                    builder.setNeutralButton("Fertig!", (dialog, which) -> {});
                    builder.show();
                }
              catch (NotInitializedDBException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },this);

         */
        //TODO: Johana/Amir getCurrentCocktailCocktail liefert null Object zurück
/*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PumpsStatus");
        CocktailMachine.getCurrentCocktailStatus(new Postexecute() {
            @Override
            public void post() {
                //Pump.getPumpStatus();
                builder.setMessage(CocktailMachine.current.toString());
                builder.setNeutralButton("Fertig!", (dialog, which) -> {});
                builder.show();
            }
        },BluetoothTestEnviroment.this);*/

        /*
        singleton.adminReadCurrentCocktail(new Postexecute() {
            @Override
            public void post() {
                try {
                    //Pump.getPumpStatus();
                    builder.setMessage(CocktailMachine.getCurrentCocktailStatus(
                            BluetoothTestEnviroment.this).toString());
                    builder.setNeutralButton("Fertig!", (dialog, which) -> {});
                    builder.show();
                }
                catch (NotInitializedDBException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },BluetoothTestEnviroment.this);



         */

/*
CocktailMachine.getCurrentCocktailStatus(new Postexecute() {
            @Override
            public void post() {
                //LinkedHashMap<Ingredient, Integer> current=
                //        CocktailMachine.geterCurrentCocktailStatus();
                //System.out.println(current.get(0).toString());

            }
        },BluetoothTestEnviroment.this);


        //singleton.adminReset(BluetoothTestEnviroment.this);

 */
        //singleton.adminRefillPump(3000,1,BluetoothTestEnviroment.this);
        //singleton.adminManuelCalibrateSetPumpTimes(1,1000,1000,1.0,
        //        BluetoothTestEnviroment.this);

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //singleton.userInitUser("amir", BluetoothTestEnviroment.this);
        singleton.adminReadPumpsStatus(BluetoothTestEnviroment.this);
        //singleton.adminDefinePumps(BluetoothTestEnviroment.this,"Water",100,2);
        //singleton.adminReadLiquidsStatus(BluetoothTestEnviroment.this);


    }

    public void showUser(View view) throws JSONException {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step7: Unbind Service if the Activity is destroyed
        //singleton.mBluetoothLeService.disconnect();
        //unbindService(singleton.mServiceConnection);
        //singleton.mBluetoothLeService = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //singleton.unRegisterService(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // Step8: make sure that Gatt Server is founded
        //singleton.registerReceiver(this);


    }
}