package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private int millilitersPumpedInMilliseconds = -1;
    private SQLIngredientPump bunker = null;

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
        try {
            this.bunker.delete();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        this.bunker = new SQLIngredientPump(-1, this.getID(), ingredient.getID());
    }

    @Override
    public void empty() {
        this.bunker = null;
    }

    @Override
    public void fill(int milliliters) {
        this.bunker.setFillLevel(milliliters);
    }

    //General
    @Override
    public void setPumps() throws NotInitializedDBException{
        //TODO:????
        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }

    @Override
    public void setOverrideEmptyPumps(int numberOfPumps) throws NotInitializedDBException {
        DatabaseConnection.getDataBase().loadEmpty();
    }


    @Override
    public boolean isAvailable() {
        return true;
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
    public int compareTo(Pump o) {
        return Long.compare(this.getID(), o.getID());
    }
}
