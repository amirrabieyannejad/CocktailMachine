package com.example.cocktailmachine.data.db.elements;

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
