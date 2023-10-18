package com.example.cocktailmachine.ui.fillAnimation;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.ui.model.v2.GetActivity;
import com.example.cocktailmachine.ui.model.v2.GetDialog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;


public class FillAnimation extends AppCompatActivity {

    private static final String TAG = "CocktailList";

    FragmentManager fragmentManager;
    Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_animation);


        //initialise Fragment Manager
        fragmentManager = getSupportFragmentManager();
        long id = 0L;


        Intent i = getIntent();
        if(i != null){
            id = i.getLongExtra(GetActivity.ID, id);
        }

        recipe = Recipe.getRecipe(id);//Recipe.getRecipe(id);//SingeltonTestdata.getSingelton().getRecipe();
        Bitmap image = null;

        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        //animation.setStartDelay(5000);
        animation.setDuration(10000);

        Context context = this;

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                Bitmap image = null;
                if(recipe!=null) {
                    recipe.save(context);
                }
                if(true){
                    try {
                        image = BildgeneratorGlas.bildgenerationGlas(context,recipe, animatedValue);
                    } catch (TooManyTimesSettedIngredientEcxception | NoSuchIngredientSettedException e) {
                        e.printStackTrace();
                    }
                    GlassFillFragment fragment = GlassFillFragment.newInstance(
                            recipe != null ? recipe.getName() : "",
                            image);
                    replaceFragment(fragment);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(animatedValue>=1f) {
                    onFinish();
                }
            }
        });

        animation.start();

    }



    /**
     * TODO: Phillip: when is done is checked with CocktailMaschine.isFinished(activity)
     *
     * when cocktail is done, do this,
     * @author Johanna Reidt
     */
    private void onFinish(){

        GetDialog.isDone(this, recipe);
    }



    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in_slow,  // enter
                R.anim.do_nothing  // popExit
        );
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static float roundAvoid(float value, int places) {
        double scale = Math.pow(10, places);
        return (float)(Math.round(value * scale) / scale);
    }
}