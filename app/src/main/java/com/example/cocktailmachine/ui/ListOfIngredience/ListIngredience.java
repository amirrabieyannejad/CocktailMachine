package com.example.cocktailmachine.ui.ListOfIngredience;

import static com.example.cocktailmachine.data.Topic.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListIngredience extends AppCompatActivity implements RecyclerViewListenerListIngredience{


    private List<Ingredient> listIngredients;
    private List<Ingredient> filteredListIngredients;
    private RecyclerView recyclerView;

    private Context context;

    Ingredient chosenIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ingredience);

        this.context = this;

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
        filteredListIngredients = new ArrayList<>(listIngredients);
        /**try {

        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.e(TAG, "ListIngredience : Error "+ e.toString());
        }*/




        //Einrichtung des RecyclerView
        recyclerView = findViewById(R.id.recyclerViewListIngredience);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,listIngredients,this);
        recyclerView.setAdapter(adapterComments);


        //Einrichtung des Suchfeldes
        EditText searchFild = findViewById(R.id.editTextTextListIngredienceSearchTerm);
        searchFild.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchterm = searchFild.getText().toString();
                filteredListIngredients = ingredientListFilter(listIngredients,searchterm);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,context);
                recyclerView.setAdapter(adapterComments);

            }
        });
        System.out.println("");
    }

    @Override
    public void selectIngredience(Ingredient ingredient) {
        if(ingredient.equals(chosenIngredient)){
            chosenIngredient = null;
        }else{
            chosenIngredient = ingredient;
        }

        Toast.makeText(this, "Es wurde eine Auswahl getroffen",Toast.LENGTH_LONG).show();
        //Todo Philipp hier musst du noch den Wert zurück geben
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,context);
        recyclerView.setAdapter(adapterComments);
    }

    private List<Ingredient> ingredientListFilter(List<Ingredient> list,String searchterm){
        if(searchterm == ""){
            return list;
        }
        LinkedList<Ingredient> output = new LinkedList<>();
        for (Ingredient item : list){
            if(item.getName().toLowerCase().contains(searchterm.toLowerCase())){
                output.add(item);
            }
        }
        if(chosenIngredient !=null){
            output.remove(chosenIngredient);
            output.addFirst(chosenIngredient);
        }

        return output;

    }
}