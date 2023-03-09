package com.example.cocktailmachine.data.db.elements;

public abstract class DataBaseElement {
    private long ID;
    private boolean saved = false;
    private boolean changed = false;

    DataBaseElement(){}
    DataBaseElement(long id){
        this.ID = id;
        this.wasSaved();
    }

    public long getID(){
        return this.ID;
    }
    public void setID(long id){
        this.ID = id;
    }

    public boolean isSaved(){
        return this.saved;
    }

    public boolean needsUpdate(){
        return this.changed;
    }

    void wasSaved(){
        this.saved = true;
        this.changed = false;
    }

    void wasChanged(){
        this.changed = true;
    }

    abstract void save();
    abstract void delete();
}
