package com.example.cocktailmachine.bluetoothlegatt;

import static android.content.Context.BIND_AUTO_CREATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONException;


public class BluetoothSingleton {

    //Variable
    private static BluetoothSingleton instance;

    public String EspResponseValue;
    public String EspDeviceName;
    public String EspDeviceAddress;
    private final static String TAG = BluetoothSingleton.class.getSimpleName();
    public BluetoothLeService mBluetoothLeService;
    private static final int PERMISSIONS_REQUEST_CODE = 191;
    private BluetoothSingleton singleton;
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

    //Constructor
    private BluetoothSingleton(){

    }

    //Functions

    public static BluetoothSingleton getInstance(){
        if (instance == null) {
            instance = new BluetoothSingleton();
        }
        return instance;

    }

    public String getEspDeviceName() {
        return this.EspDeviceName;
    }

    public String getEspDeviceAddress() {
        return this.EspDeviceAddress;
    }

    public void setEspDeviceName(String espDeviceName) {
        this.EspDeviceName = espDeviceName;
    }
    public void setEspResponseValue(String espResponseValue) {
        this.EspResponseValue = espResponseValue;
    }
    public String getEspResponseValue() {
        return this.EspResponseValue;
    }

    public void setEspDeviceAddress(String espDeviceAddress) {
        this.EspDeviceAddress = espDeviceAddress;
    }

/*    public BluetoothLeService getBluetoothLe () throws BluetoothDeviceAdressNotDefinedException {
        BluetoothLeService bluetooth = new BluetoothLeService();
        if (this.EspDeviceAddress.equals(null) || this.EspDeviceAddress==""){
            throw new BluetoothDeviceAdressNotDefinedException("The Address for the Bluetooth" +
                    " Device is not defined in Bluetooth Singleton");
        }
        bluetooth.connect(this.EspDeviceAddress);
        return (bluetooth);
    }*/

    // Code to manage Service lifecycle.
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.w(TAG, "try to initialize");
            singleton = BluetoothSingleton.getInstance();
            //TODO: Check localBinder
            singleton.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!singleton.mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.

            Log.w(TAG, "try to connect: " + singleton.EspDeviceAddress);
            //singleton.mBluetoothLeService.connect(EspDeviceAddress);
            singleton.mBluetoothLeService.connect("54:43:B2:A9:32:26");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            singleton = BluetoothSingleton.getInstance();
            singleton.mBluetoothLeService = null;
        }
    };

    public void requestBlePermissions(Activity activity) {
        int requestCode = PERMISSIONS_REQUEST_CODE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }
    public void bindService(Activity activity) {
        singleton = BluetoothSingleton.getInstance();
        singleton.requestBlePermissions(activity);
        Intent gattServiceIntent = new Intent(activity,BluetoothLeService.class);
        activity.bindService(gattServiceIntent, singleton.mServiceConnection, BIND_AUTO_CREATE);
    }

    public String initUser(String user) throws JSONException, InterruptedException {
        singleton = BluetoothSingleton.getInstance();
        singleton.mBluetoothLeService.initUser(user);
        return "true";
    }

}
