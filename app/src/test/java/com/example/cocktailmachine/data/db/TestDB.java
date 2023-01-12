package com.example.cocktailmachine.data.db;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestDB {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connection(){
        assertNotNull("DB", NewDatabaseConnection.getDataBase());
    }
}
