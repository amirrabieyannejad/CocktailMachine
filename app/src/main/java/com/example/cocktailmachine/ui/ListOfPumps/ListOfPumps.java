package com.example.cocktailmachine.ui.ListOfPumps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.ListOfIngredience.RecyclerAdapterListIngredience;

import java.util.List;

public class ListOfPumps extends AppCompatActivity implements RecyclerViewListenerListPumps {

    private List<Pump> listPumps;
    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_pumps);

        this.context = this;
        this.listPumps = Pump.getPumps(this);

        //Einrichtung des RecyclerView
        recyclerView = findViewById(R.id.recyclerViewActivityListOfPumps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapterListPumps adapterComments = new RecyclerAdapterListPumps(this.listPumps,this.context);
        recyclerView.setAdapter(adapterComments);
    }

    @Override
    public void selectedPump(Pump pump) {
        Toast.makeText(this,pump.getIngredientName(),Toast.LENGTH_LONG).show();
    }
}