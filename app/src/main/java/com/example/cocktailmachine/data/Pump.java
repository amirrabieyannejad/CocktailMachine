package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NewDatabaseConnection;

import org.json.JSONObject;


import java.util.List;

public interface Pump {
    /**
     * Get Id.
     * @return
     */
    public long getID();

    /**
     * Returns milliliters pumped in milliseconds.
     * aka minimum available pump size
     * @return minimum milliliters to be pumped
     */
    public int getMillilitersPumpedInMilliseconds();

    /**
     * Return current ingredient set in pump.
     * @return current ingredient
     */
    public Ingredient getCurrentIngredient();

    /**
     * Set up pumps with ingredients.
     */
    public void setPumps();

    /**
     * Update ingredient in pump.
     * @param ingredient next ingredient.
     */
    public void setCurrentIngredient(Ingredient ingredient);

    /**
     * Update ingredient in pump.
     * @param id id of next ingredient
     */
    public default void setCurrentIngredient(long id){
        setCurrentIngredient(Ingredient.getIngredient(id));
    }

    /**
     * Deletes this instance in db and in buffer.
     */
    public void delete();

    /**
     * Saves this object to db.
     */
    public void save();


    //general

    /**
     * Static access to pumps.
     * Get available pumps.
     * @return pumps
     */
    public static List<Pump> getPumps(){
          return NewDatabaseConnection.getDataBase().getPumps();
    }

    /**
     * Static access to pumps.
     * Get available pump with id k
     * @param id pump id k
     * @return
     */
    public static Pump getPump(long id){
        return NewDatabaseConnection.getDataBase().getPump(id);
    }

    public default JSONObject asMesssage(){
        //TODO: asMessage
        return new JSONObject();
    }


}
