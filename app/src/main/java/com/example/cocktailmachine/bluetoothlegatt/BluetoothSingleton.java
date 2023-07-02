package com.example.cocktailmachine.bluetoothlegatt;

import static android.content.Context.BIND_AUTO_CREATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cocktailmachine.ui.BluetoothTestEnviroment;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Boolean state = false;
    private BluetoothSingleton singleton;
    // Code to manage Service lifecycle.


    ///////////////////// START TO GET RIDE OF SERVICE/////////////////////////////
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

    String value;
    String finalValue;
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
/*                singleton = BluetoothSingleton.getInstance();
                try {
                    singleton.initUser2("a");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
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
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                singleton = BluetoothSingleton.getInstance();
                //singleton.setEspResponseValue(characteristic.getStringValue(0));

                value = characteristic.getStringValue(0);
                singleton.setEspResponseValue(value);
                //Log.w(TAG, "value has been read!" + value);

            } else {
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }


        }

        @Override
        @SuppressLint("MissingPermission")
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

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
        Log.w(TAG, "BluetoothAdapter is initialized! Address is specified!");
        // Previously connected device.  Try to reconnect.
        if (address.equals("54:43:B2:A9:32:26")
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

        singleton.mBluetoothGatt = device.connectGatt(activity.getApplicationContext(),
                false, singleton.mGattCallback);
        Log.w(TAG, "Connection has been established!");
        singleton.mBluetoothDeviceAddress = address;
        return true;
    }

    public Boolean connectGatt(Activity activity) {
        Log.w(TAG, "try to initialize BluetoothGATT!");
        singleton = BluetoothSingleton.getInstance();
        if (!singleton.initialize(activity)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            return false;
        }
        // Automatically connects to the device upon successful start-up initialization.

        //Log.w(TAG, "try to connect: " + singleton.EspDeviceAddress);
        singleton.connect(activity, "54:43:B2:A9:32:26");
        Log.w(TAG, "connected to 54:43:B2:A9:32:26");
        return true;
    }

    @SuppressLint("MissingPermission")
    public String readCharacteristicValue(String server, String characteristic)
            throws InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter or Bluetooth Gatt not initialized");
            return null;
        }
        BluetoothGattCharacteristic mGattCharacteristics;
        mGattCharacteristics = getBluetoothGattCharacteristic(server, characteristic);
        singleton.value = mGattCharacteristics.getStringValue(0);
        if (mGattCharacteristics != null) {
            Log.w(TAG, "characteristic is : " + singleton.value);
        }

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                singleton.value = finalValue;
                Log.w(TAG, "handle Message! " + singleton.finalValue);
            }
        };
        Runnable run = new Runnable() {
            @Override
            public void run() {
                while (!singleton.mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                    Log.w(TAG, "Is reading in Progess? ");
                    synchronized (this) {
                        try {
                            Log.w(TAG, "we will wait");
                            wait(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread newThread = new Thread(run);
        newThread.start();

        Log.w(TAG, "characteristic has been changed: " +
                value);
        return value;
    }

    @SuppressLint("MissingPermission")
    public Boolean writeCharacteristic(String value, Boolean admin) {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        /* check if the service is available on the device */
        BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
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

        if (!singleton.mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
            Log.w(TAG, "Failed to write characteristic");
            return false;
        }
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
        Log.w(TAG, "getBluetoothGattCharacteristic mBluetoothAdapter" +
                " and mBluetoothGatt are available!");
        BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
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


    ///////////////////////////END TO GET RIDE OF SERVICE


    //Functions
    private boolean mConnected;
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                singleton.setEspResponseValue(null);
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

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

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.w(TAG, "try to initialize BluetoothLeService!");
            singleton = BluetoothSingleton.getInstance();
            //TODO: Check localBinder
            singleton.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!singleton.mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.

            //Log.w(TAG, "try to connect: " + singleton.EspDeviceAddress);
            Log.w(TAG, "try to connect: 54:43:B2:A9:32:26");
            //singleton.mBluetoothLeService.connect(EspDeviceAddress);
            singleton.mBluetoothLeService.connect("54:43:B2:A9:32:26");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            singleton = BluetoothSingleton.getInstance();
            Log.w(TAG, "try to disconnecting Service!");
            singleton.mBluetoothLeService = null;
        }
    };

    private String displayData(String data) {
        if (data != null) {
            singleton = BluetoothSingleton.getInstance();
            singleton.setEspResponseValue(data);
            Log.w(TAG, "Action Data Available triggered!" + singleton.getEspResponseValue());
            return singleton.getEspResponseValue();
        }
        return null;
    }

    public void registerService(Activity activity) {
        activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect("54:43:B2:A9:32:26");
            Log.d(TAG, "Connect request result=" + result);
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

    public void bindService(AppCompatActivity activity) {


        @SuppressLint("HandlerLeak") Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                state = true;
                Log.w(TAG, "handle Message! connecting...");
            }
        };
        Runnable run = new Runnable() {
            @Override
            public void run() {
                singleton = BluetoothSingleton.getInstance();
                singleton.requestBlePermissions(activity);
                Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
                while (!activity.bindService(gattServiceIntent, singleton.mServiceConnection,
                        BIND_AUTO_CREATE)) {

                    Log.w(TAG, " reading in Process ");
                    try {
                        Log.w(TAG, "we will wait for Service Connection!");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

                handle.sendEmptyMessage(0);
            }

        };
        Thread newThread = new Thread(run);
        newThread.start();
    }

    public void initUser1(String user, TextView txtView) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        //singleton.bindService(activity);
        if (!state) {
            Log.w(TAG, "unable to bind Service");
        }
        singleton.mBluetoothLeService.initUser(user);
        waitForBroadcastReceiver(txtView);
    }

    @SuppressLint("MissingPermission")
    public void initUser(String name, TextView txtView, Activity activity) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        @SuppressLint("HandlerLeak") Handler handle = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                singleton = BluetoothSingleton.getInstance();
                Log.w(TAG, "handle Message!" );
                if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
                    Log.w(TAG, "BluetoothAdapter not initialized");
                    return;
                }
                Log.w(TAG, "initUser: BluetoothAdapter initialized!");
                //generate JSON Format
                JSONObject jsonObject = new JSONObject();
                try {
                    singleton = BluetoothSingleton.getInstance();
                    jsonObject.put("cmd", "init_user");
                    jsonObject.put("name", name);
                    String payload = jsonObject.toString();
                    singleton.writeCharacteristic(payload, false);
                    Log.w(TAG, "initUser: write Characteristic!");
                    singleton.readCharacteristicValue(SERVICE_READ_WRITE,
                            CHARACTERISTIC_MESSAGE_USER);
                    Log.w(TAG, "initUser: read Characteristic!");
                    singleton.waitForBroadcastReceiver(txtView);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        };

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    singleton = BluetoothSingleton.getInstance();
                    while (!singleton.connectGatt(activity)) {
                        try {
                            Thread.sleep(500);
                            Log.w(TAG, "we will wait here!");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    handle.sendEmptyMessage(0);
                }
            };
        Thread thread = new Thread(run);
        thread.start();


    }
    @SuppressLint("MissingPermission")
    public void initUser2(String name) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
/*        Handler handler = new Handler(Looper.getMainLooper());
        if (singleton.mBluetoothGatt == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    singleton.connectGatt(activity);

                }
            });
        } else {
            singleton.mBluetoothGatt.connect();
        }
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        */

        Log.w(TAG, "initUser: BluetoothAdapter initialized!");
        //generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "init_user");
        jsonObject.put("name", name);
        String payload = jsonObject.toString();

        singleton.writeCharacteristic(payload, false);
        Log.w(TAG, "initUser: write Characteristic!");
        singleton.readCharacteristicValue(SERVICE_READ_WRITE,
                CHARACTERISTIC_MESSAGE_USER);
        Log.w(TAG, "initUser: read Characteristic!");
        //singleton.waitForBroadcastReceiver(txtView);
    }

    private void waitForBroadcastReceiver(TextView textView) {

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                while (singleton.getEspResponseValue() == null) {
                    try {
                        Thread.sleep(500);
                        Log.w("onClickListener", "We wait to receive a Broadcast" +
                                "Update!");
                        //TODO: maximal setzen
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
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }

}
