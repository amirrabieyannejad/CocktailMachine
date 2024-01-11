package com.example.cocktailmachine.data;



import android.content.Context;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.tables.BasicColumn;


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
    Pump getPump(Context context);

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

    void setPump(Context context,Long pump, int volume);

    /**
     * emptys pump if exist
     */
    void empty(Context context);


    /**
     * Pump m milliliters.
     * @param volume m
     * @throws NewlyEmptyIngredientException ingredient is empty.
     */
    void pump(int volume) throws NewlyEmptyIngredientException, MissingIngredientPumpException;








    //general


    static  BasicColumn<SQLIngredient>.DatabaseIterator getChunkIterator(Context context, int n) {
        return GetFromDB.getIngredientChunkIterator(context, n);
    }
    static  BasicColumn<SQLIngredient>.DatabaseIterator getChunkAvIterator(Context context, int n) {
        return GetFromDB.loadIngredientChunkAvIterator(context, n);
    }

    /**
     * Static Access to ingredients if necessary from db
     * Get all ingredients.
     * @return List of ingredients.
     */
    static List<Ingredient> getAllIngredients(Context context) {
        return (List<Ingredient>) GetFromDB.loadIngredients(context);
    }

    /**
     * Static Access to ingredients.
     * Get all ingredient names
     * @return List of ingredients.
     */
    static List<String> getAllIngredientNames(Context context) {
        return GetFromDB.loadIngredientNames(context);
    }


    /**
     * Static Access to ingredients.
     * Get all available ingredients.
     * @return List of ingredients.
     */
    static List<Ingredient> getAvailableIngredients(Context context) {
        return (List<Ingredient>) GetFromDB.loadAvailableIngredients(context);
    }

    /**
     * Static Access to ingredients.
     * Get all available ingredient names.
     * @return List of ingredients.
     */
    static List<String> getAvailableIngredientNames(Context context) {
        List<Ingredient> res = getAvailableIngredients(context);
        List<String> name = new ArrayList<>();
        for(Ingredient i: res){
            name.add(i.getName());
        }
        return name;
        //return GetFromDB.getAvailableIngredientNames(context);
    }

    /**
     * Static Access to ingredients.
     * Get all (available) ingredients with ids in given list k.
     * @param ingredientsIds k
     * @return List of ingredients.
     */
    static List<Ingredient> getAvailableIngredients(Context context,List<Long> ingredientsIds) {
        return (List<Ingredient>) GetFromDB.loadAvailableIngredients(context,ingredientsIds);
    }



    static Ingredient getIngredient(Context context, long id) {
        return GetFromDB.loadIngredient(context, id);
    }

    /**
     * Static Access to ingredients.
     * Get available ingredients with name k.
     * @param name k
     * @return
     */
    static Ingredient getIngredient(Context context, String name) {
        Log.i(TAG, "getIngredient "+name);
        List<Ingredient> g = (List<Ingredient>) GetFromDB.loadIngredients(context, name);
        if(g.isEmpty()){
            return null;
        }
        return g.get(0);
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

    /**
     * Static access to ingredients.
     * Make new instance.
     * Set up without link to a pump.
     * @param name name
     * @return  new Ingredient instance
     */
    static Ingredient makeNew(String name,
                              boolean alcoholic){
        return new SQLIngredient(name, alcoholic);
    }


    static Ingredient searchOrNew(Context context, String name){
        Ingredient ingredient = Ingredient.getIngredient(context, name);
        if(ingredient == null){
            ingredient =  Ingredient.makeNew(name);
            ingredient.save(context);
        }
        return ingredient;
    }

    static Ingredient searchOrNew(Context context, String name, boolean alcoholic, int color) {
        Ingredient i = Ingredient.searchOrNew(context, name);
        i.setAlcoholic(alcoholic);
        i.setColor(color);
        i.save(context);
        return i;
    }




    //TO DO: JSON Object
    //TO  DO: JSON Array
    //TO DO: nachschauen ob DB schon mit infos geladen sein kann beim runterladen

}
