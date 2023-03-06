package com.cocktailmachine.data.db.elements;

import com.cocktailmachine.data.db.NotInitializedDBException;

public abstract class SQLDataBaseElement implements DataBaseElement {
    private long ID = -1L;
    private boolean saved = false;
    private boolean changed = false;

    SQLDataBaseElement(){}

    SQLDataBaseElement(long id){
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

    public void wasSaved(){
        this.saved = true;
        this.changed = false;
    }

    public void wasChanged(){
        this.changed = true;
    }


}
