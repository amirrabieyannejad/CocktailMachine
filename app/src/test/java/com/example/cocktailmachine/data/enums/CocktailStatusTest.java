package com.example.cocktailmachine.data.enums;

import static org.junit.jupiter.api.Assertions.*;

class CocktailStatusTest {

    @org.junit.jupiter.api.Test
    void testToString() {
        assertEquals(CocktailStatus.cocktail_done.toString(), "cocktail done");
        assertEquals(CocktailStatus.init.toString(), "init");
        assertEquals(CocktailStatus.mixing.toString(), "mixing");
        assertEquals(CocktailStatus.not.toString(), "not");
        assertEquals(CocktailStatus.pumping.toString(), "pumping");
        assertEquals(CocktailStatus.ready.toString(), "ready");
    }
}