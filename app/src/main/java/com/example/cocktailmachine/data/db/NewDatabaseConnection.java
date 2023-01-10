package com.example.cocktailmachine.data.db;
import android.content.Context;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.IngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.elements.RecipeImageUrlElement;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

import java.util.HashMap;
import java.util.List;

public interface NewDatabaseConnection {

    static NewDatabaseConnection db = null;

    public static void intialize(Context context){
        NewSQLDatabaseConnection.initialize(context);
    }

    public static void intialize(Context context, UserPrivilegeLevel privilege){
        NewSQLDatabaseConnection.initialize(context, privilege);
    }

    /**
     * From this class should only one single object exist.
     * This is treated as Singleton.
     * However it needs context to be initialized.
     * As long no context exists, this throws an Exception.
     * @return singleton
     */
    public static NewDatabaseConnection getDataBase(){
        return NewSQLDatabaseConnection.getSingleton();
    }


    void emptyUpPumps();

    void setUpPumps();



    //LOAD

    void loadBufferWithAvailable();

    void loadEmpty();

    List<? extends Recipe> loadAvailableRecipes();

    List<? extends Ingredient> loadAvailableIngredients();



    //CHECK

    boolean checkavailablilityofallingredients(HashMap<Long, Integer> ingredients);



    //GET

    public List<SQLIngredientPump> getIngredientPumps();

    /**
     * Get Ingredient with id. If current App-User has the privilege
     * of an user, this returns the Ingredient, when the ingredient is available.
     * If current App-User has the privilege
     * of an admin, this returns the Ingredient regardless of availability.
     * @param id search
     * @return Ingredient
     */
    public Ingredient getIngredient(Long id);

    /**
     * Get Ingredients with id in given list. If current App-User has the privilege
     * of an user, this returns only available ingredients.
     * If current App-User has the privilege
     * of an admin, this returns the Ingredient regardless of availability.
     * @param ingredients search
     * @return Ingredient
     */
    public List<Ingredient> getIngredients(List<Long> ingredients);

    /**
     * Get Recipe with id. If current App-User has the privilege
     * of an user, this returns the Recipe, when the Recipe is available.
     * If current App-User has the privilege
     * of an admin, this returns the Recipe regardless of availability.
     * @param id search
     * @return Recipe
     */
    public Recipe getRecipe(Long id);

    public List<? extends Recipe> getRecipes(List<Long> recipeIds);

    /**
     * Get Pump with id. If current App-User has the privilege
     * of an user, this returns the Recipe, when the Recipe is available.
     * If current App-User has the privilege
     * of an admin, this returns the Recipe regardless of availability.
     * @param id search
     * @return Recipe
     */
    public Pump getPump(Long id);

    public Topic getTopic(long id);

    List<? extends Ingredient> getAvailableIngredients();

    List<? extends Recipe> getAvailableRecipes();

    List<String> getUrls(SQLIngredient newSQLIngredient);

    List<String> getUrls(SQLRecipe newSQLRecipe);

    List<Topic> getTopics(SQLRecipe newSQLRecipe);

    List<Topic> getTopics(Recipe recipe);

    List<Topic> getTopics();

    List<SQLRecipeIngredient> getPumpTimes(SQLRecipe newSQLRecipe);

    /**
     * Get recipes with regex in name.
     * @param regex
     * @return Recipes with regex in name
     */
    List<Recipe> getRecipeWith(String regex);

    /**
     * Get recipes with regex in name.
     * @param regex
     * @return
     */
    List<Ingredient> getIngredientWith(String regex);

    /**
     * Get User Privilege.
     * @return privilege
     */
    UserPrivilegeLevel getPrivilege();

    List<? extends Pump> getPumps();





    //REMOVE

    void removeRecipe(long id);

    void removeIngredient(long id);

    void removePump(long id);


    public void remove(Ingredient ingredient);

    public void remove(Recipe recipe);

    public void remove(Pump pump);


    void remove(SQLRecipeTopic newSQLRecipeTopic);

    void remove(SQLTopic newSQLTopic);

    void remove(SQLRecipeIngredient newSQLRecipeIngredient);

    void remove(SQLIngredientPump sqlIngredientPump);

    void remove(IngredientImageUrlElement ingredientImageUrlElement);

    void remove(RecipeImageUrlElement recipeImageUrlElement);






    //ADD OR UPDATE

    void addOrUpdate(SQLIngredient ingredient);

    void addOrUpdate(SQLRecipe recipe);

    void addOrUpdate(SQLRecipeTopic newSQLRecipeTopic);

    void addOrUpdate(SQLIngredientPump newSQLIngredientPump);

    void addOrUpdate(SQLRecipeIngredient newSQLRecipeIngredient);

    void addOrUpdate(RecipeImageUrlElement recipeImageUrlElement);

    void addOrUpdate(IngredientImageUrlElement ingredientImageUrlElement);

    void addOrUpdate(SQLTopic newSQLTopic);

    void addOrUpdate(SQLPump newSQLPump);

}
