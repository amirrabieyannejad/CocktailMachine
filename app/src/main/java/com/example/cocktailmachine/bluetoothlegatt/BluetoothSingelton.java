package com.example.cocktailmachine.bluetoothlegatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.util.Log;

public class BluetoothSingelton {

    //Variable
    private static BluetoothSingelton instance;

    private String EspDeviceName;
    private String EspDeviceAddress;

    //Konstruktor
    private BluetoothSingelton(){

    }

    //Funktionen

    public static BluetoothSingelton getInstance(){
        if (instance == null) {
            instance = new BluetoothSingelton();
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

    public void setEspDeviceAddress(String espDeviceAddress) {
        this.EspDeviceAddress = espDeviceAddress;
    }

    public BluetoothLeService getBluetoothLe () throws BluetoothDeviceAdressNotDefinedException {
        BluetoothLeService bluetooth = new BluetoothLeService();
        if (this.EspDeviceAddress.equals(null) || this.EspDeviceAddress==""){
            throw new BluetoothDeviceAdressNotDefinedException("The Adress for the Bluetooth Device is not defined in Bluetooth Singelton");
        }
        bluetooth.connect(this.EspDeviceAddress);
        return (bluetooth);
    }



}
