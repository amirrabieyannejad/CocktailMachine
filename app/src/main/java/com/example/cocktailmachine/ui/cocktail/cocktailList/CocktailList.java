package com.example.cocktailmachine.ui.cocktail.cocktailList;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

public class CocktailList extends AppCompatActivity {
    private static final String TAG = "CocktailList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocktail_list);
        if(!DatabaseConnection.isInitialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(this, UserPrivilegeLevel.Admin);
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
}