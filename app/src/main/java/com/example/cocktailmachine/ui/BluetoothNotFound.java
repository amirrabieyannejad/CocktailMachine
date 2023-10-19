package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BluetoothNotFound extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_not_found);
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