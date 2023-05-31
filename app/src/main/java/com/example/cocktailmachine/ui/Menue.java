package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;

public class Menue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);
    }




    public void openSingelCocktailView(View view){
        Intent success = new Intent(this, SingleCocktailChoice.class);
        startActivity(success);

    }

    public void openGlassFillAnimationView(View view){
        Intent success = new Intent(this, FillAnimation.class);
        startActivity(success);

    }


    public void openGrafik(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }

    public void openDeviceScan(View view){
        Intent success = new Intent(this, DeviceScanActivity.class);
        startActivity(success);
    }

    public void exit(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }
}