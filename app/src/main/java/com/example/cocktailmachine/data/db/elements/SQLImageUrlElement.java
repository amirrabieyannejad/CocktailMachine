package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import java.io.File;

public abstract class SQLImageUrlElement extends SQLDataBaseElement {
    private String url = "";
    private final long ownerID;
    private static final String TAG="SQLImageUrlElement";
    private boolean available;

    public SQLImageUrlElement(long ID, String url, long ingredientID) {
        super();
        this.setID(ID);
        this.url = url;
        this.ownerID = ingredientID;
        this.available = false;
    }

    public SQLImageUrlElement(String url, long ownerID) {
        super();
        this.url = url;
        this.ownerID = ownerID;
    }

    public String getUrl(){
        Log.i(TAG, "getUrl");
        return this.url;
    }

    public long getOwnerID(){
        Log.i(TAG, "getOwnerID");
        return this.ownerID;
    }

    /**
     * true, if file with getUrl as path exists
     * @return
     */
    @Override
    public boolean isAvailable() {
        Log.i(TAG, "isAvailable");
        return this.available;
    }

    /**
     * true, if file with getUrl as path exists
     * @return
     */
    @Override
    public boolean loadAvailable(Context context) {
        Log.i(TAG, "loadAvailable");
        File f = new File(getUrl());
        this.available = f.exists() && !f.isDirectory();
        return isAvailable();
    }
}
