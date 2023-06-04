package com.example.cocktailmachine.bluetoothlegatt;

public class BluethoothSingelton {

    //Variable
    private static BluethoothSingelton instance;

    private String EspDeviceName;
    private String EspDeviceAddress;

    //Konstruktor
    private BluethoothSingelton(){

    }

    //Funktionen

    public static BluethoothSingelton getInstance(){
        if (instance == null) {
            instance = new BluethoothSingelton();
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

    BluetoothLeService getBluetoothLe () throws BluethoothDeviceAdressNotDefinedException {
        BluetoothLeService bluetooth = new BluetoothLeService();
        if (this.EspDeviceAddress.equals(null) || this.EspDeviceAddress==""){
            throw new BluethoothDeviceAdressNotDefinedException("The Adress for the Bluetooth Device is not defined in Bluetooth Singelton");
        }
        bluetooth.connect(this.EspDeviceAddress);
        return (bluetooth);
    }

}
