package com.example.cocktailmachine.ui;

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

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.cocktailmachine.databinding.ActivityLoadDataAnimationBinding;

import com.example.cocktailmachine.R;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LoadDataAnimation extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoadDataAnimationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data_animation);

        /**backgroundImage = findViewById(R.id.bluetoothNotFoundBackgroundImage);
        loopImage = findViewById(R.id.bluetoothNotFoundLoopImage);

        try {
            image = BildgeneratorGlas.bildgenerationGlas(this,this.getRandomRecipe(),(float)1.0);
        } catch (TooManyTimesSettedIngredientEcxception e) {
            throw new RuntimeException(e);
        } catch (NoSuchIngredientSettedException e) {
            throw new RuntimeException(e);
        }

        Animation anim = new CircularAnimation(loopImage, 100);
        anim.setDuration(3000);
        anim.setRepeatCount(Animation.INFINITE);
        loopImage.startAnimation(anim);

        backgroundImage.setImageBitmap(image);
        //Idea: how to get connection
        //BluetoothSineelton   public boolean connect = false;
        CocktailStatus.getCurrentStatus(new Postexecute() {
            @Override
            public void post() {
                //GetActivity.goToMenu(BluetoothNotFound.this);
                //go back
            }
        }, BluetoothNotFound.this);**/
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
}