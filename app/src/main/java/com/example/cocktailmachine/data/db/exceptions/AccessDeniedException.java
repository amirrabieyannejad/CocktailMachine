package com.example.cocktailmachine.data.db.exceptions;

/**
 * is thrown when the user accesses a function that requires admin rights
 * @author Johanna Reidt
 */
public class AccessDeniedException extends Exception{
    public AccessDeniedException() {
        super("User is no admin!");
    }
}
