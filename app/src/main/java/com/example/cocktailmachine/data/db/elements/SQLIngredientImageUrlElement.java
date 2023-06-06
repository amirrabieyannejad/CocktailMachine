package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

public class SQLIngredientImageUrlElement extends SQLImageUrlElement {
    private static final String TAG = "SQLIngredientImageUrlEl";

    public SQLIngredientImageUrlElement(long ID, String url, long ingredientID) {
        super(ID, url, ingredientID);
    }

    public SQLIngredientImageUrlElement(String url, long ownerID) {
        super(url, ownerID);
    }

    @Override
    public void save() throws NotInitializedDBException {
        Log.i(TAG, "save");
        DatabaseConnection.getDataBase().addOrUpdate(this);

        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        Log.i(TAG, "delete");

        DatabaseConnection.getDataBase().remove(this);
    }


}
