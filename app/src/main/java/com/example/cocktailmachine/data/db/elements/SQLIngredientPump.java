package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import java.util.ArrayList;
import java.util.List;

public class SQLIngredientPump extends SQLDataBaseElement {
    private static final String TAG = "SQLIngredientPump";
    private int volume = -1;
    private long pump = -1L;
    private long ingredient = -1L;
    private boolean available = false;



    public SQLIngredientPump(int volume, long pump, long ingredient) {
        super();
        this.volume = volume;
        this.pump = pump;
        this.ingredient = ingredient;
    }

    public SQLIngredientPump(long id, int volume, long pump, long ingredient) {
        super(id);
        this.volume = volume;
        this.pump = pump;
        this.ingredient = ingredient;
        /*
        Objects.requireNonNull(Ingredient.getIngredient(id)).setIngredientPump(this);
        Objects.requireNonNull(Ingredient.getIngredient(id)).isAvailable();
        Objects.requireNonNull(Pump.getPump(id)).setIngredientPump(this);
         */
    }

    public int getVolume(){
        return this.volume;
    }

    public Pump getPump(){
        return Pump.getPump(this.pump);
    }

    public long getPumpID() {return this.pump;}

    @Nullable
    public Ingredient getIngredient(){
        return Ingredient.getIngredient(this.ingredient);

    }
    public Ingredient getIngredient(Context context){
        return Ingredient.getIngredient(context, this.ingredient);

    }

    public long getIngredientID() {return this.ingredient;}

    public boolean isPumpable(){
        return this.volume >0;
    }

    public boolean isPumpable(int volume){
        return this.volume >volume;
    }

    public void pump(int volume) throws NewlyEmptyIngredientException {
        if(this.volume - volume <= 0){
            throw new NewlyEmptyIngredientException(this.ingredient);
        }
        this.volume = this.volume - volume;
        this.wasChanged();
    }

    public void setVolume(int volume){
        this.volume = volume;
        this.wasChanged();
    }

    public void setPumpID(long id) {
        this.pump = id;
        this.wasChanged();
    }

    @Override
    public String getClassName() {
        return "SQLIngredientPump";
    }

    /**
     * true, if volume > zero
     * @return
     */
    @Override
    public boolean isAvailable() {
        return this.volume>0;
    }

    @Override
    public boolean loadAvailable(Context context) {
        boolean res =  loadAvailable();
        this.save(context);
        return res;
    }

    /**
     * true, if pump and ingredient exists and volume > zero
     * @return
     */
    public boolean loadAvailable() {
        boolean res = (getIngredient() != null)&&(getPump() != null);

        if(res != this.available){
            Log.i(TAG, "loadAvailable: available has changed to: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available&&this.isAvailable();
    }

    @Override
    public void save(Context context) {
        AddOrUpdateToDB.addOrUpdate(context,this);
    }

    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context, this);
        //DatabaseConnection.getDataBase().remove(this);
        Log.i(TAG, "delete: success");

    }

    @Override
    public String toString() {
        return "SQLIngredientPump{" +
                "ID=" + getID() +
                ", volume=" + volume +
                ", pump=" + pump +
                ", ingredient=" + ingredient +
                '}';
    }

    private static List<SQLIngredientPump> getAvailableInstances(){
        //TO DO
        return Buffer.getSingleton().getIngredientPumps();
    }

    public static SQLIngredientPump getInstanceWithPump(long pump){
        List<SQLIngredientPump> available = getAvailableInstances();
        if(available != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return available.stream().filter(ip -> ip.pump == pump).findFirst().orElse(null);
            }
            return Helper.getWithPumpID(available, pump);
        }
        return null;
    }

    public static SQLIngredientPump getInstanceWithIngredient(long ingredient) {
        List<SQLIngredientPump> available = getAvailableInstances();
        if (available != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return available.stream().filter(ip -> ip.ingredient == ingredient).findFirst().orElse(null);
            }
            return Helper.getWithIngredientID(available, ingredient);
        }
        return null;
    }


}
