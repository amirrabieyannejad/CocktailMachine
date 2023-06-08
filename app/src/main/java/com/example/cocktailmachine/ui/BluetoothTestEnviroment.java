package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothDeviceAdressNotDefinedException;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothLeService;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingelton;

import org.json.JSONException;

public class BluetoothTestEnviroment extends AppCompatActivity {

    EditText editText;
    TextView textView;

    BluetoothSingelton singelton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test_enviroment);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.ESPData);

        singelton = BluetoothSingelton.getInstance();
    }

    public void addUser(View view) throws BluetoothDeviceAdressNotDefinedException, JSONException {
        BluetoothLeService bluetooth =  singelton.getBluetoothLe();
        bluetooth.initUser(editText.getText().toString());














        BluetoothGattCharacteristic mGattCharacteristics = bluetooth.getBluetoothGattCharacteristic(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);

        bluetooth.readCharacteristic(mGattCharacteristics);
        String text =mGattCharacteristics.getStringValue(0);
        Toast.makeText(this, text,Toast.LENGTH_LONG).show();



    }
}