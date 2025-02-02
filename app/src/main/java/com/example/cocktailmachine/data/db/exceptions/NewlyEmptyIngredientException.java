package com.example.cocktailmachine.data.db.exceptions;

import com.example.cocktailmachine.data.Ingredient;

/**
 * is thrown when the ingredient is freshly empty and needs refilling
 * @author Johanna Reidt
 */
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
        //this.ingredient = Ingredient.getIngredient(ingredientId);
    }

    /**
     *
     * @return related ingredient
     * @author Johanna Reidt
     */
    public Ingredient getIngredient() {
        return this.ingredient;
    }
}
