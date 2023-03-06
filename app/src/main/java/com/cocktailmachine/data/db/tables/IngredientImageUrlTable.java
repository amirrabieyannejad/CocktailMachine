package com.cocktailmachine.data.db.tables;


import android.database.sqlite.SQLiteDatabase;

import com.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;

import java.util.List;

public class IngredientImageUrlTable extends ImageUrlTable {
    IngredientImageUrlTable(){
        super();
        this.TABLE_NAME = "IngredientImageUrl";
    }

    @Override
    public SQLImageUrlElement initializeElement(String url, long owner_id) {
        return new SQLIngredientImageUrlElement(url, owner_id);
    }

    @Override
    public SQLImageUrlElement initializeElement(long id, String url, long owner_id) {
        return new SQLIngredientImageUrlElement(id, url, owner_id);
    }

    @Override
    public List<SQLIngredientImageUrlElement> getElements(SQLiteDatabase db, long id) {
        return (List<SQLIngredientImageUrlElement>) super.getElements(db, id);
    }
}
