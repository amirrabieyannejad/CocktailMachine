package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

public interface DataBaseElement {
    long getID();

    void setID(long id);

    boolean isAvailable();

    boolean loadAvailable();

    boolean isSaved();

    boolean needsUpdate();

    void wasSaved();

    void wasChanged();

    void save() throws NotInitializedDBException;

    void delete() throws NotInitializedDBException;
}
