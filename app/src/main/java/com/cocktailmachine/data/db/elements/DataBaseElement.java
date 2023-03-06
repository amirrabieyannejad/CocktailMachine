package com.cocktailmachine.data.db.elements;

import com.cocktailmachine.data.db.NotInitializedDBException;

public interface DataBaseElement {
    public long getID();

    public void setID(long id);

    public boolean isAvailable();

    public boolean isSaved();

    public boolean needsUpdate();

    void wasSaved();

    void wasChanged();

    public abstract void save() throws NotInitializedDBException;

    public abstract void delete() throws NotInitializedDBException;
}
