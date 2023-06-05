package com.example.cocktailmachine.data;

import android.graphics.Color;
import android.util.Log;


import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class BasicRecipes {
    private static final String TAG = "BasicRecipes";

    /**
     * Am besten nicht Aufrufen.
     * Nur für den Gebrauch in den Test,
     * wenn kein Context vorahanden,
     * sonst immer DB mit Context initialisieren!!!!
     */
    public static void loadTest(){
        DatabaseConnection.initialize_singleton(null);
    }

    public static void loadTopics() throws NotInitializedDBException{

        Topic ice_crushed = Topic.makeNew("Crushed Eis", "klein gehaktes Eis");
        ice_crushed.save();

        Topic ice_cubes = Topic.makeNew("Eis", "gefrorenes Wasser");
        ice_cubes.save();

        Topic zuckersirup = Topic.makeNew("Zuckersirup", "steht neben der Maschine, versüßt, einmal pumpen");
        zuckersirup.save();

        Topic melone = Topic.makeNew("Melone", "eine Scheibe an den Rand stecken");
        melone.save();

    }

    public static void loadIngredients() throws NotInitializedDBException {
        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.WHITE);
        tequila.save();
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        orangenlikör.save();
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.CYAN);
        limettensaft.save();

        /*
        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.RED);
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.WHITE);

         */
        Ingredient wodka = Ingredient.makeNew("Wodka", true, 0);
        wodka.save();
        Ingredient rum = Ingredient.makeNew("Rum", true, 0);
        rum.save();
        Ingredient cola = Ingredient.makeNew("Cola", false, 0);
        cola.save();

        //DatabaseConnection.localRefresh();
    }

    public static void loadPumps() throws NotInitializedDBException{

        Pump t_p = Pump.makeNew();
        t_p.setCurrentIngredient(Ingredient.getIngredient("Tequila"));
        t_p.fill(100);
        t_p.save();
        Pump o_p = Pump.makeNew();
        o_p.setCurrentIngredient(Ingredient.getIngredient("Orangenlikör"));//orangenlikör);
        o_p.fill(100);
        o_p.save();
        Pump l_p = Pump.makeNew();
        l_p.setCurrentIngredient(Ingredient.getIngredient("Limettensaft"));//limettensaft);
        l_p.fill(100);
        l_p.save();
        Pump w_p = Pump.makeNew();
        w_p.setCurrentIngredient(Ingredient.getIngredient("Wodka"));//wodka);
        w_p.fill(100);
        w_p.save();
        Pump r_p = Pump.makeNew();
        r_p.setCurrentIngredient(Ingredient.getIngredient("Rum"));//rum);
        r_p.fill(100);
        r_p.save();
        Pump c_p = Pump.makeNew();
        c_p.setCurrentIngredient(Ingredient.getIngredient("Cola"));//cola);
        c_p.fill(100);
        c_p.save();

        //DatabaseConnection.localRefresh();
    }


    /**
     * Load margarita cocktail
     * @throws NotInitializedDBException
     */
    public static void loadMargarita() throws NotInitializedDBException {
        Log.i(TAG,"loadMargarita");
        /**
        8 cl   weißer Tequila
        4 cl   Orangenlikör (z.B. Cointreau)
                4 cl   frischer Limettensaft
         **/

        
        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(Ingredient.getIngredient("Tequila"), 8);
        magarita.addOrUpdate(Ingredient.getIngredient("Orangenlikör"), 4);
        magarita.addOrUpdate(Ingredient.getIngredient("Limettensaft"), 4);
        magarita.addOrUpdate(Topic.getTopic("Eis"));
        magarita.save();



        //DatabaseConnection.localRefresh();

        Log.i(TAG,"loadMargarita finished");
    }

    public static void loadTequila() throws NotInitializedDBException {
        Log.i(TAG,"loadTequila");
        Recipe magarita = Recipe.makeNew("Margarita 2.0");
        magarita.addOrUpdate(Ingredient.getIngredient("Tequila"), 8);
        magarita.addOrUpdate(Ingredient.getIngredient("Orangenlikör"), 4);
        magarita.addOrUpdate(Ingredient.getIngredient("Limettensaft"), 4);
        magarita.save();

        //DatabaseConnection.localRefresh();
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


        Recipe magarita = Recipe.makeNew("Long Island Ice Tea");
        magarita.addOrUpdate(Ingredient.getIngredient("Tequila"), 2);
        magarita.addOrUpdate(Ingredient.getIngredient("Orangenlikör"), 2);
        magarita.addOrUpdate(Ingredient.getIngredient("Limettensaft"), 2);
        magarita.addOrUpdate(Ingredient.getIngredient("Wodka"), 2);
        magarita.addOrUpdate(Ingredient.getIngredient("Rum"), 2);
        magarita.addOrUpdate(Ingredient.getIngredient("Cola"), 25);
        magarita.addOrUpdate(Topic.getTopic("Zuckersirup"));
        magarita.addOrUpdate(Topic.getTopic("Eis"));
        magarita.save();

        //DatabaseConnection.localRefresh();


        Log.i(TAG,"loadLongIslandIceTea finished");
    }
}
