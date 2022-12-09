package com.example.cocktailmachine.data;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface Recipe {
    //Getter
    public long getId();
    public List<Long> getIngredientIds();
    public List<Ingredient> getIngredients();
    public HashMap<Long, Integer> getIngredientPumpTime();
    public int getSpecificIngredientPumpTime(long ingredientId);
    public int getSpecificIngredientPumpTime(Ingredient ingredient);
    public boolean isAlcoholic();
    public boolean isAvailable();
    public List<String> getImageUrls();

    //Ingredient Changer

    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredient
     * @param timeInMilliseconds
     */
    public void add(Ingredient ingredient, int timeInMilliseconds);
    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredientId
     * @param timeInMilliseconds
     */
    public void add(long ingredientId, int timeInMilliseconds);

    /**
     * Remove Ingredient from Recipe.
     * @param ingredient
     */
    public void remove(Ingredient ingredient);
    /**
     * Remove Ingredient from Recipe.
     * @param ingredientId
     */
    public void remove(long ingredientId);


    //this Instance

    public default JSONObject asMessage(){
        //TODO
        return new JSONObject();
    }

    /**
     * Deletes this instance in db and in buffer.
     */
    public void delete();

    /**
     * Saves to db.
     */
    public void save();

    /**
     * Sends to CocktailMachine to get mixed.
     */
    public default void send(){
        //TODO:
        return;
    }

    //general
    public static Ingredient load(long id){
        return getIngredient(id);
    }
    public static Ingredient getIngredient(long id){
        //TODO
        return null;
    }
    public static List<Ingredient> getIngredients(List<Long> ids){
        //TODO
        return null;
    }


}
