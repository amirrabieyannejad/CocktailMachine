package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.model.ModelFragment;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;

public class Menue extends AppCompatActivity {
    private static final String TAG = "Menue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);
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



    public void exit(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }


}