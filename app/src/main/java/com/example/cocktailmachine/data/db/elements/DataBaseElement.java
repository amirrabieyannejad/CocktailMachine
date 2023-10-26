package com.example.cocktailmachine.data.db.elements;

import android.content.Context;

import java.util.Objects;

public interface DataBaseElement {

    public String getClassName();
    public default boolean areContentsTheSame(DataBaseElement element){
        if(element != null){
            if(Objects.equals(element.getClassName(), this.getClassName())) {
                if (this.isSaved() || element.isSaved()) {
                    return element.getID() == this.getID();
                }
            }
        }
        return false;
    }

    long getID();

    void setID(long id);

    boolean isAvailable();

    boolean loadAvailable(Context context);

    boolean isSaved();

    boolean needsUpdate();

    void wasSaved();

    void wasChanged();

    void save(Context context);

    void delete(Context context);

}
