package com.example.cocktailmachine.data;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.ui.model.v2.GetDialog;
import com.example.cocktailmachine.ui.model.v2.WaitingQueueCountDown;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Recipe extends Comparable<Recipe>, DataBaseElement {

    WaitingQueueCountDown getWaitingQueueCountDown();
    void setWaitingQueueCountDown(Activity activity);

    void addDialogWaitingQueueCountDown(Activity activity, AlertDialog alertDialog);

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
    List<Long> getTopicIDs();


    /**
     * Get recommended topics.
     * @return recommended topics
     */
    List<Topic> getTopics();


    //Zutaten Getter

    /**
     * Get ingredients ids and their associated pumptimes in milliseconds
     * @return hashmap ids, pump time
     */
    HashMap<Long, Integer> getIngredientNameVolumes();


    /**
     * Get ingredients ids and their associated pumptimes in milliseconds
     * @return hashmap ids, pump time
     */
    HashMap<Ingredient, Integer> getIngredientVolumes();

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
    void setName(String name);

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


    default void addOrUpdateAndRemoveNotMentioned(HashMap<Ingredient, Integer> ingredientVol){
        this.removeAllIngredients();
        for(Ingredient i: ingredientVol.keySet()){
            this.addOrUpdate(i, ingredientVol.get(i));
        }
    }

    default void addOrUpdateAndRemoveNotMentioned(List<Topic> topics){
        this.removeAllTopics();
        for(Topic i: topics){
            this.addOrUpdate(i);
        }
    }


    default void removeAllIngredients(){
        for(Ingredient i: this.getIngredients()){
            this.remove(i);
        }
    }

    default void removeAllTopics(){
        for(Topic i: this.getTopics()){
            this.remove(i);
        }
    }





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

    //JSON Formating Information from db

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
        //TO DO: USE THIS AMIR ** ich vermute, dasss ich das schon verwendet habe
        // ** bitte nochmal überprüfen
        JSONObject json = new JSONObject();
        json.put("name", this.getName());
        json.put("liquids", this.getLiquidsJSON());
        return json;

    }

    //Use Bluetooth

    /**
     * send to be mixed
     *   {"cmd": "make_recipe", "user": 8858, "recipe": "radler"}
     * TO DO:show topics to user!
     */
    default void send(Activity activity) {
        //service.
        //BluetoothSingleton.getInstance().mBluetoothLeService.makeRecipe(AdminRights.getUserId(), );

        GetDialog.sendRecipe(activity, this);
        CocktailMachine.setCurrentRecipe(this);
        /*

        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.userStartRecipe(this.getID(),activity);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

         */
        //TO DO: Bluetooth send to mix
        //TO DO: AMIR **DONE**
    }


    /**
     * Sends to CocktailMachine to get saved.
     * {"cmd": "define_recipe", "user": 0, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}
     *
     *
     * TODO: find what this is doing :     {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}
     */
    default boolean sendSave(Activity activity){
        //TO DO: USE THIS AMIR  * ich habe in adminDefinePump das gleiche. wollen wir vlt.
        // * hier das verwenden vlt. durch:


        //JSONObject jsonObject = new JSONObject();


            /* jsonObject.put("cmd", "define_recipe");
            jsonObject.put("user", AdminRights.getUserId());
            jsonObject.put("recipe", this.getName());

             */
        save(activity);
        if(Dummy.isDummy){
            return true;
        }
        try {
            JSONArray array = new JSONArray();
            List<Ingredient> is = this.getIngredients();
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getSpecificIngredientVolume(i));
                array.put(temp);
            }
            //jsonObject.put("liquids", array);
            BluetoothSingleton.getInstance().userDefineRecipe(
                    this.getID(),
                    this.getName(),
                    array,
                    activity);

            return true;
        } catch (JSONException | TooManyTimesSettedIngredientEcxception |
                 NoSuchIngredientSettedException | InterruptedException|NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    default boolean sendDelete(Activity activity){
        try {
            BluetoothSingleton.getInstance().userDeleteRecipe(this.getID(),
                    this.getName(),activity
            );
            return true;
        } catch (JSONException | InterruptedException|NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Methods to be called form bluetooth singleton to set informations in db






    //general for all Recipes
    //JSON Formating Information from db

    /**
     * produces JSON Array with all recipes from db
     * exmaple  [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * like described in Services.md
     * @return
     * @throws NotInitializedDBException
     * @throws JSONException
     */
    static JSONArray getRecipesAsMessage() throws NotInitializedDBException, JSONException, InterruptedException {
        //TO DO: USE THIS AMIR * Ich glaube ist für mich setRecipe interessant? *
        JSONArray json = new JSONArray();
        for(Recipe r: getRecipes()){
            json.put(r.asMessage());
        }

        return json;
    }

    //Use Bluetooth

    /**
     * get pump status
     * check last cange
     * get recipes
     * @author Johanna Reidt
     * @param activity
     */
    static void syncRecipeDBWithCocktailmachine(Activity activity){
        //TO DO: Sync, get alle recipes from bluetooth

        /*JSONArray answer = new JSONArray();
        try {
            setRecipes(answer);
        } catch (NotInitializedDBException | JSONException e) {
            e.printStackTrace();
        }

         */
        Pump.readPumpStatus(activity);
        CocktailMachine.updateRecipeListIfChanged(activity);
        Buffer.localRefresh(activity);
    }



    //Methods to be called form bluetooth singleton to set informations in db
    /**
     * add Recipes to db from json array gotten from cocktail machine
     * [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]},
     * {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * @param json
     * @throws NotInitializedDBException
     */
    static void setRecipes(Context context,JSONArray json) throws JSONException{
        //TO DO: USE THIS AMIR **DONE**
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
                        Ingredient ig = Ingredient.searchOrNew(context,name);
                        temp.addOrUpdate(ig, volume);
                    }
                }
            }
            temp.save(context);
        }
    }




    //Getting from db considering all recipes
    /**
     * Static access to recipes.
     * Get Recipe with id k.
     * @param id id k
     * @return Recipe
     */
    static Recipe getRecipe(long id) {
        return Buffer.getSingleton().getRecipe(id);
    }

    /**
     * get recipe with name
     * @param name
     * @return
     */
    static Recipe getRecipe(String name){
        return Buffer.getSingleton().getRecipe(name);

    }

    /**
     * Static access to recipes.
     * Get available recipes.
     * @return list of recipes
     */
     static List<Recipe> getRecipes() {
        return Buffer.getSingleton().getAvailableRecipes();

     }

    /**
     * Static access to recipes.
     * Get all saved recipes.
     * @return list of recipes
     */
    static List<Recipe> getAllRecipes() {
        return Buffer.getSingleton().getRecipes();
    }

    /**
     * Static access to recipes.
     * Get available recipes with id in list of ids k
     * @param ids list of ids k
     * @return list of recipes
     */
    static List<Recipe> getRecipes(List<Long> ids) {
        return Buffer.getSingleton().getRecipes(ids);
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
