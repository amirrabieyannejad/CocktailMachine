package com.example.cocktailmachine.data.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Johanna Reidt
 * @created Do. 21.Dez 2023 - 10:58
 * @project CocktailMachine
 */

@RunWith(AndroidJUnit4.class)
public class PumpTest {

    Context context;
    public void setUp(){
        //InstrumentationRegistry.registerInstance(new Instrumentation(), new Bundle());

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertNotNull("cant be", context);
        DatabaseConnection.init(context);
        try {
            if(DatabaseConnection.getSingleton().getWritableDatabase()==null){
                System.out.println("no writable");
            }
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            System.out.println("no writable");
        }
        try {
            if(DatabaseConnection.getSingleton().getReadableDatabase()==null){
                System.out.println("no readable");
            }


        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            System.out.println("no readable");
        }

        assertEquals("com.example.cocktailmachine", context.getPackageName());
        ExtraHandlingDB.loadPrepedDB(context);
        Log.i("Test", "load prep");
        try {

            Pump.updatePumpStatus(context, new JSONObject("{\"1\":{\"liquid\":\"Bier\",\"volume\":1000.0,\"cal\":[0.0,1000,1000]}, \"2\":{\"liquid\":\"Wodka\",\"volume\":1000.0,\"cal\":[0.0,1000,1000]}}"));
            Log.i("Test", Pump.getPumps(context).toString());
            for(Pump p:  Pump.getPumps(context)){
                Log.i("Test", p.toString());
            }
            assertEquals(Pump.getPumpWithSlot(context, 1).getIngredientName(),"Bier" );
            assertEquals(Pump.getPumpWithSlot(context, 2).getIngredientName(),"Wodka" );
            Log.i("Test", Pump.getPumps(context).toString());
            Log.i("Test", Pump.getPumps(context).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void finish() {
        try {
            DatabaseConnection.getSingleton().close();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreConditions() {
        try {
            assertNotNull(DatabaseConnection.getSingleton());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateLiquidStatus() {

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        try {

            Pump.updatePumpStatus(context, new JSONObject("{\"1\":{\"liquid\":\"water\",\"volume\":1000.0,\"cal\":[0.0,1000,1000]}, \"2\":{\"liquid\":\"Wodka\",\"volume\":1000.0,\"cal\":[0.0,1000,1000]}}"));
            Log.i("Test", Pump.getPumps(context).toString());
            for(Pump p:  Pump.getPumps(context)){
                Log.i("Test", p.toString());
            }
            assertEquals(Pump.getPumpWithSlot(context, 1).getIngredientName(),"water" );
            assertEquals(Pump.getPumpWithSlot(context, 2).getIngredientName(),"Wodka" );
            Log.i("Test", Pump.getPumps(context).toString());
            Log.i("Test", Pump.getPumps(context).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
