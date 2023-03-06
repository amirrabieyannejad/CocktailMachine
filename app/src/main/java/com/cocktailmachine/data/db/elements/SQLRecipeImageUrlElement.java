package com.cocktailmachine.data.db.elements;

import com.cocktailmachine.data.db.DatabaseConnection;
import com.cocktailmachine.data.db.NotInitializedDBException;

public class SQLRecipeImageUrlElement extends SQLImageUrlElement {
    public SQLRecipeImageUrlElement(long ID, String url, long ingredientID) {
        super(ID, url, ingredientID);
    }

    public SQLRecipeImageUrlElement(String url, long ownerID) {
        super(url, ownerID);
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
