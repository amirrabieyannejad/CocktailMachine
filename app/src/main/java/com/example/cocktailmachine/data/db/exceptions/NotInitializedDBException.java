package com.example.cocktailmachine.data.db.exceptions;
/**
 * is thrown if the DB has not yet been initialised with a context.
 * @author Johanna Reidt
 */
public class NotInitializedDBException extends Exception{
    public NotInitializedDBException() {
        super("The db was not initialized!!!");
    }
}
