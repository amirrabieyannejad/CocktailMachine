package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMainBinding;
import com.example.cocktailmachine.databinding.ActivityMenueBinding;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.model.ModelFragment;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;

public class Menue extends AppCompatActivity {
    private static final String TAG = "Menue";
    private ActivityMenueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_menue);
        binding = ActivityMenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(!DatabaseConnection.is_initialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initialize_singleton(this, UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.i(TAG, "onCreate: DataBase is initialized");
                //Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
        }
        if(AdminRights.isAdmin()){
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }else{
            binding.activityMenueLogout.setVisibility(View.GONE);
            binding.activityMenueLogin.setVisibility(View.VISIBLE);
        }
    }

    public void openRecipeList(View view) {
        Intent success = new Intent(this, ModelActivity.class);
        Bundle b = new Bundle();
        b.putString("Fragment", "RecipeList");
        startActivity(success, b);
    }

    public void openRecipeCreator(View view){
        Intent success = new Intent(this, ModelActivity.class);
        Bundle b = new Bundle();
        b.putString("Fragment", "RecipeCreator");
        startActivity(success, b);
    }


    public void openSingelCocktailView(View view){
        Intent success = new Intent(this, SingleCocktailChoice.class);
        startActivity(success);

    }

    public void openGlassFillAnimationView(View view){
        Intent success = new Intent(this, FillAnimation.class);
        startActivity(success);

    }


    public void openGrafik(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }

    public void openDeviceScan(View view){
        Intent success = new Intent(this, DeviceScanActivity.class);
        startActivity(success);
    }

    public void login(View view){
        Log.i(TAG, "login");
        AdminRights.login(this,
                getLayoutInflater(),
                dialog -> successfulLogin());
        Log.i(TAG, "finished login");
    }

    public void successfulLogin(){
        Log.i(TAG, "successfulLogin");
        if(AdminRights.isAdmin()) {
            Log.i(TAG, "successful login: admin");
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }
    }

    public void logout(View view){
        Log.i(TAG, "logout");
        AdminRights.logout(this);
        binding.activityMenueLogout.setVisibility(View.GONE);
        binding.activityMenueLogin.setVisibility(View.VISIBLE);
        Log.i(TAG, "finished logout");
    }



    public void exit(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }


}