package com.example.cocktailmachine.data.db.elements;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.NeedsMoreIngredientException;
import com.example.cocktailmachine.data.db.NewDatabaseConnection;

import java.util.List;

public class SQLIngredient extends DataBaseElement implements Ingredient {
    private String name;
    private List<String> imageUrls;
    private boolean alcoholic;
    private boolean available;
    //private int fluidInMillimeters;
    //private long pump;
    private int color;

    private SQLIngredientPump bunker;

    public SQLIngredient(String name, boolean alcoholic, int color) {
        this.name = name;
        this.alcoholic = alcoholic;
        this.color = color;
        this.available = false;
        //this.fluidInMillimeters = -1;
        //this.pump = -1L;
        this.bunker = null;
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
        //this.fluidInMillimeters = fluidInMillimeters;
        //this.pump = pump;
        this.color = color;
        this.save();
        this.wasSaved();
        this.bunker = new SQLIngredientPump(fluidInMillimeters, pump, this.getID());
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
        //this.fluidInMillimeters = fluidInMillimeters;
        //this.pump = pump;
        this.color = color;
        this.loadUrls();

        this.bunker = new SQLIngredientPump(fluidInMillimeters, pump, this.getID());
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
        //this.fluidInMillimeters = fluidInMillimeters;
        //this.pump = pump;
        this.color = color;

        this.bunker = new SQLIngredientPump(fluidInMillimeters, pump, this.getID());
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
        //this.pump = pump;
        //this.fluidInMillimeters = fluidInMillimeters;
        Pump pp = NewDatabaseConnection.getDataBase().getPump(pump);
        pp.setCurrentIngredient(this);
        pp.save();
        this.bunker = new SQLIngredientPump(fluidInMillimeters, pump, this.getID());
        this.wasChanged();
        this.save();
        this.wasSaved();
    }

    @Override
    public int getFillLevel() {
        //return this.fluidInMillimeters;
        return this.bunker.getFillLevel();
    }

    @Override
    public Pump getPump() {
        return this.bunker.getPump();
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void pump(int millimeters) throws NeedsMoreIngredientException {
        //if(this.fluidInMillimeters - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
        //    throw new NewEmptyIngredientException(this);
        //}
        //this.fluidInMillimeters = this.fluidInMillimeters - millimeters;
        //this.wasChanged();
        try {
            this.bunker.pump(millimeters);
        } catch (NeedsMoreIngredientException e) {
            this.available = false;
            throw e;
        }

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
