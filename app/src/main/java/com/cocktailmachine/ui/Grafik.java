package com.cocktailmachine.ui;

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
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.cocktailmachine.data.BasicRecipes;
import com.cocktailmachine.data.Ingredient;
import com.cocktailmachine.data.Pump;
import com.cocktailmachine.data.Recipe;
import com.cocktailmachine.data.Topic;
import com.cocktailmachine.data.db.DatabaseConnection;
import com.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.cocktailmachine.data.db.NotInitializedDBException;
import com.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.cocktailmachine.data.db.elements.SQLIngredient;
import com.cocktailmachine.data.db.elements.SQLRecipe;
import com.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;
import com.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grafik extends AppCompatActivity {


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


        Ingredient tequila = new SQLIngredient(1,"Tequila", true, Color.RED);
        Ingredient orangenlikör = new SQLIngredient(2,"Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = new SQLIngredient(3,"Limettensaft", false, Color.WHITE);


        Recipe magarita = new SQLRecipe("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);


        List<Recipe> recipes= null;
        try {
            recipes = Recipe.getRecipes();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }

        System.out.println(recipes);

        ImageView iv =(ImageView) findViewById(R.id.ivtest);
        iv.setImageBitmap(BildgeneratorGlas.bildgenerationGlas(this,magarita));
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