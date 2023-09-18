package com.example.cocktailmachine.data.db;

import android.content.Context;

import com.example.cocktailmachine.data.Pump;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 16:43
 * @project CocktailMachine
 */
public class Availability {
    /*
private void checkAllAvailability() throws NotInitializedDBException {
    //TO DO: checkAllAvailability
    Log.i(TAG, "checkAllAvailability");
    Log.i(TAG, "checkAllAvailability: pumps");
    for(Pump p: this.pumps){
        p.loadAvailable();//loads ingredient pump connection if exists
        p.save();
    }
    Log.i(TAG, "checkAllAvailability: pumps: "+this.pumps);
    Log.i(TAG, "checkAllAvailability: ingredients");
    for(Ingredient i: this.ingredients){
        i.loadAvailable();//loads ingredient pump connection if exists
        i.save();
    }
    Log.i(TAG, "checkAllAvailability: ingredients: "+this.ingredients);
    Log.i(TAG, "checkAllAvailability: recipes");
    for(Recipe r: this.recipes){
        r.loadAvailable();
        r.save();
    }
    Log.i(TAG, "checkAllAvailability: recipes: "+this.recipes);
}

 */
    public static void check(){
        for(Pump p:Pump.getPumps()){
            p.loadAvailable();
        }

    }

    public static void check(Context context){
        for(Pump p:Pump.getPumps()){
            p.loadAvailable(context);
        }

    }


}
