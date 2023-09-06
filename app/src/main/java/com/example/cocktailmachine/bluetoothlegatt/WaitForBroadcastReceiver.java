package com.example.cocktailmachine.bluetoothlegatt;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.Postexecute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;


public abstract class WaitForBroadcastReceiver extends AsyncTask<Void, Void, JSONObject> {
    private final static String TAG = WaitForBroadcastReceiver.class.getSimpleName();
    BluetoothSingleton singleton = BluetoothSingleton.getInstance();
    JSONObject jsonObject;
    JSONArray jsonArray;
    String result;

    Postexecute postexecute = null;

    public WaitForBroadcastReceiver(){}
    public WaitForBroadcastReceiver(Postexecute postexecute){
        this.postexecute = postexecute;
    }

    public void post(){
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
        return result;
    }

    public JSONArray getJSONArrayResult() throws JSONException {
        jsonArray = jsonObject.getJSONArray("");
        return jsonArray;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected JSONObject doInBackground(Void... voids) {
        int timeout = 500;
        int timeoutMax = 0;
        while (singleton.getEspResponseValue() == null
                || singleton.getEspResponseValue().equals("\"processing\"")) {
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
                e.printStackTrace();
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
            return new JSONObject();
        } catch (JSONException e) {
            //throw new RuntimeException(e);
            Log.w(TAG, "waitForBroadcastReceiver: the response Value is no JSON Format..!");


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
                e.printStackTrace();
            }


        }

}