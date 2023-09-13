package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;



import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.db.DatabaseConnection;


import java.util.List;

public class Grafik extends AppCompatActivity {

    private static final String TAG = "Grafik";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);


        /**Resources res = this.getResources();
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.glas8, null);
        Drawable myImage2 = ResourcesCompat.getDrawable(res, R.drawable.ic_launcher_background, null);

        Bitmap bm = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        myImage.draw(canvas);
        myImage2.setBounds(0, 0, canvas.getWidth()/2, canvas.getHeight()/2);
        myImage2.draw(canvas);

        //iv.draw(canvas);
         **/
       /** DatabaseConnection.initialize_singleton(this);
        //BasicRecipes.loadTest();
        try {
            BasicRecipes.loadMargarita();
            BasicRecipes.loadLongIslandIceTea();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        Recipe recipe = null;
        try {
            recipe = DatabaseConnection.getDataBase().getRecipe(Recipe.makeNew("Magarita").getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }



        DatabaseConnection.initialize_singleton(this.getBaseContext());
*/
        DatabaseConnection.initializeSingleton(this);
        Integer idRecipe = 0;

        Ingredient tequila = new SQLIngredient(1,"Tequila", true, Color.RED);
        Ingredient orangenlikör = new SQLIngredient(2,"Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = new SQLIngredient(3,"Limettensaft", false, Color.WHITE);


        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);


        List<Recipe> recipes = Recipe.getRecipes();


        System.out.println(recipes);

        Recipe drink = recipes.get(idRecipe);

        List<Ingredient> ing = drink.getIngredients();
        System.out.println(drink);
        Context context = this;

        ImageView iv = findViewById(R.id.ivtest);

        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(10000);

        Recipe finalRecipe = drink;// SingeltonTestdata.getSingelton().getRecipe();
        System.out.println("Die Farbe ist = " + finalRecipe.getIngredients());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                System.out.println(animatedValue);

                try {
                    iv.setImageBitmap(BildgeneratorGlas.bildgenerationGlas(context, finalRecipe,animatedValue));
                } catch (TooManyTimesSettedIngredientEcxception e) {
                    e.printStackTrace();
                } catch (NoSuchIngredientSettedException e) {
                    e.printStackTrace();
                }
            }
        });

        animation.start();





        //iv.setImageBitmap(bm);
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public static Bitmap bildgenerationGlas(Context context){
        Resources res = context.getResources();

        Drawable myImage =  ResourcesCompat.getDrawable(res, R.drawable.glas_fluessigkeit01, null);
        Drawable myImage2 = ResourcesCompat.getDrawable(res, R.drawable.glas_fluessigkeit02, null);


        Bitmap bm1 = BitmapFactory.decodeResource(res,R.drawable.glas_fluessigkeit01);
        Bitmap bm2 = BitmapFactory.decodeResource(res,R.drawable.glas_fluessigkeit02);



        Bitmap bm = Bitmap.createBitmap(800,800,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

        int color = Color.BLUE;
        ColorFilter colorfilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_ATOP);
        //myImage.setColorFilter(0, PorterDuff.Mode.SRC_ATOP);
        myImage.draw(canvas);
        //myImage2.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //myImage.setTint(0xFFFF0000);
        //myImage2.draw(canvas);

        return (bm);
    }


}