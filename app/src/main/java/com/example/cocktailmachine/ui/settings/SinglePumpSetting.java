package com.example.cocktailmachine.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Pump;

public class SinglePumpSetting extends AppCompatActivity {

    private Pump pump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pump_setting);

        pump = Pump.getPumps(this).get(0);
        Toast.makeText(this, ""+pump.getIngredientName(this), Toast.LENGTH_SHORT).show();
        System.out.println("Test");
        pump.ge
    }
}