package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_TEXT;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;

import com.example.cocktailmachine.data.db.elements.SQLTopic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:52
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public class TopicTable extends BasicColumn<SQLTopic>{
    public static final String TABLE_NAME = "Topic";
    public static final String COLUMN_NAME_NAME = "Name";
    public static final String COLUMN_NAME_DESCRIPTION = "Description";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_NAME = TYPE_TEXT;
    public static final String COLUMN_TYPE_DESCRIPTION = TYPE_TEXT;
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(this._ID);
        columns.add(COLUMN_NAME_NAME);
        columns.add(COLUMN_NAME_DESCRIPTION);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_NAME);
        columns.add(COLUMN_TYPE_DESCRIPTION);
        return columns;
    }

    @Override
    public SQLTopic makeElement(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DESCRIPTION));
        return new SQLTopic(id, name, desc);
    }

    @Override
    public ContentValues makeContentValues(SQLTopic element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_NAME, element.getName());
        cv.put(COLUMN_NAME_DESCRIPTION, element.getDescription());
        return cv;
    }
}
