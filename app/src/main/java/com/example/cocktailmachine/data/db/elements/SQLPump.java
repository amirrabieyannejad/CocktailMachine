package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;

import java.util.List;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private static final String TAG = "SQLPump";
    private int minimumPumpVolume = 1;
    private SQLIngredientPump ingredientPump = null;
    private boolean available = false;

    public SQLPump(){
        super();
    }

    public SQLPump(long ID, int minimumPumpVolume) {
        super(ID);
        this.wasSaved();
        this.minimumPumpVolume = minimumPumpVolume;
        this.checkIngredientPumps();
    }

    @Override
    public int getMinimumPumpVolume() {
        return this.minimumPumpVolume;
    }

    @Override
    public String getIngredientName() {
        this.checkIngredientPumps();

        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient().getName();
        }
        return "Keine Zutat";
    }

    @Override
    public int getVolume() {
        this.checkIngredientPumps();
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getVolume();
        }
        Log.i(TAG, "getVolume: no ingredient pump");
        return -1;
    }

    @Override
    public Ingredient getCurrentIngredient() {
        this.checkIngredientPumps();
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient();
        }
        return null;
    }

    @Override
    public void setCurrentIngredient(long id) {
        this.checkIngredientPumps();
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

    /**
     * set new ingridient pump connection and if existing delete old
     * @param ingredientPump
     */
    @Override
    public void setIngredientPump(SQLIngredientPump ingredientPump) {
        Log.i(TAG, "setIngredientPump");
        this.checkIngredientPumps();
        if(ingredientPump != null){
            Log.i(TAG, "setIngredientPump: delete old: "+this.ingredientPump);
            try {
                ingredientPump.delete();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.i(TAG, "setIngredientPump: failed to delete old: "+this.ingredientPump);
            }
        }
        this.ingredientPump = ingredientPump;
        Log.i(TAG, "setIngredientPump: "+ingredientPump.toString());
    }

    private void checkIngredientPumps() {
        Log.i(TAG, "checkIngredientPumps");
        if(this.ingredientPump == null){
            Log.i(TAG, "checkIngredientPumps: check ingredient pump");
            try {
                List<SQLIngredientPump> ips = DatabaseConnection.getDataBase().getIngredientPumps();
                for(SQLIngredientPump ip: ips){
                    if(ip.getIngredientID()==this.getID()){
                        this.setIngredientPump(ip);
                        Log.i(TAG, "checkIngredientPumps: setted IngredientPump: "+ip);
                        return;
                    }
                }
                Log.i(TAG, "checkIngredientPumps: none found");
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        /*else {
            Log.i(TAG, "checkIngredientPumps: already set: "+this.ingredientPump);
        }

         */
    }

    /**
     * no pump, check for one
     * delete if connection exists
     * set to null
     */
    @Override
    public void empty() {
        Log.i(TAG, "empty");
        this.checkIngredientPumps();
        if(this.ingredientPump != null){
            Log.i(TAG, "empty: delete old: "+this.ingredientPump);
            try {
                this.ingredientPump.delete();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.i(TAG, "empty: failed to delete old: "+this.ingredientPump);
            }
        }
        this.ingredientPump = null;
        this.wasChanged();
    }

    @Override
    public void fill(int volume) throws MissingIngredientPumpException {
        this.checkIngredientPumps();
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            try {
                this.ingredientPump.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }else{
            throw new MissingIngredientPumpException("There is no IngredientPump in Pump: "+this);
        }
        this.wasChanged();
    }

    //General


    /**
     * true, if ingredient pump connection exists and  volume > 0
     * @return
     */
    @Override
    public boolean isAvailable() {
        return this.available && (this.getVolume() > 0);
    }

    /**
     * check for ingredient pump connection
     * true, if ingredient pump connection exists
     *
     * @return
     */
    @Override
    public boolean loadAvailable() {
        Log.i(TAG, "loadAvailable");
        this.checkIngredientPumps();
        boolean res = (this.ingredientPump!=null);
        if(res != this.available){
            Log.i(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
    }

    @Override
    public void save() throws NotInitializedDBException {
        Log.i(TAG, "save");
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.checkIngredientPumps();
        if(this.ingredientPump != null) {
            this.ingredientPump.setPumpID(this.getID());
            this.ingredientPump.save();
        }
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        Log.i(TAG, "delete");
        DatabaseConnection.getDataBase().remove(this);
    }

    @Override
    public int compareTo(Pump o) {
        return Long.compare(this.getID(), o.getID());
    }

    @Override
    public String toString() {
        return "SQLPump{" +
                "ID=" + getID() +
                ", minimumPumpVolume=" + minimumPumpVolume +
                ", ingredientPump=" + ingredientPump +
                '}';
    }
}
