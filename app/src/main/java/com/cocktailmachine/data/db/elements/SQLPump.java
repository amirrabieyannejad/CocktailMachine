package com.cocktailmachine.data.db.elements;

import com.cocktailmachine.data.Ingredient;
import com.cocktailmachine.data.Pump;
import com.cocktailmachine.data.db.DatabaseConnection;
import com.cocktailmachine.data.db.NotInitializedDBException;

import org.json.JSONArray;
import org.json.JSONObject;

public class SQLPump extends SQLDataBaseElement implements Pump {
    private int minimumPumpVolume = -1;
    private SQLIngredientPump ingredientPump = null;

    public SQLPump(){
        super();
    }

    public SQLPump(long ID, int minimumPumpVolume) {
        super(ID);
        this.wasSaved();
        this.minimumPumpVolume = minimumPumpVolume;
    }

    @Override
    public int getMinimumPumpVolume() {
        return this.minimumPumpVolume;
    }

    @Override
    public String getIngredientName() {
        return getCurrentIngredient().getName();
    }

    @Override
    public int getVolume() {
        if (ingredientPump.equals(null))
            return 0;
        return getCurrentIngredient().getVolume();
    }

    @Override
    public Ingredient getCurrentIngredient() {
        return this.ingredientPump.getIngredient();
    }

    @Override
    public void setCurrentIngredient(Ingredient ingredient) {
        try {
            this.ingredientPump.delete();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        this.ingredientPump = new SQLIngredientPump(-1, this.getID(), ingredient.getID());
    }

    @Override
    public void empty() {
        this.ingredientPump = null;
    }

    @Override
    public void fill(int volume) {
        this.ingredientPump.setVolume(volume);
    }

    //General



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
