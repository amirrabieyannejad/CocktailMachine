package com.example.cocktailmachine.ui;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.logic.Animation.CircularAnimation;
import com.example.cocktailmachine.logic.BildgeneratorGlas;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.cocktailmachine.databinding.ActivityLoadDataAnimationBinding;

import com.example.cocktailmachine.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LoadDataAnimation extends AppCompatActivity {
    ImageView loadImage1,loadImage2,loadImage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data_animation);

        loadImage1 = findViewById(R.id.imageViewLoadDataAnimationImage1);
        loadImage2 = findViewById(R.id.imageViewLoadDataAnimationImage2);
        loadImage3 = findViewById(R.id.imageViewLoadDataAnimationImage3);

        loadImage1.setVisibility(View.GONE);
        loadImage2.setVisibility(View.GONE);
        loadImage3.setVisibility(View.GONE);

        List<Recipe> sublistRecipe = getListOfRandomRecipe(3);

        try {
            Bitmap image = BildgeneratorGlas.bildgenerationGlas(this,sublistRecipe.get(0),(float)1.0);
            loadImage1.setImageBitmap(image);
            image = BildgeneratorGlas.bildgenerationGlas(this,sublistRecipe.get(1),(float)1.0);
            loadImage2.setImageBitmap(image);
            image = BildgeneratorGlas.bildgenerationGlas(this,sublistRecipe.get(2),(float)1.0);
            loadImage3.setImageBitmap(image);
        } catch (TooManyTimesSettedIngredientEcxception e) {
            throw new RuntimeException(e);
        } catch (NoSuchIngredientSettedException e) {
            throw new RuntimeException(e);
        }

        Animation anim1 = new CircularAnimation(loadImage1, 200);
        anim1.setDuration(3000);
        anim1.setRepeatCount(Animation.INFINITE);
        anim1.setStartOffset(500);
        loadImage1.startAnimation(anim1);

        Animation anim2 = new CircularAnimation(loadImage2, 200);
        anim2.setDuration(2500);
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setStartOffset(1000);
        loadImage2.startAnimation(anim2);

        Animation anim3 = new CircularAnimation(loadImage3, 200);
        anim3.setDuration(2000);
        anim3.setRepeatCount(Animation.INFINITE);
        anim3.setStartOffset(1500);
        loadImage3.startAnimation(anim3);

    }

    private Recipe getRandomRecipe(){
        Recipe recipe;
        Random random = new Random();

        List<Recipe> list = Recipe.getAllRecipes(this);
        int recipeNr = random.nextInt(list.size());
        recipe = list.get(recipeNr);
        list = null;
        List<Ingredient> ingredience = recipe.getIngredients(this);
        //int vol = recipe.getVolume(this,ingredience.get(0).getID());
        HashMap<Long, Integer> re = recipe.getIngredientIDToVolume(this);
        return recipe;
    }

    private List<Recipe> getListOfRandomRecipe(int numberOfRecipes){
        //Recipe recipe;
        Random random = new Random();
        List<Recipe> output = new LinkedList<>();
        List<Recipe> list = Recipe.getAllRecipes(this);
        for (int i = 0; i < numberOfRecipes;i++){
            int recipeNr = random.nextInt(list.size());
            output.add( list.get(recipeNr));
        }
        return output;
    }
}