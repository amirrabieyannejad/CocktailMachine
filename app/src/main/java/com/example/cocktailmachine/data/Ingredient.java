package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NeedsMoreIngredientException;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;
import com.example.cocktailmachine.data.db.NewEmptyIngredientException;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;


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

    //Setter
    /**
     * Add to the image address list an address.
     * @param url to be added image address
     */
    public void addImageUrl(String url);

    public void setPump(Long pump, int fluidInMillimeters);

    //use
    /**
     * Pump m milliliters.
     * @param millimeters m
     * @throws NewEmptyIngredientException ingredient is empty.
     */
    public void pump(int millimeters) throws NewEmptyIngredientException, NeedsMoreIngredientException;

    //db
    /**
     * Save this object to database.
     */
    public void save();

    /**
     * Delete this object including in the database.
     */
    public void delete();

    //general
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

    /**
     * Static access to ingredients.
     * Make new instance.
     * Set up with direct link to pump.
     * @param name name
     * @param alcoholic alcoholic?
     * @param available available?
     * @param fluidInMillimeters milliliter of ingredient in pump
     * @param pump pump id
     * @param color color in Integer representation
     * @return new Ingredient instance
     */
    public static Ingredient makeNew(String name,
                                     boolean alcoholic,
                                     boolean available,
                                     int fluidInMillimeters,
                                     long pump,
                                     int color){
        return new SQLIngredient(name, alcoholic, available, fluidInMillimeters, pump, color);
    }

    /**
     * Static access to ingredients.
     * Make new instance.
     * Set up without link to a pump.
     * @param name name
     * @param alcoholic alcoholic
     * @param color color in Integer representation
     * @return  new Ingredient instance
     */
    public static Ingredient makeNew(String name,
                                     boolean alcoholic,
                                     int color){
        return new SQLIngredient(name, alcoholic, color);
    }

    public default JSONObject asMessage(){
        //TODO
        return null;
    }


}
