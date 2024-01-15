package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class PumpTable extends BasicColumn<SQLPump> {

    public static final String TABLE_NAME = "Pump";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_SLOT_ID = "SlotID";

        public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_SLOT_ID = TYPE_INTEGER;

        PumpTable(){
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
            columns.add(COLUMN_NAME_INGREDIENT_ID);
            columns.add(COLUMN_NAME_SLOT_ID);
            return columns;
        }

        @Override
        public List<String> getColumnTypes() {
            List<String> types = new LinkedList<>();
            types.add(COLUMN_TYPE_ID);
            types.add(COLUMN_TYPE_INGREDIENT_ID);
            types.add(COLUMN_TYPE_SLOT_ID);
            return types;
        }

    @Override
    protected List<Long> getAvailableIDs(SQLiteDatabase db) {

        return this.getIDs(db);
    }

    @Override
        public SQLPump makeElement(Cursor cursor) {
            long i_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
            int slot_id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SLOT_ID));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            SQLPump pump = new SQLPump(id, slot_id);
            pump.preSetIngredient(i_id);
            return pump;
        }

        @Override
        public ContentValues makeContentValues(SQLPump element) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_INGREDIENT_ID, element.preGetIngredientID());
            cv.put(COLUMN_NAME_SLOT_ID, element.getSlot());
            return cv;
        }


    public List<? extends SQLPump> getPumpWithSlot(SQLiteDatabase readableDatabase, int slot) {
        try {
            return this.getElementsIn(readableDatabase, COLUMN_NAME_SLOT_ID, new LinkedList<Object>());
        } catch (NoSuchColumnException e) {
            return new LinkedList<>();
        }

    }
}
