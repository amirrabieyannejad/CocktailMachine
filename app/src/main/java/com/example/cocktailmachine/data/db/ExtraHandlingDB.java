package com.example.cocktailmachine.data.db;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtraHandlingDB {
    private static final String TAG = "ExtraHandlingDB";

    public static void localRefresh(Context context) {
        DatabaseConnection.init(context).refreshAvailable(context);
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
    }


    public static void loadAvailabilityForAll(Context context){
        //TODO:
    }

    public static boolean loadAvailability(Context context, SQLRecipeTopic recipeTopic){
        return false; //TODO
    }

    public static boolean loadAvailability(Context context, SQLRecipeIngredient recipeIngredient) {
        return false; //TODO
    }

    public static boolean loadAvailability(Context context, SQLIngredientPump sqlIngredientPump) {
        return false; //TODO
    }
}