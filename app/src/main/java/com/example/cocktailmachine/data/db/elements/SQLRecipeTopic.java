package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class SQLRecipeTopic extends DataBaseElement {
    private long recipeID;
    private long topicID;

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

    @Override
    void save() {
        NewDatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    void delete() {
        NewDatabaseConnection.getDataBase().remove(this);
    }
}
