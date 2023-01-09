package com.example.cocktailmachine.data.db.tables;

import static android.text.style.TtsSpan.TYPE_TEXT;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_BOOLEAN;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLIngredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientTable extends BasicColumn<SQLIngredient> {

        public static final String TABLE_NAME = "Ingredient";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_ALCOHOLIC = "alcoholic";
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS = "FluidPumpedInMilliseconds";
        public static final String COLUMN_NAME_PUMP_ID = "PumpID";
        public static final String COLUMN_NAME_COLOR = "Color";

        public static final String COLUMN_TYPE_ID = TYPE_ID;
        public static final String COLUMN_TYPE_NAME = TYPE_TEXT;
        public static final String COLUMN_TYPE_ALCOHOLIC = TYPE_BOOLEAN;
        public static final String COLUMN_TYPE_AVAILABLE = TYPE_BOOLEAN;
        public static final String COLUMN_TYPE_FLUID_PUMPED_IN_MILLISECONDS = TYPE_INTEGER;
        public static final String COLUMN_TYPE_PUMP_ID= TYPE_LONG;
        public static final String COLUMN_TYPE_COLOR = TYPE_INTEGER;

        IngredientTable(){
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
            columns.add(COLUMN_NAME_NAME);
            columns.add(COLUMN_NAME_ALCOHOLIC);
            columns.add(COLUMN_NAME_AVAILABLE);
            columns.add(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS);
            columns.add(COLUMN_NAME_PUMP_ID);
            columns.add(COLUMN_NAME_COLOR);
            return columns;
        }

        @Override
        public List<String> getColumnTypes() {
            List<String> columns = new ArrayList<>();
            columns.add(COLUMN_TYPE_ID);
            columns.add(COLUMN_TYPE_NAME);
            columns.add(COLUMN_TYPE_ALCOHOLIC);
            columns.add(COLUMN_TYPE_AVAILABLE);
            columns.add(COLUMN_TYPE_FLUID_PUMPED_IN_MILLISECONDS);
            columns.add(COLUMN_TYPE_PUMP_ID);
            columns.add(COLUMN_TYPE_COLOR);
            return columns;
        }

        @Override
        public SQLIngredient makeElement(Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
            boolean alcoholic = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_ALCOHOLIC));
            boolean available = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_AVAILABLE));
            int fluid = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS));
            long pump_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_PUMP_ID));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_COLOR));
            return new SQLIngredient(id, name,
                    alcoholic, available, fluid, pump_id, color);
        }

        @Override
        public ContentValues makeContentValues(SQLIngredient element) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_NAME, element.getName());
            cv.put(COLUMN_NAME_ALCOHOLIC, element.isAlcoholic());
            cv.put(COLUMN_NAME_AVAILABLE, element.isAvailable());
            cv.put(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS, element.getFluidInMilliliter());
            cv.put(COLUMN_NAME_PUMP_ID, element.getPump().getID());
            cv.put(COLUMN_NAME_COLOR, element.getColor());
            return cv;
        }

        public List<SQLIngredient> getAvailable(SQLiteDatabase db){
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
            cv.put(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS, -1);
            cv.put(COLUMN_NAME_PUMP_ID, -1L);
            try {
                this.updateColumnToConstant(db, cv);
            } catch (NoSuchColumnException e) {
                e.printStackTrace();
            }
        }
}
