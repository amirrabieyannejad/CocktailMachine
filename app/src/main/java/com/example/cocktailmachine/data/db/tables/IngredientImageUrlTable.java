package com.example.cocktailmachine.data.db.tables;


import com.example.cocktailmachine.data.db.elements.ImageUrlElement;
import com.example.cocktailmachine.data.db.elements.IngredientImageUrlElement;

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
}
