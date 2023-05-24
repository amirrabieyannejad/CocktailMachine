package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.databinding.FragmentModelBinding;
import com.example.cocktailmachine.ui.FirstFragment;
import com.example.cocktailmachine.ui.MainActivity;
import com.example.cocktailmachine.ui.SecondFragment;

public class ModelFragment extends Fragment {
    private FragmentModelBinding binding;
    private MainActivity activity;
    private static final String TAG = "ModelFragment";
    private ModelType type;
    private Long id;

    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;
    private Recipe recipe;

    public ModelFragment(){
    }

    protected static enum ModelType{
        TOPIC, INGREDIENT, PUMP,RECIPE
    }



    private void setUP(){
        Log.i(TAG, "setUP: "+type+" "+id.toString());
        binding.textViewDescription.setVisibility(View.GONE);
        binding.includeAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includePump.getRoot().setVisibility(View.GONE);
        binding.includeIngredientAdmin.getRoot().setVisibility(View.GONE);
        binding.includeIngredients.getRoot().setVisibility(View.GONE);
        binding.includeTopics.getRoot().setVisibility(View.GONE);


        switch (type){
            case TOPIC:setTopic();return;
            case INGREDIENT:setIngredient();return;
            case PUMP:setPump();return;
            case RECIPE: setRecipe();return;
            default: error();return;
        }
    }

    private void setTopic(){
        Log.i(TAG, "setTopic: "+id.toString());
        topic = Topic.getTopic(id);
        binding.textViewTitle.setText(topic.getName());
        binding.textViewDescription.setText(topic.getDescription());
        binding.textViewDescription.setVisibility(View.VISIBLE);
    }

    private void setPump(){
        Log.i(TAG, "setPump: "+id.toString());
        pump = Pump.getPump(id);
        if(pump != null) {
            binding.textViewTitle.setText(
                    new StringBuilder()
                            .append("Pumpe: ")
                            .append(pump.getIngredientName())
                            .toString());
            binding.includePump.getRoot().setVisibility(View.VISIBLE);
            binding.includePump.textViewMinPumpVolume.setText(pump.getMinimumPumpVolume());
            binding.includePump.textViewPumpVolume.setText(pump.getVolume());
            binding.includePump.textViewPumpIngredientName.setText(pump.getIngredientName());
            if (pump.isAvailable()) {
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            } else {
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }
        }else{
            error();
        }
    }

    private void setIngredient(){
        Log.i(TAG, "setIngredient: "+id.toString());
        ingredient = Ingredient.getIngredient(id);
        if (ingredient != null) {
            binding.textViewTitle.setText(ingredient.getName());
            if(ingredient.isAlcoholic()){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }if(ingredient.isAvailable()){
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            }else{
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }
            if(AdminRights.isAdmin()){
                binding.includeIngredientAdmin.getRoot().setVisibility(View.VISIBLE);
                binding.includeIngredientAdmin.textViewIngredientVolume.setText(ingredient.getVolume());
                binding.includeIngredientAdmin.getRoot().setOnClickListener(v -> {
                    Bundle b = new Bundle();
                    b.putString("Type","PUMP");
                    b.putLong("ID", ingredient.getPumpId());
                    NavHostFragment.findNavController(ModelFragment.this)
                            .navigate(R.id.action_modelFragment_self, b);
                });
            }
            ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter ingredientListAdapter =
                    new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe.getID());
            binding.includeIngredients.textViewNameListTitle.setText("Zutaten");
            binding.includeIngredients.recylerViewNames.setLayoutManager(ingredientListAdapter.getManager(this.getContext()));
            binding.includeIngredients.recylerViewNames.setAdapter(ingredientListAdapter);
            binding.includeIngredients.getRoot().setVisibility(View.VISIBLE);

            ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter topicListAdapter =
                    new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe.getID());
            binding.includeTopics.textViewNameListTitle.setText("Servierempfehlungen");
            binding.includeTopics.recylerViewNames.setLayoutManager(topicListAdapter.getManager(this.getContext()));
            binding.includeTopics.recylerViewNames.setAdapter(topicListAdapter);
            binding.includeTopics.getRoot().setVisibility(View.VISIBLE);

        }else{
            error();
        }

    }

    private void setRecipe(){
        Log.i(TAG, "setRecipe: "+id.toString());
        recipe = Recipe.getRecipe(id);
        if(recipe != null){
            binding.textViewTitle.setText(recipe.getName());
            if(recipe.isAlcoholic()){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }if(recipe.isAvailable()){
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            }else{
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }

            binding.includeIngredients.textViewNameListTitle.setText("Zutaten");

            ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter ingredientAdapter
                    = new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe);

            ListLayout.set(binding.includeIngredients.textViewNameListTitle,
                    "Zutaten",
                    binding.includeIngredients.recylerViewNames,
                    this.getContext(),
                    ingredientAdapter,
                    this,
                    binding.includeIngredients.imageButtonListDelete,
                    binding.includeIngredients.imageButtonListEdit);
            binding.includeIngredients.getRoot().setVisibility(View.VISIBLE);

            ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter topicAdapter
                    = new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe);

            ListLayout.set(binding.includeTopics.textViewNameListTitle,
                    "Serviervorschläge",
                    binding.includeTopics.recylerViewNames,
                    this.getContext(),
                    topicAdapter,
                    this,
                    binding.includeTopics.imageButtonListDelete,
                    binding.includeTopics.imageButtonListEdit);
            binding.includeTopics.getRoot().setVisibility(View.VISIBLE);

            




        }else{
            error();
        }
    }


    private void error(){
        Log.i(TAG, "error");
        NavHostFragment.findNavController(ModelFragment.this)
                .navigate(R.id.action_modelFragment_to_mainActivity);
        return;
    }

    private void setFAB(){
        activity.setFAB(v -> {
            Bundle b = new Bundle();
            b.putString("Type", type.name());
            b.putLong("ID", id);

            NavHostFragment
                    .findNavController(ModelFragment.this)
                    .navigate(R.id.action_modelFragment_to_editModelFragment,
                            b);
        }, R.drawable.ic_edit);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentModelBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if(args != null) {
            Log.i(TAG, "onViewCreated:getArguments!=null: "+ args);
            type = ModelType.valueOf(args.getString("Type"));
            id = args.getLong("ID");
            Log.i(TAG, "onViewCreated:type: "+ type);
            Log.i(TAG, "onViewCreated:type: "+ id);
            setUP();
        }else{
            Log.i(TAG, "onViewCreated:getArguments==null ");
            error();
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onViewCreated");
        super.onStart();
        setFAB();
    }
}