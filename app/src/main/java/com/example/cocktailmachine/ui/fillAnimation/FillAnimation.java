package com.example.cocktailmachine.ui.fillAnimation;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.SingeltonTestdata;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.BildgeneratorGlas;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.os.Bundle;



public class FillAnimation extends AppCompatActivity {

    FragmentManager fragmentManager;
    androidx.fragment.app.FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_animation);

        //initialise Fragment Manager
        fragmentManager = getSupportFragmentManager();

        Recipe recipe = SingeltonTestdata.getSingelton().getRecipe();
        Bitmap image = null;
        try {
            image = BildgeneratorGlas.bildgenerationGlas(this,recipe,(float)0.5);
        } catch (TooManyTimesSettedIngredientEcxception e) {
            e.printStackTrace();
        } catch (NoSuchIngredientSettedException e) {
            e.printStackTrace();
        }
        GlassFillFragment fragment = GlassFillFragment.newInstance("",image);
        replaceFragment(fragment);
    }


    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}