package com.example.cocktailmachine.data.db;

import com.example.cocktailmachine.data.db.elements.SQLIngredient;

public class NewlyEmptyIngredientException extends Exception{
    public SQLIngredient newSQLIngredient;
    public NewlyEmptyIngredientException(SQLIngredient newSQLIngredient) {
        super(newSQLIngredient.asMessage().toString());
        this.newSQLIngredient = newSQLIngredient;
    }

    public SQLIngredient getIngredient() {
        return newSQLIngredient;
    }
}
