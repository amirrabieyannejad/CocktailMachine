package com.example.cocktailmachine.bluetoothlegatt;

import static com.example.cocktailmachine.bluetoothlegatt.BluetoothLeService.CHARACTERISTIC_STATUS_CURRENT_USER;
import static com.example.cocktailmachine.bluetoothlegatt.BluetoothLeService.SERVICE_STATUS_STATE;

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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;

import com.example.cocktailmachine.data.enums.AdminRights;
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

    private String value;
    private String finalValue;
    public String result;
    private Thread threadWaitForBroadcast;
    private Thread threadReadFirstValue;
    private Thread threadReadSecondValue;



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
/*                try {
                    singleton.initUser2("a");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
                //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.w(TAG, "onServicesDiscovered received: Services has been discovered! ");
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
                finalValue = characteristic.getStringValue(0);
                if (finalValue.equals("\"processing\"") || finalValue == "") {
                    Log.w(TAG, "request is in processing: wait for response! " +
                            finalValue);
                    isReading = false;
                } else {
                    Log.w(TAG, "esp send a real response!" + finalValue);
                    singleton.setEspResponseValue(finalValue);
                }
                synchronized (this) {
                    isReading = false;
                    this.notifyAll();
                }

                //singleton.mBluetoothGatt.disconnect();


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
        singleton.connect(activity, "54:43:B2:A9:32:26");
        singleton.mBluetoothGatt.disconnect();
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
    public String readCharacteristicValue(String server, String characteristic) throws InterruptedException {
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
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> singleton.readCharacteristic(mGattCharacteristics),
                2000);
        Log.w(TAG, "readCharactersitc fist");

        @SuppressLint("HandlerLeak") Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //singleton.value = finalValue;

                Log.w(TAG, "handle Message! " + singleton.value);
            }
        };

        Runnable readFirstValue =new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                while (!singleton.mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                    Log.w(TAG, "run: Is reading Characteristic  in Process!");

                    try {
                        Log.w(TAG, "run: read Characteristic: Reading from ESP is in Process!"
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
        threadReadFirstValue.start();

        @SuppressLint("HandlerLeak") Handler handle2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //singleton.value = finalValue;

                Log.w(TAG, "handle Message! " + singleton.value);
            }
        };

        Runnable readSecondValue = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                while (!singleton.mBluetoothGatt.readCharacteristic(mGattCharacteristics)) {
                    Log.w(TAG, "run2: Is reading Characteristic  in Process!");

                    try {
                        Log.w(TAG, "run2: read Characteristic: Reading from ESP is in Process!"
                                + timeout);

                        Thread.sleep(timeout);
                        timeout = timeout + 500;
                        if (timeout == 3000) {
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
        threadReadSecondValue = new Thread(readSecondValue);
        threadReadFirstValue.join();
        threadReadSecondValue.start();
        Log.w(TAG, "readCharactersitc second");

        Log.w(TAG, "try to read Response Value from specific Characteristic!");
        return singleton.value;
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
        Log.w(TAG, "Characteristic has been written. The value is now: " +
                mWriteCharacteristic.getStringValue(0));


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
        Log.w(TAG, "getBluetoothGattCharacteristic: BluetoothAdapter" +
                " and BluetoothGatt are still available!");
        BluetoothGattService mCustomService = singleton.mBluetoothGatt.getService
                (UUID.fromString(SampleGattAttributes.lookupUuid(service)));
        if (mCustomService == null) {
            Log.w(TAG, "getBluetoothGattCharacteristic: Custom BLE Service not found");

            //return null;
        }
        /* get the characteristic from the service */
        Log.w(TAG, "Desired Characteristic found. Prepare for writing on it!");
        return (mCustomService.getCharacteristic(UUID.fromString
                (SampleGattAttributes.lookupUuid(characteristic))));

    }


    ///////////////////////////END TO GET RIDE OF SERVICE


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
                singleton.makeGattUpdateIntentFilter());
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

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                Log.w(TAG, "wait for broadcastreceiver: " + singleton.getEspResponseValue());
                Log.w(TAG, "wait for broadcastreceiver: " + finalValue);
                while (singleton.getEspResponseValue() == null) {
                    try {
                        Thread.sleep(timeout);
                        timeout = timeout + 500;
                        Log.w("onClickListener", "We wait to receive a Broadcast" +
                                "Update!");
                        if (timeout == 5000) {
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
            public void handleMessage(Message msg) {
                //singleton.value = finalValue;
                result = singleton.getEspResponseValue();
                Log.w(TAG, "waitForBroadcastReceiver:handle Message! "
                        + singleton.result);
            }
        };

        Runnable waitForBroadcast = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                try {


                    while (singleton.getEspResponseValue() == null
                            || singleton.getEspResponseValue().equals("\"processing\"")) {
                        synchronized (this) {
                            try {
                                //TODO: Loop never ended! do something!
                                Log.w(TAG, "waitForBroadcastReceiver: this is middle night.." +
                                        singleton.getEspResponseValue());
                                Thread.sleep(500);
                                timeout = timeout + 500;
                                if (timeout == 5000) {
                                    throw new RuntimeException();
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
    @SuppressLint("MissingPermission")
    private String waitForBroadcastReceiver2() throws InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.result = null;
        //WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver();
        //threadWaitForBroadcast = new Thread(wfb);
        threadReadSecondValue.join();
        threadWaitForBroadcast.start();
        //threadWaitForBroadcast.join();
        //Log.w(TAG, "Return Value: waitForBroadcastReceiver" + wfb.getValue());
        return null;
        //return wfb.getValue();



    }


    @SuppressLint("MissingPermission")
    private JSONObject convertStringToJson1() throws InterruptedException, JSONException {
        singleton = BluetoothSingleton.getInstance();

        int timeout = 500;
        while (singleton == null) {
            wait(timeout);
            Log.w(TAG, "waitForBroadcastReceiver: waiting for Broadcast to receive desired" +
                    " value!");
            timeout = timeout + 500;
            if (timeout == 5000) {
                Log.w(TAG, "waitForBroadcastReceiver: Timeout has been triggered! ");
                break;
            }
        }
        //String toJson = singleton.waitForBroadcastReceiver();
        //if (toJson != null) {
        //   JSONObject jsonObj = new JSONObject(toJson);
        //  Log.w(TAG, "json format is generated: " + jsonObj);
        // return jsonObj;
        return null;
    }


    @SuppressLint("MissingPermission")
    private JSONObject convertStringToJson(String toJson) throws InterruptedException,
            JSONException {
        singleton = BluetoothSingleton.getInstance();

        if (toJson != null) {
            JSONObject jsonObj = new JSONObject(toJson);
            Log.w(TAG, "json format is generated: " + jsonObj);
            return jsonObj;
        }
        return null;
    }

    public String readCharacteristicValue1(String server, String characteristic,
                                           String command)
            throws InterruptedException {
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
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                int timeout = 500;
                while (mGattCharacteristics.getStringValue(0) == command ||
                        mGattCharacteristics.getStringValue(0) == "") {
                    Log.w(TAG, "Is reading Characteristic  in Process!");

                    try {
                        Log.w(TAG, "read Characteristic: Reading from ESP is in Process!"
                                + timeout);
                        wait(timeout);
                        timeout = timeout + 500;
                        if (timeout == 5000) {
                            Log.w(TAG, "read Characteristic: Timeout, unable to " +
                                    "read value from ESP");
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                singleton.setEspResponseValue(mGattCharacteristics.getStringValue(0));
                Log.w(TAG, "after we wait for read a characteristics" +
                        singleton.getEspResponseValue());
            }
        };
        Thread threadWriteValue = new Thread(run);
        threadWriteValue.start();

        Log.w(TAG, "try to read Response Value from specific Characteristic!");
        return singleton.getEspResponseValue();
    }

    @SuppressLint("MissingPermission")
    public void initUser(String name) throws JSONException,
            InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "init_user");
        jsonObject.put("name", name);
        singleton.sendReadWrite(jsonObject,false,true);

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() {
                AdminRights.setUser(this.getResult());
                Log.w(TAG, "To Save: " + this.getResult());
            }
        };
        wfb.execute();
        Log.w(TAG, "this is the end of world!" + singleton.getEspResponseValue());

        return ;

    }
    /**
     * adminReadCurrentUser: the current user for whom a cocktail is being made
     * receives a message along with Read on {@code BluetoothGattCharacteristic} from the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminReadCurrentUser() throws JSONException, InterruptedException {
    singleton.sendStatus(CHARACTERISTIC_STATUS_CURRENT_USER);
        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                //AdminRights.(this.getResult());
                Log.w(TAG, "To Save: " + this.getResult());
            }
        };
        wfb.execute();
        Log.w(TAG, "this is the end of world!" + singleton.getEspResponseValue());
    }

    /**
     * addPump: add a new pump to ESP
     * sends a message along with write on {@code BluetoothGattCharacteristic} on to the Device.
     */
    @SuppressLint("MissingPermission")
    public void adminDefinePump(String liquid, float volume, int slot) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        // generate JSON Format
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cmd", "define_pump");
        jsonObject.put("user", 0);
        jsonObject.put("liquid", liquid);
        jsonObject.put("volume", volume);
        jsonObject.put("slot", slot);
        singleton.sendReadWrite(jsonObject,true,true);
        //TODO: Exeption for Connection

        WaitForBroadcastReceiver wfb = new WaitForBroadcastReceiver() {
            @Override
            public void toSave() throws InterruptedException {
                if (!check()) {
                    throw new InterruptedException();
                }
                //AdminRights.(this.getResult());
                Log.w(TAG, "To Save: " + this.getResult());
            }
        };
        wfb.execute();
        Log.w(TAG, "this is the end of world!" + singleton.getEspResponseValue());



    }


    private void send(JSONObject jsonObject ,
                      Boolean admin,
                      Boolean write,
                      String service,
                      String characteristic

    ) throws InterruptedException, JSONException {
        singleton = BluetoothSingleton.getInstance();
        if (singleton.mBluetoothAdapter == null || singleton.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter or BluetoothGatt are not initialized");
            return;
        }
        singleton.setEspResponseValue(null);
        //singleton.connect(activity, "54:43:B2:A9:32:26");
        Log.w(TAG, "initUser: BluetoothAdapter initialized!");
        //generate JSON Format
        if (write) {
            singleton.writeCharacteristic(jsonObject.toString(), admin);
        }
        singleton.readCharacteristicValue(service,
                characteristic);
        //singleton.waitForBroadcastReceiver();

    }
    private void sendStatus(String status) throws InterruptedException, JSONException {
       send(null,true,false,
               SERVICE_STATUS_STATE,
               status
                );
        //singleton.waitForBroadcastReceiver();

    }
    private void sendReadWrite(JSONObject jsonObject , Boolean admin, Boolean write) throws InterruptedException, JSONException {
        send(jsonObject,admin,write,
                SERVICE_READ_WRITE,
                (admin)?CHARACTERISTIC_MESSAGE_ADMIN:CHARACTERISTIC_MESSAGE_USER);

    }
}