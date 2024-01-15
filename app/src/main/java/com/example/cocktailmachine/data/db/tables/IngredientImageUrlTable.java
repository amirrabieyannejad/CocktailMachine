package com.example.cocktailmachine.data.db.tables;


import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:50
 * @project CocktailMachine
 * @author Johanna Reidt
 */
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

    @Override
    protected List<Long> getAvailableIDs(SQLiteDatabase db) {

        return new LinkedList<>();
    }
}
