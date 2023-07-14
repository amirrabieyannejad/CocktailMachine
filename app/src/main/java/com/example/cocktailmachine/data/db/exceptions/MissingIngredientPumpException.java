package com.example.cocktailmachine.data.db.exceptions;

import androidx.annotation.Nullable;

/**
 * is thrown when the connection between the pump and the ingredient is missing.
 * @author Johanna Reidt
 */
public class MissingIngredientPumpException extends Throwable{
    public MissingIngredientPumpException(@Nullable String message) {
        super(message);
    }
}
