package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
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
    public String getClassName() {
        return "SQLRecipeTopic";
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public boolean loadAvailable(Context context) {
        loadAvailable();
        this.save(context);
        return this.available;
    }

    /**
     * true, if topic and recipe exists
     * @return
     */
    public boolean loadAvailable() {
       // Log.v(TAG, "loadAvailable");
        boolean res = (this.getTopic()!=null)&&(this.getRecipe()!=null);
        if(res != this.available){
           // Log.v(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
    }



    @Override
    public void save(Context context) {
        AddOrUpdateToDB.addOrUpdate(context, this);
    }

    @Override
    public void delete(Context context) {
       // Log.v(TAG, "delete");
        DeleteFromDB.remove(context,this);

    }

    @Override
    public String toString() {
        return "SQLRecipeTopic{" +
                "recipeID=" + recipeID +
                ", topicID=" + topicID +
                '}';
    }
}
