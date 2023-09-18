package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
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
        this.setIngredientPumps();
    }

    @Override
    public int getMinimumPumpVolume() {
        return this.minimumPumpVolume;
    }

    @Override
    public String getIngredientName() {
        this.setIngredientPumps();

        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient().getName();
        }
        return "Keine Zutat";
    }

    @Override
    public int getVolume() {
        this.setIngredientPumps();
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getVolume();
        }
        Log.i(TAG, "getVolume: no ingredient pump");
        return -1;
    }

    @Override
    public Ingredient getCurrentIngredient() {
        this.setIngredientPumps();
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
    public void setCurrentIngredient(Context context, long id) {
        this.setIngredientPumps();
        if(ingredientPump != null) {
            if (this.ingredientPump.getIngredientID() == id) {
                return;
            }
            this.ingredientPump.delete(context);
        }
        this.ingredientPump = new SQLIngredientPump(-1, this.getID(), id);
        this.ingredientPump.save(context);
        this.wasChanged();
        this.save(context);
    }


    /**
     * set new ingridient pump connection and if existing delete old
     * @param ingredientPump
     */
    @Override
    public void setIngredientPump(Context context, SQLIngredientPump ingredientPump) {
        Log.i(TAG, "setIngredientPump");
        this.setIngredientPumps();
        if(ingredientPump != null){
            Log.i(TAG, "setIngredientPump: delete old: "+this.ingredientPump);
            ingredientPump.delete(context);
        }
        this.ingredientPump = ingredientPump;
        Log.i(TAG, "setIngredientPump: "+ingredientPump.toString());
        this.save(context);
    }

    private void setIngredientPumps() {
        Log.i(TAG, "checkIngredientPumps");
        if(this.ingredientPump == null){
            Log.i(TAG, "checkIngredientPumps: check ingredient pump");

            List<SQLIngredientPump> ips = Buffer.getSingleton().getIngredientPumps();
            for(SQLIngredientPump ip: ips){
                if(ip.getIngredientID()==this.getID()){
                    this.setIngredientPump(ip);
                    Log.i(TAG, "checkIngredientPumps: setted IngredientPump: "+ip);
                    return;
                }
            }
            Log.i(TAG, "checkIngredientPumps: none found");

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
    public void empty(Context context) {
        Log.i(TAG, "empty");
        this.setIngredientPumps();
        if(this.ingredientPump != null){
            Log.i(TAG, "empty: delete old: "+this.ingredientPump);
            this.ingredientPump.delete(context);
        }
        this.ingredientPump = null;
        this.wasChanged();
    }

    @Override
    public void fill(Context context,int volume) throws MissingIngredientPumpException {
        this.setIngredientPumps();
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            this.ingredientPump.save(context);

        }else{
            throw new MissingIngredientPumpException("There is no IngredientPump in Pump: "+this);
        }
        this.wasChanged();
    }

    @Override
    public void setMinimumPumpVolume(Context context, int volume)  {
        this.setMinimumPumpVolume(volume);
        this.save(context);
    }

    /**
     * no pump, check for one
     * delete if connection exists
     * set to null
     */
    @Override
    public void empty() {
        Log.i(TAG, "empty");
        this.setIngredientPumps();
        if(this.ingredientPump != null){
            Log.i(TAG, "empty: delete old: "+this.ingredientPump);
            this.ingredientPump.delete(context);
        }
        this.ingredientPump = null;
        this.wasChanged();
    }

    @Override
    public void fill(int volume) throws MissingIngredientPumpException {
        this.setIngredientPumps();
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            this.ingredientPump.save(context);

        }else{
            throw new MissingIngredientPumpException("There is no IngredientPump in Pump: "+this);
        }
        this.wasChanged();
    }

    @Override
    public void setMinimumPumpVolume( int volume)  {
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
        this.setIngredientPumps();
        boolean res = (this.ingredientPump!=null);
        if(res != this.available){
            Log.i(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
    }

    @Override
    public void save(Context context) {
        Log.i(TAG, "save");

        AddOrUpdateToDB.addOrUpdate(context,this);
        this.setIngredientPumps();
        if(this.ingredientPump != null) {
            this.ingredientPump.setPumpID(this.getID());
            this.ingredientPump.save(context);
        }

    }

    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context, this);
        Log.i(TAG, "delete: successfull deleted"+this.ingredientPump);

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
