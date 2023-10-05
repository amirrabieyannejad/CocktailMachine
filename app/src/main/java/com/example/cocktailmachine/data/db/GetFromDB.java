package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.exceptions.AccessDeniedException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.enums.AdminRights;

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
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), needle);
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

    static Iterator<List<SQLIngredient>> loadIngredientChunkIterator(Context context, int n){
        Log.i(TAG, "loadIngredientChunkIterator");
        return Tables.TABLE_INGREDIENT.getChunkIterator(getReadableDatabase(context), n);
    }


    static Recipe loadRecipe(Context context,long id){
        Log.i(TAG, "loadRecipe");
        //res.loadAvailable(context);
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), id);
    }


    static List<SQLRecipe> loadRecipes(Context context, String needle) {
        Log.i(TAG, "loadIngredients");
        return Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), needle);
    }

    static Recipe loadRecipe(Context context, String name){
        Log.i(TAG, "loadIngredient");
        List<SQLRecipe> ings = loadRecipes(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Recipe) ings.get(0);
    }

    static Iterator<SQLRecipe> loadRecipeIterator(Context context){
        Log.i(TAG, "loadIngredientIterator");
        return Tables.TABLE_RECIPE.getIterator(getReadableDatabase(context));
    }

    static Iterator<List<SQLRecipe>> loadRecipeChunkIterator(Context context, int n){
        Log.i(TAG, "loadIngredientChunkIterator");
        return Tables.TABLE_RECIPE.getChunkIterator(getReadableDatabase(context), n);
    }

    static Topic loadTopic(Context context,long id){
        Log.i(TAG, "loadTopic");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }

    static List<Topic> loadTopics(Context context, String needle) {
        Log.i(TAG, "loadIngredients");
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), needle);
    }

    static Topic loadTopic(Context context, String name){
        Log.i(TAG, "loadIngredient");
        List<SQLRecipe> ings = loadRecipes(context, name);
        if(ings.isEmpty()){
            return null;
        }
        return (Topic) ings.get(0);
    }

    static Iterator<SQLTopic> loadTopicIterator(Context context){
        Log.i(TAG, "loadIngredientIterator");
        return Tables.TABLE_TOPIC.getIterator(getReadableDatabase(context));
    }

    static Iterator<List<SQLTopic>> loadTopicChunkIterator(Context context, int n){
        Log.i(TAG, "loadIngredientChunkIterator");
        return Tables.TABLE_TOPIC.getChunkIterator(getReadableDatabase(context), n);
    }


    static Pump loadPump(Context context, long id){
        Log.i(TAG, "loadTopic");
        return Tables.TABLE_PUMP.getElement(getReadableDatabase(context), id);
    }
}
