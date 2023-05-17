package com.example.cocktailmachine.data;

import android.graphics.Color;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class BasicRecipes {
    public static void loadTest(){
        DatabaseConnection.initialize_singleton(null);
    }

    public static void loadMargarita() throws NotInitializedDBException {
        /**
        8 cl   weißer Tequila
        4 cl   Orangenlikör (z.B. Cointreau)
                4 cl   frischer Limettensaft
         **/
        Ingredient tequila = Ingredient.makeNew("Tequila", true, 0);
        tequila.save();
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, 0);
        orangenlikör.save();
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, 0);
        limettensaft.save();
        Topic ice = Topic.makeNew(" Crushed Eis", "klein gehaktes Eis");
        ice.save();
        
        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);
        magarita.addOrUpdate(ice);
        magarita.save();
    }

    public static void loadTequila(){

        Ingredient tequila = Ingredient.makeNew("Tequila", true, Color.RED);
        Ingredient orangenlikör = Ingredient.makeNew("Orangenlikör", true, Color.YELLOW);
        Ingredient limettensaft = Ingredient.makeNew("Limettensaft", false, Color.WHITE);


        Recipe magarita = Recipe.makeNew("Margarita");
        magarita.addOrUpdate(tequila, 8);
        magarita.addOrUpdate(orangenlikör, 4);
        magarita.addOrUpdate(limettensaft, 4);
    }

    public static void loadLongIslandIceTea() throws NotInitializedDBException{
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
    }
}
