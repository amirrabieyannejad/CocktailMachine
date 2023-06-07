package com.example.cocktailmachine.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.example.cocktailmachine.databinding.ActivitySettingsBinding;

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

    /*
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

     */

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setVisibility();
    }

    private void setVisibility(){
        Log.i(TAG, "setVisibility");
        if(AdminRights.isAdmin()){
            Log.i(TAG, "setVisibility: isAdmin");
            binding.textViewSettingsClean.setVisibility(View.VISIBLE);
            binding.textViewSettingsCalibration.setVisibility(View.VISIBLE);
            binding.textViewSettingsPumps.setVisibility(View.VISIBLE);

        }else{
            Log.i(TAG, "setVisibility: is no Admin");
            binding.textViewSettingsClean.setVisibility(View.GONE);
            binding.textViewSettingsCalibration.setVisibility(View.GONE);
            binding.textViewSettingsPumps.setVisibility(View.GONE);
        }
    }

    public void calibrate(View view) {
        //TODO: calibrate
        Toast.makeText(this,"calibrate",Toast.LENGTH_SHORT).show();
    }

    public void status(View view) {
        //TODO status
        Toast.makeText(this,"status",Toast.LENGTH_SHORT).show();
    }

    public void loadNew(View view) {
        //TODO loadNew
        Toast.makeText(this,"loadNew",Toast.LENGTH_SHORT).show();
    }

    public void clean(View view) {
        //TODO clean
        Toast.makeText(this,"clean",Toast.LENGTH_SHORT).show();
    }

    public void sync(View view) {
        //TODO clean
        Toast.makeText(this,"sync",Toast.LENGTH_SHORT).show();
    }

    public void bluetooth(View view) {
        //TODO bluetooth
        Toast.makeText(this,"bluetooth",Toast.LENGTH_SHORT).show();
    }

    public void recipes(View view) {
    }

    public void pumps(View view) {
    }

    public void ingredients(View view) {
    }

    public void topics(View view) {
        //TODO topics
        Toast.makeText(this,"topics",Toast.LENGTH_SHORT).show();
    }
}