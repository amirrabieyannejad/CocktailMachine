package com.cocktailmachine.data.db.elements;

import com.cocktailmachine.data.db.DatabaseConnection;
import com.cocktailmachine.data.db.NotInitializedDBException;

public class SQLRecipeTopic extends SQLDataBaseElement {
    private long recipeID = -1;
    private long topicID = -1;

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
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
    }
}
