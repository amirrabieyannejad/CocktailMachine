package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;

public class SQLPump extends DataBaseElement implements Pump {
    private int millilitersPumpedInMilliseconds;
    private SQLIngredientPump bunker;

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
        return this.bunker.getIngredient();
    }

    @Override
    public void setCurrentIngredient(Ingredient ingredient) {
        this.bunker.delete();
        this.bunker = new SQLIngredientPump(-1, this.getID(), ingredient.getID());
    }

    //General
    @Override
    public void setPumps() {
        //TODO:????
        NewDatabaseConnection.getDataBase().loadBufferWithAvailable();
    }

    @Override
    public void setOverrideEmptyPumps(int numberOfPumps) {
        NewDatabaseConnection.getDataBase().loadEmpty();
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
