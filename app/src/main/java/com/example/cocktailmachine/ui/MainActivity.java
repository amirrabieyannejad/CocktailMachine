package com.example.cocktailmachine.ui;

import android.os.Bundle;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.cocktailmachine.R;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        DatabaseConnection.initialize_singleton(this);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(view -> Snackbar.make(
                        view,
                        "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.i(TAG, "onSupportNavigateUp");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setFAB(View.OnClickListener listener, @DrawableRes int drawable) {
        binding.fab.setOnClickListener(listener);
        binding.fab.setImageResource(drawable);
        binding.fab.setVisibility(View.VISIBLE);
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