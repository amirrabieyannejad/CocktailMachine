package com.example.cocktailmachine.data;


import android.app.Activity;
import android.content.Context;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.data.enums.AdminRights;


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
     * Get ingredient ids used in this recipe.
     * @return list of ingredient ids.
     */
    List<String> getIngredientNames();

    /**
     * Get ingredients used in this recipe.
     * @return list of ingredient.
     */
    List<Ingredient> getIngredients();

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
     * Is available? with ingredients
     * @return available?
     */
    boolean loadAvailable();

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


    //Zutaten Getter

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


    default JSONArray getLiquidsJSON(){
        JSONArray json = new JSONArray();
        for(Map.Entry<String, Integer> e:this.getIngredientNameNVolumes()){
            JSONArray j = new JSONArray();
            j.put(e.getKey());
            j.put(e.getValue());
            json.put(j);
        }
        return json;
    }

    /**
     * Recipe in {"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]} Format
     * like described in ProjektDokumente/esp/Services.md
     * @return
     * @throws JSONException
     */
    default JSONObject asMessage() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", this.getName());
        json.put("liquids", this.getLiquidsJSON());
        return json;

    }


    /**
     * send to be mixed
     *   {"cmd": "make_recipe", "user": 8858, "recipe": "radler"}
     * TODO:show topics to user!
     */
    default void send(Activity activity){
        //service.
        //BluetoothSingleton.getInstance().mBluetoothLeService.makeRecipe(AdminRights.getUserId(), );
        //TODO: Bluetooth send to mix
    }

    /**
     * Sends to CocktailMachine to get saved.
     * {"cmd": "define_recipe", "user": 0, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}
     *
     *
     * TODO: find what this is doing :     {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}
     */
    default boolean sendSave(Activity activity){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "define_recipe");
            jsonObject.put("user", AdminRights.getUserId());
            jsonObject.put("recipe", this.getName());
            JSONArray array = new JSONArray();
            List<Ingredient> is = this.getIngredients();
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getSpecificIngredientVolume(i));
                array.put(temp);
            }
            jsonObject.put("liquids", array);
            //TODO: send
            return true;
        } catch (JSONException | TooManyTimesSettedIngredientEcxception |
                NoSuchIngredientSettedException  e) {
            e.printStackTrace();
        }
        return false;
    }



    //general

    /**
     * produces JSON Array with all recipes
     * exmaple  [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * like described in Services.md
     * @return
     * @throws NotInitializedDBException
     * @throws JSONException
     */
    static JSONArray getRecipesAsMessage() throws NotInitializedDBException, JSONException {
        JSONArray json = new JSONArray();
        for(Recipe r: getRecipes()){
            json.put(r.asMessage());
        }
        return json;
    }

    static void sync(Activity activity){
        //TODO: Sync, get alle recipes from bluetooth
        JSONArray answer = new JSONArray();
        try {
            setRecipes(answer);
        } catch (NotInitializedDBException | JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * add Recipes to db from json array gotten from cocktail machine
     * [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * @param json
     * @throws NotInitializedDBException
     */
    static void setRecipes(JSONArray json) throws NotInitializedDBException, JSONException{
        //[{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
        for(int i=0; i<json.length(); i++){
            JSONObject j = json.optJSONObject(i);
            Recipe temp = Recipe.searchOrNew(j.optString("name", "Default"));
            JSONArray a = j.optJSONArray("liquids");
            if(a != null){
                for(int l=0; l<a.length(); l++){
                    JSONArray liq = a.optJSONArray(l);
                    if(liq!=null){
                        String name = a.getString(0);
                        int volume = a.getInt(1);
                        Ingredient ig = Ingredient.searchOrNew(name);
                        temp.addOrUpdate(ig, volume);
                    }
                }
            }
            temp.save();
        }
    }




    //Getting
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

    /**
     * get recipe with name
     * @param name
     * @return
     */
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

    /**
     * get recipe with name
     * or create a new one with name
     * @param name
     * @return
     */
    static Recipe searchOrNew(String name){
        Recipe recipe = Recipe.getRecipe(name);
        if(recipe == null){
            return Recipe.makeNew(name);
        }
        return recipe;
    }

}
