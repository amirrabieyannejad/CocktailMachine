package com.example.cocktailmachine.data;

import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLTopic;

import java.util.ArrayList;
import java.util.List;

public interface Topic extends Comparable<Topic>, DataBaseElement {
    String TAG = "Topic";

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
        return Buffer.getSingleton().getTopic(id);
    }

    static Topic getTopic(String name)  {
        Log.i(TAG, "getTopic");
        return Buffer.getSingleton().getTopic(name);
    }

    static List<Topic> getTopics(Recipe recipe)  {
        Log.i(TAG, "getTopics");
        return Buffer.getSingleton().getTopics(recipe);
    }

    static List<Long> getTopicIDs(Recipe recipe)  {
        Log.i(TAG, "getTopics");
        return Buffer.getSingleton().getTopicIDs(recipe);
    }

    static List<Topic> getTopics(){
        Log.i(TAG, "getTopics");
        return Buffer.getSingleton().getTopics();
    }

    static List<Topic> getTopics(Context context){
        Log.i(TAG, "getTopics");
        return Buffer.getSingleton().getTopics(context);
    }

    static List<String> getTopicTitles(){
       return Buffer.getSingleton().getTopicNames();
    }

}
