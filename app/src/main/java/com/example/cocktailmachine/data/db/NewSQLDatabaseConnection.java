package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.IngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.elements.RecipeImageUrlElement;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class NewSQLDatabaseConnection extends SQLiteOpenHelper implements NewDatabaseConnection {
    private static NewSQLDatabaseConnection singleton = null;
    private static String DBname = "DB";
    private static int version = 1;
    private static SQLiteDatabase.CursorFactory factory = null;
    private UserPrivilegeLevel privilege;
    private List<? extends Pump> pumps;
    private List<? extends Ingredient> ingredients;
    private List<? extends Recipe> recipes;
    private List<? extends Topic> topics;

    private static final Tables tables = new Tables();


    private NewSQLDatabaseConnection(@Nullable Context context) {
        super(context, NewSQLDatabaseConnection.DBname,
                NewSQLDatabaseConnection.factory,
                NewSQLDatabaseConnection.version);
        this.privilege = UserPrivilegeLevel.User;
    }

    private NewSQLDatabaseConnection(@Nullable Context context, UserPrivilegeLevel privilege) {
        super(context, NewSQLDatabaseConnection.DBname,
                NewSQLDatabaseConnection.factory,
                NewSQLDatabaseConnection.version);
        this.privilege = privilege;
    }

    static NewDatabaseConnection getSingleton() {
        return NewSQLDatabaseConnection.singleton;
    }

    static void initialize(Context context){
        NewSQLDatabaseConnection.singleton = new NewSQLDatabaseConnection(context);
    }

    static void initialize(Context context, UserPrivilegeLevel privilege){
        NewSQLDatabaseConnection.singleton = new NewSQLDatabaseConnection(context, privilege);
    }


    //NewDatabaseConnection Overrides
    @Override
    public Pump getPump(Long id){
        return this.pumps.stream().filter(p->p.getID()==id).collect(Collectors.toList()).get(0);
    }

    @Override
    public Ingredient getIngredient(Long id) {
        return this.ingredients.stream().filter(i->i.getID()==id).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<Ingredient> getIngredients(List<Long> ingredients) {
        return this.ingredients.stream().filter(i->ingredients.contains(i.getID())).collect(Collectors.toList());
    }

    @Override
    public Recipe getRecipe(Long id) {
        return this.recipes.stream().filter(i->i.getID()==id).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<? extends Recipe> getRecipes(List<Long> recipeIds) {
        return this.recipes.stream().filter(i->recipeIds.contains(i.getID())).collect(Collectors.toList());
    }


    public void addIngredientImageUrl(long ingredientId, String url) {
       // this.getWritableDatabase().
        Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(),ingredientId, url);
    }

    @Override
    public void addOrUpdate(SQLIngredient ingredient) {
        if(ingredient.isSaved() && ingredient.needsUpdate()){
            Tables.TABLE_INGREDIENT.updateElement(this.getWritableDatabase(), ingredient);
        }else{
            Tables.TABLE_INGREDIENT.addElement(this.getWritableDatabase(), ingredient);
        }
    }

    @Override
    public void addOrUpdate(SQLRecipe recipe) {
        if(recipe.isSaved() && recipe.needsUpdate()){
            Tables.TABLE_RECIPE.updateElement(this.getWritableDatabase(), recipe);
        }else{
            Tables.TABLE_RECIPE.addElement(this.getWritableDatabase(), recipe);
        }
    }

    @Override
    public void remove(Ingredient ingredient) {
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), ingredient.getID());
    }

    @Override
    public void remove(Recipe recipe) {
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), recipe.getID());
    }

    @Override
    public void remove(Pump pump) {
        Tables.TABLE_PUMP.deleteElement(this.getWritableDatabase(), pump.getID());
    }


    @Override
    public void loadBufferWithAvailable() {
        this.pumps = this.getPumps();
        this.ingredients = this.loadAvailableIngredients();
        this.recipes = this.loadAvailableRecipes();
    }

    @Override
    public List<? extends Recipe> loadAvailableRecipes() {
        return Tables.TABLE_RECIPE.getAvailable(this.getReadableDatabase());
    }

    @Override
    public List<? extends Ingredient> loadAvailableIngredients() {
       // return IngredientTable.
        return Tables.TABLE_INGREDIENT.getAvailable(this.getReadableDatabase());
    }

    @Override
    public List<Recipe> getRecipeWith(String needle) {
        return this.recipes.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
    }

    @Override
    public List<Ingredient> getIngredientWith(String needle) {
        return this.ingredients.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
    }

    @Override
    public UserPrivilegeLevel getPrivilege() {
        return this.privilege;
    }

    @Override
    public boolean checkavailablilityofallingredients(HashMap<Long, Integer> ingredients) {
        final List<Boolean> availables = new ArrayList<>();
        ingredients.forEach((id, time)->{
            availables.add(this.getIngredient(id).getFluidInMilliliter()>time);
        });
        return availables.stream().reduce(true, (b1,b2)-> b1 && b2);
        //return false;
    }

    @Override
    public List<Pump> getPumps() {
        return null;
    }

    @Override
    public void removeRecipe(long id) {
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), id);
    }

    @Override
    public void removeIngredient(long id) {
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), id);
    }

    @Override
    public void removePump(long id) {
        Tables.TABLE_PUMP.deleteElement(this.getWritableDatabase(), id);
    }

    @Override
    public List<? extends Ingredient> getAvailableIngredients() {
        return this.ingredients.stream().filter(Ingredient::isAvailable).collect(Collectors.toList());
    }

    @Override
    public List<? extends Recipe> getAvailableRecipes() {
        return this.recipes.stream().filter(Recipe::isAvailable).collect(Collectors.toList());
    }

    @Override
    public List<String> getUrls(SQLIngredient newSQLIngredient) {
        return Tables.TABLE_INGREDIENT_URL.getUrls(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    @Override
    public List<String> getUrls(SQLRecipe newSQLRecipe) {
        return Tables.TABLE_RECIPE_URL.getUrls(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    @Override
    public List<Long> getTopics(SQLRecipe newSQLRecipe) {
        return null;
    }

    @Override
    public void addOrUpdate(SQLTopic newSQLTopic) {
        if(newSQLTopic.isSaved() && newSQLTopic.needsUpdate()){
            Tables.TABLE_TOPIC.updateElement(this.getWritableDatabase(), newSQLTopic);
        }else{
            Tables.TABLE_TOPIC.addElement(this.getWritableDatabase(), newSQLTopic);
        }
    }

    @Override
    public void addOrUpdate(SQLPump newSQLPump) {
        if(newSQLPump.isSaved() && newSQLPump.needsUpdate()){
            Tables.TABLE_PUMP.updateElement(this.getWritableDatabase(), newSQLPump);
        }else{
            Tables.TABLE_PUMP.addElement(this.getWritableDatabase(), newSQLPump);
        }
    }

    @Override
    public List<SQLRecipeIngredient> getPumpTimes(SQLRecipe newSQLRecipe) {
        return Tables.TABLE_RECIPE_INGREDIENT.getPumpTime(this.getReadableDatabase(), newSQLRecipe);
    }

    @Override
    public void addOrUpdate(SQLRecipeTopic newSQLRecipeTopic) {
        if(newSQLRecipeTopic.isSaved() && newSQLRecipeTopic.needsUpdate()){
            Tables.TABLE_RECIPE_TOPIC.updateElement(this.getWritableDatabase(), newSQLRecipeTopic);
        }else{
            Tables.TABLE_RECIPE_TOPIC.addElement(this.getWritableDatabase(), newSQLRecipeTopic);
        }
    }

    @Override
    public void remove(SQLRecipeTopic newSQLRecipeTopic) {
        Tables.TABLE_RECIPE_TOPIC.deleteElement(this.getWritableDatabase(), newSQLRecipeTopic);
    }

    @Override
    public void remove(SQLTopic newSQLTopic) {
        Tables.TABLE_TOPIC.deleteElement(this.getWritableDatabase(), newSQLTopic);
    }

    @Override
    public void remove(SQLRecipeIngredient newSQLRecipeIngredient) {
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(this.getWritableDatabase(),newSQLRecipeIngredient);
    }

    @Override
    public void addOrUpdate(SQLRecipeIngredient newSQLRecipeIngredient) {
        if(newSQLRecipeIngredient.isSaved() && newSQLRecipeIngredient.needsUpdate()){
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(this.getWritableDatabase(), newSQLRecipeIngredient);
        }else{
            Tables.TABLE_RECIPE_INGREDIENT.addElement(this.getWritableDatabase(), newSQLRecipeIngredient);
        }
    }

    @Override
    public void addOrUpdate(RecipeImageUrlElement recipeImageUrlElement) {
        if(recipeImageUrlElement.isSaved() && recipeImageUrlElement.needsUpdate()){
            Tables.TABLE_RECIPE_URL.updateElement(this.getWritableDatabase(), recipeImageUrlElement);
        }else{
            Tables.TABLE_RECIPE_URL.addElement(this.getWritableDatabase(), recipeImageUrlElement);
        }
    }

    @Override
    public void remove(RecipeImageUrlElement recipeImageUrlElement) {
        Tables.TABLE_RECIPE_URL.deleteElement(this.getWritableDatabase(),recipeImageUrlElement);
    }

    @Override
    public void addOrUpdate(IngredientImageUrlElement ingredientImageUrlElement) {
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Tables.TABLE_INGREDIENT_URL.updateElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }else{
            Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }
    }

    @Override
    public void remove(IngredientImageUrlElement ingredientImageUrlElement) {
        Tables.TABLE_INGREDIENT_URL.deleteElement(this.getWritableDatabase(),ingredientImageUrlElement);
    }


    // Helper Over rides

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.enableWriteAheadLogging();
        for (String deleteCmd : Tables.getDeletes()) {
            db.execSQL(deleteCmd);
        }
        for (String createCmd : Tables.getCreates()) {
            db.execSQL(createCmd);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.enableWriteAheadLogging();
        if(oldVersion == NewSQLDatabaseConnection.version) {
            for (String deleteCmd : Tables.getDeletes()) {
               db.execSQL(deleteCmd);
            }
            for (String createCmd : Tables.getCreates()) {
                db.execSQL(createCmd);
            }
            NewSQLDatabaseConnection.version = newVersion;
        }
    }
}
