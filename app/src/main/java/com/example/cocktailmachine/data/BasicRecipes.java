package com.example.cocktailmachine.data;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;


import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.enums.Postexecute;

public class BasicRecipes {
    private static final String TAG = "BasicRecipes";

    /**
     * Am besten nicht Aufrufen.
     * Nur für den Gebrauch in den Test,
     * wenn kein Context vorahanden,
     * sonst immer DB mit Context initialisieren!!!!
     */

    public static void loadTopics(Context context) {

        Topic ice_crushed = Topic.searchOrNew(context,"Crushed Eis", "klein gehaktes Eis");

        Topic ice_cubes = Topic.searchOrNew(context,"Eis", "gefrorenes Wasser");

        Topic zuckersirup = Topic.searchOrNew(context,"Zuckersirup", "steht neben der Maschine, versüßt, einmal pumpen");

        Topic melone = Topic.searchOrNew(context,"Melone", "eine Scheibe an den Rand stecken");

    }

    public static void loadIngredients(Context context) {
        Ingredient.searchOrNew(context,"Tequila", true, Color.WHITE);
        Ingredient.searchOrNew(context,"Orangenlikör", true, Color.YELLOW);
        Ingredient.searchOrNew(context,"Limettensaft", false, Color.CYAN);

        /*
        Ingredient tequila = Ingredient.searchOrNew("Tequila", true, Color.RED);
        Ingredient orangenlikör = Ingredient.searchOrNew("Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = Ingredient.searchOrNew("Limettensaft", false, Color.WHITE);

         */
        Ingredient wodka = Ingredient.searchOrNew(context,"Wodka", true, Color.BLUE);


        Ingredient rum = Ingredient.searchOrNew(context,"Rum", true, Color.GRAY);

        Ingredient cola = Ingredient.searchOrNew(context,"Cola", false, Color.BLACK);


        //DatabaseConnection.localRefresh();
    }

    public static void loadPumps(Context context) throws  MissingIngredientPumpException {

        ExtraHandlingDB.loadForSetUp(context);

        Pump t_p = Pump.makeNew();
        t_p.setSlot(3);
        //Pump t_p  = Pump.getPumpWithSlot(context,1);
        Log.i(TAG, String.valueOf(t_p));
        t_p.setCurrentIngredient(context, Ingredient.searchOrNew(context,"Tequila"));
        t_p.fill(context, 1000);
        t_p.save(context);
        Pump o_p = Pump.makeNew();
        o_p.setSlot(3);
        //Pump o_p  = Pump.getPumpWithSlot(context,2);
        Log.i(TAG, String.valueOf(o_p));
        o_p.setCurrentIngredient(context, Ingredient.searchOrNew(context, "Orangenlikör"));//orangenlikör);
        o_p.fill(context, 1000);
        o_p.save(context);
        Pump l_p = Pump.makeNew();
        l_p.setSlot(3);
        //Pump l_p  = Pump.getPumpWithSlot(context,3);
        Log.i(TAG, String.valueOf(l_p));
        l_p.setCurrentIngredient(context, Ingredient.searchOrNew(context, "Limettensaft"));//limettensaft);
        l_p.fill(context, 1000);
        l_p.save(context);
        Pump w_p = Pump.makeNew();
        w_p.setSlot(4);
        //Pump w_p  = Pump.getPumpWithSlot(context,4);
        Log.i(TAG, String.valueOf(w_p));
        w_p.setCurrentIngredient(context, Ingredient.searchOrNew(context, "Wodka"));//wodka);
        w_p.fill(context, 1000);
        w_p.save(context);
        Pump r_p = Pump.makeNew();
        r_p.setSlot(5);
        //Pump r_p  = Pump.getPumpWithSlot(context,5);
        Log.i(TAG, String.valueOf(r_p));
        r_p.setCurrentIngredient(context, Ingredient.searchOrNew(context, "Rum"));//rum);
        r_p.fill(context, 1000);
        r_p.save(context);
        Pump c_p = Pump.makeNew();
        c_p.setSlot(6);
        //Pump c_p  = Pump.getPumpWithSlot(context,6);
        Log.i(TAG, String.valueOf(c_p));
        c_p.setCurrentIngredient(context, Ingredient.searchOrNew(context, "Cola"));//cola);
        c_p.fill(context, 1000);
        c_p.save(context);

        //DatabaseConnection.localRefresh();
    }


    /**
     * Load margarita cocktail
     * @throws NotInitializedDBException
     */
    public static void loadMargarita(Context context) throws NotInitializedDBException {
        Log.i(TAG,"loadMargarita");
        /**
        8 cl   weißer Tequila
        4 cl   Orangenlikör (z.B. Cointreau)
                4 cl   frischer Limettensaft
         **/

        
        Recipe magarita = Recipe.searchOrNew(context,"Margarita");
        magarita.add(context, Ingredient.searchOrNew(context, "Tequila"), 8);
        magarita.add(context, Ingredient.searchOrNew(context, "Orangenlikör"), 4);
        magarita.add(context, Ingredient.searchOrNew(context, "Limettensaft"), 4);
        magarita.add(context, Topic.searchOrNew(context, "Eis", "gefrorenes Wasser"));
        magarita.save(context);



        //DatabaseConnection.localRefresh();

        Log.i(TAG,"loadMargarita finished");
    }

    public static void loadTequila(Context context) throws NotInitializedDBException {
        Log.i(TAG,"loadTequila");
        Recipe magarita = Recipe.searchOrNew(context,"Margarita 2.0");
        magarita.add(context, Ingredient.searchOrNew(context, "Tequila"), 8);
        magarita.add(context, Ingredient.searchOrNew(context, "Orangenlikör"), 4);
        magarita.add(context, Ingredient.searchOrNew(context, "Limettensaft"), 4);
        magarita.save(context);

        //DatabaseConnection.localRefresh();
        Log.i(TAG,"loadTequila finished");
    }

    public static void loadLongIslandIceTea(Context context) throws NotInitializedDBException{
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


        Recipe magarita = Recipe.searchOrNew(context,"Long Island Ice Tea");
        magarita.add(context, Ingredient.getIngredient(context, "Tequila"), 2);
        magarita.add(context, Ingredient.getIngredient(context, "Orangenlikör"), 2);
        magarita.add(context, Ingredient.getIngredient(context, "Limettensaft"), 2);
        magarita.add(context, Ingredient.getIngredient(context, "Wodka"), 2);
        magarita.add(context, Ingredient.getIngredient(context, "Rum"), 2);
        magarita.add(context, Ingredient.getIngredient(context, "Cola"), 25);
        magarita.add(context, Topic.getTopic(context, "Zuckersirup"));
        magarita.add(context, Topic.getTopic(context, "Eis"));
        magarita.save(context);


        //DatabaseConnection.localRefresh();


        Log.i(TAG,"loadLongIslandIceTea finished");
    }
}
