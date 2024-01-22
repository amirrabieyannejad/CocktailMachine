package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.db.elements.SQLNewPump;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Johanna Reidt
 * @created Fr. 19.Jan 2024 - 13:35
 * @project CocktailMachine
 */
public class NewPumpTable extends BasicColumn<SQLNewPump>{


    public static final String TABLE_NAME = "NewPump";
    public static final String COLUMN_NAME_SLOT_ID = "SlotID";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_VOLUME = "volume";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_SLOT_ID = TYPE_INTEGER;
    public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_VOLUME = TYPE_INTEGER;
    private static final String TAG = "NewPumpTable";

    NewPumpTable(){
        super();
    }


    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new LinkedList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_SLOT_ID);
        columns.add(COLUMN_NAME_INGREDIENT_ID);
        columns.add(COLUMN_NAME_VOLUME);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> types = new LinkedList<>();
        types.add(COLUMN_TYPE_ID);
        types.add(COLUMN_TYPE_SLOT_ID);
        types.add(COLUMN_TYPE_INGREDIENT_ID);
        types.add(COLUMN_TYPE_VOLUME);
        return types;
    }

    @Override
    protected List<Long> getAvailableIDs(SQLiteDatabase db) {
        return this.getIDs(db);
    }

    @Override
    public SQLNewPump makeElement(Cursor cursor) {
        long ID = cursor.getLong(cursor.getColumnIndexOrThrow(this._ID));
        int slotID = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SLOT_ID));
        long ingID = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
        int volume = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_VOLUME));
        return new SQLNewPump(ID, slotID, ingID, volume);
    }

    @Override
    public ContentValues makeContentValues(SQLNewPump element) {
        return null;
    }
}
