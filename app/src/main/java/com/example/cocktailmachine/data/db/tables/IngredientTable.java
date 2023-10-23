package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_TEXT;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_BOOLEAN;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class IngredientTable extends BasicColumn<SQLIngredient> {

    public static final String TABLE_NAME = "Ingredient";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_ALCOHOLIC = "alcoholic";
    //public static final String COLUMN_NAME_AVAILABLE = "available";
        //public static final String COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS = "FluidPumpedInMilliseconds";
        //public static final String COLUMN_NAME_PUMP_ID = "PumpID";
    public static final String COLUMN_NAME_COLOR = "Color";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_NAME = TYPE_TEXT;
    public static final String COLUMN_TYPE_ALCOHOLIC = TYPE_BOOLEAN;
    //public static final String COLUMN_TYPE_AVAILABLE = TYPE_BOOLEAN;
        //public static final String COLUMN_TYPE_FLUID_PUMPED_IN_MILLISECONDS = TYPE_INTEGER;
        //public static final String COLUMN_TYPE_PUMP_ID= TYPE_LONG;
    public static final String COLUMN_TYPE_COLOR = TYPE_INTEGER;
    private static final String TAG = "IngredientTable";

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
            //columns.add(COLUMN_NAME_AVAILABLE);
            //columns.add(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS);
            //columns.add(COLUMN_NAME_PUMP_ID);
            columns.add(COLUMN_NAME_COLOR);
            return columns;
    }
    @Override
    public List<String> getColumnTypes() {
            List<String> columns = new ArrayList<>();
            columns.add(COLUMN_TYPE_ID);
            columns.add(COLUMN_TYPE_NAME);
            columns.add(COLUMN_TYPE_ALCOHOLIC);
            //columns.add(COLUMN_TYPE_AVAILABLE);
            //columns.add(COLUMN_TYPE_FLUID_PUMPED_IN_MILLISECONDS);
            //columns.add(COLUMN_TYPE_PUMP_ID);
            columns.add(COLUMN_TYPE_COLOR);
            return columns;
    }
    @Override
    public SQLIngredient makeElement(Cursor cursor) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
            boolean alcoholic = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_ALCOHOLIC));
            //boolean available = 0 < cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_AVAILABLE));
            //int fluid = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS));
            //long pump_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_PUMP_ID));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_COLOR));
            return new SQLIngredient(id, name,
                    alcoholic, color);
    }
    @Override
    public ContentValues makeContentValues(SQLIngredient element) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_NAME, element.getName());
            cv.put(COLUMN_NAME_ALCOHOLIC, element.isAlcoholic());
            //cv.put(COLUMN_NAME_AVAILABLE, element.isAvailable());
            //cv.put(COLUMN_NAME_FLUID_PUMPED_IN_MILLISECONDS, element.getFillLevel());
            //cv.put(COLUMN_NAME_PUMP_ID, element.getPump().getID());
            cv.put(COLUMN_NAME_COLOR, element.getColor());
            return cv;
    }

    public List<SQLIngredient> getElements(SQLiteDatabase db, String name){
            try {
                return this.getElementsLike(db,
                        COLUMN_NAME_NAME,
                        name);
            } catch (NoSuchColumnException e) {
               // Log.v(TAG, "getElement name failed");
                Log.e(TAG, "error", e);
                Log.getStackTraceString(e);
            }
            return new ArrayList<>();
    }

    public HashMap<String, Long> getHashIngredientNameToID(SQLiteDatabase db) {
        Cursor cursor = db.query(this.getName(),
                getColumns().toArray(new String[0]),
                null,
                null,
                null,
                null,
                null);
        return this.cursorToHashIngredientNameToID(cursor);
    }

    public Iterator<HashMap<String, Long>> getIteratorHashIngredientNameToID(
            SQLiteDatabase db,
            int n) {
        return new Iterator<HashMap<String, Long>>() {
            private final List<Long> ids = IngredientTable.this.getIDs(db);
            private int position = 0;
            @Override
            public boolean hasNext() {
                return position<ids.size();
            }

            @Override
            public HashMap<String, Long> next() {
                int oldPosition = position;
                position = position + n;
                if(position>ids.size()){
                    position = ids.size();
                }
                List<Long> temp = ids.subList(oldPosition, position);
                return this.translate(IngredientTable.this.getElements(db, temp));
            }

            private HashMap<String, Long> translate(List<SQLIngredient> ings){
                HashMap<String, Long> hashIngsIDs = new HashMap<>();
                for(SQLIngredient ing: ings){
                    hashIngsIDs.put(ing.getName(), ing.getID());
                }
                return hashIngsIDs;
            }
        };
    }


    private HashMap<String, Long> cursorToHashIngredientNameToID(Cursor cursor){
       // Log.v(TAG, "cursorToList");
        HashMap<String, Long>  res = new HashMap<>();
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME_NAME);
        int idIndex = cursor.getColumnIndex(_ID);
        if(cursor.moveToFirst()) {
            res.put(cursor.getString(nameIndex), cursor.getLong(idIndex));
            while (cursor.moveToNext()) {
                res.put(cursor.getString(nameIndex), cursor.getLong(idIndex));
            }
        }
        cursor.close();
       // Log.v(TAG, "cursorToList : "+res);
        return res;
    }


    public List<SQLIngredient> getAvailableElements(SQLiteDatabase db){
        return this.cursorToList(
                db.query(true, this.getName(), this.getColumns().toArray(new String[]{}),
                this._ID+" IN (SELECT "+
                        IngredientPumpTable.COLUMN_NAME_INGREDIENT_ID+
                        " FROM "+Tables.TABLE_INGREDIENT_PUMP.getName()+")"
                        , null, null,null, null, null));
    }

    public List<SQLIngredient> getAvailableElements(SQLiteDatabase db, List<Long> ids){
        try {
            return this.cursorToList(
                    db.query(true, this.getName(), this.getColumns().toArray(new String[]{}),
                            "("+this._ID+" IN (SELECT "+
                                    IngredientPumpTable.COLUMN_NAME_INGREDIENT_ID+
                                    " FROM "+Tables.TABLE_INGREDIENT_PUMP.getName()+"))"+
                                    " AND "+this._ID +" IN "+makeSelectionList(this._ID, ids)
                            , null, null,null, null, null));
        } catch (NoSuchColumnException e) {
            Log.e(TAG, "getAvailableElements", e);
            return new ArrayList<>();
        }
    }


    public List<String> getNames(SQLiteDatabase db) {


        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToNames(cursor);

    }

    private List<String> cursorToNames(Cursor cursor){
        ArrayList<String> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));
            while (cursor.moveToNext()) {
                res.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+res);
        return res;
    }

    public List<? extends Ingredient> getElements(SQLiteDatabase readableDatabase, Recipe recipe) {
        ArrayList<Object> os = new ArrayList<>();
        os.addAll(Tables.TABLE_RECIPE_INGREDIENT.getIngredientIDs(readableDatabase, recipe));
        try {
            return this.getElementsIn(
                    readableDatabase,
                    _ID,
                    os
            );
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
    }
}
