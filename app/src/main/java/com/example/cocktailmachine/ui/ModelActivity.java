package com.example.cocktailmachine.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.model.ListFragment;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.cocktailmachine.R;

import org.json.JSONException;


public class ModelActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private Menu menu;
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        if(!DatabaseConnection.is_initialized()) {
            DatabaseConnection.initialize_singleton(this, UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.i(TAG, "onCreate: DataBase is initialized");
                //Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
            /* catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate:  Json didnt work");
            }
            */
        }
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Log.i(TAG, "onCreate: binding set");
        setContentView(binding.getRoot());
        Log.i(TAG, "onCreate: setContentView");

        //setSupportActionBar(binding.toolbar);
        //setActionBar(binding.toolbar);
        setSupportActionBar(binding.toolbar);
        Log.i(TAG, "onCreate: setSupportActionBar");

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.i(TAG, "onCreateOptionsMenu" );
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, this.menu);
        if(AdminRights.isAdmin()){
            successfullLogin();
        }else{
            logout();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.i(TAG, "onSupportNavigateUp" );
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    //public void switchToNight(){
    //    getDelegate().applyDayNight();
    //}


    //Menu
    public void refresh(MenuItem item) {
        //TODO
    }

    public void goToMenue(MenuItem item) {
        Intent success = new Intent(this, Menue.class);
        startActivity(success);
    }

    public void goToTopics(MenuItem item) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Type","Topics");
        navController.navigate(R.id.listFragment, bundle);
    }

    public void goToIngredients(MenuItem item) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Type","Ingredients");
        navController.navigate(R.id.listFragment, bundle);
    }

    public void goToPumps(MenuItem item) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Type","Pumps");
        navController.navigate(R.id.listFragment, bundle);
    }

    public void goToRecipes(MenuItem item) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        if(AdminRights.isAdmin()) {
            bundle.putString("Type", "AllRecipes");
        }else{
            bundle.putString("Type", "AvailableRecipes");
        }
        navController.navigate(R.id.listFragment, bundle);
    }


    //Login / logout
    private void login(){
        Log.i(TAG, "login" );
        AdminRights.login(this, getLayoutInflater(), dialog -> successfullLogin());
    }

    private void successfullLogin(){
        Log.i(TAG, "successfullLogin" );
        if(AdminRights.isAdmin()) {
            this.menu.findItem(R.id.action_admin_login).setVisible(false);
            this.menu.findItem(R.id.action_admin_logout).setVisible(true);
            this.menu.findItem(R.id.action_pumps).setVisible(true);
        }
    }

    private void logout(){
        Log.i(TAG, "logout" );
        AdminRights.logout(this);
        this.menu.findItem(R.id.action_admin_login).setVisible(true);
        this.menu.findItem(R.id.action_admin_logout).setVisible(false);
        this.menu.findItem(R.id.action_pumps).setVisible(false);
    }

    public void login(MenuItem item) {
        login();
    }

    public void logout(MenuItem item) {
        logout();
        Toast.makeText(this,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
    }


}