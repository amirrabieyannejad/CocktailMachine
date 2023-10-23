package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class RecipeTopicTable extends BasicColumn<SQLRecipeTopic> {

    public static final String TABLE_NAME = "RecipeTopic";
    public static final String COLUMN_NAME_RECIPE_ID = "RecipeID";
    public static final String COLUMN_NAME_TOPIC_ID = "TopicID";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_RECIPE_ID = TYPE_LONG;
    public static final String COLUMN_TYPE_TOPIC_ID = TYPE_LONG;
    private static final String TAG = "RecipeTopicTable";

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_RECIPE_ID);
        columns.add(COLUMN_NAME_TOPIC_ID);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_RECIPE_ID);
        columns.add(COLUMN_TYPE_TOPIC_ID);
        return columns;
    }

    @Override
    public SQLRecipeTopic makeElement(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        long rid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_RECIPE_ID));
        long tid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TOPIC_ID));
        return new SQLRecipeTopic(id, rid, tid);
    }

    @Override
    public ContentValues makeContentValues(SQLRecipeTopic element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_RECIPE_ID, element.getRecipeID());
        cv.put(COLUMN_NAME_TOPIC_ID, element.getTopicID());
        return cv;
    }

    public List<SQLRecipeTopic> getElements(SQLiteDatabase db, Recipe recipe) {
        if(recipe == null){
            return new ArrayList<>();
        }
        try {
            return this.getElementsWith(db, COLUMN_NAME_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            //Log.v(TAG, "getTopics error");
            Log.e(TAG, "getTopics", e);
            Log.getStackTraceString(e);
            return new ArrayList<>();
        }
    }

    public List<Long> getTopicIDs(SQLiteDatabase db, Recipe recipe) {
        if(recipe == null){
            return new ArrayList<>();
        }
        try {
            //return this.getElementsWith(db, COLUMN_NAME_RECIPE_ID, Long.toString(recipe.getID()));
            if(!getColumns().contains(COLUMN_NAME_RECIPE_ID) || !getColumns().contains(COLUMN_NAME_TOPIC_ID)){
                throw new NoSuchColumnException(getName(), COLUMN_NAME_RECIPE_ID+" oder "+COLUMN_NAME_TOPIC_ID);
            }
            Cursor cursor = db.query(true,
                    this.getName(),
                    new String[]{COLUMN_NAME_TOPIC_ID},
                    COLUMN_NAME_RECIPE_ID+" = "+Long.toString(recipe.getID()),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            return this.cursorToTopicIDs(cursor);
            //this.cursorToList()
        } catch (NoSuchColumnException e) {
            //Log.v(TAG, "getTopics error");
            Log.e(TAG, "getTopics", e);
            Log.getStackTraceString(e);
            return new ArrayList<>();
        }
    }
    private List<Long> cursorToTopicIDs(Cursor cursor) {
        List<Long> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TOPIC_ID)));
            while (cursor.moveToNext()) {
                res.add(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_TOPIC_ID)));
            }
        }
        cursor.close();
        // Log.v(TAG, "cursorToList : "+res);
        return res;
    }


    public List<SQLRecipeTopic> getTopics(SQLiteDatabase readableDatabase, Recipe recipe) {
        try {
            return this.getElementsWith(readableDatabase,
                    COLUMN_NAME_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            return new ArrayList<>();
        }
    }

    public List<SQLRecipeTopic> getElements(SQLiteDatabase db, Recipe recipe, Topic topic) throws NoSuchColumnException {
        if(!getColumns().contains(COLUMN_NAME_RECIPE_ID)){
            throw new NoSuchColumnException(getName(), COLUMN_NAME_RECIPE_ID);
        }
        if(!getColumns().contains(COLUMN_NAME_TOPIC_ID)){
            throw new NoSuchColumnException(getName(), COLUMN_NAME_TOPIC_ID);
        }
        Cursor cursor = db.query(true,
                this.getName(),
                getColumns().toArray(new String[0]),
                COLUMN_NAME_RECIPE_ID+" = "+recipe.getID()+" AND "+COLUMN_NAME_TOPIC_ID+" = "+topic.getID(),
                null,
                null,
                null,
                null,
                null,
                null);
        return this.cursorToList(cursor);
    }
}
