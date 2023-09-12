package com.example.cocktailmachine.ui.model.v2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddActivity extends BasicActivity {
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
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseConnection.initializeSingleton(this);
    }

    @Override
    void preSetUp() {
        binding.editTextAddTitle.setVisibility(View.GONE);
        binding.switchAlcohol.setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
        binding.editTextDescription.setVisibility(View.GONE);
        binding.textViewAddTitle.setVisibility(View.GONE);
        binding.textViewError.setVisibility(View.GONE);
        binding.subLayoutAddTopic.setVisibility(View.GONE);
        binding.subLayoutAddIngredient.setVisibility(View.GONE);


    }

    @Override
    void setUpPump() {

        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Pumpe: ");

        //maybe needed
        pump = Pump.getPump(this.getID());

        //TODO
    }

    /**
     * set up topic
     */
    @Override
    void setUpTopic() {
        String title_tag = "Serviervorschlag: ";

        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.editTextDescription.setVisibility(View.VISIBLE);

        binding.textViewAddTitle.setText(title_tag);

        topic = Topic.getTopic(this.getID());
        if(topic == null){
            binding.buttonSave.setOnClickListener(v -> {
                topic = Topic.makeNew(binding.editTextAddTitle.getText().toString(), binding.editTextDescription.getText().toString());
                if(!topic.save()){
                    error("Datenbankfehler: Der Serviervorschlag konnte nicht gespeichert werden.");
                    return;
                }
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.TOPIC, topic.getID());
            });

        }else{
            binding.editTextAddTitle.setText(topic.getName());
            binding.editTextDescription.setText(topic.getDescription());
            binding.buttonSave.setOnClickListener(v -> {
                topic.setName(binding.editTextAddTitle.getText().toString());
                topic.setDescription(binding.editTextDescription.getText().toString());
                if(!topic.save()){
                    error();
                    return;
                }
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.TOPIC, topic.getID());
            });
        }



    }

    /**
     * setup ingredient
     */
    @Override
    void setUpIngredient() {
        ingredient = Ingredient.getIngredient(this.getID());
        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.switchAlcohol.setVisibility(View.VISIBLE);
        binding.subLayoutColor.setVisibility(View.VISIBLE);

        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);

        //saving instances
        final int[] set_color = {new Random().nextInt()};


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
                        }

                        @Override
                        public void onCancel() {
                            colorPickerPopUp.dismissDialog();	// Dismiss the dialog.
                        }
                    })
                    .show();
        });


        if(ingredient == null){
            binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.buttonSave.setOnClickListener(v -> {
                ingredient = Ingredient.makeNew(
                        binding.editTextAddTitle.getText().toString(),
                        binding.switchAlcohol.isChecked(),
                        set_color[0]
                );
                if(!ingredient.save()){
                    error("Datenbankfehler: Die Zutat konnte nicht gespeichert werden.");
                    return;
                }
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.INGREDIENT, ingredient.getID());
            });

        }else{
            set_color[0] = ingredient.getColor();
            binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.editTextAddTitle.setText(ingredient.getName());
            binding.switchAlcohol.setChecked(ingredient.isAlcoholic());

            binding.buttonSave.setOnClickListener(v -> {
                ingredient.setName(binding.editTextAddTitle.getText().toString());
                ingredient.setAlcoholic(binding.switchAlcohol.isChecked());
                ingredient.setColor(set_color[0]);
                if(!ingredient.save()){
                    error("Datenbankfehler: Die Zutat konnte nicht gespeichert werden.");
                    return;
                }
                GetActivity.goToDisplay(activity, FragmentType.Model, ModelType.INGREDIENT, ingredient.getID());
            });
        }
    }

    /**
     * set up recipe
     */
    @Override
    void setUpRecipe() {
        this.recipe = Recipe.getRecipe(this.getID());


        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Rezepte: ");

        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.editTextAddTitle.setHint("Name des Cocktails");

        if(this.recipe != null){
            binding.editTextAddTitle.setText(this.recipe.getName());
            this.ingredientVolumeHashMap = this.recipe.getIngredientVolumes();
            this.topics = this.recipe.getTopics();
        }else{
            this.ingredientVolumeHashMap = new HashMap<>();
            this.topics = new ArrayList<>();
        }


        binding.subLayoutAddIngredient.setVisibility(View.VISIBLE);
        binding.subLayoutAddIngredient.setOnClickListener(v ->
                GetDialog.getIngredientVolume(activity,
                    !AdminRights.isAdmin(),
                    (ingredient, tippedName, volume) -> {
                        if(ingredient == null){
                            ingredient = Ingredient.makeNew(tippedName);
                        }
                        AddActivity.this.ingredientVolumeHashMap.put(ingredient, volume);
                    }));
        updateIngredients();


        binding.subLayoutAddTopic.setVisibility(View.VISIBLE);
        binding.subLayoutAddTopic.setOnClickListener(v ->
            GetDialog.addTopic(activity,
                    topic -> AddActivity.this.topics.add(topic)));
        updateTopics();


        //save
        binding.buttonSave.setOnClickListener(v -> {
            if(AddActivity.this.recipe == null){
                AddActivity.this.recipe =
                        Recipe.makeNew(binding.editTextAddTitle.getText().toString());
            }else{
                AddActivity.this.recipe.setName(binding.editTextAddTitle.getText().toString());
            }
            AddActivity.this.recipe.addOrUpdateAndRemoveNotMentioned(
                    AddActivity.this.ingredientVolumeHashMap);
            AddActivity.this.recipe.addOrUpdateAndRemoveNotMentioned(
                    AddActivity.this.topics);
            if(AddActivity.this.recipe.sendSave(
                    activity)){
                GetActivity.goToDisplay(
                        activity,
                        FragmentType.Model,
                        ModelType.RECIPE,
                        AddActivity.this.recipe.getID());
            }else{
                Toast.makeText(activity, "Speicher nochmal!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    void postSetUp() {

        //for all
        binding.buttonStop.setOnClickListener(v -> GetActivity.goBack(activity));

    }

    /**
     * fehler
     */
    void error(){
        error(null);
    }

    /**
     * Fehler
     * @param msg
     */
    @Nullable
    void error(String msg){
        preSetUp();
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Fehler!");
        if(msg != null) {
            binding.textViewError.setVisibility(View.VISIBLE);
            binding.textViewError.setText(msg);
        }
        binding.subLayoutSave.setVisibility(View.GONE);
    }






    private boolean isAlcoholic(){
        boolean isAlcoholic = false;
        for(Ingredient i:this.ingredientVolumeHashMap.keySet()){
            isAlcoholic = isAlcoholic || i.isAlcoholic();
        }
        return isAlcoholic;
    }

    private void setAlcoholic(){
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

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        return llm;
    }

    /**
     * is supposed to reload the current ingredient list
     */
    private void updateIngredients(){
        if(ingredientVolumeHashMap.size()>0) {
            binding.recyclerViewIngredients.setVisibility(View.VISIBLE);
            binding.recyclerViewIngredients.setLayoutManager(getNewLinearLayoutManager());
            binding.recyclerViewIngredients.setAdapter(new IngredientVolAdapter());
        }else{
            binding.recyclerViewIngredients.setVisibility(View.GONE);
        }
        setAlcoholic();

    }

    /**
     * is supposed to reload the current topic list
     */
    private void updateTopics() {
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
        private Ingredient ingredient;
        private int volume;
        private Topic topic;

        public StringView(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.textView_item_little_title);
        }

        private void setTxt(@NonNull Ingredient ingredient, int volume){
            this.ingredient = ingredient;
            this.volume = volume;
            this.txt.setText(String.format("%s: %s", ingredient.getName(), volume));
            this.txt.setOnLongClickListener(v -> {
                GetDialog.deleteAddElement(AddActivity.this.activity, "die Zutat "+ingredient.getName() ,new Postexecute() {
                    @Override
                    public void post() {
                        AddActivity.this.ingredientVolumeHashMap.remove(ingredient);
                        AddActivity.this.updateIngredients();
                    }
                });
                return true;
            });
        }

        private void setTxt(@NonNull Topic topic){
            this.topic = topic;
            this.txt.setText(topic.getName());
            this.txt.setOnLongClickListener(v -> {
                GetDialog.deleteAddElement(AddActivity.this.activity, "den Serviervorschlag "+topic.getName() ,new Postexecute() {
                    @Override
                    public void post() {
                        AddActivity.this.topics.remove(topic);
                        AddActivity.this.updateTopics();
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
            return new StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StringView holder, int position) {
            holder.setTxt(topics.get(position));
        }

        @Override
        public int getItemCount() {
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
            return new StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StringView holder, int position) {
            Ingredient i = ingredientVolumeHashMap.keySet().toArray(new Ingredient[]{})[position];
            if(i== null){
                return;
            }
            int vol;
            try {
                vol = ingredientVolumeHashMap.get(i);
            }catch (NullPointerException e){
                vol = -1;
            }
            holder.setTxt(i, vol);
        }

        @Override
        public int getItemCount() {
            return ingredientVolumeHashMap.size();
        }
    }


}