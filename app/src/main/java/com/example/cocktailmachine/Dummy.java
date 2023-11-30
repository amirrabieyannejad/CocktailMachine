package com.example.cocktailmachine;

public class Dummy {
    // APP: zur Abgabe: isDummy = false, withSetCalibration=true, withTestEnvs= false, asAdmin = false
    public final static boolean isDummy = false;
    //if true-> keine Bluetoothverbindung, für VM-testing,
    // false -> Bluetooth
    public static boolean withSetCalibration =  true;
    //false -> keine Kalibrierungscheck,
    // true -> kalibrierungscheck  und ggf. automatische kalibrirung,

    public final static boolean withTestEnvs = false;
    //false -> keine Test Activities
    //true -> Test Activities zugänglich

    public final static boolean asAdmin = false;
    //true  -> direkt admin
    //false -> normaler User
}
