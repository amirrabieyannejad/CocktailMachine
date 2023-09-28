package com.example.cocktailmachine.ui.ListOfPumps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Pump;

public class ListOfPumps extends AppCompatActivity implements RecyclerViewListenerListPumps {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_pumps);
        //Pump.setOverrideEmptyPumps(this,5);
        Toast.makeText(this,Pump.getPumps(this).size(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void selectedPump(Pump pump) {
        Toast.makeText(this,pump.getIngredientName(),Toast.LENGTH_LONG).show();
    }
}