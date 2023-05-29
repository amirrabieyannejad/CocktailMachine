package com.example.cocktailmachine;

import android.graphics.Color;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SingeltonTestdata {

    private static SingeltonTestdata singelton;

    private SingeltonTestdata(){

    }

    public static SingeltonTestdata getSingelton(){
        if (singelton == null){
            SingeltonTestdata.singelton = new SingeltonTestdata();

        }
        return (singelton);
    }

    public Recipe getRecipe(){
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


        return(recipe);
    }

    public Recipe getRecipe2(){
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


        return(recipe);
    }

}
