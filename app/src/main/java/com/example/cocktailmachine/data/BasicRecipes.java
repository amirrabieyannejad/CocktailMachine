package com.example.cocktailmachine.data;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;


import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;

public class BasicRecipes {
    private static final String TAG = "BasicRecipes";

    /**
     * Am besten nicht Aufrufen.
     * Nur für den Gebrauch in den Test,
     * wenn kein Context vorahanden,
     * sonst immer DB mit Context initialisieren!!!!
     */

    public static void loadTopics(Context context) {

        Topic ice_crushed = Topic.makeNew("Crushed Eis", "klein gehaktes Eis");
        ice_crushed.save(context);

        Topic ice_cubes = Topic.makeNew("Eis", "gefrorenes Wasser");
        ice_cubes.save(context);

        Topic zuckersirup = Topic.makeNew("Zuckersirup", "steht neben der Maschine, versüßt, einmal pumpen");
        zuckersirup.save(context);

        Topic melone = Topic.makeNew("Melone", "eine Scheibe an den Rand stecken");
        melone.save(context);

    }

    public static void loadIngredients(Context context) {
        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.WHITE);
        tequila.save(context);
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        orangenlikör.save(context);
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.CYAN);
        limettensaft.save(context);

        /*
        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.RED);
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.WHITE);

         */
        Ingredient wodka = Ingredient.makeNew("Wodka", true, Color.BLUE);
        wodka.save(context);
        Ingredient rum = Ingredient.makeNew("Rum", true, Color.GRAY);
        rum.save(context);
        Ingredient cola = Ingredient.makeNew("Cola", false, Color.BLACK);
        cola.save(context);

        //DatabaseConnection.localRefresh();
    }

    public static void loadPumps(Context context) throws  MissingIngredientPumpException {

        Pump t_p = Pump.makeNew();
        t_p.setCurrentIngredient(context, Ingredient.getIngredient("Tequila"));
        t_p.fill(1000);
        t_p.save(context);
        Pump o_p = Pump.makeNew();
        o_p.setCurrentIngredient(context, Ingredient.getIngredient("Orangenlikör"));//orangenlikör);
        o_p.fill(1000);
        o_p.save(context);
        Pump l_p = Pump.makeNew();
        l_p.setCurrentIngredient(context, Ingredient.getIngredient("Limettensaft"));//limettensaft);
        l_p.fill(1000);
        l_p.save(context);
        Pump w_p = Pump.makeNew();
        w_p.setCurrentIngredient(context, Ingredient.getIngredient("Wodka"));//wodka);
        w_p.fill(1000);
        w_p.save(context);
        Pump r_p = Pump.makeNew();
        r_p.setCurrentIngredient(context, Ingredient.getIngredient("Rum"));//rum);
        r_p.fill(1000);
        r_p.save(context);
        Pump c_p = Pump.makeNew();
        c_p.setCurrentIngredient(context, Ingredient.getIngredient("Cola"));//cola);
        c_p.fill(1000);
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

        
        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.add(context, Ingredient.getIngredient("Tequila"), 8);
        magarita.add(context, Ingredient.getIngredient("Orangenlikör"), 4);
        magarita.add(context, Ingredient.getIngredient("Limettensaft"), 4);
        magarita.add(context, Topic.getTopic("Eis"));
        magarita.save(context);



        //DatabaseConnection.localRefresh();

        Log.i(TAG,"loadMargarita finished");
    }

    public static void loadTequila(Context context) throws NotInitializedDBException {
        Log.i(TAG,"loadTequila");
        Recipe magarita = Recipe.makeNew("Margarita 2.0");
        magarita.add(context, Ingredient.getIngredient("Tequila"), 8);
        magarita.add(context, Ingredient.getIngredient("Orangenlikör"), 4);
        magarita.add(context, Ingredient.getIngredient("Limettensaft"), 4);
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


        Recipe magarita = Recipe.makeNew("Long Island Ice Tea");
        magarita.add(context, Ingredient.getIngredient("Tequila"), 2);
        magarita.add(context, Ingredient.getIngredient("Orangenlikör"), 2);
        magarita.add(context, Ingredient.getIngredient("Limettensaft"), 2);
        magarita.add(context, Ingredient.getIngredient("Wodka"), 2);
        magarita.add(context, Ingredient.getIngredient("Rum"), 2);
        magarita.add(context, Ingredient.getIngredient("Cola"), 25);
        magarita.add(context, Topic.getTopic("Zuckersirup"));
        magarita.add(context, Topic.getTopic("Eis"));
        magarita.save(context);


        //DatabaseConnection.localRefresh();


        Log.i(TAG,"loadLongIslandIceTea finished");
    }
}
