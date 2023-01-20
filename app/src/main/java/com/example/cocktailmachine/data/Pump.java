package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;

import org.json.JSONObject;


import java.util.List;

public interface Pump extends Comparable<Pump>, DataBaseElement {
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
     * Update ingredient in pump.
     * @param ingredient next ingredient.
     */
    public void setCurrentIngredient(Ingredient ingredient);

    /**
     * Update ingredient in pump.
     * @param id id of next ingredient
     */
    public default void setCurrentIngredient(long id) throws NotInitializedDBException {
        setCurrentIngredient(Ingredient.getIngredient(id));
    }

    public void empty();

    public void fill(int milliliters);




    //general

    /**
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    public void setPumps() throws NotInitializedDBException;

    /**
     * Set up k empty pumps.
     * @param numberOfPumps k
     */
    public void setOverrideEmptyPumps(int numberOfPumps) throws NotInitializedDBException;

    /**
     * Static access to pumps.
     * Get available pumps.
     * @return pumps
     */
    public static List<Pump> getPumps() throws NotInitializedDBException {
          return (List<Pump>) DatabaseConnection.getDataBase().getPumps();
    }

    /**
     * Static access to pumps.
     * Get available pump with id k
     * @param id pump id k
     * @return
     */
    public static Pump getPump(long id) {
        try {
            return DatabaseConnection.getDataBase().getPump(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public default JSONObject asMesssage(){
        //TODO: asMessage
        return new JSONObject();
    }


}
