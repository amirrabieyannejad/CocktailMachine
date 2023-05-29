package com.example.cocktailmachine.data;


import androidx.annotation.ColorInt;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;


import org.json.JSONObject;

import java.util.ArrayList;
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
public interface Ingredient extends Comparable<Ingredient>, DataBaseElement {

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
    public int getVolume();

    /**
     * Get Pump representative class, where the ingredient is within.
     * @return pump
     */
    public Pump getPump();

    /**
     * Get Pump representative class, where the ingredient is within.
     * @return pump
     */
    public Long getPumpId();

    /**
     * Get fluid color.
     * @return Integer representative of color
     */
    @ColorInt
    public int getColor();

    //Setter
    /**
     * Add to the image address list an address.
     * @param url to be added image address
     */
    public void addImageUrl(String url);

    public void setPump(Long pump, int volume);


    public void setColor(@ColorInt int color);

    public void setAlcoholic(boolean alcoholic);


    void setName(String name);

    //use
    /**
     * Pump m milliliters.
     * @param volume m
     * @throws NewlyEmptyIngredientException ingredient is empty.
     */
    public void pump(int volume) throws NewlyEmptyIngredientException;

    //general
    /**
     * Static Access to ingredients.
     * Get all ingredients.
     * @return List of ingredients.
     */
    public static List<Ingredient> getAllIngredients() {
        try {
            return (List<Ingredient>) DatabaseConnection.getDataBase().getAllIngredients();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * Static Access to ingredients.
     * Get all available ingredients.
     * @return List of ingredients.
     */
    public static List<Ingredient> getIngredientWithIds() {
        try {
            return (List<Ingredient>) DatabaseConnection.getDataBase().getAvailableIngredients();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Static Access to ingredients.
     * Get all (available) ingredients with ids in given list k.
     * @param ingredientsIds k
     * @return List of ingredients.
     */
    public static List<Ingredient> getIngredientWithIds(List<Long> ingredientsIds) {
        try {
            return DatabaseConnection.getDataBase().getIngredients(ingredientsIds);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Static Access to ingredients.
     * Get available ingredients with id k.
     * @param id k
     * @return
     */
    public static Ingredient getIngredient(Long id) {
        try {
            return DatabaseConnection.getDataBase().getIngredient(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Static Access to ingredients.
     * Get available ingredients with name k.
     * @param name k
     * @return
     */
    public static Ingredient getIngredient(String name) {
        try {
            return DatabaseConnection.getDataBase().getIngredientWithExact(name);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Static access to ingredients.
     * Make new instance.
     * Set up with direct link to pump.
     * @param name name
     * @param alcoholic alcoholic?
     * @param available available?
     * @param volume milliliter of ingredient in pump
     * @param pump pump id
     * @param color color in Integer representation
     * @return new Ingredient instance
     */
    public static Ingredient makeNew(String name,
                                     boolean alcoholic,
                                     boolean available,
                                     int volume,
                                     long pump,
                                     int color){
        return new SQLIngredient(name, alcoholic, available, volume, pump, color);
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

    /**
     * Static access to ingredients.
     * Make new instance.
     * Set up without link to a pump.
     * @param name name
     * @return  new Ingredient instance
     */
    public static Ingredient makeNew(String name){
        return new SQLIngredient(name);
    }

    public static Ingredient searchOrNew(String name){
        Ingredient ingredient = Ingredient.getIngredient(name);
        if(ingredient == null){
            return Ingredient.makeNew(name);
        }
        return ingredient;
    }


}
