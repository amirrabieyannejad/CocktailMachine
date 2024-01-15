package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_INTEGER;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;

import java.util.ArrayList;
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
    protected List<Long> getAvailableIDs(SQLiteDatabase db) {

        return new ArrayList<>();
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
            return this.getElementsWith(db, COLUMN_NAME_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            Log.e(TAG, "error", e);
            return new ArrayList<>();
        }
    }

    public  List<SQLRecipeIngredient> getAvailable(SQLiteDatabase db,
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
            Log.e(TAG, "error", e);
            return new ArrayList<>();
        }
    }


    public List<SQLRecipeIngredient> getWithRecipes(SQLiteDatabase db,
                                                   List<Long> recipeIDs){
        List<Object> resIDs = new ArrayList<>();
        for(Long i: recipeIDs){
            resIDs.add((Object) i);
        }
        try {
            this.getElementsIn(db, COLUMN_NAME_RECIPE_ID, resIDs);
        } catch (NoSuchColumnException e) {
            Log.e(TAG, "getWithRecipe" );
        }
        return new ArrayList<>();
    }


    public List<Long> getRecipeIDsWithIngs(SQLiteDatabase db, List<Long> ingIDs){
        List<Object> resIDs = new ArrayList<>();
        for(Long i: ingIDs){
            resIDs.add((Object) i);
        }
        List<SQLRecipeIngredient> temp = new ArrayList<>();
        try {
            temp = this.getElementsIn(db, COLUMN_NAME_INGREDIENT_ID, resIDs);

        } catch (NoSuchColumnException e) {
            Log.e(TAG, "getWithIngredients" );
        }
        List<Long> res = new ArrayList<>();
        for(SQLRecipeIngredient ri: temp){
            res.add(ri.getRecipeID());
        }

        return res;
    }

    public List<Long> getRecipeIDsWithoutIngs(SQLiteDatabase db, List<Long> ingIDs){
        List<Object> resIDs = new ArrayList<>();
        for(Long i: ingIDs){
            resIDs.add((Object) i);
        }
        List<SQLRecipeIngredient> temp = new ArrayList<>();
        try {
            temp = this.getElementsNotIn(db, COLUMN_NAME_INGREDIENT_ID, resIDs);

        } catch (NoSuchColumnException e) {
           Log.e(TAG, "getWithIngredients" );
        }
        List<Long> res = new ArrayList<>();
        for(SQLRecipeIngredient ri: temp){
            res.add(ri.getRecipeID());
        }

        return res;
    }




    public List<SQLRecipeIngredient> getWithIngredients(SQLiteDatabase db,
                                                        List<Long> recipeIDs){
        List<Object> resIDs = new ArrayList<>();
        for(Long i: recipeIDs){
            resIDs.add((Object) i);
        }
        List<SQLRecipeIngredient> res = new ArrayList<>();
        try {
            res = this.getElementsIn(db, COLUMN_NAME_INGREDIENT_ID, resIDs);

        } catch (NoSuchColumnException e) {
           Log.e(TAG, "getWithIngredients" );
        }

        return res;
    }

    private List<Long> getRecipeIDs(SQLiteDatabase db){
        Cursor cursor = db.query(this.getName(),
                this.getColumns().toArray(new String[0]),
                null,
                null,
                null,
                null,
                null);

        List<Long> ids = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(COLUMN_NAME_RECIPE_ID);
        if(cursor.moveToFirst()) {
            ids.add(cursor.getLong(id_index));
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(id_index));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+ids);
        //cursor.close();
        //db.close();
        return ids;
    }

    private List<Long> getIngredientIDs(SQLiteDatabase db){
        Cursor cursor = db.query(this.getName(),
                new String[]{COLUMN_NAME_INGREDIENT_ID},
                null,
                null,
                null,
                null,
                null);

        List<Long> ids = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID);
        if(cursor.moveToFirst()) {
            ids.add(cursor.getLong(id_index));
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(id_index));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+ids);
        //db.close();
        return ids;
    }



    public List<SQLRecipeIngredient> getWithIngredientsOnlyFullRecipe(SQLiteDatabase db,
                                                        List<Long> ingIDs){
        List<Object> resIDs = new ArrayList<>();
        for(Long i: ingIDs){
            resIDs.add((Object) i);
        }
        List<SQLRecipeIngredient> res = new ArrayList<>();
        List<Long> recipeIDs = this.getRecipeIDsWithoutIngs(db, ingIDs);
        try {
            res = this.getElementsNotIn(db, COLUMN_NAME_RECIPE_ID, resIDs);

        } catch (NoSuchColumnException e) {
            Log.e(TAG, "getWithRecipe" );
        }

        return res;
    }

    public List<SQLRecipeIngredient> getElements(SQLiteDatabase db, Recipe recipe, Ingredient ingredient) throws NoSuchColumnException {
        if(!getColumns().contains(COLUMN_NAME_RECIPE_ID)){
            throw new NoSuchColumnException(getName(), COLUMN_NAME_RECIPE_ID);
        }
        if(!getColumns().contains(COLUMN_NAME_INGREDIENT_ID)){
            throw new NoSuchColumnException(getName(), COLUMN_NAME_INGREDIENT_ID);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                new String[]{_ID},
                COLUMN_NAME_RECIPE_ID+" = "+recipe.getID()+" AND "+COLUMN_NAME_INGREDIENT_ID+" = "+ingredient.getID(),
                null,
                null,
                null,
                null,
                null,
                null);
        //db.close();
        return this.cursorToList(cursor);
    }

    public List<Long> getIngredientIDs(SQLiteDatabase db, Recipe recipe) {
        Cursor cursor = db.query(this.getName(),
                new String[]{COLUMN_NAME_INGREDIENT_ID},
                COLUMN_NAME_RECIPE_ID+" = "+recipe.getID(),
                null,
                null,
                null,
                null);

        List<Long> ids = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(COLUMN_NAME_INGREDIENT_ID);
        if(cursor.moveToFirst()) {
            ids.add(cursor.getLong(id_index));
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(id_index));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+ids);
        //cursor.close();
        //db.close();
        return ids;
    }

    public int getVolume(SQLiteDatabase db, SQLRecipe recipe, Ingredient ingredient) throws TooManyTimesSettedIngredientEcxception {
        //TO DO: check this

        Cursor cursor = db.query(this.getName(),
                new String[]{COLUMN_NAME_PUMP_TIME},
                COLUMN_NAME_RECIPE_ID+
                        " = "+
                        recipe.getID() +
                        " AND "+
                        COLUMN_NAME_INGREDIENT_ID +
                        " = "+
                        ingredient.getID(),
                null,
                null,
                null,
                null);

        List<Integer> ids = new ArrayList<>();
        int id_index = cursor.getColumnIndexOrThrow(COLUMN_NAME_PUMP_TIME);
        if(cursor.moveToFirst()) {
            ids.add(cursor.getInt(id_index));
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(id_index));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+ids);
        cursor.close();
        if(ids.isEmpty()){
            return -1;
        }
        if(ids.size()>1){
            throw new TooManyTimesSettedIngredientEcxception(recipe, ingredient.getID());
        }
        return ids.get(0);
    }

    public List<SQLRecipeIngredient> getElements(SQLiteDatabase db, SQLRecipe sqlRecipe) {
        try {
            return this.getElementsWith(db,COLUMN_NAME_RECIPE_ID, String.valueOf(sqlRecipe.getID()));
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
    }
}
