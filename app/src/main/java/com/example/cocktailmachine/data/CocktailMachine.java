package com.example.cocktailmachine.data;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.CalibrateStatus;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.v2.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.v2.GetActivity;
import com.example.cocktailmachine.ui.model.v2.GetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

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
            Log.e(TAG, "error"+e);
            e.printStackTrace();
            currentWeight = -1.0;
        }
        Log.i(TAG, "setCurrentWeight: "+currentWeight);
    }


    /**
     * sets current Cocktail status (what ingredients and how much of ech is already in the used glas)
     * @author Johanna Reidt
     * @param jsonObject
     */
    public static void setCurrentCocktail(JSONObject jsonObject) {
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
                Ingredient ingredient = Ingredient.getIngredient(temp.getString(0));
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
            Log.e(TAG, "error"+e);
            e.printStackTrace();
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
        res = res.replace("[","");
        res = res.replace("]","");
        if(res.length()==0){
            queue = new ArrayList<>();
            currentUser = -1;
        }
        String[] users = res.split(",");
        queue = new ArrayList<Integer>();
        for (String number : users) {
            queue.add(Integer.parseInt(number.trim()));
        }
        currentUser = queue.get(0);
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
                for(Ingredient i: currentRecipe.getIngredients()){
                    if(!current.containsKey(i)){
                        current.put(i, currentRecipe.getVolume(i));
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
            e.printStackTrace();
            Log.e(TAG, "error: "+e);
        }
        Log.i(TAG, "getCurrentWeight: "+currentWeight);
        return currentWeight;
    }


    public static double getCurrentWeight(Activity activity, Postexecute postexecute){

        Log.i(TAG, "getCurrentWeight");
        if(Dummy.isDummy){
            Log.i(TAG, "getCurrentWeight: dummy");
            if(current == null){
                current = new LinkedHashMap<>();
            }
            if(currentRecipe != null){
                for(Ingredient i: currentRecipe.getIngredients()){
                    if(!current.containsKey(i)){
                        current.put(i, currentRecipe.getVolume(i));
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
            return currentWeight;
        }
        try {
            BluetoothSingleton.getInstance().adminReadScaleStatus(activity, postexecute);
            Log.i(TAG,"getCurrentWeight: success");
        } catch (JSONException | InterruptedException|NullPointerException  e) {
            Log.i(TAG,"getCurrentWeight: error");
            e.printStackTrace();
            Log.e(TAG, "error: "+e);
        }
        Log.i(TAG, "getCurrentWeight: "+currentWeight);
        return currentWeight;
    }


    /**
     * check if cocktailmachine is set,
     * if not set, do post notSet
     * if set, do post set
     * @param notSet task to do, if not set
     * @param set task to do, if set
     * @return if setted status
     * @author Johanna Reidt
     */
    public static boolean isCocktailMachineSet(Postexecute notSet,
                                               Postexecute set,
                                               Activity activity){
        Log.i(TAG, "isCocktailMachineSet");

        if(Dummy.isDummy){
            Log.i(TAG, "isCocktailMachineSet: dummy");
            if( CocktailMachineCalibration.isIsDone()){
                Log.i(TAG, "isCocktailMachineSet: dummy. SET");
                set.post();
                return true;
            }
            Log.i(TAG, "isCocktailMachineSet: dummy. NOT SET");
            notSet.post();
            return false;
        }
        //TODO: add bluetooth /esp implementation
        try{
            BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, new Postexecute(){
                @Override
                public void post() {
                    Log.i(TAG, "isCocktailMachineSet: post");
                    if(Pump.getPumps().size()==0){
                        Log.i(TAG, "isCocktailMachineSet: NOT SET");
                        notSet.post();
                    }else{
                        Log.i(TAG, "isCocktailMachineSet: SET");
                        set.post();
                    }
                }
            });
            Log.i(TAG, "isCocktailMachineSet: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "isCocktailMachineSet: failed");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
        return Pump.getPumps().size()>0;
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
            return CocktailMachineCalibration.isIsDone();
        }else{
            try {
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity);
                Log.i(TAG, "isCocktailMachineSet: done");
            } catch (JSONException | InterruptedException |NullPointerException e) {
                Log.i(TAG, "isCocktailMachineSet: failed");
                Log.e(TAG, "error: "+e);
                e.printStackTrace();
            }
            return Pump.getPumps().size()>0;
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
        Toast.makeText(activity, "Tarierung der Waage eingeleitet.",Toast.LENGTH_SHORT).show();
        //TO DO: tareScale
        if(Dummy.isDummy){
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateTareScale(activity);
            Log.i(TAG, "tareScale: done");
        } catch (JSONException | InterruptedException |NullPointerException e) {
            e.printStackTrace();
            Log.i(TAG, "tareScale failed");
            Toast.makeText(activity, "Die Tarierung ist fehlgeschlagen!",Toast.LENGTH_SHORT).show();
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
    public static void sendCalibrateScale(Activity activity, float weight){
        Log.i(TAG, "sendCalibrateScale");
        Toast.makeText(activity, "Kalibrierung der Waage mit Gewicht: "+weight+" g",Toast.LENGTH_SHORT).show();
        //TO DO: calibrateScale
        if(Dummy.isDummy){
            Log.i(TAG, "sendCalibrateScale: dummy");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateScale(weight,activity);
            Log.i(TAG, "sendCalibrateScale: done");
        } catch (JSONException | InterruptedException  |NullPointerException e) {
            Log.i(TAG, "sendCalibrateScale: failed");
            Log.e(TAG, "error"+ e);
            e.printStackTrace();
            Toast.makeText(activity, "Die Kalibrierung ist fehlgeschlagen!",Toast.LENGTH_SHORT).show();
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
        if(Dummy.isDummy){
            return;
        }
        //TO DO: send scale factor
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateSetScaleFactor(factor,activity);
            Log.i(TAG, "sendScaleFactor done");
        } catch (JSONException | InterruptedException |NullPointerException e) {
            Log.i(TAG, "sendScaleFactor failed");
            Log.e(TAG, "error"+ e);
            e.printStackTrace();
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
        }
    }














    //DB

    /**
     * get LastChange timestamp  from ESP-DB
     * @author Johanna Reidt
     */
    public static void getLastChange(Activity activity){
        Log.i(TAG, "getLastChange");
        if(Dummy.isDummy){
            Log.i(TAG, "getLastChange: dummy");
            dbChanged = false;
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadLastChange(activity);
            Log.i(TAG, "getLastChange: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "getLastChange failed");
            Log.e(TAG, "error"+ e);
            e.printStackTrace();
        }
    }

    /**
     * update DB If Cange in ESP-DB
     * @author Johanna Reidt
     */
    public static void updateRecipeListIfChanged(Activity activity){
        Log.i(TAG, "updateRecipeListIfChanged");
        if(Dummy.isDummy){
            Log.i(TAG, "updateRecipeListIfChanged: dummy");
            Buffer.localRefresh(activity);
            Log.i(TAG, "updateRecipeListIfChanged: dummy: localRefresh done");
            return;
        }
        getLastChange(activity);
        if(dbChanged){
            try {
                BluetoothSingleton.getInstance().adminReadRecipesStatus(activity);
                Log.i(TAG, "updateRecipeListIfChanged: done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG, "updateRecipeListIfChanged failed");
                Log.e(TAG, "error"+ e);
                e.printStackTrace();
            }
        }
    }































    //Mix Recipe

    /**
     * queues a recipe for mixing
     * @author Johanna Reidt
     * @param recipe
     */
    public static void queueRecipe(Recipe recipe , Activity activity){
        Log.i(TAG,  "queueRecipe");
        if(Dummy.isDummy){
            Log.i(TAG,  "queueRecipe: Dummy queue");
            return;
        }
        try {
                BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId()
                        , recipe.getName(),activity);
                Log.i(TAG,  "queueRecipe: queued");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG,  "queueRecipe: error");
                Log.e(TAG, "error"+ e);
                e.printStackTrace();
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
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
    }

    /**
     * number of users before this one in the queue
     * @author Johanna Reidt
     * @return
     */
    public static int getNumberOfUsersUntilThisUsersTurn(Activity activity) {
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn");
        /*TODO:
        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.makeRecipe(this.getName());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }h

         */
        readUserQueue(activity);
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: readUserQueue done");
        return queue.indexOf(AdminRights.getUserId());
    }
    public static int getNumberOfUsersUntilThisUsersTurn(int tick){ //TODO: -> bluetooth
        Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn");
        /*TODO:
        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.makeRecipe(this.getName());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

         */
        if(Dummy.isDummy) {
            Log.i(TAG, "getNumberOfUsersUntilThisUsersTurn: dummy");
            return tick - 1;
        }
        return getNumberOfUsersUntilThisUsersTurn(tick);
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

    public static void startMixing(Activity activity){
        Log.i(TAG,  "startMixing");
        if(Dummy.isDummy){
            Log.i(TAG,  "startMixing: Dummy");
        }
        try {
                BluetoothSingleton.getInstance().userStartRecipe(AdminRights.getUserId(),activity);
                //BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(), recipe.getName());
                Log.i(TAG,  "startMixing: started");
            } catch (JSONException | InterruptedException |NullPointerException e) {
                Log.i(TAG,  "startMixing: error");
                Log.e(TAG, "error: "+e);
                e.printStackTrace();
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
                for(Ingredient i: currentRecipe.getIngredients()){
                    if(!current.containsKey(i)){
                        Log.i(TAG, "getCurrentCocktailStatus :  adding another ingredient and volume:");
                        current.put(i, currentRecipe.getVolume(i));
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
                Log.e(TAG, "error: "+e);
                e.printStackTrace();
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
                    e.printStackTrace();
                    return false;
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
                    e.printStackTrace();
                    return false;
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
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;

         */
        return CocktailStatus.getCurrentStatus(activity)==CocktailStatus.cocktail_done;
    }


    public static void takeCocktail(Activity activity){
        Log.i(TAG,  "takeCocktail");
        if(Dummy.isDummy){
            Log.i(TAG,  "takeCocktail: Dummy taken");
            CocktailStatus.setStatus(CocktailStatus.ready);
            Log.i(TAG,  "takeCocktail: status to ready");
        }
        try {
            BluetoothSingleton.getInstance().userTakeCocktail(AdminRights.getUserId(),activity);
            Log.i(TAG,  "takeCocktail: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG,  "takeCocktail: error");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
    }















    //Calibrations


    /**
     * call auto calibration start
     * @author Johanna Reidt
     */
    public static void automaticCalibration(Activity activity){
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
                BluetoothSingleton.getInstance().adminAutoCalibrateStart(activity);
                Log.i(TAG,  "automaticCalibration: done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.e(TAG,"automaticCalibration. FAILED");
                Log.e(TAG, "error: "+e);
                e.printStackTrace();
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
                Log.i(TAG, "tickDummy: pumpe.size = "+Pump.getPumps().size());
            }
            //return new Random(42).nextBoolean();
            if( dummyCounter==Pump.getPumps().size()){
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_calculation){
                    CocktailMachineCalibration.setIsDone(true);
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
            } else if (dummyCounter<Pump.getPumps().size()) {
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_pumps){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
                    Log.i(TAG, "tickDummy: Dummy: calibration_pumps -> calibration_empty_container");
                    Log.i(TAG,"tickDummy: dummyCounter schon: "+dummyCounter);
                }else if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
                    Log.i(TAG, "tickDummy: Dummy: calibration_empty_container -> calibration_pumps");
                    //dummyCounter = dummyCounter+1;
                    Log.i(TAG,"tickDummy: dummyCounter +1: "+dummyCounter);
                }else{
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
                    Log.i(TAG, "tickDummy: ??? -> calibration_pumps");
                }
                return;
            }
            Log.i(TAG, "tickDummy: dummycounter = 0");
            dummyCounter = 0;
            Log.i(TAG, "tickDummy: pumpe.size = "+Pump.getPumps().size());
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
            e.printStackTrace();
        }
    }

     */


    /**
     * tells esp that the glass with 100 ml water has been places
     * @author Johanna Reidt
     */
    public static void automaticWeight(Activity activity){
        Log.i(TAG, "automaticWeight");
        //tickDummy(activity);
        if(Dummy.isDummy){
            CalibrateStatus.setStatus(CalibrateStatus.calibration_known_weight);
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddWeight(100, activity);
            Log.e(TAG, "automaticWeight: done");
            return;
        } catch (JSONException | InterruptedException | NullPointerException e) {
            //throw new RuntimeException(e);
            Log.e(TAG, "automaticWeight: failed");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
            return;
        }

        //CalibrateStatus.setStatus(CalibrateStatus.calibration_known_weight);
    }


    /**
     * empty glass is ready
     * @author Johanna Reidt
     */
    public static void automaticEmptyPumping(Activity activity){
        Log.i(TAG, "automaticEmptyPumping");
        if(Dummy.isDummy) {
            Log.i(TAG, "automaticEmptyPumping: dummy");
            dummyCounter = dummyCounter + 1;
            Log.i(TAG, "automaticEmptyPumping: dummyCounter + 1: "+dummyCounter);
            tickDummy(activity);
            Log.i(TAG, "automaticEmptyPumping: tickDummy done ");
            //CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
            //Log.i(TAG,"automaticEmptyPumping "+dummyCounter );
            return;
        }

        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddEmpty(activity);
            Log.e(TAG, "automaticEmptyPumping: done");
        } catch (JSONException | InterruptedException | NullPointerException e) {

            //throw new RuntimeException(e);
            Log.e(TAG, "automaticEmptyPumping: failed");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
    }


    /**
     * last call for calibration
     * @author Johanna Reidt
     */
    public static void automaticEnd(Activity activity){
        Log.i(TAG, "automaticEnd");
        if(!Dummy.isDummy) {
            try {
                BluetoothSingleton.getInstance().adminAutoCalibrateFinish(activity);
                Log.e(TAG, "automaticEnd: done");
            } catch (JSONException | InterruptedException | NullPointerException e) {

                //throw new RuntimeException(e);
                Log.e(TAG, "automaticEnd: failed");
                Log.e(TAG, "error: "+e);
                e.printStackTrace();
            }
            return;
        }
        Log.e(TAG, "automaticEnd: dummy");
        tickDummy(activity);
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
            Toast.makeText(activity,"Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
            return;
        }
        //CocktailMachine.clean(activity);
        try {
            BluetoothSingleton.getInstance().adminClean(activity);
            Toast.makeText(activity, "Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "clean: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "clean: failed");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
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
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
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
     * @param activity
     * @author Johanna Reidt
     */
    public static void factoryReset(Activity activity){
        Log.i(TAG, "factoryReset");
        //TO DO: factoryReset
        Toast.makeText(activity,
                "Die Cocktailmachine wird auf Werkseinstellungen zurückgesetzt.",
                Toast.LENGTH_SHORT).show();
        DeleteFromDB.removeAll(activity);
        Log.i(TAG, "factoryReset: db complete delete");
        if(Dummy.isDummy){
            Log.i(TAG, "factoryReset: dummy");
            Toast.makeText(activity,"Erfolgreich zurückgesetzt!", Toast.LENGTH_SHORT).show();
            CocktailMachineCalibration.setIsDone(false);
            Log.i(TAG, "factoryReset: dummy: CocktailMachineCalibration.setIsDone(false)");
            GetActivity.goToMenu(activity, true);
            Log.i(TAG, "factoryReset: dummy: clear stack, go to menu, all pumps empty, all recipes deleted");
        }


        try {
            BluetoothSingleton.getInstance().adminFactoryReset(activity,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity,"Erfolgreich zurückgesetzt!", Toast.LENGTH_SHORT).show();
                            CocktailMachineCalibration.setIsDone(false);
                            Log.i(TAG, "factoryReset: CocktailMachineCalibration.setIsDone(false)");
                            GetActivity.goToMenu(activity, true);
                            Log.i(TAG, "factoryReset: clear stack, go to menu, all pumps empty, all recipes deleted");
                        }
                    });
            Log.i(TAG, "factoryReset: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "factoryReset: failed");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
            Toast.makeText(activity,
                    "Werkseinstellungen konnten NICHT wieder hergestellt werden.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * reset CocktailMachine
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
            Log.i(TAG, "Reset stored error");
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
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
     * @param activity
     */
    /*
    public static void reset(Activity activity){
        //TO DO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset();
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

     */

}

