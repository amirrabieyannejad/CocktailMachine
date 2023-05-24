package com.example.cocktailmachine.ui;

import android.os.Bundle;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.cocktailmachine.R;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        DatabaseConnection.initialize_singleton(this, UserPrivilegeLevel.Admin);
        try {
            DatabaseConnection.getDataBase();
            Log.i(TAG, "onCreate: DataBase is initialized");
            Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate: DataBase is not initialized");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onCreate:  Json didnt work");
        }
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreate: binding set");
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: setContentView");

        setSupportActionBar(binding.toolbar);
        Log.i(TAG, "onCreate: setSupportActionBar");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        Log.i(TAG, "onCreate: setupActionBarWithNavController");
        /*
        binding.fab.setOnClickListener(view -> Snackbar.make(
                        view,
                        "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

         */
        Log.i(TAG,"onCreate finished");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.i(TAG, "onSupportNavigateUp");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setFAB(View.OnClickListener listener, @DrawableRes int drawable) {
        if(binding.fab == null){
            Log.i(TAG, "fab is null");
        }else {
            binding.fab.setOnClickListener(listener);
            binding.fab.setImageResource(drawable);
            binding.fab.setVisibility(View.VISIBLE);
        }
    }

    public void invisibleFAB(){
        binding.fab.setVisibility(View.GONE);
    }

    public void setToolBar(String title){
        binding.toolbar.setTitle(title);
    }

    public void invisibleToolBar(){
        binding.toolbar.setVisibility(View.GONE);
    }



}