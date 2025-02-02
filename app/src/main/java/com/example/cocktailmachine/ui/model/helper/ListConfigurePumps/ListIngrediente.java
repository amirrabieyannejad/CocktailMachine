package com.example.cocktailmachine.ui.model.helper.ListConfigurePumps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;


import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;

import java.util.LinkedList;
import java.util.List;

public class ListIngrediente extends AppCompatActivity implements RecyclerViewListenerListIngrediente {


    private List<Ingredient> listIngredients;
    private List<Ingredient> filteredListIngredients;
    private RecyclerView recyclerView;

    private Context context;

    Ingredient chosenIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list_ingredience);

        this.context = this;

        listIngredients = Ingredient.getAllIngredients(this);
        filteredListIngredients = new LinkedList<>(listIngredients);



        //Einrichtung des RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDialogPumpconfigure);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapterListIngrediente adapterComments = new RecyclerAdapterListIngrediente(chosenIngredient,listIngredients,this);
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
                RecyclerAdapterListIngrediente adapterComments = new RecyclerAdapterListIngrediente(chosenIngredient,filteredListIngredients,context);
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
        RecyclerAdapterListIngrediente adapterComments = new RecyclerAdapterListIngrediente(chosenIngredient,filteredListIngredients,context);
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