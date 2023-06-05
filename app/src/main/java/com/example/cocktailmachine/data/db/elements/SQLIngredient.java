package com.example.cocktailmachine.data.db.elements;

import android.graphics.Color;
import android.util.Log;

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
    private int color = Color.GREEN;

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
        try {
            this.checkIngredientPumps();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }
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
        if(this.ingredientPump==null){
            Log.i(TAG,"isAvailable: check for ingredientpump");
            try {
                this.checkIngredientPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        boolean res = false;
        if(this.ingredientPump != null){
            res = this.ingredientPump.isAvailable();
        }
        if(res != this.available){
            Log.i(TAG, "isAvailable: available changed");
            this.available = res;
            this.wasChanged();
        }
        Log.i(TAG, "isAvailable: available"+this.available);
        return this.available;
    }

    @Override
    public void addImageUrl(String url) {
        this.imageUrls.add(url);
        this.wasChanged();
    }

    /*
    void checkIngredientPump() throws NotInitializedDBException {
        List<SQLIngredientPump> ingredientPumps = DatabaseConnection.getDataBase().getIngredientPumps();
        for(SQLIngredientPump ip: ingredientPumps){
            if(ip.getIngredientID()==this.getID()){
                this.ingredientPump = ip;
            }
        }
    }

     */

    @Override
    public int getVolume() {
        //return this.fluidInMillimeters;
        if(this.ingredientPump==null){
            Log.i(TAG,"getVolume: check for ingredientpump");
            try {
                this.checkIngredientPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        if(this.ingredientPump!=null) {
            Log.i(TAG, "getVolume: Ingredientpump is not null for ingredient "+this.getID()+this.name);
            return this.ingredientPump.getVolume();
        }
        Log.i(TAG, "getVolume: Ingredientpump is null for ingredient "+this.getID()+this.name);
        return -1;
    }

    @Override
    public Pump getPump() {
        return this.ingredientPump.getPump();
    }

    @Override
    public Long getPumpId() {
        if(this.ingredientPump != null) {
            return this.ingredientPump.getPumpID();
        }
        return -1L;
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
        if(pp != null) {
            pp.setCurrentIngredient(this);
            this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
        }
        this.wasChanged();
    }


    @Override
    public void setIngredientPump(SQLIngredientPump ingredientPump) {
        this.ingredientPump = ingredientPump;
        //this.wasChanged();
        Log.i(TAG, "setIngredientPump: "+ingredientPump.toString()+available);
        this.wasChanged();
    }

    private void checkIngredientPumps() throws NotInitializedDBException {
        Log.i(TAG, "checkIngredientPumps");
        List<SQLIngredientPump> ips = DatabaseConnection.getDataBase().getIngredientPumps();
        for(SQLIngredientPump ip: ips){
            if(ip.getIngredientID()==this.getID()){
                this.setIngredientPump(ip);
            }
        }
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
        } catch (NewlyEmptyIngredientException|NullPointerException e) {
            e.printStackTrace();
            this.available = false;
            throw new NewlyEmptyIngredientException(this);
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
