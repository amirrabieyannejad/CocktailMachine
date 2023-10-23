package com.example.cocktailmachine.bluetoothlegatt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.CalibrateStatus;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.ErrorStatus;
import com.example.cocktailmachine.data.enums.Postexecute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothSingleton {

    private final static String TAG = BluetoothSingleton.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 191;
    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    //Variable
    private static BluetoothSingleton instance;
    public String EspResponseValue;
    public String EspDeviceName;
    public String EspDeviceAddress;
    public BluetoothLeService mBluetoothLeService;
    private final Boolean state = false;
    private BluetoothSingleton singleton;
    // Code to manage Service lifecycle.


    ///////////////////// START TO GET RIDE OF SERVICE/////////////////////////////
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
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
    public final static String CHARACTERISTIC_STATUS_USER_QUEUE =
            "Status User Queue Characteristic";
    // Last Change Status UUID Wrappers
    public final static String CHARACTERISTIC_STATUS_LAST_CHANGE =
            "Status Last Change Characteristic";
    public final static String CHARACTERISTIC_STATUS_SCALE =
            "Status Scale Characteristic";
    public final static String CHARACTERISTIC_STATUS_ERROR =
            "Status Error Characteristic";

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
    public final static UUID UUID_STATUS_USER_QUEUE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status User Queue Characteristic"));
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
    public final static UUID UUID_STATUS_SCALE_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Scale Characteristic"));
    public final static UUID UUID_STATUS_ERROR_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.lookupUuid
                    ("Status Error Characteristic"));


    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public String value;
    private String finalValue;
    public String result;
    private Thread threadWaitForBroadcast;
    private Thread threadReadFirstValue;
    private Thread threadReadSecondValue;
    private Thread threadConnection;
    private Thread threadDisconnect;
    private Thread threadWriteCharacteristic;
    private Thread threadOnReadCharacteristic;


    public boolean connect = false;
    private boolean isReading;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                //broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        singleton.mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                //broadcastUpdate(intentAction);
            }
        }

        @Override
        @SuppressLint("MissingPermission")
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                connect = true;
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.w(TAG, "onServicesDiscovered received: Services has been discovered! ");
            } else {
                connect = false;
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
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                singleton = BluetoothSingleton.getInstance();
                byte[] data = characteristic.getValue();
                String finalValue = new String(data, StandardCharsets.UTF_8);

                // START
                //if (finalValue.equals("\"processing\"") ||
                //        finalValue.equals("\"ready\"") || finalValue == "") {
                if (finalValue.equals("processing")) {
                Log.w(TAG, "onCharacteristicRead: read Alert Notification: " +
                            finalValue);
                    singleton.mBluetoothGatt.readCharacteristic(characteristic);
                } else {
                    Log.w(TAG, "read Response Value!" + finalValue);
                    singleton.setEspResponseValue(finalValue);
                }
                singleton.setEspResponseValue(
                        characteristic.getStringValue(0));
                Log.w(TAG, "onCharacteristicRead: read Alert Notification: " +
                        singleton.getEspResponseValue());
                /*Handler handle = new Handler();
                Runnable readResponse = () -> {
                    Log.w(TAG, "onCharacteristicRead: Start!");
                    singleton = BluetoothSingleton.getInstance();

                    Log.w(TAG, "onCharacteristicRead: " +
                            singleton.getEspResponseValue());
                    };
                handle.postDelayed(readResponse,3000);*/
                //END
                //singleton.setEspResponseValue(characteristic.getStringValue(0));
/*                finalValue = characteristic.getStringValue(0);
                if (finalValue.equals("\"processing\"") ||
                        finalValue.equals("\"ready\"") || finalValue == "") {
                    Log.w(TAG, "onCharacteristicRead: read Alert Notification: " + finalValue);
                } else {
                    Log.w(TAG, "read Response Value!" + finalValue);
                    singleton.setEspResponseValue(finalValue);
                }
                isReading = false;
                synchronized (this) {
                    isReading = false;
                    this.notifyAll();
                }*/

            } else {
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }


        }

        @Override
        @SuppressLint("MissingPermission")
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.d(TAG, "onCharacteristicChange: " + characteristic.getStringValue(0));

        }
    };

    private boolean initialize(Activity activity) {
        singleton = BluetoothSingleton.getInstance();
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (singleton.mBluetoothManager == null) {
            singleton.mBluetoothManager =
                    (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            if (singleton.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
            Log.w(TAG, "get Instance of BluetoothManager! ");
        }

        singleton.mBluetoothAdapter = singleton.mBluetoothManager.getAdapter();
        if (singleton.mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        Log.w(TAG, "Adapter has been read!");
        return true;
    }

    @SuppressLint("MissingPermission")
    private boolean connect(Activity activity, String address) {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        singleton.requestBlePermissions(activity);
        Log.w(TAG, "BluetoothAdapter is initialized! Address is specified!");
        // Previously connected device.  Try to reconnect.
        //if (address.equals("54:43:B2:A9:32:26")
        if (address.equals(singleton.getEspDeviceAddress())
                && singleton.mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return singleton.mBluetoothGatt.connect();
        }

        final BluetoothDevice device = singleton.mBluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        Log.w(TAG, "Device found.  Able to connect.");
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.

        Runnable connection = new Runnable() {
            @Override
            public void run() {
                int timeout = 1500;
                int timeoutMax = 0;
                while (!connect) {
                    try {
                        Log.w(TAG, "Connection: Connection to ESP GATT Server Failed!" +
                                " try again after ..."
                                + timeout + "ms");
                        singleton.mBluetoothGatt = device.connectGatt
                                (activity.getApplicationContext(),
                                        false, singleton.mGattCallback);
                        Thread.sleep(timeout);
                        timeoutMax = timeoutMax + 500;
                        if (timeoutMax == 2000) {
                            Log.w(TAG, "Connection: Timeout, unable to " +
                                    "connect ESP");
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        };
        threadConnection = new Thread(connection);
        threadConnection.start();

        Log.w(TAG, "Connection has been established! try to check the connection!");
        if (mBluetoothGatt == null) {
            Log.w(TAG, "Connection was corrupt! try to reconnect!");
            singleton.mBluetoothGatt = device.connectGatt(activity.getApplicationContext(),
                    false, singleton.mGattCallback);
        }
        if (mBluetoothGatt == null) {
            Log.w(TAG, "Connection failed!");
        }
        singleton.mBluetoothDeviceAddress = address;
        return true;
    }

    @SuppressLint("MissingPermission")
    public Boolean connectGatt(Activity activity) {
        Log.w(TAG, "try to initialize BluetoothGATT!");
        singleton = BluetoothSingleton.getInstance();
        if (!singleton.initialize(activity)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            return false;
        }
        // Automatically connects to the device upon successful start-up initialization.

        //Log.w(TAG, "try to connect: " + singleton.EspDeviceAddress);
        //singleton.connect(activity, "78:E3:6D:1A:87:9E");
        singleton.connect(activity, singleton.getEspDeviceAddress());
        return true;
    }

    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

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
                UUID_STATUS_USER_QUEUE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_SCALE_CHARACTERISTIC.equals(characteristic.getUuid()) ||
                UUID_STATUS_ERROR_CHARACTERISTIC.equals(characteristic.getUuid()) ||
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


    @SuppressLint("MissingPermission")
    public String readCharacteristicValue(String server, String characteristic) throws
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter or Bluetooth Gatt not initialized");
            return null;
        }
        BluetoothGattCharacteristic mGattCharacteristics;
        mGattCharacteristics = getBluetoothGattCharacteristic(server, characteristic);

        if (mGattCharacteristics != null) {
            singleton.value = mGattCharacteristics.getStringValue(0);
            Log.w(TAG, "Desired command to send is : " + singleton.value);


            //Handler handler1 = new Handler();
            //handler1.postDelayed(() -> singleton.readCharacteristic(mGattCharacteristics),
              //      2000);
            Log.w(TAG, "readCharactersitc first");

/*            @SuppressLint("HandlerLeak") Handler handle = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //singleton.value = finalValue;

                    Log.w(TAG, "readFirstValueNotification" );
                }
            };

            Runnable readFirstValue = new Runnable() {
                @Override
                public void run() {
                    int timeout = 500;
                    while (!singleton.mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                        Log.w(TAG, "readFirstValue: Is reading Characteristic  in Process!");

                        try {
                            Log.w(TAG, "readFirstValue: read Characteristic: Reading from ESP " +
                                    "is in Process!"
                                    + timeout);

                            Thread.sleep(timeout);
                            timeout = timeout + 500;
                            if (timeout == 3000) {
                                Log.w(TAG, "run:read Characteristic: Timeout, unable to " +
                                        "read value from ESP");
                                break;
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    isReading = true;
                    handle.sendEmptyMessage(0);
                }
            };
            threadReadFirstValue = new Thread(readFirstValue);
            threadConnection.join();
            threadReadFirstValue.start();*/

            @SuppressLint("HandlerLeak")
            Handler handle2 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //singleton.value = finalValue;

                    Log.w(TAG, "readSecondValue " );
                }
            };

            Runnable readSecondValue = new Runnable() {
                @Override
                public void run() {
                    int timeout = 500;
                    int timeoutMax = 0;
                    while (!singleton.mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                        Log.w(TAG, "run2: Is reading Characteristic  in Process!");

                        try {
                            Log.w(TAG, "run2: read Characteristic: Reading from ESP is in Process!"
                                    + timeout);

                            Thread.sleep(timeout);
                            timeoutMax = timeoutMax + 500;
                            if (timeoutMax == 3000) {
                                Log.w(TAG, "run: read Characteristic: Timeout, unable to " +
                                        "read value from ESP");
                                break;
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    isReading = true;
                    handle2.sendEmptyMessage(0);
                }
            };
            threadReadSecondValue = new
                    Thread(readSecondValue);
            //threadReadFirstValue.join();
            threadReadSecondValue.start();

            Log.w(TAG, "readCharactersitc second");

        } else {
            Log.w(TAG, "Characteristic can't find");
            return ("Characteristic can't find");
        }
        /*
        @SuppressLint("HandlerLeak")
        Handler handle3 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //singleton.value = finalValue;

                Log.w(TAG, "disconnectGatt ");
            }
        };
        Runnable runDisconnectGatt = new Runnable() {
            @Override
            public void run() {
                singleton = BluetoothSingleton.getInstance();

                handle3.sendEmptyMessage(0);
            }
        };

         */
        //threadDisconnect = new

             //   Thread(runDisconnectGatt);
        //threadReadSecondValue.join();
        //threadDisconnect.start();

        Log.w(TAG, "try to read Response Value from specific Characteristic!");
        return singleton.value;

    }



    @SuppressLint("MissingPermission")
    public Boolean writeCharacteristic(String value, Boolean admin) throws InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }

        // New ///////////////////////////////
        @SuppressLint("HandlerLeak")
        Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //singleton.value = finalValue;

                Log.w(TAG, "writeCharacteristic");
            }
        };
        Runnable writeCharacteristic = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                int timeoutMax = 0;
                while (!connect) {
                    Log.w(TAG, "writeCharacteristic: wait for Connection!");

                    try {
                        Log.w(TAG, "writeCharacteristic: wait for Connection!"
                                + timeout);

                        Thread.sleep(timeout);
                        timeoutMax = timeoutMax + 500;
                        if (timeoutMax == 3000) {
                            Log.w(TAG, "writeCharacteristic:  " +
                                    "Timeout, unable to " +
                                    "connect ESP");
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Log.w(TAG, "writeCharacteristic: after while");
                    singleton = BluetoothSingleton.getInstance();
                    BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
                            (UUID.fromString(SampleGattAttributes.lookupUuid(SERVICE_READ_WRITE)));
                    if (mCustomService == null) {
                        Log.w(TAG, "write Characteristic: Custom BLE Service not found");

                    }
                    BluetoothGattCharacteristic mWriteCharacteristic;
                    // get the characteristic from the service
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
                    Log.w(TAG, "Characteristic has been written. The value is now: " +
                            mWriteCharacteristic.getStringValue(0));


                    if (!singleton.mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
                        Log.w(TAG, "Failed to write characteristic");

                }
                handle.sendEmptyMessage(0);
            }
        };
        threadWriteCharacteristic = new

                Thread(writeCharacteristic);
        threadConnection.join();
        threadWriteCharacteristic.start();

        // EndNew ///////////////////////////////
        /* check if the service is available on the device */
        /*
        BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
                (UUID.fromString(SampleGattAttributes.lookupUuid(SERVICE_READ_WRITE)));
        if (mCustomService == null) {
            Log.w(TAG, "write Characteristic: Custom BLE Service not found");
            return false;
        }
        BluetoothGattCharacteristic mWriteCharacteristic;
        // get the characteristic from the service
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
        Log.w(TAG, "Characteristic has been written. The value is now: " +
                mWriteCharacteristic.getStringValue(0));


        if (!singleton.mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
            Log.w(TAG, "Failed to write characteristic");
            return false;
        }

         */

        return true;

    }

    @SuppressLint("MissingPermission")
    public BluetoothGattCharacteristic getBluetoothGattCharacteristic
            (String service, String characteristic) {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        Log.w(TAG, "getBluetoothGattCharacteristic: BluetoothAdapter" +
                " and BluetoothGatt are still available!");
        BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
                (UUID.fromString(SampleGattAttributes.lookupUuid(service)));
        if (mCustomService == null) {
            Log.w(TAG, "getBluetoothGattCharacteristic: Custom BLE Service not found");
            return null;
        }
        /* get the characteristic from the service */
        Log.w(TAG, "Desired Characteristic found. Prepare for writing on it!");
        return (mCustomService.getCharacteristic(UUID.fromString
                (SampleGattAttributes.lookupUuid(characteristic))));

    }


    ///////////////////////////END TO GET RIDE OF SERVICE


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
            singleton = BluetoothSingleton.getInstance();
            boolean mConnected;
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                singleton.setEspResponseValue(null);
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.w(TAG, "Broadcast Receiver Action Data Available");

            }
        }
    };

    //Constructor
    private BluetoothSingleton() {

    }

    public static BluetoothSingleton getInstance() {
        if (instance == null) {
            instance = new BluetoothSingleton();
        }
        return instance;

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public String getEspDeviceName() {
        return this.EspDeviceName;
    }

    public void setEspDeviceName(String espDeviceName) {
        this.EspDeviceName = espDeviceName;
    }

    public String getEspDeviceAddress() {
        return this.EspDeviceAddress;
    }

    public void setEspDeviceAddress(String espDeviceAddress) {
        this.EspDeviceAddress = espDeviceAddress;
    }

    public String getEspResponseValue() {
        return this.EspResponseValue;
    }

    public void setEspResponseValue(String espResponseValue) {
        this.EspResponseValue = espResponseValue;
    }

    private String displayData(String data) {
        if (data != null) {
            singleton = BluetoothSingleton.getInstance();
            //singleton.setEspResponseValue(data);
            Log.w(TAG, "Action Data Available triggered!" + singleton.getEspResponseValue());
            return singleton.getEspResponseValue();
        }
        return null;
    }

    public void registerReceiver(Activity activity) {
        singleton = BluetoothSingleton.getInstance();
        activity.registerReceiver(singleton.mGattUpdateReceiver,
                makeGattUpdateIntentFilter());
        if (singleton.mBluetoothGatt == null) {
            final boolean status = singleton.connectGatt(activity);
            Log.d("onResume:", "Connect request: " + status);
        }

    }

    public void unRegisterService(Activity activity) {
        activity.unregisterReceiver(mGattUpdateReceiver);

    }

    public void requestBlePermissions(Activity activity) {
        int requestCode = PERMISSIONS_REQUEST_CODE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    @SuppressLint("MissingPermission")
    private void waitForBroadcastReceiver1(TextView textView) {
        singleton = BluetoothSingleton.getInstance();

        Runnable myRunnable = () -> {
            int timeout = 500;
            int timeoutMax = 0;
            Log.w(TAG, "wait for broadcastreceiver: " + singleton.getEspResponseValue());
            Log.w(TAG, "wait for broadcastreceiver: " + finalValue);
            while (singleton.getEspResponseValue() == null) {
                try {
                    Thread.sleep(timeout);
                    timeoutMax = timeoutMax + 500;
                    Log.w("onClickListener", "We wait to receive a Broadcast" +
                            "Update!");
                    if (timeoutMax == 5000) {
                        Log.w(TAG, "Timeout has been trigger");
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String updateWords = singleton.getEspResponseValue();
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(updateWords);
                    }
                });
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }

    @SuppressLint("MissingPermission")
    private void waitForBroadcastReceiver() throws InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.result = null;
        @SuppressLint("HandlerLeak") Handler handle = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                //singleton.value = finalValue;
                result = singleton.getEspResponseValue();
                Log.w(TAG, "waitForBroadcastReceiver"
                        + singleton.result);
            }
        };

        Runnable waitForBroadcast = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                int timeoutMax = 0;
                try {


                    while (singleton.getEspResponseValue() == null
                            || singleton.getEspResponseValue().equals("\"processing\"")) {
                        synchronized (this) {
                            try {
                                Log.w(TAG, "wait for target value...!" +
                                        singleton.getEspResponseValue());
                                Thread.sleep(timeout);
                                timeoutMax = timeoutMax + 500;
                                if (timeoutMax == 5000) {
                                    break;
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                handle.sendEmptyMessage(0);
            }
        };
        threadWaitForBroadcast = new Thread(waitForBroadcast);
        threadReadSecondValue.join();
        threadWaitForBroadcast.start();
    }

    private void send(JSONObject jsonObject , Boolean admin, Boolean write, String service,
                      String characteristic
    ) throws InterruptedException, JSONException {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter or BluetoothGatt are not initialized");
            return;
        }
        singleton.setEspResponseValue(null);
        //singleton.connect(activity, "54:43:B2:A9:32:26");
        Log.w(TAG, "sendMethod: BluetoothAdapter initialized!");
        //generate JSON Format
        if (write) {
            singleton.writeCharacteristic(jsonObject.toString(), admin);
        }
        Log.w(TAG, "send: finish writing");
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
                    try {
                        singleton.readCharacteristicValue(service,
                                characteristic);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                     2000);


    }

    private void sendStatus(String status) throws InterruptedException, JSONException {
       send(null,true,false,
               SERVICE_STATUS_STATE,
               status
                );
        //singleton.waitForBroadcastReceiver();

    }

    private void sendReadWrite(JSONObject jsonObject , Boolean admin, Boolean write)
            throws InterruptedException, JSONException {
        send(jsonObject,admin,write,
                SERVICE_READ_WRITE,
                (admin)?CHARACTERISTIC_MESSAGE_ADMIN:CHARACTERISTIC_MESSAGE_USER);

    }

    /*
             COMMAND METHODS TOTAL:19
     */

    /**
     * initUser(USER): register as a new user and receive a user ID
     * JSON-sample: {"cmd": "init_user", "name": "Jane"}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userInitUser(String name, Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "init_user");
        jsonObject.put("name", name);
        singleton.sendReadWrite(jsonObject, false, true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() {
                AdminRights.setUser(this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
        Log.w(TAG, "this is the end of world!" + singleton.getEspResponseValue());
    }



    /**
     * define_pump (ADMIN): add a new pump to ESP
     * JSON-sample: {"cmd": "define_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}
     * like described in ProjektDokumente/esp/Befehle.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminDefinePump(String liquid, float volume, int slot,Activity activity)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "define_pump");
        jsonObject.put("user", 0);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        singleton.sendReadWrite(jsonObject,true,true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
        //
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * edit_pump (ADMIN): Edits a pump
     * JSON-sample: {"cmd": "edit_pump", "user": 0, "liquid": "water", "volume": 1000, "slot": 1}
     * like described in ProjektDokumente/esp/Befehle.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminEditPump(String liquid, float volume, int slot, Activity activity)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "edit_pump");
        jsonObject.put("user", 0);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        singleton.sendReadWrite(jsonObject,true,true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /**
     *
     * define_recipe (USER): defines a new recipe or changes an existing recipe
     * JSON-sample: {"cmd": "define_recipe", "user": 0, "name": "radler",
     * "ingredients": [["beer", 250], ["lemonade", 250]]}
     * like described in ProjektDokumente/esp/Befehle.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * @return
     * @throws JSONException
     */
    public void userDefineRecipe(long user,String name, JSONArray ingredients, Activity activity)
    //public void defineRecipe(float user,String name, JSONArray liquids)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        // generate JSON Format
       /*JSONArray arrayLiquids = new JSONArray();
        int i = 0;
        for (Pair p : liquids) {
            JSONArray arrayPair = new JSONArray();
            arrayPair.put(i, p.first);
            arrayPair.put(i + 1, p.second);
            arrayLiquids.put(arrayPair);

                }
        */

        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "define_recipe");
        jsonObject.put("user", user);
        jsonObject.put("name", name);
        jsonObject.put("ingredients", ingredients);

        singleton.sendReadWrite(jsonObject,false,true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * edit_Recipe (USER): edit an existing recipe
     * JSON-sample: {"cmd": "edit_recipe", "user": 0, "name": "radler",
     * "ingredients": [["beer", 250], ["lemonade", 250]]}
     * like described in ProjektDokumente/esp/Befehle.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userEditRecipe(float user,String name, ArrayList<Pair<String, Float>> ingredients
            , Activity activity)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        // generate JSON Format
        JSONArray arrayIngredients = new JSONArray();
        int i = 0;
        for (Pair p : ingredients) {
            JSONArray arrayPair = new JSONArray();
            arrayPair.put(i, p.first);
            arrayPair.put(i + 1, p.second);
            arrayIngredients.put(arrayPair);
        }
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "edit_recipe");
        jsonObject.put("user", user);
        jsonObject.put("name", name);
        jsonObject.put("ingredients", arrayIngredients);

        singleton.sendReadWrite(jsonObject,false,true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * start_recipe (USER): starts the recipe when the machine is ready
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "start_recipe", "user": 8858}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userStartRecipe(long user, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "start_recipe");
        jsonObject.put("user", user);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getJsonResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * cancel_recipe (USER): Cancels the current recipe
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "cancel_recipe", "user": 483}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userCancelRecipe(long user, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "cancel_recipe");
        jsonObject.put("user", user);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getJsonResult());
            }
        };
        wfb.execute();
        //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * take_cocktail (USER): informs that the cocktail has been taken out
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "take_cocktail", "user": 483}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userTakeCocktail(long user, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "take_cocktail");
        jsonObject.put("user", user);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /**
     * delete_recipe (USER): delete a recipe
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "delete_recipe", "user": 0, "name": "radler"}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userDeleteRecipe(long user, String name, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "delete_recipe");
        jsonObject.put("user", user);
        jsonObject.put("name", name);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }
    /**
     * queue_recipe (USER): commissions a recipe
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "queue_recipe", "user": 8858, "recipe": "radler"}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userQueueRecipe(long user, String recipe, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "queue_recipe");
        jsonObject.put("user", user);
        jsonObject.put("recipe", recipe);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * reset (ADMIN): Reset the machine
     * JSON-sample: {"cmd": "reset", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReset(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "reset");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());

    }

    /**
     * reset (ADMIN): Reset the machine
     * JSON-sample: {"cmd": "reset", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReset(Activity activity, Postexecute postexecute) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "reset");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());

    }

    /**
     * reset_error (ADMIN): Reset stored error.The command removes the current
     * error and continues in normal operation. This usually only happens when
     * a recipe had a problem and the machine could not fix the problem.
     * JSON-sample: {"cmd": "reset_error", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminResetError(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "reset_error");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());

    }

    /**
     * addLiquid (USER): Adds liquid to the current recipe
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "add_liquid", "user": 0, "liquid": "water", "volume": 30}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void userAddLiquid(float user, String liquid, float volume, Activity activity)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "add_liquid");
        jsonObject.put("user", user);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        singleton.sendReadWrite(jsonObject, false, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * refill_pump (ADMIN): Fills pump
     * like described in ProjecktDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "refill_pump", "user": 0, "volume": 1000, "slot": 1}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminRefillPump(float volume, int slot, Activity activity) throws JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "refill_pump");
        jsonObject.put("user", 0);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());

    }

    /** Manual Calibration
     * run_pump (ADMIN): Runs the pump for a certain time. The time is given in milliseconds.
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "run_pump", "user": 0, "slot": 1, "time": 1000}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminManuelCalibrateRunPump(int slot, int time, Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "run_pump");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time", time);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Automatic Calibration
     * calibration_start (ADMIN): Start automated calibration
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "calibration_start", "user": 0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminAutoCalibrateStart(Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        //generate JSON Format
        singleton.connectGatt(activity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibration_start");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned <String> result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /** Automatic Calibration
     * calibration_add_empty (ADMIN): empty vessel is ready
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "calibration_add_empty", "user": 0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminAutoCalibrateAddEmpty(Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        //generate JSON Format
        singleton.connectGatt(activity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibration_add_empty");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Automatic Calibration
     * calibration_cancel (ADMIN): Cancel automated calibration
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "calibration_cancel", "user": 0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminAutoCalibrateCancel(Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibration_cancel");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Automatic Calibration
     * calibration_finish (ADMIN): Calibration ready
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "calibration_finish", "user": 0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminAutoCalibrateFinish(Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        //generate JSON Format
        singleton.connectGatt(activity);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibration_finish");
        jsonObject.put("user", 0);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + this.getStringResult());
            }
        };
        wfb.execute();
    }



    /** Automatic Calibration
     * calibration_add_weight (ADMIN): Vessel is filled with a quantity of water
     * Automatic Calibration
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "calibration_add_weight", "user": 0, "weight": 100.0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */

    @SuppressLint("MissingPermission")
    public void adminAutoCalibrateAddWeight(float weight, Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibration_add_weight");
        jsonObject.put("user", 0);
        jsonObject.put("weight", weight);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }




    /** Manuel Calibration
     * calibratePump: For calibration, two measured values must be available for which the pump
     * has been running for a different time. This is then used to calculate the flow rate and
     * the pump rate. The times are given in milliseconds and the liquids in millilitres.
     * JSON-sample: {"cmd": "calibrate_pump", "user": 0, "slot": 1,
     * like described in ProjektDokumente/esp/Befehle.md
     * "time1": 10000, "time2": 20000, "volume1": 15.0, "volume2": 20.0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminManuelCalibratePump(int slot, int time1, int time2,
                                     double volume1, double  volume2, Activity activity)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibrate_pump");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time1", time1);
        jsonObject.put("time2", time2);
        jsonObject.put("volume1", volume1);
        jsonObject.put("volume2", volume2);
        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Manuel Calibration
     * set_pump_times (ADMIN): sets the calibration values for a pump. time_init is the lead time
     * and time_reverse is the return time in milliseconds. Normally these values should be similar
     * or the same. The rate is given in mL/ms.
     * like described in ProjektDokumente/esp/Befehle.md
     * JSON-sample: {"cmd": "set_pump_times", "user": 0, "slot": 1,
     * "time_init": 1000, "time_reverse": 1000, "rate": 1.0}
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminManuelCalibrateSetPumpTimes(int slot, int timeInit, int timeReverse,
                                                 double rate, Activity activity) throws
            JSONException, InterruptedException {

        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "set_pump_times");
        jsonObject.put("user", 0);
        jsonObject.put("slot", slot);
        jsonObject.put("time_init", timeInit);
        jsonObject.put("time_reverse", timeReverse);
        jsonObject.put("rate", rate);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Manuel Calibration
     * tare_scale (ADMIN): Tares the scale
     * JSON-Sample: {"cmd": "tare_scale", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminManuelCalibrateTareScale( Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "tare_scale");
        jsonObject.put("user", 0);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
      //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /** Manuel Calibration
     * set_scale_factor (ADMIN): calibrates the scale. The weight is given in milligrams.
     * JSON-Sample: {"cmd": "calibrate_scale", "user": 0, "weight": 100.0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminManuelCalibrateScale(float weight, Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "calibrate_scale");
        jsonObject.put("user", 0);
        jsonObject.put("weight", weight);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /** Manuel Calibration
     * set_scale_factor (ADMIN): sets the calibration value for the scale
     * JSON-Sample: {"cmd": "set_scale_factor", "user": 0, "factor": 1.0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminManuelCalibrateSetScaleFactor(float factor, Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "set_scale_factor");
        jsonObject.put("user", 0);
        jsonObject.put("factor", factor);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * restart (ADMIN): restart the machine
     * JSON-Sample: {"cmd": "restart", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminRestart( Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "restart");
        jsonObject.put("user", 0);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * Factroy Reset (ADMIN): resets all settings
     * JSON-Sample: {"cmd": "factory_reset", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminFactoryReset( Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "factory_reset");
        jsonObject.put("user", 0);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /**
     * Factroy Reset (ADMIN): resets all settings
     * JSON-Sample: {"cmd": "factory_reset", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminFactoryReset( Activity activity, Postexecute postexecute) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "factory_reset");
        jsonObject.put("user", 0);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * clean (ADMIN): clean the machine
     * JSON-Sample: {"cmd": "clean", "user": 0}
     * like described in ProjektDokumente/esp/Befehle.md
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminClean(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        //generate JSON Format

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "clean");
        jsonObject.put("user", 0);

        singleton.sendReadWrite(jsonObject, true, true);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }

                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /*
            STATUS METHODS TOTAL:7
     */

    /**
     * adminPumpsStatus: Map of all available pumps and their level
     * JSON-Sample: {"1":{"liquid":"water","volume":1000.0,"calibrated":true,
     * "rate":0.0,"time_init":1000,"time_reverse":1000}
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadPumpsStatus(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_PUMPS);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException, NotInitializedDBException, JSONException, MissingIngredientPumpException {
                if (!check()) {
                    throw new InterruptedException();
                }
                Pump.updatePumpStatus(activity,this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }
    /**
     * adminPumpsStatus: Map of all available pumps and their level
     * JSON-Sample: {"1":{"liquid":"water","volume":1000.0,"calibrated":true,
     * "rate":0.0,"time_init":1000,"time_reverse":1000}
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadPumpsStatus(Activity activity, Postexecute postexecute)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_PUMPS);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException, NotInitializedDBException
                    , JSONException, MissingIngredientPumpException {
                if (!check()) {
                    throw new InterruptedException();
                }
                Pump.updatePumpStatus(activity,this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadLastChange: Last Change Characteristic: If the timestamp has not changed,
     * the available recipes and ingredients are still the same.The timestamp is an internal
     * value of the ESP and has no relation to the real time.
     * Sample: 275492
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadLastChange( Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_LAST_CHANGE);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException, NotInitializedDBException, JSONException, MissingIngredientPumpException {
                if (!check()) {
                    throw new InterruptedException();
                }
                //Pump.updatePumpStatus(this.getResult());
                CocktailMachine.setLastChange(this.result);
                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
      //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadLiquidsStatus: Map of all available liquids and their volumes
     * JSON-Sample: {"beer": 200, "lemonade": 2000, "orange juice": 2000}
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadLiquidsStatus(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_LIQUIDS);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException, NotInitializedDBException, JSONException, MissingIngredientPumpException {
                if (!check()) {
                    throw new InterruptedException();
                }
                Pump.updateLiquidStatus(activity,this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
       // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadState: The current state of the cocktail machine and what it does.
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * Samples:
     * init: Machine is being initialised
     * ready: machine is ready to execute a command and is waiting
     * mixing: Machine makes a cocktail
     * pumping: machine pumps liquids
     * cocktail done: Cocktail is prepared and can be taken out. After that
     * reset should be executed.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadState(Activity activity, Postexecute postexecute)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_STATE);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException, JSONException {
                if (!check()) {
                    throw new InterruptedException();
                }
                CocktailStatus.
                        setStatus(getStringResult());
                CalibrateStatus.setStatus(getStringResult());
                Log.w(TAG, "returned result is now:" + getStringResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadRecipesStatus: Status of all saved recipes and their names
     * JSON-Sample: [{"name": "radler", "liquids": [["beer", 250], ["lemonade", 250]]},
     * {"name": "spezi", "liquids": [["cola", 300], ["orange juice", 100]]}]
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadRecipesStatus(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_RECIPES);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException, JSONException,
                    NotInitializedDBException {
                if (!check()) {
                    throw new InterruptedException();
                }
                Recipe.setRecipes(activity,this.getJSONArrayResult());
                Log.w(TAG, "To Save: " + this.getJSONArrayResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadCurrentCocktail: The content of the current cocktail being mixed.
     * JSON-Sample: {"weight": 500.0, "content": [["beer", 250], ["lemonade", 250]]}
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     * like described in ProjektDokumente/esp/Services.md
     *
     * @return
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadCurrentCocktail(Activity activity, Postexecute postexecute)
            throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_COCKTAIL);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                CocktailMachine.setCurrentCocktail(activity,this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
        //Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadUserQueue (ADMIN): all users in the queue for whom a
     * cocktail is being made. If no user is active, the value is `[]`.
     * Sample: [1, 4, 2]
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic}
     * from the Device.
     *
     * @return JSONArray
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadUserQueue(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_USER_QUEUE);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException, JSONException {
                if (!check()) {
                    throw new InterruptedException();
                }
                //CocktailMachine.setCurrentUser(getJSONArrayResult());
                Log.w(TAG, "To Save: " + this.getJSONArrayResult());
            }
        };
        wfb.execute();
        //  Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadScaleStatus: Read Status of Scale
     * Sample: {"weight":0.0,"calibrated":true}
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic}
     * from the Device.
     *
     * @return JSONObject
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadScaleStatus(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_SCALE);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                CocktailMachine.setCurrentWeight(this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
        //   Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }


    /**
     * adminReadScaleStatus: Read Status of Scale
     * Sample: {"weight":0.0,"calibrated":true}
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic}
     * from the Device.
     *
     * @return JSONObject
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadScaleStatus(Activity activity, Postexecute postexecute) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_SCALE);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                CocktailMachine.setCurrentWeight(this.getJsonResult());
                Log.w(TAG, "To Save: " + this.getJsonResult());
            }
        };
        wfb.execute();
        //   Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadScaleStatus: current error (if any)
     * Sample: "invalid volume"
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic}
     * from the Device.
     *
     * @return JSONObject
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadErrorStatus(Activity activity) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_ERROR);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                ErrorStatus.setError(this.result);
                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
        Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }

    /**
     * adminReadScaleStatus: current error (if any)
     * Sample: "invalid volume"
     * like described in ProjektDokumente/esp/Services.md
     * receives a message along with Read on {@code BluetoothGattCharacteristic}
     * from the Device.
     *
     * @return JSONObject
     * @throws JSONException
     */
    @SuppressLint("MissingPermission")
    public void adminReadErrorStatus(Activity activity, Postexecute postexecute) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.connectGatt(activity);
        singleton.sendStatus(CHARACTERISTIC_STATUS_ERROR);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver(postexecute) {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                ErrorStatus.setError(this.result);
                Log.w(TAG, "To Save: " + this.getStringResult());
            }
        };
        wfb.execute();
        // Log.w(TAG, "returned value is now: " + singleton.getEspResponseValue());
    }



}