package com.example.cocktailmachine.data;


import android.app.Activity;
import android.util.Log;

import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.ui.model.v2.GetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface Pump extends Comparable<Pump>, DataBaseElement {
    String TAG = "Pump";

    /**
     * Get Id.
     *
     * @return
     */
    long getID();



    //Volume

    /**
     * Returns milliliters pumped in milliseconds.
     * aka minimum available pump size
     *
     * @return minimum milliliters to be pumped
     */
    int getMinimumPumpVolume();

    int getVolume();

    void empty();

    /**
     * set volume
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void fill(int volume) throws MissingIngredientPumpException;

    /**
     * set volume
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void setMinimumPumpVolume(int volume);


    //Slot

    void setSlot(int slot);

    int getSlot();




    //Ingredient
    String getIngredientName();

    /**
     * Return current ingredient set in pump.
     *
     * @return current ingredient
     */
    Ingredient getCurrentIngredient();

    /**
     * Update ingredient in pump.
     *
     * @param ingredient next ingredient.
     */
    default void setCurrentIngredient(Ingredient ingredient) {
        setCurrentIngredient(ingredient.getID());
    }

    /**
     * Update ingredient in pump.
     *
     * @param id id of next ingredient
     */
    void setCurrentIngredient(long id);





    //DATA BASE Stuff

    /**
     * only use after db loading to connect pump and ingredient
     *
     * @param ingredientPump
     */
    void setIngredientPump(SQLIngredientPump ingredientPump);


    /**
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     */
    static void loadFromDB() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }


    /**
     * Set up k empty pumps.
     *
     * @param numberOfPumps k
     */
    static List<Pump> setOverrideEmptyPumps(int numberOfPumps) {
        try {
            DatabaseConnection.getDataBase().loadForSetUp();
            for (int i = 0; i < numberOfPumps; i++) {
                Pump pump = makeNew();
                pump.save();
            }
            return DatabaseConnection.getDataBase().getPumps();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    static Pump makeNew() {
        return new SQLPump();
    }

    /**
     * update pump with ingredient name and volume
     *
     * @param liquidName
     * @param volume
     * @return pump
     * @throws MissingIngredientPumpException
     * @throws NotInitializedDBException
     */
    static Pump makeNewOrUpdate(String liquidName, int volume)
            throws MissingIngredientPumpException, NotInitializedDBException {
        Ingredient ingredient = Ingredient.getIngredient(liquidName);
        if (ingredient == null) {
            ingredient = Ingredient.makeNew(liquidName);
            ingredient.save();
        }
        if (ingredient.getPump() == null) {
            Pump pump = makeNew();
            pump.setCurrentIngredient(ingredient);
            pump.fill(volume);
        } else {
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
     *
     * @return
     * @throws JSONException
     */
    default JSONObject asMesssage() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(this.getIngredientName(), this.getVolume());
        return json;
    }

    /**
     * {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     *
     * @return {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static JSONObject getLiquidStatus() throws JSONException, NotInitializedDBException {
        JSONObject json = new JSONObject();
        List<Pump> pumps = Pump.getPumps();
        for (Pump p : pumps) {
            json.put(p.getIngredientName(), p.getVolume());
        }
        return json;
    }

    /**
     * {"1": {"liquid": "lemonade", "volume": 200}}
     *
     * @return {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static JSONObject getPumpStatus() throws JSONException, NotInitializedDBException {
        JSONObject json = new JSONObject();
        List<Pump> pumps = Pump.getPumps();
        for (int i = 1; i <= pumps.size(); i++) {
            Pump p = pumps.get(i);
            JSONObject temp = new JSONObject();
            temp.put(p.getIngredientName(), p.getVolume());
            json.put(String.valueOf(i), temp);
        }
        return json;
    }




    //reading json objects

    /**
     * {"1": {"liquid": "lemonade", "volume": 200}}
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     *
     * @param json
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static void updatePumpStatus(JSONObject json) {
        Log.i(TAG, "updatePumpStatus");
        //TO DO: USE THIS AMIR
        try {
            JSONArray t_ids = json.names();
            if (t_ids == null) {
                throw new JSONException("Pump IDs not readable.");
            }
            List<Pump> toDeletePumps = Pump.getPumps();
            for (int i = 0; i < t_ids.length(); i++) {
                toDeletePumps.remove(
                        makeNewOrUpdate(
                                Objects.requireNonNull(
                                        json.optJSONObject(
                                                t_ids.getString(i)))));
            }
            for (Pump toDelete : toDeletePumps) {
                toDelete.delete();
            }
            DatabaseConnection.getDataBase().loadBufferWithAvailable();
        } catch (NotInitializedDBException | JSONException | MissingIngredientPumpException e) {
            e.printStackTrace();
        }

    }

    /**
     * {"beer": 200, "lemonade": 2000, "orange juice": 2000} Flüssigkeiten Status
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     *
     * @param json
     * @throws JSONException
     * @throws NotInitializedDBException
     */
    static void updateLiquidStatus(JSONObject json) throws JSONException, NotInitializedDBException, MissingIngredientPumpException {
        Log.i(TAG, "updateLiquidStatus");
        //TO DO: USE THIS AMIR
        //List<Pump> pumps = DatabaseConnection.getDataBase().getPumps();
        JSONArray t_names = json.names();
        if (t_names == null) {
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
        for (int i = 0; i < t_names.length(); i++) {
            toDeletePumps.remove(
                    makeNewOrUpdate(
                            t_names.optString(i),
                            json.optInt(t_names.optString(i))));
        }
        for (Pump toDelete : toDeletePumps) {
            toDelete.delete();
        }
        DatabaseConnection.getDataBase().loadBufferWithAvailable();
    }

    /**
     * [["beer", 250], ["lemonade", 250]] current Mixing cocktail
     *
     * @param json
     * @throws JSONException
     * @throws NewlyEmptyIngredientException
     */
    static void currentMixingCocktail(JSONArray json) throws JSONException, NewlyEmptyIngredientException {
        //TO DO: USE THIS AMIR
        Log.i(TAG, "updatePumpStatus");
        int i = 0;
        JSONArray temp = json.optJSONArray(i);
        while (temp != null) {
            String name = temp.getString(0);
            int volume = temp.getInt(1);
            try {
                Ingredient.getIngredient(name).pump(volume);
            } catch (MissingIngredientPumpException e) {
                e.printStackTrace();
                Log.i(TAG, "updatePumpStatus: should not happen");
            }
            i++;
            temp = json.optJSONArray(i);
        }

    }

    /**
     * {"liquid": "lemonade", "volume": 200}
     *
     * @param jsonObject
     * @return
     */
    static Pump makeNewOrUpdate(JSONObject jsonObject) throws NotInitializedDBException, MissingIngredientPumpException {
        return makeNewOrUpdate(jsonObject.optString("liquid"), jsonObject.optInt("volume"));
    }





    //send with Bluetooth for a single pump

    /**
     * send new Pump with Bluetooth
     * ### define_pump: fügt Pumpe zu ESP hinzu
     * - user: User
     * - liquid: str
     * - volume: float
     * - slot: int
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}
     */
    default void sendSave(Activity activity) {
        /*
        //TO DO: AMIR
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
        //TO DO: send define pump
        JSONObject answer = new JSONObject();

         */

        try {
            BluetoothSingleton.getInstance().adminDefinePump(
                    this.getIngredientName(),
                    this.getVolume(),
                    this.getSlot());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendRefill
     * ### refill_pump: füllt Pumpe auf
     * - user: User
     * - liquid: str
     * - slot: int
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}
     *
     * @param activity
     * @param volume
     * @author Johanna Reidt
     */
    default void sendRefill(Activity activity, int volume) {
        //TO DO: AMIR
        //TO DO: refillPump
        /*
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "refill_pump");
            request.put("user", AdminRights.getUserId());
            request.put("volume", this.getVolume());
            request.put( "slot", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TO DO: send refillPump
        JSONObject answer = new JSONObject();

         */
        try {
            fill(volume);
        } catch (MissingIngredientPumpException e) {
            e.printStackTrace();
        }
        sendRefill(activity);
    }


    /**
     * sendRefill from volume already settted und updated in db
     * ### refill_pump: füllt Pumpe auf
     * - user: User
     * - liquid: str
     * - slot: int
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}
     *
     * @param activity
     * @author Johanna Reidt
     */
    default void sendRefill(Activity activity) {
        //TO DO: AMIR
        //TO DO: refillPump
        /*
        JSONObject request = new JSONObject();
        try {
            request.put("cmd", "refill_pump");
            request.put("user", AdminRights.getUserId());
            request.put("volume", this.getVolume());
            request.put( "slot", 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TO DO: send refillPump
        JSONObject answer = new JSONObject();

         */
        try {
            BluetoothSingleton.getInstance().adminRefillPump(this.getVolume(),
                    this.getSlot());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ### run_pump: lässt die Pumpe für eine bestimmte Zeit laufen
     * - user: User
     * - slot: int
     * - time: int
     * <p>
     * Die Zeit wird in Millisekunden angegeben.
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}
     */
    default void run(Activity activity, int time) {
        /*
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
        //TO DO: send runPump
        JSONObject answer = new JSONObject();
        //TO DO: AMIR

         */
        try {
            BluetoothSingleton.getInstance().adminRunPump(
                    this.getSlot(),
                    time);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    default void calibrate(Activity activity) {
        //TO do
        //calibrate(activity, time1, time2, volume1, volume2);
        GetDialog.calibratePump(activity, this);
    }

    /**
     * ### calibrate_pump: kalibriert die Pumpe mit vorhandenen Messwerten
     * - user: User
     * - slot: int
     * - time1: int
     * - time2: int
     * - volume1: float
     * - volume2: float
     * <p>
     * Zur Kalibrierung müssen zwei Messwerte vorliegen, bei denen die Pumpe für eine unterschiedliche Zeit gelaufen ist.
     * Daraus wird dann der Vorlauf und die Pumprate berechnet.
     * <p>
     * Die Zeiten werden in Millisekunden und die Flüssigkeiten in Milliliter angegeben.
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "calibrate_pump", "user": 0, "slot": 1, "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}
     *
     * @param activity
     * @author Johanna Reidt
     */
    default void sendCalibrate(Activity activity, int time1, int time2, float volume1, float volume2) {
        //TO DO calibrate
        //TO DO: AMIR
        try {
            BluetoothSingleton.getInstance().adminCalibratePump(
                    this.getSlot(),
                    time1,
                    time2,
                    volume1,
                    volume2);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    default void pumpTimes(Activity activity) {
        GetDialog.calibratePumpTimes(activity, this);
    }


    /**
     * ### set_pump_times: setzt die Kalibrierungswerte für eine Pumpe
     * - user: User
     * - slot: int
     * - time_init: int
     * - time_reverse: int
     * - rate: float
     * <p>
     * `time_init` ist die Vorlaufzeit und `time_init` die Rücklaufzeit in Millisekunden.
     * Normalerweise sollten diese Werte ähnlich oder gleich sein. Die Rate wird in mL/ms angegeben.
     * <p>
     * JSON-Beispiel:
     * <p>
     * {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000, "rate": 1.0}
     */
    public default void sendPumpTimes(Activity activity, int timeInit, int timeReverse, float rate) {
        //TO  DO: setPumpTimes
        //TO DO: AMIR

        try {
            BluetoothSingleton.getInstance().adminSetPumpTimes(this.getSlot(), timeInit, timeReverse, rate);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    //FOR ALL PUMPS

    /**
     * ask for pump status to update db with availability
     *
     * @param activity
     */
    static void sync(Activity activity) {
        //TO DO sync
        readPumpStatus(activity);
        DatabaseConnection.localRefresh();
    }

    /**
     * read pump status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readPumpStatus(Activity activity) {
        try {
            BluetoothSingleton.getInstance().adminReadPumpsStatus();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * read liquid status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readLiquidStatus(Activity activity) {

        try {
            BluetoothSingleton.getInstance().adminReadLiquidsStatus();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void calibratePumps(Activity activity) {
        //Anzahl
        //Setze alle gleicher maßen
        //Kalibrarierung
        //Skalierung
        //Zutat

        GetDialog.calibrateAllPumpTimes(activity);
    }





    //general access to db to all pumps

    /**
     * Static access to pumps.
     * Get available pumps.
     *
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
     *
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

    static Pump getPumpWithSlot(int slot) {

        try {
            return DatabaseConnection.getDataBase().getPumpWithSlot(slot);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return null;
        }
    }


}