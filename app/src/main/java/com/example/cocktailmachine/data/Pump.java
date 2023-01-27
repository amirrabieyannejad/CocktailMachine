package com.example.cocktailmachine.data;


import android.view.animation.ScaleAnimation;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLPump;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Pump extends Comparable<Pump>, DataBaseElement {
    /**
     * Get Id.
     * @return
     */
    public long getID();

    /**
     * Returns milliliters pumped in milliseconds.
     * aka minimum available pump size
     * @return minimum milliliters to be pumped
     */
    public int getMinimumPumpVolume();

    public String getIngredientName();

    public int getVolume();

    /**
     * Return current ingredient set in pump.
     * @return current ingredient
     */
    public Ingredient getCurrentIngredient();


    /**
     * Update ingredient in pump.
     * @param ingredient next ingredient.
     */
    public void setCurrentIngredient(Ingredient ingredient);

    /**
     * Update ingredient in pump.
     * @param id id of next ingredient
     */
    public default void setCurrentIngredient(long id) throws NotInitializedDBException {
        setCurrentIngredient(Ingredient.getIngredient(id));
    }

    public void empty();

    public void fill(int volume);




    //general

    /**
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    public static void loadFromPumps() throws NotInitializedDBException{
        DatabaseConnection.getDataBase().loadBufferWithAvailable();

    }


    /**
     * {"beer": 200, "lemonade": 2000, "orange juice": 2000} Flüssigkeiten Status
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    public static void setPumps(JSONObject json) throws NotInitializedDBException, JSONException {
        JSONArray names = json.names();
        if(names==null){
            return;
        }
        int l = names.length();
        List<Pump> pumps = setOverrideEmptyPumps(l);
        int volume = 0;
        Ingredient ig;

        for (int i = 0; i< l; i++){
            String n = names.optString(i, null);
            volume = json.getInt(n);
            ig = DatabaseConnection.getDataBase().getIngredientWithExact(n);
            if(ig == null){
                ig = Ingredient.makeNew(n);
                ig.save();
            }
            pumps.get(i).setCurrentIngredient(ig);
            pumps.get(i).fill(volume);
            pumps.get(i).save();
        }

        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }

    /**
     * {"beer": 200, "lemonade": 2000, "orange juice": 2000} Flüssigkeiten Status
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    public static void updatePumpStatus(JSONObject json) throws JSONException, NewlyEmptyIngredientException, NotInitializedDBException {
        List<Pump> pumps = DatabaseConnection.getDataBase().getPumps();
        JSONArray t_names = json.names();
        if(t_names == null){
            throw new JSONException("Ingredient Names not readable.");
        }
        List<String> names = new ArrayList<>();
        for(int i=0; i<t_names.length();i++){
            names.add(t_names.getString(i));
        }

        //TODO
        for(Pump p:pumps){
            Ingredient ig = p.getCurrentIngredient();
            if(names.contains(ig.getName())){
                p.fill(json.getInt(ig.getName()));
                p.save();
                names.remove(ig.getName());
            }else{
                p.empty();
                p.delete(); //Lösche weil ungenannt
            }
        }

        for(String n: names){ //Falls noch Namen übrig
            Pump temp = Pump.makeNew();
            Ingredient ig = DatabaseConnection.getDataBase().getIngredientWithExact(n);
            if(ig == null){
                ig = Ingredient.makeNew(n);
                ig.save();
            }
            temp.setCurrentIngredient(ig);
            temp.fill(json.getInt(n));
            temp.save();
        }

        DatabaseConnection.getDataBase().loadBufferWithAvailable();

    }


    /**
     * [["beer", 250], ["lemonade", 250]] current Mixing cocktail
     * @param json
     * @throws JSONException
     * @throws NewlyEmptyIngredientException
     */
    public static void updatePumpStatus(JSONArray json) throws JSONException, NewlyEmptyIngredientException {
        int i = 0;
        JSONArray temp = json.optJSONArray(i);
        while(temp != null){
            String name = temp.getString(0);
            int volume = temp.getInt(1);
            Ingredient.getIngredient(name).pump(volume);
            i++;
            temp = json.optJSONArray(i);
        }

    }

    public static Pump makeNew(){
        return new SQLPump();
    }


    /**
     * Set up k empty pumps.
     *
     * @param numberOfPumps k
     */
    public static List<Pump> setOverrideEmptyPumps(int numberOfPumps) throws NotInitializedDBException {
        DatabaseConnection.getDataBase().loadForSetUp();
        for(int i=0; i<numberOfPumps;i++){
            Pump pump = makeNew();
            pump.save();
        }
        return DatabaseConnection.getDataBase().getPumps();
    }

    /**
     * Static access to pumps.
     * Get available pumps.
     * @return pumps
     */
    public static List<Pump> getPumps() throws NotInitializedDBException {
          return DatabaseConnection.getDataBase().getPumps();
    }

    /**
     * Static access to pumps.
     * Get available pump with id k
     * @param id pump id k
     * @return
     */
    public static Pump getPump(long id) {
        try {
            return DatabaseConnection.getDataBase().getPump(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }


    public default JSONObject asMesssage() throws JSONException {
        //TODO: asMessage: https://github.com/johannareidt/CocktailMachine/blob/main/ProjektDokumente/esp/Services.md
        JSONObject json = new JSONObject();
        json.put(this.getIngredientName(), this.getVolume());
        return json;
    }

    public static JSONObject getAllPumpsStatus() throws JSONException, NotInitializedDBException {

        JSONObject json = new JSONObject();
        List<Pump> pumps = Pump.getPumps();
        for(Pump p: pumps){
            json.put(p.getIngredientName(), p.getVolume());
        }
        return json;
    }


}
