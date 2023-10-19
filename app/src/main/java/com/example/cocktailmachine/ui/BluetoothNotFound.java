package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.BildgeneratorGlas;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BluetoothNotFound extends AppCompatActivity {

    Bitmap image = null;
    ImageView backgroundImage = findViewById(R.id.bluetoothNotFoundBackgroundImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_not_found);

        try {
            image = BildgeneratorGlas.bildgenerationGlas(this,this.getRandomRecipe());
        } catch (TooManyTimesSettedIngredientEcxception e) {
            throw new RuntimeException(e);
        } catch (NoSuchIngredientSettedException e) {
            throw new RuntimeException(e);
        }
        backgroundImage.setImageBitmap(image);
    }

    private Recipe getRandomRecipe(){
        Recipe recipe = new SQLRecipe(-1, "random", false, true);
        Random random = new Random();
        int numberIngredience = random.nextInt(3)+2;
        List<Ingredient> list = new LinkedList<>();
        for (int i = 0; i < numberIngredience; i++) {
            Ingredient ingredient = new SQLIngredient("ingredient" + i, false, (-1) * random.nextInt(16777216));
            recipe.add(this, ingredient,random.nextInt(5)+4);

        }
        return recipe;
    }
}