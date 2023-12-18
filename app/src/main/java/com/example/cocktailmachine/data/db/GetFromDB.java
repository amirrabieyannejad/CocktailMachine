package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.data.db.tables.BasicColumn;
import com.example.cocktailmachine.data.db.tables.IngredientTable;
import com.example.cocktailmachine.data.db.tables.PumpTable;
import com.example.cocktailmachine.data.db.tables.RecipeTable;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.db.tables.TopicTable;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 13:16
 * @project CocktailMachine
 */
public class GetFromDB {
    private static String TAG = "GetFromDB";

    public static SQLiteDatabase getReadableDatabase(Context context){
        return DatabaseConnection.init(context).getReadableDatabase();
    }

    public static Ingredient loadIngredient(Context context, long id){
       // Log.v(TAG, "loadIngredient");
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    public static List<? extends Ingredient> loadIngredients(Context context, String needle){
       // Log.v(TAG, "loadIngredients");
        return Tables.TABLE_INGREDIENT.getElements(getReadableDatabase(context), needle);
    }
    static List<? extends Ingredient> loadIngredients(Context context, List<Long> ids){
        // Log.v(TAG, "loadIngredients");
        return Tables.TABLE_INGREDIENT.getElements(getReadableDatabase(context), ids);
    }

    static Ingredient loadIngredient(Context context, String name){
       // Log.v(TAG, "loadIngredient");
        List<? extends Ingredient> ings = loadIngredients(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Ingredient) ings.get(0);
    }

    static Iterator<SQLIngredient> loadIngredientIterator(Context context){
       // Log.v(TAG, "loadIngredientIterator");
        return Tables.TABLE_INGREDIENT.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLIngredient>.DatabaseIterator loadIngredientChunkIterator(Context context, int n){
       // Log.v(TAG, "loadIngredientChunkIterator");
        return Tables.TABLE_INGREDIENT.getChunkIterator(context ,n);
    }

    public static List<? extends Recipe> loadRecipes(Context context){
        // Log.v(TAG, "loadRecipe");
        //res.loadAvailable(context);
        return Tables.TABLE_RECIPE.getAllElements(getReadableDatabase(context));
    }

    public static Recipe loadRecipe(Context context, long id){
       // Log.v(TAG, "loadRecipe");
        //res.loadAvailable(context);
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), id);
    }


