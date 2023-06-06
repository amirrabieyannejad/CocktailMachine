package com.example.cocktailmachine.data.db.elements.exceptions;

import com.example.cocktailmachine.data.db.elements.SQLRecipe;

public class AlreadySetIngredientException extends Throwable {
    AlreadySetIngredientException(SQLRecipe recipe, long ingredientID){
        super("The Ingredient with id "+ingredientID+" is already set in Recipe "+recipe.getName()+" with id "+recipe.getID()+".");
    }
}
