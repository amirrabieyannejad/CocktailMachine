package com.example.cocktailmachine.data;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.db.GetFromDB;
import com.example.cocktailmachine.data.db.exceptions.NewlyEmptyIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.helper.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.list.AbstractLinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * get volume in pump
     * @author Johanna Reidt
     * @return
     */
    int getVolume(Context context);



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
    void fill(Context context, int volume) throws MissingIngredientPumpException;




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
    Ingredient getCurrentIngredient(Context context);


    /**
     * Update ingredient in pump. and save
     *
     * @param ingredient next ingredient.
     */
    default void setCurrentIngredient(Context context, Ingredient ingredient) {
        if (
                ingredient == null
        ) {
            return;
        }

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
     * @author Johanna Reidt
     * @param context
     * @param numberOfPumps k
     */
    static void setOverrideEmptyPumps(Activity context, int numberOfPumps, Postexecute postexecute) {
        Log.i(TAG, "setOverrideEmptyPumps");
        ExtraHandlingDB.loadForSetUp(context);
        Log.i(TAG, "setOverrideEmptyPumps "+numberOfPumps);

        for (int i = 1; i < numberOfPumps+1; i++) {

            Pump pump = Pump.makeNew();
            pump.setSlot(i);
            Ingredient water = Ingredient.searchOrNew(context,"Wasser");
            water.save(context);
            pump.setCurrentIngredient(context, water);
            pump.save(context);
            try {
                pump.fill(context, 100);
            } catch (MissingIngredientPumpException e) {
                Log.e(TAG, "setOverrideEmptyPumps", e);
            }

            pump.save(context);
            //pump.save(context);
            //pump.sendSave(context, postexecute);


            //Wait for ready
            // try 3 times with wait 1000 ms
/*

            final Postexecute doAfterWait = postexecute;
            final List<Boolean> count = new ArrayList<>();
            //final int count = 0;
            final int timesToTry= 3;

            postexecute = new Postexecute() {
                @Override
                public void post() {

                    Log.i(TAG,"setOverrideEmptyPumps: wait for ready status" );
                    CocktailStatus.getCurrentStatus(
                            new Postexecute() {
                                @Override
                                public void post() {
                                    if(Dummy.isDummy){
                                        Log.i(TAG,"setOverrideEmptyPumps: dummy" );
                                        doAfterWait.post();
                                        return;
                                    }

                                    Log.i(TAG,"setOverrideEmptyPumps: status received :"+CocktailStatus.getCurrentStatus() );
                                    if(CocktailStatus.getCurrentStatus() == CocktailStatus.ready) {
                                        Log.i(TAG,"setOverrideEmptyPumps: ready" );
                                        doAfterWait.post();
                                        return;
                                    } else if (count.size()<=timesToTry) {
                                        Log.i(TAG,"setOverrideEmptyPumps: tried times for ready status: "+count.size());
                                        count.add(false);
                                        try {
                                            Log.i(TAG,"setOverrideEmptyPumps: sleep");
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            Log.e(TAG, "error", e);
                                        }
                                        Log.i(TAG,"setOverrideEmptyPumps: post wait again");
                                        this.post();
                                        return;
                                    }
                                    Log.i(TAG,"setOverrideEmptyPumps: handle bluetooth/esp fail");
                                    GetDialog.handleBluetoothFailed(context);
                                }
                            }, context);
                }
            };


            //Send Save

            Postexecute nextStep = postexecute;
            postexecute = new Postexecute() {
                @Override
                public void post() {
                    Log.i(TAG,"setOverrideEmptyPumps: sendSave: Slot: "+pump.getSlot());
                    pump.sendSave(context, nextStep);
                }
            };

 */




            Log.i(TAG, "setOverrideEmptyPumps: made Pump "+i);
            Log.i(TAG, "setOverrideEmptyPumps: made Pump "+pump.toString());
            Log.i(TAG, "setOverrideEmptyPumps: control list len "+ getPumps(context).size() );
        }

        if(!Dummy.isDummy) {
            CocktailStatus.getCurrentStatus( new Postexecute() {
                @Override
                public void post() {
                    if(Dummy.isDummy){
                        postexecute.post();
                        return;
                    }
                    if(CocktailStatus.getCurrentStatus() == CocktailStatus.ready) {
                        try {
                            BluetoothSingleton.getInstance().adminDefinePumps(context,postexecute, "Wasser", 100, numberOfPumps);
                        } catch (JSONException | InterruptedException e) {
                            //throw new RuntimeException(e);
                            Log.e(TAG, "setOverrideEmptyPumps: send preset Pumps ", e);
                        }
                    }
                }
            }, context);

        }

        Log.i(TAG, "setOverrideEmptyPumps: control given len "+ numberOfPumps);
        Log.i(TAG, "setOverrideEmptyPumps: control list len "+ getPumps(context).size() );
    }

    void setSlot(int i);


    /**
     * creates a new Pump with out any information
     * @author Johanna Reidt
     * @return
     */
    static Pump makeNew() {
        Log.i(TAG, "makeNew");
        return new SQLPump();
    }

    /**
     * create or update pump with ingredient name and volume
     *
     * @param liquidName
     * @param volume
     * @return pump
     * @throws MissingIngredientPumpException
     * @throws NotInitializedDBException
     */
    static Pump makeNewOrUpdate(Context context, String liquidName, int volume)
            throws MissingIngredientPumpException, NotInitializedDBException {
        Ingredient ingredient = Ingredient.getIngredient(context,liquidName);
        if (ingredient == null) {
            ingredient = Ingredient.makeNew(liquidName);
            ingredient.save(context);
        }
        if (ingredient.getPump(context) == null) {
            Pump pump = makeNew();
            pump.setCurrentIngredient(context, ingredient);
            pump.fill(context,volume);
        } else {
            ingredient.getPump(context).fill(context,volume);
        }
        ingredient.save(context);
        ingredient.getPump(context).save(context);

        return ingredient.getPump(context);
    }






    //JSON Object

    // creation









    //reading json objects

    /*
    {"1":{"liquid":"water","volume":1000.0,"cal":[0.0,1000,1000]}
     */
    static void updatePumpStatus(Context context, JSONObject json){
        Log.i(TAG, "updatePumpStatus");
        Log.i(TAG, "updatePumpStatus: "+json.toString());
        List<Long> toSave = new ArrayList<>();
        try {
            Iterator<String> t_ids = json.keys();
            while (t_ids.hasNext()){
                String key = t_ids.next();
                JSONObject jsonTemp = json.getJSONObject(key);
                int slot = Integer.parseInt(key);
                int vol = (int) jsonTemp.getDouble("volume");
                Ingredient ingredient = Ingredient.searchOrNew(context, jsonTemp.getString("liquid"));
                /*
                try {
                    boolean calibrated = jsonTemp.getBoolean("calibrated");
                    if(!calibrated){
                        CocktailMachineCalibration.setIsDone(false);
                    }
                }catch(JSONException e){
                    Log.i(TAG, "updatePumpStatus: no calibrated" );
                }
                 */
                Pump pump = getPumpWithSlot(context,slot);
                if(pump == null){
                    pump = new SQLPump();
                }
                pump.setSlot(slot);
                //pump.setMinimumPumpVolume();
                pump.setCurrentIngredient(context, ingredient);
                pump.fill(context,vol);
                pump.save(context);
                toSave.add(pump.getID());
            }
            for (Pump p : getPumps(context)) {
                if (!toSave.contains(p.getID())) {
                    DeleteFromDB.remove(context, p);
                }
            }
            ExtraHandlingDB.localRefresh(context);
        } catch (JSONException | MissingIngredientPumpException e) {
            Log.e(TAG, "updatePumpStatus: error");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
        }
    }

    /**
     * {"1":{"liquid":"water","volume":1000.0,"calibrated":true,
     *      "rate":0.0,"time_init":1000,"time_reverse":1000},
     *  "2":{"liquid":"wodka","volume":1000.0,"calibrated":true,
     *       "rate":0.0,"time_init":1000,"time_reverse":1000}}
     * Set up pumps with ingredients.
     * Load buffer with status quo from data base.
     *
     * @param json
     */
    /*
    static void updatePumpStatus(Context context, JSONObject json) {
        Log.i(TAG, "updatePumpStatus");
        Log.i(TAG, "updatePumpStatus: "+json.toString());
        List<Long> toSave = new ArrayList<>();
        try {
            Iterator<String> t_ids = json.keys();
            while (t_ids.hasNext()){
                String key = t_ids.next();
                JSONObject jsonTemp = json.getJSONObject(key);
                int slot = Integer.parseInt(key);
                int vol = (int) jsonTemp.getDouble("volume");
                Ingredient ingredient = Ingredient.searchOrNew(context, jsonTemp.getString("liquid"));
                try {
                    boolean calibrated = jsonTemp.getBoolean("calibrated");
                    if(!calibrated){
                        CocktailMachineCalibration.setIsDone(false);
                    }
                }catch(JSONException e){
                    Log.i(TAG, "updatePumpStatus: no calibrated" );
                }
                Pump pump = getPumpWithSlot(context,slot);
                if(pump == null){
                    pump = new SQLPump();
                }
                pump.setSlot(slot);
                //pump.setMinimumPumpVolume();
                pump.setCurrentIngredient(context, ingredient);
                pump.fill(context,vol);
                pump.save(context);
                toSave.add(pump.getID());
            }
            for (Pump p : getPumps(context)) {
                if (!toSave.contains(p.getID())) {
                    DeleteFromDB.remove(context, p);
                }
            }
            ExtraHandlingDB.localRefresh(context);
        } catch (JSONException | MissingIngredientPumpException e) {
            Log.e(TAG, "updatePumpStatus: error");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
        }

    }

     */

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

        List<Pump> toDeletePumps = Pump.getPumps(context);
        for (int i = 0; i < t_names.length(); i++) {
            toDeletePumps.remove(
                    makeNewOrUpdate(context,
                            t_names.optString(i),
                            json.optInt(t_names.optString(i))));
        }
        for (Pump toDelete : toDeletePumps) {
            toDelete.delete(context);
        }
        ExtraHandlingDB.localRefresh(context);
    }

    /**
     * [["beer", 250], ["lemonade", 250]] current Mixing cocktail
     *
     * @param json
     * @throws JSONException
     * @throws NewlyEmptyIngredientException
     */
    static void currentMixingCocktail(Context context,JSONArray json) throws JSONException, NewlyEmptyIngredientException {
        //TO DO: USE THIS AMIR
        Log.i(TAG, "updatePumpStatus");
        int i = 0;
        JSONArray temp = json.optJSONArray(i);
        while (temp != null) {
            String name = temp.getString(0);
            int volume = temp.getInt(1);
            try {
                Ingredient.getIngredient(context,name).pump(volume);
            } catch (MissingIngredientPumpException e) {
                Log.i(TAG, "updatePumpStatus: should not happen");
                Log.e(TAG, "error ",e);
                Log.getStackTraceString(e);
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
                    activity,
                    this.getSlot(),
                    this.getIngredientName(activity),
                    this.getVolume(activity));
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "sendSave failed");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
        }
    }


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
    default void sendSave(Activity activity, Postexecute postexecute) {
        save(activity);
        if(Dummy.isDummy){
            postexecute.post();
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
                    activity,
                    this.getSlot(),
                    this.getIngredientName(activity),
                    this.getVolume(activity),
                    postexecute);
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "sendSave failed");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
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
    default void sendRefill(Activity activity, int volume, Postexecute postexecute) {
        try {
            fill(activity,volume);
        } catch (MissingIngredientPumpException e) {
            Log.i(TAG, "sendRefill failed");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
        }
        if(Dummy.isDummy){

            save(activity);
            postexecute.post();
            return;
        }
        save(activity);
        sendRefill(activity, postexecute);
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
            fill(activity,volume);
        } catch (MissingIngredientPumpException e) {
            Log.i(TAG, "sendRefill failed");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
        }
        if(Dummy.isDummy){
            save(activity);
            return;
        }
        save(activity);
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
        sendRefill(activity, new Postexecute() {
            @Override
            public void post() {
                Log.i(TAG, "Refill send done, no post");
            }
        });
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
    default void sendRefill(Activity activity, Postexecute postexecute) {

        if(!Dummy.isDummy) {
            try {
                BluetoothSingleton.getInstance().adminRefillPump(this.getVolume(activity),
                        this.getSlot(), activity, postexecute);
            } catch (JSONException | InterruptedException e) {
                Log.i(TAG, "sendRefill failed");
                Log.e(TAG, "error ",e);
                Log.getStackTraceString(e);
            }
        }
    }


    /**
     * Let pump run for given time
     * @author Johanna Reidt
     * @param activity
     * @param time
     */
    default void run(Activity activity, int time) {
        if(Dummy.isDummy){
            try {
                this.fill(activity, this.getVolume(activity)-time);
            } catch (MissingIngredientPumpException e) {
                Log.i(TAG, "run failed: MissingIngredientPumpException");
                Log.e(TAG, "error ",e);
                Log.getStackTraceString(e);
            }
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateRunPump(
                    this.getSlot(),
                    time,
                    activity,
                    new Postexecute(){
                        @Override
                        public void post() {
                            sync(activity);
                        }
                    });
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "run failed");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
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
        if(Dummy.isDummy){
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibratePump(
                    this.getSlot(),
                    time1,
                    time2,
                    volume1,
                    volume2,
                    activity);
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "sendCalibrate failed ");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
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
        if(Dummy.isDummy){
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateSetPumpTimes(this.getSlot(),
                    timeInit, timeReverse, rate,activity);
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "sendPumpTimes failed ");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
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
        readPumpStatus(activity, new Postexecute() {
            @Override
            public void post() {
                ExtraHandlingDB.localRefresh(activity);
            }
        });
    }

    /**
     * ask for pump status to update db with availability
     *
     * @param activity
     */
    static void sync(Activity activity, Postexecute postexecute) {
        //TO DO sync
        readPumpStatus(activity, new Postexecute() {
            @Override
            public void post() {
                ExtraHandlingDB.localRefresh(activity);
                postexecute.post();
            }
        });
    }

    /**
     * read pump status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readPumpStatus(Activity activity) {
        if(!Dummy.isDummy) {
            try {
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity);
            } catch (JSONException | InterruptedException e) {
                Log.i(TAG, "readPumpStatus failed ");
                Log.e(TAG, "error ",e);
                Log.getStackTraceString(e);
            }
        }
    }

    /**
     * read pump status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readPumpStatus(Activity activity, Postexecute postexecute) {
        if(!Dummy.isDummy) {
            try {
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, postexecute);
            } catch (JSONException | InterruptedException e) {
                Log.i(TAG, "readPumpStatus failed ");
                Log.e(TAG, "error ",e);
                Log.getStackTraceString(e);
            }
        }
    }
    /**
     * read liquid status without refresh of availability
     *
     * @param activity
     * @author Johanna Reidt
     */
    static void readLiquidStatus(Activity activity) {
        if(Dummy.isDummy){
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadLiquidsStatus(activity);
        } catch (JSONException | InterruptedException e) {
            Log.i(TAG, "readLiquidStatus failed ");
            Log.e(TAG, "error ",e);
            Log.getStackTraceString(e);
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
     * get pumps if necessary from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    static List<Pump> getPumps(Context context){
        return (List<Pump>) GetFromDB.getPumps(context);
    }


    static Iterator<List<SQLPump>> getChunkIterator(Context context, int n) {
        return GetFromDB.getPumpChunkIterator(context, n);
    }


    /**
     * Static access to pumps.
     * Get available pump with id k
     *
     * @param id pump id k
     * @return
     */
    static Pump getPump(Context context, long id) {
        return GetFromDB.getPump(context, id);
    }

    static Pump getPumpWithSlot(Context context, int slot) {
        return GetFromDB.getPumpWithSlot(context, slot);
    }




}