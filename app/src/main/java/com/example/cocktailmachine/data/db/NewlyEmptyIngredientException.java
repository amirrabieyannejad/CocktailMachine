package com.example.cocktailmachine.data.db;

import com.example.cocktailmachine.data.Ingredient;

public class NewlyEmptyIngredientException extends Exception{
    public Ingredient ingredient;
    public NewlyEmptyIngredientException(Ingredient ingredient) {
        super(ingredient.asMessage().toString());
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
