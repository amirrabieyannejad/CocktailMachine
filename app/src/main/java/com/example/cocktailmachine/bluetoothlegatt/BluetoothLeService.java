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
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.List;

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
    // Recipes UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_RECIPES =
            "Status Recipes Characteristic";
    // Cocktail UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_COCKTAIL =
            "Status Cocktail Characteristic";

    public final static UUID UUID_ADMIN_MESSAGE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid("Admin Message Characteristic"));
    public final static UUID UUID_USER_MESSAGE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid("User Message Characteristic"));
    public final static UUID UUID_STATUS_RECIPES_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid("Status Recipes Characteristic"));
    public final static UUID UUID_STATUS_COCKTAIL_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid("Status Cocktail Characteristic"));
    public final static UUID UUID_STATUS_LIQUIDS_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.
                    lookupUuid("Status Liquids Characteristic"));
    public final static UUID UUID_STATUS_STATE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid("Status State Characteristic"));
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


        /*@Override
        @SuppressLint("MissingPermission")
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
            } else {
                Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
            }

        }*/

        @Override
        @SuppressLint("MissingPermission")
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            /*super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> services = getSupportedGattServices();
            for (BluetoothGattService gattService : services) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(
                            UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                    Log.i(TAG, "characteristic:" + gattCharacteristic.getUuid() +
                            "Descriptor:" + descriptor.getValue().toString());

                    setCharacteristicNotification(gattCharacteristic, true);

                }
            }*/
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
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.d(TAG, "onCharacteristicRead: "+ characteristic.getStringValue(0));
            } else {
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }


        }

        @Override
        @SuppressLint("MissingPermission")
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }, 1000);

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
                UUID_STATUS_COCKTAIL_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_RECIPES_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_ADMIN_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_USER_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid())
        ) {
            System.out.println("UUID IS: " + characteristic.getUuid().toString());
            // For all other profiles, writes the data formatted in HEX.
           /* final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }*/
            /*final byte[] data = characteristic.getValue();
            String s = new String(data, StandardCharsets.UTF_8);
            intent.putExtra(EXTRA_DATA, s);*/
            //final String pin = characteristic.getStringValue(0);
            //intent.putExtra(EXTRA_DATA, String.valueOf(pin));
            intent.putExtra(EXTRA_DATA, characteristic.getStringValue(0));
            Log.w("broadcastUpdate: ", "has been read");
            //}

        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
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
        // such that resources are cleaned up properly.  In this particular example, close() is
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
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

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
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
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
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
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
                UUID_STATUS_COCKTAIL_CHARACTERISTIC.equals(characteristic.getUuid())||
                UUID_STATUS_RECIPES_CHARACTERISTIC.equals(characteristic.getUuid())||
                UUID_ADMIN_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid())||
                UUID_USER_MESSAGE_CHARACTERISTIC.equals(characteristic.getUuid())
            ) {
           System.out.println("UUID IS: "+ characteristic.getUuid().toString());
           BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
           descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
           mBluetoothGatt.writeDescriptor(descriptor);
           Log.w(TAG, "setCharacteristicNotification: " + BluetoothGattCharacteristic.PROPERTY_NOTIFY);
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
    public BluetoothGattCharacteristic getBluetoothGattCharacteristic(String service, String characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        BluetoothGattService mCustomService = mBluetoothGatt.getService(UUID.fromString(SampleGattAttributes.lookupUuid(service)));
        if (mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return null;
        }
        /* get the characteristic from the service */
        BluetoothGattCharacteristic mReadCharacteristic = mCustomService.getCharacteristic(UUID.fromString(SampleGattAttributes.lookupUuid(characteristic)));
        return mReadCharacteristic;
    }


    @SuppressLint("MissingPermission")
    public void writeCharacteristic(String value, Boolean admin) {
        /* check if the service is available on the device */
        BluetoothGattService mCustomService = mBluetoothGatt.getService(UUID.fromString(SampleGattAttributes.lookupUuid(SERVICE_READ_WRITE)));
        if (mCustomService == null) {
            Log.w(TAG, "Custom BLE Service not found");
            return;
        }
        BluetoothGattCharacteristic mWriteCharacteristic;
        /* get the characteristic from the service */
        if (admin) {
            mWriteCharacteristic = mCustomService.getCharacteristic(UUID.fromString(SampleGattAttributes.lookupUuid(CHARACTERISTIC_MESSAGE_ADMIN)));
        } else {
            mWriteCharacteristic = mCustomService.getCharacteristic(UUID.fromString(SampleGattAttributes.lookupUuid(CHARACTERISTIC_MESSAGE_USER)));
        }
        mWriteCharacteristic.setValue(value);
        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        mCustomService.addCharacteristic(mWriteCharacteristic);

        if (mBluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false) {
            Log.w(TAG, "Failed to write characteristic");
        }

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }


    /**
     * initUser: register as a new user and receive a user ID
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void initUser(String name) throws JSONException {
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
    }

    /**
     * define_recipe: defines a new recipe or changes an existing recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
      */

    @SuppressLint("MissingPermission")
    public void defineRecipe(float user,String name, ArrayList<Pair<String, Float>> liquids)
            throws JSONException {
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
    }
    /**
     * edit_Recipe: edit an existing recipe
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
      */
    @SuppressLint("MissingPermission")
    public void editRecipe(float user,String name, ArrayList<Pair<String, Float>> liquids)
            throws JSONException {
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
    }
    /**
     * addPump: add a new pump to ESP
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminRefillPump(String liquid, float volume, int slot) throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "refill_pump");
        jsonObject.put("user", 0);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
    }


    /**
     * calibratePumps: calibrate all pumps
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminCalibratePumps() throws JSONException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibrate_pumps");
        jsonObject.put("user", 0);
        String payload = jsonObject.toString();
        writeCharacteristic(payload, true);
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
    }



}
