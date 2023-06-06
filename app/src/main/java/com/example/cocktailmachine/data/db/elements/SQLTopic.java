package com.example.cocktailmachine.data.db.elements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import org.json.JSONException;
import org.json.JSONObject;

public class SQLTopic extends SQLDataBaseElement implements Topic {
    private String name = "";
    private String description = "";

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

    /*
    @Override
    public long getID(){
        return super.getID();
    }

     */

    /**
     * always true
     * @return
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * always true
     * @return
     */
    @Override
    public boolean loadAvailable() {
        return true;
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
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException{
        DatabaseConnection.getDataBase().remove(this);
    }

    //Comparable

    public JSONObject asJson(){
        JSONObject json = new JSONObject();
        try{
            json.put("name", this.name);
            json.put("description", this.description);
            return json;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Topic){
            return this.compareTo((Topic) obj)==0;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return this.asJson().toString();
    }

    @Override
    public int compareTo(Topic o) {
        return Long.compare(this.getID(), o.getID());
    }
}
