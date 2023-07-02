package com.example.cocktailmachine.data.db.exceptions;

import com.example.cocktailmachine.data.db.elements.SQLRecipe;

/**
 * Thrown if the ingredient is already present in the recipe
 *
 * @author Johanna Reidt
 */
public class AlreadySetIngredientException extends Throwable {
    public AlreadySetIngredientException(SQLRecipe recipe, long ingredientID){
        super("The Ingredient with id "+ingredientID+" is already set in Recipe "+recipe.getName()+" with id "+recipe.getID()+".");
    }
}
