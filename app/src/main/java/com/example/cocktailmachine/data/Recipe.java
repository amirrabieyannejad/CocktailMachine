package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NewDatabaseConnection;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface Recipe {
    //Getter
    public long getID();
    public String getName();
    public List<Long> getIngredientIds();
    public List<Ingredient> getIngredients();
    public HashMap<Long, Integer> getIngredientPumpTime();
    public int getSpecificIngredientPumpTime(long ingredientId) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;
    public int getSpecificIngredientPumpTime(Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;
    public boolean isAlcoholic();
    public boolean isAvailable();
    public List<String> getImageUrls();
    public List<Long> getTopics();


    //Ingredient Changer
    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredient
     * @param timeInMilliseconds
     */
    public void addOrUpdate(Ingredient ingredient, int timeInMilliseconds);
    /**
     * Adds ingredient with quantity measured in needed pump time.
     * @param ingredientId
     * @param timeInMilliseconds
     */
    public void addOrUpdate(long ingredientId, int timeInMilliseconds);

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

     * Reminder:
     * send topics to user!
     */
    public default void send(){
        //TODO:
        return;
    }



    //general
    public static Ingredient getIngredient(long id){
        return NewDatabaseConnection.getDataBase().getIngredient(id);
    }
    public static List<Ingredient> getIngredients(List<Long> ids){
        return NewDatabaseConnection.getDataBase().getIngredients(ids);
    }

    public static Recipe makeNew(String name){
        return new SQLRecipe(name);
    }


}
