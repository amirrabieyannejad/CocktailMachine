package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.databinding.FragmentListBinding;
import com.example.cocktailmachine.databinding.FragmentModelBinding;

class ListFragment extends Fragment {
    private FragmentListBinding binding;
    private static final String TAG = "ListFragment";

    private ListRecyclerViewAdapters.ListRecyclerViewAdapter recyclerViewAdapter = null;


    private void setUP(String type, Long recipe_id){
        switch (type){
            case "AvailableRecipes": error();
            case "AllRecipes": error();
            case "Topics": setTopics(recipe_id);
            case "Ingredients": setIngredients(recipe_id);
            case "AddTopics": setAddTopics(recipe_id);
            case "AddIngredients": setAddIngredients(recipe_id);
            case "Pumps": error();
        }
    }

    private void setUP(String type){
        switch (type){
            case "AvailableRecipes": setAvailableRecipes();
            case "AllRecipes": setAllRecipes();
            case "Topics": setTopics();
            case "Ingredients": setIngredients();
            case "AddTopics": error();
            case "AddIngredients": error();
            case "Pumps": setPumps();
        }
    }

    private void setAddIngredients(Long recipe_id){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        recyclerViewAdapter.loadAdd();
        set( "Zutaten hinzufügen");
    }

    private void setAddTopics(Long recipe_id){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        recyclerViewAdapter.loadAdd();
        set( "Serviervorschläge hinzufügen");
    }

    private void setIngredients(Long recipe_id){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        set( "Zutaten");
    }

    private void setTopics(Long recipe_id){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        set("Serviervorschläge");
    }

    private void setPumps(){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter();
        set( "Pumpen");
    }

    private void setIngredients(){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter();
        set( "Zutaten");
    }

    private void setTopics(){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter();
        set("Serviervorschläge");
    }

    private void setAvailableRecipes(){
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        set( "Rezepte");
    }

    private void setAllRecipes(){
        ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        try {
            recyclerViewAdapter.replaceRecipes(Recipe.getAllRecipes());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        this.recyclerViewAdapter = recyclerViewAdapter;
        set("Rezepte (Administrator)");
    }

    private void set(String title){
        binding.includeList.textViewNameListTitle.setText(title);
        binding.includeList.recylerViewNames.setLayoutManager(this.recyclerViewAdapter.getManager(this.getContext()));
        binding.includeList.recylerViewNames.setAdapter(this.recyclerViewAdapter);
        binding.includeList.getRoot().setVisibility(View.VISIBLE);
    }

    private void reload(){}

    private void setButtons(){
        binding.includeList.imageButtonListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewAdapter.isCurrentlyDeleting()){
                    recyclerViewAdapter.finishDelete();
                    reload();
                }else{
                    if(recyclerViewAdapter.isCurrentlyAdding()){
                        Toast.makeText(getContext(), "Kein Löschen möglich!", Toast.LENGTH_SHORT);
                    }else {
                        recyclerViewAdapter.loadDelete();
                    }
                }
            }
        });

        binding.includeList.imageButtonListEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewAdapter.isCurrentlyAdding()){
                    recyclerViewAdapter.finishAdd();
                    reload();
                }else{
                    if(recyclerViewAdapter.isCurrentlyDeleting()){
                        Toast.makeText(getContext(), "Kein Editieren möglich!", Toast.LENGTH_SHORT);
                    }else {
                        recyclerViewAdapter.loadAdd();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentListBinding.inflate(inflater, container, false);
        if(savedInstanceState != null) {
            String type = savedInstanceState.getString("Type");
            if(savedInstanceState.containsKey("Recipe_ID")){
                Long id = savedInstanceState.getLong("Recipe_ID");
                setUP(type, id);
            }else{
                setUP(type);
            }
        }else{
            setUP("AvailableRecipes");
        }
        return binding.getRoot();
    }

    private void error(){
        NavHostFragment.findNavController(ListFragment.this)
                .navigate(R.id.action_listFragment_to_mainActivity);
    }
}