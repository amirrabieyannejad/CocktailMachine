package com.example.cocktailmachine.bluetoothlegatt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class WaitForBroadcastReceiver extends AsyncTask<Void, Void, JSONObject> {
    private final static String TAG = WaitForBroadcastReceiver.class.getSimpleName();
    private final BluetoothSingleton singleton = BluetoothSingleton.getInstance();
    private JSONObject jsonObject;
    private String result;

    private Postexecute postexecute = null;
    //private final AlertDialog dialog;


    public WaitForBroadcastReceiver(){
        this.postexecute = postexecute;
    }

    public WaitForBroadcastReceiver(Postexecute postexecute){
        this.postexecute = postexecute;
    }

    public void post(){
        //dialog.dismiss();
        //dialog.cancel();
        if(postexecute != null){
            postexecute.post();
        }
    }

    public abstract void toSave() throws InterruptedException, JSONException,
            NotInitializedDBException, MissingIngredientPumpException;

    public Boolean check() {
        return (jsonObject != null) || (result != null);
    }

    public JSONObject getJsonResult() {
        Log.w(TAG, "ASYNC-TASK-onPostExecute-getJsonResult->" + jsonObject);
        return jsonObject;
    }
    public String getStringResult() {
        //if(this.result != null) {
        //    result = result.replaceAll("\"", "");
        //}
        return result;
    }

    public JSONArray getJSONArrayResult() throws JSONException {
        return new JSONArray(this.result);
        //return jsonObject.getJSONArray("");
    }

    @SuppressLint("MissingPermission")
    @Override
    protected JSONObject doInBackground(Void... voids) {
        //this.dialog.show();
        int timeout = 500;
        int timeoutMax = 0;
        Log.w(TAG, "ASYNC-TASK-doInBackground: doInBackground has been reached asyncFlag value " +
                "is: " + singleton.asyncFlag);
        while (!singleton.asyncFlag) {

            try {
                Log.w(TAG, "ASYNC-TASK-doInBackground: syncFlag is True - Waiting for target value..");

                Thread.sleep(timeout);
                timeoutMax = timeoutMax + 500;
                if (timeoutMax == 5000) {
                    Log.w(TAG, "ASYNC-TASK-doInBackground: timeout...");
                    break;
                }

            } catch (InterruptedException e) {
                Log.getStackTraceString(e);
            }

        }
        result = singleton.getEspResponseValue();

        //initUserResult
        Log.w(TAG, "ASYNC-TASK-doInBackground: Check Value-> " + result);
        try {
            jsonObject = new JSONObject(result);
            //jsonArray = jsonObject.getJSONArray(result);

        }catch(NullPointerException e){
            Log.w(TAG, "ASYNC-TASK-doInBackground:  Value is Null");
            jsonObject =  new JSONObject();
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            Log.w(TAG, "ASYNC-TASK-doInBackground: the response Value is no JSON Format..!");
            jsonObject =  new JSONObject();
        }
        return jsonObject;
    }



    @Override
    @SuppressLint("MissingPermission")
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        Log.w(TAG, "ASYNC-TASK-onPostExecute has been reached!");
        singleton.mBluetoothGatt.disconnect();
        Log.w(TAG, "ASYNC-TASK-onPostExecute:BluetoothGATT is Disconnected!");
        singleton.connect = false;
        singleton.value = null;
        singleton.asyncFlag = false;
        singleton.busy = false;
        try {
            Log.i(TAG, "ASYNC-TASK-onPostExecute: start toSave");
            toSave();

        } catch (InterruptedException
                 | NotInitializedDBException
                 | JSONException
                 | MissingIngredientPumpException e) {
            Log.e(TAG, "ASYNC-TASK-onPostExecute", e);
            //Log.getStackTraceString(e);
            //dialog.setTitle("Fehler!");

        }finally {
            Log.i(TAG, "ASYNC-TASK-onPostExecute: start post");
            //dialog.cancel();
            post();
            jsonObject = null;

        }

    }

}