package com.example.cocktailmachine.ui.fillAnimation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.SingeltonTestdata;
import com.example.cocktailmachine.data.Orientation;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.logic.FlingAnalysis;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;
import com.example.cocktailmachine.ui.singleCocktailChoice.fragment1;

import java.util.LinkedList;
import java.util.List;

public class FillAnimation {

    private GestureDetector mDetector;
    private int counter = 0;
    private int fragmentCounter = 0;



    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    SingeltonTestdata singeltonCocktail = SingeltonTestdata.getSingelton();
    List<Recipe> recipes = new LinkedList();



    List<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cocktail_choice);

        testData = new LinkedList<>();
        testData.add("a");
        testData.add("b");
        testData.add("c");


        recipes.add(singeltonCocktail.getRecipe());
        recipes.add(singeltonCocktail.getRecipe2());

        //load List of available recipes
       /* try {
            this.recipes = loadRecipes(this);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }*/

        //initialise Fragment Manager
        fragmentManager = getSupportFragmentManager();


        fragment1 f1 = fragment1.newInstance();
        //f2 = fragment2.newInstance();
        //fragment.updateImage(getResources().getDrawable(R.drawable.glas2));

        replaceFragment(f1);
        //fragment.updateTextView(new Integer(this.counter).toString());
        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.frameLayout);

        // get the gesture detector
        mDetector = new GestureDetector(this, new SingleCocktailChoice.MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);

    }




    private void replaceFragment(Fragment fragment){

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragmentWithOrientation(Fragment fragment, Orientation orientation){

        fragmentTransaction = fragmentManager.beginTransaction();

        switch (orientation){
            case RIGHT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.fade_out  // popExit
                );
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left,  // enter
                        R.anim.fade_out  // popExit
                );
                break;
            default:
                fragmentTransaction.setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.fade_out  // popExit
                );
        }
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private List<Recipe> loadRecipes(Context context) throws NotInitializedDBException {
        List<Recipe> recipes;
        try {
            recipes = DatabaseConnection.getDataBase().loadAvailableRecipes();
        } catch (NotInitializedDBException e) {
            DatabaseConnection.initialize_singleton(context);
            recipes = DatabaseConnection.getDataBase().loadAvailableRecipes();
        }
        return(recipes);
    }





}
