package com.example.cocktailmachine.ui.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.helper.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

public class WaitNotSetActivity extends AppCompatActivity {

    private static final String TAG = "WaitNotSetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_not_set);

        ExtraHandlingDB.loadPrepedDB(this);
        Log.i(TAG, "loaded");

        if(Dummy.isDummy){
            ExtraHandlingDB.loadDummy(this);
            Log.v(TAG, "onCreate: dummy: load Dummy");

            if(!Dummy.withSetCalibration){
                CocktailMachineCalibration.getSingleton().setIsDone(true);
                Log.v(TAG, "onCreate: dummy:  not withSetCalibration ");
                GetActivity.goToMenu(this);
            }
        }
        reload();
    }

    public void login(View view) {
        if(!ExtraHandlingDB.hasLoadedDB(this)){
            Toast.makeText(this, "Bitte warte noch einen Moment!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(AdminRights.isAdmin()) {
            Log.i(TAG, "login: is admin");
            GetDialog.startAutomaticCalibration(this);
            return;
            //GetActivity.goToMenu(a);
        }
        AdminRights.login(this, this.getLayoutInflater(), dialog -> {
            Log.i(TAG, "login: is admin");
            dialog.dismiss();
            if(AdminRights.isAdmin()) {
                GetDialog.startAutomaticCalibration(WaitNotSetActivity.this);
                //GetActivity.goToMenu(a);
            }
        });
    }

    public void nextCocktailmachine(View view) {
        GetActivity.startAgain(this);
    }

    public void reload(View view) {
        reload();

    }

    private void reload(){
        Postexecute success = new Postexecute() {
            @Override
            public void post() {

                Log.v(TAG, "is set");
                Toast.makeText(WaitNotSetActivity.this, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
                GetActivity.goToMenu(WaitNotSetActivity.this);
            }
        };
        Postexecute noSuccess = new Postexecute() {
            @Override
            public void post() {
                Toast.makeText(WaitNotSetActivity.this, "Cocktailmaschine ist nicht bereit.", Toast.LENGTH_SHORT).show();
            }
        };
        CocktailMachine.isCocktailMachineSet(noSuccess,success,this);
    }
}

