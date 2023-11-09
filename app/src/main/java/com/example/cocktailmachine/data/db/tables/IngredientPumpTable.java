package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:50
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class IngredientPumpTable extends BasicColumn<SQLIngredientPump> {


    public static final String TABLE_NAME = "IngredientPump";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_PUMP_ID = "PumpID";
    public static final String COLUMN_NAME_VOLUME = "volume";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_PUMP_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_VOLUME = TYPE_INTEGER;
    private static final String TAG = "IngredientPumpTable";

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_PUMP_ID);
        columns.add(COLUMN_NAME_INGREDIENT_ID);
        columns.add(COLUMN_NAME_VOLUME
        );
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_INGREDIENT_ID);
        columns.add(COLUMN_TYPE_PUMP_ID);
        columns.add(COLUMN_TYPE_VOLUME);
        return columns;
    }

    @Override
    public SQLIngredientPump makeElement(Cursor cursor) {
        long this_id = cursor.getLong(cursor.getColumnIndexOrThrow(this._ID));
        long i_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
        long p_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_PUMP_ID));
        int fill_level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_VOLUME));
        return new SQLIngredientPump(this_id, fill_level, p_id, i_id);
    }

    @Override
    public ContentValues makeContentValues(SQLIngredientPump element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_INGREDIENT_ID, element.getIngredientID());
        cv.put(COLUMN_NAME_PUMP_ID, element.getPumpID());
        cv.put(COLUMN_NAME_VOLUME, element.getVolume());
       // Log.v(TAG, "makeContentValues"+cv.toString());
        return cv;
    }

    public List<SQLIngredientPump> getElements(SQLiteDatabase db, Pump pump){
        try {
            return this.getElementsWith(db, COLUMN_NAME_PUMP_ID, String.valueOf(pump.getID()));
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
    }


    public boolean hasIngredient(SQLiteDatabase db, long ingredientID) {
        try {
            return !this.getElementsWith(db, COLUMN_NAME_INGREDIENT_ID, String.valueOf(ingredientID)).isEmpty();
        } catch (NoSuchColumnException e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    public List<Long> getIngredientIDs(SQLiteDatabase db) {
        Cursor cursor = db.query(true,
                this.getName(),
                new String[]{COLUMN_NAME_INGREDIENT_ID},
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        List<Long> res = this.cursorToIngredientIDList(cursor);
        db.close();
        return res;
    }

    /**
     * reads with given cursor each given rows to List of ids
     * @author Johanna Reidt
     * @param cursor
     * @return Long list
     */
    private List<Long> cursorToIngredientIDList(Cursor cursor){
        // Log.v(TAG, "cursorToList");
        List<Long> res = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID);
        if(cursor.moveToFirst()) {
            res.add(cursor.getLong(id_index));
            while (cursor.moveToNext()) {
                res.add(cursor.getLong(id_index));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+res);
        return res;
    }
}
