package com.example.cocktailmachine.ui.model.v2;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

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
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.Random;

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
        binding.subLayoutAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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