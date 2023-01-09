package com.example.cocktailmachine.data;

import com.example.cocktailmachine.data.db.elements.SQLTopic;

public interface Topic {
    /**
     * Get id.
     * @return id
     */
    public long getID();

    /**
     * Get name.
     * @return name
     */
    public String getName();

    /**
     * Get description.
     * @return description
     */
    public String getDescription();

    //setter
    /**
     * Set name.
     * @param name name
     */
    public void setName(String name);

    /**
     * Set description.
     * @param description description
     */
    public void setDescription(String description);

    /**
     * Deletes this instance in db and in buffer.
     */
    public void delete();

    /**
     * Saves to db.
     */
    public void save();

    /**
     * Make new topic instance with already given name and description.
     * It will be instantly be saved to the database.
     * @param name name
     * @param description description
     * @return new saved instance
     */
    public static Topic makeNew(String name, String description){
        return new SQLTopic(name, description);
    }
}
