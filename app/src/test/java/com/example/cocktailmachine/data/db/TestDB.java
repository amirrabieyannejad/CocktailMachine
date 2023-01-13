package com.example.cocktailmachine.data.db;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.cocktailmachine.data.Recipe;

public class TestDB {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connection(){
        assertNotNull("DB", NewDatabaseConnection.getDataBase());
    }

    @Test
    public void new_recipe(){
        Recipe recipe = Recipe.makeNew("Magarita");
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }
}
