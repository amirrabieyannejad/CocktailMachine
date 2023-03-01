package com.example.cocktailmachine.data.db;

public class NotInitializedDBException extends Exception{
    public NotInitializedDBException() {
        super("The db was not initialized!!!");
    }
}
