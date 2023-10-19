package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.BasicRecipes;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.exceptions.AccessDeniedException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class DatabaseConnection extends SQLiteOpenHelper {
    private static DatabaseConnection singleton = null;
    private static final String DBname = "DB.db";
    private static final String TAG = "DatabaseConnection";
    private static int version = 1;
    private static final SQLiteDatabase.CursorFactory factory = null;


    private DatabaseConnection(@Nullable Context context) {
        super(context,
                DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);
        Log.i(TAG, "DatabaseConnection");
    }

    static synchronized DatabaseConnection getSingleton() throws NotInitializedDBException {

        Log.i(TAG, "getSingleton");
        if(!DatabaseConnection.isInitialized()) {
            throw new NotInitializedDBException();
        }
        return DatabaseConnection.singleton;
    }

    public void loadDummy(Context context) throws NotInitializedDBException, MissingIngredientPumpException {
        if(Dummy.isDummy) {
            //DatabaseConnection.singleton.emptyAll();
            BasicRecipes.loadTopics(context);
            BasicRecipes.loadIngredients(context);
            if(!Dummy.withSetCalibration) {
                BasicRecipes.loadPumps(context);
            }
            BasicRecipes.loadMargarita(context);
            BasicRecipes.loadLongIslandIceTea(context);
        }
    }


    static synchronized boolean isInitialized(){
        Log.i(TAG, "is_initialized");
        return DatabaseConnection.singleton != null;
    }

    static synchronized DatabaseConnection init(Context context){
        Log.i(TAG, "init");
        if(DatabaseConnection.isInitialized()){
            DatabaseConnection.singleton.close();
        }
        DatabaseConnection.singleton = new DatabaseConnection(context);
        return  DatabaseConnection.singleton;
    }











    //NewDatabaseConnection Overrides

    /**
     * deletes all Tables and creates all newly empty
     * @author Johanna Reidt
     */
    void emptyAll(){
        Log.i(TAG, "emptyAll");
        //resetAll();
        Tables.deleteAll(this.getWritableDatabase());
        Tables.createAll(this.getWritableDatabase());
    }
    /*
    private void resetAll(){
        Log.i(TAG, "resetAll");
        this.ingredients = new ArrayList<>();
        this.ingredientPumps = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.pumps = new ArrayList<>();
        this.recipeIngredients = new ArrayList<>();
        this.recipes = new ArrayList<>();
    }


     */
    /**
     * complety deletes table with ingredient pump connection
     */
    void emptyUpPumps() {
        Log.i(TAG, "emptyUpPumps");
        Tables.TABLE_INGREDIENT_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_INGREDIENT_PUMP.createTable(this.getWritableDatabase());
    }

    void setUpEmptyPumps() {
        Log.i(TAG, "setUpEmptyPumps");
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_PUMP.createTable(this.getWritableDatabase());
    }














    //LOAD



    void loadForSetUp() {
        Log.i(TAG, "loadForSetUp");
        this.setUpEmptyPumps();
        //this.ingredients = this.loadAllIngredients();
    }

    /**
     * Loads all ingredients from db
     * @author Johanna Reidt
     * @return
     */
    List<Ingredient> loadAllIngredients() {
        Log.i(TAG, "loadAllIngredients");

        List<? extends Ingredient> res = Tables.TABLE_INGREDIENT.getAllElements(this.getReadableDatabase());
        return (List<Ingredient>) res;
    }



    /**
     * loads all recipes from db
     * @author Johanna Reidt
     * @return
     */
    List<Recipe> loadAllRecipes() {
        Log.i(TAG, "loadAllRecipes");
        List<? extends Recipe> res =  Tables.TABLE_RECIPE.getAllElements(this.getReadableDatabase());
        return (List<Recipe>) res;
    }



    /**
     *
     * loads ingredient pump from db
     * @author Johanna Reidt
     * @return
     */
    List<SQLIngredientPump> loadIngredientPumps() {
        Log.i(TAG, "loadIngredientPumps");
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(this.getReadableDatabase());
    }

    /**
     * loads pumps from db
     * @author Johanna Reidt
     * @return
     */
    List<Pump> loadPumps() {
        Log.i(TAG, "loadPumps");
        // return IngredientTable.
        List<? extends Pump> res = Tables.TABLE_PUMP.getAllElements(this.getReadableDatabase());
        return (List<Pump>) res;
    }

    /**
     * loads topics from db
     * @author Johanna Reidt
     * @return
     */
    List<Topic> loadTopics() {
        Log.i(TAG, "loadTopics");
        //return
        List<? extends Topic> res = Tables.TABLE_TOPIC.getAllElements(this.getReadableDatabase());
        return (List<Topic>) res;
    }

    /**
     * load ingr vol/ ingr recipe connection from db
     * @author Johanna Reidt
     * @return
     */
    List<SQLRecipeIngredient> loadIngredientVolumes(){
        Log.i(TAG, "loadIngredientVolumes");
        //return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);
        return Tables.TABLE_RECIPE_INGREDIENT.getAllElements(this.getReadableDatabase());

    }

    List<SQLRecipeTopic> loadRecipeTopic(){
        Log.i(TAG, "loadIngredientVolumes");
        //return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);
        return Tables.TABLE_RECIPE_TOPIC.getAllElements(this.getReadableDatabase());
    }














    //GETTER //Not allowed to fetch from database !!!only!!! from buffer unless they are admins





    List<String> getUrls(SQLIngredient newSQLIngredient) {
        Log.i(TAG, "getUrls");
        return Tables.TABLE_INGREDIENT_URL.getUrls(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    List<SQLIngredientImageUrlElement> getUrlElements(SQLIngredient newSQLIngredient) {
        Log.i(TAG, "getUrlElements");
        return Tables.TABLE_INGREDIENT_URL.getElements(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    List<String> getUrls(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getUrls");
        return Tables.TABLE_RECIPE_URL.getUrls(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    List<SQLRecipeImageUrlElement> getUrlElements(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getUrlElements");
        return Tables.TABLE_RECIPE_URL.getElements(this.getReadableDatabase(), newSQLRecipe.getID());
    }



















    // Helper Over rides

    public void onConfigure(SQLiteDatabase db) {
        Log.i(TAG, "onConfigure");
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        db.enableWriteAheadLogging();
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
        Tables.createAll(db);
        /*
        for (String createCmd : Tables.getCreateCmds()) {
            db.execSQL(createCmd);
        }

         */


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
        //db.enableWriteAheadLogging();
        if(oldVersion == DatabaseConnection.version) {
            /*
            for (String deleteCmd : Tables.getDeleteCmds()) {
               db.execSQL(deleteCmd);
            }

             */
            Tables.deleteAll(db);
            onCreate(db);
            DatabaseConnection.version = newVersion;
        }
    }


    /**
     *
     */
    @Override
    public void close(){
        super.close();
        singleton = null;
    }


}
