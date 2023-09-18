package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.AccessDeniedException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.enums.AdminRights;

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

    static Ingredient loadIngredient(Context context, long id) throws AccessDeniedException {
        Log.i(TAG, "loadIngredient");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static Ingredient loadIngredientForPump(Context context,long id) {
        Log.i(TAG, "loadIngredientForPump");
        return Tables.TABLE_INGREDIENT.getElement(getReadableDatabase(context), id);
    }

    static Recipe loadRecipe(Context context,long id) throws AccessDeniedException {
        Log.i(TAG, "loadRecipe");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        Recipe res =  Tables.TABLE_RECIPE.getElement(getReadableDatabase(context), id);
        res.loadAvailable();
        return res;
    }

    static Topic loadTopic(Context context,long id) throws AccessDeniedException {
        Log.i(TAG, "loadTopic");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_TOPIC.getElement(getReadableDatabase(context), id);
    }


    static Pump loadPump(Context context, long id) throws AccessDeniedException {
        Log.i(TAG, "loadTopic");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_PUMP.getElement(getReadableDatabase(context), id);
    }
}
