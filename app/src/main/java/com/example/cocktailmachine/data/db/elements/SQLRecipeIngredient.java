package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class SQLRecipeIngredient extends DataBaseElement {
    private long ingredientID;
    private long recipeID;
    private int pumpTime;

    public SQLRecipeIngredient(long ingredientID, long recipeID, int pumpTime) {
        super();
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.pumpTime = pumpTime;
        this.save();
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
    void save() {
        NewDatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    void delete() {
        NewDatabaseConnection.getDataBase().remove(this);
    }
}
