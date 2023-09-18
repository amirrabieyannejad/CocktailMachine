package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.tables.Tables;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 15:59
 * @project CocktailMachine
 */
public class DeleteFromDB {
    private static String TAG = "DeleteFromDB";

    private static SQLiteDatabase getReadableDatabase(Context context){
        return DatabaseConnection.init(context).getReadableDatabase();
    }
    private static SQLiteDatabase getWritableDatabase(Context context){
        return DatabaseConnection.init(context).getWritableDatabase();
    }

    //REMOVE in DB and buffer
    public static void removeRecipe(Context context, long id) {
        Log.i(TAG, "removeRecipe");
        Tables.TABLE_RECIPE.deleteElement(getWritableDatabase(context), id);
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(getWritableDatabase(context), id);
        Buffer.getSingleton().removeFromBuffer(Recipe.getRecipe(id));
    }

    public static void removeIngredient(Context context, long id) {
        Log.i(TAG, "removeIngredient");
        Tables.TABLE_INGREDIENT.deleteElement(getWritableDatabase(context), id);
        Tables.TABLE_INGREDIENT_URL.deleteWithOwnerId(getWritableDatabase(context), id);
        Buffer.getSingleton().removeFromBuffer(Ingredient.getIngredient(id));
    }

    public static void removePump(Context context, long id) {
        Log.i(TAG, "removePump");
        Tables.TABLE_PUMP.deleteElement(getWritableDatabase(context), id);
        Tables.TABLE_INGREDIENT_PUMP.deletePump(getWritableDatabase(context), id);
        // .deletePump(getWritableDatabase(), pump.getID());
        Buffer.getSingleton().removeFromBuffer(Pump.getPump(id));
    }


    public static void remove(Context context, Ingredient ingredient) {
        //TODO: delete from recipes
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT.deleteElement(getWritableDatabase(context), ingredient.getID());
        Tables.TABLE_INGREDIENT_URL.deleteWithOwnerId(getWritableDatabase(context), ingredient.getID());
        Buffer.getSingleton().removeFromBuffer(ingredient);
    }

    public static void remove(Context context, Recipe recipe) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE.deleteElement(getWritableDatabase(context), recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(getWritableDatabase(context), recipe.getID());
        Buffer.getSingleton().removeFromBuffer(recipe);
    }

    public static void remove(Context context, Pump pump) {
        //TODO: check available
        Log.i(TAG, "remove");

        Tables.TABLE_PUMP.deleteElement(getWritableDatabase(context), pump.getID());
        Tables.TABLE_INGREDIENT_PUMP.deletePump(getWritableDatabase(context), pump.getID());
        Buffer.getSingleton().removeFromBuffer(pump);

    }


    public static void remove(Context context, SQLRecipeTopic sQLRecipeTopic) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_TOPIC.deleteElement(getWritableDatabase(context), sQLRecipeTopic);
    }

    public static void remove(Context context, SQLTopic topic) {
        //TODO: delete from recipes
        Log.i(TAG, "remove");
        Tables.TABLE_TOPIC.deleteElement(getWritableDatabase(context), topic);
        Buffer.getSingleton().removeFromBuffer(topic);
    }

    public static void remove(Context context, SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(getWritableDatabase(context),recipeIngredient);
        Buffer.getSingleton().removeFromBuffer(recipeIngredient);
    }

    public static void remove(Context context, SQLIngredientPump ingredientPump) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(getWritableDatabase(context), ingredientPump);
        Buffer.getSingleton().removeFromBuffer(ingredientPump);
    }

    public static void remove(Context context, SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_URL.deleteElement(getWritableDatabase(context),recipeImageUrlElement);
    }

    public static void remove(Context context, SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_URL.deleteElement(getWritableDatabase(context),ingredientImageUrlElement);
    }


}
