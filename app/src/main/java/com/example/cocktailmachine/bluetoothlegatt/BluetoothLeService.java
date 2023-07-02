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
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;


import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothSingleton singleton;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // Communication UUID Wrappers
    public final static String SERVICE_READ_WRITE =
            "Communication Service";
    public final static String CHARACTERISTIC_MESSAGE_USER =
            "User Message Characteristic";
    public final static String CHARACTERISTIC_MESSAGE_ADMIN =
            "Admin Message Characteristic";

    // STATUS Attribute UUID Wrappers
    public final static String SERVICE_STATUS_STATE =
            "Status Service";
    public final static String CHARACTERISTIC_STATUS_STATE =
            "Status State Characteristic";
    public final static String CHARACTERISTIC_STATUS_LIQUIDS =
            "Status Liquids Characteristic";
    // Recipes Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_RECIPES =
            "Status Recipes Characteristic";
    // Cocktail Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_COCKTAIL =
            "Status Cocktail Characteristic";
    // Current User Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_CURRENT_USER =
            "Status Current user Characteristic";
    // Last Change Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_LAST_CHANGE =
            "Status Last Change Characteristic";
    // Pumps Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_PUMPS =
            "Status Pumps Characteristic";


    public final static UUID UUID_ADMIN_MESSAGE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Admin Message Characteristic"));
    public final static UUID UUID_USER_MESSAGE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("User Message Characteristic"));
    public final static UUID UUID_STATUS_PUMPS_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Pumps Characteristic"));
    public final static UUID UUID_STATUS_CURRENT_USER_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Current user Characteristic"));
    public final static UUID UUID_STATUS_LAST_CHANGE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Last Change Characteristic"));
    public final static UUID UUID_STATUS_RECIPES_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Recipes Characteristic"));
    public final static UUID UUID_STATUS_COCKTAIL_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Cocktail Characteristic"));
    public final static UUID UUID_STATUS_LIQUIDS_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Liquids Characteristic"));
    public final static UUID UUID_STATUS_STATE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status State Characteristic"));
    private String value;

    Boolean isReading = false;
    private String finalValue;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }


        @Override
        @SuppressLint("MissingPermission")
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        @SuppressLint("MissingPermission")
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicRead: " + characteristic.getStringValue(0));
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                //singleton = BluetoothSingleton.getInstance();
                //singleton.setEspResponseValue(characteristic.getStringValue(0));

                value = characteristic.getStringValue(0);
                Log.w(TAG, "value has been read!" + value);

            } else {
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }


        }

        @Override
        @SuppressLint("MissingPermission")
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // if the characteristic is the characteristic that we are looking for
        // then send it to activity through Intent -> EXTRA_DATA
        if (UUID_STATUS_LIQUIDS_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_STATE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_PUMPS_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_CURRENT_USER_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_LAST_CHANGE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_COCKTAIL_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_RECIPES_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_ADMIN_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_USER_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid())
        ) {
            System.out.println("UUID IS: " + characteristic.getUuid().toString());
            intent.putExtra(EXTRA_DATA, characteristic.getStringValue(0));
            Log.w("broadcastUpdate: ", "has been read");
        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            Log.w(TAG, "localBinder has been reached! type to getService" );
            return BluetoothLeService.this;
        }
    }

    // The BluetoothLeService needs a Binder implementation that provides access
