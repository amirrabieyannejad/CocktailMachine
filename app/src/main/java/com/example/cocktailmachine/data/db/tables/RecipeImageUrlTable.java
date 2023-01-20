package com.example.cocktailmachine.data.db.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.ImageUrlElement;
import com.example.cocktailmachine.data.db.elements.RecipeImageUrlElement;

import java.util.List;

public class RecipeImageUrlTable extends ImageUrlTable{
    RecipeImageUrlTable(){
        super();
        this.TABLE_NAME = "RecipeImageUrl";
    }

    @Override
    public ImageUrlElement initializeElement(String url, long owner_id) {
        return new RecipeImageUrlElement(url, owner_id);
    }

    @Override
    public ImageUrlElement initializeElement(long id, String url, long owner_id) {
        return new RecipeImageUrlElement(id, url, owner_id);
    }

    @Override
    public List<RecipeImageUrlElement> getElements(SQLiteDatabase db, long id) {
        return (List<RecipeImageUrlElement>) super.getElements(db, id);
    }
}
