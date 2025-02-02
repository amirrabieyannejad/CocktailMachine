package com.example.cocktailmachine.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.enums.CalibrateStatus;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class CocktailMachine {
    private static final String TAG = "CocktailMachine";
    //TO DO: AMIR


    //FOR SENDING TO Bluetooth

    static int time;
    static boolean dbChanged = false;
    //static Recipe currentRecipe;

    public static LinkedHashMap<Ingredient, Integer> current;
    //
    static int currentUser = -1;
    static Recipe currentRecipe;
    static List<Integer> queue;

    static double currentWeight;


    public static int dummyCounter=0;






    //Calibration


    //private static final String TAG = "CocktailMachineCalibr" ;
    private static CocktailMachine singleton = null;
    private boolean isDone = false;
    private CocktailMachine() {}

    public static CocktailMachine getSingleton() {
        if(singleton == null){
            singleton = new CocktailMachine();
        }

        return singleton;
    }

    public void start(Activity activity) {
        //TO DO: force of automation -> ???
        //TODO: wait dialog ??? besonderes neu Kalibrierung
        Log.v(TAG, "start");
        //Dialog wait = GetDialog.loadingBluetooth(activity);
        //wait.show();
        ExtraHandlingDB.loadForSetUp(activity);
        Log.v(TAG, "start: loaded db");
        //wait.cancel();
        AdminRights.login(activity, activity.getLayoutInflater(), dialog -> {
            Log.v(TAG, "start: login canceling");

            //Dialog wait_blue = GetDialog.loadingBluetooth(activity);
            //wait_blue.show();
            AdminRights.initUser(activity, String.valueOf(new Random().nextInt()), new Postexecute() {
                @Override
                public void post() {
                    Log.v(TAG, "start: login init");
                    if(CocktailMachine.isCocktailMachineSet(activity)){
                        Log.v(TAG, "start: login isCocktailMachineSet");
                        isDone = true;
                        Log.v(CocktailMachine.TAG, "start: is set");
                        Toast.makeText(activity, "Cocktailmaschine ist bereit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    GetActivity.waitNotSet(activity);
                    /*
                    if(AdminRights.isAdmin()){
                        Log.v(TAG, "start: is admin");
                        wait_blue.cancel();
                        GetDialog.startAutomaticCalibration(activity);
                    }else{
                        Log.v(TAG, "start: is user");
                        wait_blue.cancel();
                    }
                     */
                }
            });
            //Pump.calibratePumpsAndTimes(activity);
            /*
            for(Pump p: Pump.getPumps()){
                GetDialog.setPumpIngredient(activity, p, true, true);
            }
             */

        });

    }

    /**
     * true if pumps in cocktailmachine calibrated and ready to mix
     * @author Johanna Reidt
     * @return
     */
    public boolean isIsDone() {
        Log.v(TAG, "isIsDone: "+isDone);
        return isDone;
    }


    public void askIsDone(Activity activity, Postexecute postexecute){


        if(Dummy.isDummy){
            postexecute.post();
            return;
        }
        Dialog wait = GetDialog.loadingBluetooth(activity);
        wait.show();

        try {
            BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, new Postexecute() {
                @Override
                public void post() {
                    wait.cancel();
                    postexecute.post();
                }
            });
        } catch (JSONException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.v(TAG, "Error is triggert in CocktailMachineCalibration.askIsDone ");
            GetDialog.errorStatus(activity, e);
        };
    }

    public void setIsDone(boolean isDone) {
        Log.v(TAG, "start: setIsDone");
        this.isDone = isDone;
        if(isDone){
            Dummy.withSetCalibration = true;
        }
        Log.v(TAG, "start: isDone: "+isDone);
    }


    //Calibration














    //Settings von Bluetoothseite

    /**
     * set recognized recipe
     * @param currentRecipe
     * @author Johanna Reidt
     */
    public static void setCurrentRecipe(Recipe currentRecipe) {
        Log.i(TAG, "setCurrentRecipe");
        CocktailMachine.currentRecipe = currentRecipe;
        Log.i(TAG, "setCurrentRecipe: "+currentRecipe);
    }

    public static Recipe getCurrentRecipe(){
        return CocktailMachine.currentRecipe;
    }


    /**
     * set weigth on scale
     * @param weight
     * @author Johanna Reidt
     */
    public static void setCurrentWeight(JSONObject weight) {
        Log.i(TAG, "setCurrentWeight");
        try {
            currentWeight = weight.getDouble("weight");
            Log.i(TAG, "setCurrentWeight : done");
        } catch (JSONException e) {
            Log.i(TAG, "setCurrentWeight: failed");
            Log.e(TAG, "error",e);
            Log.e(TAG, "error", e);
            currentWeight = -1.0;
        }
        Log.i(TAG, "setCurrentWeight: "+currentWeight);
    }


    /**
     * sets current Cocktail status (what ingredients and how much of ech is already in the used glas)
     * @author Johanna Reidt
     * @param jsonObject
     */
    public static void setCurrentCocktail(Context context, JSONObject jsonObject) {
        Log.i(TAG, "setCurrentCocktail");
        /**
         * {"weight": 500.0, "content": [["beer", 250], ["lemonade", 250]]}
         *  {"weight": 500.0, "content": [["beer", 250], ["lemonade", 250]]}
         */
        //current
        Log.i(TAG, "setCurrentCocktail: "+jsonObject.toString());
        try {
            JSONArray array = jsonObject.getJSONArray("content");
            Log.i(TAG, "setCurrentCocktail: content"+array.toString());
            current = new LinkedHashMap<Ingredient, Integer>();
            for(int i=0;i< array.length(); i++) {
                JSONArray temp = array.getJSONArray(i);
                Log.i(TAG, "setCurrentCocktail: elm "+temp.toString());
                Ingredient ingredient = Ingredient.getIngredient(context, temp.getString(0));
                Log.i(TAG, "setCurrentCocktail: ing "+ingredient.toString());
                int vol = temp.getInt(1);
                Log.i(TAG, "setCurrentCocktail: vol "+vol);
                current.put(ingredient, vol);
            }
            Log.i(TAG, "setCurrentCocktail: current "+current);

            currentWeight = jsonObject.getDouble("weight");
            Log.i(TAG, "setCurrentCocktail: current weight "+currentWeight);
        } catch (JSONException e) {
            Log.i(TAG, "setCurrentCocktail failed");
            Log.e(TAG, "error",e);
            Log.e(TAG, "error", e);
        }

    }


    // TO DO: Johanna change to array setCurrentUser is now queueUser DONE
    /**
     * set current users and the queue
     * in the queue the first element is the current user
     * @author Johanna Reidt
     * @param res
     */
    public static void setCurrentUser(String res) {
        Log.i(TAG, "setCurrentUser");
        Log.i(TAG, "setCurrentUser: res: "+res);
        res = res.replace("[","");
        res = res.replace("]","");
        Log.i(TAG, "setCurrentUser: res(filtered) : "+res);
        if(res.length()==0){
            queue = new LinkedList<>();
            currentUser = -1;
        }
        String[] users = res.split(",");
        queue = new LinkedList<Integer>();
        for (String number : users) {
            try {
                queue.add(Integer.parseInt(number.trim()));
            }catch (NumberFormatException e){
                Log.e(TAG, "setCurrentUser error for "+number, e);
            }
        }
        if(!queue.isEmpty()) {
            currentUser = queue.get(0);
        }else {
            currentUser = -1;
        }
        Log.i(TAG, "setCurrentUser: queue: "+queue);
        Log.i(TAG, "setCurrentUser: currentUser: "+currentUser);
    }


    /**
     * sets timestamp from ESP-DB's last change
     * if it is different from saved timestamp, set dbChanged=true
     * @author Johanna Reidt
     * @param n
     */
    public static void setLastChange(String n){
        Log.i(TAG, "setLastChange");
        int oldTime = time;
        time = Integer.parseInt(n);
        if(oldTime<time){
            dbChanged = true;
        }
        Log.i(TAG, "setLastChange: oldTime: "+oldTime);
        Log.i(TAG, "setLastChange: time: "+time);
        Log.i(TAG, "setLastChange: dbChanged: "+dbChanged);
    }











    //StatusAbfragen

    /**
     * last measured weight on scale
     * @author Johanna Reidt
     * @return
     */
    public static double getCurrentWeight(){
        return currentWeight;
    }

    /**
     * weight on scale
     * @return
     * @author Johanna Reidt
     */
    public static double getCurrentWeight(Activity activity) {
        Log.i(TAG, "getCurrentWeight");
        if(Dummy.isDummy){
            Log.i(TAG, "getCurrentWeight: dummy");
            if(current == null){
                current = new LinkedHashMap<>();
            }
            if(currentRecipe != null){
                for(Ingredient i: currentRecipe.getIngredients(activity)){
                    if(!current.containsKey(i)){
                        current.put(i, currentRecipe.getVolume(activity,i));
                        Log.i(TAG, "getCurrentWeight: dummy: add ing: "+i);
                        break;
                    }
                }
            }
            Log.i(TAG, "getCurrentWeight: dummy: "+current);
            int g = 0;
            for(Integer v : current.values()) {
                if(v!= null) {
                    g = g + v;
                }
            }
            currentWeight = g;
            return currentWeight;
        }
        try {
            BluetoothSingleton.getInstance().adminReadScaleStatus(activity);
            Log.i(TAG,"getCurrentWeight: success");
        } catch (JSONException | InterruptedException|NullPointerException  e) {
            Log.i(TAG,"getCurrentWeight: error");
            Log.e(TAG, "error", e);
            //Log.e(TAG, "error: "+e);
        }
        Log.i(TAG, "getCurrentWeight: "+currentWeight);
        return currentWeight;
    }

    /**
     * weight on scale and do afterwards
     *
     * @param activity
     * @param postexecute
     * @author Johanna Reidt
     */
    public static void getCurrentWeight(Activity activity, Postexecute postexecute){

        Log.i(TAG, "getCurrentWeight");
        if(Dummy.isDummy){
            Log.i(TAG, "getCurrentWeight: dummy");
            if(current == null){
                current = new LinkedHashMap<>();
            }
            if(currentRecipe != null){
                for(Ingredient i: currentRecipe.getIngredients(activity)){
                    if(!current.containsKey(i)){
                        current.put(i, currentRecipe.getVolume(activity,i));
                        Log.i(TAG, "getCurrentWeight: dummy: add ing: "+i);
                        break;
                    }
                }
            }
            Log.i(TAG, "getCurrentWeight: dummy: "+current);
            int g = 0;
            for(Integer v : current.values()) {
                if(v!= null) {
                    g = g + v;
                }
            }
            currentWeight = g;
            postexecute.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadScaleStatus(activity, postexecute);
            Log.i(TAG,"getCurrentWeight: success");
        } catch (JSONException | InterruptedException|NullPointerException  e) {
            Log.i(TAG,"getCurrentWeight: error");
            Log.e(TAG, "error", e);
            //Log.e(TAG, "error: "+e);
        }
        Log.i(TAG, "getCurrentWeight: "+currentWeight);
    }


    /**
     * check if cocktailmachine is set,
     * if not set, do post notSet
     * if set, do post set
     *
     * @param notSet task to do, if not set
     * @param set    task to do, if set
     * @author Johanna Reidt
     */
    public static void isCocktailMachineSet(Postexecute notSet,
                                            Postexecute set,
                                            Activity activity){
        Log.i(TAG, "isCocktailMachineSet");

        if(Dummy.isDummy){
            Log.i(TAG, "isCocktailMachineSet: dummy");
            if(CocktailMachine.getSingleton().isIsDone()){
                Log.i(TAG, "isCocktailMachineSet: dummy. SET");
                set.post();
                return;
            }
            Log.i(TAG, "isCocktailMachineSet: dummy. NOT SET");
            notSet.post();
            return;
        }
        //TO DO: add bluetooth /esp implementation

        try{
            BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, new Postexecute(){
                @Override
                public void post() {
                    Log.i(TAG, "isCocktailMachineSet: post");
                    if(CocktailMachine.getSingleton().isIsDone()){
                        Log.i(TAG, "isCocktailMachineSet: SET");
                        set.post();
                    }else{
                        Log.i(TAG, "isCocktailMachineSet: NOT SET");
                        notSet.post();
                    }
                }
            });
            Log.i(TAG, "isCocktailMachineSet: done");
        } catch (JSONException | SQLiteException | InterruptedException | NullPointerException e) {
            Log.i(TAG, "isCocktailMachineSet: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
        //Pump.getPumps(activity).size();
        //return r.nextDouble() >= 0.5;
        //return false;
    }
    /**
     *check if cocktail machine isset
     * @return
     * @author Johanna Reidt
     */
    public static boolean isCocktailMachineSet(Activity activity){
        Log.i(TAG, "isCocktailMachineSet");

        if(Dummy.isDummy){
            Log.i(TAG, "isCocktailMachineSet: dummy");
            return CocktailMachine.getSingleton().isIsDone();
        }else{
            try {
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity);
                Log.i(TAG, "isCocktailMachineSet: done");
            } catch (JSONException | InterruptedException |NullPointerException e) {
                Log.i(TAG, "isCocktailMachineSet: failed");
                //Log.e(TAG, "error: "+e);
                Log.e(TAG, "error", e);
            }
            return CocktailMachine.getSingleton().isIsDone();
        }
        //return r.nextDouble() >= 0.5;
        //return false;
    }











    //SCALE


    /**
     ### tare_scale: tariert die Waage
     - user: User

     JSON-Beispiel:

     {"cmd": "tare_scale", "user": 0}
     */
    public static void tareScale(Activity activity){
        Log.i(TAG, "tareScale");
        Dialog wait = GetDialog.loadingBluetooth(activity);
        wait.show();

        Log.i(TAG, "tareScale: tare");
        tareScale(activity, new Postexecute() {
            @Override
            public void post() {
                wait.dismiss();
                Log.i(TAG, "tareScale: wait done");
            }
        });
    }
    /**
     ### tare_scale: tariert die Waage
     - user: User

     JSON-Beispiel:

     {"cmd": "tare_scale", "user": 0}
     */
    public static void tareScale(Activity activity, Postexecute postexecute){
        Log.i(TAG, "tareScale");
        Toast.makeText(activity, "Tarierung der Waage eingeleitet.",Toast.LENGTH_SHORT).show();
        //TO DO: tareScale
        if(Dummy.isDummy){
            postexecute.post();
            Log.i(TAG, "tareScale: post done");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateTareScale(activity, postexecute);
            Log.i(TAG, "tareScale: done");
        } catch (JSONException | InterruptedException |NullPointerException e) {
            Log.e(TAG, "error", e);
            Log.i(TAG, "tareScale failed");
            Toast.makeText(activity, "Die Tarierung ist fehlgeschlagen!",Toast.LENGTH_SHORT).show();
            postexecute.post();
            Log.i(TAG, "tareScale: post done");
        }
    }

    /**
     * send to esp with bluetooth
     ### calibrate_scale: kalibriert die Waage
     - user: User
     - weight: float

     Das Gewicht wird in Milligramm angegeben.

     JSON-Beispiel:

     {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     */
    public static void sendCalibrateScale(Activity activity, float weight, Postexecute postexecute){
        Log.i(TAG, "sendCalibrateScale");
        Toast.makeText(activity, "Kalibrierung der Waage mit Gewicht: "+weight+" g",Toast.LENGTH_SHORT).show();
        //TO DO: calibrateScale
        if(Dummy.isDummy){
            Log.i(TAG, "sendCalibrateScale: dummy");
            postexecute.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateScale(weight,activity, postexecute);
            Log.i(TAG, "sendCalibrateScale: done");
        } catch (JSONException | InterruptedException  |NullPointerException e) {
            Log.i(TAG, "sendCalibrateScale: failed");
            Log.e(TAG, "error"+ e);
            Log.e(TAG, "error", e);
            Toast.makeText(activity, "Die Kalibrierung ist fehlgeschlagen!",Toast.LENGTH_SHORT).show();
            postexecute.post();
        }
    }

    /**
     * calibrate Scale with Dialog view
     * @author Johanna Reidt
     * @param activity
     */
    public static void calibrateScale(Activity activity){
        Log.i(TAG, "calibrateScale");
        //TO DO: calibrateScale
        //TO DO get Weight with dialog
        GetDialog.calibrateScale(activity);

    }

    /**
     *
     * starts Dialog to set scale factor.
     * @author Johanna Reidt
     * @param activity
     */
    public static void scaleFactor(Activity activity){
        Log.i(TAG, "scaleFactor");
        //TO DO: setScaleFactor
        //TO DO get factor with dialog
        GetDialog.calibrateScaleFactor(activity);

    }

    /**
     * send scalefactor to ESP
     * @author Johanna Reidt
     * @param activity
     * @param factor
     */
    public static void sendScaleFactor(Activity activity, Float factor){
        Log.i(TAG, "sendScaleFactor");
        Toast.makeText(activity, "Skalierung der Waage mit Faktor: "+factor,Toast.LENGTH_SHORT).show();
        AlertDialog wait = GetDialog.loadingBluetooth(activity);
        wait.show();
        Postexecute postexecute = new Postexecute() {
            @Override
            public void post() {
                wait.dismiss();
            }
        };
        if(Dummy.isDummy){
            postexecute.post();
            return;
        }
        //TO DO: send scale factor
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateSetScaleFactor(factor,activity, postexecute);
            Log.i(TAG, "sendScaleFactor done");
        } catch (JSONException | InterruptedException |NullPointerException e) {
            Log.i(TAG, "sendScaleFactor failed");
            Log.e(TAG, "error", e);
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
            GetDialog.errorStatus(activity, e);
            postexecute.post();
        }
    }














    //DB

    /**
     * get LastChange timestamp  from ESP-DB
     * @author Johanna Reidt
     */
    public static void getLastChange(Activity activity, Postexecute postexecute){
        Log.i(TAG, "getLastChange");
        if(Dummy.isDummy){
            Log.i(TAG, "getLastChange: dummy");
            dbChanged = false;
            postexecute.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadLastChange(activity, postexecute);
            Log.i(TAG, "getLastChange: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "getLastChange failed");
            Log.e(TAG, "error"+ e);
            Log.e(TAG, "error", e);
            postexecute.post();
        }
    }

    /**
     * update DB If Cange in ESP-DB
     * @author Johanna Reidt
     */
    public static void updateRecipeListIfChanged(Activity activity, Postexecute postexecute){
        Log.i(TAG, "updateRecipeListIfChanged");
        if(Dummy.isDummy){
            Log.i(TAG, "updateRecipeListIfChanged: dummy");
            postexecute.post();
            // in post execute drin: ExtraHandlingDB.localRefresh(activity);
            Log.i(TAG, "updateRecipeListIfChanged: dummy: post done");
            return;
        }
        getLastChange(activity, new Postexecute() {
            @Override
            public void post() {
                if(dbChanged){
                    try {
                        BluetoothSingleton.getInstance().adminReadRecipesStatus(activity, postexecute);
                        Log.i(TAG, "updateRecipeListIfChanged: done");
                    } catch (JSONException | InterruptedException|NullPointerException e) {
                        Log.i(TAG, "updateRecipeListIfChanged failed");
                        Log.e(TAG, "error"+ e);
                        Log.e(TAG, "error", e);
                        postexecute.post();
                    }
                }else{
                    postexecute.post();
                }
            }
        });

    }































    //Mix Recipe

    /**
     * queues a recipe for mixing
     * @author Johanna Reidt
     * @param recipe
     */
    public static void queueRecipe(Recipe recipe , Activity activity, Postexecute continueHere, Postexecute errorHandle){
        Log.i(TAG,  "queueRecipe");
        currentRecipe = recipe;
        if(Dummy.isDummy){
            Log.i(TAG,  "queueRecipe: Dummy queue");
            continueHere.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(),
                    recipe.getName(),
                    activity,
                    continueHere,
                    errorHandle
                    );
            Log.i(TAG,  "queueRecipe: queued");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG,  "queueRecipe: error");
            Log.e(TAG, "error"+ e);
            Log.e(TAG, "error", e);
            errorHandle.post();
        }
    }

    /**
     * reads the queue
     * @author Johanna Reidt
     */
    public static void readUserQueue(Activity activity){
        Log.i(TAG,  "readUserQueue");
        if(Dummy.isDummy){
            Log.i(TAG,  "readUserQueue: Dummy queue");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadUserQueue(activity);
            //BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(), recipe.getName());
            Log.i(TAG,  "readUserQueue: queued");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG,  "readUserQueue: error");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
    }

    /**
     * number of users before this one in the queue
     * @author Johanna Reidt
     * @return
     */
    public static int getNumberOfUsersUntilThisUsersTurn(Activity activity) {
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn");
        /*TO DO:
        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.makeRecipe(this.getName());
        } catch (JSONException | InterruptedException e) {
            Log.e(TAG, "error), e
;        }h

         */
        readUserQueue(activity);
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: readUserQueue done");
        if(queue != null) {
            if(queue.isEmpty()){
                Toast.makeText(activity, "Anfrage hat den ESP nicht erreicht!", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return queue.indexOf(AdminRights.getUserId());
        }
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: queue is still none");
        return 10;
    }

    /**
     * number of users before this one in the queue
     * @author Johanna Reidt
     * @return
     */
    public static int getNumberOfUsersUntilThisUsersTurn(Activity activity, int tick) {
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn");
        if(Dummy.isDummy){
            Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: dummy");
            return tick - 1;
        }
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: bluetooth");
        return getNumberOfUsersUntilThisUsersTurn(activity);
    }


    /**
     * check if user is the current one
     * @author Johanna Reidt
     * @return
     */
    public static boolean isCurrentUser(Activity activity){
        Log.i(TAG, "isCurrentUser");
        readUserQueue(activity);
        Log.i(TAG, "isCurrentUser: readUserQueue done");
        return (currentUser==AdminRights.getUserId());
    }

    /**
     *
     * @author Johanna Reidt
     * @param activity
     * @param errorHandle
     * @param continueHere
     */
    public static void startMixing(Activity activity, Postexecute errorHandle, Postexecute continueHere){
        Log.i(TAG,  "startMixing");
        if(Dummy.isDummy){
            Log.i(TAG,  "startMixing: Dummy");
            continueHere.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().userStartRecipe(AdminRights.getUserId(),activity, errorHandle, continueHere);
            //BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(), recipe.getName());
            Log.i(TAG,  "startMixing: started");
        } catch (JSONException | InterruptedException |NullPointerException e) {
            Log.i(TAG,  "startMixing: error");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
    }


    /**
     * get status of ingredient and volume in glas of the current mixing cocktail
     * @author Johanna Reidt
     * @return hashmap ingredient to filled volume in glas
     */
    public static LinkedHashMap<Ingredient, Integer> getCurrentCocktailStatus(
            Postexecute postexecute,
            Activity activity){

        Log.i(TAG, "getCurrentCocktailStatus");
        if(isCurrentUser(activity)) {
            Log.i(TAG, "getCurrentCocktailStatus: this users turn");
            if(Dummy.isDummy){
                Log.i(TAG, "getCurrentCocktailStatus : dummy");
                if(current == null){
                    current = new LinkedHashMap<>();
                    Log.i(TAG, "getCurrentCocktailStatus : current null");
                }
                if(currentRecipe == null){
                    Log.e(TAG, "getCurrentCocktailStatus : no current recipe: ERROR");
                    return current;
                }
                CocktailStatus.setStatus(CocktailStatus.mixing);
                Log.i(TAG, "getCurrentCocktailStatus :  status to mixing");
                for(Ingredient i: currentRecipe.getIngredients(activity)){
                    if(!current.containsKey(i)){
                        Log.i(TAG, "getCurrentCocktailStatus :  adding another ingredient and volume:");
                        current.put(i, currentRecipe.getVolume(activity,i));
                        Log.i(TAG, "getCurrentCocktailStatus :  ingredient: "+i);
                        Log.i(TAG, "getCurrentCocktailStatus :  current: "+current);
                        Log.i(TAG, "getCurrentCocktailStatus :  return");
                        return current;
                    }
                }
                Log.i(TAG, "getCurrentCocktailStatus :  all ingredients in current list");
                Log.i(TAG, "getCurrentCocktailStatus :  current: "+current);
                CocktailStatus.setStatus(CocktailStatus.cocktail_done);
                Log.i(TAG, "getCurrentCocktailStatus :  status to cocktail done");
                return current;
            }
            try {
                BluetoothSingleton.getInstance().adminReadCurrentCocktail(activity, postexecute);
                Log.i(TAG, "getCurrentCocktailStatus done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG,  "getCurrentCocktailStatus: error");
                //Log.e(TAG, "error: "+e);
                Log.e(TAG, "error", e);
                //count down
            }
        }else{
            Log.i(TAG, "getCurrentCocktailStatus: NOT this users turn");
            current = new LinkedHashMap<>();
        }
        return current;
    }


    /**
     * checks if the current status is "mixing"
     * @author Johanna Reidt
     * @return
     */
    public static boolean isMixing(Activity activity){
        Log.i(TAG, "isMixing");

        /*
        if(currentRecipe == null){
            return false;
        }
        LinkedHashMap<Ingredient, Integer> temp =
                CocktailMachine.getCurrentCocktailStatus(activity);
        if(currentRecipe.getIngredients().size()==temp.size()) {
            for(Ingredient i:temp.keySet() ) {
                try {
                    int t = temp.get(i);
                    if(0!=Integer.compare(currentRecipe.getSpecificIngredientVolume(i),t)){
                        return false;
                    }
                } catch (TooManyTimesSettedIngredientEcxception | NoSuchIngredientSettedException e) {
                    Log.e(TAG, "error), e
;                    return false;
                }
            }
            return true;
        }
        return false;

         */
        return CocktailStatus.getCurrentStatus(activity)==CocktailStatus.mixing;
    }

    /**
     *
     * @return
     */
    public static boolean isPumping(Activity activity){
        Log.i(TAG, "isPumping");

        /*
        if(currentRecipe == null){
            return false;
        }
        LinkedHashMap<Ingredient, Integer> temp =
                CocktailMachine.getCurrentCocktailStatus(activity);
        if(currentRecipe.getIngredients().size()==temp.size()) {
            for(Ingredient i:temp.keySet() ) {
                try {
                    int t = temp.get(i);
                    if(0!=Integer.compare(currentRecipe.getSpecificIngredientVolume(i),t)){
                        return false;
                    }
                } catch (TooManyTimesSettedIngredientEcxception | NoSuchIngredientSettedException e) {
                    Log.e(TAG, "error), e
;                    return false;
                }
            }
            return true;
        }
        return false;

         */
        return CocktailStatus.getCurrentStatus(activity)==CocktailStatus.pumping;
    }


    /**
     * checks if cocktails mixing is done
     * @author Johanna Reidt
     */
    public static boolean isFinished(Activity activity){
        Log.i(TAG, "isFinished");

        /*
        if(currentRecipe == null){
            return false;
        }
        LinkedHashMap<Ingredient, Integer> temp =
                CocktailMachine.getCurrentCocktailStatus(activity);
        if(currentRecipe.getIngredients().size()==temp.size()) {
            for(Ingredient i:temp.keySet() ) {
                try {
                    int t = temp.get(i);
                    if(0!=Integer.compare(currentRecipe.getSpecificIngredientVolume(i),t)){
                        return false;
                    }
                } catch (TooManyTimesSettedIngredientEcxception | NoSuchIngredientSettedException e) {
                    Log.e(TAG, "error), e
;                    return false;
                }
            }
            return true;
        }
        return false;

         */
        return CocktailStatus.getCurrentStatus(activity)==CocktailStatus.cocktail_done;
    }


    /**
     * send to esp that cocktail is taken
     * @author Johanna Reidt
     * @param activity
     */
    public static void takeCocktail(Activity activity, Postexecute errorHandle, Postexecute continueHere){
        Log.i(TAG,  "takeCocktail");
        if(Dummy.isDummy){
            Log.i(TAG,  "takeCocktail: Dummy taken");
            CocktailStatus.setStatus(CocktailStatus.ready);
            continueHere.post();
            Log.i(TAG,  "takeCocktail: status to ready");
            return;
        }
        try {
            BluetoothSingleton.getInstance().userTakeCocktail(AdminRights.getUserId(),activity, continueHere, errorHandle);
            Log.i(TAG,  "takeCocktail: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG,  "takeCocktail: error");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
            Toast.makeText(activity, "FEHLER", Toast.LENGTH_SHORT).show();
        }
    }















    //Calibrations


    /**
     * call auto calibration start
     * @author Johanna Reidt
     */
    public static void automaticCalibration(Activity activity, Postexecute continueHere, Postexecute errorHandle){
        Log.i(TAG, "automaticCalibration");

        /**
         * a.	Bitte erst wasser beim ersten Durchgang
         * b.	Tarierung
         * c.	Gefässe mit 100 ml Wasser
         * d.	Pumpenanzahl
         * e.	Automatische Kalibrierung
         * f.	Warten auf fertig
         * g.	Angabe von Zutaten
         */
        //TO DO: call bluetooth
        if(!Dummy.isDummy){
            try {
                BluetoothSingleton.getInstance().adminAutoCalibrateStart(activity, continueHere, errorHandle);
                Log.i(TAG,  "automaticCalibration: done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.e(TAG,"automaticCalibration. FAILED");
                //Log.e(TAG, "error: "+e);
                Log.e(TAG, "error", e);
            }
            return;
        }
        Log.i(TAG, "automaticCalibration. DUMMY");
        CalibrateStatus.setStatus(CalibrateStatus.ready);
        Log.i(TAG, "automaticCalibration. status to ready");
    }

    /**
     * a help function for the vm mode
     * @author Johanna Reidt
     * @param activity
     */
    public static void tickDummy(Activity activity){
        Log.i(TAG, "tickDummy");
        if(Dummy.isDummy) {
            if(dummyCounter<0){
                Log.i(TAG, "tickDummy: dummycounter = 0");
                dummyCounter = 0;
                Log.i(TAG, "tickDummy: pumpe.size = "+Pump.getPumps(activity).size());
            }
            //return new Random(42).nextBoolean();
            if( dummyCounter==Pump.getPumps(activity).size()){
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_calculation){
                    CocktailMachine.getSingleton().setIsDone(true);
                    Log.i(TAG, "tickDummy: CocktailMachineCalibration.setIsDone(true)");
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_done);
                    Log.i(TAG, "tickDummy: Dummy: calibration_calculation->calibration_done => fertig done ");
                    Log.i(TAG, "tickDummy: Dummy: FERITG");
                    return;
                }else{
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_calculation);
                    Log.i(TAG, "tickDummy: Dummy: dummyCounter, alle Pumpen fertig -> calibration_calculation  ");
                    Log.i(TAG, "tickDummy: Dummy: ");
                    return;
                }
            } else if (dummyCounter<Pump.getPumps(activity).size()) {
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_pumps){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
                    Log.i(TAG, "tickDummy: Dummy: calibration_pumps -> calibration_empty_container");
                    Log.i(TAG,"tickDummy: dummyCounter : "+dummyCounter);
                    dummyCounter ++;
                    Log.i(TAG,"tickDummy: dummyCounter +1: "+dummyCounter);
                }else if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
                    Log.i(TAG, "tickDummy: Dummy: calibration_empty_container -> calibration_pumps");
                    //dummyCounter = dummyCounter+1;
                    Log.i(TAG,"tickDummy: dummyCounter +1: "+dummyCounter);
                }else{
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
                    Log.i(TAG, "tickDummy: ??? -> calibration_pumps");
                }
                //dummyCounter++;
                Log.i(TAG, "tickDummy added: "+dummyCounter);
                return;
            }
            Log.i(TAG, "tickDummy: dummycounter = 0");
            dummyCounter = 0;
            Log.i(TAG, "tickDummy: pumpe.size = "+Pump.getPumps(activity).size());
            CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
            Log.i(TAG, "tickDummy: ??? -> calibration_pumps");
        }
    }

    /**
     * check if calibration is done
     * @return
     * @author Johanna Reidt
     */
    public static boolean isAutomaticCalibrationDone(Activity activity){
        Log.i(TAG, "isAutomaticCalibrationDone");
        //TO DO: bluetooth connection

        return CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_done;

    }

    /**
     * checks if admin has to empty the glass to continue the calibration process
     * @return
     * @author Johanna Reidt
     */
    public static boolean needsEmptyingGlass(Activity activity) {
        Log.i(TAG, "needsEmptyingGlass");
        return CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container;
    }


    /**
     * check if calibration is done
     * @return
     * @author Johanna Reidt
     */
    public static boolean isAutomaticCalibrationDone(){
        Log.i(TAG, "isAutomaticCalibrationDone");
        //TO DO: bluetooth connection

        return CalibrateStatus.getCurrent()==CalibrateStatus.calibration_done;

    }

    /**
     * checks if admin has to empty the glass to continue the calibration process
     * @return
     * @author Johanna Reidt
     */
    public static boolean needsEmptyingGlass() {
        Log.i(TAG, "needsEmptyingGlass");
        return CalibrateStatus.getCurrent()==CalibrateStatus.calibration_empty_container;
    }


    /**
     * tells the esp that user hsa emptied the glass
     * @author Johanna Reidt
     */
    /*
    public static void automaticEmpty(Activity activity){
        try{
            BluetoothSingleton.getInstance().adminAutoCalibrateAddEmpty(activity);
        } catch (JSONException | InterruptedException|NullPointerException e) {
            //throw new RuntimeException(e);
            Log.e(TAG,"automaticEmpty");
            Log.e(TAG, "error: )+e);
            Log.e(TAG, "error), e
;        }
    }

     */


    /**
     * tells esp that the glass with 100 ml water has been places
     * @author Johanna Reidt
     */
    public static void automaticWeight(Activity activity, Postexecute continueHere, Postexecute errorHandle){
        Log.i(TAG, "automaticWeight");
        //tickDummy(activity);
        if(Dummy.isDummy){
            CalibrateStatus.setStatus(CalibrateStatus.calibration_known_weight);
            continueHere.post();
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddWeight(100, activity, continueHere, errorHandle);
            Log.e(TAG, "automaticWeight: done");
            return;
        } catch (JSONException | InterruptedException | NullPointerException e) {
            //throw new RuntimeException(e);
            Log.e(TAG, "automaticWeight: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
            return;
        }

        //CalibrateStatus.setStatus(CalibrateStatus.calibration_known_weight);
    }

    /**
     * empty glass is ready
     * @author Johanna Reidt
     */
    public static void automaticEmptyGlass(Activity activity, Postexecute continueHere, Postexecute errorHandle){
        Log.i(TAG, "automaticEmptyPumping");
        if(Dummy.isDummy) {
            Log.i(TAG, "automaticEmptyPumping: dummy");
            continueHere.post();
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddEmpty(activity, continueHere, errorHandle);
            Log.e(TAG, "automaticEmptyPumping: done");
        } catch (JSONException | InterruptedException | NullPointerException e) {

            //throw new RuntimeException(e);
            Log.e(TAG, "automaticEmptyPumping: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
    }



    /**
     * empty glass is ready
     * @author Johanna Reidt
     */
    public static void automaticEmptyPumping(Activity activity, Postexecute continueHere, Postexecute errorHandle){
        Log.i(TAG, "automaticEmptyPumping");
        if(Dummy.isDummy) {
            Log.i(TAG, "automaticEmptyPumping: dummy: "+dummyCounter);
            dummyCounter += 1;
            Log.i(TAG, "automaticEmptyPumping: dummyCounter + 1: "+dummyCounter);
            tickDummy(activity);
            Log.i(TAG, "automaticEmptyPumping: tickDummy done ");
            //CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
            //Log.i(TAG,"automaticEmptyPumping "+dummyCounter );
            continueHere.post();
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddEmpty(activity, continueHere, errorHandle);
            Log.e(TAG, "automaticEmptyPumping: done");
        } catch (JSONException | InterruptedException | NullPointerException e) {

            //throw new RuntimeException(e);
            Log.e(TAG, "automaticEmptyPumping: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
    }


    /**
     * last call for calibration
     * @author Johanna Reidt
     */
    public static void automaticEnd(Activity activity,
                                    Postexecute continueHere,
                                    Postexecute errorHandle){
        Log.i(TAG, "automaticEnd");
        if(!Dummy.isDummy) {
            try {
                BluetoothSingleton.getInstance().adminAutoCalibrateFinish(activity, continueHere, errorHandle);
                Log.e(TAG, "automaticEnd: done");
            } catch (JSONException | InterruptedException | NullPointerException e) {

                //throw new RuntimeException(e);
                Log.e(TAG, "automaticEnd: failed");
                //Log.e(TAG, "error: "+e);
                Log.e(TAG, "error", e);
            }
            return;
        }
        Log.e(TAG, "automaticEnd: dummy");
        tickDummy(activity);
        continueHere.post();
        Log.e(TAG, "automaticEnd: tickDummy done");
        //CalibrateStatus.setStatus(CalibrateStatus.calibration_done);
    }











    //Maintaince of cocktailmachine


    /**
     ### clean: reinigt die Maschine
     - user: User

     JSON-Beispiel:

     {"cmd": "clean", "user": 0}
     */
    /**
     * start cleaning sequence
     * @param activity
     * @author Johanna Reidt
     */
    public static void clean(Activity activity){
        Log.i(TAG, "clean");
        if(Dummy.isDummy){
            Log.i(TAG, "clean: dummy");
            Toast.makeText(activity,"Die Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
            return;
        }
        //CocktailMachine.clean(activity);
        try {
            BluetoothSingleton.getInstance().adminClean(activity, new Postexecute() {
                @Override
                public void post() {
                    Toast.makeText(activity,"Die Reinigung wurde beendet!", Toast.LENGTH_SHORT).show();

                }
            });
            Toast.makeText(activity, "Die Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "clean: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "clean: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
            Toast.makeText(activity,
                    "Der Reinigungsablauf konnten NICHT gestartet werden.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     ### restart: startet die Maschine neu
     - user: User

     JSON-Beispiel:

     {"cmd": "restart", "user": 0}
     */
    /**
     * restart of the cocktailmachine
     * @param activity
     * @author Johanna Reidt
     */
    public static void restart(Activity activity){
        Log.i(TAG, "restart");
        //TO DO: restart
        Toast.makeText(activity,
                "Die Cocktailmaschine wird neugestartet.",
                Toast.LENGTH_SHORT).show();
        if(Dummy.isDummy){
            Log.i(TAG, "restart: dummy");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminRestart(activity);
            Log.i(TAG, "restart: done");

        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "restart: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
            Toast.makeText(activity,
                    "Die Cocktailmaschine konnten NICHT neugestartet werden.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     ### factory_reset: setzt alle Einstellungen zurück
     - user: User

     JSON-Beispiel:

     {"cmd": "factory_reset", "user": 0}


     */
    /**
     * factory Reset for the cocktailmachine
     *
     * @param activity
     * @author Johanna Reidt
     */
    public static void factoryReset(Activity activity){
        Log.i(TAG, "factoryReset");
        //TO DO: factoryReset
        Toast.makeText(activity,
                "Die Cocktailmachine wird auf Werkseinstellungen zurückgesetzt.",
                Toast.LENGTH_SHORT).show();
        //DeleteFromDB.removeAll(activity);
        //Log.i(TAG, "factoryReset: db complete delete");
        if(Dummy.isDummy){
            Log.i(TAG, "factoryReset: dummy");
            Toast.makeText(activity,"Erfolgreich zurückgesetzt!", Toast.LENGTH_SHORT).show();
            CocktailMachine.getSingleton().setIsDone(false);
            Log.i(TAG, "factoryReset: dummy: CocktailMachineCalibration.setIsDone(false)");
            GetActivity.waitNotSet(activity);
            Log.i(TAG, "factoryReset: dummy: clear stack, go to menu, all pumps empty, all recipes deleted");
        }


        try {
            BluetoothSingleton.getInstance().adminFactoryReset(activity,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity,"Erfolgreich zurückgesetzt!", Toast.LENGTH_SHORT).show();
                            CocktailMachine.getSingleton().setIsDone(false);
                            Log.i(TAG, "factoryReset: CocktailMachineCalibration.setIsDone(false)");
                            GetActivity.waitNotSet(activity);
                            Log.i(TAG, "factoryReset: clear stack, go to menu, all pumps empty, all recipes deleted");
                        }
                    });
            Log.i(TAG, "factoryReset: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "factoryReset: failed");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
            Toast.makeText(activity,
                    "Werkseinstellungen konnten NICHT wieder hergestellt werden.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * reset CocktailMachine,  stops cocktail mixing
     * @author Johanna Reidt
     * @param activity
     */
    public static void reset(Activity activity){
        Log.i(TAG, "reset");
        if(Dummy.isDummy){
            Log.i(TAG, "reset: dummy");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReset(activity, new Postexecute(){
                @Override
                public void post() {
                    Log.i(TAG, "reset: post");
                    Toast.makeText(activity, "Reset erledigt!",Toast.LENGTH_SHORT).show();
                }

            });
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Toast.makeText(activity, "Reset ist fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "Reset stored error");
            //Log.e(TAG, "error: "+e);
            Log.e(TAG, "error", e);
        }
    }






    /**
     * TO DO: find usage
     *
     * to be called if cocktail done and new Cocktail should be mixed
     * reset: reset the machine so that it can make a new cocktail
     * JSON-sample: {"cmd": "reset", "user": 9650}
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.

     * @author Johanna Reidt
     * @param acti vity
     */
    /*
    public static void reset(Activity activity){
        //TO DO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            Log.e(TAG, "error), e
;        }
    }

     */

    public static class AdminRights {
        private static final String TAG = "AdminRights";
        public static final String PASSWORD = "admin";
        private static AdminRights singleton = null;

        private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
        private int userId = 2;

        private AdminRights(){
        }

        public static AdminRights getSingleton(){
            Log.i(TAG, "getSingleton");
            if(singleton == null){
                singleton = new AdminRights();
            }
            return singleton;
        }

        //USer ID handling
        public static int getUserId(){
            Log.i(TAG, "getUserId");
            //TO DO: USE THIS AMIR *OK*
            return getSingleton().userId;
        }
        public static void setUserId(int userId){
            Log.i(TAG, "setUserId");
            getSingleton().userId = userId;
            Log.i(TAG, "setUserId: userId");
        }

        /**
         * set user wit {"user": 4}
         * @author Johanna Reidt
         * @param jsonObject
         */
        public static void setUser(JSONObject jsonObject){
            Log.i(TAG, "setUser");
            //TO DO: USE THIS AMIR **DONE**

            if(jsonObject == null){
                Log.w(TAG, "setUser: jsonObject null");
                return;
            }

            try {
                setUserId(jsonObject.getInt("user"));
                Log.i(TAG, "setUser: done");
            }catch (NumberFormatException | JSONException e){
                Log.i(TAG, "setUser: failed");
                Log.e(TAG, "error: ",e);
                setUserId(-1);
            }
        }

        /**
         * get JSON Object to init user
         * @return
         */
        private static JSONObject getUserIdAsMessage(){
            Log.i(TAG, "getUserIdAsMessage");

            JSONObject json = new JSONObject();
            try {
                json.put("name", String.valueOf(System.currentTimeMillis()));
                json.put("cmd", "init_user");
                Log.i(TAG, "getUserIdAsMessage: done");
            } catch (JSONException e) {
                Log.i(TAG, "getUserIdAsMessage: failed");
                Log.e(TAG, "error: ",e);
                ////Log.e(TAG, "error", e);
            }
            return json;
        }

        /**
         * init user with bluetooth
         * @return
         */
        public static void initUser(Activity activity, String name){
            //TO DO: DUMMY
            Log.i(TAG, "initUser");
            if(Dummy.isDummy) {
                Log.i(TAG, "initUser dummy");
                getSingleton().userId = -1;
                return;
            }
            try{
                BluetoothSingleton.getInstance().userInitUser(name,activity);
                Log.i(TAG, "initUser done");
            } catch (JSONException | InterruptedException e) {
                //throw new RuntimeException(e);
                Log.i(TAG, "init User failed");
                Log.e(TAG, "error",e);
                //Log.e(TAG, "error", e);
            }
        }

        /**
         * init user with bluetooth
         * @return
         */
        public static void initUser(Activity activity, String name, Postexecute postexecute){
            //TO DO: DUMMY
            Log.i(TAG, "initUser");
            if(Dummy.isDummy) {
                Log.i(TAG, "initUser dummy");
                getSingleton().userId = 3;
                postexecute.post();
                return;
            }
            try{
                BluetoothSingleton.getInstance().userInitUser(name,activity, postexecute);
                Log.i(TAG, "initUser done");
            } catch (JSONException | InterruptedException e) {
                //throw new RuntimeException(e);
                Log.i(TAG, "initUser failed");
                Log.e(TAG, "error",e);
                //Log.e(TAG, "error", e);
            }
        }

        /**
         * {"cmd": "abort", "user": 483}
         * @return
         */
        private static JSONObject getUserAbortMessage(){
            Log.i(TAG, "getUserAbortMessage");
            JSONObject json = new JSONObject();
            try {
                json.put("cmd", "abort");
                json.put("user", getUserId());
                Log.i(TAG, "getUserAbortMessage done");
            } catch (JSONException e) {
                Log.i(TAG, "getUserAbortMessage User failed");
                Log.e(TAG, "error",e);
                //Log.e(TAG, "error", e);
            }
            return json;
        }


        static void saveMCAdresse(Context context){

            BluetoothSingleton.getInstance().getEspDeviceAddress();
        }

        static String loadMCAdresseFromDB(Context context){
            return "";
        }





        //Admin/ User status
        public static UserPrivilegeLevel getUserPrivilegeLevel(){
            Log.i(TAG, "getUserPrivilegeLevel");
            return getSingleton().privilege;
        }

        public static void setUserPrivilegeLevel(UserPrivilegeLevel privilege){
            Log.i(TAG, "setUserPrivilegeLevel");
            getSingleton().privilege = privilege;
            Log.i(TAG, "setUserPrivilegeLevel: "+privilege);
        }

        public static boolean isAdmin(){
            Log.i(TAG, "isAdmin");
            return getUserPrivilegeLevel().equals(UserPrivilegeLevel.Admin);
        }

        public static void login(Context getContext,
                                 LayoutInflater getLayoutInflater,
                                 DialogInterface.OnDismissListener dismissListener){
            Log.i(TAG, "login" );

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext);
            builder.setTitle("Login");
            View v = getLayoutInflater.inflate(R.layout.layout_login, null);
            LoginView loginView = new LoginView(getContext, v);

            builder.setView(v);
            builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "login: Weiter");
                    if(loginView.check()){
                        Log.i(TAG, "login: admin");
                        AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
                        Toast.makeText(getContext,"Eingeloggt!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "login: abbruch");
                }
            });
            builder.setOnDismissListener(dismissListener);
            builder.show();
        }

        public static void logout(){
            Log.i(TAG, "logout");
            AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.User);
            //Toast.makeText(getContext,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
        }


        private static class LoginView{
            private final TextView t;
            private final EditText e;
            private final View v;
            public LoginView(Context context, View v) {
                Log.i(TAG, "LoginView");
                this.v = v;
                t = v.findViewById(R.id.textView_edit_text);
                e = v.findViewById(R.id.editText_edit_text);
                e.setHint("");
                t.setText("Passwort: ");
                e.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            }
            public boolean check(){
                Log.i(TAG, "LoginView: check");
                return e.getText().toString().equals("admin");
            }


        }
    }
}

