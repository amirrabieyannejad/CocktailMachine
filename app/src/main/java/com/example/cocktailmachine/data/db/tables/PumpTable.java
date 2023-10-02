package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.cocktailmachine.data.db.elements.SQLPump;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class PumpTable extends BasicColumn<SQLPump> {

        public static final String TABLE_NAME = "Pump";
        public static final String COLUMN_NAME_MINIMUM_PUMP_VOLUME = "MinimumPumpVolume";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_SLOT_ID = "SlotID";

        public static final String COLUMN_TYPE_ID = TYPE_ID;
        public static final String COLUMN_TYPE_MINIMUM_PUMP_VOLUME = TYPE_INTEGER;
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
            List<String> columns = new ArrayList<>();
            columns.add(this._ID);
            columns.add(COLUMN_NAME_MINIMUM_PUMP_VOLUME);
            columns.add(COLUMN_NAME_INGREDIENT_ID);
            columns.add(COLUMN_NAME_SLOT_ID);
            return columns;
        }

        @Override
        public List<String> getColumnTypes() {
            List<String> types = new ArrayList<>();
            types.add(COLUMN_TYPE_ID);
            types.add(COLUMN_TYPE_MINIMUM_PUMP_VOLUME);
            types.add(COLUMN_TYPE_INGREDIENT_ID);
            types.add(COLUMN_TYPE_SLOT_ID);
            return types;
        }

        @Override
        public SQLPump makeElement(Cursor cursor) {
            long i_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
            int mlpims = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_MINIMUM_PUMP_VOLUME));
            int slot_id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SLOT_ID));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            SQLPump pump = new SQLPump(id, mlpims, slot_id);
            pump.preSetIngredient(i_id);
            return pump;
        }

        @Override
        public ContentValues makeContentValues(SQLPump element) {
            ContentValues cv = new ContentValues();
            if(element.getCurrentIngredient() != null) {
                cv.put(COLUMN_NAME_INGREDIENT_ID, element.getCurrentIngredient().getID());
            }
            cv.put(COLUMN_NAME_MINIMUM_PUMP_VOLUME, element.getMinimumPumpVolume());
            cv.put(COLUMN_NAME_SLOT_ID, element.getSlot());
            return cv;
        }


}
