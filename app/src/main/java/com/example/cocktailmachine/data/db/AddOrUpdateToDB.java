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
        Log.i(TAG, "addIngredientImageUrl");
        // getWritableDatabase(context).
        Tables.TABLE_INGREDIENT_URL.addElement(getWritableDatabase(context),ingredientId, url);
    }

    public static void addOrUpdate(Context context, SQLIngredient ingredient) {
        Log.i(TAG, "addOrUpdate: "+ingredient.toString());
        if(ingredient.isSaved() && ingredient.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT.updateElement(getWritableDatabase(context), ingredient);
            ingredient.wasSaved();
        }else if(ingredient.isSaved() && !ingredient.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredient.setID(Tables.TABLE_INGREDIENT.addElement(getWritableDatabase(context), ingredient));
            //ingredients.add(ingredient);
            Buffer.getSingleton().addToBuffer(ingredient);
            ingredient.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(ingredient);

    }

    public static void addOrUpdate(Context context, SQLRecipe recipe) {
        Log.i(TAG, "addOrUpdate: "+recipe.toString());
        if(recipe.isSaved() && recipe.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE.updateElement(getWritableDatabase(context), recipe);
            recipe.wasSaved();
        }else if(recipe.isSaved() && !recipe.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipe.setID(Tables.TABLE_RECIPE.addElement(getWritableDatabase(context), recipe));
            Buffer.getSingleton().addToBuffer(recipe);
            recipe.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(recipe);
    }

    public static void addOrUpdate(Context context, SQLTopic topic) {
        Log.i(TAG, "addOrUpdate: "+topic.toString());
        if(topic.isSaved() && topic.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_TOPIC.updateElement(getWritableDatabase(context), topic);
            topic.wasSaved();
        }else if(topic.isSaved() && !topic.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            topic.setID(Tables.TABLE_TOPIC.addElement(getWritableDatabase(context), topic));
            Buffer.getSingleton().addToBuffer(topic);
            topic.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(topic);
    }

    public static void addOrUpdate(Context context, SQLPump pump) {
        //TODO: check available
        Log.i(TAG, "addOrUpdate: "+pump.toString());
        if(pump.isSaved() && pump.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_PUMP.updateElement(getWritableDatabase(context), pump);
            pump.wasSaved();
        }else if(pump.isSaved() && !pump.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            pump.setID(Tables.TABLE_PUMP.addElement(getWritableDatabase(context), pump));
            Buffer.getSingleton().addToBuffer(pump);
            pump.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(pump);
    }

    public static void addOrUpdate(Context context, SQLRecipeTopic recipeTopic) {
        Log.i(TAG, "addOrUpdate: "+recipeTopic.toString());
        if(recipeTopic.isSaved() && recipeTopic.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_TOPIC.updateElement(getWritableDatabase(context), recipeTopic);
            recipeTopic.wasSaved();
        }else if(recipeTopic.isSaved() && !recipeTopic.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeTopic.setID(Tables.TABLE_RECIPE_TOPIC.addElement(getWritableDatabase(context), recipeTopic));
            recipeTopic.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(recipeTopic);
    }

    public static void addOrUpdate(Context context, SQLIngredientPump ingredientPump) {
        Log.i(TAG, "addOrUpdate: "+ingredientPump);
        if(ingredientPump.isSaved() && ingredientPump.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_PUMP.updateElement(getWritableDatabase(context), ingredientPump);
            ingredientPump.wasSaved();
            //ingredientPumps.remove(ingredientPump);
        }else if(ingredientPump.isSaved() && !ingredientPump.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredientPump.setID(Tables.TABLE_INGREDIENT_PUMP.addElement(getWritableDatabase(context), ingredientPump));
            Buffer.getSingleton().addToBuffer(ingredientPump);
            ingredientPump.wasSaved();
        }
        Buffer.getSingleton(context).addToBuffer(ingredientPump);
    }

    public static void addOrUpdate(Context context, SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "addOrUpdate: "+recipeIngredient.toString());
        if(recipeIngredient.isSaved() && recipeIngredient.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(getWritableDatabase(context), recipeIngredient);
            recipeIngredient.wasSaved();;
            //recipeIngredients.remove(recipeIngredient);
        }else if(recipeIngredient.isSaved() && !recipeIngredient.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeIngredient.setID(Tables.TABLE_RECIPE_INGREDIENT.addElement(getWritableDatabase(context), recipeIngredient));
            Buffer.getSingleton().addToBuffer(recipeIngredient);
            recipeIngredient.wasSaved();;
        }
        Buffer.getSingleton(context).addToBuffer(recipeIngredient);
    }

    public static void addOrUpdate(Context context, SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "addOrUpdate: "+recipeImageUrlElement.toString());
        if(recipeImageUrlElement.isSaved() && recipeImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_URL.updateElement(getWritableDatabase(context), recipeImageUrlElement);
            recipeImageUrlElement.wasSaved();
        }else if(recipeImageUrlElement.isSaved() && !recipeImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeImageUrlElement.setID(Tables.TABLE_RECIPE_URL.addElement(getWritableDatabase(context), recipeImageUrlElement));
            recipeImageUrlElement.wasSaved();
        }
        //TODO: Buffer.getSingleton(context).addToBuffer(recipeImageUrlElement);
    }

    public static void addOrUpdate(Context context, SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "addOrUpdate: "+ingredientImageUrlElement);
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_URL.updateElement(getWritableDatabase(context), ingredientImageUrlElement);
            ingredientImageUrlElement.wasSaved();
        }else if(ingredientImageUrlElement.isSaved() && !ingredientImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredientImageUrlElement.setID(
                    Tables.TABLE_INGREDIENT_URL.addElement(
                            getWritableDatabase(context),
                            ingredientImageUrlElement));
            ingredientImageUrlElement.wasSaved();
        }
        //TODO: Buffer.getSingleton(context).addToBuffer(ingredientImageUrlElement);
    }


}
