package com.example.cocktailmachine.ui.manualtestingsuit.calibration.scale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;

import org.json.JSONException;

public class calibrationScale extends AppCompatActivity {

    //TODO: wahrscheinlich löschbar
    private final BluetoothSingleton blSingelton = BluetoothSingleton.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_scale);
        blSingelton.connectGatt(this);
        try {
            blSingelton.adminReadState(this, new Postexecute() {
                @Override
                public void post() {
                    System.out.println("Wurde gelesen");
                }
            });
        } catch (JSONException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Die Nachricht ist");
        System.out.println("Der momentane Status ist" + CocktailStatus.getCurrentStatus( new Postexecute() {
            @Override
            public void post() {
                System.out.println("dkjflkjldsök");
            }
        },this));
        CocktailStatus.getCurrentStatus( new Postexecute() {
            @Override
            public void post() {
                System.out.println("Der momentane Status ist " +CocktailStatus.getCurrentStatus()+".");
            }
        },this);
    }
}