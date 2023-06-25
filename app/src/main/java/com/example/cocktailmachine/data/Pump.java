package com.example.cocktailmachine.data;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.enums.AdminRights;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface Pump extends Comparable<Pump>, DataBaseElement {
    static final String TAG = "Pump";

    /**
     * Get Id.
     * @return
     */
    long getID();

    //Volume

    /**
     * Returns milliliters pumped in milliseconds.
     * aka minimum available pump size
     * @return minimum milliliters to be pumped
     */
    int getMinimumPumpVolume();

    int getVolume();

    void empty();

    /**
     * set volume
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void fill(int volume) throws MissingIngredientPumpException;




    //Ingredient
    String getIngredientName();

    /**
     * Return current ingredient set in pump.
     * @return current ingredient
     */
    Ingredient getCurrentIngredient();

    /**
     * Update ingredient in pump.
     * @param ingredient next ingredient.
     */
    default void setCurrentIngredient(Ingredient ingredient){
        setCurrentIngredient(ingredient.getID());
    }

    /**
     * Update ingredient in pump.
     * @param id id of next ingredient
     */
    void setCurrentIngredient(long id);


    //DATA BASE Stuff

    /**
     * only use after db loading to connect pump and ingredient
     * @param ingredientPump
     */
    void setIngredientPump(SQLIngredientPump ingredientPump);


    /**
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    static void loadFromDB() throws NotInitializedDBException{
        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }



    /**
     * Set up k empty pumps.
     *
     * @param numberOfPumps k
     */
    static List<Pump> setOverrideEmptyPumps(int numberOfPumps) throws NotInitializedDBException {
        DatabaseConnection.getDataBase().loadForSetUp();
        for(int i=0; i<numberOfPumps;i++){
            Pump pump = makeNew();
            pump.save();
        }
        return DatabaseConnection.getDataBase().getPumps();
    }



    static Pump makeNew(){
        return new SQLPump();
    }

    /**
     * update pump with ingredient name and volume
     * @param liquidName
     * @param volume
     * @return pump
     * @throws MissingIngredientPumpException
     * @throws NotInitializedDBException
     */
    static Pump makeNewOrUpdate(String liquidName, int volume)
            throws MissingIngredientPumpException, NotInitializedDBException {
        Ingredient ingredient =  Ingredient.getIngredient(liquidName);
        if(ingredient == null){
            ingredient = Ingredient.makeNew(liquidName);
            ingredient.save();
        }
        if(ingredient.getPump()==null){
            Pump pump = makeNew();
            pump.setCurrentIngredient(ingredient);
            pump.fill(volume);
        }else{
            ingredient.getPump().fill(volume);
        }
        ingredient.save();
        ingredient.getPump().save();

        return ingredient.getPump();
    }



    //JSON Object
    // creation
    /**
     * {"beer": 200}
     * @return
     * @throws JSONException
     */
    default JSONObject asMesssage() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(this.getIngredientName(), this.getVolume());
        return json;
    }



    /**
     *  {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * @return {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static JSONObject getLiquidStatus() throws JSONException, NotInitializedDBException {
        JSONObject json = new JSONObject();
        List<Pump> pumps = Pump.getPumps();
        for(Pump p: pumps){
            json.put(p.getIngredientName(), p.getVolume());
        }
        return json;
    }

    /**
     *  {"1": {"liquid": "lemonade", "volume": 200}}
     * @return {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static JSONObject getPumpStatus() throws JSONException, NotInitializedDBException {
        JSONObject json = new JSONObject();
        List<Pump> pumps = Pump.getPumps();
        for(int i = 1; i<=pumps.size(); i++){
            Pump p = pumps.get(i);
            JSONObject temp = new JSONObject();
            temp.put(p.getIngredientName(), p.getVolume());
            json.put(String.valueOf(i),temp);
        }
        return json;
    }

    //reading

    /**
     * {"1": {"liquid": "lemonade", "volume": 200}}
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     * @param json
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static void updatePumpStatus(JSONObject json) throws JSONException, NotInitializedDBException, MissingIngredientPumpException {
        Log.i(TAG,"updatePumpStatus");
        JSONArray t_ids = json.names();
        if(t_ids == null){
            throw new JSONException("Pump IDs not readable.");
        }
        List<Pump> toDeletePumps = Pump.getPumps();
        for(int i=0; i<t_ids.length();i++){
            toDeletePumps.remove(
                    makeNewOrUpdate(
                            Objects.requireNonNull(
                                    json.optJSONObject(
                                            t_ids.getString(i)))));
        }
        for(Pump toDelete: toDeletePumps){
            toDelete.delete();
        }
        DatabaseConnection.getDataBase().loadBufferWithAvailable();

    }

    /**
     *
     * {"beer": 200, "lemonade": 2000, "orange juice": 2000} Flüssigkeiten Status
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     * @param json
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static void updateLiquidStatus(JSONObject json) throws JSONException, NotInitializedDBException, MissingIngredientPumpException {
        Log.i(TAG,"updatePumpStatus");
        //List<Pump> pumps = DatabaseConnection.getDataBase().getPumps();
        JSONArray t_names = json.names();
        if(t_names == null){
            throw new JSONException("Ingredient Names not readable.");
        }
        /*
        List<String> names = new ArrayList<>();
        for(int i=0; i<t_names.length();i++){
            names.add(t_names.getString(i));
        }

        //TO DO
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

         */

        List<Pump> toDeletePumps = Pump.getPumps();
        for(int i=0; i<t_names.length(); i++){
            toDeletePumps.remove(
                    makeNewOrUpdate(
                            t_names.optString(i),
                            json.optInt(t_names.optString(i))));
        }
        for(Pump toDelete: toDeletePumps){
            toDelete.delete();
        }
        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }

    /**
     * [["beer", 250], ["lemonade", 250]] current Mixing cocktail
     * @param json
     * @throws JSONException
     * @throws NewlyEmptyIngredientException
     */
    static void updatePumpStatus(JSONArray json) throws JSONException, NewlyEmptyIngredientException {
        Log.i(TAG,"updatePumpStatus");
        int i = 0;
        JSONArray temp = json.optJSONArray(i);
        while(temp != null){
            String name = temp.getString(0);
            int volume = temp.getInt(1);
            try {
                Ingredient.getIngredient(name).pump(volume);
            } catch (MissingIngredientPumpException e) {
                e.printStackTrace();
                Log.i(TAG,"updatePumpStatus: should not happen");
            }
            i++;
            temp = json.optJSONArray(i);
        }

    }

    /**
     * {"liquid": "lemonade", "volume": 200}
     * @param jsonObject
     * @return
     */
    static Pump makeNewOrUpdate(JSONObject jsonObject) throws NotInitializedDBException, MissingIngredientPumpException {
        return makeNewOrUpdate(jsonObject.optString("liquid"),jsonObject.optInt("volume") );
    }


    //Bluetooth

    /**
     *clean with     {"cmd": "clean", "user": 0}
     * @param activity
     */
    static void clean(Activity activity){
        //TODO: clean
        //
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("cmd", "clean");
            cmd.put("user", AdminRights.getUserId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * send new Pump with Bluetooth
     ### define_pump: fügt Pumpe zu ESP hinzu
     - user: User
     - liquid: str
     - volume: float
     - slot: int

     JSON-Beispiel:

     {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}
     */
    default void sendSave(Context context){
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "define_pump");
            request.put("user", AdminRights.getUserId());
            request.put("liquid", this.getIngredientName());
            request.put("volume", this.getVolume());
            request.put( "slot", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO: send define pump
        JSONObject answer = new JSONObject();
    }

    /**
     * TODO: sendRefill
     ### refill_pump: füllt Pumpe auf
     - user: User
     - liquid: str
     - slot: int

     JSON-Beispiel:

     {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}
     */
    default void sendRefill(Context context){
        //TODO: refillPump
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "refill_pump");
            request.put("user", AdminRights.getUserId());
            request.put("volume", this.getVolume());
            request.put( "slot", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO: send refillPump
        JSONObject answer = new JSONObject();
    }

    /**
     ### run_pump: lässt die Pumpe für eine bestimmte Zeit laufen
     - user: User
     - slot: int
     - time: int

     Die Zeit wird in Millisekunden angegeben.

     JSON-Beispiel:

     {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}
     */
    default void run(Context context, int time){
        //TODO: runPump, when needed???
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "run_pump");
            request.put("user", AdminRights.getUserId());
            request.put( "slot", 1);
            request.put("volume", time);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO: send runPump
        JSONObject answer = new JSONObject();
    }

    static void calibrate(Context context){
        //TODO calibrate
    }

    /**
     * ask for liquid status to update db with availability
     * @param context
     */
    static void sync(Context context){
        //TODO sync
    }






    //general

    /**
     * Static access to pumps.
     * Get available pumps.
     * @return pumps
     */
     static List<Pump> getPumps() {
        try {
            return DatabaseConnection.getDataBase().getPumps();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Static access to pumps.
     * Get available pump with id k
     * @param id pump id k
     * @return
     */
    static Pump getPump(long id) {
        try {
            return DatabaseConnection.getDataBase().getPump(id);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }







}
