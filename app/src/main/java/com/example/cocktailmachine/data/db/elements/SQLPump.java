package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;

import java.util.LinkedList;
import java.util.List;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private static final String TAG = "SQLPump";

    private int slot = -1;
    private SQLIngredientPump ingredientPump = null;
    private boolean available = false;

    private long ingredientID = -1L;


    //private loaded
    private String loadedIngredientName = "Keine Zutat";
    private int loadedIngredientVolume = 0;

    public SQLPump(){
        super();
    }

    public SQLPump(long ID, int slot_id) {
        super(ID);
        this.wasSaved();
        this.slot = slot_id;
        //this.setIngredientPumps();
    }
    public SQLPump(long ID, int slot_id, long ingredientID) {
        super(ID);
        this.wasSaved();
        this.slot = slot_id;
        this.ingredientID = ingredientID;
        //this.setIngredientPumps();
    }

    /**
     * get ingredient name
     * or "Keine Zutat"
     * @author Johanna Reidt
     * @param context
     * @return
     */
    @Override
    public String getIngredientName(Context context) {
        //loadIngredientPump(context);
        return this.loadedIngredientName;

        //return "Keine Zutat";
    }

    /*
    private void //loadIngredientPump(Context context){
        this.ingredientPump = GetFromDB.getIngredientPump(context, this);//Buffer.getSingleton(context).getIngredientPump(this);
        if(this.ingredientPump!=null) {
            Ingredient temp = this.ingredientPump.getIngredient(context);
            if(temp!=null){
                this.ingredientID = temp.getID();
                this.loadedIngredientName = temp.getName();
                this.loadedIngredientVolume = this.ingredientPump.getVolume();
            }
        }
    }

     */

    /**
     * get current volume
     * or -1
     * @author Johanna Reidt
     * @return
     */

    @Override
    public int getVolume() {
        //this.setIngredientPumps();

        if(this.ingredientPump!=null) {
            return this.ingredientPump.getVolume();
        }
       // Log.v(TAG, "getVolume: no ingredient pump");
        return -1;
    }

    @Override
    public void deleteCurrentIngredient(Context context) {
        this.ingredientPump.delete(context);
        this.ingredientID = -1L;
        this.loadedIngredientVolume = -1;
        this.loadedIngredientName = "Keine Zutat";
        this.save(context);

    }

    @Override
    public void pump(int volume) throws NewlyEmptyIngredientException, MissingIngredientPumpException {

    }

    @Override
    public String getIngredientName() {
        return this.loadedIngredientName;
    }


    /**
     * get current ingredient
     * or null
     * @author Johanna Reidt
     * @return
     */
    @Override
    public Ingredient getCurrentIngredient(Context context) {
        //this.setIngredientPumps();
        if(this.ingredientPump!=null) {
            return this.ingredientPump.getIngredient(context);
        }
        return null;
    }


    /**
     * translaste id to slot which is equal
     * @author Johanna Reidt
     * @return
     */
    @Override
    public int getSlot() {
        return this.slot;
    }

    /**
     * sets current ingredient
     * if current ingredient already equals given id => do nothing
     * else delete if exists current ingredient
     * add ingredient with vol -1
     * save pump and ingredientpump
     * @author Johanna Reidt
     * @param context
     * @param id id of next ingredient
     */
    @Override
    public void setCurrentIngredient(Context context, long id) {
        this.setIngredientPumps(context);
        if(this.ingredientPump != null) {
            this.ingredientPump.delete(context);
        }
        SQLIngredientPump ip = new SQLIngredientPump(10, this.getID(), id);
        ip.save(context);
        this.setIngredientPump(context, ip);
        this.wasChanged();
        this.save(context);
    }

    @Override
    public void preSetIngredient(long id) {
        this.ingredientID = id;
        this.ingredientPump = new SQLIngredientPump(-1, this.getID(),id);
        //TODO this.ingredientPump = GetFromDB.getIngredientPumps(context);
        this.wasChanged();
    }

    public long preGetIngredientID(){
        return this.ingredientID;
    }

    /**
     * set new ingredient pump connection and if existing delete old
     * @param ingredientPump
     */
    //@Override
    public void setIngredientPump(Context context, SQLIngredientPump ingredientPump) {
        Log.v(TAG, "setIngredientPump");
        Log.v(TAG, "setIngredientPump: "+ingredientPump);
        //this.setIngredientPumps(context);
        if(this.ingredientPump != null){
            Log.v(TAG, "setIngredientPump: delete old: "+this.ingredientPump);
            this.ingredientPump.delete(context);
        }
        //ExtraHandlingDB.deleteDoublePumpSettingsAndNulls(context);
        this.ingredientPump = ingredientPump;
        this.ingredientPump.setPumpID(this.getID());
        this.ingredientPump.save(context);
        Log.v(TAG, "setIngredientPump: after save "+this.ingredientPump);
        this.save(context);
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }


    /**
     * checks for ingredientPump if missing in loaded buffer
     * @author Johanna Reidt
     * @param context
     */
    private void setIngredientPumps(Context context) {
       // Log.v(TAG, "setIngredientPumps");
        //if(this.ingredientPump == null){
        // Log.v(TAG, "setIngredientPumps: check ingredient pump");
        List<SQLIngredientPump> ips = new LinkedList<>();//GetFromDB.getIngredientPumps(context);
        for(SQLIngredientPump ip: ips){
            if(ip.getPumpID()==this.getID()){
                this.setIngredientPump(context, ip);
                // Log.v(TAG, "setIngredientPumps: setted IngredientPump: "+ip);
                return;
            }
        }
           // Log.v(TAG, "setIngredientPumps: none found");
        //}
        /*else {
           // Log.v(TAG, "checkIngredientPumps: already set: "+this.ingredientPump);
        }

         */
    }

    @Override
    public long getIngredientID() {
        return this.ingredientID;
    }

    /**
     * no pump, check for one
     * delete if connection exists
     * set to null
     */
    @Override
    public void empty(Context context) {
       // Log.v(TAG, "empty");
        this.setIngredientPumps(context);
        if(this.ingredientPump != null){
           // Log.v(TAG, "empty: delete old: "+this.ingredientPump);
            this.ingredientPump.delete(context);
        }
        this.ingredientPump = null;
        this.wasChanged();
    }

    /**
     * checks for ingredientPump
     * sets volume
     * saves
     * throws missing ingredient if none set
     * @author Johanna Reidt
     * @param context
     * @param volume
     * @throws MissingIngredientPumpException
     */
    @Override
    public void fill(Context context, int volume) throws MissingIngredientPumpException {
        Log.v(TAG, "fill");
        //this.setIngredientPumps(context);
        if(this.ingredientPump == null){
            this.setIngredientPumps(context);
        }
        if(this.ingredientPump!=null) {
            this.ingredientPump.setVolume(volume);
            Log.v(TAG, "fill: setVolume");
            this.ingredientPump.save(context);
            Log.v(TAG, "fill: save");
        }else{
            throw new MissingIngredientPumpException("There is no IngredientPump in Pump: "+this);
        }
        this.wasChanged();
    }



    //General


    @Override
    public String getClassName() {
        return "SQLPump";
    }

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
    public boolean loadAvailable(Context context) {
       // Log.v(TAG, "loadAvailable");
        this.setIngredientPumps(context);
        boolean res = (this.ingredientPump!=null);
        if(res != this.available){
           // Log.v(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
            this.save(context);
        }
        return this.available;
    }

    public boolean loadAvailable() {
        return false;
    }

    @Override
    public void save(Context context) {

        Log.v(TAG, "save: "+this);
        Log.v(TAG, "save: "+this.ingredientPump);
        //AddOrUpdateToDB.addOrUpdate(context,this);
        //this.setIngredientPumps(context);
        if(this.ingredientPump != null) {
            this.ingredientPump.setPumpID(this.getID());
            this.ingredientPump.save(context);
        }else{
           Log.v(TAG, "save: no ingredient pump");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.w(TAG, "save: ListOfPumps: ");
            Pump.getPumps(context).iterator().forEachRemaining(p->Log.w(TAG, "pump: "+p));
            Log.w(TAG, "save: ingpumps: ");
            //GetFromDB.getIngredientPumps(context).iterator().forEachRemaining(p->Log.w(TAG, "ip: "+p));
        }

    }

    @Override
    public void delete(Context context) {
       // Log.v(TAG, "delete");
        DeleteFromDB.remove(context, this);
       // Log.v(TAG, "delete: successfull deleted"+this.ingredientPump);

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
                ", slot=" + getSlot() +
                ", ingredientPump=" + ingredientPump +
                '}';
    }
}
