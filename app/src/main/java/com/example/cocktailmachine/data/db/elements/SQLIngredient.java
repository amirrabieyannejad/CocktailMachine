package com.example.cocktailmachine.data.db.elements;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SQLIngredient extends SQLDataBaseElement implements Ingredient {
    private String name = "";
    private List<String> imageUrls = new ArrayList<>();
    private final boolean urlsLoaded = false;
    private boolean alcoholic;
    private boolean available = false;
    //private int fluidInMillimeters;
    //private long pump;
    private int color = -1;

    private SQLIngredientPump ingredientPump;

    public SQLIngredient(String name) {
        this.name = name;
        this.available = false;
        //this.fluidInMillimeters = -1;
        //this.pump = -1L;
        this.ingredientPump = null;
        //this.loadUrls();
    }

    public SQLIngredient(String name, boolean alcoholic, int color) {
        this.name = name;
        this.alcoholic = alcoholic;
        this.color = color;
        this.available = false;
        //this.fluidInMillimeters = -1;
        //this.pump = -1L;
        this.ingredientPump = null;
        //this.loadUrls();
    }

    public SQLIngredient(String name,
                         boolean alcoholic,
                         boolean available,
                         int volume,
                         long pump,
                         int color){
        super();
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        //this.volume = volume;
        //this.pump = pump;
        this.color = color;
        //this.loadUrls();
        this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
    }

    public SQLIngredient(long ID,
                         String name,
                         boolean alcoholic,
                         int color) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.color = color;
        //this.loadUrls();
    }

    public SQLIngredient(long ID,
                         String name,
                         boolean alcoholic,
                         boolean available,
                         int volume,
                         long pump,
                         int color) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        //this.volume = volume;
        //this.pump = pump;
        this.color = color;
        //this.loadUrls();

        this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
    }

    public SQLIngredient(long ID,
                         String name,
                         List<String> imageUrls,
                         boolean alcoholic,
                         boolean available,
                         int volume,
                         long pump,
                         int color) {
        super();
        this.setID(ID);
        this.name = name;
        this.imageUrls = imageUrls;
        this.alcoholic = alcoholic;
        this.available = available;
        //this.volume = volume;
        //this.pump = pump;
        this.color = color;
        //this.loadUrls();
        this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
    }


    //Loading
    public void loadUrls() throws NotInitializedDBException{
        this.imageUrls = DatabaseConnection.getDataBase().getUrls(this);
        if(this.imageUrls == null){
            this.imageUrls = new ArrayList<>();
        }
    }

    //Getter

    public Ingredient getThis(){
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getImageUrls() {
        if(!this.urlsLoaded) {
            try {
                this.loadUrls();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
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
    public int getVolume() {
        //return this.fluidInMillimeters;
        return this.ingredientPump.getVolume();
    }

    @Override
    public Pump getPump() {
        return this.ingredientPump.getPump();
    }

    @Override
    public int getColor() {
        return this.color;
    }

    //Setter / Changer
    @Override
    public void setPump(Long pump, int volume) {
        this.available = true;
        //this.pump = pump;
        //this.volume = volume;

        Pump pp = Pump.getPump(pump);
        assert pp != null;
        pp.setCurrentIngredient(this);
        this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
        this.wasChanged();
    }

    @Override
    public void pump(int volume) throws NewlyEmptyIngredientException{
        //if(this.fluidInMillimeters - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
        //    throw new NewEmptyIngredientException(this);
        //}
        //this.fluidInMillimeters = this.fluidInMillimeters - millimeters;
        //this.wasChanged();
        try {
            this.ingredientPump.pump(volume);
        } catch (NewlyEmptyIngredientException e) {
            e.printStackTrace();
            this.available = false;
            throw e;
        }

    }

    //DB
    @Override
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        this.wasSaved();
    }

    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
    }

    //Comparable

    private JSONObject asJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("ID", this.getID());
            json.put("name", this.name);
            json.put("color", this.color);
            json.put("alcoholic", this.alcoholic);
            json.put("available", this.available);
            json.put("imageUrls", this.imageUrls.toString());
            if(this.available) {
                json.put("pumpID", this.ingredientPump.getPumpID());
            }
            return json;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Ingredient){
            return ((Ingredient) obj).getID()==this.getID();
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return Objects.requireNonNull(this.asJSON()).toString();
        }catch (NullPointerException e){
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int compareTo(Ingredient o) {
        return Long.compare(this.getID(), o.getID());
    }
}
