package com.example.cocktailmachine.logic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.res.ResourcesCompat;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BildgeneratorGlas {

    //
    private int[] listIdGlasFlüssigkeit={R.drawable.glas_fluessigkeit01,R.drawable.glas_fluessigkeit02,R.drawable.glas_fluessigkeit03,
            R.drawable.glas_fluessigkeit04,R.drawable.glas_fluessigkeit05,R.drawable.glas_fluessigkeit06,R.drawable.glas_fluessigkeit07,
            R.drawable.glas_fluessigkeit08,R.drawable.glas_fluessigkeit09,R.drawable.glas_fluessigkeit10,R.drawable.glas_fluessigkeit11,R.drawable.glas_fluessigkeit12,
            R.drawable.glas_fluessigkeit13,R.drawable.glas_fluessigkeit14, R.drawable.glas_fluessigkeit15,R.drawable.glas_fluessigkeit16,
            R.drawable.glas_fluessigkeit17,R.drawable.glas_fluessigkeit18,R.drawable.glas_fluessigkeit19,R.drawable.glas_fluessigkeit20,
            R.drawable.glas_fluessigkeit21,R.drawable.glas_fluessigkeit22};

    public static Bitmap bildgenerationGlas(Context context, Recipe recipe) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {

        Bitmap bm = Bitmap.createBitmap(800,800,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);


        Resources res = context.getResources();

        Drawable glasHintereDarstellung =  ResourcesCompat.getDrawable(res, R.drawable.glas_hintere_darstellung, null);
        glasHintereDarstellung.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        glasHintereDarstellung.draw(canvas);

        canvas = new BildgeneratorGlas().erzeugeFlüssigkeitGlas(context,canvas,recipe);

        Drawable glasFordereDarstellung =  ResourcesCompat.getDrawable(res, R.drawable.glas_fordere_darstellung, null);
        glasFordereDarstellung.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        glasFordereDarstellung.draw(canvas);

        Drawable myImage2 = ResourcesCompat.getDrawable(res, R.drawable.glas_fluessigkeit02, null);


        Bitmap bm1 = BitmapFactory.decodeResource(res,R.drawable.glas_fluessigkeit01);
        Bitmap bm2 = BitmapFactory.decodeResource(res,R.drawable.glas_fluessigkeit02);





        /**myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

        int color = Color.RED;
        ColorFilter colorfilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP);
        myImage.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        myImage.draw(canvas);
        myImage2.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //myImage.setTint(0xFFFF0000);
        myImage2.draw(canvas);**/

        return (bm);
    }


    private Canvas erzeugeFlüssigkeitGlas(Context context, Canvas canvas, Recipe recipe) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        Resources res = context.getResources();


        List<Ingredient> ingredientList = recipe.getIngredients();
        int sumLiquit = 0;
        int animationSlots= listIdGlasFlüssigkeit.length;
        int slotCounter= 0;

        for (Ingredient ingredient : recipe.getIngredients()){

            //recipe.getSpecificIngredientVolume(ingredient);
            sumLiquit += recipe.getSpecificIngredientVolume(ingredient);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientList.sort(new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient ingredient, Ingredient t1) {
                    try {
                        return (recipe.getSpecificIngredientVolume(ingredient)-recipe.getSpecificIngredientVolume(t1));
                    } catch (TooManyTimesSettedIngredientEcxception e) {
                        return(0);
                    } catch (NoSuchIngredientSettedException e) {
                        return(0);
                    }
                }
            });
        }else{ //für api kleiner als 26
            Collections.sort(ingredientList, new Comparator<Ingredient>() {
                @Override
                public int compare(Ingredient ingredient, Ingredient t1) {
                    try {
                        return (recipe.getSpecificIngredientVolume(ingredient)-recipe.getSpecificIngredientVolume(t1));
                    } catch (TooManyTimesSettedIngredientEcxception e) {
                        return(0);
                    } catch (NoSuchIngredientSettedException e) {
                        return(0);
                    }
                }
            });
        }

        for (Ingredient ingredient : ingredientList){
            int numberSlots = this.getNumberOfSlots(sumLiquit,animationSlots-slotCounter,recipe, ingredient);
            for (int i = 0 ; i < numberSlots; i++){
                Drawable myImage =  ResourcesCompat.getDrawable(res, listIdGlasFlüssigkeit[i+slotCounter], null);
                myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                myImage.setColorFilter(ingredient.getColor(), PorterDuff.Mode.MULTIPLY);//MULTIPLY,SRC_IN,SRC_ATOP
                myImage.draw(canvas);
            }
            slotCounter += numberSlots;
            sumLiquit -= recipe.getSpecificIngredientVolume(ingredient);
        }

        return canvas;
    }

    private int getNumberOfSlots(float sumLiquit, int animationSlots, Recipe recipe, Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        float liquitProSlot = sumLiquit/animationSlots;
        if(liquitProSlot>recipe.getSpecificIngredientVolume(ingredient)){
            return 1;
        }
        return (int) (recipe.getSpecificIngredientVolume(ingredient)/liquitProSlot);

    }



}
