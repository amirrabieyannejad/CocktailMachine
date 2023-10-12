package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
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

    public List<SQLRecipeTopic> getTopics(SQLiteDatabase db, SQLRecipe recipe) {
        try {
            return this.getElementsWith(db, COLUMN_NAME_RECIPE_ID, Long.toString(recipe.getID()));
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
