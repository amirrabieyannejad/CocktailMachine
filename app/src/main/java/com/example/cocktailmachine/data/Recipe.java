package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Recipe extends Comparable<Recipe>, DataBaseElement {
    //Getter

    /**
     * Get id.
     * @return id
     */
    public long getID();

    /**
     * Get name.
     * @return name
     */
    public String getName();

    /**
     * Get ingredient ids used in this recipe.
     * @return list of ingredient ids.
     */
    public List<Long> getIngredientIds();

    /**
     * Get ingredients used in this recipe.
     * @return list of ingredient.
     */
    public List<Ingredient> getIngredients();

    /**
     * Get ingredients ids and their associated pumptimes in milliseconds
     * @return hashmap ids, pump time
     */
    public HashMap<Long, Integer> getIngredientPumpTime();

    /**
     * Get specific pump time for ingredient with id k
     * @param ingredientId ingredient id k
     * @return pump time in milliseconds
     * @throws TooManyTimesSettedIngredientEcxception There are multiple times setted. only one time is allowed.
     * @throws NoSuchIngredientSettedException There is no such ingredient. The id is not known.
     */
    public int getSpecificIngredientPumpTime(long ingredientId) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;

    /**
     * Get specific pump time for ingredient k
     * @param ingredient ingredient k
     * @return pump time in milliseconds
     * @throws TooManyTimesSettedIngredientEcxception There are multiple times setted. only one time is allowed.
     * @throws NoSuchIngredientSettedException There is no such ingredient. The id is not known.
     */
    public int getSpecificIngredientPumpTime(Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException;

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
     * Get associated image addresses.
     * @return list of image addresses
     */
    public List<String> getImageUrls();

    /**
     * Get recommended topics.
     * @return recommended topics
     */
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

    public void addOrUpdate(Topic topic);

    public void addOrUpdate(String imageUrls);

    /**
     * Remove Ingredient from Recipe.
     * @param ingredient
     */
    public void remove(Ingredient ingredient);
    /**
     * Remove Ingredient from Recipe.
     * @param ingredientId
     */
    public void removeIngredient(long ingredientId);

    public void remove(Topic topic);

    public void removeTopic(long topicId);

    public void remove(SQLRecipeImageUrlElement url);

    public void removeUrl(long urlId);


    //this Instance


    public default JSONObject asMessage(){
        //TODO
        return new JSONObject();
    }



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

    /**
     * Static access to recipes.
     * Get Recipe with id k.
     * @param id id k
     * @return Recipe
     */
    public static Recipe getRecipe(long id) {
        try {
            return DatabaseConnection.getDataBase().getRecipe(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Static access to recipes.
     * Get available recipes.
     * @return list of recipes
     */
    public static List<Recipe> getRecipes() throws NotInitializedDBException {
        return (List<Recipe>) DatabaseConnection.getDataBase().getAvailableRecipes();
    }

    /**
     * Static access to recipes.
     * Get available recipes with id in list of ids k
     * @param ids list of ids k
     * @return list of recipes
     */
    public static List<Recipe> getRecipes(List<Long> ids) {
        try {
            return (List<Recipe>) DatabaseConnection.getDataBase().getRecipes(ids);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Make a new recipe.
     * @param name name of the new recipe
     * @return new recipe instance with given name. It is already saved in the database!
     */
    public static Recipe makeNew(String name){
        return new SQLRecipe(name);
    }

}
