package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NeedsMoreIngredientException;

import java.util.List;
import java.util.Optional;

public class SQLIngredientPump extends DataBaseElement{
    private int fillLevel;
    private long pump;
    private long ingredient;

    public SQLIngredientPump(int fillLevel, long pump, long ingredient) {
        super();
        this.fillLevel = fillLevel;
        this.pump = pump;
        this.ingredient = ingredient;
        this.save();
    }

    public SQLIngredientPump(long id, int fillLevel, long pump, long ingredient) {
        super(id);
        this.fillLevel = fillLevel;
        this.pump = pump;
        this.ingredient = ingredient;
        this.save();
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




    @Override
    void save() {

    }

    @Override
    void delete() {

    }


    private static List<SQLIngredientPump> getAvailableInstances(){
        //TODO
        return null;
    }

    public static SQLIngredientPump getInstanceWithPump(int pump){
        Optional<SQLIngredientPump> o =  getAvailableInstances().stream().filter(ip-> ip.pump==pump).findFirst();
        if(o.isPresent()){
            return null;
        }
        return o.get();
    }

}
