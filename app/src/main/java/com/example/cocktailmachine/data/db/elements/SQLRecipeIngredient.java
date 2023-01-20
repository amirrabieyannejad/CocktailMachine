package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class SQLRecipeIngredient extends SQLDataBaseElement {
    private long ingredientID = -1L;
    private long recipeID = -1L;
    private int pumpTime = -1;

    public SQLRecipeIngredient(long ingredientID, long recipeID, int pumpTime) {
        super();
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.pumpTime = pumpTime;
    }

    public SQLRecipeIngredient(long id, long ingredientID, long recipeID, int pumpTime) {
        super(id);
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.pumpTime = pumpTime;
    }

    public long getIngredientID() {
        return ingredientID;
    }

    public long getRecipeID() {
        return recipeID;
    }

    public int getPumpTime() {
        return pumpTime;
    }

    public void setPumpTime(int pumpTime) {
        this.pumpTime = pumpTime;
        this.wasChanged();
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
    }
}
