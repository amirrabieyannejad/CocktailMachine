package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.cocktailmachine.data.db.tables.BasicColumn;
import com.example.cocktailmachine.data.db.tables.Tables;
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
        Log.i(TAG, "loadIngredient");
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static List<SQLIngredient> loadIngredients(Context context, String needle){
        Log.i(TAG, "loadIngredients");
        return Tables.TABLE_INGREDIENT.getElements(getReadableDatabase(context), needle);
    }

    static Ingredient loadIngredient(Context context, String name){
        Log.i(TAG, "loadIngredient");
        List<SQLIngredient> ings = loadIngredients(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Ingredient) ings.get(0);
    }

    static Iterator<SQLIngredient> loadIngredientIterator(Context context){
        Log.i(TAG, "loadIngredientIterator");
        return Tables.TABLE_INGREDIENT.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLIngredient>.DatabaseIterator loadIngredientChunkIterator(Context context, int n){
        Log.i(TAG, "loadIngredientChunkIterator");
        return Tables.TABLE_INGREDIENT.getChunkIterator(getReadableDatabase(context) ,n);
    }


    static Recipe loadRecipe(Context context,long id){
        Log.i(TAG, "loadRecipe");
        //res.loadAvailable(context);
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), id);
    }


    static List<SQLRecipe> loadRecipes(Context context, String needle) {
        Log.i(TAG, "loadRecipes");
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), needle);
    }

    static Recipe loadRecipe(Context context, String name){
        Log.i(TAG, "loadRecipe");
        List<SQLRecipe> ings = loadRecipes(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Recipe) ings.get(0);
    }

    static Iterator<SQLRecipe> loadRecipeIterator(Context context){
        Log.i(TAG, "loadRecipeIterator");
        return Tables.TABLE_RECIPE.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLRecipe>.DatabaseIterator loadRecipeChunkIterator(Context context, int n){
        Log.i(TAG, "loadRecipeChunkIterator");
        return Tables.TABLE_RECIPE.getChunkIterator(getReadableDatabase(context) ,n);
    }

    static Topic loadTopic(Context context,long id){
        Log.i(TAG, "loadTopic");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }

    static List<SQLTopic> loadTopics(Context context, String needle) {
        Log.i(TAG, "loadTopics");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), needle);
    }

    static List<SQLRecipeTopic> loadRecipeTopics(Context context, List<Recipe> recipes) {
        Log.i(TAG, "loadTopics");
        //return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), needle);
        List<SQLRecipeTopic> res = new ArrayList<>();
        for(Recipe r: recipes) {

            res.addAll(Tables.TABLE_RECIPE_TOPIC.getTopics(getReadableDatabase(context), (SQLRecipe) r));
        }
        return res;
    }

    static Topic loadTopic(Context context, String name){
        Log.i(TAG, "loadTopic");
        List<SQLTopic> ings = loadTopics(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Topic) ings.get(0);
    }

    static Iterator<SQLTopic> loadTopicIterator(Context context){
        Log.i(TAG, "loadTopicIterator");
        return Tables.TABLE_TOPIC.getIterator(getReadableDatabase(context));
    }

    static BasicColumn<SQLTopic>.DatabaseIterator loadTopicChunkIterator(Context context, int n){
        Log.i(TAG, "loadTopicChunkIterator");
        return Tables.TABLE_TOPIC.getChunkIterator(getReadableDatabase(context), n);
    }


    static Pump loadPump(Context context, long id){
        Log.i(TAG, "loadPump");
        return Tables.TABLE_PUMP.getElement(getReadableDatabase(context), id);
    }

    static Iterator<SQLPump> loadPumpIterator(Context context){
        Log.i(TAG, "loadPumpIterator");
        return Tables.TABLE_PUMP.getIterator(getReadableDatabase(context));
    }

    static Iterator<List<SQLPump>> loadPumpChunkIterator(Context context, int n){
        Log.i(TAG, "loadPumpChunkIterator");
        return Tables.TABLE_PUMP.getChunkIterator(getReadableDatabase(context), n);
    }


    static SQLRecipeIngredient loadRecipeIngredient(Context context, long id){

        Log.i(TAG, "loadRecipeIngredient");
        return Tables.TABLE_RECIPE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static List<SQLRecipeIngredient> loadRecipeIngredientFromIngredient(
            Context context, List<Long> ids){
        Log.i(TAG, "loadRecipeIngredientFromIngredient");
        return Tables.TABLE_RECIPE_INGREDIENT.getWithIngredientsOnlyFullRecipe(getReadableDatabase(context), ids);
    }





}
