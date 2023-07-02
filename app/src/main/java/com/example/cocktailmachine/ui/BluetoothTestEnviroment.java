package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothDeviceAdressNotDefinedException;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothLeService;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;

public class BluetoothTestEnviroment extends AppCompatActivity {

    EditText editText;
    TextView textView;


    // Step1
    BluetoothSingleton singleton=BluetoothSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_enviroment);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        // Step2: Only at the very first time Connect to Device
        singleton.connectGatt(BluetoothTestEnviroment.this);


    }
    public void addUser(View view) throws JSONException, InterruptedException {

        //Step5: Call initUser() Method
        String user = editText.getText().toString();
        //singleton.initUser(user,textView,BluetoothTestEnviroment.this);
        //TODO: als Rückgabe JSON Datei

        singleton.initUser(user,textView,BluetoothTestEnviroment.this);
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
        singleton.unRegisterService(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // Step8: make sure that Gatt Server is founded
        singleton.registerService(this);

    }
}