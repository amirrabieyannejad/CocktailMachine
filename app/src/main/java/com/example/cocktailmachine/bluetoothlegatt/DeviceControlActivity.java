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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
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

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // Communication UUID Wrappers
    public final static String SERVICE_READ_WRITE =
            "Communication Service";

    // State UUID Wrappers
    public final static String SERVICE_READ_STATE =
            "Status Service";
    public final static String CHARACTERISTIC_READ_STATE =
            "State Characteristic";
    public final static String CHARACTERISTIC_READ_LIQUIDS =
            "Liquids Characteristic";
    private BluetoothGatt mBluetoothGatt;

    // Recipes UUID Wrappers
    public final static String CHARACTERISTIC_READ_RECIPES =
            "Recipes Characteristic";

    // Cocktail UUID Wrappers

    public final static String CHARACTERISTIC_READ_COCKTAIL =
            "Cocktail Characteristic";

    private TextView mConnectionState;
    private TextView txtNotificationData;

    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private EditText edtTxtUser;
    private EditText edtTxtRecipe;
    private EditText edtTxtLiquid;
    private TextView txtReadCharacteristicData;


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
            /*} else if (!BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Toast.makeText(DeviceControlActivity.this, "Sevices has been dicoverd", Toast.LENGTH_SHORT).show();*/
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
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        txtNotificationData.setText(R.string.txt_notificationData);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_transfer);
        requestBlePermissions(this, PERMISSIONS_REQUEST_CODE);
/*
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*/
        final Intent intent = getIntent();
        String mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        //String mDeviceName="Cocktail Machine";


        txtNotificationData = findViewById(R.id.txtNotificationData);

        //getActionBar().setTitle(mDeviceName);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnClean = findViewById(R.id.btnClean);
        Button btnAddPump = findViewById(R.id.btnAddPump);
        Button btnCalibrate = findViewById(R.id.btnCalibrate);
        Button btnMakeRecipe = findViewById(R.id.btnMakeRecipe);
        Button btnAddLiquid = findViewById(R.id.btnAddLiquid);
        Button btnDefineRecipe = findViewById(R.id.btnDefineRecipe);
        Button btnReadLiquids = findViewById(R.id.btnReadLiquids);
        Button btnReadCocktail = findViewById(R.id.btnReadCocktail);
        Button btnReadRecipes = findViewById(R.id.btnReadRecipes);
        Button btnReadState = findViewById(R.id.btnReadState);

        edtTxtUser = findViewById(R.id.edtTxtUser);
        edtTxtRecipe = findViewById(R.id.edtTxtRecipe);
        edtTxtLiquid = findViewById(R.id.edtTxtLiquid);
        txtReadCharacteristicData = findViewById(R.id.txtReadCharacteristicData);

        EditText edtTxtVolume = findViewById(R.id.edtTxtVolume);

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
            setCharacteristicReadValue(SERVICE_READ_STATE, CHARACTERISTIC_READ_COCKTAIL);
        });

        //Read Liquids
        btnReadLiquids.setOnClickListener(v -> {
            setCharacteristicReadValue(SERVICE_READ_STATE, CHARACTERISTIC_READ_LIQUIDS);
        });

        // Read State
        btnReadState.setOnClickListener(v -> {
            setCharacteristicReadValue(SERVICE_READ_STATE, CHARACTERISTIC_READ_STATE);
        });

        // Read saved Recipes from Device
        btnReadRecipes.setOnClickListener(v -> {
            setCharacteristicReadValue(SERVICE_READ_STATE, CHARACTERISTIC_READ_RECIPES);
        });
        //Initiate User
        btnAddUser.setOnClickListener(v -> {
            getResponseValue(false);
        });

        // Reset the machine so that it can make a new cocktail
        btnReset.setOnClickListener(v -> {

            try {
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                mBluetoothLeService.reset(userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // Clean the machine
        btnClean.setOnClickListener(v -> {

            try {
                mBluetoothLeService.adminClean();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Calibrate all pumps
        btnCalibrate.setOnClickListener(v -> {
            try {
                mBluetoothLeService.adminCalibratePumps();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // Add Pump
        btnAddPump.setOnClickListener(v -> {
            try {
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                mBluetoothLeService.adminAddPump(edtTxtLiquid.getText().toString(), volume1);
                /*if (mBluetoothLeService != null) {
                    mBluetoothLeService.readCharacteristic();
                } else {
                    Log.w(TAG, "Something Wrong! Service has been stoped");
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Add Liquid
        btnAddLiquid.setOnClickListener(v -> {
            try {
                float volume1 = Float.parseFloat(edtTxtVolume.getText().toString());
                mBluetoothLeService.adminDefineLiquid(edtTxtLiquid.getText().toString(), volume1);
                //mBluetoothLeService.readCharacteristic();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        // make Recipe mix the recipe
        btnMakeRecipe.setOnClickListener(v -> {
            try {
                float userId = Float.parseFloat(edtTxtUser.getText().toString());
                mBluetoothLeService.makeRecipe(userId, edtTxtRecipe.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Define Recipe
        btnDefineRecipe.setOnClickListener(v -> {
            try {
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
                mBluetoothLeService.adminDefineRecipe(edtTxtRecipe.getText().toString(), liquids);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            txtNotificationData.setText("Notification Data: "+data);
        });
    }

    // Demonstrate how to read response characteristic form deice when through
    // Characteristic "Message" one message deliver to device. This is response
    // from received from app
    private void getResponseValue(Boolean admin) {
        // This should be change due to no more existences of Characteristic_read
        // it will be replace to "User Message Characteristic"
        // receive Notification from Server will be handled by given notify as broadcasts
        if (admin) {
            mGattCharacteristics = mBluetoothLeService.getBluetoothGattCharacteristic(
                    SERVICE_READ_WRITE, BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
        } else {
            mGattCharacteristics = mBluetoothLeService.getBluetoothGattCharacteristic(
                    SERVICE_READ_WRITE, BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
        }

        if (mGattCharacteristics != null) {
            Log.println(Log.ASSERT, TAG, "characteristic is not null: " + mGattCharacteristics.
                    getStringValue(0));
            final int charaProp = mGattCharacteristics.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }

                // from initUser
                try {
                    String user = edtTxtUser.getText().toString();
                    //mDataField.setText(user);
                    mBluetoothLeService.initUser(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                txtReadCharacteristicData.setText("read Characteristic Value: " + mGattCharacteristics.getStringValue(0));
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = mGattCharacteristics;
                mBluetoothLeService.setCharacteristicNotification(
                        mGattCharacteristics, true);
                setCharacteristicReadValue(BluetoothLeService.SERVICE_READ_WRITE,BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
                //txtReadCharacteristicData.setText("read Characteristic Value: " + mGattCharacteristics.getStringValue(0));
            }
        } else {
            Log.w(TAG, "Characteristic can't find");
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void setCharacteristicReadValue(String server, String characteristic) {
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
                // refer to BluetoothGatt Class readCharacteristic(characteristic)
                mBluetoothLeService.readCharacteristic(mGattCharacteristics);
                txtReadCharacteristicData.setText("read Characteristic Value: " + mGattCharacteristics.getStringValue(0));
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = mGattCharacteristics;
                mBluetoothLeService.setCharacteristicNotification(
                        mGattCharacteristics, true);
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
