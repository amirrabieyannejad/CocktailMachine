package com.example.cocktailmachine.bluetoothlegatt;

public class BluethoothDeviceAdressNotDefinedException extends Exception{

    public BluethoothDeviceAdressNotDefinedException(String errorMessage){
        super(errorMessage);
    }

    public BluethoothDeviceAdressNotDefinedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
