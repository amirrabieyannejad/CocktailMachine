/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cocktailmachine.bluetoothlegatt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.example.cocktailmachine.R;

import org.json.JSONException;

import java.util.ArrayList;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    private TextView mConnectionState;
    private TextView txtNotificationData;
    private EditText edtTxtUser;
    private EditText edtTxtRecipe;
    private EditText edtTxtLiquid;
    private EditText edtTxtSlot;
    private TextView txtReadCharacteristicData;
    private TextView edtTxtVolume;

    // TODO Step1
    BluetoothSingleton singleton;


    private BluetoothGattCharacteristic mGattCharacteristics;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final boolean mConnected = false;


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_transfer);

        //TODO: step2
        singleton = BluetoothSingleton.getInstance();

        //TODO: step3
        singleton.requestBlePermissions(this);

        txtNotificationData = findViewById(R.id.txtNotificationData);

        // TODO: step4
        Intent gattServiceIntent = new Intent(this,BluetoothLeService.class);
        //bindService(gattServiceIntent, singleton.mServiceConnection, BIND_AUTO_CREATE);

        mConnectionState = findViewById(R.id.connection_state);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnClean = findViewById(R.id.btnClean);
        Button btnDefinePump = findViewById(R.id.btnDefinePump);
        Button btnCalibrate = findViewById(R.id.btnCalibrate);
        Button btnMakeRecipe = findViewById(R.id.btnMakeRecipe);
        Button btnAddLiquid = findViewById(R.id.btnAddLiquid);
        Button btnDefineRecipe = findViewById(R.id.btnDefineRecipe);
        Button btnReadPumpsVolume = findViewById(R.id.btnReadPumpsVolume);
        Button btnReadCocktail = findViewById(R.id.btnReadCocktail);
        Button btnReadRecipes = findViewById(R.id.btnReadRecipes);
        Button btnEditRecipes = findViewById(R.id.btnEditRecipe);
        Button btnReadState = findViewById(R.id.btnReadState);
        Button btnRefillPump = findViewById(R.id.btnRefillPump);
        Button btnRestart = findViewById(R.id.btnRestart);
        edtTxtUser = findViewById(R.id.edtTxtUser);
        edtTxtRecipe = findViewById(R.id.edtTxtRecipe);
        edtTxtLiquid = findViewById(R.id.edtTxtLiquid);
        edtTxtSlot = findViewById(R.id.edtTxtSlot);
        txtReadCharacteristicData = findViewById(R.id.txtReadCharacteristicData);

        edtTxtVolume = findViewById(R.id.edtTxtVolume);

        EditText edtTxtLiquid2 = findViewById(R.id.edtTxtLiquid2);
        EditText edtTxtVolume2 = findViewById(R.id.edtTxtVolume2);

        EditText edtTxtLiquid3 = findViewById(R.id.edtTxtLiquid3);
        EditText edtTxtVolume3 = findViewById(R.id.edtTxtVolume3);

        EditText edtTxtLiquid4 = findViewById(R.id.edtTxtLiquid4);
        EditText edtTxtVolume4 = findViewById(R.id.edtTxtVolume4);

        EditText edtTxtLiquid5 = findViewById(R.id.edtTxtLiquid5);
        EditText edtTxtVolume5 = findViewById(R.id.edtTxtVolume5);

        // register as a new user and receive a user ID
        btnAddUser.setOnClickListener(v -> {
            //getResponseValue(false, "init_user");
            // from initUser
            // TODO: step5
            try {
                    String user = edtTxtUser.getText().toString();
                    singleton.mBluetoothLeService.initUser(user);

                    //TODO: step6
                    Handler handler = new Handler();
                    handler.postDelayed(()->txtNotificationData.setText
                            (singleton.getEspResponseValue()),6000);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        // Read Current Cocktail
        btnReadCocktail.setOnClickListener(v -> {


        });

        //Read Pumps Volume
        btnReadPumpsVolume.setOnClickListener(v -> {
            singleton.mBluetoothLeService.adminReadPumpsStatus();
            Handler handler = new Handler();
            handler.postDelayed(()->txtNotificationData.setText
                    (singleton.getEspResponseValue()),6000);


        });

        // Status State
        btnReadState.setOnClickListener(v -> {

        });

        // Read saved Recipes from Device
        btnReadRecipes.setOnClickListener(v -> {

        });


        // Reset the machine so that it can make a new cocktail
        btnReset.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                singleton.mBluetoothLeService.reset(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Clean the machine
        btnClean.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                singleton.mBluetoothLeService.adminClean();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
        // restart the machine
        btnRestart.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                // If factory_reset is set to true, all settings will also be deleted.
                singleton.mBluetoothLeService.adminRestart(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        //Calibrate  pump
        btnCalibrate.setOnClickListener(v -> {
            txtNotificationData.setText("get Notification...");


        });

        // Refill Pump
        btnRefillPump.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                int slot = Integer.parseInt(edtTxtSlot.getText().toString());
                singleton.mBluetoothLeService.adminRefillPump(volume1, slot);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // Add Pump
        btnDefinePump.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                int volume2 = Integer.parseInt(edtTxtSlot.getText().toString());
                singleton.mBluetoothLeService.adminDefinePump(edtTxtLiquid.getText().toString(),
                        volume1, volume2);
                Handler handler = new Handler();
                handler.postDelayed(()->txtNotificationData.setText
                        (singleton.getEspResponseValue()),6000);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // Add Liquid
        btnAddLiquid.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                singleton.mBluetoothLeService.addLiquid
                        (userId, edtTxtLiquid.getText().toString(), volume1);
                //mBluetoothLeService.readCharacteristic();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });

        // make Recipe mix the recipe
        btnMakeRecipe.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                singleton.mBluetoothLeService.makeRecipe
                        (userId, edtTxtRecipe.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // Define Recipe: defines a new recipe or changes an existing recipe
        btnDefineRecipe.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                ArrayList<Pair<String, Float>> liquids = new ArrayList<>();
                liquids.add(new Pair<>(edtTxtLiquid.getText().toString(), volume1));
                if (TextUtils.isEmpty(edtTxtVolume2.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume2 = Float.parseFloat(edtTxtVolume2.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid2.getText().toString(), volume2));
                }
                if (TextUtils.isEmpty(edtTxtVolume3.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume3 = Float.parseFloat(edtTxtVolume3.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid3.getText().toString(), volume3));
                }
                if (TextUtils.isEmpty(edtTxtVolume4.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume4 = Float.parseFloat(edtTxtVolume4.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid4.getText().toString(), volume4));
                }
                if (TextUtils.isEmpty(edtTxtVolume5.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume5 = Float.parseFloat(edtTxtVolume5.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid5.getText().toString(), volume5));
                }
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                singleton.mBluetoothLeService.defineRecipe
                        (userId, edtTxtRecipe.getText().toString(), liquids);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
        // Edit Recipe: defines a new recipe or changes an existing recipe
        btnEditRecipes.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                ArrayList<Pair<String, Float>> liquids = new ArrayList<>();
                liquids.add(new Pair<>(edtTxtLiquid.getText().toString(), volume1));
                if (TextUtils.isEmpty(edtTxtVolume2.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume2 = Float.parseFloat(edtTxtVolume2.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid2.getText().toString(), volume2));
                }
                if (TextUtils.isEmpty(edtTxtVolume3.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume3 = Float.parseFloat(edtTxtVolume3.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid3.getText().toString(), volume3));
                }
                if (TextUtils.isEmpty(edtTxtVolume4.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume4 = Float.parseFloat(edtTxtVolume4.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid4.getText().toString(), volume4));
                }
                if (TextUtils.isEmpty(edtTxtVolume5.getText().toString())) {
                    Log.w(TAG, "not filled");
                } else {
                    float volume5 = Float.parseFloat(edtTxtVolume5.getText().toString());
                    liquids.add(new Pair<>(edtTxtLiquid5.getText().toString(), volume5));
                }
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                singleton.mBluetoothLeService.
                        editRecipe(userId, edtTxtRecipe.getText().toString(), liquids);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Step8: make sure that Gatt Server is founded
        if (singleton.mBluetoothLeService != null) {
            final boolean result = singleton.mBluetoothLeService.connect(
                    singleton.getEspDeviceAddress());
            Log.d("OnResume", "Connect request result=" + result);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(BluetoothSingleton.getInstance().mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Step7: Unbind Service if the Activity is destroyed
        //unbindService(singleton.mServiceConnection);
        singleton.mBluetoothLeService = null;

    }


}
