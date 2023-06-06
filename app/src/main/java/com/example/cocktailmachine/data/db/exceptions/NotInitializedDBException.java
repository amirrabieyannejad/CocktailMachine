package com.example.cocktailmachine.data.db.exceptions;

public class NotInitializedDBException extends Exception{
    public NotInitializedDBException() {
        super("The db was not initialized!!!");
    }
}
