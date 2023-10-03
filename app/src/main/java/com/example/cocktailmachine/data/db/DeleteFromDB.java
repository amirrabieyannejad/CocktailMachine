package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.navigation.ActionOnlyNavDirections;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 15:59
 * @project CocktailMachine
 */
public class DeleteFromDB {
    private static final String TAG = "DeleteFromDB";


    private static SQLiteDatabase getWritableDatabase(Context context){
        return DatabaseConnection.init(context).getWritableDatabase();
    }

    //REMOVE in DB and buffer
    public static void remove(Context context, Ingredient ingredient) {
        //TO DO: delete from recipes
        Log.i(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_INGREDIENT.deleteElement(db, ingredient.getID());
        Tables.TABLE_INGREDIENT_URL.deleteWithOwnerId(db, ingredient.getID());
        Buffer.getSingleton(context).removeFromBuffer(ingredient);
    }

    public static void remove(Context context, Recipe recipe) {
        Log.i(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_RECIPE.deleteElement(db, recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(db, recipe.getID());
        Buffer.getSingleton(context).removeFromBuffer(recipe);
    }

    public static void remove(Context context, Pump pump) {
        //TO DO: check available
        Log.i(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_PUMP.deleteElement(db, pump.getID());
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(db, Buffer.getSingleton().getIngredientPump(pump));
        Buffer.getSingleton(context).removeFromBuffer(pump);
    }


    public static void remove(Context context, SQLRecipeTopic sQLRecipeTopic) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_TOPIC.deleteElement(getWritableDatabase(context), sQLRecipeTopic);
        Buffer.getSingleton(context).removeFromBuffer(sQLRecipeTopic);
    }

    public static void remove(Context context, Recipe recipe, Topic topic) {
        Log.i(TAG, "remove");
        SQLRecipeTopic rt = Buffer.getSingleton(context).get(recipe, topic);
        remove(context, rt);
        //Buffer.getSingleton(context).removeFromBuffer(topic);
    }

    public static void remove(Context context, SQLTopic topic) {
        //TODO: delete from recipes
        Log.i(TAG, "remove");
        Tables.TABLE_TOPIC.deleteElement(getWritableDatabase(context), topic);
        Buffer.getSingleton(context).removeFromBuffer(topic);
    }

    public static void remove(Context context, SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(getWritableDatabase(context),recipeIngredient);
        Buffer.getSingleton(context).removeFromBuffer(recipeIngredient);
    }
    public static void remove(Context context, Recipe recipe, Ingredient ingredient) {
        Log.i(TAG, "remove");
        remove(context, Buffer.getSingleton(context).get(recipe, ingredient));
        //Buffer.getSingleton(context).removeFromBuffer(recipe, ingredient);
    }

    public static void remove(Context context, SQLIngredientPump ingredientPump) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(getWritableDatabase(context), ingredientPump);
        Buffer.getSingleton(context).removeFromBuffer(ingredientPump);
    }

    public static void remove(Context context, SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_URL.deleteElement(getWritableDatabase(context),recipeImageUrlElement);
    }

    public static void remove(Context context, SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_URL.deleteElement(getWritableDatabase(context),ingredientImageUrlElement);
    }


    public static void removeAll(Context context) {
        DatabaseConnection.init(context).emptyAll();
        Buffer.getSingleton(context).noMemory();
    }
}
