package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.Objects;

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

    //REMOVE in DB and //buffer
    public static void remove(Context context, Ingredient ingredient) {
        //TO DO: delete from recipes
       // Log.v(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_INGREDIENT.deleteElement(db, ingredient.getID());
        Tables.TABLE_INGREDIENT_URL.deleteWithOwnerId(db, ingredient.getID());
        //Buffer.getSingleton(context).removeFrom//Buffer(ingredient);
    }

    public static void remove(Context context, Recipe recipe) {
       // Log.v(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_RECIPE.deleteElement(db, recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(db, recipe.getID());
        //Buffer.getSingleton(context).removeFrom//Buffer(recipe);
    }

    public static void remove(Context context, Pump pump) {
        //TO DO: check available
       // Log.v(TAG, "remove");
        SQLiteDatabase db = getWritableDatabase(context);
        Tables.TABLE_PUMP.deleteElement(db, pump.getID());
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(db, GetFromDB.getIngredientPump(context, pump)); //Buffer.getSingleton().getIngredientPump(pump));
        //Buffer.getSingleton(context).removeFrom//Buffer(pump);
    }


    public static void remove(Context context, SQLRecipeTopic sQLRecipeTopic) {
       // Log.v(TAG, "remove");
        Tables.TABLE_RECIPE_TOPIC.deleteElement(getWritableDatabase(context), sQLRecipeTopic);
        //Buffer.getSingleton(context).removeFrom//Buffer(sQLRecipeTopic);
    }

    public static void remove(Context context, Recipe recipe, Topic topic) {
       // Log.v(TAG, "remove");
        SQLRecipeTopic rt = GetFromDB.getRecipeTopic(context, recipe, topic);//Buffer.getSingleton(context).get(recipe, topic);
        remove(context, rt);
        ////Buffer.getSingleton(context).removeFrom//Buffer(topic);
    }

    public static void remove(Context context, SQLTopic topic) {
        //TODO: delete from recipes
       // Log.v(TAG, "remove");
        Tables.TABLE_TOPIC.deleteElement(getWritableDatabase(context), topic);
        //Buffer.getSingleton(context).removeFrom//Buffer(topic);
    }

    public static void remove(Context context, SQLRecipeIngredient recipeIngredient) {
       // Log.v(TAG, "remove");
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(getWritableDatabase(context),recipeIngredient);
        //Buffer.getSingleton(context).removeFrom//Buffer(recipeIngredient);
    }
    public static void remove(Context context, Recipe recipe, Ingredient ingredient) {
       // Log.v(TAG, "remove");
        remove(context, GetFromDB.getRecipeIngredient(context, recipe, ingredient)); //Buffer.getSingleton(context).get(recipe, ingredient));
        ////Buffer.getSingleton(context).removeFrom//Buffer(recipe, ingredient);
    }

    public static void remove(Context context, SQLIngredientPump ingredientPump) {
       // Log.v(TAG, "remove");
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(getWritableDatabase(context), ingredientPump);
        //Buffer.getSingleton(context).removeFrom//Buffer(ingredientPump);
    }

    public static void remove(Context context, SQLRecipeImageUrlElement recipeImageUrlElement) {
       // Log.v(TAG, "remove");
        Tables.TABLE_RECIPE_URL.deleteElement(getWritableDatabase(context),recipeImageUrlElement);
    }

    public static void remove(Context context, SQLIngredientImageUrlElement ingredientImageUrlElement) {
       // Log.v(TAG, "remove");
        Tables.TABLE_INGREDIENT_URL.deleteElement(getWritableDatabase(context),ingredientImageUrlElement);
    }


    public static void removeAll(Context context) {
        DatabaseConnection.init(context).emptyAll();
        //Buffer.getSingleton(context).noMemory();
    }

    public static void removeIngredientPump(Context context, long id) {
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(getWritableDatabase(context), id);
    }

    public static void delete(Context context, ModelType modelType, Long id) {
        switch (modelType){
            case TOPIC: Topic.getTopic(context,id).delete(context);return;
            case RECIPE:Recipe.getRecipe(context,id).delete(context);return;
            case INGREDIENT: Ingredient.getIngredient(context, id).delete(context);return;
            case PUMP: Pump.getPump(context, id).delete(context);return;
        }
        return;
    }

    public static void deleteElementFromRecipe(Context context, Recipe recipe, ModelType modelType, Long id) {
        if(modelType == ModelType.INGREDIENT){
            Objects.requireNonNull(GetFromDB.getRecipeIngredient(context, recipe, Ingredient.getIngredient(context, id))).delete(context);
            return;
        }
        if(modelType == ModelType.TOPIC){
            Objects.requireNonNull(GetFromDB.getRecipeTopic(context, recipe, Topic.getTopic(context, id))).delete(context);
        }
    }
}
