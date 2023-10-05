package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_TEXT;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_BOOLEAN;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class RecipeTable extends BasicColumn<SQLRecipe>{

    public static final String TABLE_NAME = "Recipe";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_ALCOHOLIC = "alcoholic";
    public static final String COLUMN_NAME_AVAILABLE = "available";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_NAME = TYPE_TEXT;
    public static final String COLUMN_TYPE_ALCOHOLIC = TYPE_BOOLEAN;
    public static final String COLUMN_TYPE_AVAILABLE = TYPE_BOOLEAN;



    RecipeTable() {
        super();
    }

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    public List<SQLRecipe> getElement(SQLiteDatabase db, String name){
        try {
            return this.getElementsLike(db, COLUMN_NAME_NAME, name);
        } catch (NoSuchColumnException e) {
            Log.e("RecipeTable", "getElement", e);
            Log.getStackTraceString(e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_NAME);
        columns.add(COLUMN_NAME_ALCOHOLIC);
        columns.add(COLUMN_NAME_AVAILABLE);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_NAME);
        columns.add(COLUMN_TYPE_ALCOHOLIC);
        columns.add(COLUMN_TYPE_AVAILABLE);
        return columns;
    }

    @Override
    public SQLRecipe makeElement(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
        boolean alcoholic = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_ALCOHOLIC));
        boolean available = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_AVAILABLE));

        return new SQLRecipe(id, name, alcoholic, available);

    }

    @Override
    public ContentValues makeContentValues(SQLRecipe element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_NAME, element.getName());
        cv.put(COLUMN_NAME_ALCOHOLIC, element.isAlcoholic());
        cv.put(COLUMN_NAME_AVAILABLE, element.isAvailable());
        return cv;
    }

    public List<? extends Recipe> getAvailable(SQLiteDatabase db){
        try {
            return this.getElementsWith(db, COLUMN_NAME_AVAILABLE, String.valueOf(1));
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setEmptyPumps(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_AVAILABLE, false);
        try {
            this.updateColumnToConstant(db, cv);
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
        }
    }
}
