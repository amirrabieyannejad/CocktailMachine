package com.example.cocktailmachine.data;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.CalibrateStatus;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.v2.CocktailMachineCalibration;
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
        CocktailMachine.currentRecipe = currentRecipe;
    }


    /**
     * set weigth on scale
     * @param weight
     * @author Johanna Reidt
     */
    public static void setCurrentWeight(JSONObject weight) {
        try {
            currentWeight = weight.getDouble("weight");
        } catch (JSONException e) {
            currentWeight = -1.0;
        }
    }


    /**
     * sets current Cocktail status (what ingredients and how much of ech is already in the used glas)
     * @author Johanna Reidt
     * @param jsonObject
     */
    public static void setCurrentCocktail(JSONObject jsonObject) {
        /**
         * {"weight": 500.0, "content": [["beer", 250], ["lemonade", 250]]}
         */
        //current
        try {
            JSONArray array = jsonObject.getJSONArray("content");
            current = new LinkedHashMap<Ingredient, Integer>();
            for(int i=0;i< array.length(); i++) {
                JSONArray temp = array.getJSONArray(i);
                current.put(Ingredient.getIngredient(temp.getString(0)), temp.getInt(1));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "setCurrentCocktail failed");
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
    }


    /**
     * sets timestamp from ESP-DB's last change
     * if it is different from saved timestamp, set dbChanged=true
     * @author Johanna Reidt
     * @param n
     */
    public static void setLastChange(String n){
        int old_time = time;
        time = Integer.parseInt(n);
        if(old_time<time){
            dbChanged = true;
        }
    }











    //StatusAbfragen

    /**
     * weight on scale
     * @return
     * @author Johanna Reidt
     */
    public static double getCurrentWeight(Activity activity) {
        try {
            BluetoothSingleton.getInstance().adminReadScaleStatus(activity);
        } catch (JSONException | InterruptedException|NullPointerException  e) {
            Log.i(TAG,"getCurrentWeight: error");
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
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
    public static boolean isCocktailMachineSet(Postexecute notSet, Postexecute set,
                                               Activity activity){

        if(Dummy.isDummy){
            return CocktailMachineCalibration.isIsDone();
        }else{
            //TODO: add bluetooth /esp implementation
            try{
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity, new Postexecute(){
                    @Override
                    public void post() {
                        if(Pump.getPumps().size()==0){
                            notSet.post();
                        }else{
                            set.post();
                        }
                    }
                });
                Log.i(TAG, "isCocktailMachineSet: done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG, "isCocktailMachineSet: failed");
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return Pump.getPumps().size()>0;
        }
        //return r.nextDouble() >= 0.5;
        //return false;
    }
    /**
     *check if cocktail machine isset
     * @return
     * @author Johanna Reidt
     */
    public static boolean isCocktailMachineSet(Activity activity){

        if(Dummy.isDummy){
            return CocktailMachineCalibration.isIsDone();
        }else{
            try {
                BluetoothSingleton.getInstance().adminReadPumpsStatus(activity);
                Log.i(TAG, "isCocktailMachineSet: done");
            } catch (JSONException | InterruptedException |NullPointerException e) {
                Log.i(TAG, "isCocktailMachineSet: failed");
                Log.e(TAG, e.getMessage());
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
        //TO DO: tareScale
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateTareScale(activity);
            Toast.makeText(activity, "Tarierung der Waage eingeleitet.",Toast.LENGTH_SHORT).show();
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
        //TO DO: calibrateScale
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateScale(weight,activity);
            Toast.makeText(activity, "Kalibrierung der Waage mit Gewicht: "+weight+" g",Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException  |NullPointerException e) {
            e.printStackTrace();
            Log.i(TAG, "sendCalibrateScale failed");
            Toast.makeText(activity, "Die Kalibrierung ist fehlgeschlagen!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * calibrate Scale with Dialog view
     * @author Johanna Reidt
     * @param activity
     */
    public static void calibrateScale(Activity activity){
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
        //TO DO: setScaleFactor
        //TO DO get factor with dialog
        GetDialog.calibrateScaleFactor(activity);

    }

    /**
     * send skalierungsfaktor to ESP
     * @author Johanna Reidt
     * @param activity
     * @param factor
     */
    public static void sendScaleFactor(Activity activity, Float factor){
        //TO DO: send scale factor
        try {
            BluetoothSingleton.getInstance().adminManuelCalibrateSetScaleFactor(factor,activity);
            Toast.makeText(activity, "Skalierung der Waage mit Faktor: "+factor,Toast.LENGTH_SHORT).show();
        } catch (JSONException | InterruptedException |NullPointerException e) {
            e.printStackTrace();
            Log.i(TAG, "sendScaleFactor failed");
            Toast.makeText(activity, "ERROR",Toast.LENGTH_SHORT).show();
        }
    }














    //DB

    /**
     * get LastChange timestamp  from ESP-DB
     * @author Johanna Reidt
     */
    public static void getLastChange(Activity activity){
        try {
            BluetoothSingleton.getInstance().adminReadLastChange(activity);
        } catch (JSONException | InterruptedException|NullPointerException e) {
            e.printStackTrace();
            Log.i(TAG, "getLastChange failed");
        }
    }

    /**
     * update DB If Cange in ESP-DB
     * @author Johanna Reidt
     */
    public static void updateRecipeListIfChanged(Activity activity){
        getLastChange(activity);
        if(dbChanged){
            try {
                BluetoothSingleton.getInstance().adminReadRecipesStatus(activity);
            } catch (JSONException | InterruptedException|NullPointerException e) {
                e.printStackTrace();
                Log.i(TAG, "updateRecipeListIfChanged failed");
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
        }else {
            try {
                BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId()
                        , recipe.getName(),activity);
                Log.i(TAG,  "queueRecipe: queued");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG,  "queueRecipe: error");
                e.printStackTrace();
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
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
        }else {
            try {
                BluetoothSingleton.getInstance().adminReadUserQueue(activity);
                //BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(), recipe.getName());
                Log.i(TAG,  "readUserQueue: queued");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG,  "readUserQueue: error");
                e.printStackTrace();
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    /**
     * number of users before this one in the queue
     * @author Johanna Reidt
     * @return
     */
    public static int getNumberOfUsersUntilThisUsersTurn(Activity activity) {
        /*TODO:
        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.makeRecipe(this.getName());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }h

         */
        readUserQueue(activity);
        return queue.indexOf(AdminRights.getUserId());
    }
    public static int getNumberOfUsersUntilThisUsersTurn(int tick){
        /*TODO:
        BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
        try {
            bluetoothSingleton.makeRecipe(this.getName());
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }

         */
        if(Dummy.isDummy) {
            return tick - 1;
        }else{
            return getNumberOfUsersUntilThisUsersTurn(tick);
        }
    }

    /**
     * check if user is the current one
     * @author Johanna Reidt
     * @return
     */
    public static boolean isCurrentUser(Activity activity){
        readUserQueue(activity);
        return (currentUser==AdminRights.getUserId());
    }

    public static void startMixing(Activity activity){
        Log.i(TAG,  "startMixing");
        if(Dummy.isDummy){
            Log.i(TAG,  "startMixing: Dummy queue");
        }else {
            try {
                BluetoothSingleton.getInstance().userStartRecipe(AdminRights.getUserId(),activity);
                //BluetoothSingleton.getInstance().userQueueRecipe(AdminRights.getUserId(), recipe.getName());
                Log.i(TAG,  "startMixing: started");
            } catch (JSONException | InterruptedException |NullPointerException e) {
                Log.i(TAG,  "startMixing: error");
                e.printStackTrace();
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }


    /**
     * get status of ingredient and volume in glas of the current mixing cocktail
     * @author Johanna Reidt
     * @return hashmap ingredient to filled volume in glas
     */
    public static LinkedHashMap<Ingredient, Integer> getCurrentCocktailStatus(
            Postexecute postexecute,Activity activity){
        if(isCurrentUser(activity)) {
            try {
                BluetoothSingleton.getInstance().adminReadCurrentCocktail(activity, postexecute);
            } catch (JSONException | InterruptedException|NullPointerException e) {
                e.printStackTrace();
                Log.i(TAG, "getCurrentCocktailStatus failed");
                //count down
            }
        }else{
            current = null;
        }
        return current;
    }


    /**
     * checks if the current status is "mixing"
     * @author Johanna Reidt
     * @return
     */
    public static boolean isMixing(Activity activity){

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
        }else {
            try {
                BluetoothSingleton.getInstance().userTakeCocktail(AdminRights.getUserId(),activity);
                Log.i(TAG,  "takeCocktail: done");
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.i(TAG,  "takeCocktail: error");
                e.printStackTrace();
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    }















    //Calibrations


    /**
     * call auto calibration start
     * @author Johanna Reidt
     */
    public static void automaticCalibration(Activity activity){

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
            } catch (JSONException | InterruptedException|NullPointerException e) {
                Log.e(TAG,"automaticCalibration");
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();

            }
        }else{
            Log.i(TAG, "automaticCalibration");
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
        if(Dummy.isDummy) {
            if(dummyCounter<0){
                Log.i(TAG, "isAutomaticCalibrationDone: dummycounter = 0");
                dummyCounter = 0;
                Log.i(TAG, "isAutomaticCalibrationDone: pumpe.size = "+Pump.getPumps().size());
            }
            //return new Random(42).nextBoolean();
            if( dummyCounter==Pump.getPumps().size()){
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_calculation){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_done);
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: calibration_calculation->calibration_done=>fertig done ");
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: FERITG");
                    return true;
                }else{
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_calculation);
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: dummyCounter, alle Pumpen fertig ->calibration_calculation  ");
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: ");
                    return false;
                }
            } else if (dummyCounter<Pump.getPumps().size()) {
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_pumps){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_empty_container);
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: calibration_pumps-> calibration_empty_container");
                    Log.i(TAG,"isAutomaticCalibrationDone: dummyCounter schon: "+dummyCounter);
                }else if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container){
                    CalibrateStatus.setStatus(CalibrateStatus.calibration_pumps);
                    Log.i(TAG, "isAutomaticCalibrationDone: Dummy: calibration_empty_container-> calibration_pumps");
                    //dummyCounter = dummyCounter+1;
                    Log.i(TAG,"isAutomaticCalibrationDone: dummyCounter +1: "+dummyCounter);
                }
                return false;
            }
        }else{
            //TO  DO: Bluetooth connection
            //return new Random(42).nextBoolean();
            return CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_done;
        }
        return false;
    }

    /**
     * checks if admin has to empty the glass to continue the calibration process
     * @return
     * @author Johanna Reidt
     */
    public static boolean needsEmptyingGlass(Activity activity) {
        Log.i(TAG, "needsEmptyingGlass");
        if(Dummy.isDummy) {
            return CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container;
            //return new Random(42).nextBoolean();
        }else{
            //TO DO: Bluetooth connection
            //return new Random(42).nextBoolean();
            return CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_empty_container;
        }
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
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }

     */


    /**
     * tells esp that the glass with 100 ml water has been places
     * @author Johanna Reidt
     */
    public static void automaticWeight(Activity activity){
        try{
            BluetoothSingleton.getInstance().adminAutoCalibrateAddWeight(100,activity);
        } catch (JSONException | InterruptedException|NullPointerException e) {

            //throw new RuntimeException(e);
            Log.e(TAG,"automaticWeight");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }


    /**
     * empty glass is ready
     * @author Johanna Reidt
     */
    public static void automaticEmptyPumping(Activity activity){
        if(Dummy.isDummy) {
            dummyCounter = dummyCounter + 1;
            Log.i(TAG,"automaticEmptyPumping "+dummyCounter );
        }
        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateAddEmpty(activity);
        } catch (JSONException | InterruptedException|NullPointerException e) {

            //throw new RuntimeException(e);
            Log.e(TAG,"automaticEmptyPumping");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
    }


    /**
     * last call for calibration
     * @author Johanna Reidt
     */
    public static void automaticEnd(Activity activity){
        try {
            BluetoothSingleton.getInstance().adminAutoCalibrateFinish(activity);
        } catch (JSONException | InterruptedException|NullPointerException e){

            //throw new RuntimeException(e);
            Log.e(TAG,"automaticEnd");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
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
        //CocktailMachine.clean(activity);
        try {
            BluetoothSingleton.getInstance().adminClean(activity);
            Toast.makeText(activity, "Reinigung wurde gestartet!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "clean: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "clean: failed");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
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
        //TO DO: restart
        try {
            BluetoothSingleton.getInstance().adminRestart(activity);
            Log.i(TAG, "restart: done");

        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "restart: failed");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
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
        //TO DO: factoryReset
        try {
            BluetoothSingleton.getInstance().adminFactoryReset(activity);
            Log.i(TAG, "factoryReset: done");
        } catch (JSONException | InterruptedException|NullPointerException e) {
            Log.i(TAG, "factoryReset: failed");
            Log.e(TAG, e.getMessage());
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

        try {
            BluetoothSingleton.getInstance().adminReset(activity, new Postexecute(){
                @Override
                public void post() {
                    Toast.makeText(activity, "Reset erledigt!",Toast.LENGTH_SHORT).show();
                }

            });
        } catch (JSONException | InterruptedException|NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Reset ist fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Reset stored error");
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

