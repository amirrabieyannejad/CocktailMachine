package com.example.cocktailmachine.ui.ListOfIngredience;

import static com.example.cocktailmachine.data.Topic.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;

import java.util.List;

public class ListIngredience extends AppCompatActivity implements RecyclerViewListenerListIngredience{


    private List<Ingredient> listIngredients;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ingredience);

        if(!DatabaseConnection.isInitialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(this, AdminRights.getUserPrivilegeLevel());// UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.i(TAG, "onCreate: DataBase is initialized");
                //Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
        }else{
            Log.i(TAG, "onCreate: DataBase is already initialized");
        }


        listIngredients = Ingredient.getAllIngredients();
        /**try {

        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.e(TAG, "ListIngredience : Error "+ e.toString());
        }*/


        //Einrichtung des RecyclerView für Personen
        recyclerView = findViewById(R.id.recyclerViewListIngredience);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(listIngredients,this);
        recyclerView.setAdapter(adapterComments);

        System.out.println("");
    }

    @Override
    public void selectIngredience(long Id) {
        Toast.makeText(this, "Es wurde eine Auswahl getroffen",Toast.LENGTH_LONG).show();
        //Todo Philipp hier musst du noch den Wert zurück geben
    }
}