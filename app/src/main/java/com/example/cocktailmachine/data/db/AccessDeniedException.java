package com.example.cocktailmachine.data.db;

public class AccessDeniedException extends Exception{
    public AccessDeniedException() {
        super("User is no admin!");
    }
}
