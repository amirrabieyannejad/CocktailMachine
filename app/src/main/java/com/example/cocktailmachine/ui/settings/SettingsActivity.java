package com.example.cocktailmachine.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Status;
import com.example.cocktailmachine.databinding.ActivitySettingsBinding;
import com.example.cocktailmachine.ui.model.ModelActivity;
import com.example.cocktailmachine.ui.model.ModelType;

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

        }else{
            Log.i(TAG, "setVisibility: is no Admin");
            binding.textViewSettingsPumps.setVisibility(View.GONE);
            binding.textViewMachine.setVisibility(View.GONE);
        }
    }



    //Listen
    /**
     * got to recipe list
     * @param view
     * @author Johanna Reidt
     */
    public void recipes(View view) {
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.RECIPE.name());
        bundle.putString("FragmentType", ModelActivity.FragmentType.List.name());
        startActivity(intent, bundle);
    }

    /**
     * go to pump list
     * @param view
     * @author Johanna Reidt
     */
    public void pumps(View view) {
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.PUMP.name());
        bundle.putString("FragmentType", ModelActivity.FragmentType.List.name());
        startActivity(intent, bundle);
    }

    /**
     * go to ingredient list
     * @author Johanna Reidt
     * @param view
     */
    public void ingredients(View view) {
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.INGREDIENT.name());
        bundle.putString("FragmentType", ModelActivity.FragmentType.List.name());
        startActivity(intent, bundle);
    }

    /** got to topics list*
     * @author Johanna Reidt
     *
     * @param view
     */
    public void topics(View view) {
        //TO DO topics
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.TOPIC.name());
        bundle.putString("FragmentType", ModelActivity.FragmentType.List.name());
        startActivity(intent, bundle);
        //Toast.makeText(this,"topics",Toast.LENGTH_SHORT).show();
    }



    //Bluetooth

    /**
     * open bluetooth scan activity
     * @author Johanna Reidt
     * @param view
     */
    public void bluetooth(View view) {
        //TO DO bluetooth
        //Toast.makeText(this,"bluetooth",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    /**
     * open bluetooth Scan
     * @author Johanna Reidt
     * @param view
     */
    public void scan(View view) {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);

    }

    /**
     * synchronised Pumps
     * and downloads all recipes into db
     * @author Johanna Reidt
     * @param view
     */
    public void sync(View view) {
        Pump.sync(this);
        Recipe.sync(this);
        Toast.makeText(this,"Synchronisierung läuft!",Toast.LENGTH_SHORT).show();
    }



    //CocktailMachine
    /**
     * TO DO machine title
     * @author Johanna Reidt
     * @param view
     */
    public void machine(View view) {
        Intent intent = new Intent(this, MachineSettingsActivity.class);
        startActivity(intent);
    }



    //DB
    /**
     * TODO db title, maybe delete
     * @author Johanna Reidt
     * @param view
     */
    public void db(View view) {
        Toast.makeText(this,"Datenbank!",Toast.LENGTH_SHORT).show();
    }

    /**
     * do local refresh of database if initialized
     * @author Johanna Reidt
     * @param view
     */
    public void loadNew(View view) {
        DatabaseConnection.initializeSingleton(this);
        try {
            DatabaseConnection.localRefresh();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }

        Toast.makeText(this,"Lade aus der Datenbank neu!",Toast.LENGTH_SHORT).show();
    }



}