package com.example.cocktailmachine.data.db.elements;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class SQLRecipeIngredient extends SQLDataBaseElement {
    private long ingredientID = -1L;
    private long recipeID = -1L;
    private int volume = -1;

    public SQLRecipeIngredient(long ingredientID, long recipeID, int volume) {
        super();
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.volume = volume;
    }

    public SQLRecipeIngredient(long id, long ingredientID, long recipeID, int volume) {
        super(id);
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.volume = volume;
    }

    public long getIngredientID() {
        return ingredientID;
    }

    public Ingredient getIngredient() {
        return Ingredient.getIngredient(this.ingredientID);
    }

    public long getRecipeID() {
        return recipeID;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.wasChanged();
    }
    public void setRecipeID(long recipeID) {
        this.recipeID= recipeID;
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

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("id: ");
        builder.append(this.getID());
        builder.append(", recipe id: ");
        builder.append(this.getRecipeID());
        builder.append(", ingredient id: ");
        builder.append(this.getIngredientID());
        builder.append(", volume: ");
        builder.append(this.getVolume());
        builder.append("}");
        return builder.toString();
    }



}
