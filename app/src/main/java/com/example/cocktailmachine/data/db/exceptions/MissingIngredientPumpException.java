package com.example.cocktailmachine.data.db.exceptions;

import androidx.annotation.Nullable;

public class MissingIngredientPumpException extends Throwable{
    public MissingIngredientPumpException(@Nullable String message) {
        super(message);
    }
}
