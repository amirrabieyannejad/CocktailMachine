package com.example.cocktailmachine.ui.model.v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.databinding.ActivityDisplayBinding;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;

public class AddActivity extends BasicActivity {
    ActivityAddBinding binding;
    Pump pump;
    Topic topic;
    Recipe recipe;
    Ingredient ingredient;

    final Activity activity = this;


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
        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.switchAlcohol.setVisibility(View.VISIBLE);

        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);

    }

    @Override
    void setUpRecipe() {


        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);


        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);

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


}