package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

public class SQLRecipeImageUrlElement extends SQLImageUrlElement {
    private static final String TAG = "SQLRecipeImageUrlEl" ;

    public SQLRecipeImageUrlElement(long ID, String url, long ingredientID) {
        super(ID, url, ingredientID);
    }

    public SQLRecipeImageUrlElement(String url, long ownerID) {
        super(url, ownerID);
    }



    @Override
    public boolean save() {
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
            Log.i(TAG, "delete: successful deleted");
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "delete: failed");
        }
    }
}
