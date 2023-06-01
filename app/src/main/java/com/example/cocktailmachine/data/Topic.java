package com.example.cocktailmachine.data;

import android.util.Log;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLTopic;

import java.util.ArrayList;
import java.util.List;

public interface Topic extends Comparable<Topic>, DataBaseElement {
    static final String TAG = "Topic";

    /**
     * Get id.
     * @return id
     */
    long getID();

    /**
     * Get name.
     * @return name
     */
    String getName();

    /**
     * Get description.
     * @return description
     */
    String getDescription();

    //setter
    /**
     * Set name.
     * @param name name
     */
    void setName(String name);

    /**
     * Set description.
     * @param description description
     */
    void setDescription(String description);


    /**
     * Make new topic instance with already given name and description.
     * It will be instantly be saved to the database.
     * @param name name
     * @param description description
     * @return new saved instance
     */
    static Topic makeNew(String name, String description){
        Log.i(TAG, "makeNew");
        return new SQLTopic(name, description);
    }

    static Topic getTopic(long id)  {
        Log.i(TAG, "getTopic");
        try {
            return DatabaseConnection.getDataBase().getTopic(id);
        } catch (NotInitializedDBException e) {
            Log.i(TAG, "getTopic: NotInitializedDBException");
            e.printStackTrace();
            return null;
        }
    }

    static Topic getTopic(String name)  {
        Log.i(TAG, "getTopic");
        try {
            return DatabaseConnection.getDataBase().getTopicWith(name);
        } catch (NotInitializedDBException e) {
            Log.i(TAG, "getTopic: NotInitializedDBException");
            e.printStackTrace();
            return Topic.makeNew(name, "Füll bitte bei Gelegenheit aus!");
        }
    }

    static List<Topic> getTopics(Recipe recipe)  {
        Log.i(TAG, "getTopics");
        try {
            return DatabaseConnection.getDataBase().getTopics(recipe);
        } catch (NotInitializedDBException e) {
            Log.i(TAG, "getTopics: NotInitializedDBException");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Topic> getTopics(){
        Log.i(TAG, "getTopics");
        try {
            return DatabaseConnection.getDataBase().getTopics();
        } catch (NotInitializedDBException e) {
            Log.i(TAG, "getTopics: NotInitializedDBException");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
