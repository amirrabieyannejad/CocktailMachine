package com.example.cocktailmachine.data;

import android.graphics.Color;
import android.util.Log;


import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class BasicRecipes {
    private static final String TAG = "BasicRecipes";

    public static void loadTest(){
        DatabaseConnection.initialize_singleton(null);
    }

    public static void loadMargarita() throws NotInitializedDBException {
        Log.i(TAG,"loadMargarita");
        /**
        8 cl   weißer Tequila
        4 cl   Orangenlikör (z.B. Cointreau)
                4 cl   frischer Limettensaft
         **/
        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.WHITE);
        tequila.save();
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        orangenlikör.save();
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.CYAN);
        limettensaft.save();
        Topic ice = Topic.makeNew(" Crushed Eis", "klein gehaktes Eis");
        ice.save();
        
        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);
        magarita.addOrUpdate(ice);
        magarita.save();

        Pump t_p = Pump.makeNew();
        t_p.setCurrentIngredient(tequila);
        t_p.fill(100);
        t_p.save();
        Pump o_p = Pump.makeNew();
        o_p.setCurrentIngredient(orangenlikör);
        o_p.fill(100);
        o_p.save();
        Pump l_p = Pump.makeNew();
        l_p.setCurrentIngredient(limettensaft);
        l_p.fill(100);
        l_p.save();
        Log.i(TAG,"loadMargarita finished");
    }

    public static void loadTequila(){
        Log.i(TAG,"loadTequila");

        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.RED);
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.WHITE);


        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);
        Log.i(TAG,"loadTequila finished");
    }

    public static void loadLongIslandIceTea() throws NotInitializedDBException{
        Log.i(TAG,"loadLongIslandIceTea");
        /**
         *
         2 cl Rum
         2 cl Wodka
         2 cl Tequila
         2 cl Orangenlikör
         2 cl Limettensaft
         2 Zuckersirup
         1/4 Liter Cola
         */
        Ingredient tequila = Ingredient.makeNew("Tequila", true, 0);
        tequila.save();
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, 0);
        orangenlikör.save();
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, 0);
        limettensaft.save();
        Ingredient wodka = Ingredient.makeNew("Wodka", true, 0);
        wodka.save();
        Ingredient rum = Ingredient.makeNew("Rum", true, 0);
        rum.save();
        Ingredient cola = Ingredient.makeNew("Cola", false, 0);
        cola.save();
        Ingredient zuckersirup = Ingredient.makeNew("Zuckersirup", false, 0);
        zuckersirup.save();
        Topic ice = Topic.makeNew("Eis", "gefrorenes Wasser");
        ice.save();

        Recipe magarita = Recipe.makeNew("Long Island Ice Tea");
        magarita.addOrUpdate(tequila, 2);
        magarita.addOrUpdate(orangenlikör, 2);
        magarita.addOrUpdate(limettensaft, 2);
        magarita.addOrUpdate(wodka, 2);
        magarita.addOrUpdate(rum, 2);
        magarita.addOrUpdate(zuckersirup, 2);
        magarita.addOrUpdate(cola, 25);
        magarita.addOrUpdate(ice);
        magarita.save();

        Pump t_p = Pump.makeNew();
        t_p.setCurrentIngredient(tequila);
        t_p.fill(100);
        t_p.save();
        Pump o_p = Pump.makeNew();
        o_p.setCurrentIngredient(orangenlikör);
        o_p.fill(100);
        o_p.save();
        Pump l_p = Pump.makeNew();
        l_p.setCurrentIngredient(limettensaft);
        l_p.fill(100);
        l_p.save();
        Pump w_p = Pump.makeNew();
        w_p.setCurrentIngredient(wodka);
        w_p.fill(100);
        w_p.save();
        Pump r_p = Pump.makeNew();
        r_p.setCurrentIngredient(rum);
        r_p.fill(100);
        r_p.save();
        Pump c_p = Pump.makeNew();
        c_p.setCurrentIngredient(cola);
        c_p.fill(100);
        c_p.save();
        Pump z_p = Pump.makeNew();
        z_p.setCurrentIngredient(zuckersirup);
        z_p.fill(100);
        z_p.save();
        Log.i(TAG,"loadLongIslandIceTea finished");
    }
}
