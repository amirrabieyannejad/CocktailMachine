package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class IngredientImageUrlElement extends ImageUrlElement{
    public IngredientImageUrlElement(long ID, String url, long ingredientID) {
        super(ID, url, ingredientID);
    }

    public IngredientImageUrlElement(String url, long ownerID) {
        super(url, ownerID);
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
