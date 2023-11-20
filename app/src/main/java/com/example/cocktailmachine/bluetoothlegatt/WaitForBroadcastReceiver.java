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
    private BluetoothSingleton singleton = BluetoothSingleton.getInstance();
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    String result;

    private Postexecute postexecute = null;
    private final AlertDialog dialog;



    public WaitForBroadcastReceiver(Activity activity){
        dialog = GetDialog.loadingBluetooth(activity);
    }
    public WaitForBroadcastReceiver(Activity activity, Postexecute postexecute){
        dialog = GetDialog.loadingBluetooth(activity);
        this.postexecute = postexecute;
    }

    public void post(){
        dialog.dismiss();
        //dialog.cancel();
        if(postexecute != null){
            postexecute.post();
        }
    }

    public abstract void toSave() throws InterruptedException, JSONException,
            NotInitializedDBException, MissingIngredientPumpException;

    public Boolean check() {
        return (jsonObject != null) ||
                (result != null);
    }

    public JSONObject getJsonResult() {
        return jsonObject;
    }
    public String getStringResult() {
        result = result.replaceAll("\"", "");
        return result;
    }

    public JSONArray getJSONArrayResult() throws JSONException {
        jsonArray = jsonObject.getJSONArray("");
        return jsonArray;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected JSONObject doInBackground(Void... voids) {
        this.dialog.show();
        int timeout = 500;
        int timeoutMax = 0;
        while (singleton.getEspResponseValue() == null
                || singleton.getEspResponseValue().equals("processing")) {
            Log.w(TAG, "we are in WaitForBroadcast doInBackground before try-catch!");
            try {
                Log.w(TAG, "waitForBroadcastReceiverAsyncTask: Waiting for target value.." +
                        singleton.getEspResponseValue());

                Thread.sleep(timeout);
                timeoutMax = timeoutMax + 500;
                if (timeoutMax == 5000) {
                    Log.w(TAG, "waitforBraodcastReceiver: timeout...");
                    break;
                }

            } catch (InterruptedException e) {
                Log.getStackTraceString(e);
            }
            Log.w(TAG, "we are in WaitForBroadcast doInBackground after try-catch!");

        }
        result = singleton.getEspResponseValue();
        Log.w(TAG, "waitForBroadcastReceiver: " + result);
        try {
            jsonObject = new JSONObject(result);
            //jsonArray = jsonObject.getJSONArray(result);

        }catch(NullPointerException e){
            Log.w(TAG, "waitForBroadcastReceiver: result is null");
            jsonObject =  new JSONObject();
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            Log.w(TAG, "waitForBroadcastReceiver: the response Value is no JSON Format..!");
            jsonObject =  new JSONObject();
        }
        return jsonObject;
    }



        @Override
        @SuppressLint("MissingPermission")
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
            Log.w(TAG, "we are in WaitForBroadcast Post Execute!");
            try {
                toSave();
                post();
                singleton.mBluetoothGatt.disconnect();
                singleton.connect = false;
                singleton.value = null;
            } catch (InterruptedException
                     | NotInitializedDBException
                     | JSONException |
                     MissingIngredientPumpException e) {
                Log.e(TAG, "onPostExecute", e);
                //Log.getStackTraceString(e);
                dialog.setTitle("Fehler!");
            }


        }

}