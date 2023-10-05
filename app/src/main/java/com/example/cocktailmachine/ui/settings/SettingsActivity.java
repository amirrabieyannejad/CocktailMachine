package com.example.cocktailmachine.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;

import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.enums.AdminRights;

import com.example.cocktailmachine.databinding.ActivitySettingsBinding;
import com.example.cocktailmachine.ui.model.v1.ModelActivity;

import com.example.cocktailmachine.ui.model.FragmentType;


import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.ui.model.v2.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.v2.GetActivity;

/**
 * Settings
 * @created Fr. 23.Jun 2023 - 16:09
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreate: binding set");
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: setContentView");

        //setContentView(R.layout.activity_settings);
        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

         */
        /*
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

         */
        setVisibility();

        //TODO: bind bluetooth

    }



    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setVisibility();
    }

    /**
     * some features only visible for admin
     * @author Johanna Reidt
     */
    private void setVisibility(){
        Log.i(TAG, "setVisibility");
        if(AdminRights.isAdmin()){
            Log.i(TAG, "setVisibility: isAdmin");
            binding.textViewSettingsPumps.setVisibility(View.VISIBLE);
            binding.textViewMachine.setVisibility(View.VISIBLE);
            binding.textViewMachineClick.setVisibility(View.VISIBLE);

            binding.textViewSettingsLogin.setVisibility(View.GONE);
            binding.textViewSettingsLogout.setVisibility(View.VISIBLE);


        }else{
            Log.i(TAG, "setVisibility: is no Admin");
            binding.textViewSettingsPumps.setVisibility(View.GONE);
            binding.textViewMachine.setVisibility(View.GONE);
            binding.textViewMachineClick.setVisibility(View.GONE);

            binding.textViewSettingsLogin.setVisibility(View.VISIBLE);
            binding.textViewSettingsLogout.setVisibility(View.GONE);
        }
    }



    //Listen
    /**
     * got to recipe list
     * @param view
     * @author Johanna Reidt
     */
    public void recipes(View view) {
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.RECIPE.name());

        bundle.putString("FragmentType", FragmentType.List.name());

        startActivity(intent, bundle);

         */
        GetActivity.goToList(this,  ModelType.RECIPE);
    }

    /**
     * go to pump list
     * @param view
     * @author Johanna Reidt
     */
    public void pumps(View view) {
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.PUMP.name());

        bundle.putString("FragmentType", FragmentType.List.name());

        startActivity(intent, bundle);

         */

        GetActivity.goToList(this, ModelType.PUMP);
    }

    /**
     * go to ingredient list
     * @author Johanna Reidt
     * @param view
     */
    public void ingredients(View view) {
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.INGREDIENT.name());

        bundle.putString("FragmentType", FragmentType.List.name());

        startActivity(intent, bundle);

         */
        GetActivity.goToList(this, ModelType.INGREDIENT);
    }

    /** got to topics list*
     * @author Johanna Reidt
     *
     * @param view
     */
    public void topics(View view) {
        /*
        //TO DO topics
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.TOPIC.name());

        bundle.putString("FragmentType", FragmentType.List.name());
        startActivity(intent, bundle);
        //Toast.makeText(this,"topics",Toast.LENGTH_SHORT).show();

         */
        GetActivity.goToList(this,  ModelType.TOPIC);
    }



    //Bluetooth

    /**
     * open bluetooth scan activity
     * @author Johanna Reidt
     * @param view
     */
    /*
    public void bluetooth(View view) {
        //TO DO bluetooth
        //Toast.makeText(this,"bluetooth",Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, DeviceScanActivity.class);
        //startActivity(intent);
        if(Dummy.isDummy){
            Toast.makeText(this,
                    "Öffnet eigentlich BluetoothScan, " +
                            "aber wird sind in der VM freundlichen Version.",
                    Toast.LENGTH_SHORT).show();
            GetActivity.goToMenu(this);
            return;
        }
        GetActivity.goToScan(this);

    }

     */

    /**
     * open bluetooth Scan
     * @author Johanna Reidt
     * @param view
     */
    public void scan(View view) {
        if(Dummy.isDummy){
            Toast.makeText(this,
                    "Öffnet eigentlich BluetoothScan, " +
                            "aber wird sind in der VM-freundlichen Version.",
                    Toast.LENGTH_SHORT).show();
            GetActivity.goToMenu(this);
            return;
        }
        GetActivity.goToScan(this);

    }

    /**
     * synchronised Pumps
     * and downloads all recipes into db
     * @author Johanna Reidt
     * @param view
     */
    public void sync(View view) {
        //Pump.sync(this);
        Recipe.syncRecipeDBWithCocktailmachine(this);
        Toast.makeText(this,
                "Synchronisierung läuft!",
                Toast.LENGTH_SHORT).show();
    }



    //CocktailMachine
    /**
     * TO DO machine title
     * @author Johanna Reidt
     * @param view
     */
    public void machine(View view) {
        Intent intent = new Intent(
                this,
                MachineSettingsActivity.class);
        startActivity(intent);
    }

    public void newCalibration(View view){
        CocktailMachineCalibration.start(this);
    }




    //DB
    /**
     * TO DO db title, maybe delete
     * @author Johanna Reidt
     * @param view
     */
    /*
    public void db(View view) {
        Buffer.localRefresh(this);
        Toast.makeText(
                this,
                "Lade aus der Datenbank neu!",
                Toast.LENGTH_SHORT).show();
    }

     */

    /**
     * do local refresh of database if initialized
     * @author Johanna Reidt
     * @param view
     */
    public void loadNew(View view) {
        /*
        if(!DatabaseConnection.isInitialized()) {
            DatabaseConnection.initializeSingleton(this);
        }

         */
        Log.i(TAG, "loadNew");
        Buffer.localRefresh(this);
        Toast.makeText(this,"Lade aus der Datenbank neu!",Toast.LENGTH_SHORT).show();
    }


    public void loadPreped(View view) {
        Log.i(TAG, "loadPreped");
        Buffer.loadPreped(this);
    }

    public void login(View view) {
        Log.i(TAG, "login");
        AdminRights.login(this, getLayoutInflater(), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SettingsActivity.this.setVisibility();
            }
        });
    }

    public void logout(View view) {
        Log.i(TAG, "logout");
        AdminRights.logout();
        setVisibility();
    }

}