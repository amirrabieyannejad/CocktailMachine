package com.example.cocktailmachine.data.db.elements;

import android.content.Context;

import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;

public class SQLIngredientImageUrlElement extends SQLImageUrlElement {
    private static final String TAG = "SQLIngredientImageUrlEl";

    public SQLIngredientImageUrlElement(long ID, String url, long ingredientID) {
        super(ID, url, ingredientID);
    }

    public SQLIngredientImageUrlElement(String url, long ownerID) {
        super(url, ownerID);
    }

    @Override
    public String getClassName() {
        return "SQLIngredientImageUrlElement";
    }

    @Override
    public boolean loadAvailable(Context context) {
        return false;
    }

    @Override
    public void save(Context context) {
       // Log.v(TAG, "save");
        AddOrUpdateToDB.addOrUpdate(context, this);

    }

    @Override
    public void delete(Context context) {
       // Log.v(TAG, "delete");
        DeleteFromDB.remove(context, this);
    }


}
