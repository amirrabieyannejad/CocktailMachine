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

import com.cocktailmachine.data.Ingredient;
import com.cocktailmachine.data.Pump;
import com.cocktailmachine.data.Recipe;
import com.cocktailmachine.data.db.NotInitializedDBException;
import com.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;

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
        List<Recipe> recipes= null;
        try {
            recipes = Recipe.getRecipes();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        /**Recipe recipe = new Recipe() {
            @Override
            public int compareTo(Recipe recipe) {
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
                List<Ingredient> list = new LinkedList<>();
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient ingredient) {
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
                    public void save() throws NotInitializedDBException {

                    }

                    @Override
                    public void delete() throws NotInitializedDBException {

                    }

                    @Override
                    public int getVolume()  {
                        return  30;
                    }

                    @Override
                    public int getColor() {
                        return Color.RED;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }


                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void setPump(Long pump, int volume) {

                    }

                    @Override
                    public void pump(int volume) throws NewlyEmptyIngredientException {

                    }
                });

                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient ingredient) {
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
                        return "BLUE";
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
                    public void save() throws NotInitializedDBException {

                    }

                    @Override
                    public void delete() throws NotInitializedDBException {

                    }

                    @Override
                    public int getVolume()  {
                        return  90;
                    }

                    @Override
                    public int getColor() {
                        return Color.BLUE;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }


                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void setPump(Long pump, int volume) {

                    }

                    @Override
                    public void pump(int volume) throws NewlyEmptyIngredientException {

                    }
                });

                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient ingredient) {
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
                    public void save() throws NotInitializedDBException {

                    }

                    @Override
                    public void delete() throws NotInitializedDBException {

                    }

                    @Override
                    public int getVolume()  {
                        return  30;
                    }

                    @Override
                    public int getColor() {
                        return Color.RED;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }


                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void setPump(Long pump, int volume) {

                    }

                    @Override
                    public void pump(int volume) throws NewlyEmptyIngredientException {

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
                return 0;
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
            public void save() throws NotInitializedDBException {

            }

            @Override
            public void delete() throws NotInitializedDBException {

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
            public void addOrUpdate(Ingredient ingredient, int volume) {

            }

            @Override
            public void addOrUpdate(long ingredientId, int volume) {

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
        };**/

        System.out.pri(recipes);
        ImageView iv =(ImageView) findViewById(R.id.ivtest);
        //iv.setImageBitmap(BildgeneratorGlas.bildgenerationGlas(this,recipe));
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