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
            if(Dummy.isDummy) {
                //DatabaseConnection.singleton.emptyAll();
                BasicRecipes.loadTopics();
                BasicRecipes.loadIngredients();
                if(!Dummy.withSetCalibration) {
                    BasicRecipes.loadPumps();
                }
                BasicRecipes.loadMargarita();
                BasicRecipes.loadLongIslandIceTea();
            }
            DatabaseConnection.singleton.checkAllAvailability();
            DatabaseConnection.singleton.print();
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
            if(Dummy.isDummy) {
                //DatabaseConnection.singleton.emptyAll();
                BasicRecipes.loadTopics();
                BasicRecipes.loadIngredients();
                if(!Dummy.withSetCalibration) {
                    BasicRecipes.loadPumps();
                }
                BasicRecipes.loadMargarita();
                BasicRecipes.loadLongIslandIceTea();
            }
            DatabaseConnection.singleton.checkAllAvailability();
            DatabaseConnection.singleton.print();
            Log.i(TAG, "initialize_singleton: finished loading");
        }  catch (NotInitializedDBException|MissingIngredientPumpException e) {
            Log.i(TAG, "initialize_singleton: Exception");
            Log.e(TAG, e.toString());
            e.printStackTrace();
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


    //Refresher
    //Local
    static synchronized void localRefresh() {
        Log.i(TAG, "localRefresh");
        try {
            getDataBase().loadBufferWithAvailable();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }








    //NewDatabaseConnection Overrides

    /**
     * deletes all Tables and creates all newly empty
     * @author Johanna Reidt
     */
    private void emptyAll(){
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
        this.ingredientPumps = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.pumps.forEach(Pump::empty);
            this.ingredients.forEach(Ingredient::empty);
        }else {
            Helper.emptyPump(this.pumps);
            Helper.emptyIngredient(this.ingredients);
        }
        try {
            this.checkAllAvailability();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }
    }

    void setUpEmptyPumps() {
        Log.i(TAG, "setUpEmptyPumps");
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_PUMP.createTable(this.getWritableDatabase());
        this.pumps = new ArrayList<>();
        try {
            this.checkAllAvailability();
        } catch (NotInitializedDBException e) {
            throw new RuntimeException(e);
        }
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

    void loadBufferWithAvailable() {
        Log.i(TAG, "loadBufferWithAvailable");
        resetAll();
        this.ingredients = this.loadAllIngredients();
        Log.i(TAG, "loadBufferWithAvailable: all Ingredients: "+this.ingredients.toString());
        this.pumps = this.loadPumps();
        Log.i(TAG, "loadBufferWithAvailable: all Pumps: "+this.pumps.toString());
        this.topics = this.loadTopics();
        Log.i(TAG, "loadBufferWithAvailable: Topics: "+this.topics.toString());
        this.ingredientPumps = this.loadIngredientPumps();
        Log.i(TAG, "loadBufferWithAvailable: all IngredientPumps: "+this.ingredientPumps.toString());
        this.recipeIngredients = this.loadIngredientVolumes();
        Log.i(TAG, "loadBufferWithAvailable: all recipeIngredients: "+this.recipeIngredients.toString());
        this.recipes = this.loadAllRecipes();
        Log.i(TAG, "loadBufferWithAvailable: all Recipes: "+this.recipes.toString());
        //this.loadAvailabilityForIngredients();
        //Log.i(TAG, "loadBufferWithAvailable: no admin Ingredients: "+this.ingredients.toString());
        //this.loadAvailabilityForRecipes();
        //Log.i(TAG, "loadBufferWithAvailable: no admin AvailableRecipes: "+this.recipes.toString());

        try {
            this.checkAllAvailability();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "loadBufferWithAvailable: checkAllAvailability: saving of one instance failed");
        }
        Log.i(TAG, "loadBufferWithAvailable: finished now print");
        print();
    }

    private void print(){
        Log.i(TAG, "print");
        Log.i(TAG, "print ingredients: "+this.ingredients);
        Log.i(TAG, "print pumps: "+this.pumps);
        Log.i(TAG, "print ingredientPumps: "+this.ingredientPumps);
        Log.i(TAG, "print topics: "+this.topics);
        Log.i(TAG, "print recipeIngredients: "+this.recipeIngredients);
        Log.i(TAG, "print recipes: "+this.recipes);
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
    private void checkAllAvailability() throws NotInitializedDBException {
        //TO DO: checkAllAvailability
        Log.i(TAG, "checkAllAvailability");
        Log.i(TAG, "checkAllAvailability: pumps");
        for(Pump p: this.pumps){
            p.loadAvailable();//loads ingredient pump connection if exists
            p.save();
        }
        Log.i(TAG, "checkAllAvailability: pumps: "+this.pumps);
        Log.i(TAG, "checkAllAvailability: ingredients");
        for(Ingredient i: this.ingredients){
            i.loadAvailable();//loads ingredient pump connection if exists
            i.save();
        }
        Log.i(TAG, "checkAllAvailability: ingredients: "+this.ingredients);
        Log.i(TAG, "checkAllAvailability: recipes");
        for(Recipe r: this.recipes){
            r.loadAvailable();
            r.save();
        }
        Log.i(TAG, "checkAllAvailability: recipes: "+this.recipes);
    }









    //GETTER //Not allowed to fetch from database !!!only!!! from buffer unless they are admins

    private List<Long> getAvailableIngredientIDs(){
        Log.i(TAG, "getAvailableIngredientIDs");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredientPumps
                    .stream().map(SQLIngredientPump::getIngredientID)
                    .collect(Collectors.toList());
        }
        return Helper.getIngredientIds(this.ingredientPumps);
    }

    List<SQLIngredientPump> getIngredientPumps() {
        Log.i(TAG, "getIngredientPumps");
        return this.ingredientPumps;
    }

    List<Pump> getPumps() {
        Log.i(TAG, "getPumps");
        return this.pumps;
    }

    Pump getPump(Long id){
        Log.i(TAG, "getPump");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.pumps.stream().filter(p->p.getID()==id).findFirst().orElse(null);
        }
        return Helper.getPumpHelper().getWithId(this.pumps, id);
    }

    @Nullable
    Pump getPumpWithSlot(int slot) {
        for(Pump p: this.pumps){
            if(p.getSlot()==slot){
                return p;
            }
        }
        return null;
    }

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
        return null;
    }

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


















    //ADD OR UPDATE
    void addIngredientImageUrl(long ingredientId, String url) {
        Log.i(TAG, "addIngredientImageUrl");
        // this.getWritableDatabase().
        Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(),ingredientId, url);
    }

    void addOrUpdate(SQLIngredient ingredient) {
        Log.i(TAG, "addOrUpdate: "+ingredient.toString());
        if(ingredient.isSaved() && ingredient.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT.updateElement(this.getWritableDatabase(), ingredient);
        }else if(ingredient.isSaved() && !ingredient.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredient.setID(Tables.TABLE_INGREDIENT.addElement(this.getWritableDatabase(), ingredient));
            this.ingredients.add(ingredient);
        }

    }

    void addOrUpdate(SQLRecipe recipe) {
        Log.i(TAG, "addOrUpdate: "+recipe.toString());
        if(recipe.isSaved() && recipe.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE.updateElement(this.getWritableDatabase(), recipe);
        }else if(recipe.isSaved() && !recipe.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipe.setID(Tables.TABLE_RECIPE.addElement(this.getWritableDatabase(), recipe));
            this.recipes.add(recipe);
        }
    }

    void addOrUpdate(SQLTopic topic) {
        Log.i(TAG, "addOrUpdate: "+topic.toString());
        if(topic.isSaved() && topic.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_TOPIC.updateElement(this.getWritableDatabase(), topic);
        }else if(topic.isSaved() && !topic.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            topic.setID(Tables.TABLE_TOPIC.addElement(this.getWritableDatabase(), topic));
            this.topics.add(topic);
        }
    }

    void addOrUpdate(SQLPump pump) {
        Log.i(TAG, "addOrUpdate: "+pump.toString());
        if(pump.isSaved() && pump.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_PUMP.updateElement(this.getWritableDatabase(), pump);
        }else if(pump.isSaved() && !pump.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            pump.setID(Tables.TABLE_PUMP.addElement(this.getWritableDatabase(), pump));
            this.pumps.add(pump);
        }
    }

    void addOrUpdate(SQLRecipeTopic recipeTopic) {
        Log.i(TAG, "addOrUpdate: "+recipeTopic.toString());
        if(recipeTopic.isSaved() && recipeTopic.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_TOPIC.updateElement(this.getWritableDatabase(), recipeTopic);
        }else if(recipeTopic.isSaved() && !recipeTopic.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeTopic.setID(Tables.TABLE_RECIPE_TOPIC.addElement(this.getWritableDatabase(), recipeTopic));
        }
    }

    void addOrUpdate(SQLIngredientPump ingredientPump) {
        Log.i(TAG, "addOrUpdate: "+ingredientPump);
        if(ingredientPump.isSaved() && ingredientPump.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_PUMP.updateElement(this.getWritableDatabase(), ingredientPump);
            //this.ingredientPumps.remove(ingredientPump);
        }else if(ingredientPump.isSaved() && !ingredientPump.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredientPump.setID(Tables.TABLE_INGREDIENT_PUMP.addElement(this.getWritableDatabase(), ingredientPump));
            this.ingredientPumps.add(ingredientPump);
        }
    }

    void addOrUpdate(SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "addOrUpdate: "+recipeIngredient.toString());
        if(recipeIngredient.isSaved() && recipeIngredient.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(this.getWritableDatabase(), recipeIngredient);
            //this.recipeIngredients.remove(recipeIngredient);
        }else if(recipeIngredient.isSaved() && !recipeIngredient.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeIngredient.setID(Tables.TABLE_RECIPE_INGREDIENT.addElement(this.getWritableDatabase(), recipeIngredient));
            this.recipeIngredients.add(recipeIngredient);
        }
    }

    void addOrUpdate(SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "addOrUpdate: "+recipeImageUrlElement.toString());
        if(recipeImageUrlElement.isSaved() && recipeImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_RECIPE_URL.updateElement(this.getWritableDatabase(), recipeImageUrlElement);
        }else if(recipeImageUrlElement.isSaved() && !recipeImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            recipeImageUrlElement.setID(Tables.TABLE_RECIPE_URL.addElement(this.getWritableDatabase(), recipeImageUrlElement));
        }
    }

    void addOrUpdate(SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "addOrUpdate: "+ingredientImageUrlElement);
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs update");
            Tables.TABLE_INGREDIENT_URL.updateElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }else if(ingredientImageUrlElement.isSaved() && !ingredientImageUrlElement.needsUpdate()){
            Log.i(TAG, "was saved and needs no update");
        }else{
            Log.i(TAG, "first time saving");
            ingredientImageUrlElement.setID(
                    Tables.TABLE_INGREDIENT_URL.addElement(
                            this.getWritableDatabase(),
                            ingredientImageUrlElement));
        }
    }



    //REMOVE in DB and buffer
    void removeRecipe(long id) {
        Log.i(TAG, "removeRecipe");
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), id);
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.recipes.removeIf(i->i.getID()==id);
        }else {
            this.recipes = Helper.getRecipeHelper().removeIf(this.recipes, id);
        }
    }

    void removeIngredient(long id) {
        Log.i(TAG, "removeIngredient");
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.ingredients.removeIf(i->i.getID()==id);
            this.ingredientPumps.removeIf(ip->ip.getIngredientID()== id);
        }else {
            this.ingredients = Helper.getIngredientHelper().removeIfAndDelete(this.ingredients, id);
            //this.ingredientPumps =
            Helper.removeIfIngredientID(this.ingredientPumps, id);

        }
        Tables.TABLE_INGREDIENT_URL.deleteWithOwnerId(this.getWritableDatabase(), id);
    }

    void removePump(long id) {
        Log.i(TAG, "removePump");
        Tables.TABLE_PUMP.deleteElement(this.getWritableDatabase(), id);
        Tables.TABLE_INGREDIENT_PUMP.deletePump(this.getWritableDatabase(), id);
        // .deletePump(this.getWritableDatabase(), pump.getID());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.pumps.removeIf(i->i.getID()== id);
            this.ingredientPumps.removeIf(ip->ip.getPumpID()== id);
        }else{
            this.pumps = Helper.getPumpHelper().removeIf(this.pumps, id);
            //this.ingredientPumps =
            Helper.removeIfPumpID(this.ingredientPumps, id);
            /*
            List<SQLIngredientPump> toBeDeleted = new ArrayList<>();
            for(SQLIngredientPump ip: this.ingredientPumps){
                if(ip.getPumpID() == id) {
                    toBeDeleted.add(ip);
                }
            }
            for(SQLIngredientPump ip: toBeDeleted){
                try {
                    ip.delete();
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }
            this.ingredientPumps.removeAll(toBeDeleted);
            //return elements;

             */
        }
    }


    void remove(Ingredient ingredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), ingredient.getID());
        this.ingredients.remove(ingredient);
    }

    void remove(Recipe recipe) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), recipe.getID());
        this.recipes.remove(recipe);
    }

    void remove(Pump pump) {
        Log.i(TAG, "remove");

        Tables.TABLE_PUMP.deleteElement(this.getWritableDatabase(), pump.getID());
        Tables.TABLE_INGREDIENT_PUMP.deletePump(this.getWritableDatabase(), pump.getID());
        this.pumps.remove(pump);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.ingredientPumps.removeIf(ip->ip.getPumpID()==pump.getID());
        }else {
            this.ingredientPumps = Helper.removeIfPumpID(this.ingredientPumps, pump.getID());
        }

    }


    void remove(SQLRecipeTopic newSQLRecipeTopic) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_TOPIC.deleteElement(this.getWritableDatabase(), newSQLRecipeTopic);
    }

    void remove(SQLTopic topic) {
        Log.i(TAG, "remove");
        Tables.TABLE_TOPIC.deleteElement(this.getWritableDatabase(), topic);
        this.topics.remove(topic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.recipes.forEach(r -> r.removeTopic(topic.getID()));
        }else {
            for (Recipe r : this.recipes) {
                r.remove(topic);
            }
        }
    }

    void remove(SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(this.getWritableDatabase(),recipeIngredient);
        this.recipeIngredients.remove(recipeIngredient);
    }

    void remove(SQLIngredientPump ingredientPump) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(this.getWritableDatabase(), ingredientPump);
        this.ingredientPumps.remove(ingredientPump);
    }

    void remove(SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_URL.deleteElement(this.getWritableDatabase(),recipeImageUrlElement);
    }

    void remove(SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_URL.deleteElement(this.getWritableDatabase(),ingredientImageUrlElement);
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
