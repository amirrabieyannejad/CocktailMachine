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

import java.util.Collections;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        canvas = new BildgeneratorGlas().erzeugeFlüssigkeitGlas(context,canvas,recipe,(float)1);

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

    public static Bitmap bildgenerationGlas(Context context, Recipe recipe,Float filling) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {

        Bitmap bm = Bitmap.createBitmap(800,800,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Resources res = context.getResources();

        Drawable glasHintereDarstellung =  ResourcesCompat.getDrawable(res, R.drawable.glas_hintere_darstellung, null);
        glasHintereDarstellung.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        glasHintereDarstellung.draw(canvas);

        if (filling > 1){
            filling = (float)1;
        }
        if (filling < 0){
            filling = (float)0;
        }


        canvas = new BildgeneratorGlas().erzeugeFlüssigkeitGlas(context,canvas,recipe,filling);

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


    private Canvas erzeugeFlüssigkeitGlas(Context context, Canvas canvas, Recipe recipe,Float filling) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {

        if (filling > 1){
            filling = (float)1;
        }
        if (filling < 0){
            filling = (float)0;
        }

        Resources res = context.getResources();
        List<Ingredient> ingredientList = recipe.getIngredients();
        int animationSlots= listIdGlasFlüssigkeit.length;
        int slotCounter= 0;

        Map<Ingredient,Integer> proportionenFlüssigkeitenImGlas =
                this.getProportionOfLiquidInCocktail(recipe,  ingredientList, animationSlots);

        for (Ingredient ingredient : ingredientList){
            int numberSlots = proportionenFlüssigkeitenImGlas.get(ingredient);
            for (int i = 0 ; i < numberSlots; i++){
                if((slotCounter+(i+1))/animationSlots>filling){
                    return(canvas);
                }
                Drawable myImage =  ResourcesCompat.getDrawable(res, listIdGlasFlüssigkeit[i+slotCounter], null);
                myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                myImage.setColorFilter(ingredient.getColor(), PorterDuff.Mode.MULTIPLY);//MULTIPLY,SRC_IN,SRC_ATOP
                myImage.draw(canvas);
            }
            slotCounter += numberSlots;
        }

        return canvas;
    }

    private List<Ingredient> sortIngredientsAscending(Recipe recipe, List<Ingredient> ingredientList){
        List<Ingredient> list = new LinkedList<>(ingredientList);
        Collections.sort(list,new Comparator<Ingredient>() {
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
        return list;
    }

    private int getNumberOfSlots(float sumLiquit, int animationSlots, Recipe recipe, Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        float liquitProSlot = sumLiquit/animationSlots;
        if(liquitProSlot>recipe.getSpecificIngredientVolume(ingredient)){
            return 1;
        }
        return (int) (recipe.getSpecificIngredientVolume(ingredient)/liquitProSlot);

    }

    private Map<Ingredient,Integer> getProportionOfLiquidInCocktail(Recipe recipe, List<Ingredient> ingredientList,int animationSlots) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        int sumLiquit = 0;
        int slotCounter= 0;
        List<Ingredient> newIngredientList = this.sortIngredientsAscending(recipe,ingredientList);

        Map<Ingredient,Integer> outputMap = new HashMap<>();

        for (Ingredient ingredient : recipe.getIngredients()){
            sumLiquit += recipe.getSpecificIngredientVolume(ingredient);
        }

        for (Ingredient ingredient : newIngredientList){
            int numberSlots = this.getNumberOfSlots(sumLiquit,animationSlots-slotCounter,recipe, ingredient);
            outputMap.put(ingredient,numberSlots);
            slotCounter += numberSlots;
            sumLiquit -= recipe.getSpecificIngredientVolume(ingredient);
        }

        return(outputMap);
    }



}
