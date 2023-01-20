package com.example.cocktailmachine.data.db.elements;

public abstract class ImageUrlElement extends SQLDataBaseElement {
    private String url = "";
    private final long ownerID;

    public ImageUrlElement(long ID, String url, long ingredientID) {
        super();
        this.setID(ID);
        this.url = url;
        this.ownerID = ingredientID;
    }

    public ImageUrlElement(String url, long ownerID) {
        super();
        this.url = url;
        this.ownerID = ownerID;
    }

    public String getUrl(){
        return this.url;
    }

    public long getOwnerID(){
        return this.ownerID;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
