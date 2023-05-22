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
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.databinding.FragmentEditModelBinding;
import com.example.cocktailmachine.databinding.FragmentListBinding;
import com.example.cocktailmachine.ui.MainActivity;

public class EditModelFragment extends Fragment {
    private FragmentEditModelBinding binding;
    private MainActivity activity;
    private static final String TAG = "EditModelFragment";

    //data
    private Recipe recipe;
    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;

    private ModelFragment.ModelType type;

    public EditModelFragment(){}

    private void setFAB(){
        //TODO: if change just save
        //TODO: else goto model
        activity.setFAB(v -> {
            Bundle b = new Bundle();
            if(recipe != null){
                try {
                    recipe.save();

                    b.putString("Type", ModelFragment.ModelType.RECIPE.name());
                    b.putLong("ID", recipe.getID());

                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }else if(topic != null){
                try {
                    topic.save();

                    b.putString("Type", ModelFragment.ModelType.TOPIC.name());
                    b.putLong("ID", topic.getID());
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }else if(ingredient != null){
                try {
                    ingredient.save();
                    b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                    b.putLong("ID", ingredient.getID());
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }else if(pump != null){
                try {
                    pump.save();
                    b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                    b.putLong("ID", pump.getID());
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }
            NavHostFragment
                    .findNavController(EditModelFragment.this)
                    .navigate(R.id.action_editModelFragment_to_modelFragment,
                            b);
        },
                R.drawable.ic_save);
    }

    private void setUpNew(){
        switch (type){
            case PUMP: setUpNewPump();return;
            case TOPIC:setUpNewTopic();return;
            case RECIPE:setUpNewRecipe();return;
            case INGREDIENT:setUpNewIngredient();return;
        }
    }

    private void setUpNewIngredient() {

    }

    private void setUpNewRecipe() {

    }

    private void setUpNewTopic() {

    }

    private void setUpNewPump(){
        pump = Pump.makeNew();
    }

    private void setUpEdit( Long id){
        switch (type){
            case PUMP: setUpEditPump();return;
            case TOPIC:setUpEditTopic();return;
            case RECIPE:setUpEditRecipe();return;
            case INGREDIENT:setUpEditIngredient();return;
        }
    }

    private void setUpEditIngredient() {

    }

    private void setUpEditRecipe() {

    }

    private void setUpEditTopic() {

    }

    private void setUpEditPump() {

    }


    private void error(){
        Log.i(TAG, "error");
        NavHostFragment.findNavController(EditModelFragment.this)
                .navigate(R.id.action_modelFragment_to_mainActivity);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_edit_model, container, false);

        Log.i(TAG, "onCreateView");
        binding = FragmentEditModelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();

        if(savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState != null");
            String type = savedInstanceState.getString("Type");
            this.type = ModelFragment.ModelType.valueOf(type);
            if(savedInstanceState.containsKey("ID")){
                Log.i(TAG, "savedInstanceState has ID -> Edit");
                Long id = savedInstanceState.getLong("ID");
                setUpEdit( id);
            }else{
                Log.i(TAG, "savedInstanceState has no ID -> New");
                setUpNew();
            }
        }else{
            Log.i(TAG, "savedInstanceState == null");
            error();
        }
        setFAB();
    }
}