package com.cocktailmachine.data;

import java.util.List;

public interface Pump {
    public long getId();

    /**
     * Returns Milliliters pumped in milliseconds.
     * @return
     */
    public float getPumpInMilliseconds();

    //general

    /**
     *
     * get all pumps
     * @return
     */
    public static List<Pump> getPumps(){
        //TODO: implements this
        return null;
    }
}
