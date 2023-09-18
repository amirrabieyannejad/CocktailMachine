package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
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
    public void save(Context context) {
        Log.i(TAG, "save");
        AddOrUpdateToDB.addOrUpdate(context,this);
    }

    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context, this);
    }
}
