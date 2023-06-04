package com.example.cocktailmachine.bluetoothlegatt;

public class SingeltonSettings {

    //Variable
    private static SingeltonSettings instance;

    private String EspDeviceName;
    private String EspDeviceAddress;

    //Konstruktor
    private SingeltonSettings(){

    }

    //Funktionen

    public static SingeltonSettings getInstance(){
        if (instance == null) {
            instance = new SingeltonSettings();
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

    BluetoothLeService getBluetoothLe (){
        BluetoothLeService bluetooth = new BluetoothLeService();
        bluetooth.connect(this.EspDeviceAddress);
        return (bluetooth);
    }

}
