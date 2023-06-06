package com.example.cocktailmachine.data.db.exceptions;

public class AccessDeniedException extends Exception{
    public AccessDeniedException() {
        super("User is no admin!");
    }
}
