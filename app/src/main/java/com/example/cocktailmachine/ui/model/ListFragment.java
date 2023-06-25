package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.databinding.FragmentListBinding;

public class ListFragment extends Fragment {
    private ModelActivity activity;
    private FragmentListBinding binding;
    private static final String TAG = "ListFragment";

    private String local_type = "AllRecipes";
    private ModelType type;
    private Long recipeID = -1L;

    public ListFragment(){
    }

    private ListRecyclerViewAdapters.ListRecyclerViewAdapter recyclerViewAdapter = null;


    private void preSetUp(){
        Log.i(TAG, "preSetUp");
        if(recipeID >-1L){
            Log.i(TAG, "preSetUp with recipe");
            setUP(recipeID);
        }else{
            Log.i(TAG, "preSetUp without recipe");
            setUP();
        }
    }

    private void setUP(Long recipeID){
        Log.i(TAG, "setUP: "+ local_type +" "+recipeID.toString());
        switch (local_type){
            case "AvailableRecipes":
            case "AllRecipes":
            case "Pumps":
                error();return;
            case "Topics": setTopics(recipeID);return;
            case "Ingredients": setIngredients(recipeID);return;
            case "AddTopics": setAddTopics(recipeID);return;
            case "AddIngredients": setAddIngredients(recipeID);return;
        }
    }

    private void setUP(){
        Log.i(TAG, "setUP: "+ local_type);
        switch (local_type){
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
        type = ModelType.INGREDIENT;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe_id);
        recyclerViewAdapter.loadAdd();
        set( "Zutaten hinzufügen");
    }

    private void setAddTopics(Long recipe_id){
        Log.i(TAG, "setAddTopics: "+recipe_id.toString());
        type = ModelType.TOPIC;
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
        type = ModelType.TOPIC;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe_id);
        set("Serviervorschläge");
    }

    private void setPumps(){
        Log.i(TAG, "setPumps");
        if(!AdminRights.isAdmin()){
            Toast.makeText(this.getContext(),
                    "Pumpen find nur für den Administrator sichtbar.",
                    Toast.LENGTH_SHORT)
                            .show();
            error();
        }
        type = ModelType.PUMP;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter();
        set( "Pumpen");
    }

    private void setIngredients(){
        Log.i(TAG, "setIngredients");
        type = ModelType.INGREDIENT;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter();
        set( "Zutaten");
    }

    private void setTopics(){
        Log.i(TAG, "setTopics");
        type = ModelType.TOPIC;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter();
        set("Serviervorschläge");
    }

    private void setAvailableRecipes(){
        Log.i(TAG, "setAvailableRecipes");
        type = ModelType.RECIPE;
        recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        set( "Rezepte");
    }

    private void setAllRecipes(){
        Log.i(TAG, "setAllRecipes");
        type = ModelType.RECIPE;
        ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter recyclerViewAdapter =
                new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter();
        recyclerViewAdapter.replaceRecipes(Recipe.getAllRecipes());
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
                ListFragment.this,
                binding.includeList.imageButtonListDelete,
                binding.includeList.imageButtonListEdit,
                binding.includeList.imageButtonListAdd);
        binding.includeList.getRoot().setVisibility(View.VISIBLE);

    }

    private void setFAB(){
        Log.i(TAG, "setFAB: "+type.name());
        if(AdminRights.isAdmin()) {
            Log.i(TAG, "setFAB: admin, add fab");
            activity.setFAB(v -> {
                Log.i(TAG, "setFAB: clicked: list to edit: " + type.name());
                Bundle b = new Bundle();
                b.putString("Type", type.name());
                Log.i(TAG, "setFAB: clicked: b: " + b);
                NavHostFragment
                        .findNavController(
                                ListFragment.this)
                        .navigate(R.id.action_listFragment_to_editModelFragment,
                                b);
            }, R.drawable.ic_add);
        }else{
            Log.i(TAG, "setFAB: no admin, no add");
            activity.invisibleFAB();
        }
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
        activity = (ModelActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            Log.i(TAG, "onViewCreated: getArguments != null");
            Log.i(TAG, "onViewCreated: getArguments: "+args);
            local_type = args.getString("Type");
            if(args.containsKey("Recipe_ID")){
                Log.i(TAG, "onViewCreated: getArguments has Recipe_ID");
                recipeID = args.getLong("Recipe_ID");
                //setUP(recipeID);
            }else{
                Log.i(TAG, "onViewCreated: getArguments has no Recipe_ID");
                //setUP();
            }
        }else{
            Log.i(TAG, "onViewCreated: getArguments == null");
            //setUP();
        }
        preSetUp();
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        if(!local_type.contains("Add")) {
            setFAB();
        }
        if (activity != null) {
            //Objects.requireNonNull(activity.getSupportActionBar()).setTitle("");
            activity.setToolBarTitle("");
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        preSetUp();
    }
}