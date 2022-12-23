package com.example.cocktailmachine.data.db.tables;

import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_ID;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_LONG;
import static com.example.cocktailmachine.data.db.tables.Tables.TYPE_TEXT;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cocktailmachine.data.db.elements.ImageUrlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ImageUrlTable extends BasicColumn<ImageUrlElement>{
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
    public ImageUrlElement makeElement(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
        long iid = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_OWNER_ID));
        String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_URL));
        return this.initializeElement(id,url, iid);
    }

    @Override
    public ContentValues makeContentValues(ImageUrlElement element) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_OWNER_ID, element.getOwnerID());
        cv.put(COLUMN_NAME_URL, element.getUrl());
        return cv;
    }

    public List<String> getUrls(SQLiteDatabase db, long id) {
        List<ImageUrlElement> urltable = null;
        try {
            urltable = this.getElementsWith(db, COLUMN_NAME_OWNER_ID, Long.toString(id));

        } catch (NoSuchColumnException e) {
            e.printStackTrace();
        }
        return urltable != null ? urltable.stream().map(ImageUrlElement::getUrl).collect(Collectors.toList()) : null;
    }

    public void addElement(SQLiteDatabase db, Long owner_id, String url){
        this.addElement(db, this.initializeElement(url, owner_id));
    }

    public abstract ImageUrlElement initializeElement(String url, long owner_id);
    public abstract ImageUrlElement initializeElement(long id, String url, long owner_id);
}
