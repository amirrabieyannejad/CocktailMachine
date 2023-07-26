package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

public class SQLRecipeTopic extends SQLDataBaseElement {
    private static final String TAG = "SQLRecipeTopic";
    private long recipeID = -1;
    private long topicID = -1;
    private boolean available = false;

    public SQLRecipeTopic(long recipeID, long topicID) {
        super();
        this.recipeID = recipeID;
        this.topicID = topicID;
    }

    public SQLRecipeTopic(long id, long recipeID, long topicID) {
        super(id);
        this.recipeID = recipeID;
        this.topicID = topicID;
    }

    public long getRecipeID() {
        return recipeID;
    }

    public long getTopicID() {
        return topicID;
    }

    public Recipe getRecipe() {
        return Recipe.getRecipe(recipeID);
    }

    public Topic getTopic() {
        return Topic.getTopic(topicID);
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * true, if topic and recipe exists
     * @return
     */
    @Override
    public boolean loadAvailable() {
        Log.i(TAG, "loadAvailable");
        boolean res = (this.getTopic()!=null)&&(this.getRecipe()!=null);
        if(res != this.available){
            Log.i(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
    }

    @Override
    public boolean save() {
        try {
            DatabaseConnection.getDataBase().addOrUpdate(this);
            this.wasSaved();
            return true;
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void delete() {
        Log.i(TAG, "delete");
        try {
            DatabaseConnection.getDataBase().remove(this);
            Log.i(TAG, "delete: successful deleted");
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "delete: failed");
        }
    }

    @Override
    public String toString() {
        return "SQLRecipeTopic{" +
                "recipeID=" + recipeID +
                ", topicID=" + topicID +
                '}';
    }
}
