package com.example.cocktailmachine.data.db.exceptions;

import com.example.cocktailmachine.data.Ingredient;

public class NewlyEmptyIngredientException extends Exception{
    public Ingredient ingredient;
    public long ingredientId;
    public NewlyEmptyIngredientException(Ingredient ingredient) {
        super(ingredient.toString());
        this.ingredient = ingredient;
        this.ingredientId = ingredient.getID();
    }

    public NewlyEmptyIngredientException(long ingredientId) {
        super(String.valueOf(ingredientId));
        this.ingredientId = ingredientId;
        this.ingredient = Ingredient.getIngredient(ingredientId);
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
