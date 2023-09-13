package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.CocktailStatus;
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
        DatabaseConnection.initializeSingleton(this);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        // Step2: Only at the very first time Connect to Device
        //singleton.connectGatt(BluetoothTestEnviroment.this);

    }

    public void addUser(View view) throws JSONException, InterruptedException, NotInitializedDBException {
        String user = editText.getText().toString();
        //singleton.adminAutoCalibrateStart(BluetoothTestEnviroment.this);
        //singleton.adminAutoCalibrateAddEmpty(BluetoothTestEnviroment.this);
        //singleton.adminAutoCalibrateFinish(BluetoothTestEnviroment.this);
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
        //singleton.adminManuelCalibrateTareScale(BluetoothTestEnviroment.this);

        //singleton.adminManuelCalibrateScale(100,BluetoothTestEnviroment.this);
        //singleton.adminDefinePump("wein",1000,1,this);
        //singleton.adminDefinePump("beer",2000,2,this);
        // manuelCalibration
        //singleton.adminManuelCalibrateRunPump(1,10000,
          //      BluetoothTestEnviroment.this);
       // singleton.adminManuelCalibrateRunPump(1,20000,
         //       BluetoothTestEnviroment.this);
        //singleton.adminManuelCalibratePump(1,10000,20000,);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PumpsStatus");
        singleton.adminReadPumpsStatus(new Postexecute() {
            @Override
            public void post() {
                try {
                    Pump.getPumpStatus();
                }
              catch (NotInitializedDBException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },this);






        // empty buttle should be placed- in GUI we should ask to put empty buttle  wait for ok
        //singleton.adminAutoCalibrateAddEmpty();
        //singleton.adminReadState();
        //Status wert gespeichert auf DB
        //durch while warten auf "calibration known weight"
        //
        // GUI put 100ml water in glass. we should ask to fill empty buttle with 100ml water wait for ok
        //singleton.adminAutoCalibrateAddWeight();
        //singleton.adminReadState();
        //Status wert gespeichert auf DB
        //durch while warten auf "calibration emtpty container"

        // we should wait empty buttle and User should tip ok
        //singleton.adminAutoCalibrateAddEmpty();
        //singleton.adminReadState();

        //singleton.adminDefinePump("beer", 1000,1);
        //singleton.adminReadCurrentUser();
        Log.w("Activity", "bluetoothTestEnvironment: is everything in a right place??");
        //textView.setText(json.toString());
        //singleton.connectGatt(BluetoothTestEnviroment.this);
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