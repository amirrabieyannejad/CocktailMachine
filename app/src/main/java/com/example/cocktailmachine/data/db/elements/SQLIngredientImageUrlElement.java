package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;

import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
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
    public boolean loadAvailable(Context context) {
        return false;
    }

    @Override
    public void save(Context context) {
        Log.i(TAG, "save");
        AddOrUpdateToDB.addOrUpdate(context, this);

    }

    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context, this);
    }


}
