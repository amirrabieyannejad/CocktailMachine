package com.example.cocktailmachine.data.db.tables;


import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.elements.ImageUrlElement;
import com.example.cocktailmachine.data.db.elements.IngredientImageUrlElement;

import java.util.List;

public class IngredientImageUrlTable extends ImageUrlTable {
    IngredientImageUrlTable(){
        super();
        this.TABLE_NAME = "IngredientImageUrl";
    }

    @Override
    public ImageUrlElement initializeElement(String url, long owner_id) {
        return new IngredientImageUrlElement(url, owner_id);
    }

    @Override
    public ImageUrlElement initializeElement(long id, String url, long owner_id) {
        return new IngredientImageUrlElement(id, url, owner_id);
    }

    @Override
    public List<IngredientImageUrlElement> getElements(SQLiteDatabase db, long id) {
        return (List<IngredientImageUrlElement>) super.getElements(db, id);
    }
}
