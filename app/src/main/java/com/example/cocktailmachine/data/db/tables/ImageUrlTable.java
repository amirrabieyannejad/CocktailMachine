package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_TEXT;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.example.cocktailmachine.data.db.exceptions.NoSuchColumnException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @created Fr. 23.Jun 2023 - 12:50
 * @project CocktailMachine
 * @author Johanna Reidt
 */
public abstract class ImageUrlTable extends BasicColumn<SQLImageUrlElement>{
    public String TABLE_NAME;
    public static final String COLUMN_NAME_URL = "URL";
    public static final String COLUMN_NAME_OWNER_ID = "OwnerID";

    public static final String COLUMN_TYPE_ID = TYPE_ID;
    public static final String COLUMN_TYPE_URL = TYPE_TEXT;
    public static final String COLUMN_TYPE_OWNER_ID = TYPE_LONG;

    ImageUrlTable(){
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
        columns.add(COLUMN_NAME_URL);
        columns.add(COLUMN_NAME_OWNER_ID);
        return columns;
    }

    @Override
    public List<String> getColumnTypes() {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_TYPE_ID);
        columns.add(COLUMN_TYPE_URL);
        columns.add(COLUMN_TYPE_OWNER_ID);
        return columns;
    }


    @Override
    public SQLImageUrlElement makeElement(Cursor cursor) {
        try {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
            long iid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_OWNER_ID));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_URL));
            return this.initializeElement(id, url, iid);
        }catch (CursorIndexOutOfBoundsException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ContentValues makeContentValues(SQLImageUrlElement element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_OWNER_ID, element.getOwnerID());
        cv.put(COLUMN_NAME_URL, element.getUrl());
        return cv;
    }

    public void deleteWithOwnerId(SQLiteDatabase db, long ownerID){
        try {
            this.deleteElementsWith(db, COLUMN_NAME_OWNER_ID, String.valueOf(ownerID));
        } catch (NoSuchColumnException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUrls(SQLiteDatabase db, long id) {
        try {
            List<SQLImageUrlElement> urltable = this.getElementsWith(db, COLUMN_NAME_OWNER_ID, Long.toString(id));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return urltable != null ? urltable.stream().map(imageUrlElement ->
                    {
                        if(imageUrlElement==null){
                            return "";
                        }
                        return imageUrlElement.getUrl();
                    }
                    ).collect(Collectors.toList()) : null;
            }
            return urltable != null ? Helper.getUrls(urltable) : null;

        } catch (NoSuchColumnException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<? extends SQLImageUrlElement> getElements(SQLiteDatabase db, long id) {
        try {
            return this.getElementsWith(db, COLUMN_NAME_OWNER_ID, Long.toString(id));

        } catch (NoSuchColumnException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addElement(SQLiteDatabase db, Long owner_id, String url){
        this.addElement(db, this.initializeElement(url, owner_id));
    }

    public abstract SQLImageUrlElement initializeElement(String url, long owner_id);

    public abstract SQLImageUrlElement initializeElement(long id, String url, long owner_id);
}
