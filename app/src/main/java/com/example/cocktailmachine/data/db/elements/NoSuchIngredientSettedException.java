package com.example.cocktailmachine.data.db.elements;

public class NoSuchIngredientSettedException extends Exception {
    public NoSuchIngredientSettedException(SQLRecipe recipe, long ingredientID) {
        super("In Recipe"
                +recipe.getName()
                +" with id "
                +Long.toString(recipe.getID())
                +" the Ingredient with id"
                +Long.toString(ingredientID)
                +" was not found!!! ");
    }
}
