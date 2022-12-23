package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NewDatabaseConnection;

import org.json.JSONObject;


import java.util.List;

public interface Pump {
    public long getID();

    /**
     * Returns Milliliters pumped in milliseconds.
     * @return
     */

    public int getMillilitersPumpedInMilliseconds();

    public Ingredient getCurrentIngredient();

    public void setPumps();

    public void setCurrentIngredient(Ingredient ingredient);

    public default void setCurrentIngredient(long id){
        setCurrentIngredient(Ingredient.getIngredient(id));
    }

    /**
     * Deletes this instance in db and in buffer.
     */
    public void delete();

    /**
     * Saves to db.
     */
    public void save();


    //general

    public static List<Pump> getPumps(){
          return NewDatabaseConnection.getDataBase().getPumps();
    }

    public static Pump getPump(long id){
        return NewDatabaseConnection.getDataBase().getPump(id);
    }

    public default JSONObject asMesssage(){
        //TODO: asMessage
        return new JSONObject();
    }


}
