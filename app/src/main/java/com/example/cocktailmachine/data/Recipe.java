package com.example.cocktailmachine.data;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
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





    //Getter
    String getName();
    boolean isAlcoholic();

    /**
     * get topics
     * @author Johanna Reidt
     * @return
     */
    List<Topic> getTopics();

    /**
     * get Topic ids
     * @author Johanna Reidt
     * @return
     */
    List<Long> getTopicIDs();

    /**
     * get topic names
     * @author Johanna Reidt
     * @return
     */
    List<String> getTopicNames();

    /**
     * get ingredients
     * @author Johanna Reidt
     * @return
     */
    List<Ingredient> getIngredients();

    /**
     * get ingredient ids
     * @author Johanna Reidt
     * @return
     */
    List<Long> getIngredientIDs();

    /**
     * get ingreients names
     * @author Johanna Reidt
     * @return
     */
    List<String> getIngredientNames();

    /**
     * get list with "<ingredient_name>: <volume> ml"
     * @author Johanna Reidt
     * @return
     */
    List<String> getIngredientNameNVolumes();

    /**
     * get hashmap ingredient id -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<Ingredient, Integer> getIngredientToVolume();


    /**
     * get hashmap ingredient id -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<Long, Integer> getIngredientIDToVolume();

    /**
     * get hasmap ingredient name -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<String, Integer> getIngredientNameToVolume();

    /**
     * get volume with ingredient
     * @author Johanna Reidt
     * @param ingredient
     * @return
     */
    int getVolume(Ingredient ingredient);
    /**
     * get volume with ingredient
     * @author Johanna Reidt
     * @param ingredientID
     * @return
     */
    int getVolume(long ingredientID);

    /**
     * gives availability
     * @author Johanna Reidt
     * @return
     */
    boolean isAvailable();








    //Setter

    /**
     * set name  or replace
     * @author Johanna Reidt
     * @param name
     */
    void setName(Context context, String name);

    /**
     * add topic or replace
     * @author Johanna Reidt
     * @param context
     * @param topic
     */
    void add(Context context, Topic topic);

    /**
     * add ingredient
     * @author Johanna Reidt
     * @param context
     * @param ingredient
     */
    void add(Context context, Ingredient ingredient);

    /**
     * add ingredient with volume or replace
     * @author Johanna Reidt
     * @param context
     * @param ingredient
     * @param volume
     */
    void add(Context context, Ingredient ingredient, int volume);

    /**
     * add topics  or replace
     * @author Johanna Reidt
     * @param context
     * @param topics
     */
    default void addTopics(Context context, List<Topic> topics){
        for(Topic e: topics ){
            this.add(context, e);
        }
        this.save(context);
    }
    /**
     * add ingredients  or replace
     * @author Johanna Reidt
     * @param context
     * @param ingredients
     */
    default void addIngredients(Context context, List<Ingredient> ingredients){
        for(Ingredient e: ingredients ){
            this.add(context, e);
        }
        this.save(context);
    }
    /**
     * add ingredients with volumes  or replace
     * @author Johanna Reidt
     * @param context
     * @param ingVol
     */
    default void addIngredients(Context context, HashMap<Ingredient, Integer> ingVol){
        for(Ingredient e: ingVol.keySet() ){
            this.add(context, e, ingVol.get(e));
        }
        this.save(context);
    }

    /**
     * replace ingredients with volumes  or replace
     * @author Johanna Reidt
     * @param context
     * @param ingVol
     */
    default void replaceIngredients(Context context, HashMap<Ingredient, Integer> ingVol){
        for(Ingredient i: this.getIngredients()){
            this.remove(context, i);
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
        for(Ingredient e: ingVol.keySet() ){
            this.add(context, e, ingVol.get(e));
        }
        this.save(context);
    }

    /**
     * replace ingredients with volumes  or replace
     * @author Johanna Reidt
     * @param context
     * @param topics
     */
    default void replaceTopics(Context context, List<Topic> topics){
        for(Topic t: this.getTopics()){
            this.remove(context, t);
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
        for(Topic e: topics){
            this.add(context,e);
        }
        this.save(context);
    }



    //Remove

    /**
     * remove ingredient
     * @author Johanna Reidt
     * @param context
     * @param ingredient
     */
    default void remove(Context context, Ingredient ingredient){
        Buffer.getSingleton().removeFromBuffer(this, ingredient);
        DeleteFromDB.remove(context, this, ingredient);
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
    }

    /**
     * remove topic
     * @author Johanna Reidt
     * @param context
     * @param topic
     */
    default void remove(Context context, Topic topic){
        Buffer.getSingleton().removeFromBuffer(this, topic);
        DeleteFromDB.remove(context, this, topic);
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
    }

    /**
     * remove ingredients
     * @author Johanna Reidt
     * @param context
     * @param topics
     */
    default void removeTopics(Context context, List<Topic> topics){
        for(Topic e: topics){
            this.remove(context, e);
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
    }

    /**
     * remove topics
     * @author Johanna Reidt
     * @param context
     * @param ingredients
     */
    default void removeIngredients(Context context, List<Ingredient> ingredients){
        for(Ingredient e: ingredients){
            this.remove(context, e);
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);

    }





    boolean loadAlcoholic(Context context);

















































    //this Instance

    //JSON Formating Information from db

    default JSONArray getLiquidsJSON(){
        JSONArray json = new JSONArray();
        HashMap<String, Integer> nameVol = this.getIngredientNameToVolume();
        for(String e: nameVol.keySet()){
            JSONArray j = new JSONArray();
            j.put(e);
            j.put(nameVol.get(e));
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
                temp.put(this.getVolume(i));
                array.put(temp);
            }
            //jsonObject.put("liquids", array);
            BluetoothSingleton.getInstance().userDefineRecipe(
                    this.getID(),
                    this.getName(),
                    array,
                    activity);

            return true;
        } catch (JSONException| InterruptedException|NullPointerException e) {
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
                        temp.add(context, ig, volume);
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

    List<SQLRecipeIngredient> getRecipeIngredient();

    List<SQLRecipeTopic> getRecipeTopic();
}
