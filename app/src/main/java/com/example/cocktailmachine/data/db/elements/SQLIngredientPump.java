package com.example.cocktailmachine.data.db.elements;

import android.os.Build;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.NeedsMoreIngredientException;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

import java.util.ArrayList;
import java.util.List;

public class SQLIngredientPump extends SQLDataBaseElement {
    private int fillLevel = -1;
    private long pump = -1L;
    private long ingredient = -1L;

    public SQLIngredientPump(int fillLevel, long pump, long ingredient) {
        super();
        this.fillLevel = fillLevel;
        this.pump = pump;
        this.ingredient = ingredient;
    }

    public SQLIngredientPump(long id, int fillLevel, long pump, long ingredient) {
        super(id);
        this.fillLevel = fillLevel;
        this.pump = pump;
        this.ingredient = ingredient;
    }

    public int getFillLevel(){
        return this.fillLevel;
    }

    public Pump getPump(){
        return Pump.getPump(this.pump);
    }

    public long getPumpID() {return this.pump;}

    public Ingredient getIngredient(){
        return Ingredient.getIngredient(this.ingredient);
    }

    public long getIngredientID() {return this.ingredient;}

    public boolean isPumpable(){
        return this.fillLevel >0;
    }

    public boolean isPumpable(int milliliters){
        return this.fillLevel >milliliters;
    }

    public void pump(int millimeters) throws NeedsMoreIngredientException {
        if(this.fillLevel - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
            throw new NeedsMoreIngredientException(Ingredient.getIngredient(this.ingredient));
        }
        this.fillLevel = this.fillLevel - millimeters;
        this.wasChanged();
    }

    public void setFillLevel(int milliliters){
        this.fillLevel = milliliters;
    }

    @Override
    public boolean isAvailable() {
        return false;
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


    private static List<SQLIngredientPump> getAvailableInstances(){
        //TODO
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
