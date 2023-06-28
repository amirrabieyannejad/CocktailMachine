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
    BluetoothSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_enviroment);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        // Step2
        singleton = BluetoothSingleton.getInstance();
        // Step3: request Ble Permissions

        //Step4: bind Service
        singleton.bindService(this);
    }
    public void addUser(View view) throws JSONException, InterruptedException {

        //Step5: Call initUser() Method
        String user = editText.getText().toString();
        singleton.initUser(user);

        //Step6: Wait 6 Second to return the value from ESP
/*        Handler handler = new Handler();
        handler.postDelayed(() -> textView.setText
                (singleton.getEspResponseValue()), 6000);*/
        textView.setText(singleton.getEspResponseValue());
    }
    public void showUser(View view) throws JSONException {

        //Step9: Call adminReadCurrentUser() Method
        singleton.mBluetoothLeService.adminReadCurrentUser();

        //Step6: Wait 6 Second to return the value from ESP
        textView.setText
                (singleton.getEspResponseValue());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step7: Unbind Service if the Activity is destroyed
        unbindService(singleton.mServiceConnection);
        singleton.mBluetoothLeService = null;
    }
    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // Step8: make sure that Gatt Server is founded
        if (singleton.mBluetoothLeService != null) {
            final boolean result = singleton.mBluetoothLeService.connect(
                    singleton.getEspDeviceAddress());
            Log.d("OnResume", "Connect request result=" + result);

        }
    }
}