package com.example.cocktailmachine.ui.model;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

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
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class EditModelFragment extends Fragment {
    private FragmentEditModelBinding binding;
    private MainActivity activity;
    private static final String TAG = "EditModelFragment";

    //data
    private Recipe recipe = null;
    private Topic topic = null;
    private Ingredient ingredient = null;
    private Pump pump = null;

    private boolean saved=false;
    private boolean old=false;
    private Bundle b;

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
                pump = Pump.makeNew();
                pump.setCurrentIngredient(ingredient);
                pump.fill(Integer.parseInt(
                        binding
                                .includeNewPump
                                .includeNewPumpFillEmpty
                                .editTextPumpFillEmptyVolume
                                .getText().toString()));
                setUpEditPump();
                return;
            case TOPIC:
                topic = Topic.makeNew(
                        binding
                                .includeName
                                .editTextEditText
                                .getText().toString(),
                        binding
                                .editTextTopic
                                .getText()
                                .toString());
                return;
            case RECIPE:
                recipe = Recipe.makeNew(
                        binding
                                .includeName
                                .editTextEditText
                                .getText().toString());
                setUpEditRecipe();
                return;
            case INGREDIENT:
                ingredient = Ingredient.makeNew(
                        binding
                                .includeName
                                .editTextEditText
                                .getText().toString());
                setUpEditIngredient();
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
        binding.includeName.textViewEditText.setText("Zutat");
        binding.includeName.editTextEditText.setHint("bspw. Tequila");
        /*
        binding.layoutIngredient.setVisibility(View.VISIBLE);
        binding.layoutPickColor.setOnClickListener(v -> {
            ColorPickerPopUp colorPickerPopUp = new ColorPickerPopUp(activity);	// Pass the context.
            colorPickerPopUp.setShowAlpha(true)			// By default show alpha is true.
                    .setDefaultColor(Color.GREEN)
                    .setDialogTitle("Wähle eine Farbe!")
                    .setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                        @Override
                        public void onColorPicked(int color) {
                            //handle the use of color
                            saveNew();
                            ingredient.setColor(color);
                            binding.imageButtonGlassColor.setBackgroundColor(color);
                        }

                        @Override
                        public void onCancel() {
                            colorPickerPopUp.dismissDialog();	// Dismiss the dialog.
                        }
                    })
                    .show();
        });
        binding.checkBoxAlcoholic.setChecked(false);

         */
    }

    private void setUpNewRecipe() {
        binding.includeName.textViewEditText.setText("Rezept");
        binding.includeName.editTextEditText.setHint("bspw. Magarita");
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
        //TODO
    }

    private void setUpNewTopic() {
        binding.includeName.textViewEditText.setText("Serviervorschlag");
        binding.includeName.editTextEditText.setHint("bspw. Limettensirup");
        binding.editTextTopic.setVisibility(View.VISIBLE);
    }

    private void setUpNewPump(){
        binding.includeNewPump.getRoot().setVisibility(View.VISIBLE);
        binding.includeNewPump.includeNewPumpFillEmpty.imageButtonEmpty.setOnClickListener(v -> {
            binding.includeNewPump.includeNewPumpFillEmpty.editTextPumpFillEmptyVolume.setText(null);
        });
        final List<Ingredient> ingredients;
        String[] ingredientNames = new String[0];
        ingredients = Ingredient.getIngredientWithIds();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientNames = ingredients
                    .stream()
                    .map(Ingredient::getName)
                    .toArray(String[]::new);
        }else{
            List<String> names = new ArrayList<>();
            for(Ingredient ingredient: ingredients){
                names.add(ingredient.getName());
            }
            ingredientNames = names.toArray(new String[0]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,//);
                //this,
                android.R.layout.simple_spinner_item,
                ingredientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeNewPump.spinnerNewPump.setAdapter(adapter);
        binding.includeNewPump.spinnerNewPump.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ingredient = ingredients.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setUpEdit(Long id){
        switch (type){
            case PUMP:
                pump = Pump.getPump(id);
                setUpEditPump();
                return;
            case TOPIC:
                topic = Topic.getTopic(id);
                setUpEditTopic();return;
            case RECIPE:
                recipe = Recipe.getRecipe(id);
                setUpEditRecipe();
                return;
            case INGREDIENT:
                ingredient = Ingredient.getIngredient(id);
                setUpEditIngredient();
                return;
        }
    }

    private void setUpEditIngredient() {
        binding.includeName.textViewEditText.setText("Zutat");
        binding.includeName.editTextEditText.setHint("bspw. Tequila");
        binding.includeName.editTextEditText.setText(ingredient.getName());
        binding.includeName.editTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
                setSaveFAB();
                ingredient.setName(s.toString());
            }
        });
        binding.layoutIngredient.setVisibility(View.VISIBLE);
        binding.layoutPickColor.setOnClickListener(v -> {
            ColorPickerPopUp colorPickerPopUp = new ColorPickerPopUp(activity);	// Pass the context.
            colorPickerPopUp.setShowAlpha(true)			// By default show alpha is true.
                    .setDefaultColor(ingredient.getColor())
                    .setDialogTitle("Wähle eine Farbe!")
                    .setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                        @Override
                        public void onColorPicked(int color) {
                            // handle the use of color
                            ingredient.setColor(color);
                            binding.imageButtonGlassColor.setBackgroundColor(color);

                            saved = false;
                            setSaveFAB();
                        }

                        @Override
                        public void onCancel() {
                            colorPickerPopUp.dismissDialog();	// Dismiss the dialog.
                        }
                    })
                    .show();
        });
        binding.checkBoxAlcoholic.setChecked(ingredient.isAlcoholic());
        binding.checkBoxAlcoholic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saved = false;
            setSaveFAB();
            ingredient.setAlcoholic(true);
        });
    }

    private void setUpEditRecipe() {
        //TODO

        binding.includeName.textViewEditText.setText("Rezept");
        binding.includeName.editTextEditText.setHint("bspw. Magarita");
        binding.includeName.editTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
                setSaveFAB();
                recipe.setName(s.toString());
            }
        });
        binding.layoutRecipe.setVisibility(View.VISIBLE
        );
    }

    private void setUpEditTopic() {
        binding.includeName.textViewEditText.setText("Serviervorschlag");
        binding.includeName.editTextEditText.setHint("bspw. Limettensirup");
        binding.includeName.editTextEditText.setText(topic.getName());
        binding.includeName.editTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
                setSaveFAB();
                topic.setName(s.toString());
            }
        });
        binding.editTextTopic.setText(topic.getDescription());
        binding.editTextTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
                setSaveFAB();
                topic.setDescription(s.toString());
            }
        });
        binding.editTextTopic.setVisibility(View.VISIBLE);
    }

    private void setUpEditPump() {
        binding.includeNewPump.getRoot().setVisibility(View.VISIBLE);
        binding.includeNewPump.includeNewPumpFillEmpty.editTextPumpFillEmptyVolume.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saved = false;
                setSaveFAB();
                pump.fill(Integer.parseInt(s.toString()));
            }
        });
        binding.includeNewPump.includeNewPumpFillEmpty.imageButtonEmpty.setOnClickListener(v -> {
            binding.includeNewPump.includeNewPumpFillEmpty.editTextPumpFillEmptyVolume.setText(null);
            saved = false;
            setSaveFAB();
            pump.empty();
        });
        final List<Ingredient> ingredients;
        String[] ingredientNames;
        ingredients = Ingredient.getIngredientWithIds();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientNames = ingredients
                    .stream()
                    .map(Ingredient::getName)
                    .toArray(String[]::new);
        }else{
            List<String> names = new ArrayList<>();
            for(Ingredient ingredient: ingredients){
                names.add(ingredient.getName());
            }
            ingredientNames = names.toArray(new String[0]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,//);
                //this,
                android.R.layout.simple_spinner_item,
                ingredientNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeNewPump.spinnerNewPump.setAdapter(adapter);
        binding.includeNewPump.spinnerNewPump.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pump.setCurrentIngredient(ingredients.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(pump.getCurrentIngredient()!= null) {
            int position = 0;
            String in_name = pump.getIngredientName();
            for(int i = 0; i< ingredientNames.length; i++){
                if(Objects.equals(in_name, ingredientNames[i])){
                    position = i;
                }
            }
            binding.includeNewPump.spinnerNewPump.setSelection(position);
        }
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
        setSaveFAB();
    }
}