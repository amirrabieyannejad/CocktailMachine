package com.example.cocktailmachine.data.db.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;

import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
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
