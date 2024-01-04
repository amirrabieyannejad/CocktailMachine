package com.example.cocktailmachine.data;

import android.content.Context;
import android.util.Log;


import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cocktailmachine.ui.Menue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

/**
 * @author Johanna Reidt
 * @created Di. 10.Okt 2023 - 14:47
 * @project CocktailMachine
 */
class IngredientTest {

    private static final String TAG = "IngredientTest";
    private Context context;
    private Ingredient i1;
    private Ingredient i2;

    @BeforeEach
    void setUp() {
        context = Robolectric.buildActivity(Menue.class).create().get();
        i1 = Ingredient.searchOrNew(context, "Wasser");
        i2 = Ingredient.searchOrNew(context, "Bier");
        Log.i(TAG, i1.toString());
        Log.i(TAG, i2.toString());
    }

    @AfterEach
    void tearDown() {
        //TODO: ???
    }

    @Test
    void getID() {
        Log.i(TAG,"getID");
    }

    @Test
    void getName() {
    }

    @Test
    void isAlcoholic() {
    }

    @Test
    void isAvailable() {
    }

    @Test
    void getColor() {
    }

    @Test
    void setColor() {
    }

    @Test
    void setAlcoholic() {
    }

    @Test
    void setName() {
    }

    @Test
    void getImageUrls() {
    }

    @Test
    void addImageUrl() {
    }

    @Test
    void removeImageUrl() {
    }

    @Test
    void getPump() {
    }

    @Test
    void getPumpId() {
    }

    @Test
    void getVolume() {
    }

    @Test
    void setIngredientPump() {
    }

    @Test
    void setPump() {
    }

    @Test
    void empty() {
    }

    @Test
    void pump() {
    }

    @Test
    void getAllIngredients() {
    }

    @Test
    void testGetAllIngredients() {
    }

    @Test
    void getAllIngredientNames() {
    }

    @Test
    void getAvailableIngredients() {
    }

    @Test
    void getAvailableIngredientNames() {
    }

    @Test
    void testGetAvailableIngredients() {
    }

    @Test
    void getIngredient() {
    }

    @Test
    void testGetIngredient() {
    }

    @Test
    void testGetIngredient1() {
    }

    @Test
    void makeNew() {
    }

    @Test
    void testMakeNew() {
    }

    @Test
    void testMakeNew1() {
    }

    @Test
    void testMakeNew2() {
    }

    @Test
    void searchOrNew() {
    }

    @Test
    void getPumpSet() {
    }



    @Test
    void areContentsTheSame() {
    }

    @Test
    void testGetID() {
    }

    @Test
    void testGetName() {
    }

    @Test
    void testIsAlcoholic() {
    }

    @Test
    void testIsAvailable() {
    }

    @Test
    void testGetColor() {
    }

    @Test
    void testSetColor() {
    }

    @Test
    void testSetAlcoholic() {
    }

    @Test
    void testSetName() {
    }

    @Test
    void testGetImageUrls() {
    }

    @Test
    void testAddImageUrl() {
    }

    @Test
    void testRemoveImageUrl() {
    }

    @Test
    void testGetPump() {
    }

    @Test
    void testGetPumpId() {
    }

    @Test
    void testGetVolume() {
    }

    @Test
    void testSetIngredientPump() {
    }

    @Test
    void testSetPump() {
    }

    @Test
    void testEmpty() {
    }

    @Test
    void testPump() {
    }

    @Test
    void getChunkIterator() {
    }

    @Test
    void testGetAllIngredients1() {
    }

    @Test
    void testGetAllIngredientNames() {
    }

    @Test
    void testGetAvailableIngredients1() {
    }

    @Test
    void testGetAvailableIngredientNames() {
    }

    @Test
    void testGetAvailableIngredients2() {
    }

    @Test
    void testGetIngredient2() {
    }

    @Test
    void testGetIngredient3() {
    }

    @Test
    void testMakeNew3() {
    }

    @Test
    void testMakeNew4() {
    }

    @Test
    void testMakeNew5() {
    }

    @Test
    void testMakeNew6() {
    }

    @Test
    void testSearchOrNew() {
    }

    @Test
    void testSearchOrNew1() {
    }
}