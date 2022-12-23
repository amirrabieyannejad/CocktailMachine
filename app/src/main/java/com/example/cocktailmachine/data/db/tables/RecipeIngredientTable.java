package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientTable extends BasicColumn<SQLRecipeIngredient> {


    public static final String TABLE_NAME = "RecipeIngredient";
    public static final String COLUMN_NAME_RECIPE_ID = "RecipeID";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_PUMP_TIME = "pump_time";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_RECIPE_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_PUMP_TIME = TYPE_INTEGER;

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_RECIPE_ID);
        columns.add(COLUMN_NAME_INGREDIENT_ID);
        columns.add(COLUMN_NAME_PUMP_TIME);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_RECIPE_ID);
        columns.add(COLUMN_TYPE_INGREDIENT_ID);
        columns.add(COLUMN_TYPE_PUMP_TIME);
        return columns;
    }

    @Override
    public SQLRecipeIngredient makeElement(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        long rid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_RECIPE_ID));
        long iid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
        int pt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_PUMP_TIME));
        return new SQLRecipeIngredient(id, iid, rid, pt);
    }

    @Override
    public ContentValues makeContentValues(SQLRecipeIngredient element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TYPE_RECIPE_ID, element.getRecipeID());
        cv.put(COLUMN_TYPE_INGREDIENT_ID, element.getIngredientID());
        cv.put(COLUMN_TYPE_PUMP_TIME, element.getPumpTime());
        return cv;
    }

    public List<SQLRecipeIngredient> getPumpTime(SQLiteDatabase db, SQLRecipe recipe) {
        try {
            return this.getElementsWith(db, COLUMN_TYPE_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
            return null;
        }
    }
}
