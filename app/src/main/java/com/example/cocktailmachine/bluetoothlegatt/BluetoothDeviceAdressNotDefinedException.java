package com.example.cocktailmachine.bluetoothlegatt;

public class BluetoothDeviceAdressNotDefinedException extends Exception{

    public BluetoothDeviceAdressNotDefinedException(String errorMessage){
        super(errorMessage);
    }

    public BluetoothDeviceAdressNotDefinedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
