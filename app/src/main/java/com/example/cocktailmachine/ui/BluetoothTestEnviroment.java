package com.example.cocktailmachine.ui;

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
import com.example.cocktailmachine.data.db.DatabaseConnection;

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
        //DatabaseConnection.initializeSingleton(this);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        // Step2: Only at the very first time Connect to Device
        //singleton.connectGatt(BluetoothTestEnviroment.this);

    }

    public void addUser(View view) throws JSONException, InterruptedException {
        String user = editText.getText().toString();
        //singleton.adminAutoCalibrateStart(BluetoothTestEnviroment.this);
        //singleton.adminAutoCalibrateAddEmpty(BluetoothTestEnviroment.this);
        singleton.adminAutoCalibrateFinish(BluetoothTestEnviroment.this);


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