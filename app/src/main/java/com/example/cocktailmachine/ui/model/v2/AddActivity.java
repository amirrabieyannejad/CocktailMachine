package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AddActivity extends BasicActivity {
    private static final String TAG = "AddActivity";

    private ActivityAddBinding binding;
    private Pump pump;
    private Topic topic;
    private Recipe recipe;
    private Ingredient ingredient;

    private HashMap<Ingredient, Integer> ingredientVolumeHashMap;
    private List<Topic> topics;

    final private Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    void preSetUp() {
        Log.i(TAG, "preSetUp");
        binding.textViewAddTitle.setVisibility(View.GONE);
        binding.editTextAddTitle.setVisibility(View.GONE);
        binding.textViewError.setVisibility(View.GONE);

        binding.editTextDescription.setVisibility(View.GONE);


        binding.subLayoutAlcohol.setVisibility(View.GONE);
        binding.switchAlcohol.setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
        //binding.includeNotAlcoholic


        binding.subLayoutColor.setVisibility(View.GONE);


        binding.subLayoutAddIngredient.setVisibility(View.GONE);
        binding.subLayoutAddTopic.setVisibility(View.GONE);

        //binding.includePump;//.getRoot().setVisibility(View.GONE);

        binding.includePump.getRoot().setVisibility(View.GONE);



    }

    @Override
    void setUpPump() {
        Log.i(TAG, "setUpPump");

        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Pumpe");

        //maybe needed
        pump = Pump.getPump(this.getID());

        binding.includePump.getRoot().setVisibility(View.VISIBLE);
        binding.includePump.editTextNumberSearchIngredientVol.setVisibility(View.VISIBLE);
        search();

        binding.buttonSave.setOnClickListener(v -> {
            Log.i(TAG, "setUpPump: buttonSave clicked");
            if(binding.includePump.editTextSearchIngredientIng.getText().toString().length()>0) {
                Log.i(TAG, "setUpPump: buttonSave has title");
                getIngredient();
            }else{
                Log.i(TAG, "setUpPump: buttonSave has no title -> toast & stop");
                Toast.makeText(AddActivity.this, "Nenne die Zutat!", Toast.LENGTH_SHORT).show();
                return;
            }
            AddActivity.this.pump.setCurrentIngredient(this.activity, AddActivity.this.ingredient);
            String vol = binding.includePump.editTextNumberSearchIngredientVol.getText().toString();
            if(vol.length()==0){
                Log.i(TAG, "setUpPump: buttonSave has no vol -> toast & stop");
                Toast.makeText(AddActivity.this, "Gib das Volumen an!", Toast.LENGTH_SHORT).show();
                return;
            }
            int level = -1;
            try {
                level = Integer.parseInt(vol);
                Log.i(TAG, "setUpPump: buttonSave parsed vol");
            }catch (NumberFormatException e){
                Log.i(TAG, "setUpPump: buttonSave error parsing vol -> toast & stop");
                Toast.makeText(AddActivity.this, "Gib eine ganze Zahl für das Volumen an!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                AddActivity.this.pump.fill(level);
            } catch (MissingIngredientPumpException e) {
                Log.e(TAG, "setUpPump: buttonSave error missing ingredient -> toast & stop");
                Log.e(TAG, "setUpPump: buttonSave error missing ingredient: "+e.getMessage());
                e.printStackTrace();
                Toast.makeText(AddActivity.this, "Nochmal!", Toast.LENGTH_SHORT).show();
                return;
            }
            AddActivity.this.pump.sendSave(AddActivity.this);
            GetActivity.goToDisplay(AddActivity.this, FragmentType.Model, ModelType.PUMP, AddActivity.this.pump.getID());
            Log.i(TAG, "setUpPump:done");
        });
    }

    /**
     * set up topic
     */
    @Override
    void setUpTopic() {
        Log.i(TAG, "setUpTopic");
        String title_tag = "Serviervorschlag";

        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.editTextDescription.setVisibility(View.VISIBLE);

        binding.textViewAddTitle.setText(title_tag);

        topic = Topic.getTopic(this.getID());
        if(topic == null){
            Log.i(TAG, "setUpPump: new topic");
            binding.buttonSave.setOnClickListener(v -> {
                Log.i(TAG, "setUpPump: buttonSave: clicked");
                topic = Topic.makeNew(binding.editTextAddTitle.getText().toString(), binding.editTextDescription.getText().toString());
                topic.save(activity);
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.TOPIC, topic.getID());
                Log.i(TAG, "setUpPump: done");
            });

        }else{
            Log.i(TAG, "setUpPump: buttonSave: old topic");
            binding.editTextAddTitle.setText(topic.getName());
            binding.editTextDescription.setText(topic.getDescription());
            binding.buttonSave.setOnClickListener(v -> {
                Log.i(TAG, "setUpPump: buttonSave: clicked");
                topic.setName(binding.editTextAddTitle.getText().toString());
                topic.setDescription(binding.editTextDescription.getText().toString());
                topic.save(activity);
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.TOPIC, topic.getID());
                Log.i(TAG, "setUpPump: done");
            });
        }



    }

    /**
     * setup ingredient
     */
    @Override
    void setUpIngredient() {
        Log.i(TAG, "setUpIngredient");
        ingredient = Ingredient.getIngredient(this.getID());
        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.subLayoutAlcohol.setVisibility(View.VISIBLE);
        binding.switchAlcohol.setVisibility(View.VISIBLE);
        binding.subLayoutColor.setVisibility(View.VISIBLE);

        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);

        //saving instances
        final int[] set_color = {new Random().nextInt()};

        binding.textViewAddTitle.setText("Zutat");
        binding.switchAlcohol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
                binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
            }else{
                binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
                binding.includeNotAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }
        });

        binding.subLayoutColor.setClickable(true);
        binding.subLayoutColor.setOnClickListener(v -> {
            Log.i(TAG, "setUpIngredient: color picker clicked");
            ColorPickerPopUp colorPickerPopUp = new ColorPickerPopUp(activity);	// Pass the context.
            colorPickerPopUp.setShowAlpha(true)			// By default show alpha is true.
                    .setDefaultColor(ingredient.getColor())
                    .setDialogTitle("Wähle eine Farbe!")
                    .setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                        @Override
                        public void onColorPicked(int color) {
                            // handle the use of color
                            set_color[0] = color;
                            binding.imageViewColorShow.setColorFilter(color);
                            Log.i(TAG, "setUpIngredient: color picked");
                        }

                        @Override
                        public void onCancel() {
                            Log.i(TAG, "setUpIngredient: color picker canceled");
                            colorPickerPopUp.dismissDialog();	// Dismiss the dialog.
                        }
                    })
                    .show();
        });


        if(ingredient == null){
            Log.i(TAG, "setUpIngredient: new ingredient");
            binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.buttonSave.setOnClickListener(v -> {
                Log.i(TAG, "setUpIngredient:buttonSave: clicked");
                ingredient = Ingredient.makeNew(
                        binding.editTextAddTitle.getText().toString(),
                        binding.switchAlcohol.isChecked(),
                        set_color[0]
                );
                ingredient.save(activity);
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.INGREDIENT, ingredient.getID());
            });

        }else{
            Log.i(TAG, "setUpIngredient: old ingredient");
            set_color[0] = ingredient.getColor();
            binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.editTextAddTitle.setText(ingredient.getName());
            binding.switchAlcohol.setChecked(ingredient.isAlcoholic());

            binding.buttonSave.setOnClickListener(v -> {
                Log.i(TAG, "setUpIngredient:buttonSave: clicked");
                ingredient.setName(binding.editTextAddTitle.getText().toString());
                ingredient.setAlcoholic(binding.switchAlcohol.isChecked());
                ingredient.setColor(set_color[0]);
                ingredient.save(activity);
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.INGREDIENT, ingredient.getID());
            });
        }
    }

    /**
     * set up recipe
     */
    @Override
    void setUpRecipe() {
        Log.i(TAG, "setUpRecipe");
        this.recipe = Recipe.getRecipe(this.getID());
        if(this.getID()==-1 || this.recipe == null){
            this.recipe = Recipe.makeNew("temp");
            this.ingredientVolumeHashMap = new HashMap<>();
            this.topics = new ArrayList<>();
        } else {
            binding.editTextAddTitle.setText(this.recipe.getName());
            this.ingredientVolumeHashMap = this.recipe.getIngredientToVolume();
            this.topics = this.recipe.getTopics();
        }


        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Rezept");

        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.editTextAddTitle.setHint("Name des Cocktails");

        binding.subLayoutAddIngredient.setVisibility(View.VISIBLE);
        View.OnClickListener ingAdd = v ->{
            Log.i(TAG, "subLayoutAddIngredient: clicked");
            GetDialog.getIngVol(activity,
                    !AdminRights.isAdmin(),
                    new GetDialog.IngredientVolumeSaver() {
                        private Ingredient ing;
                        private int vol;
                        private String tippedName;
                        @Override
                        public void save(Ingredient ingredient, String tippedName) {
                            this.ing = ingredient;
                            this.tippedName = tippedName;
                            Log.i(TAG,"IngredientVolumeSaver: save: "+this.ing.toString() +this.tippedName);
                        }

                        @Override
                        public void save(Integer volume) {
                            this.vol = volume;
                            Log.i(TAG,"IngredientVolumeSaver: save: "+this.vol);
                        }

                        @Override
                        public void post() {
                            Log.i(TAG,"IngredientVolumeSaver: post: ");
                            AddActivity.this.ingredientVolumeHashMap.put(ing, vol);
                            Log.i(TAG, "subLayoutAddIngredient: ing vol added");
                            Log.i(TAG, AddActivity.this.ingredientVolumeHashMap.toString());
                            AddActivity.this.updateIngredients();
                            Log.i(TAG, "subLayoutAddIngredient: updateIngredients");
                        }
                    });

        };
        binding.subLayoutAddIngredientAdd.setOnClickListener(ingAdd);
        binding.ButtonAddIngredient.setOnClickListener(ingAdd);
        updateIngredients();


        binding.subLayoutAddTopic.setVisibility(View.VISIBLE);
        View.OnClickListener topAdd =v ->{
            Log.i(TAG, "subLayoutAddTopic: clicked");
            GetDialog.addTopic(activity,
                    (t, d) -> {
                        AddActivity.this.topics.add(t);
                        Log.i(TAG, "subLayoutAddTopic: topic added");
                        d.dismiss();
                        Log.i(TAG, "subLayoutAddTopic: dialog dimiss");
                        AddActivity.this.updateTopics();
                        Log.i(TAG, "subLayoutAddTopic: updateTopics");
                    });
        };
        binding.subLayoutAddTopicAdd.setOnClickListener(topAdd);
        binding.ButtonAddTopic.setOnClickListener(topAdd);
        updateTopics();


        //save
        binding.buttonSave.setOnClickListener(v -> {
            Log.i(TAG, "buttonSave: clicked");
            Log.i(TAG, "ingvol "+AddActivity.this.ingredientVolumeHashMap.toString());
            Log.i(TAG, "topics "+AddActivity.this.topics.toString());
            AddActivity.this.recipe.setName(activity, binding.editTextAddTitle.getText().toString());
            AddActivity.this.recipe.save(activity);
            AddActivity.this.recipe.replaceIngredients(activity,
                    AddActivity.this.ingredientVolumeHashMap);
            AddActivity.this.recipe.replaceTopics(activity,
                    AddActivity.this.topics);
            if(AddActivity.this.recipe.sendSave(
                    activity)){
                Log.i(TAG, "buttonSave: show recipe");
                GetActivity.goToDisplay(
                        activity,
                        FragmentType.Model,
                        ModelType.RECIPE,
                        AddActivity.this.recipe.getID());
            }else {
                Log.i(TAG, "buttonSave: saving or sending failed -> toast \"nochmal\"");
                Toast.makeText(activity, "Speicher nochmal!", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "buttonSave: done");
        });
    }

    @Override
    void postSetUp() {
        Log.i(TAG, "postSetUp");


        //for all
        binding.buttonStop.setOnClickListener(v -> GetActivity.goBack(activity));
        binding.textViewAddTitle.setOnClickListener(v -> AddActivity.this.reload());

    }

    @Override
    public void reload() {
    }

    /**
     * Fehler
     * @param msg Error Message
     */
    void error(String msg){
        Log.i(TAG, "error");
        preSetUp();
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Fehler!");
        if(msg != null) {
            binding.textViewError.setVisibility(View.VISIBLE);
            binding.textViewError.setText(msg);
        }
        binding.buttonSave.setVisibility(View.GONE);
    }






    //no Name Twice Policie

    /*
     *
     * @author Johanna Reidt
     * @return
     */
    private boolean checkName(){
        //TODO: ????

        return true;
    }














    //pump helper

    private void search(){
        Log.i(TAG, "search");
        binding.includePump.editTextSearchIngredientIng.setVisibility(View.VISIBLE);
        binding.includePump.imageButtonSearchIngredientDone.setVisibility(View.VISIBLE);
        binding.includePump.textViewSearchIngredientIng.setVisibility(View.GONE);

        binding.includePump.imageButtonSearchIngredientDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.includePump.editTextSearchIngredientIng.getText().toString().length()>0) {
                    searchDone();
                }else{
                    Toast.makeText(AddActivity.this, "Nenne die Zutat!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    private void getIngredient(){
        Log.i(TAG, "getIngredient");
        this.ingredient = Ingredient.searchOrNew(activity,
                binding.includePump.editTextSearchIngredientIng.getText().toString());
    }
    private void searchDone(){
        Log.i(TAG, "searchDone");
        getIngredient();
        binding.includePump.editTextSearchIngredientIng.setVisibility(View.GONE);
        binding.includePump.imageButtonSearchIngredientDone.setVisibility(View.GONE);
        binding.includePump.textViewSearchIngredientIng.setVisibility(View.VISIBLE);
        binding.includePump.textViewSearchIngredientIng.setText(this.ingredient.getName());
        binding.includePump.textViewSearchIngredientIng.setOnClickListener(v -> search());


    }














    //recipe helper

    private boolean isAlcoholic(){
        Log.i(TAG, "isAlcoholic");
        boolean isAlcoholic = false;
        for(Ingredient i:this.ingredientVolumeHashMap.keySet()){
            if(i == null){
                Log.i(TAG, "missing ingredients in ingredientVolumeHashMap");
            }
            else {
                isAlcoholic = isAlcoholic || i.isAlcoholic();
            }
        }
        return isAlcoholic;
    }

    private void setAlcoholic(){
        Log.i(TAG, "setAlcoholic");
        if (this.ingredientVolumeHashMap.size() == 0) {
            binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
            binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
        }
        if(isAlcoholic()){
            binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
            binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
        }else{
            binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
            binding.includeNotAlcoholic.getRoot().setVisibility(View.VISIBLE);
        }
    }

    /**
     * get a new vertical LinearLayoutManager
     * @return LinearLayoutManager
     */
    private LinearLayoutManager getNewLinearLayoutManager(){
        Log.i(TAG, "getNewLinearLayoutManager");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        return llm;
    }

    /**
     * is supposed to reload the current ingredient list
     */
    private void updateIngredients(){
        Log.i(TAG, "updateIngredients");
        if(ingredientVolumeHashMap.size()>0) {
            Log.i(TAG, "updateIngredients size> 0");
            Log.i(TAG, AddActivity.this.ingredientVolumeHashMap.toString());
            binding.recyclerViewIngredients.setVisibility(View.VISIBLE);
            binding.recyclerViewIngredients.setLayoutManager(getNewLinearLayoutManager());
            binding.recyclerViewIngredients.setAdapter(new IngredientVolAdapter());
        }else{
            Log.i(TAG, "updateIngredients size<= 0");
            binding.recyclerViewIngredients.setVisibility(View.GONE);
        }
        setAlcoholic();

    }

    /**
     * is supposed to reload the current topic list
     */
    private void updateTopics() {
        Log.i(TAG, "updateTopics");
        if(topics.size()>0) {
            binding.recyclerViewTopics.setVisibility(View.VISIBLE);
            binding.recyclerViewTopics.setLayoutManager(getNewLinearLayoutManager());
            binding.recyclerViewTopics.setAdapter(new TopicAdapter());
        }else{
            binding.recyclerViewTopics.setVisibility(View.GONE);
        }
    }

    /**
     * basic string view, ergo row element of topic and ingredient diplay
     */
    private class StringView extends RecyclerView.ViewHolder {
        //for layout item_little_title
        private final TextView txt;

        public StringView(@NonNull View itemView) {
            super(itemView);
            Log.i(TAG, "StringView");
            txt = itemView.findViewById(R.id.textView_item_little_title);
        }

        private void setTxt(@NonNull Ingredient ingredient, int volume){
            Log.i(TAG, "StringView: setTxt ingredient");
            this.txt.setText(String.format("%s: %s", ingredient.getName(), volume));
            this.txt.setOnLongClickListener(v -> {
                Log.i(TAG, "StringView: setTxt ingredient clicked");
                GetDialog.deleteAddElement(AddActivity.this.activity, "die Zutat "+ingredient.getName() ,new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG,"StringView: setTxt choose to delete");
                        AddActivity.this.ingredientVolumeHashMap.remove(ingredient);
                        Log.i(TAG,"StringView: setTxt remove from hashmap");
                        AddActivity.this.updateIngredients();
                        Log.i(TAG,"StringView: setTxt updateIngredients");
                    }
                });
                return true;
            });
        }

        private void setTxt(@NonNull Topic topic){
            Log.i(TAG, "StringView: setTxt topic");
            this.txt.setText(topic.getName());
            this.txt.setOnLongClickListener(v -> {
                Log.i(TAG, "StringView: setTxt topic clicked");
                GetDialog.deleteAddElement(AddActivity.this.activity, "den Serviervorschlag "+topic.getName() ,new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG,"StringView: setTxt choose to delete");
                        AddActivity.this.topics.remove(topic);
                        Log.i(TAG,"StringView: setTxt remove from list");
                        AddActivity.this.updateTopics();
                        Log.i(TAG,"StringView: setTxt updateTopics");
                    }
                });
                return true;
            });
        }

    }

    /**
     * topic row
     */
    private class TopicAdapter extends RecyclerView.Adapter<StringView>{


        @NonNull
        @Override
        public StringView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i(TAG, "TopicAdapter: onCreateViewHolder");
            return new StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StringView holder, int position) {
            Log.i(TAG, "TopicAdapter: onBindViewHolder");
            holder.setTxt(topics.get(position));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "TopicAdapter: getItemCount");
            return topics.size();
        }
    }

    /**
     * ingredient row
     */
    private class IngredientVolAdapter extends RecyclerView.Adapter<StringView>{


        @NonNull
        @Override
        public StringView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i(TAG, "IngredientVolAdapter: onCreateViewHolder");
            return new StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StringView holder, int position) {
            Log.i(TAG, "IngredientVolAdapter: onBindViewHolder");
            Ingredient i = ingredientVolumeHashMap.keySet().toArray(new Ingredient[]{})[position];
            if(i== null){
                Log.e(TAG, "IngredientVolAdapter:onBindViewHolder getting ingredient failed");
                return;
            }
            Integer vol = ingredientVolumeHashMap.get(i);
            if(vol == null){
                Log.e(TAG, "IngredientVolAdapter:onBindViewHolder getting vol failed");
                vol = -1;
            }
            holder.setTxt(i, vol);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "IngredientVolAdapter: getItemCount"+ingredientVolumeHashMap.size());
            return ingredientVolumeHashMap.size();
        }
    }


}