package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class RecipeIngredientTable extends BasicColumn<SQLRecipeIngredient> {


    public static final String TABLE_NAME = "RecipeIngredient";
    public static final String COLUMN_NAME_RECIPE_ID = "RecipeID";
    public static final String COLUMN_NAME_INGREDIENT_ID = "IngredientID";
    public static final String COLUMN_NAME_PUMP_TIME = "pump_time";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_RECIPE_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_INGREDIENT_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_PUMP_TIME = TYPE_INTEGER;
    private static final String TAG = "RecipeIngredientTable";

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
        cv.put(COLUMN_NAME_RECIPE_ID, element.getRecipeID());
        cv.put(COLUMN_NAME_INGREDIENT_ID, element.getIngredientID());
        cv.put(COLUMN_NAME_PUMP_TIME, element.getVolume());
        return cv;
    }

    public List<SQLRecipeIngredient> getIngredientVolumes(SQLiteDatabase db, SQLRecipe recipe) {
        try {
            return this.getElementsWith(db, COLUMN_TYPE_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public  List<SQLRecipeIngredient>  getAvailable(SQLiteDatabase db,
                                                    List<? extends Recipe> recipes){
        if(recipes.size() == 1){
            return getIngredientVolumes(db, (SQLRecipe) recipes.get(0));
        }

        try {
            List<Object> ids = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ids = recipes.stream()
                        .map(Recipe::getID)
                        .collect(Collectors.toList());
            }
            else{
                ids = Helper.getRecipeHelper().getIdsExtend(recipes);
            }
            return this.getElementsIn(
                    db,
                    COLUMN_NAME_RECIPE_ID,
                    ids);
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<SQLRecipeIngredient> getWithRecipe(SQLiteDatabase db,
                                                   List<Long> recipeIDs){
        try {
            this.getElementsIn(db, COLUMN_NAME_RECIPE_ID, Collections.singletonList(recipeIDs));
        } catch (NoSuchColumnException e) {
            Log.e(TAG, "getWithRecipe" );
        }
        return new ArrayList<>();
    }



}
