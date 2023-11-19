package com.example.cocktailmachine;

public class Dummy {
    // APP: zur Abgabe: isDummy = false, withSetCalibration=true, withTestEnvs= false, asAdmin = false
    public static boolean isDummy = true;
    //if true-> keine Bluetoothverbindung, für VM-testing,
    // false -> Bluetooth
    public static boolean withSetCalibration =  false;
    //false -> keine Kalibrierungscheck,
    // true -> kalibrierungscheck  und ggf. automatische kalibrirung,

    public static boolean withTestEnvs = true;
    //false -> keine Test Activities
    //true -> Test Activities zugänglich

    public static boolean asAdmin = false;
    //true  -> direkt admin
    //false -> normaler User
}
