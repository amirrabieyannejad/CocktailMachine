package com.example.cocktailmachine.data.db;

//import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import com.example.cocktailmachine.data.db.old.SQLDataBaseConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SQLDataBaseConnectionTest {

    @Before
    public void setUp() throws Exception {
        SQLDataBaseConnection.initialize(null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSingleton() {
    }

    @Test
    public void initialize() {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onUpgrade() {
    }

    @Test
    public void getIngredient() {
    }

    @Test
    public void getIngredients() {
    }

    @Test
    public void getRecipes() {
    }

    @Test
    public void getRecipe() {
    }

    @Test
    public void addIngredient() {
    }

    @Test
    public void addRecipe() {
    }

    @Test
    public void checkavailablilityofallingredients() {
    }

    @Test
    public void updateIngredient() {
    }

    @Test
    public void updateRecipe() {
    }

    @Test
    public void loadBufferWithAvailable() {
    }

    @Test
    public void loadAvailableRecipes() {
    }

    @Test
    public void loadAvailableIngredients() {
    }

    @Test
    public void getRecipeWith() {
    }

    @Test
    public void getIngredientWith() {
    }


}