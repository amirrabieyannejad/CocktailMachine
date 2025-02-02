package com.example.cocktailmachine.data.db.elements;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;

import java.util.LinkedList;
import java.util.List;

public class SQLIngredientPump extends SQLDataBaseElement {
    private static final String TAG = "SQLIngredientPump";
    private int volume = 0;
    private long pump = -1L;
    private long ingredient = -1L;


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

    public Pump getPump(Context context){
        return Pump.getPump(context,this.pump);
    }

    public long getPumpID() {return this.pump;}

    @Nullable
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
        boolean available = false;//ExtraHandlingDB.loadAvailability(context, this);
        return available &&this.isAvailable();
    }



    @Override
    public void save(Context context) {
        //AddOrUpdateToDB.addOrUpdate(context,this);
    }

    @Override
    public void delete(Context context) {
        // Log.v(TAG, "delete");
        //DeleteFromDB.remove(context, this);
        //DatabaseCon   nection.getDataBase().remove(this);
        // Log.v(TAG, "delete: success");

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

    private static List<SQLIngredientPump> getAvailableInstances(Context context){
        //TO DO
        //return GetFromDB.getIngredientPumps(context);
        return new LinkedList<>();
    }






}
