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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import com.example.cocktailmachine.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private TextView mConnectionState;
    private TextView txtNotificationData;

    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private EditText edtTxtUser;
    private EditText edtTxtRecipe;
    private EditText edtTxtLiquid;
    private EditText edtTxtSlot;
    private TextView txtReadCharacteristicData;
    private TextView edtTxtVolume;


    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private BluetoothGattCharacteristic mGattCharacteristics;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private boolean mConnected = false;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 191;

    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                updateConnectionState(R.string.data_available);
                displayNotificationData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.

    private void clearUI() {
        txtNotificationData.setText(R.string.txt_notificationData);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_transfer);
        requestBlePermissions(this, PERMISSIONS_REQUEST_CODE);
        final Intent intent = getIntent();
        String mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        // TODO: device address will not receive from Extra_intent anymore device Address will
        //  directly called
        //mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceAddress = "54:43:B2:A9:32:26";
        //String mDeviceName="Cocktail Machine";


        txtNotificationData = findViewById(R.id.txtNotificationData);

        //getActionBar().setTitle(mDeviceName);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
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

        // Read Current Cocktail
        btnReadCocktail.setOnClickListener(v -> {
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_STATE,
                    BluetoothLeService.CHARACTERISTIC_STATUS_COCKTAIL);
        });

        //Read Pumps Volume
        btnReadPumpsVolume.setOnClickListener(v -> {
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_STATE,
                    BluetoothLeService.CHARACTERISTIC_STATUS_LIQUIDS);
        });

        // Read State
        btnReadState.setOnClickListener(v -> {
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_STATE,
                    BluetoothLeService.CHARACTERISTIC_STATUS_STATE);
        });

        // Read saved Recipes from Device
        btnReadRecipes.setOnClickListener(v -> {
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_STATE,
                    BluetoothLeService.CHARACTERISTIC_STATUS_RECIPES);
        });
        // register as a new user and receive a user ID
        btnAddUser.setOnClickListener(v -> {
            //getResponseValue(false, "init_user");
            // from initUser
            try {
                String user = edtTxtUser.getText().toString();
                mBluetoothLeService.initUser(user);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);


        });

        // Reset the machine so that it can make a new cocktail
        btnReset.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                mBluetoothLeService.reset(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
        });

        // Clean the machine
        btnClean.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                mBluetoothLeService.adminClean();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
        });
        // restart the machine
        btnRestart.setOnClickListener(v -> {

            try {
                txtNotificationData.setText("get Notification...");
                // If factory_reset is set to true, all settings will also be deleted.
                mBluetoothLeService.adminRestart(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
        });
        //Calibrate all pumps
        btnCalibrate.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                mBluetoothLeService.adminCalibratePumps();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);

        });

        // Refill Pump
        btnRefillPump.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                int volume2 = Integer.parseInt(edtTxtSlot.getText().toString());
                mBluetoothLeService.adminRefillPump(edtTxtLiquid.getText().toString(),
                        volume1, volume2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
        });

        // Add Pump
        btnDefinePump.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                int volume2 = Integer.parseInt(edtTxtSlot.getText().toString());
                mBluetoothLeService.adminDefinePump(edtTxtLiquid.getText().toString(),
                        volume1, volume2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
        });

        // Add Liquid
        btnAddLiquid.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                mBluetoothLeService.addLiquid(userId, edtTxtLiquid.getText().toString(), volume1);
                //mBluetoothLeService.readCharacteristic();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
        });

        // make Recipe mix the recipe
        btnMakeRecipe.setOnClickListener(v -> {
            try {
                txtNotificationData.setText("get Notification...");
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                mBluetoothLeService.makeRecipe(userId, edtTxtRecipe.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
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
                mBluetoothLeService.defineRecipe(userId, edtTxtRecipe.getText().toString(), liquids);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
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
                mBluetoothLeService.editRecipe(userId, edtTxtRecipe.getText().toString(), liquids);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
/*        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(() -> {
            mConnectionState.setText(resourceId);
        });
    }

    private void displayNotificationData(String data) {
        runOnUiThread(() -> {

            txtNotificationData.setText("Notification Data: " + data);
            Toast.makeText(mBluetoothLeService, data, Toast.LENGTH_LONG).show();
        });
    }

    // Demonstrate how to read response characteristic form deice when through
    // Characteristic "Message" one message deliver to device. This is response
    // from received from app
    private void getResponseValue(Boolean admin, String method) {
        // This should be change due to no more existences of Characteristic_read
        // it will be replaced to "User Message Characteristic"
        // receive Notification from Server will be handled by given notify as broadcasts
        if (admin) {
            mGattCharacteristics = mBluetoothLeService.getBluetoothGattCharacteristic(
                    BluetoothLeService.SERVICE_READ_WRITE, BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
            Log.w(TAG, "Characteristic Admin ist activated  ");
        } else {
            mGattCharacteristics = mBluetoothLeService.getBluetoothGattCharacteristic(
                    BluetoothLeService.SERVICE_READ_WRITE, BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
            Log.w(TAG, "Characteristic User ist activated  ");
        }

        if (mGattCharacteristics != null) {
            Log.println(Log.ASSERT, TAG, "characteristic is not null: " + mGattCharacteristics.
                    getStringValue(0));


            switch (method) {
                case "init_user": {
                    // from initUser
                    try {
                        String user = edtTxtUser.getText().toString();
                        mBluetoothLeService.initUser(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "define_pump": {

                }
                break;
            }
            final int charaProp = mGattCharacteristics.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(mGattCharacteristics);
            }


            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = mGattCharacteristics;
                mBluetoothLeService.setCharacteristicNotification(
                        mGattCharacteristics, true);
                //txtReadCharacteristicData.setText("read Characteristic Value: " + mGattCharacteristics.getStringValue(0));

            }
        } else {
            Log.w(TAG, "Characteristic can't find");
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void getCharacteristicValue(String server, String characteristic) {
        mGattCharacteristics = mBluetoothLeService.getBluetoothGattCharacteristic(server, characteristic);
        // refer to BluetoothGatt Class readCharacteristic(characteristic)
        if (mGattCharacteristics != null) {
            Log.println(Log.ASSERT, TAG, "characteristic is not null: " + mGattCharacteristics.getStringValue(0));
            final int charaProp = mGattCharacteristics.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }


                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mBluetoothLeService.readCharacteristic(mGattCharacteristics);
                    }, 5000);


            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = mGattCharacteristics;
                mBluetoothLeService.setCharacteristicNotification(
                        mGattCharacteristics, true);
                // refer to BluetoothGatt Class readCharacteristic(characteristic)
                txtReadCharacteristicData.setText("received Command:  " + mGattCharacteristics.getStringValue(0));
            }
        } else {
            Log.w(TAG, "Characteristic can't find");
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }


}
