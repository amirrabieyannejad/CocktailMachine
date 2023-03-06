package com.cocktailmachine.data.db;

import com.cocktailmachine.data.Ingredient;

public class NewlyEmptyIngredientException extends Exception{
    public Ingredient ingredient;
    public NewlyEmptyIngredientException(Ingredient ingredient) {
        super(ingredient.toString());
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
