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
    public static String COCKTAIL_MACHINE = "0f7742d4-ea2d-43c1-9b98-bb4186be905d";

    // TO-DO: Add CCC Descriptor for add Notification/Indication to specific Characteristic
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    static   {
        // Cocktail Machine ESP32
        attributes.put("Cocktail Machine ESP32", COCKTAIL_MACHINE);

        // Communication-Service
        attributes.put("Communication Service", "dad995d1-f228-38ec-8b0f-593953973406");
        attributes.put("Message Characteristic", "eb61e31a-f00b-335f-ad14-d654aac8353d");
        attributes.put("Response Characteristics", "06dc28ef-79a4-3245-85ce-a6921e35529d");

        // State
        attributes.put("State Service", "addf5391-2030-3cf0-a64f-31d5156d7f00");
        attributes.put("State Characteristic", "e9e4b3f2-fd3f-3b76-8688-088a0671843a");

        // Recipes
        attributes.put("Recipes Service", "8f0aec28-5985-335e-baa2-8e03ce08b513");
        attributes.put("Recipes Characteristic","9ede6e03-f89b-3e52-bb15-5c6c72605f6c");

        // Cocktail
        attributes.put("Cocktail Service", "8a421a72-b9a0-342d-ab57-afa3d67149d1");
        attributes.put("Cocktail Characteristic","7344136f-c552-3efc-b04f-a43793f16d43");

        // Liquids
        attributes.put("Liquids Service", "17eed42a-f06b-3f58-9b26-60e78bccf857");
        attributes.put("Liquids Characteristic", "fc60afb0-2b00-3af2-877a-69ae6815ca2f");











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
