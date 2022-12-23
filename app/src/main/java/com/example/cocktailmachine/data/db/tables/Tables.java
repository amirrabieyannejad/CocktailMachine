package com.example.cocktailmachine.data.db.tables;

import java.util.ArrayList;
import java.util.List;

public class Tables {

    static final String TYPE_LONG = "LONG";
    static final String TYPE_ID = "LONG PRIMARY KEY";
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

    public static List<String> getCreates(){
        List<String> res = new ArrayList<>();
        res.add(TABLE_RECIPE.createTable());
        res.add(TABLE_INGREDIENT.createTable());
        res.add(TABLE_RECIPE_URL.createTable());
        res.add(TABLE_PUMP.createTable());
        res.add(TABLE_INGREDIENT_URL.createTable());
        res.add(TABLE_TOPIC.createTable());
        res.add(TABLE_RECIPE_INGREDIENT.createTable());
        res.add(TABLE_RECIPE_TOPIC.createTable());
        return res;
    }

    public static List<String> getDeletes(){
        List<String> res = new ArrayList<>();
        res.add(TABLE_RECIPE.deleteTable());
        res.add(TABLE_INGREDIENT.deleteTable());
        res.add(TABLE_RECIPE_URL.deleteTable());
        res.add(TABLE_PUMP.deleteTable());
        res.add(TABLE_INGREDIENT_URL.deleteTable());
        res.add(TABLE_TOPIC.deleteTable());
        res.add(TABLE_RECIPE_INGREDIENT.deleteTable());
        res.add(TABLE_RECIPE_TOPIC.deleteTable());
        return res;
    }



}
