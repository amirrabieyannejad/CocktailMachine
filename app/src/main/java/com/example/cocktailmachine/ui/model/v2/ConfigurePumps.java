package com.example.cocktailmachine.ui.model.v2;

import static com.example.cocktailmachine.data.Topic.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerAdapterListIngredience;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerViewListenerListIngredience;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigurePumps implements RecyclerViewListenerListIngredience {

    //Variablen
    private List<Ingredient> listIngredients;
    private List<Ingredient> filteredListIngredients;
    private RecyclerView recyclerView;
    private Button buttonConfirmChoice;
    private Context context;
    private Ingredient chosenIngredient;
    private ConfigurePumps configurePumpsContext;

    //Konstruktor
    public ConfigurePumps(Activity activity) {

        this.context = activity;
        this.configurePumpsContext = this;
        this.listIngredients = new LinkedList<>();


        listIngredients = Ingredient.getAllIngredients();
        filteredListIngredients = new ArrayList<>(listIngredients);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_layout_pump_configure, null);


        //Einrichtung des RecyclerView
        recyclerView = v.findViewById(R.id.recyclerViewDialogPumpconfigure);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,listIngredients,configurePumpsContext);
        recyclerView.setAdapter(adapterComments);

        //Einrichtung des Suchfeldes
        EditText searchField = v.findViewById(R.id.editTextDialogPumpconfigureSearchTerm);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchterm = searchField.getText().toString();
                filteredListIngredients = ingredientListFilter(listIngredients,searchterm);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,configurePumpsContext);
                recyclerView.setAdapter(adapterComments);

            }
        });

        //Einrichtung des Buttons zur bestätigung der eingabe
        buttonConfirmChoice = v.findViewById(R.id.buttonDialogPumpconfigureConfirmChoice);
        buttonConfirmChoice.setEnabled(false);



        //Erzeuge Dialog und zeige diese an
        alertDialog.setView(v);
        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.show();

    }

    //Funktionen
    public void selectIngredience(Ingredient ingredient) {
        if(ingredient.equals(chosenIngredient)){
            chosenIngredient = null;
            buttonConfirmChoice.setEnabled(false);
        }else{
            chosenIngredient = ingredient;
            buttonConfirmChoice.setEnabled(true);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,configurePumpsContext);
        recyclerView.setAdapter(adapterComments);
    }

    private List<Ingredient> ingredientListFilter(List<Ingredient> list,String searchTerm){
        if(searchTerm.equals("")){
            return list;
        }
        LinkedList<Ingredient> output = new LinkedList<>();
        for (Ingredient item : list){
            if(item.getName().toLowerCase().contains(searchTerm.toLowerCase())){
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
