/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cocktailmachine.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static final HashMap<String, String> attributes = new HashMap();
    //public static String COCKTAIL_MACHINE = "c0605c38-3f94-33f6-ace6-7a5504544a80";
    public static String COCKTAIL_MACHINE = "8ccbf239-1cd2-4eb7-8872-1cb76c980d14";

    // TO-DO: Add CCC Descriptor for add Notification/Indication to specific Characteristic
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    static   {
        // Cocktail Machine ESP32
        attributes.put("Cocktail Machine ESP32", COCKTAIL_MACHINE);

        // Cocktail Machine ESP32 Name
        attributes.put("Cocktail Machine ESP32 Name", "c0605c38-3f94-33f6-ace6-7a5504544a80");

        // Communication-Service
        attributes.put("Communication Service", "dad995d1-f228-38ec-8b0f-593953973406");
        // Message Characteristic has been change to User Message Characteristic
        attributes.put("User Message Characteristic", "eb61e31a-f00b-335f-ad14-d654aac8353d");
        // Response Characteristics has been change to Admin Message Characteristic
        attributes.put("Admin Message Characteristic", "41044979-6a5d-36be-b9f1-d4d49e3f5b73");

        // Status Service and Characteristics
        attributes.put("Status Service", "0f7742d4-ea2d-43c1-9b98-bb4186be905d");

        // Pumps Characteristics: Map of all available pumps and their level
        attributes.put("Status Pumps Characteristic", "1a9a598a-17ce-3fcd-be03-40a48587d04e");
        // Liquids Characteristic: Map of all available liquids and their volumes
        attributes.put("Status Liquids Characteristic", "fc60afb0-2b00-3af2-877a-69ae6815ca2f");
        // State Characteristics: The current state of the cocktail machine and what it does.
        attributes.put("Status State Characteristic", "e9e4b3f2-fd3f-3b76-8688-088a0671843a");
        // ok Recipes Characteristic: All saved recipes and their names.
        attributes.put("Status Recipes Characteristic","9ede6e03-f89b-3e52-bb15-5c6c72605f6c");
        // Cocktail Characteristic: The content of the current cocktail being mixed.
        attributes.put("Status Cocktail Characteristic","7344136f-c552-3efc-b04f-a43793f16d43");
        // ok Current user Characteristic: the current user for whom a cocktail is being
        // made or is ready.If no user is active, the value is -1.
        attributes.put("Status User Queue Characteristic","2ce478ea-8d6f-30ba-9ac6-2389c8d5b172");
        // Last Change Characteristic: If the timestamp has not changed, the available recipes
        // and ingredients are still the same.The timestamp is an internal value of the ESP and
        // has no relation to the real time.
        attributes.put("Status Last Change Characteristic","586b5706-5856-34e1-ad17-94f840298816");
        // Scale Characteristic: Status of Scale
        attributes.put("Status Scale Characteristic","ff18f0ac-f039-4cd0-bee3-b546e3de5551");
        // Error Characteristic: Value: current error (if any)
        attributes.put("Status Error Characteristic","2e03aa0c-b25f-456a-a327-bd175771111a");












        // added attributes.put(BATTERY_LEVEL_CHARACTERISTIC_UUID, "Battery Level Measurement");
    }

/*   public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }*/
    public static String lookupUuid(String defaultName) {
        String uuid = attributes.get(defaultName);
        return uuid ;
    }
}