// to the service for the activity.
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
            Log.w(TAG,"get Instance of BluetoothManager! ");
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.w(TAG,"Adapter has been read!");
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange
     * (android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        Log.w(TAG, "BluetoothAdapter is initialized! Address is specified!");
        // Previously connected device.  Try to reconnect.
        if (address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        Log.w(TAG, "Device found.  Able to connect.");
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
                         @Override
                         public void run() {
                             mBluetoothGatt = device.connectGatt(getApplicationContext(),
                                     false, mGattCallback);
                             Log.w(TAG, "Connection has been established!");
                         }
    });

        mBluetoothDeviceAddress = address;
        return true;
    }


    public String getCharacteristicValue(String server, String characteristic) {
        BluetoothGattCharacteristic mGattCharacteristics;
        mGattCharacteristics = getBluetoothGattCharacteristic(server, characteristic);
        // refer to BluetoothGatt Class readCharacteristic(characteristic)
        if (mGattCharacteristics != null) {
            Log.w(TAG, "characteristic is not null: " +
                    mGattCharacteristics.getStringValue(0));
            final int charaProp = mGattCharacteristics.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                Handler handler = new Handler();
                handler.postDelayed(() -> readCharacteristic(mGattCharacteristics), 5000);
                System.out.println("get Characteristic Value" +
                        mGattCharacteristics.getStringValue(0));
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = mGattCharacteristics;
                setCharacteristicNotification(
                        mGattCharacteristics, true);
                // refer to BluetoothGatt Class readCharacteristic(characteristic)
            }
        } else {
            Log.w(TAG, "Characteristic can't find");
            return ("Characteristic can't find");
        }
        Log.w(TAG, "Characteristic found:" + mGattCharacteristics.getStringValue(0));
        return (mGattCharacteristics.getStringValue(0));
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange
     * (android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    @SuppressLint("MissingPermission")
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead
     * (android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from it.
     */
    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enable        If true, enable notification.  False otherwise.
     */
    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enable) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);


        if (UUID_STATUS_LIQUIDS_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_STATE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_COCKTAIL_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_RECIPES_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_ADMIN_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_USER_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_PUMPS_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_CURRENT_USER_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_LAST_CHANGE_CHARACTERISTIC.equals(characteristic.getUuid())
        ) {
            System.out.println("UUID IS: " + characteristic.getUuid().toString());
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            Log.w(TAG, "setCharacteristicNotification: " +
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY);
        }
    }

    /**
     * Read Characteristics from the device that have a value to read
     * receive a message along with Read on {@code BluetoothGattCharacteristic} from to the Device.
     * It will be use to read Services like: State, Recipes, Cocktail, Liquids
     *
     * @param service        receive specific Service
     * @param characteristic receive characteristic of Service
     * @return A {@code BluetoothGattCharacteristic} to read their Value
     */
    @SuppressLint("MissingPermission")
    public BluetoothGattCharacteristic getBluetoothGattCharacteristic
    (String service, String characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        Log.w(TAG, "getBluetoothGattCharacteristic mBluetoothAdapter" +
                " and mBluetoothGatt are available!");
        BluetoothGattService mCustomService = mBluetoothGatt.getService
                (UUID.fromString(SampleGattAttributes.lookupUuid(service)));
        if (mCustomService == null) {
            Log.w(TAG, "getBluetoothGattCharacteristic: Custom BLE Service not found");

            //return null;
        }
        /* get the characteristic from the service */
        Log.w(TAG, "Characteristic found, try to return it");
        return (mCustomService.getCharacteristic(UUID.fromString
                (SampleGattAttributes.lookupUuid(characteristic))));

    }


    @SuppressLint("MissingPermission")
    public Boolean writeCharacteristic(String value, Boolean admin) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        /* check if the service is available on the device */
        BluetoothGattService mCustomService = mBluetoothGatt.getService
                (UUID.fromString(SampleGattAttributes.lookupUuid(SERVICE_READ_WRITE)));
        if (mCustomService == null) {
            Log.w(TAG, "write Characteristic: Custom BLE Service not found");
            return false;
        }
        BluetoothGattCharacteristic mWriteCharacteristic;
        /* get the characteristic from the service */
        if (admin) {
            mWriteCharacteristic = mCustomService.getCharacteristic
                    (UUID.fromString(SampleGattAttributes.lookupUuid(CHARACTERISTIC_MESSAGE_ADMIN)));
        } else {
            mWriteCharacteristic = mCustomService.getCharacteristic
                    (UUID.fromString(SampleGattAttributes.lookupUuid(CHARACTERISTIC_MESSAGE_USER)));
        }
        mWriteCharacteristic.setValue(value);
        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        mCustomService.addCharacteristic(mWriteCharacteristic);
        Log.w(TAG, "Characteristic has been written: " + mWriteCharacteristic.getStringValue(0));

        if (!mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
            Log.w(TAG, "Failed to write characteristic");
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public String readCharacteristicValue(String server, String characteristic)
            throws InterruptedException {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter or Bluetooth Gatt not initialized");
            return null;
        }
                BluetoothGattCharacteristic mGattCharacteristics;
        mGattCharacteristics = getBluetoothGattCharacteristic(server, characteristic);
        value = mGattCharacteristics.getStringValue(0);
        if (mGattCharacteristics != null) {
            Log.w(TAG, "characteristic is : " + value);
        }

        @SuppressLint("HandlerLeak") Handler handle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                value = finalValue;
                Log.w(TAG, "handle Message! " + finalValue);
            }
        };
        Runnable run = new Runnable() {
            @Override
            public void run() {
                while (!mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                    Log.w(TAG,"Is reading in Progess? " );
                    synchronized (this) {
                        try{
                            Log.w(TAG, "we will wait");
                            wait(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                handle.sendEmptyMessage(0);
            }
        };
        Thread newThread = new Thread(run);
        newThread.start();
/*        while (isReading) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/

        Log.w(TAG, "characteristic has been changed: " +
                value);
        //singleton.setEspResponseValue(value);
        return value;
    }
    /**
     * initUser: register as a new user and receive a user ID
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void initUser(String name) throws JSONException, InterruptedException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "init_user");
        jsonObject.put("name", name);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
        readCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);

    }

    /**
     * reset: reset the machine so that it can make a new cocktail
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void reset(float user) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "reset");
        jsonObject.put("user", user);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
       getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
    }
    /**
     * reset: Cancels the current recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void abort(float user) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "abort");
        jsonObject.put("user", user);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
    }

    /**
     * make_recipe: mixes the recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void makeRecipe(float user, String recipe) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "make_recipe");
        jsonObject.put("user", user);
        jsonObject.put("recipe", recipe);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);

    }

    /**
     * addLiquid: Adds liquid to the cocktail
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void addLiquid(float user,String liquid, float volume) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "add_liquid");
        jsonObject.put("user", user);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
    }

    /**
     * define_recipe: defines a new recipe or changes an existing recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
      */

    @SuppressLint("MissingPermission")
    public void defineRecipe(float user,String name, ArrayList<Pair<String, Float>> liquids)
            throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONArray arrayLiquids = new JSONArray();
        int i = 0;
        for (Pair p : liquids) {
            JSONArray arrayPair = new JSONArray();
            arrayPair.put(i, p.first);
            arrayPair.put(i + 1, p.second);
            arrayLiquids.put(arrayPair);
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "define_recipe");
        jsonObject.put("user", user);
        jsonObject.put("name", name);
        jsonObject.put("liquids", arrayLiquids);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, false);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
    }
    /**
     * edit_Recipe: edit an existing recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
      */
    @SuppressLint("MissingPermission")
    public void editRecipe(float user,String name, ArrayList<Pair<String, Float>> liquids)
            throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
            // generate JSON Format
            JSONArray arrayLiquids = new JSONArray();
            int i = 0;
            for (Pair p : liquids) {
                JSONArray arrayPair = new JSONArray();
                arrayPair.put(i, p.first);
                arrayPair.put(i + 1, p.second);
                arrayLiquids.put(arrayPair);
            }
            // generate JSON Format
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "edit_recipe");
            jsonObject.put("user", user);
            jsonObject.put("name", name);
            jsonObject.put("liquids", arrayLiquids);
            String payload = jsonObject.toString();
            writeCharacteristic(payload, false);
            getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                    BluetoothLeService.CHARACTERISTIC_MESSAGE_USER);
        }


    /**
     * addPump: add a new pump to ESP
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminDefinePump(String liquid, float volume, int slot) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "define_pump");
        jsonObject.put("user", 0);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }
    /**
     * addPump: add a new pump to ESP
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminRefillPump(float volume, int slot) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "refill_pump");
        jsonObject.put("user", 0);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * addPump: Runs the pump for a certain time. The time is given in milliseconds.
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    // TODO add to DeviceControlActivity
    @SuppressLint("MissingPermission")
    public void adminRunPump(int slot, int time) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "run_pump");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time", time);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * calibratePump: For calibration, two measured values must be available for which the pump
     * has been running for a different time. This is then used to calculate the flow rate and
     * the pump rate. The times are given in milliseconds and the liquids in millilitres.
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    // TODO add to DeviceControlActivity
    @SuppressLint("MissingPermission")
    public void adminCalibratePump(int slot, int time1, int time2, float volume1, float volume2)
            throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibrate_pump");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time1", time1);
        jsonObject.put("time2", time2);
        jsonObject.put("volume1", volume1);
        jsonObject.put("volume2", volume2);

        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * set_pump_times: sets the calibration values for a pump-time_init is the lead time
     * and time_init is the return time in milliseconds. Normally these values should be similar
     * or the same. The rate is given in mL/ms.
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    // TODO add to DeviceControlActivity
    @SuppressLint("MissingPermission")
    public void adminSetPumpTimes(int slot, int timeInit, int timeReverse, float rate) throws
            JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "set_pump_times");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time_init", timeInit);
        jsonObject.put("time_reverse", timeReverse);
        jsonObject.put("rate", rate);

        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * tare_scale: Tares the scale
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    // TODO add to DeviceControlActivity
    @SuppressLint("MissingPermission")
    public void adminTareScale() throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "tare_scale");
        jsonObject.put("user", 0);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * restart: restart the machine
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminRestart(Boolean restFactory) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "restart");
        jsonObject.put("user", 0);
        if (restFactory) {
            jsonObject.put("factory_reset", true);
        }
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }
    /**
     * Clean: Clean the machine
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminClean() throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "clean");
        jsonObject.put("user", 0);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
        getCharacteristicValue(BluetoothLeService.SERVICE_READ_WRITE,
                BluetoothLeService.CHARACTERISTIC_MESSAGE_ADMIN);
    }

    /**
     * adminPumpsStatus: Map of all available pumps and their level
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadPumpsStatus() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_PUMPS);
    }

    /**
     * adminReadCurrentUser: the current user for whom a cocktail is being made
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadCurrentUser() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_CURRENT_USER);
    }

    /**
     * adminReadLastChange: Last Change Characteristic: If the timestamp has not changed,
     * the available recipes and ingredients are still the same.The timestamp is an internal
     * value of the ESP and has no relation to the real time.
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadLastChange() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_LAST_CHANGE);
    }

    /**
     * adminReadLiquidsStatus: Map of all available liquids and their volumes
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadLiquidsStatus() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_LIQUIDS);
    }

    /**
     * adminReadState: The current state of the cocktail machine and what it does.
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     init: Maschine wird initialisiert
     ready: Maschine ist bereit einen Befehl auszuführen und wartet
     mixing: Maschine macht einen Cocktail
     pumping: Maschine pumpt Flüssigkeiten
     cocktail done: Cocktail ist fertig zubereitet und kann entnommen werden. Danach sollte
     reset ausgeführt werden.
     */
    @SuppressLint("MissingPermission")
    public void adminReadState() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_STATE);
    }

    /**
     * adminReadRecipesStatus: Status of all saved recipes and their names
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadRecipesStatus() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_RECIPES);
    }

    /**
     * adminReadCurrentCocktail: The content of the current cocktail being mixed.
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadCurrentCocktail() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        getCharacteristicValue(BluetoothLeService.SERVICE_STATUS_STATE,
                BluetoothLeService.CHARACTERISTIC_STATUS_COCKTAIL);
    }

}
