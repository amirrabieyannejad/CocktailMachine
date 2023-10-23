package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 15:43
 * @project CocktailMachine
 */
public class AddOrUpdateToDB {
    private static String TAG = "AddOrUpdateToDB";

    public static void localRefresh(Context context){
        DatabaseConnection.init(context).refreshAvailable(context);
    }

    /*
    private static SQLiteDatabase getReadableDatabase(Context context){
        return DatabaseConnection.init(context).getReadableDatabase();
    }

     */
    private static SQLiteDatabase getWritableDatabase(Context context){
        try {
            return DatabaseConnection.getSingleton().getWritableDatabase();
        } catch (NotInitializedDBException e) {
            return DatabaseConnection.init(context).getWritableDatabase();
        }
    }


    //ADD OR UPDATE
    public static void addIngredientImageUrl(Context context, long ingredientId, String url) {
       // Log.v(TAG, "addIngredientImageUrl");
        // getWritableDatabase(context).
        Tables.TABLE_INGREDIENT_URL.addElement(getWritableDatabase(context),ingredientId, url);
    }

    public static void addOrUpdate(Context context, SQLIngredient ingredient) {
       // Log.v(TAG, "addOrUpdate: "+ingredient.toString());
        if(ingredient.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT.updateElement(getWritableDatabase(context), ingredient);
            ingredient.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            ingredient.setID(Tables.TABLE_INGREDIENT.addElement(getWritableDatabase(context), ingredient));
            //ingredients.add(ingredient);
            //Buffer.getSingleton(context).addToBuffer(ingredient);
            ingredient.wasSaved();
        }

    }

    public static void addOrUpdate(Context context, SQLRecipe recipe) {
       // Log.v(TAG, "addOrUpdate: "+recipe.toString());
        if(recipe.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE.updateElement(getWritableDatabase(context), recipe);
            recipe.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            recipe.setID(Tables.TABLE_RECIPE.addElement(getWritableDatabase(context), recipe));
            //Buffer.getSingleton(context).addToBuffer(recipe);
            recipe.wasSaved();
        }
    }

    public static void addOrUpdate(Context context, SQLTopic topic) {
       // Log.v(TAG, "addOrUpdate: "+topic.toString());
        if(topic.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_TOPIC.updateElement(getWritableDatabase(context), topic);
            topic.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            topic.setID(Tables.TABLE_TOPIC.addElement(getWritableDatabase(context), topic));
            //Buffer.getSingleton(context).addToBuffer(topic);
            topic.wasSaved();
        }
    }

    public static void addOrUpdate(Context context, SQLPump pump) {
        //TODO: check available
       // Log.v(TAG, "addOrUpdate: "+pump.toString());
        if(pump.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_PUMP.updateElement(getWritableDatabase(context), pump);
            pump.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            pump.setID(Tables.TABLE_PUMP.addElement(getWritableDatabase(context), pump));
            ////Buffer.getSingleton(context).addToBuffer(pump);
            pump.wasSaved();
            //Buffer.getSingleton(context).addToBuffer(pump);
        }
    }

    public static void addOrUpdate(Context context, SQLRecipeTopic recipeTopic) {
       // Log.v(TAG, "addOrUpdate: "+recipeTopic.toString());
        if(recipeTopic.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_TOPIC.updateElement(getWritableDatabase(context), recipeTopic);
            recipeTopic.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            recipeTopic.setID(Tables.TABLE_RECIPE_TOPIC.addElement(getWritableDatabase(context), recipeTopic));
            recipeTopic.wasSaved();
            //Buffer.getSingleton(context).addToBuffer(recipeTopic);
        }
    }

    public static void addOrUpdate(Context context, SQLIngredientPump ingredientPump) {
       // Log.v(TAG, "addOrUpdate: "+ingredientPump);
        if(ingredientPump.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_PUMP.updateElement(getWritableDatabase(context), ingredientPump);
            ingredientPump.wasSaved();
            //ingredientPumps.remove(ingredientPump);
        }else{
           // Log.v(TAG, "first time saving");
            ingredientPump.setID(Tables.TABLE_INGREDIENT_PUMP.addElement(getWritableDatabase(context), ingredientPump));
            ingredientPump.wasSaved();
            //Buffer.getSingleton(context).addToBuffer(ingredientPump);
        }
       // Log.v(TAG, "addOrUpdate: issaved???? "+ DatabaseConnection.init(context).loadIngredientPumps());
    }

    public static void addOrUpdate(Context context, SQLRecipeIngredient recipeIngredient) {
       // Log.v(TAG, "addOrUpdate: "+recipeIngredient.toString());
        if(recipeIngredient.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(getWritableDatabase(context), recipeIngredient);
            recipeIngredient.wasSaved();;
            //recipeIngredients.remove(recipeIngredient);
        }else{
           // Log.v(TAG, "first time saving");
            recipeIngredient.setID(Tables.TABLE_RECIPE_INGREDIENT.addElement(getWritableDatabase(context), recipeIngredient));
            //Buffer.getSingleton().addToBuffer(recipeIngredient);
            recipeIngredient.wasSaved();
            //Buffer.getSingleton(context).addToBuffer(recipeIngredient);
        }
    }

    public static void addOrUpdate(Context context, SQLRecipeImageUrlElement recipeImageUrlElement) {
       // Log.v(TAG, "addOrUpdate: "+recipeImageUrlElement.toString());
        if(recipeImageUrlElement.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_URL.updateElement(getWritableDatabase(context), recipeImageUrlElement);
            recipeImageUrlElement.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            recipeImageUrlElement.setID(Tables.TABLE_RECIPE_URL.addElement(getWritableDatabase(context), recipeImageUrlElement));
            recipeImageUrlElement.wasSaved();
        }
        //TO DO: //Buffer.getSingleton(context).addToBuffer(recipeImageUrlElement);
    }

    public static void addOrUpdate(Context context, SQLIngredientImageUrlElement ingredientImageUrlElement) {
       // Log.v(TAG, "addOrUpdate: "+ingredientImageUrlElement);
        if(ingredientImageUrlElement.getID() != -1){
           // Log.v(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_URL.updateElement(getWritableDatabase(context), ingredientImageUrlElement);
            ingredientImageUrlElement.wasSaved();
        }else{
           // Log.v(TAG, "first time saving");
            ingredientImageUrlElement.setID(
                    Tables.TABLE_INGREDIENT_URL.addElement(
                            getWritableDatabase(context),
                            ingredientImageUrlElement));
            ingredientImageUrlElement.wasSaved();
        }
        //TO DO: //Buffer.getSingleton(context).addToBuffer(ingredientImageUrlElement);
    }


}
