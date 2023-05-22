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
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.databinding.FragmentListBinding;
import com.example.cocktailmachine.databinding.FragmentModelBinding;
import com.example.cocktailmachine.ui.MainActivity;

public class ListFragment extends Fragment {
    private MainActivity activity;
    private FragmentListBinding binding;
    private static final String TAG = "ListFragment";

    public ListFragment(){
    }

    private ListRecyclerViewAdapters.ListRecyclerViewAdapter recyclerViewAdapter = null;


    private void setUP(String type, Long recipe_id){
        Log.i(TAG, "setUP: "+type+" "+recipe_id.toString());
        switch (type){
            case "AvailableRecipes":
            case "AllRecipes":
            case "Pumps":
                error();return;
            case "Topics": setTopics(recipe_id);return;
            case "Ingredients": setIngredients(recipe_id);return;
            case "AddTopics": setAddTopics(recipe_id);return;
            case "AddIngredients": setAddIngredients(recipe_id);return;
        }
    }

    private void setUP(String type){
        Log.i(TAG, "setUP: "+type);
        switch (type){
            case "AvailableRecipes": setAvailableRecipes(); return;
            case "AllRecipes": setAllRecipes();return;
            case "Topics": setTopics();return;
            case "Ingredients": setIngredients();return;
            case "AddTopics":
            case "AddIngredients":
                error();return;
            case "Pumps": setPumps();return;
        }
    }

    //lists

    private void setAddIngredients(Long recipe_id){
        Log.i(TAG, "setAddIngredients: "+recipe_id.toString());
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        recyclerViewAdapter.loadAdd();
        set( "Zutaten hinzufügen");
    }

    private void setAddTopics(Long recipe_id){
        Log.i(TAG, "setAddTopics: "+recipe_id.toString());
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        recyclerViewAdapter.loadAdd();
        set("Serviervorschläge hinzufügen");
    }

    private void setIngredients(Long recipe_id){
        Log.i(TAG, "setIngredients: "+recipe_id.toString());
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        set( "Zutaten");
    }

    private void setTopics(Long recipe_id){
        Log.i(TAG, "setTopics: "+recipe_id.toString());
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        set("Serviervorschläge");
    }

    private void setPumps(){
        Log.i(TAG, "setPumps");
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter();
        set( "Pumpen");
    }

    private void setIngredients(){
        Log.i(TAG, "setIngredients");
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter();
        set( "Zutaten");
    }

    private void setTopics(){
        Log.i(TAG, "setTopics");
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter();
        set("Serviervorschläge");
    }

    private void setAvailableRecipes(){
        Log.i(TAG, "setAvailableRecipes");
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        set( "Rezepte");
    }

    private void setAllRecipes(){
        Log.i(TAG, "setAllRecipes");
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
        Log.i(TAG, "set: "+title);
        /*
        binding.includeList.textViewNameListTitle.setText(title);
        binding.includeList.recylerViewNames.setLayoutManager(this.recyclerViewAdapter.getManager(this.getContext()));
        binding.includeList.recylerViewNames.setAdapter(this.recyclerViewAdapter);
        binding.includeList.getRoot().setVisibility(View.VISIBLE);
        setButtons();
        if(recyclerViewAdapter!=null) {
            recyclerViewAdapter.addGoToModelListener(this);
        }

         */
        ListLayout.set(binding.includeList.textViewNameListTitle,
                title,
                binding.includeList.recylerViewNames,
                this.getContext(),
                this.recyclerViewAdapter,
                this,
                binding.includeList.imageButtonListDelete,
                binding.includeList.imageButtonListEdit);
        binding.includeList.getRoot().setVisibility(View.VISIBLE);
    }

    private void setTopicFAB(){

    }

    private void error(){
        Log.i(TAG, "error");
        NavHostFragment.findNavController(ListFragment.this)
                .navigate(R.id.action_listFragment_to_mainActivity);
    }


    /*
    private void setButtons(){
        binding.includeList.imageButtonListDelete.setOnClickListener(v -> {
            if(recyclerViewAdapter.isCurrentlyDeleting()){
                recyclerViewAdapter.finishDelete();
                reload();
            }else{
                if(recyclerViewAdapter.isCurrentlyAdding()){
                    Toast.makeText(
                            getContext(),
                            "Kein Löschen möglich!",
                            Toast.LENGTH_SHORT)
                            .show();
                }else {
                    recyclerViewAdapter.loadDelete();
                }
            }
        });

        binding.includeList.imageButtonListEdit.setOnClickListener(v -> {
            if(recyclerViewAdapter.isCurrentlyAdding()){
                recyclerViewAdapter.finishAdd();
                reload();
            }else{
                if(recyclerViewAdapter.isCurrentlyDeleting()){
                    Toast.makeText(
                            getContext(),
                            "Kein Editieren möglich!",
                            Toast.LENGTH_SHORT)
                            .show();
                }else {
                    recyclerViewAdapter.loadAdd();
                }
            }
        });
    }

     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        if(savedInstanceState != null) {
            Log.i(TAG, "onViewCreated: savedInstanceState != null");
            Log.i(TAG, "onViewCreated: savedInstanceState"+savedInstanceState);
            String type = savedInstanceState.getString("Type");
            if(savedInstanceState.containsKey("Recipe_ID")){
                Log.i(TAG, "onViewCreated: savedInstanceState has Recipe_ID");
                Long id = savedInstanceState.getLong("Recipe_ID");
                setUP(type, id);
            }else{
                Log.i(TAG, "onViewCreated: savedInstanceState has no Recipe_ID");
                setUP(type);
            }
        }else{
            Log.i(TAG, "savedInstanceState == null");
            setUP("AvailableRecipes");
        }
    }
}