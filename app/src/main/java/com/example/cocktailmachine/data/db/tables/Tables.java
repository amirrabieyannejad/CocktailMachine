package com.example.cocktailmachine.data.db.tables;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Tables {

    static final String TYPE_LONG = "LONG";
    static final String TYPE_ID = "INTEGER PRIMARY KEY AUTOINCREMENT";
    static final String TYPE_INTEGER = "INTEGER";
    static final String TYPE_TEXT = "TEXT";
    static final String TYPE_BOOLEAN = "BOOLEAN";

    public Tables(){}
    //COLUMN_TYPE
    //COLUMN_NAME
    //TABLE_NAME

    public static final RecipeTable TABLE_RECIPE = new RecipeTable();
    public static final IngredientTable TABLE_INGREDIENT= new IngredientTable();
    public static final RecipeImageUrlTable TABLE_RECIPE_URL =  new RecipeImageUrlTable();
    public static final IngredientImageUrlTable TABLE_INGREDIENT_URL =  new IngredientImageUrlTable();
    public static final PumpTable TABLE_PUMP = new PumpTable();
    public static final TopicTable TABLE_TOPIC = new TopicTable();
    public static final RecipeIngredientTable TABLE_RECIPE_INGREDIENT = new RecipeIngredientTable();
    public static final RecipeTopicTable TABLE_RECIPE_TOPIC = new RecipeTopicTable();
    public static final IngredientPumpTable TABLE_INGREDIENT_PUMP = new IngredientPumpTable();

    public static List<String> getCreateCmds(){
        List<String> res = new ArrayList<>();
        res.add(TABLE_RECIPE.createTableCmd());
        res.add(TABLE_INGREDIENT.createTableCmd());
        res.add(TABLE_RECIPE_URL.createTableCmd());
        res.add(TABLE_PUMP.createTableCmd());
        res.add(TABLE_INGREDIENT_URL.createTableCmd());
        res.add(TABLE_TOPIC.createTableCmd());
        res.add(TABLE_RECIPE_INGREDIENT.createTableCmd());
        res.add(TABLE_RECIPE_TOPIC.createTableCmd());
        res.add(TABLE_INGREDIENT_PUMP.createTableCmd());
        return res;
    }

    public static List<String> getDeleteCmds(){
        List<String> res = new ArrayList<>();
        res.add(TABLE_RECIPE.deleteTableCmd());
        res.add(TABLE_INGREDIENT.deleteTableCmd());
        res.add(TABLE_RECIPE_URL.deleteTableCmd());
        res.add(TABLE_PUMP.deleteTableCmd());
        res.add(TABLE_INGREDIENT_URL.deleteTableCmd());
        res.add(TABLE_TOPIC.deleteTableCmd());
        res.add(TABLE_RECIPE_INGREDIENT.deleteTableCmd());
        res.add(TABLE_RECIPE_TOPIC.deleteTableCmd());
        res.add(TABLE_INGREDIENT_PUMP.deleteTableCmd());
        return res;
    }

    public static void deleteAll(SQLiteDatabase db){
        for(String cmd: getDeleteCmds()){
            db.execSQL(cmd);
        }
    }

    public static void createAll(SQLiteDatabase db){
        for(String cmd: getCreateCmds()){
            db.execSQL(cmd);
        }
    }



}
