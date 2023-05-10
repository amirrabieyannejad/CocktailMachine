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
import com.example.cocktailmachine.ui.SecondFragment;

class ModelFragment extends Fragment {
    private FragmentModelBinding binding;
    private static final String TAG = "ModelFragment";

    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;
    private Recipe recipe;



    private void setUP(String type, Long id){
        binding.textViewDescription.setVisibility(View.GONE);
        binding.includeAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includePump.getRoot().setVisibility(View.GONE);
        binding.includeIngredientAdmin.getRoot().setVisibility(View.GONE);
        switch (type){
            case "TOPIC":setTopic(id);
            case "INGREDIENT":setIngredient(id);
            case "PUMP":setPump(id);
            case "RECIPE": setRecipe(id);
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
                            .append(pump.getID())
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
            }

        }else{
            error();
        }

    }

    private void setRecipe(Long id){
        recipe = Recipe.getRecipe(id);
        if(recipe != null){

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