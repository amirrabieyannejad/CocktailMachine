package com.example.cocktailmachine.data;

import com.example.cocktailmachine.data.db.elements.SQLTopic;

public interface Topic {
    public long getID();
    public String getName();
    public String getDescription();
    public void setName(String name);
    public void setDescription(String description);

    /**
     * Deletes this instance in db and in buffer.
     */
    public void delete();

    /**
     * Saves to db.
     */
    public void save();

    public static Topic makeNew(String name, String description){
        return new SQLTopic(name, description);
    }
}
