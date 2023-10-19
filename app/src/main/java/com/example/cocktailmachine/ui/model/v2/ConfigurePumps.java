package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerAdapterListIngredience;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.model.v2.ListConfigurePumps.RecyclerViewListenerListIngredience;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ConfigurePumps implements RecyclerViewListenerListIngredience {

    //Variablen
    private List<Ingredient> listIngredients;
    private List<Ingredient> filteredListIngredients;
    private HashMap<String, Long> mapIngredients;
    private HashMap<String, Long> filteredMapIngredients;
    private RecyclerView recyclerView;
    private Button buttonConfirmChoice;
    private Activity activity;
    private Ingredient chosenIngredient;
    private String chosenIngredientString;
    private ConfigurePumps configurePumpsContext;

    //Konstruktor
    public ConfigurePumps(Activity activity, Pump pump) {

        this.activity = activity;
        this.configurePumpsContext = this;
        this.listIngredients = new LinkedList<>();



        listIngredients = Ingredient.getAllIngredients(this.activity);
        //mapIngredients = Ingredient.getPumpSet(activity);


        filteredListIngredients = new ArrayList<>(listIngredients);
        //filteredMapIngredients = new HashMap<>(mapIngredients);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        View v = activity.getLayoutInflater().inflate(R.layout.dialog_layout_pump_configure, null);


        //Einrichtung des RecyclerView
        recyclerView = v.findViewById(R.id.recyclerViewDialogPumpconfigure);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.activity));
        RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,listIngredients,configurePumpsContext);
        //RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredientString,mapIngredients,configurePumpsContext);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(ConfigurePumps.this.activity));
                RecyclerAdapterListIngredience adapterComments = new RecyclerAdapterListIngredience(chosenIngredient,filteredListIngredients,configurePumpsContext);
                recyclerView.setAdapter(adapterComments);

            }
        });

        //Einrichtung des Buttons zur bestätigung der eingabe
        buttonConfirmChoice = v.findViewById(R.id.buttonDialogPumpconfigureConfirmChoice);
        buttonConfirmChoice.setEnabled(false);
        buttonConfirmChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pump.setCurrentIngredient(ConfigurePumps.this.activity,chosenIngredient);
                GetDialog.setFixedPumpVolume(ConfigurePumps.this.activity,pump);
            }
        });



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
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
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

    private HashMap<String, Long> ingredientMapFilter(HashMap<String, Long> map,String searchTerm){
        if(searchTerm.equals("")){
            return map;
        }
        HashMap<String, Long> output = new HashMap<String, Long>(map);
        for (String item : map.keySet()){
            if(!item.toLowerCase().contains(searchTerm.toLowerCase())){
                output.remove(item);
            }
        }
        return output;

    }

}
