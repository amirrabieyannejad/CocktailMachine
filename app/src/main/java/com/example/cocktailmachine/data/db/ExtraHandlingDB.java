package com.example.cocktailmachine.data.db;

import static com.example.cocktailmachine.data.db.AddOrUpdateToDB.getWritableDatabase;
import static com.example.cocktailmachine.data.db.GetFromDB.getReadableDatabase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
//import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtraHandlingDB {
    private static final String TAG = "ExtraHandlingDB";


    public static void doOnDB(Context context, List<Runnable>  runnables){
        try{
            DatabaseConnection.getSingleton().doOnDB(context, runnables);
        } catch (NotInitializedDBException e) {
            Log.e(TAG, "doOnDB", e);
        }
    }
    public static void doOnDB(Context context, Runnable r){
        try {
            DatabaseConnection.getSingleton().doOnDB(context, r);
        } catch (NotInitializedDBException e) {
            Log.e(TAG, "doOnDB", e);
        }
    }


    /**
     * reload availability of recipes
     * @author Johanna Reidt
     * @param context
     */
    public static void localRefresh(Context context) {
        //TO DO: DatabaseConnection.init(context).refreshAvailable(context);
        loadAvailabilityForAll(context);
    }

    /**
     * reload availability of recipes
     * @author Johanna Reidt
     * @param context
     * @param asBackground
     *
     */
    public static void localRefresh(Context context, boolean asBackground) {
        //TO DO: DatabaseConnection.init(context).refreshAvailable(context);
        if(!asBackground) {
            Log.e(TAG, "localRefresh: NOT asBackground ");
            loadAvailabilityForAll(context);
            return;
        }else {
            Log.e(TAG, "localRefresh: asBackground ");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    loadAvailabilityForAll(context);
                    Log.e(TAG, "localRefresh: asBackground: done ");
                }
            };
            try {
                DatabaseConnection.getSingleton().doOnDB(context, r);
            } catch (NotInitializedDBException e) {
                Log.e(TAG, "localRefresh: asBackground ");
            }
        }
    }


    public static void localRefreshForPump(Context context, Pump pump, Ingredient previousIngr) {
        //TOD O:
        Ingredient newIngr = null;
        if(pump != null){
            newIngr = pump.getCurrentIngredient(context);
            if(newIngr != null){
                if(newIngr.equals(previousIngr)){
                    return; // no change -> do nothing
                }
            }
        }
        if(pump  != null) {
            if(newIngr != null){
                ExtraHandlingDB.localRefresh(context); // real change
                return;
            }
        }
        if(previousIngr != null){ // no new Ingredient -> make unavailable
            List<Long> ingrs = new LinkedList<>();
            ingrs.add(previousIngr.getID());
            List<Long> toUnavailable =Tables.TABLE_RECIPE_INGREDIENT.getRecipeIDsWithIngs(getReadableDatabase(context), ingrs);
            Tables.TABLE_RECIPE.setNotAvailable(getWritableDatabase(context), toUnavailable);
        }


    }


    /* *
     * load csv and json files prepared by phillip
     * @author Johanna Reidt
     * @param context
     */
    public static void loadPreped(Context context) {
       // Log.v(TAG, "loadPreped" );

        Log.i(TAG,"loadPreped" );
        DatabaseConnection.loadFromCSVFiles(context);
    }

    /**
     * delete pump table
     * create new pump table
     * set all recipes as unavailable
     * @author Johanna Reidt
     * @param context
     */
    public static void loadForSetUp(Context context) {

        DatabaseConnection.init(context).setUpEmptyPumps(); //delete all pump Tables to be sure
        Tables.TABLE_RECIPE.setAsAllNotAvailable(getWritableDatabase(context));
    }

    /**
     * load dummy recipes
     * load preset dummy pumps for testing without automatic calibration
     * @author Johanna Reidt
     * @param context
     */
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

    /*
    public static void deleteDoublePumpSettingsAndNulls(Context context) {
        List<Long> pump_ids = new LinkedList<Long>();
        //List<Long> toDelete = new LinkedList<>();
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

     */

    /**
     * delete all double setted ingredients in recipes
     * @author Johanna Reidt
     * @param context
     */
    public static void deleteDoubleRecipeIngredientSettingsAndNulls(Context context) {
        //TODO: ????
        HashMap<Long, List<Long>> set = new HashMap<>();
        //List<Long> toDelete = new LinkedList<>();
        Iterator<SQLRecipeIngredient> it = GetFromDB.getRecipeIngredientIterator(context);
        List<Long> toDelete = GetFromDB.getRecipeIngredientIDs(context);
        while (it.hasNext()) {
            SQLRecipeIngredient ip = it.next();
            if (ip == null) {
                it.remove();
            }else if(!set.containsKey(ip.getRecipeID())){
                List<Long> n = new LinkedList<>();
                n.add(ip.getIngredientID());
                set.put(ip.getRecipeID(), n);
                toDelete.remove(ip.getID());
            } else if (!Objects.requireNonNull(set.get(ip.getRecipeID())).contains(ip.getIngredientID())) {
                Objects.requireNonNull(set.get(ip.getRecipeID())).add(ip.getIngredientID());
                toDelete.remove(ip.getID());
            }

        }
        for (Long id : toDelete) {
            DeleteFromDB.removeRecipeIngredient(context, id);
        }
    }


    /**
     * load all availabilities from pumps to recipes
     * @author Johanna Reidt
     * @param context
     */
    public static void loadAvailabilityForAll(Context context){

        Log.i(TAG, "loadAvailabilityForAll: start");
        //TO DO:set available in all recipes
        List<Long> availableIngredients = Tables.TABLE_NEW_PUMP.getIngredientIDs(getReadableDatabase(context));
        List<Long> maybeRecipes = Tables.TABLE_RECIPE_INGREDIENT.getRecipeIDsWithIngs(getReadableDatabase(context), availableIngredients);
        List<Long> notRecipes = Tables.TABLE_RECIPE_INGREDIENT.getRecipeIDsWithoutIngs(getReadableDatabase(context), availableIngredients);


        Log.i(TAG, "loadAvailabilityForAll: available Ingr: "+availableIngredients.size());
        Log.i(TAG, "loadAvailabilityForAll: maybeRecipes: "+maybeRecipes.size());
        Log.i(TAG, "loadAvailabilityForAll: notRecipes: "+notRecipes.size());


        Tables.TABLE_RECIPE.setAsAllNotAvailable(getWritableDatabase(context));
        Tables.TABLE_RECIPE.setAvailable(getWritableDatabase(context), maybeRecipes);
        Tables.TABLE_RECIPE.setNotAvailable(getWritableDatabase(context), notRecipes);

        Log.i(TAG, "loadAvailabilityForAll: done");
        Log.i(TAG, "loadAvailabilityForAll: All Recipe"+ Recipe.getAllRecipes(context).size());
        Log.i(TAG, "loadAvailabilityForAll: Available Recipe"+ Recipe.getAvailableRecipes(context).size());

    }

    /**
     * load if recipe topic is available (always true)
     * @author Johanna Reidt
     * @param context
     * @param recipeTopic
     * @return
     */
    public static boolean loadAvailability(Context context, SQLRecipeTopic recipeTopic) {
        return true; //TO DO
    }

    /**
     * load if ingredient in recipe is available
     * @author Johanna Reidt
     * @param context
     * @param recipeIngredient
     * @return
     */
    public static boolean loadAvailability(Context context, SQLRecipeIngredient recipeIngredient) {
        return Tables.TABLE_NEW_PUMP.hasIngredient(getReadableDatabase(context), recipeIngredient.getIngredientID()); //TO DO
    }

    /*
    public static boolean loadAvailability(Context context, SQLIngredientPump sqlIngredientPump) {
        return Tables.TABLE_ INGREDIENT_PUMP.hasIngredient(getReadableDatabase(context), sqlIngredientPump.getIngredientID());
        //return true; //TO DO
    }

     */

    /**
     * copies preped db if no db exists
     * @author Johanna Reidt
     * @param context
     */
    public static void loadPrepedDB(Context context) {
        if(!DatabaseConnection.isInitialized()) {
            DatabaseConnection.init(context);
        }
        DatabaseConnection.loadIfNotDoneDBFromAssets(context);
    }
    /**
     * copies preped db if no db exists
     * @author Johanna Reidt
     * @param context
     */
    public static Runnable toLoadPrepedDB(Context context) {
        return DatabaseConnection.toLoadIfNotDoneDBFromAssets(context);
    }

    /**
     * checks if db files exists
     * @author Johanna Reidt
     * @param context
     * @return
     */
    public static boolean hasLoadedDB(Context context){
        if(!DatabaseConnection.isInitialized()) {
            DatabaseConnection.init(context);
        }
        return DatabaseConnection.isDBFileExistentAndNotLoading(context);
    }

}