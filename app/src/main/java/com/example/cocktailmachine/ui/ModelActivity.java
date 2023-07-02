package com.example.cocktailmachine.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.example.cocktailmachine.ui.model.ListFragment;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.cocktailmachine.R;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.List;


public class ModelActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private Menu menu;
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        if(DatabaseConnection.isInitialized()) {
            DatabaseConnection.initializeSingleton(this, UserPrivilegeLevel.Admin);
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



    private void goTo(Bundle bundle){
        FragmentType ft = FragmentType.valueOf(bundle.getString("FragmentType"));
        ModelType mt = ModelType.valueOf(bundle.getString("ModelType"));
    }

    private void goTo(FragmentType ft, ModelType mt){
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Type",mt.toString());
        navController.popBackStack(R.id.listFragment, true);
        navController.clearBackStack(R.id.listFragment);
        navController.navigate(R.id.listFragment, bundle);
    }


    public Fragment getCurrentFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    protected void refreshCurrentFragment(){
        Log.i(TAG, "refreshCurrentFragment: start");
        //NavDestination current = navController.getCurrentDestination();
        //Log.i(TAG, "refreshCurrentFragment: "+ (current != null ? current.toString() : "null"));
        //navController.popBackStack(current.getId(), true);
        //navController.navigate(current.getId());
        getCurrentFragment().onResume();
        Log.i(TAG, "refreshCurrentFragment: end");
    }

    protected void refreshCurrentFragment(Bundle bundle){
        Log.i(TAG, "refreshCurrentFragment: start");
        //NavDestination current = navController.getCurrentDestination();
        //Log.i(TAG, "refreshCurrentFragment: "+ (current != null ? current.toString() : "null"));
        //navController.popBackStack(current.getId(), true);
        //navController.navigate(current.getId());
        //getCurrentFragment().setArguments(bundle);
        reloadCurrentFragment(bundle);
        Log.i(TAG, "refreshCurrentFragment: end");
    }


    protected void reloadCurrentFragment(){
        Log.i(TAG, "refreshCurrentFragment: start");
        NavDestination current = navController.getCurrentDestination();
        Log.i(TAG, "refreshCurrentFragment: "+ (current != null ? current.toString() : "null"));
        navController.popBackStack(current.getId(), true);
        navController.navigate(current.getId());
        Log.i(TAG, "refreshCurrentFragment: end");
    }

    protected void reloadCurrentFragment(Bundle bundle){
        Log.i(TAG, "refreshCurrentFragment: start");
        NavDestination current = navController.getCurrentDestination();
        Log.i(TAG, "refreshCurrentFragment: "+ (current != null ? current.toString() : "null"));
        navController.popBackStack(current.getId(), true);
        navController.navigate(current.getId(), bundle);
        Log.i(TAG, "refreshCurrentFragment: end");
    }

    protected void reloadCurrentFragmentSavely(){
        Log.i(TAG, "reloadCurrentFragmentSavely: start");
        Bundle bundle = getCurrentFragment().getArguments();
        Log.i(TAG, "reloadCurrentFragmentSavely: "+ bundle);
        reloadCurrentFragment(bundle);
        Log.i(TAG, "reloadCurrentFragmentSavely: end");
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

    public void setToolBarTitle(String title){
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
            //successfullLogin();
            loginMenuState();
        }else{
            logoutMenuState();
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




    //
    public enum FragmentType{
        Edit, Model, List
    }


    //Menu
    public void refresh(MenuItem item) {
        //TODO sync
        refreshCurrentFragment();
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
        loginMenuState();
        reloadCurrentFragmentSavely();
    }

    private void loginMenuState(){
        if(AdminRights.isAdmin()) {
            this.menu.findItem(R.id.action_admin_login).setVisible(false);
            this.menu.findItem(R.id.action_admin_logout).setVisible(true);
            this.menu.findItem(R.id.action_pumps).setVisible(true);
        }
    }

    private void logoutMenuState(){
        Log.i(TAG, "logout" );
        this.menu.findItem(R.id.action_admin_login).setVisible(true);
        this.menu.findItem(R.id.action_admin_logout).setVisible(false);
        this.menu.findItem(R.id.action_pumps).setVisible(false);
    }

    public void login(MenuItem item) {
        login();
    }

    public void logout(MenuItem item) {
        logoutMenuState();
        AdminRights.logout();
        Toast.makeText(this,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
        reloadCurrentFragmentSavely();
    }


}