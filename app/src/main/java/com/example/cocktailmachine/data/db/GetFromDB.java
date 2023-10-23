package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Long4;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;
import com.example.cocktailmachine.data.db.tables.BasicColumn;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.db.tables.TopicTable;
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

    private static SQLiteDatabase getReadableDatabase(Context context){
        return DatabaseConnection.init(context).getReadableDatabase();
    }

    static Ingredient loadIngredient(Context context, long id){
       // Log.v(TAG, "loadIngredient");
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static List<SQLIngredient> loadIngredients(Context context, String needle){
       // Log.v(TAG, "loadIngredients");
        return Tables.TABLE_INGREDIENT.getElements(getReadableDatabase(context), needle);
    }
    static List<? extends Ingredient> loadIngredients(Context context, List<Long> ids){
        // Log.v(TAG, "loadIngredients");
        return Tables.TABLE_INGREDIENT.getElements(getReadableDatabase(context), ids);
    }

    static Ingredient loadIngredient(Context context, String name){
       // Log.v(TAG, "loadIngredient");
        List<SQLIngredient> ings = loadIngredients(context, name);
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
        return Tables.TABLE_INGREDIENT.getChunkIterator(getReadableDatabase(context) ,n);
    }

    public static Recipe loadRecipes(Context context){
        // Log.v(TAG, "loadRecipe");
        //res.loadAvailable(context);
        return (Recipe) Tables.TABLE_RECIPE.getAllElements(getReadableDatabase(context));
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
        return Tables.TABLE_RECIPE.getChunkIterator(getReadableDatabase(context) ,n);
    }

    static Topic loadTopic(Context context,long id){
       // Log.v(TAG, "loadTopic");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }

    static List<SQLTopic> loadTopics(Context context, String needle) {
       // Log.v(TAG, "loadTopics");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), needle);
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
        return Tables.TABLE_TOPIC.getChunkIterator(getReadableDatabase(context), n);
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
        return Tables.TABLE_PUMP.getChunkIterator(getReadableDatabase(context), n);
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
        SQLiteDatabase db = getReadableDatabase(context);
        List<? extends Topic> res = Tables.TABLE_TOPIC.getElements(db, Tables.TABLE_RECIPE_TOPIC.getTopicIDs(db, recipe));
        db.close();
        return res;
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
        SQLiteDatabase db = getReadableDatabase(context);
        List<Long> res = Tables.TABLE_TOPIC.getIDs(db, Tables.TABLE_RECIPE_TOPIC.getTopicIDs(db, recipe));
        db.close();
        return res;
    }

    public static List<String> loadTopicTitles(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);
        List<String> res = Tables.TABLE_TOPIC.getNames(db);
        db.close();
        return res;
    }
}
