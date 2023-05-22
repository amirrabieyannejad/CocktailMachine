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
import com.example.cocktailmachine.ui.SecondFragment;

class ModelFragment extends Fragment {
    private FragmentModelBinding binding;
    private static final String TAG = "ModelFragment";

    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;
    private Recipe recipe;

    protected static enum ModelType{
        TOPIC, INGREDIENT, PUMP,RECIPE
    }



    private void setUP(String type, Long id){
        binding.textViewDescription.setVisibility(View.GONE);
        binding.includeAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includePump.getRoot().setVisibility(View.GONE);
        binding.includeIngredientAdmin.getRoot().setVisibility(View.GONE);
        binding.includeIngredients.getRoot().setVisibility(View.GONE);
        binding.includeTopics.getRoot().setVisibility(View.GONE);
        switch (ModelType.valueOf(type)){
            case TOPIC:setTopic(id);
            case INGREDIENT:setIngredient(id);
            case PUMP:setPump(id);
            case RECIPE: setRecipe(id);
            default: error();
        }
    }

    private void setTopic(Long id){
        try {
            topic = Topic.getTopic(id);
            binding.textViewTitle.setText(topic.getName());
            binding.textViewDescription.setText(topic.getDescription());
            binding.textViewDescription.setVisibility(View.VISIBLE);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            error();
        }
    }

    private void setPump(Long id){
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

    private void setIngredient(Long id){
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

    private void setRecipe(Long id){
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
            




        }else{
            error();
        }
    }



    private void error(){
        NavHostFragment.findNavController(ModelFragment.this)
                .navigate(R.id.action_modelFragment_to_mainActivity);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        binding = FragmentModelBinding.inflate(inflater, container, false);
        if(savedInstanceState != null) {
            String type = savedInstanceState.getString("Type");
            Long id = savedInstanceState.getLong("ID");
            setUP(type, id);
        }else{
            error();
        }
        return binding.getRoot();
    }
}