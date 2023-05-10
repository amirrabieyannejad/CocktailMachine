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


import com.example.cocktailmachine.data.BasicRecipes;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.R;


import org.json.JSONArray;
import org.json.JSONException;

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

        Recipe recipe = new Recipe() {
            @Override
            public int compareTo(Recipe o) {
                return 0;
            }

            @Override
            public long getID() {
                return 0;
            }

            @Override
            public void setID(long id) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public List<Long> getIngredientIds() {
                return null;
            }

            @Override
            public List<Ingredient> getIngredients() {
                List<Ingredient> list = new LinkedList();
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public long getID() {
                        return 1;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "ROT";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.RED;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void setPump(Long pump, int fluidInMillimeters) {

                    }

                    @Override
                    public void pump(int millimeters) {

                    }

                    @Override
                    public void save() {

                    }

                    @Override
                    public void delete() {

                    }
                });
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public long getID() {
                        return 2;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "Blau";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.BLUE;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void setPump(Long pump, int fluidInMillimeters) {

                    }

                    @Override
                    public void pump(int millimeters)  {

                    }

                    @Override
                    public void save() {

                    }

                    @Override
                    public void delete() {

                    }
                });
                return list;
            }

            @Override
            public HashMap<Long, Integer> getIngredientVolumes() {
                return null;
            }

            @Override
            public List<Map.Entry<String, Integer>> getIngredientNameNVolumes() {
                return null;
            }

            @Override
            public int getSpecificIngredientVolume(long ingredientId) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
                return 0;
            }

            @Override
            public int getSpecificIngredientVolume(Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
                if (ingredient.getID() == 1){
                    return 30;
                }
                if (ingredient.getID() == 2){
                    return 60;
                }
                return 80;
            }

            @Override
            public boolean isAlcoholic() {
                return false;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean needsUpdate() {
                return false;
            }

            @Override
            public void wasSaved() {

            }

            @Override
            public void wasChanged() {

            }

            @Override
            public List<String> getImageUrls() {
                return null;
            }

            @Override
            public List<Long> getTopics() {
                return null;
            }

            @Override
            public void addOrUpdate(Ingredient ingredient, int timeInMilliseconds) {

            }

            @Override
            public void addOrUpdate(long ingredientId, int timeInMilliseconds) {

            }

            @Override
            public void addOrUpdate(Topic topic) {

            }

            @Override
            public void addOrUpdate(String imageUrls) {

            }

            @Override
            public void remove(Ingredient ingredient) {

            }

            @Override
            public void removeIngredient(long ingredientId) {

            }

            @Override
            public void remove(Topic topic) {

            }

            @Override
            public void removeTopic(long topicId) {

            }

            @Override
            public void remove(SQLRecipeImageUrlElement url) {

            }

            @Override
            public void removeUrl(long urlId) {

            }

            @Override
            public void setRecipes(JSONArray json) throws NotInitializedDBException, JSONException {

            }

            @Override
            public void delete() {

            }

            @Override
            public void save() {

            }
        };

        DatabaseConnection.initialize_singleton(this.getBaseContext());


        /**
        Ingredient tequila = new SQLIngredient(1,"Tequila", true, Color.RED);
        Ingredient orangenlikör = new SQLIngredient(2,"Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = new SQLIngredient(3,"Limettensaft", false, Color.WHITE);


        Recipe magarita = new SQLRecipe("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);


        List<Recipe> recipes= null;
        recipes = Recipe.getRecipes();

        System.out.println(recipes);
        **/

        Context context = this;

        ImageView iv =(ImageView) findViewById(R.id.ivtest);

        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(10);

        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();

                try {
                    iv.setImageBitmap(BildgeneratorGlas.bildgenerationGlas(context,recipe,animatedValue));
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