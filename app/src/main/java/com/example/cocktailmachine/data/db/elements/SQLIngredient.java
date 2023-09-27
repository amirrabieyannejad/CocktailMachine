package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        this.checkIngredientPumps();
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
        //TODO: add urls
        //this.imageUrls = DatabaseConnection.getDataBase().getUrls(this);
        //if(this.imageUrls == null){
        this.imageUrls = new ArrayList<>();
        //}
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
    public String getClassName() {
        return "SQLIngredient";
    }

    /**
     * true, if ingredient pump connection exists and pump filled with conntent
     * @return
     */
    @Override
    public boolean isAvailable() {
        Log.i(TAG, "isAvailable: available"+this.available);
        return this.available;
    }

    @Override
    public boolean loadAvailable(Context context) {
        loadAvailable();
        this.save(context);
        return isAvailable();
    }

    /**
     * true, if ingredient pump connection exists and pump filled with conntent
     * @return
     */
    public boolean loadAvailable() {
        Log.i(TAG, "loadAvailable");
        this.checkIngredientPumps();
        boolean res = false;
        Log.i(TAG,"loadAvailable: check all ingredientpumps for availability");
        if(this.ingredientPump != null){
            res = this.ingredientPump.isAvailable();
        }
        if(res != this.available){
            Log.i(TAG, "loadAvailable: available changed");
            this.available = res;
            Buffer.getSingleton().available(this, this.available);
            this.wasChanged();
        }
        return isAvailable();
    }

    @Override
    public void addImageUrl(String url) {
        Log.i(TAG,"addImageUrl");
        this.imageUrls.add(url);
        this.wasChanged();
    }

    @Override
    public void removeImageUrl(String url) {
        Log.i(TAG,"removeImageUrl");
        this.imageUrls.remove(url);
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

    /**
     * no pump, check for coennction
     * -1, if no pump
     * volume, if pump connected
     * @return
     */
    @Override
    public int getVolume() {
        //return this.fluidInMillimeters;
        Log.i(TAG,"getVolume");
        this.checkIngredientPumps();
        if(this.ingredientPump!=null) {
            Log.i(TAG, "getVolume: Ingredientpump is not null for ingredient "+this.getID()+this.name);
            return this.ingredientPump.getVolume();
        }
        Log.i(TAG, "getVolume: Ingredientpump is null for ingredient "+this.getID()+this.name);
        return -1;
    }

    /**
     * no pump, check for it
     * null, if no pump, else pump
     * @return
     */
    @Override
    public Pump getPump() {
        this.checkIngredientPumps();

        if(this.ingredientPump!=null) {
            return this.ingredientPump.getPump();
        }
        return null;
    }

    /**
     * no pump, check for it
     * -1, if no pump, else pump id
     * @return
     */
    @Override
    public Long getPumpId() {
        this.checkIngredientPumps();
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

    /**
     * make pump ingredient connection with new volume
     * @param pump
     * @param volume
     */
    @Override
    public void setPump(Context context, Long pump, int volume) {
        this.available = true;
        //this.pump = pump;
        //this.volume = volume;

        Pump pp = Pump.getPump(pump);
        if(pp != null) {
            pp.setCurrentIngredient(context,this);
            this.ingredientPump = new SQLIngredientPump(volume, pump, this.getID());
        }
        this.wasChanged();
        this.save(context);
    }

    /**
     * delete ingredient pump connection if exists, and set to null
     */
    @Override
    public void empty(Context context) {
        this.checkIngredientPumps();
        if(ingredientPump != null){
            this.ingredientPump.delete(context);
        }
        this.ingredientPump = null;
        this.loadAvailable();
        this.wasChanged();
    }


    /**
     * set ingredient pump connection
     * @param ingredientPump
     */
    @Override
    public void setIngredientPump(SQLIngredientPump ingredientPump) {
        this.ingredientPump = ingredientPump;
        //this.wasChanged();
        this.loadAvailable();
        Log.i(TAG, "setIngredientPump: "+ingredientPump+available);
        this.wasChanged();
    }

    /**
     * check for existing ingredient pump connection
     * @throws NotInitializedDBException
     */
    private void checkIngredientPumps() {
        Log.i(TAG, "checkIngredientPumps");
        if(this.ingredientPump==null){
            Log.i(TAG,"checkIngredientPumps: check for ingredientpump");
            List<SQLIngredientPump> ips = Buffer.getSingleton().getIngredientPumps();
            for(SQLIngredientPump ip: ips){
                if(ip.getIngredientID()==this.getID()){
                    this.setIngredientPump(ip);
                    Log.i(TAG,"checkIngredientPumps: settes ingredientpump: "+ip);
                    return;
                }
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


    /**
     * if no pump, check for existing connection
     * pump given volume
     * or throw exception
     * @param volume m
     * @throws NewlyEmptyIngredientException
     */
    @Override
    public void pump(int volume) throws NewlyEmptyIngredientException, MissingIngredientPumpException {
        //if(this.fluidInMillimeters - millimeters < this.getPump().getMillilitersPumpedInMilliseconds()){
        //    throw new NewEmptyIngredientException(this);
        //}
        //this.fluidInMillimeters = this.fluidInMillimeters - millimeters;
        //this.wasChanged();

        Log.i(TAG,"pump");
        this.checkIngredientPumps();
        if(this.ingredientPump != null) {
            try {
                this.ingredientPump.pump(volume);
                return;
            } catch (NewlyEmptyIngredientException | NullPointerException e) {
                e.printStackTrace();
                this.available = false;
                throw new NewlyEmptyIngredientException(this);
            }
        }
        throw new MissingIngredientPumpException("Cannot pump, because there is no pump connection from ingredient: "+this);

    }

    //DB
    @Override
    public void save(Context context) {
        AddOrUpdateToDB.addOrUpdate(context,this);
    }

    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context,this);
        Log.i(TAG, "delete: successful deleted");
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

    @Override
    public String toString() {
        return "SQLIngredient{" +
                "ID='" + getID() + '\'' +
                ", name='" + name + '\'' +
                ", imageUrls=" + imageUrls +
                ", urlsLoaded=" + urlsLoaded +
                ", alcoholic=" + alcoholic +
                ", available=" + available +
                ", color=" + color +
                ", ingredientPump=" + ingredientPump +
                '}';
    }

    @Override
    public int compareTo(Ingredient o) {
        return Long.compare(this.getID(), o.getID());
    }
}
