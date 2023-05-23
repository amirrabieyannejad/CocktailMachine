package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private boolean saved=false;
    private boolean old=false;
    private Bundle b;
    private TextWatcher textWatcher;

    private ModelFragment.ModelType type;

    public EditModelFragment(){}

    private void setGoToFAB(){
        activity.setFAB(v -> NavHostFragment
                .findNavController(EditModelFragment.this)
                .navigate(R.id.action_editModelFragment_to_modelFragment,
                        b),
                R.drawable.ic_check);
    }

    private void saveNew(){
        old = true;
        switch (type){
            case PUMP:
            case TOPIC:
                topic = Topic.makeNew(
                        binding.includeName.editTextEditText.getText().toString(),
                        binding.editTextTopic.getText().toString());
                return;
            case RECIPE:
                recipe = Recipe.makeNew(
                        binding.includeName.editTextEditText.getText().toString());
                setUpEditRecipe();
                return;
            case INGREDIENT:
        }
    }

    private void save(){
        b = new Bundle();
        saved = true;
        try {
            switch (type){
                case INGREDIENT:
                    ingredient.save();
                    b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                    b.putLong("ID", ingredient.getID());
                    return;
                case RECIPE:
                    recipe.save();
                    b.putString("Type", ModelFragment.ModelType.RECIPE.name());
                    b.putLong("ID", recipe.getID());
                    return;
                case TOPIC:
                    topic.save();
                    b.putString("Type", ModelFragment.ModelType.TOPIC.name());
                    b.putLong("ID", topic.getID());
                    return;
                case PUMP:
                    pump.save();
                    b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                    b.putLong("ID", pump.getID());
                    return;
            }
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            saved = false;
            return;
        }
    }

    private void setSaveFAB(){
        activity.setFAB(v -> {
            if(old){
                save();
            }else{
                saveNew();
            }
            if(saved) {
                setGoToFAB();
            }
        }, R.drawable.ic_save);
    }

    private void setUpNew(){
        old = false;
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
        binding.includeName.textViewEditText.setText("Rezept");
        binding.includeName.editTextEditText.setHint("bspw. Magarita");
        binding.includeName.editTextEditText.addTextChangedListener(textWatcher);
        binding.layoutRecipe.setVisibility(View.GONE);
        //binding.includeEditRecipeTopics.includeList.textViewNameListTitle.setText("");
        /*
        ListLayout.set(binding.includeEditRecipeTopics.includeList.textViewNameListTitle,
                "Serviervorschläge",
                binding.includeEditRecipeTopics.includeList.recylerViewNames,
                this.getContext(),
                ListRecyclerViewAdapters.,
                EditModelFragment.this,
                binding.includeEditRecipeTopics.includeList.imageButtonListDelete,
                binding.includeEditRecipeTopics.includeList.imageButtonListEdit);
         */
    }

    private void setUpNewTopic() {
        binding.includeName.textViewEditText.setText("Serviervorschlag");
        binding.includeName.editTextEditText.setHint("bspw. Limettensirup");
        binding.includeName.editTextEditText.addTextChangedListener(textWatcher);
        binding.editTextTopic.addTextChangedListener(textWatcher);
        binding.editTextTopic.setVisibility(View.VISIBLE);
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
        binding.includeName.textViewEditText.setText("Serviervorschlag");
        binding.includeName.editTextEditText.setHint("bspw. Limettensirup");
        binding.includeName.editTextEditText.setText(topic.getName());
        binding.includeName.editTextEditText.addTextChangedListener(textWatcher);
        binding.editTextTopic.setText(topic.getDescription());
        binding.editTextTopic.addTextChangedListener(textWatcher);
        binding.editTextTopic.setVisibility(View.VISIBLE);
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

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(saved){
                    saved = false;
                    setSaveFAB();
                }
            }
        };


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
        setSaveFAB();
    }
}