package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;
import com.example.cocktailmachine.data.db.NewEmptyIngredientException;

import java.util.List;

public class SQLIngredient extends DataBaseElement implements Ingredient {
    private String name;
    private List<String> imageUrls;
    private boolean alcoholic;
    private boolean available;
    private int fluidInMillimeters;
    private long pump;
    private int color;

    public SQLIngredient(String name, boolean alcoholic, int color) {
        this.name = name;
        this.alcoholic = alcoholic;
        this.color = color;
        this.available = false;
        this.fluidInMillimeters = -1;
        this.pump = -1L;
    }

    public SQLIngredient(String name,
                         boolean alcoholic,
                         boolean available,
                         int fluidInMillimeters,
                         long pump,
                         int color){
        super();
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.fluidInMillimeters = fluidInMillimeters;
        this.pump = pump;
        this.color = color;
    }

    public SQLIngredient(long ID,
                         String name,
                         boolean alcoholic,
                         boolean available,
                         int fluidInMillimeters,
                         long pump,
                         int color) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.fluidInMillimeters = fluidInMillimeters;
        this.pump = pump;
        this.color = color;
        this.loadUrls();
    }

    public SQLIngredient(long ID,
                         String name,
                         List<String> imageUrls,
                         boolean alcoholic,
                         boolean available,
                         int fluidInMillimeters,
                         long pump,
                         int color) {
        super();
        this.setID(ID);
        this.name = name;
        this.imageUrls = imageUrls;
        this.alcoholic = alcoholic;
        this.available = available;
        this.fluidInMillimeters = fluidInMillimeters;
        this.pump = pump;
        this.color = color;
    }

    public Ingredient getThis(){
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getImageUrls() {
        return this.imageUrls;
    }

    @Override
    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public void addImageUrl(String url) {
        this.imageUrls.add(url);
        this.wasChanged();
    }

    @Override
    public void setPump(Long pump, int fluidInMillimeters) {
        this.available = true;
        this.pump = pump;
        this.fluidInMillimeters = fluidInMillimeters;
        this.wasChanged();
        this.save();
        this.wasSaved();
    }

    @Override
    public int getFluidInMilliliter() {
        return this.fluidInMillimeters;
    }

    @Override
    public Pump getPump() {
        return Pump.getPump(this.pump);
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void pump(int millimeters) throws NewEmptyIngredientException {
        if(this.fluidInMillimeters - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
            throw new NewEmptyIngredientException(this);
        }
        this.fluidInMillimeters = this.fluidInMillimeters - millimeters;
        this.wasChanged();
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

    public void loadUrls(){
        this.imageUrls = NewDatabaseConnection.getDataBase().getUrls(this);
    }


}
