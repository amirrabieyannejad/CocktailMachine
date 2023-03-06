package com.cocktailmachine.data.db.tables;

public class NoSuchColumnException extends Exception{
    NoSuchColumnException(String tablename, String columnname){
        super("In "+tablename+" is no column with the name: "+columnname);
    }
}
