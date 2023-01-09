package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class SQLPump extends DataBaseElement implements Pump {
    private int millilitersPumpedInMilliseconds;
    private Ingredient currentIngredient;

    public SQLPump(long ID, int millilitersPumpedInMilliseconds) {
        super(ID);
        this.wasSaved();
        this.millilitersPumpedInMilliseconds = millilitersPumpedInMilliseconds;
    }

    @Override
    public int getMillilitersPumpedInMilliseconds() {
        return this.millilitersPumpedInMilliseconds;
    }

    @Override
    public Ingredient getCurrentIngredient() {
        return this.currentIngredient;
    }

    @Override
    public void setCurrentIngredient(Ingredient ingredient) {
        this.currentIngredient = ingredient;
        this.wasChanged();
    }

    @Override
    public void setPumps() {
        //TODO:????
    }


    @Override
    public void save() {
        NewDatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() {
        NewDatabaseConnection.getDataBase().remove(this);
    }
}
