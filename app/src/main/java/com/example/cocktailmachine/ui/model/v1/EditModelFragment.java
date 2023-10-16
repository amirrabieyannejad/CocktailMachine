package com.example.cocktailmachine.ui.model.v1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.databinding.FragmentEditModelBinding;
import com.example.cocktailmachine.ui.model.ModelType;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditModelFragment extends Fragment {
    private FragmentEditModelBinding binding;
    private ModelActivity activity;
    private static final String TAG = "EditModelFragment";

    //data
    private Recipe recipe = null;
    private Topic topic = null;
    private Ingredient ingredient = null;
    private Pump pump = null;

    private boolean saved=false;
    private boolean old=false;
    private Bundle b;

    private ModelType type;

    public EditModelFragment(){}

    private void setGoToFAB(){
        Log.i(TAG,"setGoToFAB Bundle"+b);
        activity.setFAB(
                v -> NavHostFragment
                        .findNavController(EditModelFragment.this)
                        .navigate(R.id.action_editModelFragment_to_modelFragment,
                                b),
                R.drawable.ic_check);
    }

    private void saveNew(){
        Log.i(TAG, "saveNew");
        b = new Bundle();
        old = true;
        saved = true;
        switch (type) {
                case PUMP:
                    pump = Pump.makeNew();
                    pump.setCurrentIngredient(activity, ingredient);
                    try {
                        pump.fill(Integer.parseInt(
                                binding
                                        .includeNewPump
                                        .includeNewPumpFillEmpty
                                        .editTextPumpFillEmptyVolume
                                        .getText().toString()));
                    } catch (MissingIngredientPumpException e) {
                        Log.getStackTraceString(e);
                        Log.i(TAG, "saveNew: pump filling failed: pump: "+pump);
                    }
                    setUpEditPump();
                    pump.save(activity);
                    saved = true;
                    pump.sendSave(activity);
                    b.putString("Type", ModelType.PUMP.name());
                    b.putLong("ID", pump.getID());
                    Log.i(TAG,"savenew"+b);
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
                    setUpEditTopic();
                    topic.save(activity);
                    b.putString("Type", ModelType.TOPIC.name());
                    b.putLong("ID", topic.getID());
                    Log.i(TAG,"savenew"+b);
                    return;
                case RECIPE:
                    recipe = Recipe.makeNew(
                            binding
                                    .includeName
                                    .editTextEditText
                                    .getText().toString());
                    setUpEditRecipe();
                    saved =true;
                    recipe.save(activity);
                    recipe.sendSave(activity);
                    b.putString("Type", ModelType.RECIPE.name());
                    b.putLong("ID", recipe.getID());
                    Log.i(TAG,"savenew"+b);
                    return;
                case INGREDIENT:
                    ingredient = Ingredient.makeNew(
                            binding
                                    .includeName
                                    .editTextEditText
                                    .getText().toString());
                    setUpEditIngredient();
                    ingredient.save(activity);
                    saved =true;
                    b.putString("Type", ModelType.INGREDIENT.name());
                    b.putLong("ID", ingredient.getID());
                    Log.i(TAG,"savenew"+b);
        }
    }

    private void save(){
        Log.i(TAG, "save");
        b = new Bundle();
        saved = true;

        switch (type){
                case INGREDIENT:
                    ingredient.setAlcoholic(binding.checkBoxAlcoholic.isChecked());
                    ingredient.save(activity);
                    b.putString("Type", ModelType.INGREDIENT.name());
                    b.putLong("ID", ingredient.getID());
                    Log.i(TAG,"saved "+b);
                    return;
                case RECIPE:
                    recipe.save(activity);
                    recipe.sendSave(activity);
                    b.putString("Type", ModelType.RECIPE.name());
                    b.putLong("ID", recipe.getID());
                    Log.i(TAG,"saved "+b);
                    return;
                case TOPIC:
                    topic.save(activity);
                    b.putString("Type", ModelType.TOPIC.name());
                    b.putLong("ID", topic.getID());
                    Log.i(TAG,"saved "+b);
                    return;
                case PUMP:
                    try {
                        String vol = binding
                                .includeNewPump
                                .includeNewPumpFillEmpty
                                .editTextPumpFillEmptyVolume
                                .getText().toString();
                        if(!vol.equals("")) {
                            pump.fill(Integer.parseInt(vol));
                        }

                        pump.sendRefill(this.activity);
                    } catch (MissingIngredientPumpException e) {
                        Log.getStackTraceString(e);
                        Log.i(TAG, "save: pump filling failed: pump: "+pump);
                    }
                    pump.save(activity);
                    saved =true;
                    pump.sendSave(activity);
                    b.putString("Type", ModelType.PUMP.name());
                    b.putLong("ID", pump.getID());
                    Log.i(TAG,"saved "+b);
        }
    }

    private void setSaveFAB(){
        Log.i(TAG,"setGoToFAB Bundle"+b);
        activity.setFAB(v -> {
            InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
            case INGREDIENT:setUpNewIngredient();
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
    }

    private void setUpNewTopic() {
        binding.includeName.textViewEditText.setText("Serviervorschlag");
        binding.includeName.editTextEditText.setHint("bspw. Limettensirup");
        binding.editTextTopic.setVisibility(View.VISIBLE);
    }

    private void setUpNewPump(){
        binding.includeName.getRoot().setVisibility(View.GONE);
        binding.includeNewPump.getRoot().setVisibility(View.VISIBLE);
        binding.includeNewPump.includeNewPumpFillEmpty.imageButtonEmpty.setOnClickListener(v -> {
            binding.includeNewPump.includeNewPumpFillEmpty.editTextPumpFillEmptyVolume.setText(null);
        });
        final List<Ingredient> ingredients;
        ArrayList<String> ingredientNames = new ArrayList<>();
        ingredients = Ingredient.getAllIngredients();
        for(Ingredient ingredient: ingredients){
            ingredientNames.add(ingredient.getName());
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
                ingredient = ingredients.get(0);
            }
        });
        ingredient = ingredients.get(0);
        binding.includeNewPump.spinnerNewPump.setSelection(0);

    }

    private void setUpEdit(Long id){
        if(id<0){
            setUpNew();
            return;
        }
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
        View.OnClickListener listener = v -> {
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
        };
        binding.layoutPickColor.setOnClickListener(listener);
        binding.imageButtonGlassColor.setOnClickListener(listener);
        binding.textViewPickAColor.setOnClickListener(listener);
        binding.checkBoxAlcoholic.setChecked(ingredient.isAlcoholic());
        binding.checkBoxAlcoholic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saved = false;
            setSaveFAB();
            ingredient.setAlcoholic(true);
        });
    }

    private void setUpEditRecipe() {
        //TODO: edit make new current design is shit


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
                recipe.setName(activity,s.toString());
            }
        });
        binding.includeName.editTextEditText.setText(recipe.getName());
        binding.layoutRecipe.setVisibility(View.VISIBLE);

        ListLayout.set(binding.includeEditRecipeTopics.includeList.textViewNameListTitle,
                "Serviervorschläge",
                binding.includeEditRecipeTopics.includeList.recylerViewNames,
                this.getContext(),
                new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe),
                EditModelFragment.this,
                binding.includeEditRecipeTopics.includeList.imageButtonListDelete,
                binding.includeEditRecipeTopics.includeList.imageButtonListEdit,
                binding.includeEditRecipeTopics.includeList.imageButtonListAdd);

        binding.includeEditRecipeIngredients.imageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Wähle neue Zutaten!");
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            List<Ingredient> ingredients = Ingredient.getAllIngredients();
            ingredients.removeAll(recipe.getIngredients());
            String[] temp = new String[ingredients.size()];
            boolean[] tempB = new boolean[ingredients.size()];
            for(int i = 0; i < ingredients.size(); i++){
                temp[i] = ingredients.get(i).getName();
                tempB[i] = false;
            }
            List<Ingredient> chosen = new ArrayList<>();
            builder.setMultiChoiceItems(
                    temp,
                    tempB,
                    (dialog, which, isChecked) -> {
                        if(isChecked){
                            chosen.add(ingredients.get(which));
                        }else{
                            chosen.remove(ingredients.get(which));
                        }
                    });
            builder.setPositiveButton("Weiter", (dialog, which) -> {
                for(Ingredient i:chosen) {
                    recipe.add(activity,i, -1);
                }
            });
            builder.show();
        });
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeEditRecipeIngredients.recyclerViewVolumeEdit.setLayoutManager(llm);
        binding.includeEditRecipeIngredients.recyclerViewVolumeEdit.setAdapter(
                new RecyclerView.Adapter<EditRowViewHolder>() {
            @NonNull
            @Override
            public EditRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.layout_volume_list, parent, false);
                return new EditRowViewHolder(view, recipe, activity);
            }

            @Override
            public void onBindViewHolder(@NonNull EditRowViewHolder holder, int position) {
                holder.setIngredient(position);
            }

            @Override
            public int getItemCount() {
                return recipe.getIngredients().size();
            }
        });
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
        Log.i(TAG, "setUpEditPump");
        binding.includeName.getRoot().setVisibility(View.GONE);
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
                try {
                    pump.fill(Integer.parseInt(s.toString()));
                } catch (MissingIngredientPumpException e) {
                    Log.getStackTraceString(e);
                    Log.i(TAG, "setUpEditPump: editTextPumpFillEmptyVolume: afterTextChanged: pump filling failed: pump: "+pump);
                }
            }
        });
        binding.includeNewPump.includeNewPumpFillEmpty.imageButtonEmpty.setOnClickListener(v -> {
            binding.includeNewPump.includeNewPumpFillEmpty.editTextPumpFillEmptyVolume.setText(null);
            saved = false;
            setSaveFAB();
            pump.empty(activity);
        });
        final List<Ingredient> ingredients;
        ArrayList<String> ingredientNames = new ArrayList<>();
        ingredients = Ingredient.getAllIngredients();
        for(Ingredient ingredient: ingredients){
            ingredientNames.add(ingredient.getName());
        }
        Log.i(TAG, ingredientNames.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this.getContext(),//);
                //this,
                android.R.layout.simple_spinner_item,
                ingredientNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.includeNewPump.spinnerNewPump.setAdapter(adapter);
        binding.includeNewPump.spinnerNewPump.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pump.setCurrentIngredient(activity,ingredients.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(pump.getCurrentIngredient()!= null) {
            String in_name = pump.getIngredientName();
            int position = ingredientNames.indexOf(in_name);
            binding.includeNewPump.spinnerNewPump.setSelection(position);
        }else{
            binding.includeNewPump.spinnerNewPump.setSelection(0);
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
        activity = (ModelActivity) getActivity();
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).setTitle("");
        }

        Bundle args = getArguments();
        /*
        if(args != null) {
            Log.i(TAG, "onViewCreated: getArguments != null");
            Log.i(TAG, "onViewCreated: getArguments"+args);
            String type = args.getString("Type");
            this.type = ModelType.valueOf(type);
            if(args.containsKey("ID")){
                Log.i(TAG, "onViewCreated: getArguments has ID -> Edit");
                Long id = args.getLong("ID");
                setUpEdit(id);
            }else{
                Log.i(TAG, "onViewCreated: getArguments has no ID -> New");
                setUpNew();
            }
        }else{
            Log.i(TAG, "onViewCreated: getArguments == null");
            error();
        }
         */
        setArgs(args);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        setSaveFAB();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if(!AdminRights.isAdmin()){
            Log.i(TAG, "onResume: GoTo List Fragment, because no admin");
            Toast.makeText(getContext(),
                    "Keine Editierung für mnicht Admins möglich!",
                    Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(EditModelFragment.this)
                    .navigate(R.id.action_modelFragment_to_listFragment);
        }
    }


    protected void setArgs(@Nullable Bundle args){
        Log.i(TAG,"setArgs");
        if(args != null) {
            Log.i(TAG, "setArgs: getArguments != null");
            Log.i(TAG, "setArgs: getArguments"+args);
            String type = args.getString("Type");
            this.type = ModelType.valueOf(type);
            if(args.containsKey("ID")){
                Log.i(TAG, "setArgs: getArguments has ID -> Edit");
                Long id = args.getLong("ID");
                setUpEdit(id);
            }else{
                Log.i(TAG, "setArgs: getArguments has no ID -> New");
                setUpNew();
            }
        }else{
            Log.i(TAG, "setArgs: getArguments == null");
            error();
        }
        Log.i(TAG,"setArgs: finished");
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        Log.i(TAG,"setArguments");
        super.setArguments(args);
        setArgs(args);
    }



    static class EditRowViewHolder extends RecyclerView.ViewHolder {
        private final Recipe recipe;
        private Ingredient ingredient;
        private int volume;
        private final TextView name;
        private final EditText vol;
        private final ModelActivity activity;

        public EditRowViewHolder(@NonNull View itemView, Recipe recipe, ModelActivity activity) {
            super(itemView);
            this.recipe = recipe;
            this.activity = activity;
            name = this.activity.findViewById(R.id.textView_edit_text);
            vol = this.activity.findViewById(R.id.editText_edit_text);

        }

        void setIngredient(int position){
            this.ingredient = recipe.getIngredients().get(position);
            this.volume = this.recipe.getVolume(ingredient);
            name.setText(ingredient.getName());
            if(volume > 0){
                this.vol.setText(String.valueOf(this.volume));
            }

            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Möchten Sie diese Zutat wirklich aus dem Rezept löschen?");
                    builder.setTitle("Warnung");
                    builder.setPositiveButton("Ja", (dialog, which) -> {
                        recipe.remove(activity,ingredient);
                        dialog.dismiss();
                    });
                    builder.setNegativeButton("Nein", (dialog, which) -> dialog.dismiss());
                    builder.show();
                    return false;
                }
            };
            name.setOnLongClickListener(longClickListener);
            vol.setOnLongClickListener(longClickListener);

        }
    }
}