    static List<SQLRecipe> loadRecipes(Context context, String needle) {
       // Log.v(TAG, "loadRecipes");
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), needle);
    }
    public static List<? extends Recipe> loadRecipes(Context context, List<Long> ids) {
        // Log.v(TAG, "loadRecipes");
        return Tables.TABLE_RECIPE.getElements(getReadableDatabase(context), ids);
    }

    public static Recipe loadRecipe(Context context, String name){
       // Log.v(TAG, "loadRecipe");
        List<SQLRecipe> ings = loadRecipes(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Recipe) ings.get(0);
    }

    static Iterator<SQLRecipe> loadRecipeIterator(Context context){
       // Log.v(TAG, "loadRecipeIterator");
        return Tables.TABLE_RECIPE.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLRecipe>.DatabaseIterator loadRecipeChunkIterator(Context context, int n){
       // Log.v(TAG, "loadRecipeChunkIterator");
        return Tables.TABLE_RECIPE.getChunkIterator(context ,n);
    }

    static Topic loadTopic(Context context,long id){
       // Log.v(TAG, "loadTopic");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }

    static List<SQLTopic> loadTopics(Context context, String needle) {
       // Log.v(TAG, "loadTopics");
        return Tables.TABLE_TOPIC.getElements(getReadableDatabase(context), needle);
    }

    static List<SQLRecipeTopic> loadRecipeTopics(Context context, List<Recipe> recipes) {
       // Log.v(TAG, "loadTopics");
        //return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), needle);
        List<SQLRecipeTopic> res = new ArrayList<>();
        for(Recipe r: recipes) {
            res.addAll(Tables.TABLE_RECIPE_TOPIC.getTopics(getReadableDatabase(context), r));
        }
        return res;
    }

    static Topic loadTopic(Context context, String name){
       // Log.v(TAG, "loadTopic");
        List<SQLTopic> ings = loadTopics(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Topic) ings.get(0);
    }

    static Iterator<SQLTopic> loadTopicIterator(Context context){
       // Log.v(TAG, "loadTopicIterator");
        return Tables.TABLE_TOPIC.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLTopic>.DatabaseIterator loadTopicChunkIterator(Context context, int n){
       // Log.v(TAG, "loadTopicChunkIterator");
        return Tables.TABLE_TOPIC.getChunkIterator(context, n);
    }


    static Pump loadPump(Context context, long id){
       // Log.v(TAG, "loadPump");
        return Tables.TABLE_PUMP.getElement(getReadableDatabase(context), id);
    }

    static Iterator<SQLPump> loadPumpIterator(Context context){
       // Log.v(TAG, "loadPumpIterator");
        return Tables.TABLE_PUMP.getIterator(getReadableDatabase(context));
    }

    static Iterator<List<SQLPump>> loadPumpChunkIterator(Context context, int n){
       // Log.v(TAG, "loadPumpChunkIterator");
        return Tables.TABLE_PUMP.getChunkIterator(context, n);
    }


    static SQLRecipeIngredient loadRecipeIngredient(Context context, long id){

       // Log.v(TAG, "loadRecipeIngredient");
        return Tables.TABLE_RECIPE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static List<SQLRecipeIngredient> loadRecipeIngredientFromIngredient(
            Context context, List<Long> ids){
       // Log.v(TAG, "loadRecipeIngredientFromIngredient");
        return Tables.TABLE_RECIPE_INGREDIENT.getWithIngredientsOnlyFullRecipe(getReadableDatabase(context), ids);
    }


    public static HashMap<String, Long> loadIngredientPumpSet(Context context) {
       // Log.v(TAG, "loadIngredientPumpSet");
        return Tables.TABLE_INGREDIENT.getHashIngredientNameToID(getReadableDatabase(context));
    }

    public static List<? extends Ingredient> getAvailableIngredients(Context context, List<Long> ids) {
        //List<Long> available = Tables.TABLE_INGREDIENT.getIDsIn(getReadableDatabase(context), Tables.TABLE_INGREDIENT)
        return Tables.TABLE_INGREDIENT.getAvailableElements(getReadableDatabase(context), ids);
    }

    public static List<? extends Ingredient> getAvailableIngredients(Context context) {
        //List<Long> available = Tables.TABLE_INGREDIENT.getIDsIn(getReadableDatabase(context), Tables.TABLE_INGREDIENT)
        return Tables.TABLE_INGREDIENT.getAvailableElements(getReadableDatabase(context));
    }

    public static List<SQLRecipeIngredient> loadRecipeIngredients(Context context) {
        return Tables.TABLE_RECIPE_INGREDIENT.getAllElements(getReadableDatabase(context));
    }

    public static List<SQLRecipeIngredient> loadRecipeIngredientFromRecipe(Context context, Recipe recipe) {
        List<Long> ids = new ArrayList<>();
        ids.add(recipe.getID());
        return Tables.TABLE_RECIPE_INGREDIENT.getWithRecipes(getReadableDatabase(context), ids);
    }

    public static List<Long> loadIngredientIDs(Context context) {
        return Tables.TABLE_INGREDIENT.getIDs(getReadableDatabase(context));
    }

    public static List<? extends Topic> getTopics(Context context){
         return Tables.TABLE_TOPIC.getAllElements(getReadableDatabase(context));
    }
    public static List<? extends Topic> getTopics(Context context, Recipe recipe){
        return Tables.TABLE_TOPIC.getElements(getReadableDatabase(context), Tables.TABLE_RECIPE_TOPIC.getTopicIDs(getReadableDatabase(context), recipe));
    }

    public static List<? extends Topic> getTopics(Context context, List<Long> ids){
        try {
            List<Object> obj = new ArrayList<>(ids);
            return Tables.TABLE_TOPIC.getElementsIn(getReadableDatabase(context), TopicTable._ID,obj);
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
    }

    public static List< ? extends Recipe> loadAvailableRecipes(Context context) {
        return Tables.TABLE_RECIPE.getAvailable(getReadableDatabase(context));
    }

    public static Topic getTopic(Context context, long id) {
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }
    public static Topic getTopic(Context context, String name) {
        return (Topic) Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), name);
    }

    public static List<Long> getTopicIDs(Context context, Recipe recipe) {
        //SQLiteDatabase db = getReadableDatabase(context);
        List<Long> res = Tables.TABLE_TOPIC.getIDs(getReadableDatabase(context), Tables.TABLE_RECIPE_TOPIC.getTopicIDs(getReadableDatabase(context), recipe));
        return res;
    }


    public static List<Long> getTopicIDs(Context context) {
        //SQLiteDatabase db = getReadableDatabase(context);
        List<Long> res = Tables.TABLE_TOPIC.getIDs(getReadableDatabase(context));
        return res;
    }

    public static List<String> loadTopicTitles(Context context) {
        //SQLiteDatabase db = getReadableDatabase(context);
        List<String> res = Tables.TABLE_TOPIC.getNames(getReadableDatabase(context));
        //db.close();
        return res;
    }

    public static List<? extends Pump> getPumps(Context context) {
        return Tables.TABLE_PUMP.getAllElements(getReadableDatabase(context));
    }

    public static Pump getPump(Context context, long id) {
        return Tables.TABLE_PUMP.getElement(getReadableDatabase(context), id);
    }

    public static Pump getPumpWithSlot(Context context, int slot) {
        List<? extends Pump> res =  Tables.TABLE_PUMP.getPumpWithSlot(getReadableDatabase(context), slot);
        if(res.isEmpty()){
            return null;
        }
        return (Pump) res.get(0);
    }

    public static List<? extends Ingredient> loadIngredients(Context context) {
        return Tables.TABLE_INGREDIENT.getAllElements(getReadableDatabase(context));
    }

    public static List<String> getIngredientNames(Context context) {
        return  Tables.TABLE_INGREDIENT.getNames(getReadableDatabase(context));
    }


    public static SQLIngredientPump getIngredientPump(Context context, Pump pump) {
        List<SQLIngredientPump> res = Tables.TABLE_INGREDIENT_PUMP.getElements(getReadableDatabase(context), pump);
        if(res.isEmpty()){
            return null;
        }
        return res.get(0);
    }

    public static SQLRecipeTopic getRecipeTopic(Context context, Recipe recipe, Topic topic) {
        try {
            List<SQLRecipeTopic> res = Tables.TABLE_RECIPE_TOPIC.getElements(getReadableDatabase(context), recipe, topic);
            if(res.isEmpty()){
                return null;
            }
            return res.get(0);
        } catch (NoSuchColumnException e) {
            return null;
        }
    }

    public static SQLRecipeIngredient getRecipeIngredient(Context context, Recipe recipe, Ingredient ingredient) {
        try {
            List<SQLRecipeIngredient> res =Tables.TABLE_RECIPE_INGREDIENT.getElements(getReadableDatabase(context), recipe, ingredient);
            if(res.isEmpty()){
                return null;
            }
            return res.get(0);
        } catch (NoSuchColumnException e) {
            return null;
        }
    }

    public static List<SQLIngredientPump> getIngredientPumps(Context context) {
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(getReadableDatabase(context));
    }

    public static List<Long> getIngredientPumpsIDs(Context context) {
        return Tables.TABLE_INGREDIENT_PUMP.getIDs(getReadableDatabase(context));
    }

    public static List<String> getTopicNames(Context context) {
        return Tables.TABLE_TOPIC.getNames(getReadableDatabase(context));
    }

    public static List<SQLRecipeTopic> getRecipeTopics(Context context, SQLRecipe sqlRecipe) {
        return Tables.TABLE_RECIPE_TOPIC.getElements(getReadableDatabase(context), sqlRecipe);
    }

    public static List<Ingredient> getIngredients(Context context, Recipe recipe) {
        SQLiteDatabase db = getReadableDatabase(context);
        if(!db.isOpen()){
            throw new RuntimeException("newly open not open");
        }
        return (List<Ingredient>) Tables.TABLE_INGREDIENT.getElements(db, recipe);
    }

    public static List<Long> getIngredientIDs(Context context) {
        return Tables.TABLE_INGREDIENT.getIDs(getReadableDatabase(context));
    }

    public static int getVolume(Context context, SQLRecipe recipe, Ingredient ingredient) {
        try {
            return Tables.TABLE_RECIPE_INGREDIENT.getVolume(getReadableDatabase(context), recipe, ingredient);
        } catch (TooManyTimesSettedIngredientEcxception e) {
            ExtraHandlingDB.deleteDoubleRecipeIngredientSettingsAndNulls(context);
            return -1;
        }
    }

    public static List<SQLRecipeIngredient> getRecipeIngredients(Context context, SQLRecipe sqlRecipe) {
        return Tables.TABLE_RECIPE_INGREDIENT.getElements(getReadableDatabase(context), sqlRecipe);
    }

    public static List<String> getIngredientNameNVolumes(Context context, SQLRecipe sqlRecipe) {
        List<SQLRecipeIngredient> q = getRecipeIngredients(context, sqlRecipe);
        List<String> res = new ArrayList<>();
        for(SQLRecipeIngredient ri: q){
            res.add(ri.getIngredient(context).getName()+": "+ri.getVolume());
        }
        return res;
    }

    public static HashMap<Ingredient, Integer> getIngredientToVolume(Context context, SQLRecipe sqlRecipe) {
        List<SQLRecipeIngredient> q = getRecipeIngredients(context, sqlRecipe);
        HashMap<Ingredient, Integer> res = new HashMap<>();
        for(SQLRecipeIngredient ri: q){
            res.put(ri.getIngredient(context), ri.getVolume());
        }
        return res;
    }

    public static HashMap<Long, Integer> getIngredientIDToVolume(Context context, SQLRecipe sqlRecipe) {
        List<SQLRecipeIngredient> q = getRecipeIngredients(context, sqlRecipe);
        HashMap<Long, Integer> res = new HashMap<>();
        for(SQLRecipeIngredient ri: q){
            res.put(ri.getIngredientID(), ri.getVolume());
        }
        return res;
    }

    public static HashMap<String, Integer> getIngredientNameToVolume(Context context, SQLRecipe sqlRecipe) {
        List<SQLRecipeIngredient> q = getRecipeIngredients(context, sqlRecipe);
        HashMap<String, Integer> res = new HashMap<>();
        for(SQLRecipeIngredient ri: q){
            res.put(ri.getIngredient(context).getName(), ri.getVolume());
        }
        return res;
    }


    /**
     * check if element with id from type modeltype is deleted
     * @author Johanna Reidt
     * @param modelType
     * @param ID
     * @return
     */
    public static boolean checkDeleted(Context context, ModelType modelType, Long ID){
        switch (modelType){
            case RECIPE:
                return Recipe.getRecipe(context,ID)==null;
            case PUMP:
                return Pump.getPump(context,ID)==null;
            case TOPIC:
                return Topic.getTopic(context,ID)==null;
            case INGREDIENT:
                return Ingredient.getIngredient(context, ID)==null;
        }
        return false;
    }


    public static Iterator<SQLRecipeIngredient> getRecipeIngredientIterator(Context context) {
        return Tables.TABLE_RECIPE_INGREDIENT.getIterator(getReadableDatabase(context));
    }

    public static List<Long> getRecipeIngredientIDs(Context context) {
        return Tables.TABLE_RECIPE_INGREDIENT.getIDs(getReadableDatabase(context));
    }

    public static Iterator<List<SQLRecipe>> getRecipeChunkIterator(Context context, int n) {
        return Tables.TABLE_RECIPE.getChunkIterator(context, n, RecipeTable.COLUMN_NAME_NAME);
    }

    public static Iterator<List<SQLIngredient>> getIngredientChunkIterator(Context context, int n) {
        return Tables.TABLE_INGREDIENT.getChunkIterator(context, n, IngredientTable.COLUMN_NAME_NAME);
    }

    public static Iterator<List<SQLTopic>> getTopicChunkIterator(Context context, int n) {
        return Tables.TABLE_TOPIC.getChunkIterator(context, n, TopicTable.COLUMN_NAME_NAME);
    }

    public static Iterator<List<SQLPump>> getPumpChunkIterator(Context context, int n) {
        return Tables.TABLE_PUMP.getChunkIterator(context, n, PumpTable.COLUMN_TYPE_SLOT_ID);
    }
}
