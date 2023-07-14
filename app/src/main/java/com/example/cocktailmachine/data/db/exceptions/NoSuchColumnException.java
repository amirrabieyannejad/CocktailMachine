package com.example.cocktailmachine.data.db.exceptions;

/**
 * is thrown if the column does not exist in the table
 * @author Johanna Reidt
 */
public class NoSuchColumnException extends Exception{
    public NoSuchColumnException(String tablename, String columnname){
        super("In "+tablename+" is no column with the name: "+columnname);
    }
}
