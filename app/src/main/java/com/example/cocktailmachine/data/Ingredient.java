package com.example.cocktailmachine.data;

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
    public default JSONObject asMessage(){
        //TODO
        return null;
    }
}
