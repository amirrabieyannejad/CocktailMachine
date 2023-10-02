package com.example.cocktailmachine.data;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.Buffer;
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
    //300ml/min
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

    /**
     * get volume in pump
     * @author Johanna Reidt
     * @return
     */
    int getVolume();

    /**
     * set volume
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void fill(int volume) throws MissingIngredientPumpException;

    /**
     * set minimum volume
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void setMinimumPumpVolume( int volume);

    /**
     * empty pump
     * no volume
     * no ingredient
     * @author Johanna Reidt
     * @param context
     */
    void empty(Context context);

    /**
     * set volume
     * and save
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void fill(Context context,int volume) throws MissingIngredientPumpException;

    /**
     * set and save min volume
     *
     * @param volume
     * @throws MissingIngredientPumpException
     */
    void setMinimumPumpVolume(Context context, int volume);


    //Slot

    /**
     * get slot number
     * @author Johanna Reidt
     * @return
     */
    int getSlot();




    //Ingredient

    /**
     * get ingredient name or "Keine Zutat"
     * @author Johanna Reidt
     * @return
     */
    String getIngredientName();
    /**
     * get ingredient name or "Keine Zutat"
     * @author Johanna Reidt
     * @return
     */
    String getIngredientName(Context context);

    /**
     * Return current ingredient set in pump.
     *
     * @return current ingredient
     */
    Ingredient getCurrentIngredient();


    /**
     * Update ingredient in pump. and save
     *
     * @param ingredient next ingredient.
     */
    default void setCurrentIngredient(Context context, Ingredient ingredient) {
        setCurrentIngredient(context,ingredient.getID());
    }

    /**
     * Update ingredient in pump. and save
     *
     * @param id id of next ingredient
     */
    void setCurrentIngredient(Context context,long id);

    void preSetIngredient(long id);




    //DATA BASE Stuff


    /**
     * only use after db loading to connect pump and ingredient
     *
     * @param ingredientPump
     */
    void setIngredientPump(Context context,SQLIngredientPump ingredientPump);



    /**
     * Set up k empty pumps.
     *
     * @param numberOfPumps k
     */
    static void setOverrideEmptyPumps(Context context, int numberOfPumps) {
        Log.i(TAG, "setOverrideEmptyPumps");
        Buffer.loadForSetUp(context);
        for (int i = 0; i < numberOfPumps; i++) {
            Pump pump = makeNew();
            pump.setSlot(i);
            pump.save(context);
            Log.i(TAG, "setOverrideEmptyPumps: made Pump "+i);
        }
    }

    void setSlot(int i);


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
    static Pump makeNewOrUpdate(Context context, String liquidName, int volume)
            throws MissingIngredientPumpException, NotInitializedDBException {
        Ingredient ingredient = Ingredient.getIngredient(liquidName);
        if (ingredient == null) {
            ingredient = Ingredient.makeNew(liquidName);
            ingredient.save(context);
        }
        if (ingredient.getPump() == null) {
            Pump pump = makeNew();
            pump.setCurrentIngredient(context, ingredient);
            pump.fill(volume);
        } else {
            ingredient.getPump().fill(volume);
        }
        ingredient.save(context);
        ingredient.getPump().save(context);

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
    static void updatePumpStatus(Context context, JSONObject json) {
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
                        makeNewOrUpdate(context,
                                Objects.requireNonNull(
                                        json.optJSONObject(
                                                t_ids.getString(i)))));
            }
            for (Pump toDelete : toDeletePumps) {
                toDelete.delete(context);
            }
            Buffer.localRefresh(context);
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
    static void updateLiquidStatus(Context context, JSONObject json) throws JSONException, NotInitializedDBException, MissingIngredientPumpException {
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
                    makeNewOrUpdate(context,
                            t_names.optString(i),
                            json.optInt(t_names.optString(i))));
        }
        for (Pump toDelete : toDeletePumps) {
            toDelete.delete(context);
        }
        Buffer.localRefresh(context);
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
    static Pump makeNewOrUpdate(Context context,JSONObject jsonObject) throws NotInitializedDBException, MissingIngredientPumpException {
        return makeNewOrUpdate(context,jsonObject.optString("liquid"), jsonObject.optInt("volume"));
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
        save(activity);
        if(Dummy.isDummy){
            return;
        }
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
                    this.getSlot(),activity);
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
                    this.getSlot(),activity);
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
            BluetoothSingleton.getInstance().adminManuelCalibrateRunPump(
                    this.getSlot(),
                    time,activity);
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
            BluetoothSingleton.getInstance().adminManuelCalibratePump(
                    this.getSlot(),
                    time1,
                    time2,
                    volume1,
                    volume2,activity);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    default void pumpTimes(Activity activity) {
        GetDialog.calibratePumpTimes(activity,
                this);
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
     * {"cmd": "set_pump_times", "user": 0, "slot": 1, "time_init": 1000, "time_reverse": 1000,
     * "rate": 1.0}
     */
    default void sendPumpTimes(Activity activity, int timeInit, int timeReverse, float rate) {
        //TO  DO: setPumpTimes
        //TO DO: AMIR

        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateSetPumpTimes(this.getSlot(),
                    timeInit, timeReverse, rate,activity);
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
        Buffer.localRefresh(activity);
    }

    /**
     * read pump status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readPumpStatus(Activity activity) {
        try {
            BluetoothSingleton.getInstance().adminReadPumpsStatus(activity);
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
            BluetoothSingleton.getInstance().adminReadLiquidsStatus(activity);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void calibratePumpsAndTimes(Activity activity) {
        //Anzahl
        //Setze alle gleicher maßen
        //Kalibrarierung
        //Skalierung
        //Zutat

        GetDialog.calibrateAllPumpsAndTimes(activity);
        //GetDialog.calibrateAllPumpTimes(activity);
    }




    //general access to db to all pumps

    /**
     * Static access to pumps.
     * Get available pumps.
     *
     * @return pumps
     */
    static List<Pump> getPumps() {
        return Buffer.getSingleton().getPumps();
    }

    /**
     * get pumps if necessary from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    static List<Pump> getPumps(Context context){
        return Buffer.getSingleton(context).getPumps();
    }

    /**
     * Static access to pumps.
     * Get available pump with id k
     *
     * @param id pump id k
     * @return
     */
    static Pump getPump(long id) {
        return Buffer.getSingleton().getPump(id);
    }
    /**
     * Static access to pumps.
     * Get available pump with id k
     *
     * @param id pump id k
     * @return
     */
    static Pump getPump(Context context, long id) {
        return Buffer.getSingleton(context).getPump(id);
    }

    static Pump getPumpWithSlot(int slot) {
        return Buffer.getSingleton().getPumpWithSlot(slot);
    }
    static Pump getPumpWithSlot(Context context, int slot) {
        return Buffer.getSingleton(context).getPumpWithSlot(slot);
    }




}