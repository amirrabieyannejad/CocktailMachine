package com.example.cocktailmachine.data;

import org.json.JSONObject;

import java.util.List;

public interface Ingredient {
    public long getID();
    public String getName();
    public List<String> getImageUrls();
    public boolean isAlcoholic();
    public boolean isAvailable();
    public void addImageUrl(String url);
    public float getFluidInMilliliter();
    public Pump getPump();
    public JSONObject asMessage();
}
