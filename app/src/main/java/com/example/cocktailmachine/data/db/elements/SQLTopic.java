package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class SQLTopic extends DataBaseElement implements Topic {
    private String name;
    private String description;

    public SQLTopic(long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public SQLTopic(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    @Override
    public long getID(){
        return super.getID();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        this.wasChanged();
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        this.wasChanged();
    }


    @Override
    public void save() {
        NewDatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() {
        NewDatabaseConnection.getDataBase().remove(this);
    }
}
