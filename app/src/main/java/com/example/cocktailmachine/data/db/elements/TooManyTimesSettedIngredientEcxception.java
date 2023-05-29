package com.example.cocktailmachine.data.db.elements;

public class TooManyTimesSettedIngredientEcxception extends Exception {
    public TooManyTimesSettedIngredientEcxception(SQLRecipe recipe, long ingredientID) {
        super("In Recipe"
                +recipe.getName()
                +" with id "
                + recipe.getID()
                +" the Ingredient with id"
                + ingredientID
                +" was added to many times!!! "
                +"Every ingredient is only allowed to be added once!");
    }
}
