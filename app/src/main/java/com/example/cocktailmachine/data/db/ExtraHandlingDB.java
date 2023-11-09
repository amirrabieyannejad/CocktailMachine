package com.example.cocktailmachine.data.db;

import static com.example.cocktailmachine.data.db.AddOrUpdateToDB.getWritableDatabase;
import static com.example.cocktailmachine.data.db.GetFromDB.getReadableDatabase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.ui.Menue;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExtraHandlingDB {
    private static final String TAG = "ExtraHandlingDB";

    public static void localRefresh(Context context) {
        //TODO: DatabaseConnection.init(context).refreshAvailable(context);
    }


    /* *
     * load csv and json files prepared by phillip
     * @author Johanna Reidt
     * @param context
     */
    public static void loadPreped(Context context) {
       // Log.v(TAG, "loadPreped" );
        DatabaseConnection.loadLiquid(context);
        DatabaseConnection.loadPrepedRecipes(context);
    }

    public static void loadForSetUp(Activity context) {

        DatabaseConnection.init(context).setUpEmptyPumps(); //delete all pump Tables to be sure
    }

    public static void loadDummy(Context context) {
        // Log.v(TAG, "loadDummy");
        try {
            DatabaseConnection.init(context).loadDummy(context);
        } catch (NotInitializedDBException | MissingIngredientPumpException e) {
            // Log.v(TAG, "loadDummy");
            Log.e(TAG, "error", e);
            // Log.getStackTraceString(e);
        }
    }

    public static void deleteDoublePumpSettingsAndNulls(Context context) {
        List<Long> pump_ids = new ArrayList<Long>();
        //List<Long> toDelete = new ArrayList<>();
        Iterator<SQLIngredientPump> it = GetFromDB.getIngredientPumps(context).iterator();
        List<Long> toDelete = GetFromDB.getIngredientPumpsIDs(context);
        while (it.hasNext()) {
            SQLIngredientPump ip = it.next();
            if (ip == null) {
                it.remove();
            } else if (!pump_ids.contains(ip.getPumpID())) {
                pump_ids.add(ip.getPumpID());
                toDelete.remove(ip.getID());
            }

        }
        for (Long id : toDelete) {
            DeleteFromDB.removeIngredientPump(context, id);
        }
    }

    public static void deleteDoubleRecipeIngredientSettingsAndNulls(Context context) {
        //TODO♦
        HashMap<Long, List<Long>> set = new HashMap<>();
        //List<Long> toDelete = new ArrayList<>();
        Iterator<SQLRecipeIngredient> it = GetFromDB.getRecipeIngredientIterator(context);
        List<Long> toDelete = GetFromDB.getRecipeIngredientIDs(context);
        while (it.hasNext()) {
            SQLRecipeIngredient ip = it.next();
            if (ip == null) {
                it.remove();
            }else if(!set.containsKey(ip.getRecipeID())){
                List<Long> n = new ArrayList<>();
                n.add(ip.getIngredientID());
                set.put(ip.getRecipeID(), n);
                toDelete.remove(ip.getID());
            } else if (!Objects.requireNonNull(set.get(ip.getRecipeID())).contains(ip.getIngredientID())) {
                Objects.requireNonNull(set.get(ip.getRecipeID())).add(ip.getIngredientID());
                toDelete.remove(ip.getID());
            }

        }
        for (Long id : toDelete) {
            DeleteFromDB.removeIngredientPump(context, id);
        }
    }


    public static void loadAvailabilityForAll(Context context){
        //TODO:set available in all recipes
        List<Long> availableIngredients = Tables.TABLE_INGREDIENT_PUMP.getIngredientIDs(getReadableDatabase(context));
        List<Long> maybeRecipes = Tables.TABLE_RECIPE_INGREDIENT.getRecipeIDsWithIngs(getReadableDatabase(context), availableIngredients);
        List<Long> notRecipes = Tables.TABLE_RECIPE_INGREDIENT.getRecipeIDsWithoutIngs(getReadableDatabase(context), availableIngredients);

        Tables.TABLE_RECIPE.setEmptyPumps(getWritableDatabase(context));
        Tables.TABLE_RECIPE.setAvailable(getWritableDatabase(context), maybeRecipes);
        Tables.TABLE_RECIPE.setNotAvailable(getWritableDatabase(context), notRecipes);
    }

    public static boolean loadAvailability(Context context, SQLRecipeTopic recipeTopic) {
        return true; //TO DO
    }

    public static boolean loadAvailability(Context context, SQLRecipeIngredient recipeIngredient) {
        return Tables.TABLE_INGREDIENT_PUMP.hasIngredient(getReadableDatabase(context), recipeIngredient.getIngredientID()); //TODO
    }

    public static boolean loadAvailability(Context context, SQLIngredientPump sqlIngredientPump) {
        return Tables.TABLE_INGREDIENT_PUMP.hasIngredient(getReadableDatabase(context), sqlIngredientPump.getIngredientID());
        //return true; //TO DO
    }

    public static void loadPrepedDB(Context context) {
        DatabaseConnection.loadIfNotDoneDBFromAssets(context);
    }
}