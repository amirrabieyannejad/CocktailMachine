package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;

/**
 * @author Johanna Reidt
 * @created Fr. 19.Jan 2024 - 13:35
 * @project CocktailMachine
 */
public class SQLNewPump extends SQLDataBaseElement implements Pump {
    private static final String TAG = "SQLNewPump";
    private int slot = -1;
    private long ingredientID = -1L;
    private int volume = 0;

    //private loaded
    private String loadedIngredientName = "Keine Zutat";
    private Ingredient loadedIngredient = null;

    public SQLNewPump() {
        super(-1L);
    }
    public SQLNewPump(long id, int slotID, long ingID, int volume) {
        super(id);
        this.slot = slotID;
        this.volume=volume;
        this.ingredientID = ingID;
        Log.i(TAG,"SQLNewPump: "+this);
    }

    private void load(Context context){
        this.loadedIngredient = Ingredient.getIngredient(context, this.ingredientID);
        this.loadedIngredientName = this.loadedIngredient.getName();
    }


    @Override
    public long getIngredientID() {
        return this.ingredientID;
    }

    @Override
    public int getVolume(Context context) {
        return 0;
    }

    @Override
    public void empty(Context context) {
        this.volume = 0;
        this.save(context);
    }

    @Override
    public void fill(Context context, int volume) throws MissingIngredientPumpException {
        this.volume = volume;
        this.save(context);
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public String getIngredientName() {
        return this.loadedIngredientName;
    }

    @Override
    public String getIngredientName(Context context) {
        this.load(context);
        return this.loadedIngredientName;
    }

    @Override
    public Ingredient getCurrentIngredient(Context context) {
        this.load(context);
        return this.loadedIngredient;
    }

    @Override
    public void setCurrentIngredient(Context context, long id) {
        Log.i(TAG, "setCurrentIngredient: ");
        this.ingredientID = id;
        Log.i(TAG, "setCurrentIngredient: "+this);
        this.load(context);
        Log.i(TAG, "setCurrentIngredient: "+this);
        this.save(context);
    }

    @Override
    public void preSetIngredient(long id) {
        this.ingredientID = id;
    }

/*
    public void setIngredientPump(Context context, SQLIngredientPump ingredientPump) {
        if(ingredientPump == null){
            return;
        }
        this.ingredientID = ingredientPump.getIngredientID();
        this.volume = ingredientPump.getVolume();
        this.load(context);
        return;
    }

 */

    @Override
    public void setSlot(int i) {
        this.slot = i;
    }

    @Override
    public int getVolume() {
        return this.volume;
    }

    @Override
    public void deleteCurrentIngredient(Context context) {
        this.ingredientID = -1L;
        this.wasChanged();
        this.save(context);
    }

    @Override
    public void pump(int volume) throws NewlyEmptyIngredientException, MissingIngredientPumpException {

    }

    @Override
    public String getClassName() {
        return "SQLNewPump";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean loadAvailable(Context context) {
        this.load(context);
        return true;
    }

    @Override
    public void save(Context context) {
        Log.v(TAG, "save: "+this);
        AddOrUpdateToDB.addOrUpdate(context, this);
        Log.i(TAG, "save: from DB: "+GetFromDB.getNewPump(context, this.getID()).toString());
    }

    @Override
    public void delete(Context context) {
        Log.v(TAG, "delete: "+this);
        DeleteFromDB.remove(context, this);
    }

    @Override
    public int compareTo(Pump o) {
        return Long.compare(this.getID(), o.getID());
    }


    @NonNull
    @Override
    public String toString() {
        return "SQLNewPump{" +
                "slot=" + slot +
                ", ingredientID=" + ingredientID +
                ", volume=" + volume +
                ", loadedIngredientName='" + loadedIngredientName + '\'' +
                ", loadedIngredient=" + loadedIngredient +
                '}';
    }
}
