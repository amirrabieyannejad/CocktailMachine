package com.example.cocktailmachine.data.db.elements;

import android.content.Context;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;

public class SQLRecipeIngredient extends SQLDataBaseElement {
    private static final String TAG = "SQLRecipeIngredient";
    private long ingredientID = -1L;
    private long recipeID = -1L;
    private int volume = -1;
    private boolean available = false;

    public SQLRecipeIngredient(long ingredientID, long recipeID, int volume) {
        super();
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.volume = volume;
    }

    public SQLRecipeIngredient(long id, long ingredientID, long recipeID, int volume) {
        super(id);
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
        this.volume = volume;
    }

    public long getIngredientID() {
        return ingredientID;
    }

    public Ingredient getIngredient(Context context) {
        return Ingredient.getIngredient(context,this.ingredientID);
    }

    public long getRecipeID() {
        return recipeID;
    }

    public Recipe getRecipe(Context context) {
        return Recipe.getRecipe(context, this.recipeID);
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.wasChanged();
    }
    public void setRecipeID(long recipeID) {
        this.recipeID= recipeID;
        this.wasChanged();
    }

    @Override
    public String getClassName() {
        return "SQLRecipeIngredient";
    }

    /**
     * always true
     * @return
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean loadAvailable(Context context) {
        this.available = ExtraHandlingDB.loadAvailability(context, this);
        //this.save(context);
        return this.available;
    }

    @Override
    public void save(Context context) {
       // Log.v(TAG, "save");
        AddOrUpdateToDB.addOrUpdate(context, this);
    }

    @Override
    public void delete(Context context) {
       // Log.v(TAG, "delete");
        DeleteFromDB.remove(context, this);
    }


    @Override
    public String toString() {
        return "SQLRecipeIngredient{" +
                "ID=" + getID() +
                ", ingredientID=" + ingredientID +
                ", recipeID=" + recipeID +
                ", volume=" + volume +
                '}';
    }
}
