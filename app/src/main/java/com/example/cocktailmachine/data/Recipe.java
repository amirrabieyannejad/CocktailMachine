package com.example.cocktailmachine.data;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
//import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.tables.BasicColumn;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.example.cocktailmachine.ui.model.helper.GetDialog;
import com.example.cocktailmachine.ui.model.helper.WaitingQueueCountDown;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public interface Recipe extends Comparable<Recipe>, DataBaseElement {
    final String TAG="Recipe";

    static BasicColumn<SQLRecipe>.DatabaseIterator getChunkAvIterator(Activity activity, int n){
        return GetFromDB.loadChunkAvRecipeIterator(activity, n);
    }

    WaitingQueueCountDown getWaitingQueueCountDown();
    void setWaitingQueueCountDown(Activity activity);

    void addDialogWaitingQueueCountDown(Activity activity, AlertDialog alertDialog);







    //Getter
    String getName();
    boolean isAlcoholic();


    List<Topic> getTopics(Context context);


    List<Long> getTopicIDs(Context context);


    /**
     * get ingredients
     * @author Johanna Reidt
     * @return
     */
    List<Ingredient> getIngredients(Context context);

    /**
     * get ingredient ids
     * @author Johanna Reidt
     * @return
     */
    List<Long> getIngredientIDs(Context context);

    /**
     * get ingreients names
     * @author Johanna Reidt
     * @return
     */
    List<String> getIngredientNames(Context context);

    /**
     * get list with "<ingredient_name>: <volume> ml"
     * @author Johanna Reidt
     * @return
     */
    List<String> getIngredientNameNVolumes(Context context);

    /**
     * get hashmap ingredient id -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<Ingredient, Integer> getIngredientToVolume(Context context);


    /**
     * get hashmap ingredient id -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<Long, Integer> getIngredientIDToVolume(Context context);

    /**
     * get hasmap ingredient name -> volume
     * @author Johanna Reidt
     * @return
     */
    HashMap<String, Integer> getIngredientNameToVolume(Context context);

    /**
     * get volume with ingredient
     * @author Johanna Reidt
     * @param ingredient
     * @return
     */
    int getVolume(Context context, Ingredient ingredient);
    /**
     * get volume with ingredient
     * @author Johanna Reidt
     * @param ingredientID
     * @return
     */
    int getVolume(Context context, long ingredientID);

    /**
     * gives availability
     * @author Johanna Reidt
     * @return
     */
    boolean isAvailable(Context context);








    //Setter

    List<SQLRecipeIngredient> getRecipeIngredients(Context context);

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

        Log.i(TAG, "replaceIngredients");
        for(Ingredient i: this.getIngredients(context)){
            if(!ingVol.containsKey(i)) {
                this.remove(context, i);
            }
        }
        this.save(context);
        for(Ingredient e: ingVol.keySet() ){
            if(e == null){
                Log.i(TAG, "replaceIngredients: tryed to add null as ingredient");
            }else {
                Integer temp = ingVol.get(e);
                if (temp == null) {
                    temp = -1;
                }
                this.add(context, e, temp);
            }
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);
    }

    /**
     * replace ingredients with volumes  or replace
     * @author Johanna Reidt
     * @param context
     * @param topics
     */
    default void replaceTopics(Context context, List<Topic> topics){
        for(Topic t: this.getTopics(context)){
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
        DeleteFromDB.remove(context, this, topic);
        //Buffer.getSingleton().removeFromBuffer(this, topic);
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

    /**
     * remove topics
     * @author Johanna Reidt
     * @param context
     * @param ingredients
     */
    default void removeIngredientIDs(Context context, List<Long> ingredients){
        for(Long id: ingredients){
            DeleteFromDB.removeElementFromRecipe(context, this, ModelType.INGREDIENT, id);
        }
        this.loadAvailable(context);
        this.loadAlcoholic(context);
        this.save(context);

    }





    boolean loadAlcoholic(Context context);

















































    //this Instance

    //JSON Formating Information from db

    default JSONArray getLiquidsJSON(Context context){
        JSONArray json = new JSONArray();
        HashMap<String, Integer> nameVol = this.getIngredientNameToVolume(context);
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
        //json.put("liquids", this.getLiquidsJSON());
        return json;
    }

    //Use Bluetooth

    /**
     * send to be mixed
     *   {"cmd": "make_recipe", "user": 8858, "recipe": "radler"}
     * TO DO:show topics to user!
     *
     * @author Johanna Reidt
     * @param activity
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
            e.printSt ackTrace();
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
     *
     * @author Johanna Reidt
     * @param activity
     * @return
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
            List<Ingredient> is = this.getIngredients(activity);
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getVolume(activity,i));
                array.put(temp);
            }
            //jsonObject.put("liquids", array);
            BluetoothSingleton.getInstance().userDefineRecipe(
                    this.getID(),
                    this.getName(),
                    array,
                    activity,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Log.i(TAG, "saving done");
                        }
                    });

            return true;
        } catch (JSONException| InterruptedException|NullPointerException e) {
            Log.e(TAG, "error", e);
        }
        return false;
    }



    /**
     * Sends to CocktailMachine to get saved.
     * {"cmd": "define_recipe", "user": 0, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}
     * <p>
     * <p>
     * TODO: find what this is doing :     {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}
     *
     * @author Johanna Reidt
     * @param activity
     * @param postexecute
     */
    default void sendSave(Activity activity, Postexecute postexecute){
        //TO DO: USE THIS AMIR  * ich habe in adminDefinePump das gleiche. wollen wir vlt.
        // * hier das verwenden vlt. durch:


        //JSONObject jsonObject = new JSONObject();


            /* jsonObject.put("cmd", "define_recipe");
            jsonObject.put("user", AdminRights.getUserId());
            jsonObject.put("recipe", this.getName());

             */
        save(activity);
        if(Dummy.isDummy){
            return;
        }
        try {
            JSONArray array = new JSONArray();
            List<Ingredient> is = this.getIngredients(activity);
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getVolume(activity,i));
                array.put(temp);
            }
            //jsonObject.put("liquids", array);
            BluetoothSingleton.getInstance().userDefineRecipe(
                    this.getID(),
                    this.getName(),
                    array,
                    activity,
                    postexecute);

        } catch (JSONException| InterruptedException|NullPointerException e) {
            Log.e(TAG, "error", e);
        }
    }


    /**
     * Sends to CocktailMachine to get saved.
     * {"cmd": "define_recipe", "user": 0, "name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}
     * <p>
     * <p>
     * TODO: find what this is doing :     {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}
     *
     * @author Johanna Reidt
     * @param activity
     * @param postexecute
     * @param error
     */
    default void sendSave(Activity activity, Postexecute postexecute, Postexecute error){
        //TO DO: USE THIS AMIR  * ich habe in adminDefinePump das gleiche. wollen wir vlt.
        // * hier das verwenden vlt. durch:


        //JSONObject jsonObject = new JSONObject();


            /* jsonObject.put("cmd", "define_recipe");
            jsonObject.put("user", AdminRights.getUserId());
            jsonObject.put("recipe", this.getName());

             */
        save(activity);
        if(Dummy.isDummy){
            return;
        }
        try {
            JSONArray array = new JSONArray();
            List<Ingredient> is = this.getIngredients(activity);
            for(Ingredient i: is){
                JSONArray temp = new JSONArray();
                temp.put(i.getName());
                temp.put(this.getVolume(activity,i));
                array.put(temp);
            }
            //jsonObject.put("liquids", array);
            BluetoothSingleton.getInstance().userDefineRecipe(
                    this.getID(),
                    this.getName(),
                    array,
                    activity,
                    postexecute);

        } catch (JSONException| InterruptedException|NullPointerException e) {
            Log.e(TAG, "error", e);
            error.post();
        }
    }

    /**
     * delete recipe on esp
     * @author Johanna Reidt
     * @param activity
     * @return
     */
    default boolean sendDelete(Activity activity){
        try {
            BluetoothSingleton.getInstance().userDeleteRecipe(this.getID(),
                    this.getName(),activity
            );
            return true;
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.e(TAG, "error", e);
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
     *
     * @author Johanna Reidt
     * @param context
     * @return
     * @throws NotInitializedDBException
     * @throws JSONException
     * @throws InterruptedException
     */
    static JSONArray getRecipesAsMessage(Context context) throws NotInitializedDBException, JSONException, InterruptedException {
        //TO DO: USE THIS AMIR * Ich glaube ist für mich setRecipe interessant? *
        JSONArray json = new JSONArray();
        for(Recipe r: getRecipes(context)){
            json.put(r.asMessage());
        }

        return json;
    }

    //Use Bluetooth

    /**
     * check last change
     * get recipes
     * get pump status
     * reload availbilities
     *
     * @author Johanna Reidt
     * @param activity
     */
    static void syncRecipeDBWithCocktailmachine(Activity activity){
        //TO DO: Sync, get alle recipes from bluetooth

        /*JSONArray answer = new JSONArray();
        try {
            setRecipes(answer);
        } catch (NotInitializedDBException | JSONException e) {
            Log.e(TAG, "error", e);
        }

         */

        CocktailMachine.updateRecipeListIfChanged(activity, new Postexecute(){
            @Override
            public void post() {
                Pump.readPumpStatus(activity, new Postexecute() {
                    @Override
                    public void post() {
                        //ExtraHandlingDB.localRefresh(activity);
                        Log.i(TAG, "syncRecipeDBWithCocktailmachine: done");
                    }
                });
            }
        });
        //Buffer.localRefresh(activity);
    }





















    //Methods to be called form bluetooth singleton to set informations in db
    /**
     * add Recipes to db from json array gotten from cocktail machine
     * [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]},
     * {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     *
     * @author Johanna Reidt
     * @param context
     * @param json
     * @throws JSONException
     */
    static void setRecipes(Context context,JSONArray json) throws JSONException{
        //TO DO: USE THIS AMIR **DONE**
        //[{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]}, {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
        for(int i=0; i<json.length(); i++){
            JSONObject j = json.optJSONObject(i);
            Recipe temp = Recipe.searchOrNew(context, j.optString("name", "Default"));
            JSONArray a = j.optJSONArray("liquids");
            List<Long> toRemove = temp.getIngredientIDs(context);
            if(a != null){
                for(int l=0; l<a.length(); l++){
                    JSONArray liq = a.optJSONArray(l);
                    if(liq!=null){
                        String name = a.getString(0);
                        int volume = a.getInt(1);
                        Ingredient ig = Ingredient.searchOrNew(context,name);
                        ig.save(context);
                        temp.add(context, ig, volume);
                        toRemove.remove(ig.getID());
                    }
                }
            }
            temp.removeIngredientIDs(context, toRemove);

            //TODO: what happens if ingredient removed
            temp.save(context);
        }
    }




    //Getting from db considering all recipes

    /**
     * Static access to recipes.
     * Get Recipe with id k.
     *
     * @author Johanna Reidt
     * @param context
     * @param id
     * @return
     */
    static Recipe getRecipe(Context context,long id) {
        return GetFromDB.loadRecipe(context, id);
    }

    /**
     * get recipe with name
     * @author Johanna Reidt
     * @param context
     * @param name
     * @return
     */
    static Recipe getRecipe(Context context, String name){
        return GetFromDB.loadRecipe(context, name);

    }



    /**
     * Static access to recipes.
     * Get available recipes.
     * @return list of recipes
     */
     static List<Recipe> getRecipes(Context context) {
        return (List<Recipe>) GetFromDB.loadAvailableRecipes(context);

     }


    /**
     * Static access to recipes.
     * Get all saved recipes.
     * @return list of recipes
     */
    static List<Recipe> getAllRecipes(Context context) {
        return (List<Recipe>) GetFromDB.loadRecipes(context);//Buffer.getSingleton().getRecipes(context);
    }

    /**
     * Static access to  availablerecipes.
     * Get all saved recipes.
     * @return list of recipes
     */
    static List<Recipe> getAvailableRecipes(Context context) {
        return (List<Recipe>) GetFromDB.loadAvailableRecipes(context);//Buffer.getSingleton().getRecipes(context);
    }


    /**
     * Static access to recipes.
     * Get available recipes with id in list of ids k
     * @param ids list of ids k
     * @return list of recipes
     */
    static List<Recipe> getRecipes(Context context, List<Long> ids) {
        return (List<Recipe>) GetFromDB.loadRecipes(context, ids);//Buffer.getSingleton().getRecipes(ids);
    }




    static BasicColumn<SQLRecipe>.DatabaseIterator getChunkIterator(Context context, int n){
        return GetFromDB.getRecipeChunkIterator(context, n);
    }


    // New


    /**
     * Make a new recipe.
     * @param name name of the new recipe
     * @return new recipe instance with given name. It is already saved in the database!
     */
    static Recipe makeNew(String name){
        return new SQLRecipe(name);
    }

    /**
     * Make a new recipe.
     * @return new recipe instance with given name. It is already saved in the database!
     */
    static Recipe makeNew(){
        return new SQLRecipe();
    }

    //recipe.getVolume(context,ingredient)


    /**
     * get recipe with name
     * or create a new one with name
     * @author Johanna Reidt
     * @param context
     * @param name
     * @return
     */
    static Recipe searchOrNew(Context context, String name){
        Recipe recipe = Recipe.getRecipe(context,name);
        if(recipe == null){
            recipe= Recipe.makeNew(name);
            recipe.save(context);
        }
        return recipe;
    }

    List<SQLRecipeTopic> getRecipeTopics(Context context);




    List<String> getTopicNames(Context context);


    /**
     * deletes all ingredients and topics
     * @author Johanna Reidt
     */
    default void clean(Context context){
        for(SQLRecipeTopic rt: this.getRecipeTopics(context)) {
            rt.delete(context);
        }
        for(SQLRecipeIngredient rt: this.getRecipeIngredients(context)) {
            rt.delete(context);
        }
    }
}
