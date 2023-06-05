package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private static final String TAG = "SQLPump";
    private int minimumPumpVolume = 1;
    private SQLIngredientPump ingredientPump = null;

    public SQLPump(){
        super();
    }

    public SQLPump(long ID, int minimumPumpVolume) {
        super(ID);
        this.wasSaved();
        this.minimumPumpVolume = minimumPumpVolume;
        try {
            this.checkIngredientPumps();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getMinimumPumpVolume() {
        return this.minimumPumpVolume;
    }

    @Override
    public String getIngredientName() {
        if(this.ingredientPump == null){
            Log.i(TAG, "getIngredientName: check ingredient pump");
            try {
                this.checkIngredientPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient().getName();
        }
        return "Keine Zutat";
    }

    @Override
    public int getVolume() {
        if(this.ingredientPump == null){
            Log.i(TAG, "getVolume: check ingredient pump");
            try {
                this.checkIngredientPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getVolume();
        }
        Log.i(TAG, "getVolume: no ingredient pump");
        return -1;
    }

    @Override
    public Ingredient getCurrentIngredient() {
        if(this.ingredientPump == null){
            Log.i(TAG, "getCurrentIngredient: check ingredient pump");
            try {
                this.checkIngredientPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient();
        }
        return null;
    }

    @Override
    public void setCurrentIngredient(long id) {
        if(ingredientPump != null) {
            try {
                if (this.ingredientPump.getIngredientID() == id) {
                    return;
                }
                this.ingredientPump.delete();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        this.ingredientPump = new SQLIngredientPump(-1, this.getID(), id);
        try {
            this.ingredientPump.save();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }
        this.wasChanged();
    }

    @Override
    public void setIngredientPump(SQLIngredientPump ingredientPump) {
        this.ingredientPump = ingredientPump;
        Log.i(TAG, "setIngredientPump: "+ingredientPump.toString());
    }

    private void checkIngredientPumps() throws NotInitializedDBException {
        Log.i(TAG, "checkIngredientPumps");
        List<SQLIngredientPump> ips = DatabaseConnection.getDataBase().getIngredientPumps();
        for(SQLIngredientPump ip: ips){
            if(ip.getIngredientID()==this.getID()){
                this.setIngredientPump(ip);
            }
        }
    }

    @Override
    public void empty() {
        this.ingredientPump = null;
        this.wasChanged();
    }

    @Override
    public void fill(int volume) {
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            try {
                this.ingredientPump.save();
            } catch (NotInitializedDBException e) {
                throw new RuntimeException(e);
            }
        }
        this.wasChanged();
    }

    //General



    @Override
    public boolean isAvailable() {
        return (this.getVolume()-this.minimumPumpVolume)>0;
    }

    @Override
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.ingredientPump.setPumpID(this.getID());
        this.ingredientPump.save();
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
    }

    @Override
    public int compareTo(Pump o) {
        return Long.compare(this.getID(), o.getID());
    }

    @Override
    public String toString() {
        return "SQLPump{" +
                "minimumPumpVolume=" + minimumPumpVolume +
                ", ingredientPump=" + ingredientPump +
                '}';
    }
}
