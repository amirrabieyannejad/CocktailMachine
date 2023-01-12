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
import com.example.cocktailmachine.data.db.elements.RecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLTopic;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LoaderDB  extends SQLiteOpenHelper implements NewDatabaseConnection {
    private static String DBname = "DB";
    private static int version = 1;
    private static SQLiteDatabase.CursorFactory factory = null;
    private UserPrivilegeLevel privilege;
    private List<? extends Pump> pumps;
    private List<? extends Ingredient> ingredients;
    private List<? extends Recipe> recipes;
    private List<? extends Topic> topics;
    private List<SQLIngredientPump> ingredientPumps;

    private static final Tables tables = new Tables();


    //NewDatabaseConnection Overrides


    @Override
    public void emptyUpPumps() {
        Tables.TABLE_INGREDIENT_PUMP.deleteTable();
        Tables.TABLE_INGREDIENT_PUMP.createTable();
        this.ingredientPumps = new ArrayList<>();
        this.pumps.forEach(Pump::empty);
    }

    @Override
    public void setUpPumps() {
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable();
        Tables.TABLE_PUMP.createTable();
        this.pumps = new ArrayList<>();
    }

    //LOAD
    @Override
    public void loadBufferWithAvailable() {
        this.pumps = this.getPumps();
        this.ingredientPumps = this.loadIngredientPumps();
        this.ingredients = this.loadAvailableIngredients();
        this.recipes = this.loadAvailableRecipes();
        this.topics = this.loadTopics();
    }

    @Override
    public void loadEmpty() {
        this.setUpPumps();
    }

    @Override
    public List<? extends Recipe> loadAvailableRecipes() {
        return Tables.TABLE_RECIPE.getAvailable(this.getReadableDatabase() );
    }

    @Override
    public List<? extends Ingredient> loadAvailableIngredients() {
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT.getElements(this.getReadableDatabase(),
                this.getAvailableIngredientIDs());
    }

    private List<SQLIngredientPump> loadIngredientPumps() {
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(this.getReadableDatabase());
    }

    private List<SQLPump> loadPumps() {
        // return IngredientTable.
        return Tables.TABLE_PUMP.getAllElements(this.getReadableDatabase());
    }

    private List<? extends Topic> loadTopics() {
        return Tables.TABLE_TOPIC.getAllElements(this.getReadableDatabase());
    }

    //CHECKER

    @Override
    public boolean checkavailablilityofallingredients(HashMap<Long, Integer> ingredients) {
        final List<Boolean> availables = new ArrayList<>();
        ingredients.forEach((id, time)->{
            availables.add(this.getIngredient(id).getFillLevel()>time);
        });
        return availables.stream().reduce(true, (b1,b2)-> b1 && b2);
        //return false;
    }




    //GETTER

    private List<Long> getAvailableIngredientIDs(){
        return this.ingredientPumps
                .stream().map(SQLIngredientPump::getIngredientID)
                .collect(Collectors.toList());
    }

    @Override
    public List<SQLIngredientPump> getIngredientPumps() {
        return this.ingredientPumps;
    }


    @Override
    public List<? extends Pump> getPumps() {
        return this.pumps;
    }

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

    private List<? extends Topic> getTopics(List<Long> t_ids) {
        return Tables.TABLE_TOPIC.getElements(this.getReadableDatabase(), t_ids);
    }

    @Override
    public List<Topic> getTopics(SQLRecipe newSQLRecipe) {
        return (List<Topic>) this.getTopics(newSQLRecipe.getTopics());
    }

    @Override
    public Topic getTopic(long id) {
        return null;
    }

    @Override
    public List<Topic> getTopics(Recipe recipe) {
        return (List<Topic>) this.getTopics(recipe.getTopics());
    }

    @Override
    public List<Topic> getTopics() {
        return null;
    }


    @Override
    public List<SQLRecipeIngredient> getPumpTimes(SQLRecipe newSQLRecipe) {
        return Tables.TABLE_RECIPE_INGREDIENT.getPumpTime(this.getReadableDatabase(), newSQLRecipe);
    }




    //ADD OR UPDATE


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
    public void addOrUpdate(SQLRecipeTopic newSQLRecipeTopic) {
        if(newSQLRecipeTopic.isSaved() && newSQLRecipeTopic.needsUpdate()){
            Tables.TABLE_RECIPE_TOPIC.updateElement(this.getWritableDatabase(), newSQLRecipeTopic);
        }else{
            Tables.TABLE_RECIPE_TOPIC.addElement(this.getWritableDatabase(), newSQLRecipeTopic);
        }
    }

    @Override
    public void addOrUpdate(SQLIngredientPump newSQLIngredientPump) {
        if(newSQLIngredientPump.isSaved() && newSQLIngredientPump.needsUpdate()){
            Tables.TABLE_INGREDIENT_PUMP.updateElement(this.getWritableDatabase(), newSQLIngredientPump);
        }else{
            Tables.TABLE_INGREDIENT_PUMP.addElement(this.getWritableDatabase(), newSQLIngredientPump);
        }
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
    public void addOrUpdate(IngredientImageUrlElement ingredientImageUrlElement) {
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Tables.TABLE_INGREDIENT_URL.updateElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }else{
            Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }
    }



    //REMOVE

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

        Tables.TABLE_INGREDIENT_PUMP.deletePump(this.getWritableDatabase(), pump.getID());
        // .deletePump(this.getWritableDatabase(), pump.getID());
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
    public void remove(SQLIngredientPump sqlIngredientPump) {
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(this.getWritableDatabase(), sqlIngredientPump);
    }

    @Override
    public void remove(RecipeImageUrlElement recipeImageUrlElement) {
        Tables.TABLE_RECIPE_URL.deleteElement(this.getWritableDatabase(),recipeImageUrlElement);
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
