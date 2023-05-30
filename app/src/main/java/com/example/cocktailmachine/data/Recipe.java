package com.example.cocktailmachine.data;


import android.os.Bundle;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Recipe extends Comparable<Recipe>, DataBaseElement {
    //Getter

    /**
     * Get id.
     * @return id
     */
    long getID();

    /**
     * Get name.
     * @return name
     */
    String getName();

    /**
     * Get ingredient ids used in this recipe.
     * @return list of ingredient ids.
     */
    List<Long> getIngredientIds();

    /**
     * Get ingredients used in this recipe.
     * @return list of ingredient.
     */
    List<Ingredient> getIngredients();

    /**
     * Get ingredients ids and their associated pumptimes in milliseconds
     * @return hashmap ids, pump time
     */
    HashMap<Long, Integer> getIngredientVolumes();

    /**
     * Get ingredients names and their associated pumptimes in milliseconds
     * @return hashmap name, pump time
     */
    List<Map.Entry<String, Integer>> getIngredientNameNVolumes();

    /**
     * Get specific pump time for ingredient with id k
     * @param ingredientId ingredient id k
     * @return pump time in milliseconds
     * @throws TooManyTimesSettedIngredientEcxception There are multiple times setted. only one time is allowed.
     * @throws NoSuchIngredientSettedException There is no such ingredient. The id is not known.
     */
    int getSpecificIngredientVolume(long ingredientId) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;

    /**
     * Get specific pump time for ingredient k
     * @param ingredient ingredient k
     * @return pump time in milliseconds
     * @throws TooManyTimesSettedIngredientEcxception There are multiple times setted. only one time is allowed.
     * @throws NoSuchIngredientSettedException There is no such ingredient. The id is not known.
     */
    int getSpecificIngredientVolume(Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;

    /**
     * Is alcoholic?
     * @return alcoholic?
     */
    boolean isAlcoholic();

    /**
     * Is available?
     * @return available?
     */
    boolean isAvailable();

    /**
     * Get associated image addresses.
     * @return list of image addresses
     */
    List<String> getImageUrls();

    /**
     * Get recommended topics.
     * @return recommended topics
     */
    List<Long> getTopics();


    //bASIC CHANGER
    public void setName(String name);

    //Ingredient Changer
    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredient
     * @param volume
     */
    void addOrUpdate(Ingredient ingredient, int volume);
    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredientId
     * @param volume
     */
    void addOrUpdate(long ingredientId, int volume);

    void addOrUpdate(Topic topic);

    void addOrUpdate(String imageUrls);

    /**
     * Remove Ingredient from Recipe.
     * @param ingredient
     */
    void remove(Ingredient ingredient);
    /**
     * Remove Ingredient from Recipe.
     * @param ingredientId
     */
    void removeIngredient(long ingredientId);

    void remove(Topic topic);

    void removeTopic(long topicId);

    void remove(SQLRecipeImageUrlElement url);

    void removeUrl(long urlId);


    //this Instance

    default JSONArray getLiquids(){
        JSONArray json = new JSONArray();
        for(Map.Entry<String, Integer> e:this.getIngredientNameNVolumes()){
            JSONArray j = new JSONArray();
            j.put(e.getKey());
            j.put(e.getValue());
            json.put(j);
        }
        return json;
    }

    default JSONObject asMessage() throws JSONException {
        //TODO: https://github.com/johannareidt/CocktailMachine/blob/main/ProjektDokumente/esp/Services.md

        JSONObject json = new JSONObject();
        json.put("name", this.getName());
        json.put("liquids", this.getLiquids());
        return json;

    }

    /**
     * Sends to CocktailMachine to get mixed.

     * Reminder:
     * send topics to user!
     */
    default boolean sendSave(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "define_recipe");
            jsonObject.put("user", AdminRights.getUserId());
            jsonObject.put("recipe", this.getName());
            JSONArray array = new JSONArray();
            //TODO: send
            List<Ingredient> is = this.getIngredients();
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getSpecificIngredientVolume(i));
                array.put(temp);
            }
            jsonObject.put("liquids", array);
            return true;
        } catch (JSONException | TooManyTimesSettedIngredientEcxception |
                NoSuchIngredientSettedException  e) {
            e.printStackTrace();
        }
        return false;
    }



    //general

    static JSONArray getAllRecipesAsMessage() throws NotInitializedDBException, JSONException {
        JSONArray json = new JSONArray();
        for(Recipe r: getRecipes()){
            json.put(r.asMessage());
        }
        return json;
    }


    /**
     * [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * @param json
     * @throws NotInitializedDBException
     */
    void setRecipes(JSONArray json) throws NotInitializedDBException, JSONException;

    /**
     * Static access to recipes.
     * Get Recipe with id k.
     * @param id id k
     * @return Recipe
     */
    static Recipe getRecipe(long id) {
        try {
            return DatabaseConnection.getDataBase().getRecipe(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Recipe getRecipe(String name){
        try {
            return DatabaseConnection.getDataBase().getRecipeWithExact(name);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Static access to recipes.
     * Get available recipes.
     * @return list of recipes
     */
     static List<Recipe> getRecipes() {
        try {
            return (List<Recipe>) DatabaseConnection.getDataBase().getAvailableRecipes();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Static access to recipes.
     * Get all saved recipes.
     * @return list of recipes
     */
    static List<Recipe> getAllRecipes() {
        try {
            return (List<Recipe>) DatabaseConnection.getDataBase().getRecipes();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Static access to recipes.
     * Get available recipes with id in list of ids k
     * @param ids list of ids k
     * @return list of recipes
     */
    static List<Recipe> getRecipes(List<Long> ids) {
        try {
            return DatabaseConnection.getDataBase().getRecipes(ids);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Make a new recipe.
     * @param name name of the new recipe
     * @return new recipe instance with given name. It is already saved in the database!
     */
    static Recipe makeNew(String name){
        return new SQLRecipe(name);
    }

    static Recipe searchOrNew(String name){
        Recipe recipe = Recipe.getRecipe(name);
        if(recipe == null){
            return Recipe.makeNew(name);
        }
        return recipe;
    }

    void send();
}
