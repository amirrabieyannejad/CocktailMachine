package com.example.cocktailmachine.bluetoothlegatt;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class WaitForBroadcastReceiver extends AsyncTask<Void, Void, JSONObject> {
    private final static String TAG = WaitForBroadcastReceiver.class.getSimpleName();
    BluetoothSingleton singleton = BluetoothSingleton.getInstance();
    JSONObject jsonObject;
    String result;

    public abstract void toSave() throws InterruptedException;

    public Boolean check() {
        return (jsonObject != null) ||
                (result != null);
    }



    public JSONObject getResult() {
        return jsonObject;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        int timeout = 500;
        while (singleton.getEspResponseValue() == null
                || singleton.getEspResponseValue().equals("\"processing\"")) {
            Log.w(TAG, "we are in WaitForBroadcast doInBackground before try-catch!");
            try {
                Log.w(TAG, "waitForBroadcastReceiver: this is middle night.." +
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

}