package com.example.cocktailmachine.ui.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

public class WaitNotSetActivity extends AppCompatActivity {

    private static final String TAG = "WaitNotSetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_not_set);

        if(AdminRights.isAdmin()){
            GetDialog.startAutomaticCalibration(this);
        }
    }

    public void login(View view) {
        Activity a = this;
        if(AdminRights.isAdmin()) {
            Log.i(TAG, "login: is admin");
            GetDialog.startAutomaticCalibration(a);
            return;
            //GetActivity.goToMenu(a);
        }
        AdminRights.login(this, this.getLayoutInflater(), dialog -> {
            Log.i(TAG, "login: is admin");
            dialog.dismiss();
            if(AdminRights.isAdmin()) {
                GetDialog.startAutomaticCalibration(a);
                //GetActivity.goToMenu(a);
            }
        });
    }

    public void nextCocktailmachine(View view) {
        GetActivity.startAgain(this);
    }

    public void reload(View view) {
        if(CocktailMachine.isCocktailMachineSet(this)){
            Log.v(TAG, "is set");
            Toast.makeText(this, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
            GetActivity.goToMenu(this);
        }else {
            Toast.makeText(this, "Cocktailmaschine ist nicht bereit.", Toast.LENGTH_SHORT).show();
        }
    }
}