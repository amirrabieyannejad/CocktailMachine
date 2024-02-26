package com.example.cocktailmachine.ui.model;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.databinding.ActivityWaitNotSetBinding;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

public class WaitNotSetActivity extends AppCompatActivity {

    private static final String TAG = "WaitNotSetActivity";
    private ActivityWaitNotSetBinding binding;
    private boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_wait_not_set);
        binding = ActivityWaitNotSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ExtraHandlingDB.loadPrepedDB(this);
        Log.i(TAG, "loaded");

        if(Dummy.isDummy){
            ExtraHandlingDB.loadDummy(this);
            Log.v(TAG, "onCreate: dummy: load Dummy");

            if(!Dummy.withSetCalibration){
                CocktailMachine.getSingleton().setIsDone(true);
                Log.v(TAG, "onCreate: dummy:  not withSetCalibration ");
                GetActivity.goToMenu(this);
            }
        }
        reload();
    }

    /**
     * if no loaded DB, wait
     * login if not admin and go to automatic calibration as admin
     * @author Johanna Reidt
     */
    public void login() {
        Log.i(TAG, "login");
        lock();
        if(!ExtraHandlingDB.hasLoadedDB(this)){
            Log.i(TAG, "login: no loaded DB");
            Toast.makeText(this, "Bitte warte noch einen Moment!", Toast.LENGTH_SHORT).show();
            unlock();
            return;
        }

        DialogInterface.OnCancelListener ocl = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                unlock();
            }
        };

        if(AdminRights.isAdmin()) {
            Log.i(TAG, "login: is admin");
            GetDialog.startAutomaticCalibration(this);
            return;
            //GetActivity.goToMenu(a);
        }
        AdminRights.login(this, this.getLayoutInflater(), dialog -> {
            Log.i(TAG, "login: is admin");
            dialog.dismiss();
            unlock();
            if(AdminRights.isAdmin()) {
                GetDialog.startAutomaticCalibration(WaitNotSetActivity.this);
                //GetActivity.goToMenu(a);
            }
        });
    }

    /**
     * choose a different cocktail machine
     * @author Johanna Reidt
     */
    public void nextCocktailmachine() {
        lock();
        Log.v(TAG, "reload: nextCocktailmachine");
        GetActivity.startAgain(this);
    }

    private void reload(){
        lock();
        Log.i(TAG, "reload");
        if(!ExtraHandlingDB.hasLoadedDB(this)){
            Log.v(TAG, "reload: no loaded DB");
            Toast.makeText(this, "Bitte warte noch einen Moment!", Toast.LENGTH_SHORT).show();
            unlock();
            return;
        }
        Toast.makeText(this, "Checkt den Cocktailmaschinenstatus!", Toast.LENGTH_SHORT).show();
        Postexecute success = new Postexecute() {
            @Override
            public void post() {
                Log.v(TAG, "reload: is set");
                Toast.makeText(WaitNotSetActivity.this, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
                GetActivity.goToMenu(WaitNotSetActivity.this);
            }
        };
        Postexecute noSuccess = new Postexecute() {
            @Override
            public void post() {
                Log.v(TAG, "reload: is NOT set");
                Toast.makeText(WaitNotSetActivity.this, "Cocktailmaschine ist nicht bereit.", Toast.LENGTH_SHORT).show();
                unlock();
            }
        };
        CocktailMachine.isCocktailMachineSet(noSuccess,success,this);
    }

    private void lock(){
        Log.i(TAG, "lock");
        this.locked = true;
        View.OnClickListener l = v -> {
            Log.i(TAG, "lock: clicked");
            Toast.makeText(WaitNotSetActivity.this,"Bitte warten!",Toast.LENGTH_SHORT).show();
        };
        binding.textViewWaitNotSetLogin.setOnClickListener(l);
        binding.imageButtonReload.setOnClickListener(l);
        binding.textViewWaitNotSetNextCocktailmachine.setOnClickListener(l);
    }

    private void unlock(){
        Log.i(TAG, "unlock");
        this.locked = false;
        binding.textViewWaitNotSetLogin.setOnClickListener(v -> login());

        binding.textViewWaitNotSetNextCocktailmachine.setOnClickListener(v -> nextCocktailmachine());

        binding.imageButtonReload.setOnClickListener(v -> reload());
    }
}

