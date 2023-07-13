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
    public boolean save() {
        Log.i(TAG, "save");
        try {
            DatabaseConnection.getDataBase().addOrUpdate(this);
            this.wasSaved();
            return true;
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void delete() {
        Log.i(TAG, "delete");

        try {
            DatabaseConnection.getDataBase().remove(this);
            Log.i(TAG, "delete: success");
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "delete: failed");
        }
    }


}
