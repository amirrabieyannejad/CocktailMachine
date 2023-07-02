package com.example.cocktailmachine.data.db.exceptions;

import com.example.cocktailmachine.data.db.elements.SQLRecipe;


/**
 * is thrown if the ingredient is not included in the recipe
 * @author Johanna Reidt
 */
public class NoSuchIngredientSettedException extends Exception {
    public NoSuchIngredientSettedException(SQLRecipe recipe, long ingredientID) {
        super("In Recipe"
                +recipe.getName()
                +" with id "
                + recipe.getID()
                +" the Ingredient with id"
                + ingredientID
                +" was not found!!! ");
    }
}
