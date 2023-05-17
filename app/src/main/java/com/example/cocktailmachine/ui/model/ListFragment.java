package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private void setUP(String type, Long recipe_id){
        switch (type){
            case "AvailableRecipes": error();
            case "AllRecipes": error();
            case "Topics": setTopics(recipe_id);
            case "Ingredients": setIngredients(recipe_id);
            case "Pumps": error();
        }
    }

    private void setUP(String type){
        switch (type){
            case "AvailableRecipes": setAvailableRecipes();
            case "AllRecipes": setAllRecipes();
            case "Topics": setTopics();
            case "Ingredients": setIngredients();
            case "Pumps": setPumps();
        }
    }

    private void setIngredients(Long recipe_id){
        ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        set(recyclerViewAdapter, "Zutaten");
    }

    private void setTopics(Long recipe_id){
        ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        set(recyclerViewAdapter, "Serviervorschläge");
    }


    private void setPumps(){
        ListRecyclerViewAdapters.PumpListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter();
        set(recyclerViewAdapter, "Pumpen");
    }

    private void setIngredients(){
        ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter();
        set(recyclerViewAdapter, "Zutaten");
    }

    private void setTopics(){
        ListRecyclerViewAdapters.TopicListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter();
        set(recyclerViewAdapter, "Serviervorschläge");
    }

    private void setAvailableRecipes(){
        ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        set(recyclerViewAdapter, "Rezepte");
    }

    private void setAllRecipes(){
        ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        try {
            recyclerViewAdapter.replaceRecipes(Recipe.getAllRecipes());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        set(recyclerViewAdapter, "Rezepte (Administrator)");
    }

    private void set(ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter, String title){
        binding.includeList.textViewNameListTitle.setText(title);
        binding.includeList.recylerViewNames.setLayoutManager(adapter.getManager(this.getContext()));
        binding.includeList.recylerViewNames.setAdapter(adapter);
        binding.includeList.getRoot().setVisibility(View.VISIBLE);
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