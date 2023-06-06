package com.example.cocktailmachine.data.db.elements;

import android.os.Build;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.DatabaseConnection;
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

    public Ingredient getIngredient(){
        try {
            return DatabaseConnection.getDataBase().loadIngredientForPump(this.ingredient);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
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

    /**
     * true, if volume > zero
     * @return
     */
    @Override
    public boolean isAvailable() {
        return this.volume>0;
    }

    /**
     * true, if pump and ingredient exists and volume > zero
     * @return
     */
    @Override
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
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
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
        try {
            return DatabaseConnection.getDataBase().getIngredientPumps();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
