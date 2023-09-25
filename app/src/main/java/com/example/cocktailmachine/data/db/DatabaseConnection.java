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
    private static final String DBname = "DB";
    private static final String TAG = "DatabaseConnection";
    private static int version = 1;
    private static final SQLiteDatabase.CursorFactory factory = null;
    //private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
    //private List<Pump> pumps = new ArrayList<>();
    //private List<Ingredient> ingredients = new ArrayList<>();
    //private List<Recipe> recipes = new ArrayList<>();
    //private List<Topic> topics = new ArrayList<>();
    //private List<SQLIngredientPump> ingredientPumps = new ArrayList<>();
    //private List<SQLRecipeIngredient> recipeIngredients = new ArrayList<>();


    private DatabaseConnection(@Nullable Context context) {
        super(context, DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);
        Log.i(TAG, "DatabaseConnection");
        //this.privilege = UserPrivilegeLevel.User;
    }

    private DatabaseConnection(@Nullable Context context, UserPrivilegeLevel privilege) {
        super(context, DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);
        //this.privilege = privilege;
        Log.i(TAG, "DatabaseConnection");
        AdminRights.setUserPrivilegeLevel(privilege);

    }

    static synchronized DatabaseConnection getSingleton() throws NotInitializedDBException {

        Log.i(TAG, "getSingleton");
        if(DatabaseConnection.isInitialized()) {
            throw new NotInitializedDBException();
        }
        return DatabaseConnection.singleton;
    }

    static synchronized DatabaseConnection getDataBase() throws NotInitializedDBException {
        Log.i(TAG, "getDataBase");
        return DatabaseConnection.getSingleton();
    }

    static synchronized void initializeSingleton(Context context){
        Log.i(TAG, "initialize_singleton");
        DatabaseConnection.singleton = new DatabaseConnection(context);
        try {
            Log.i(TAG, "initialize_singleton: start loading");
            DatabaseConnection.singleton.loadDummy(context);
            Buffer.getSingleton().checkAllAvailability(context);
            Log.i(TAG, "initialize_singleton: finished loading");
        } catch (NotInitializedDBException|MissingIngredientPumpException e) {
            Log.i(TAG, "initialize_singleton: Exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    static synchronized void initializeSingleton(Context context, UserPrivilegeLevel privilege){
        Log.i(TAG, "initialize_singleton");
        DatabaseConnection.singleton = new DatabaseConnection(context, privilege);
        try {
            Log.i(TAG, "initialize_singleton: start loading");
            DatabaseConnection.singleton.loadDummy(context);
            Buffer.getSingleton().checkAllAvailability(context);
            Log.i(TAG, "initialize_singleton: finished loading");
        }  catch (NotInitializedDBException|MissingIngredientPumpException e) {
            Log.i(TAG, "initialize_singleton: Exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private void loadDummy(Context context) throws NotInitializedDBException, MissingIngredientPumpException {
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
        return DatabaseConnection.singleton == null;
    }

    static synchronized DatabaseConnection init(Context context){
        Log.i(TAG, "init");
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


    /*
    List<Recipe> loadAvailabilityForRecipes() {
        Log.i(TAG, "loadAvailableRecipes");
        //return (List<Recipe>) Tables.TABLE_RECIPE.getAvailable(this.getReadableDatabase() );
        List<Recipe> res = new ArrayList<>();
        for(Recipe i: this.recipes){
            if(i.loadAvailable()){
                res.add(i);
            }
        }
        return res;
    }

     */

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
     * load Availability For Ingredients
     * @return available ingredients
     */
    /*
    List<Ingredient> loadAvailabilityForIngredients() {
        Log.i(TAG, "loadAvailableIngredients");
       // return IngredientTable.
        /*
        List<? extends Ingredient> res =  Tables.TABLE_INGREDIENT.getElements(
                this.getReadableDatabase(),
                this.getAvailableIngredientIDs());
        return (List<Ingredient>) res;

         */
    /*
        List<Ingredient> res = new ArrayList<>();
        for(Ingredient i: this.ingredients){
            if(i.isAvailable()){
                res.add(i);
            }
        }
        return res;
    }
    */


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

    /*
    void checkForAvailablityInRecipes(){
        for(Recipe r: this.recipes){
            r.isAvailable();
        }
    }

     */



    //CHECKER
/*
    boolean checkAvailabilityOfAllIngredients(HashMap<Long, Integer> ingredientVolume) {
        Log.i(TAG, "checkAvailabilityOfAllIngredients");
        final List<Boolean> availables = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientVolume.forEach((id, time)->{
                availables.add(this.getIngredient(id).getVolume()>time);
            });
            return availables.stream().reduce(true, (b1,b2)-> b1 && b2);
        }
        return Helper.ingredientAvailable(ingredientVolume);
    }

 */

    /**
     * load all availabilities
     */










    //GETTER //Not allowed to fetch from database !!!only!!! from buffer unless they are admins

    /*
    private List<Long> getAvailableIngredientIDs(){
        Log.i(TAG, "getAvailableIngredientIDs");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredientPumps
                    .stream().map(SQLIngredientPump::getIngredientID)
                    .collect(Collectors.toList());
        }
        return Helper.getIngredientIds(this.ingredientPumps);
    }

     */
    /*
    List<SQLIngredientPump> getIngredientPumps() {
        Log.i(TAG, "getIngredientPumps");
        return this.ingredientPumps;
    }


     */
    /*
    List<Pump> getPumps() {
        Log.i(TAG, "getPumps");
        return this.pumps;
    }


     */
    /*
    Pump getPump(Long id){
        Log.i(TAG, "getPump");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.pumps.stream().filter(p->p.getID()==id).findFirst().orElse(null);
        }
        return Helper.getPumpHelper().getWithId(this.pumps, id);
    }


     */
    /*
    @Nullable
    Pump getPumpWithSlot(int slot) {
        for(Pump p: this.pumps){
            if(p.getSlot()==slot){
                return p;
            }
        }
        return null;
    }


     */
    /*
    Ingredient getIngredient(Long id) {
        Log.i(TAG, "getIngredient");
        Ingredient ingredient;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ingredient = this.ingredients.stream().filter(i->i.getID()==id)
                    .findFirst().orElse(null);
        }else {
            Helper<Ingredient> h = Helper.getIngredientHelper();
            ingredient = h.getWithId(this.ingredients, id);
        }
        if(ingredient==null&&AdminRights.isAdmin()){
            try {
                return this.loadIngredient(id);
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }
        return ingredient;
    }

     */

    /*
    List<Ingredient> getIngredients(List<Long> ingredients) {
        Log.i(TAG, "getIngredients");
        if(AdminRights.isAdmin()) {
            List<Ingredient> res = new ArrayList<>();
            for(Ingredient i: this.ingredients){
                if(ingredients.contains(i.getID())){
                    res.add(i);
                }
            }
            return res;
        }else{
            List<Ingredient> res = new ArrayList<>();
            for(Ingredient i: this.ingredients){
                if(i.isAvailable()&&ingredients.contains(i.getID())){
                    res.add(i);
                }
            }
            return res;
        }
    }


     */
    /*
    Recipe getRecipe(Long id) {
        Log.i(TAG, "getRecipe");
        if(AdminRights.isAdmin()){
            for(Recipe r: this.recipes){
                if(id.equals(r.getID())){
                    return r;
                }
            }
        }else{
            for(Recipe r: this.recipes){
                if(id.equals(r.getID())&&r.isAvailable()){
                    return r;
                }
            }
        }
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            recipe = this.recipes.stream().filter(i->i.getID()==id).findFirst().orElse(null);
        }else {
            for (int i=0; i<this.recipes.size(); i++) {
                if(this.recipes.get(i).getID()==id){
                    return this.recipes.get(i);
                }
            }
        }
        if(recipe == null && AdminRights.isAdmin()){
            try {
                return this.loadRecipe(id);
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }

         */
    /*
        return null;
    }
    */
    /*

    List<Recipe> getRecipes(List<Long> recipeIds) {
        Log.i(TAG, "getRecipes");
        if(AdminRights.isAdmin()) {
            List<Recipe> res = new ArrayList<>();
            for(Recipe i: this.recipes){
                if(recipeIds.contains(i.getID())){
                    res.add(i);
                }
            }
            return res;
        }else{
            List<Recipe> res = new ArrayList<>();
            for(Recipe i: this.recipes){
                if(i.isAvailable()&&recipeIds.contains(i.getID())){
                    res.add(i);
                }
            }
            return res;
        }
    }

    List<Recipe> getRecipeWith(String needle) {
        Log.i(TAG, "getRecipeWith");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.recipesWithNeedleInName(this.recipes, needle);
    }

    Recipe getRecipeWithExact(String name) {
        Log.i(TAG, "getRecipeWithExact");
        List<Recipe> recipes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recipes = this.recipes.stream().filter(i-> Objects.equals(i.getName(), name)).collect(Collectors.toList());
        } else {
            recipes = Helper.recipesWithNeedleInName(this.recipes, name);
        }
        if(recipes.isEmpty()){
            return null;
        }
        return recipes.get(0);

    }
    */



    /*
    List<Ingredient> getIngredientWith(String needle) {
        Log.i(TAG, "getIngredientWith");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.ingredientWithNeedleInName(this.ingredients, needle);
    }

    List<Ingredient> getIngredientsWithExact(String name) {
        Log.i(TAG, "getIngredientsWithExact");
        List<Ingredient> res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res =  this.ingredients.stream().filter(i-> {
                        Log.i(TAG, i.getName()+ name+Objects.equals(i.getName(), name));
                        return Objects.equals(i.getName(), name);})
                    .collect(Collectors.toList());
        }else {
            res = Helper.ingredientWitName(this.ingredients, name);
        }
        if(res.isEmpty()){
            Log.i(TAG, "getIngredientsWithExact ingredients is empty");
        }else{
            for(Ingredient i: res){
                Log.i(TAG, "getIngredientsWithExact res "+i.getName());
            }
        }
        return res;
    }

    Ingredient getIngredientWithExact(String name) {
        Log.i(TAG, "getIngredientWithExact");
        List<Ingredient> res = getIngredientsWithExact(name);
        if(res.isEmpty()){
            Log.i(TAG, "getIngredientWithExact ingredients is empty");
            return null;
        }
        return res.get(0);
    }


    List<? extends Ingredient> getAllIngredients() {
        Log.i(TAG, "getAllIngredients");
        return ingredients;
    }


    List<? extends Ingredient> getAvailableIngredients() {
        Log.i(TAG, "getAvailableIngredients");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(Ingredient::isAvailable).collect(Collectors.toList());
        }
        return Helper.getIngredientHelper().getAvailable(this.ingredients);
    }

     */



    /*
    List<? extends Recipe> getRecipes() {
        Log.i(TAG, "getRecipes");
        if (AdminRights.isAdmin()) {
            return this.recipes;
        }
        return this.getAvailableRecipes();
    }

    List<? extends Recipe> getAllRecipes() {
        Log.i(TAG, "getRecipes");
        return this.recipes;
    }


    List<? extends Recipe> getAvailableRecipes() {
        Log.i(TAG, "getAvailableRecipes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(Recipe::isAvailable).collect(Collectors.toList());
        }
        return Helper.getRecipeHelper().getAvailable(this.recipes);
    }

     */




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
        singleton = null;
        super.close();
    }


}
