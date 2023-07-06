package com.example.cocktailmachine.bluetoothlegatt;

import android.os.AsyncTask;
import android.util.Log;

import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class WaitForBroadcastReceiver extends AsyncTask<Void, Void, JSONObject> {
    private final static String TAG = WaitForBroadcastReceiver.class.getSimpleName();
    BluetoothSingleton singleton = BluetoothSingleton.getInstance();
    JSONObject jsonObject;
    JSONArray jsonArray;
    String result;

    public abstract void toSave() throws InterruptedException, JSONException, NotInitializedDBException, MissingIngredientPumpException;

    public Boolean check() {
        return (jsonObject != null) ||
                (result != null);
    }



    public JSONObject getResult() {
        return jsonObject;
    }
    public JSONArray getJSONArrayResult() throws JSONException {
        jsonArray = jsonObject.getJSONArray("");
        return jsonArray;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        int timeout = 500;
        while (singleton.getEspResponseValue() == null
                || singleton.getEspResponseValue().equals("\"processing\"")) {
            Log.w(TAG, "we are in WaitForBroadcast doInBackground before try-catch!");
            try {
                Log.w(TAG, "waitForBroadcastReceiverAsyncTask: Waiting for target value.." +
                        singleton.getEspResponseValue());

                Thread.sleep(500);
                timeout = timeout + 500;
                if (timeout == 5000) {
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.w(TAG, "we are in WaitForBroadcast doInBackground after try-catch!");

        }
        result = singleton.getEspResponseValue();
        try {
            jsonObject = new JSONObject(result);
            //jsonArray = jsonObject.getJSONArray(result);

        } catch (JSONException e) {
            //throw new RuntimeException(e);
            Log.w(TAG, "the response Value is no JSON Format..!");


        }
        return jsonObject;
    }



        @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
            Log.w(TAG, "we are in WaitForBroadcast Post Execute!");
            try {
                toSave();
            } catch (InterruptedException | NotInitializedDBException | JSONException |
                     MissingIngredientPumpException e) {
                e.printStackTrace();
            }


        }

}