package com.example.cocktailmachine.data.db.elements;

import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

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

    public Ingredient getIngredient() {
        return Ingredient.getIngredient(this.ingredientID);
    }

    public long getRecipeID() {
        return recipeID;
    }

    public Recipe getRecipe() {
        return Recipe.getRecipe(this.recipeID);
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

    /**
     * always true
     * @return
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * true if pump exists, ingredient exists
     * @return
     */
    @Override
    public boolean loadAvailable() {
        Log.i(TAG, "loadAvailable");
        boolean res = (this.getIngredient()!=null)&&(this.getRecipe()!=null);
        if(res != this.available){
            Log.i(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
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
    public String toString() {
        return "SQLRecipeIngredient{" +
                "ID=" + getID() +
                ", ingredientID=" + ingredientID +
                ", recipeID=" + recipeID +
                ", volume=" + volume +
                '}';
    }
}
