package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.cocktailmachine.data.db.elements.SQLPump;

import java.util.ArrayList;
import java.util.List;

public class PumpTable extends BasicColumn<SQLPump> {

        public static final String TABLE_NAME = "Pump";
        public static final String COLUMN_NAME_MILLILITERS_PUMPED_IN_MILLISECONDS = "MillilitersPumpedInMilliseconds";
        public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";

        public static final String COLUMN_TYPE_ID = TYPE_ID;
        public static final String COLUMN_TYPE_MILLILITERS_PUMPED_IN_MILLISECONDS = TYPE_INTEGER;
        public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;

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
            columns.add(COLUMN_NAME_MILLILITERS_PUMPED_IN_MILLISECONDS);
            columns.add(COLUMN_NAME_INGREDIENT_ID);
            return columns;
        }

        @Override
        public List<String> getColumnTypes() {
            List<String> types = new ArrayList<>();
            types.add(COLUMN_TYPE_ID);
            types.add(COLUMN_TYPE_MILLILITERS_PUMPED_IN_MILLISECONDS);
            types.add(COLUMN_TYPE_INGREDIENT_ID);
            return types;
        }

        @Override
        public SQLPump makeElement(Cursor cursor) {
            Long i_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID));
            int mlpims = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_MILLILITERS_PUMPED_IN_MILLISECONDS));
            Long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            SQLPump pump = new SQLPump(id, mlpims);
            pump.setCurrentIngredient(id);
            return pump;
        }

        @Override
        public ContentValues makeContentValues(SQLPump element) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_INGREDIENT_ID, element.getCurrentIngredient().getID());
            cv.put(COLUMN_NAME_MILLILITERS_PUMPED_IN_MILLISECONDS, element.getMillilitersPumpedInMilliseconds());
            return cv;
        }


}
