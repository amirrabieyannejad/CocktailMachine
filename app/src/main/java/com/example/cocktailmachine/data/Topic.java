package com.example.cocktailmachine.data;

import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLTopic;

import java.util.List;
import java.util.Objects;

public interface Topic extends Comparable<Topic>, DataBaseElement {
    String TAG = "Topic";

    static Topic searchOrNew(Context context, String name, String ex) {
        Topic t = Topic.getTopic(context,name);
        if(t == null){
            t = Topic.makeNew(name, ex);
            t.save(context);
        }
        if(!Objects.equals(t.getDescription(), ex)) {
            t.setName(ex);
            t.save(context);
        }
        return t;
    }

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

    static Topic getTopic(Context context, long id)  {
        Log.i(TAG, "getTopic");
        return GetFromDB.getTopic(context, id);
    }

    static Topic getTopic(Context context, String name)  {
        Log.i(TAG, "getTopic");
        return GetFromDB.getTopic(context, name);
    }

    public static List<Topic> getTopics(Context context,Recipe recipe)  {
        Log.i(TAG, "getTopics");
        return (List<Topic>) GetFromDB.getTopics(context, recipe);
    }

    static List<Long> getTopicIDs(Context context,Recipe recipe)  {
        Log.i(TAG, "getTopics");
        return GetFromDB.getTopicIDs(context, recipe);
    }



    static List<Topic> getTopics(Context context){
        Log.i(TAG, "getTopics");
        return (List<Topic>) GetFromDB.getTopics(context);
    }

    static List<String> getTopicTitles(Context context){
       return GetFromDB.loadTopicTitles(context);
    }

}
