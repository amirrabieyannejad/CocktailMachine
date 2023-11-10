package com.example.cocktailmachine.ui.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.Pump;

public class SinglePumpSetting extends AppCompatActivity {

    private Pump pump;
    TextView textViewHeader;
    TextView textViewIngredient;
    TextView textViewIngredientVolumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pump_setting);

        pump = Pump.getPumps(this).get(0);
        Toast.makeText(this, ""+pump.getIngredientName(this), Toast.LENGTH_SHORT).show();
        System.out.println("Test");

        this.textViewHeader = findViewById(R.id.textView_SinglePumpSetting_header);
        this.textViewIngredient = findViewById(R.id.textView_SinglePumpSetting_zutat);
        this.textViewIngredientVolumen = findViewById(R.id.textView_SinglePumpSetting_menge);

        this.textViewHeader.setText("Pumpe " + pump.getSlot());
        this.textViewIngredient.setText(pump.getIngredientName());
        this.textViewIngredientVolumen.setText(""+pump.getVolume(this));

        //pump.sendRefill(this);
        //BluetoothSingleton.getInstance().



    }
}