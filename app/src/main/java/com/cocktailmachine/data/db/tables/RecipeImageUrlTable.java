package com.cocktailmachine.data.db.tables;

import android.database.sqlite.SQLiteDatabase;

import com.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;

import java.util.List;

public class RecipeImageUrlTable extends ImageUrlTable{
    RecipeImageUrlTable(){
        super();
        this.TABLE_NAME = "RecipeImageUrl";
    }

    @Override
    public SQLImageUrlElement initializeElement(String url, long owner_id) {
        return new SQLRecipeImageUrlElement(url, owner_id);
    }

    @Override
    public SQLImageUrlElement initializeElement(long id, String url, long owner_id) {
        return new SQLRecipeImageUrlElement(id, url, owner_id);
    }

    @Override
    public List<SQLRecipeImageUrlElement> getElements(SQLiteDatabase db, long id) {
        return (List<SQLRecipeImageUrlElement>) super.getElements(db, id);
    }
}
