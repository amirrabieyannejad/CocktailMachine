package com.example.cocktailmachine.data;


import com.example.cocktailmachine.data.db.NewDatabaseConnection;
import com.example.cocktailmachine.data.db.NewEmptyIngredientException;


import org.json.JSONObject;

import java.util.List;

public interface Ingredient {

    //Reminder: Only liquids!!!

    public long getID();
    public String getName();
    public List<String> getImageUrls();
    public boolean isAlcoholic();
    public boolean isAvailable();
    public void addImageUrl(String url);
    public int getFluidInMilliliter();
    public Pump getPump();
    public int getColor();



    public void pump(int millimeters) throws NewEmptyIngredientException;
    public void save();
    public void delete();

    public static List<Ingredient> getIngredients(){
        return (List<Ingredient>) NewDatabaseConnection.getDataBase().getAvailableIngredients();
    }
    public static List<Ingredient> getIngredients(List<Long> ingredientsIds){
        return NewDatabaseConnection.getDataBase().getIngredients(ingredientsIds);
    }

    public static Ingredient getIngredient(Long id){
        return NewDatabaseConnection.getDataBase().getIngredient(id);
    }

    public default JSONObject asMessage(){
        //TODO
        return null;
    }


}
