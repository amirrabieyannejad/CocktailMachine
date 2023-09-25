package com.example.cocktailmachine.ui.model.v2;

import static com.example.cocktailmachine.data.Topic.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.Menue;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerAdapterListIngredience;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerViewListenerListIngredience;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigurePumps implements RecyclerViewListenerListIngredience {

    private List<Ingredient> listIngredients;
    private List<Ingredient> filteredListIngredients;
    private RecyclerView recyclerView;

    private Context context;

    Ingredient chosenIngredient;


    public ConfigurePumps(Activity activity) {

        this.context = activity;
        this.listIngredients = new LinkedList<Ingredient>();
        if(!DatabaseConnection.isInitialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(context, AdminRights.getUserPrivilegeLevel());// UserPrivilegeLevel.Admin);
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



        /**




        //Einrichtung des Suchfeldes
        EditText searchFild = alterCustomDialog.findViewById(R.id.editTextTextListIngredienceSearchTerm);
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
         */

        //final AlertDialog dialog = alertDialog.create();
        /**Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_layout_pump_configure);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        */
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_layout_pump_configure, null);


        //Einrichtung des RecyclerView
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerViewDialogPumpconfigure);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,listIngredients,this);
        recyclerView.setAdapter(adapterComments);

        alertDialog.setView(v);
        AlertDialog dialog = alertDialog.create();
        dialog.show();

    }


    public void selectIngredience(Ingredient ingredient) {
        if(ingredient.equals(chosenIngredient)){
            chosenIngredient = null;
        }else{
            chosenIngredient = ingredient;
        }

        Toast.makeText(context, "Es wurde eine Auswahl getroffen",Toast.LENGTH_LONG).show();
        //Todo Philipp hier musst du noch den Wert zurück geben
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,context);
        //recyclerView.setAdapter(adapterComments);
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
