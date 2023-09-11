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

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.ui.model.v1.RowViews;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class AddActivity extends BasicActivity {
    private ActivityAddBinding binding;
    private Pump pump;
    private Topic topic;
    private Recipe recipe;
    private Ingredient ingredient;

    private LinkedHashMap<Ingredient, Integer> ingredientVolumeHashMap;
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

        //maybe needed



        pump = Pump.getPump(this.getID());

    }

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
    }

    @Override
    void setUpRecipe() {


        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);


        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);


        binding.subLayoutAddIngredient.setVisibility(View.VISIBLE);
        binding.subLayoutAddIngredient.setOnClickListener(v -> {

        });
        updateIngredients();


        binding.subLayoutAddTopic.setVisibility(View.VISIBLE);
        binding.subLayoutAddTopic.setOnClickListener(v -> {

        });
        updateTopics();

    }

    @Override
    void postSetUp() {

        //for all
        binding.buttonStop.setOnClickListener(v -> GetActivity.goBack(activity));

    }

    void error(){
        error(null);
    }

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

    private LinearLayoutManager getNewLinearLayoutManager(){

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        return llm;
    }

    private void updateIngredients(){
        if(ingredientVolumeHashMap.size()>0) {
            binding.recyclerViewIngredients.setVisibility(View.VISIBLE);
            binding.recyclerViewIngredients.setLayoutManager(getNewLinearLayoutManager());
            binding.recyclerViewIngredients.setAdapter(new IngredientVolAdapter());
        }else{
            binding.recyclerViewIngredients.setVisibility(View.GONE);
        }

    }


    private void updateTopics() {
        if(topics.size()>0) {
            binding.recyclerViewTopics.setVisibility(View.VISIBLE);
            binding.recyclerViewTopics.setLayoutManager(getNewLinearLayoutManager());
            binding.recyclerViewTopics.setAdapter(new TopicAdapter());
        }else{
            binding.recyclerViewTopics.setVisibility(View.GONE);
        }
    }

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