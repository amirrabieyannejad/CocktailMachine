package com.example.cocktailmachine.ui.model.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.ListOfPumps.RecyclerAdapterListPumps;
import com.example.cocktailmachine.ui.ListOfPumps.RecyclerViewListenerListPumps;

import java.util.List;

public class DialogListOfPumps implements RecyclerViewListenerListPumps {

    private Activity activity;
    private List<Pump> listPumps;

    private RecyclerView recyclerView;

    private AlertDialog dialog;
    public DialogListOfPumps(Activity activity){
        this.activity = activity;
        this.listPumps = Pump.getPumps(this.activity);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        View v = activity.getLayoutInflater().inflate(R.layout.activity_list_of_pumps, null);


        //Erzeuge Dialog und zeige diese an
        alertDialog.setView(v);
        this.dialog = alertDialog.create();
        dialog.setCancelable(false);

        //Einrichtung des RecyclerView
        recyclerView = v.findViewById(R.id.recyclerViewActivityListOfPumps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.activity));
        RecyclerAdapterListPumps adapterComments = new RecyclerAdapterListPumps(
                this.listPumps,
                this.activity,
                this);
        recyclerView.setAdapter(adapterComments);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        dialog.show();

    }

    @Override
    public void selectedPump(Pump pump) {
        this.dialog.cancel();
        new ConfigurePumps(this.activity, pump);
    }
}
