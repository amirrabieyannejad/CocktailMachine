package com.example.cocktailmachine.ui.calibration.scale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;

public class calibrationScale extends AppCompatActivity {
    private BluetoothSingleton blSingelton = BluetoothSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_scale);
        blSingelton.connectGatt(this);
        try {
            blSingelton.adminReadState();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}