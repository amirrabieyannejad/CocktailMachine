package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NewDatabaseConnection;
import com.example.cocktailmachine.data.db.NewEmptyIngredientException;


import org.json.JSONObject;

import java.util.List;

/**
 * Ingredient is a class that represents the liquid in a pump.
 * It has a name and ID.
 * It is noted if it is alcoholic and available.
 * For simple users only available (currently in pump and not empty) should be accessible.
 * It is known how much of the ingredient is left.
 * It can be shown with an image.
 * The color is known.
 */
public interface Ingredient {

    //Reminder: Only liquids!!!

    /**
     * Get the Id.
     * @return id
     */
    public long getID();

    /**
     * Get the name.
     * @return name
     */
    public String getName();

    /**
     * Get the addresses for the images.
     * @return list of image addresses.
     */
    public List<String> getImageUrls();

    /**
     * Is alcoholic?
     * @return alcoholic?
     */
    public boolean isAlcoholic();

    /**
     * Is available?
     * @return available?
     */
    public boolean isAvailable();

    /**
     * Still available liquid/fluid in milliliter.
     * @return milliliter of ingredient
     */
    public int getFluidInMilliliter();

    /**
     * Get Pump representative class, where the ingredient is within.
     * @return pump
     */
    public Pump getPump();

    /**
     * Get fluid color.
     * @return Integer representative of color
     */
    public int getColor();



    /**
     * Add to the image address list an address.
     * @param url to be added image address
     */
    public void addImageUrl(String url);


    /**
     * Pump m milliliters.
     * @param millimeters m
     * @throws NewEmptyIngredientException ingredient is empty.
     */
    public void pump(int millimeters) throws NewEmptyIngredientException;

    /**
     * Save this object to database.
     */
    public void save();

    /**
     * Delete this object including in the database.
     */
    public void delete();

    /**
     * Static Access to ingredients.
     * Get all available ingredients.
     * @return List of ingredients.
     */
    public static List<Ingredient> getIngredients(){
        return (List<Ingredient>) NewDatabaseConnection.getDataBase().getAvailableIngredients();
    }
    /**
     * Static Access to ingredients.
     * Get all (available) ingredients with ids in given list k.
     * @param ingredientsIds k
     * @return List of ingredients.
     */
    public static List<Ingredient> getIngredients(List<Long> ingredientsIds){
        return NewDatabaseConnection.getDataBase().getIngredients(ingredientsIds);
    }
    /**
     * Static Access to ingredients.
     * Get available ingredients with id k.
     * @param id k
     * @return
     */
    public static Ingredient getIngredient(Long id){
        return NewDatabaseConnection.getDataBase().getIngredient(id);
    }

    public default JSONObject asMessage(){
        //TODO
        return null;
    }


}
