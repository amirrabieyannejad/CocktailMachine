package com.example.cocktailmachine.data.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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

    @Test
    void testToString1() {
    }

    @Test
    void getCurrentStatus() {
    }

    @Test
    void testGetCurrentStatus() {
    }

    @Test
    void testGetCurrentStatus1() {
    }

    @Test
    void getCurrentStatusMessage() {
    }

    @Test
    void setStatus() {
    }

    @Test
    void testSetStatus() {
    }

    @Test
    void testSetStatus1() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}