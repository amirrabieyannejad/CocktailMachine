package com.cocktailmachine.data.db.elements;

public class AlreadySetIngredientException extends Throwable {
    AlreadySetIngredientException(SQLRecipe recipe, long ingredientID){
        super("The Ingredient with id "+ingredientID+" is already set in Recipe "+recipe.getName()+" with id "+recipe.getID()+".");
    }
}
