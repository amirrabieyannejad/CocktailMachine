package com.cocktailmachine.data;

import com.cocktailmachine.data.db.DatabaseConnection;
import com.cocktailmachine.data.db.NotInitializedDBException;
import com.cocktailmachine.data.db.elements.DataBaseElement;
import com.cocktailmachine.data.db.elements.SQLTopic;

import java.util.List;

public interface Topic extends Comparable<Topic>, DataBaseElement {
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
     * Make new topic instance with already given name and description.
     * It will be instantly be saved to the database.
     * @param name name
     * @param description description
     * @return new saved instance
     */
    public static Topic makeNew(String name, String description){
        return new SQLTopic(name, description);
    }

    public static Topic getTopic(long id) throws NotInitializedDBException {
        return DatabaseConnection.getDataBase().getTopic(id);
    }

    public static List<Topic> getTopics(Recipe recipe) throws NotInitializedDBException {
        return DatabaseConnection.getDataBase().getTopics(recipe);
    }

    public static List<Topic> getTopics() throws NotInitializedDBException {
        return DatabaseConnection.getDataBase().getTopics();
    }
}
