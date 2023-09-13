package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;

import java.util.List;
import java.util.Objects;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private static final String TAG = "SQLPump";
    private int minimumPumpVolume = 1;

    private final int slot = -1;
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
    public int getSlot() {
        try {
            return Math.toIntExact(getID());
        }catch (ArithmeticException e){
            Log.e(TAG, "getSlot");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void setCurrentIngredient(long id) {
        this.checkIngredientPumps();
        if(ingredientPump != null) {
            if (this.ingredientPump.getIngredientID() == id) {
                return;
            }
            this.ingredientPump.delete();
        }
        this.ingredientPump = new SQLIngredientPump(-1, this.getID(), id);
        this.ingredientPump.save();
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
            ingredientPump.delete();
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
            this.ingredientPump.delete();
        }
        this.ingredientPump = null;
        this.wasChanged();
    }

    @Override
    public void fill(int volume) throws MissingIngredientPumpException {
        this.checkIngredientPumps();
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            this.ingredientPump.save();

        }else{
            throw new MissingIngredientPumpException("There is no IngredientPump in Pump: "+this);
        }
        this.wasChanged();
    }

    @Override
    public void setMinimumPumpVolume(int volume)  {
        this.minimumPumpVolume = volume;
        this.wasChanged();
    }

    //General


    /**
     * true, if volume > 0
     * @return
     */
    @Override
    public boolean isAvailable() {
        return (this.getVolume() > 0);
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
    public boolean save() {
        Log.i(TAG, "save");
        try {
            DatabaseConnection.getDataBase().addOrUpdate(this);
            this.checkIngredientPumps();
            if(this.ingredientPump != null) {
                this.ingredientPump.setPumpID(this.getID());
                this.ingredientPump.save();
            }
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
            Log.i(TAG, "delete: successfull deleted"+this.ingredientPump);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "delete: failed to delete old: "+this.ingredientPump);
        }
    }

    @Override
    public int compareTo(Pump o) {
        return Long.compare(this.getID(), o.getID());
    }

    @NonNull
    @Override
    public String toString() {
        return "SQLPump{" +
                "ID=" + getID() +
                "slot=" + getSlot() +
                ", minimumPumpVolume=" + minimumPumpVolume +
                ", ingredientPump=" + ingredientPump +
                '}';
    }
}
