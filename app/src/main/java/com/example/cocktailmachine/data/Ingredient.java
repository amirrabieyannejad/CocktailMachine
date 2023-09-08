package com.example.cocktailmachine.data;



import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;


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
    String TAG = "Ingredient";

    //Reminder: Only liquids!!!

    /**
     * Get the Id.
     * @return id
     */
    long getID();

    /**
     * Get the name.
     * @return name
     */
    String getName();


    /**
     * Is alcoholic?
     * @return alcoholic?
     */
    boolean isAlcoholic();

    /**
     * Is available?
     * @return available?
     */
    boolean isAvailable();


    /**
     * Get fluid color.
     * @return Integer representative of color
     */
    @ColorInt
    int getColor();

    //Setter
    void setColor(@ColorInt int color);

    void setAlcoholic(boolean alcoholic);


    void setName(String name);


    //FOTOS

    /**
     * Get the addresses for the images.
     * @return list of image addresses.
     */
    List<String> getImageUrls();

    /**
     * Add to the image address list an address.
     * @param url to be added image address
     */
    void addImageUrl(String url);

    /**
     * removes from image address list
     * @param url to be added image address
     */
    void removeImageUrl(String url);




    //Pump stuff

    /**
     * Get Pump representative class, where the ingredient is within.
     * @return pump
     */
    Pump getPump();

    /**
     * Get Pump representative class, where the ingredient is within.
     * @return pump
     */
    Long getPumpId();


    /**
     * Still available liquid/fluid in milliliter.
     * @return milliliter of ingredient
     */
    int getVolume();


    /**
     * use only for connecting pump and ingredient after loading
     * @param ingredientPump
     */
    void setIngredientPump(SQLIngredientPump ingredientPump);

    void setPump(Long pump, int volume);

    /**
     * emptys pump if exist
     */
    void empty();


    /**
     * Pump m milliliters.
     * @param volume m
     * @throws NewlyEmptyIngredientException ingredient is empty.
     */
    void pump(int volume) throws NewlyEmptyIngredientException, MissingIngredientPumpException;








    //general
    /**
     * Static Access to ingredients.
     * Get all ingredients.
     * @return List of ingredients.
     */
    static List<Ingredient> getAllIngredients() {
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
    static List<Ingredient> getAvailableIngredients() {
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
    static List<Ingredient> getAvailableIngredients(List<Long> ingredientsIds) {
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
    @Nullable
    static Ingredient getIngredient(Long id) {
        try {
            return DatabaseConnection.getDataBase().getIngredient(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "getIngredient failed for "+id);
            return null;
        }
    }

    /**
     * Static Access to ingredients.
     * Get available ingredients with name k.
     * @param name k
     * @return
     */
    static Ingredient getIngredient(String name) {
        Log.i(TAG, "getIngredient "+name);
        try {
            return DatabaseConnection.getDataBase().getIngredientWithExact(name);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG,"getIngredient failed for "+name);
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
    static Ingredient makeNew(String name,
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
    static Ingredient makeNew(String name,
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
    static Ingredient makeNew(String name){
        return new SQLIngredient(name);
    }

    static Ingredient searchOrNew(String name){
        Ingredient ingredient = Ingredient.getIngredient(name);
        if(ingredient == null){
            return Ingredient.makeNew(name);
        }
        return ingredient;
    }


    //TODO: JSON Object
    //TODO: JSON Array
    //TODO: nachschauen ob DB schon mit infos geladen sein kann beim runterladen

}
