package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NeedsMoreIngredientException;
import com.example.cocktailmachine.data.db.NewEmptyIngredientException;

import java.util.List;

public class SQLIngredientPump extends DataBaseElement{
    private int fluidInMillimeters;
    private long pump;
    private long ingredient;

    public SQLIngredientPump(int fluidInMillimeters, long pump, long ingredient) {
        this.fluidInMillimeters = fluidInMillimeters;
        this.pump = pump;
        this.ingredient = ingredient;
        this.save();
    }

    public int getFluidInMillimeters(){
        return this.fluidInMillimeters;
    }

    public Pump getPump(){
        return Pump.getPump(this.pump);
    }

    public Ingredient getIngredient(){
        return Ingredient.getIngredient(this.ingredient);
    }

    public boolean isPumpable(){
        return this.fluidInMillimeters>0;
    }

    public boolean isPumpable(int milliliters){
        return this.fluidInMillimeters>milliliters;
    }

    public void pump(int millimeters) throws NeedsMoreIngredientException {
        if(this.fluidInMillimeters - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
            throw new NeedsMoreIngredientException(Ingredient.getIngredient(this.ingredient));
        }
        this.fluidInMillimeters = this.fluidInMillimeters - millimeters;
        this.wasChanged();
    }

    @Override
    void save() {

    }

    @Override
    void delete() {

    }
}
