package com.example.cocktailmachine.data.db.tables;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * is a collection point for all database tables.
 * This class has a static attribute for each table.
 * It also offers two functions each to regenerate and delete all tables.
 * The difference lies in the variant:
 * - once an SQL-DB is given and thus deleted
 * - once the commands are output as a list of strings
 * @created Fr. 23.Jun 2023 - 12:51
 * @project CocktailMachine
 * @author Johanna Reidt
 */
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

    /**
     * gives list of sql cmds as strings, that create each a table
     * @return list of sql cmds as strings, that create each a table
     * @author Johanna Reidt
     */
    private static List<String> getCreateCmds(){
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

    /**
     * gives list of sql cmds as strings, that delete each a table
     * @author Johanna Reidt
     * @return list of sql cmds as strings, that delete each a table
     */
    private static List<String> getDeleteCmds(){
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

    /**
     * deletes all tables from given db
     * @author Johanna Reidt
     * @param db
     */
    public static void deleteAll(SQLiteDatabase db){
        for(String cmd: getDeleteCmds()){
            db.execSQL(cmd);
        }
        //db.close();
    }

    /**
     * deletes all tables for given db
     * @author Johanna Reidt
     * @param db
     */
    public static void createAll(SQLiteDatabase db){
        for(String cmd: getCreateCmds()){
            db.execSQL(cmd);
        }
        //db.close();
    }



}
