package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.AdminRights;
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
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseConnection extends SQLiteOpenHelper {
    private static DatabaseConnection singleton = null;
    private static final String DBname = "DB";
    private static int version = 1;
    private static final SQLiteDatabase.CursorFactory factory = null;
    //private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
    private List<Pump> pumps = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Recipe> recipes = new ArrayList<>();
    private List<Topic> topics = new ArrayList<>();
    private List<SQLIngredientPump> ingredientPumps = new ArrayList<>();
    private List<SQLRecipeIngredient> recipeIngredients = new ArrayList<>();


    private DatabaseConnection(@Nullable Context context) {
        super(context, DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);
        //this.privilege = UserPrivilegeLevel.User;
    }

    private DatabaseConnection(@Nullable Context context, UserPrivilegeLevel privilege) {
        super(context, DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);
        //this.privilege = privilege;
        AdminRights.setUserPrivilegeLevel(privilege);

    }

    static synchronized DatabaseConnection getSingleton() throws NotInitializedDBException {
        if(!DatabaseConnection.is_initialized()) {
            throw new NotInitializedDBException();
        }
        return DatabaseConnection.singleton;
    }

    public static synchronized DatabaseConnection getDataBase() throws NotInitializedDBException {
        return DatabaseConnection.getSingleton();
    }

    public static synchronized void initialize_singleton(Context context){
        DatabaseConnection.singleton = new DatabaseConnection(context);
        try {
            BasicRecipes.loadMargarita();
            BasicRecipes.loadLongIslandIceTea();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void initialize_singleton(Context context, UserPrivilegeLevel privilege){
        DatabaseConnection.singleton = new DatabaseConnection(context, privilege);
        try {
            BasicRecipes.loadMargarita();
            BasicRecipes.loadLongIslandIceTea();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean is_initialized(){
        return DatabaseConnection.singleton != null;
    }



    //NewDatabaseConnection Overrides


    public void emptyUpPumps() {
        Tables.TABLE_INGREDIENT_PUMP.deleteTable();
        Tables.TABLE_INGREDIENT_PUMP.createTable();
        this.ingredientPumps = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.pumps.forEach(Pump::empty);
        }else {
            Helper.emptyPump(this.pumps);
        }
    }

    public void setUpEmptyPumps() {
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable();
        Tables.TABLE_PUMP.createTable();
        this.pumps = new ArrayList<>();
    }

    //LOAD

    public void loadForSetUp() {
        this.emptyUpPumps();
        this.ingredients = this.loadAllAvailableIngredients();

    }

    private List<Ingredient> loadAllAvailableIngredients() {

        List<? extends Ingredient> res = Tables.TABLE_INGREDIENT.getAllElements(this.getReadableDatabase());
        return (List<Ingredient>) res;
    }

    public void loadBufferWithAvailable() {
        this.pumps = this.loadPumps();
        this.ingredientPumps = this.loadIngredientPumps();
        this.ingredients = this.loadAvailableIngredients();
        this.recipes = this.loadAvailableRecipes();
        this.topics = this.loadTopics();
        this.recipeIngredients = this.loadIngredientVolumes();
    }

    public List<Recipe> loadAvailableRecipes() {
        return (List<Recipe>) Tables.TABLE_RECIPE.getAvailable(this.getReadableDatabase() );
    }

    public List<Ingredient> loadAvailableIngredients() {
       // return IngredientTable.
        List<? extends Ingredient> res =  Tables.TABLE_INGREDIENT.getElements(this.getReadableDatabase(),
                this.getAvailableIngredientIDs());
        return (List<Ingredient>) res;
    }

    private List<SQLIngredientPump> loadIngredientPumps() {
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(this.getReadableDatabase());
    }

    private List<Pump> loadPumps() {
        // return IngredientTable.
        List<? extends Pump> res =   Tables.TABLE_PUMP.getAllElements(this.getReadableDatabase());
        return (List<Pump>) res;
    }

    private List<Topic> loadTopics() {
        //return
        List<? extends Topic> res = Tables.TABLE_TOPIC.getAllElements(this.getReadableDatabase());
        return (List<Topic>) res;
    }

    private List<SQLRecipeIngredient> loadIngredientVolumes(){
        return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);

    }

    public Ingredient loadIngredient(long id) throws AccessDeniedException {
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_INGREDIENT.getElement(this.getReadableDatabase(), id);
    }

    public Recipe loadRecipe(long id) throws AccessDeniedException {
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_RECIPE.getElement(this.getReadableDatabase(), id);
    }

    public Topic loadTopic(long id) throws AccessDeniedException {
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_TOPIC.getElement(this.getReadableDatabase(), id);
    }

    //CHECKER

    public boolean checkAvailabilityOfAllIngredients(HashMap<Long, Integer> ingredientVolume) {
        final List<Boolean> availables = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientVolume.forEach((id, time)->{
                availables.add(this.getIngredient(id).getVolume()>time);
            });
            return availables.stream().reduce(true, (b1,b2)-> b1 && b2);
        }
        return Helper.ingredientAvailable(ingredientVolume);
    }




    //GETTER //Not allowed to fetch from database !!!only!!! from buffer unless they are admins

    private List<Long> getAvailableIngredientIDs(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredientPumps
                    .stream().map(SQLIngredientPump::getIngredientID)
                    .collect(Collectors.toList());
        }
        return Helper.getIngredientIds(this.ingredientPumps);
    }

    public List<SQLIngredientPump> getIngredientPumps() {
        return this.ingredientPumps;
    }

    public List<Pump> getPumps() {
        return this.pumps;
    }

    public Pump getPump(Long id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.pumps.stream().filter(p->p.getID()==id).findFirst().orElse(null);
        }
        Helper<Pump> h = Helper.getPumpHelper();
        return h.getWithId(this.pumps, id);
    }

    public Ingredient getIngredient(Long id) {
        Ingredient ingredient = null;
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

    public List<Ingredient> getIngredients(List<Long> ingredients) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(i->ingredients.contains(i.getID())).collect(Collectors.toList());
        }

        Helper<Ingredient> h = Helper.getIngredientHelper();
        return h.getWithIds(this.ingredients, ingredients);
    }

    public Recipe getRecipe(Long id) {
        Recipe recipe= null;
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
        return recipe;
    }

    public List<Recipe> getRecipes(List<Long> recipeIds) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(i->recipeIds.contains(i.getID())).collect(Collectors.toList());
        }

        Helper<Recipe> h = Helper.getRecipeHelper();
        return h.getWithIds(this.recipes, recipeIds);
    }

    public List<Recipe> getRecipeWith(String needle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.recipesWithNeedleInName(this.recipes, needle);
    }

    public Recipe getRecipeWithExact(String name) {
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

    public List<Ingredient> getIngredientWith(String needle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.ingredientWithNeedleInName(this.ingredients, needle);
    }

    public Ingredient getIngredientWithExact(String name) {
        List<Ingredient> ingredients;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredients =  this.ingredients.stream().filter(i->i.getName()==name).collect(Collectors.toList());
        }else {
            ingredients = Helper.ingredientWitName(this.ingredients, name);
        }
        if(ingredients.isEmpty()){
            return null;
        }
        return ingredients.get(0);
    }



    public List<? extends Ingredient> getAvailableIngredients() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(Ingredient::isAvailable).collect(Collectors.toList());
        }
        return Helper.getIngredientHelper().getAvailable(this.ingredients);
    }

    public List<? extends Recipe> getRecipes() {
        if (AdminRights.isAdmin()) {
            return this.recipes;
        }
        return this.getAvailableRecipes();
    }


    public List<? extends Recipe> getAvailableRecipes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(Recipe::isAvailable).collect(Collectors.toList());
        }
        return Helper.getRecipeHelper().getAvailable(this.recipes);
    }

    public List<String> getUrls(SQLIngredient newSQLIngredient) {
        return Tables.TABLE_INGREDIENT_URL.getUrls(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    public List<SQLIngredientImageUrlElement> getUrlElements(SQLIngredient newSQLIngredient)  {
        return Tables.TABLE_INGREDIENT_URL.getElements(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    public List<String> getUrls(SQLRecipe newSQLRecipe) {
        return Tables.TABLE_RECIPE_URL.getUrls(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    public List<SQLRecipeImageUrlElement> getUrlElements(SQLRecipe newSQLRecipe) {
        return Tables.TABLE_RECIPE_URL.getElements(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    private List<Topic> getTopics(List<Long> t_ids) {
        //return Tables.TABLE_TOPIC.getElements(this.getReadableDatabase(), t_ids);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.topics.stream().filter(t -> t_ids.contains(t.getID())).collect(Collectors.toList());
        }
        List<Topic> topics = new ArrayList<>();
        for(int i=0; i<this.topics.size(); i++){
            if(t_ids.contains(this.topics.get(i).getID()) ){
                topics.add(this.topics.get(i));
            }

        }
        return topics;
    }

    public List<Topic> getTopics(SQLRecipe newSQLRecipe) {
        return this.getTopics(newSQLRecipe.getTopics());
    }

    public Topic getTopic(long id) {
        Topic topic = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            topic = this.topics.stream().filter(t-> t.getID()==id).findFirst().orElse(null);
        }else{
            topic = Helper.gettopichelper().getWithId(this.topics, id);
        }
        if(AdminRights.isAdmin() && topic==null){
            try {
                return this.loadTopic(id);
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }
        return topic;
    }

    public List<Topic> getTopics(Recipe recipe) {
        return this.getTopics(recipe.getTopics());
    }

    public List<Topic> getTopics() {
        return (List<Topic>) this.topics;
    }

    public List<Long> getTopicIDs(SQLRecipe newSQLRecipe) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.getTopics(newSQLRecipe).stream().map(Topic::getID).collect(Collectors.toList());
        }
        return Helper.gettopichelper().getIds(this.getTopics(newSQLRecipe));
    }

    public List<SQLRecipeIngredient> getIngredientVolumes(SQLRecipe newSQLRecipe) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipeIngredients
                    .stream()
                    .filter(r->r.getRecipeID()==newSQLRecipe.getID())
                    .collect(Collectors.toList());
        }
        return Helper.getWithRecipeID(this.recipeIngredients, newSQLRecipe.getID());
    }


    //ADD OR UPDATE
    public void addIngredientImageUrl(long ingredientId, String url) {
        // this.getWritableDatabase().
        Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(),ingredientId, url);
    }

    public void addOrUpdate(SQLIngredient ingredient) {
        if(ingredient.isSaved() && ingredient.needsUpdate()){
            Tables.TABLE_INGREDIENT.updateElement(this.getWritableDatabase(), ingredient);
            this.ingredients.remove(ingredient);
        }else{
            Tables.TABLE_INGREDIENT.addElement(this.getWritableDatabase(), ingredient);
        }
        this.ingredients.add(ingredient);

    }

    public void addOrUpdate(SQLRecipe recipe) {
        if(recipe.isSaved() && recipe.needsUpdate()){
            Tables.TABLE_RECIPE.updateElement(this.getWritableDatabase(), recipe);
            this.recipes.remove(recipe);
        }else{
            Tables.TABLE_RECIPE.addElement(this.getWritableDatabase(), recipe);
        }
        this.recipes.add(recipe);
    }

    public void addOrUpdate(SQLTopic topic) {
        if(topic.isSaved() && topic.needsUpdate()){
            Tables.TABLE_TOPIC.updateElement(this.getWritableDatabase(), topic);
            this.topics.remove(topic);
        }else{
            Tables.TABLE_TOPIC.addElement(this.getWritableDatabase(), topic);
        }
        this.topics.add(topic);
    }

    public void addOrUpdate(SQLPump pump) {
        if(pump.isSaved() && pump.needsUpdate()){
            Tables.TABLE_PUMP.updateElement(this.getWritableDatabase(), pump);
            this.pumps.remove(pump);
        }else{
            Tables.TABLE_PUMP.addElement(this.getWritableDatabase(), pump);
        }
        this.pumps.add(pump);
    }

    public void addOrUpdate(SQLRecipeTopic recipeTopic) {
        if(recipeTopic.isSaved() && recipeTopic.needsUpdate()){
            Tables.TABLE_RECIPE_TOPIC.updateElement(this.getWritableDatabase(), recipeTopic);
        }else{
            Tables.TABLE_RECIPE_TOPIC.addElement(this.getWritableDatabase(), recipeTopic);
        }
    }

    public void addOrUpdate(SQLIngredientPump ingredientPump) {
        if(ingredientPump.isSaved() && ingredientPump.needsUpdate()){
            Tables.TABLE_INGREDIENT_PUMP.updateElement(this.getWritableDatabase(), ingredientPump);
            this.ingredientPumps.remove(ingredientPump);
        }else{
            Tables.TABLE_INGREDIENT_PUMP.addElement(this.getWritableDatabase(), ingredientPump);
        }
        this.ingredientPumps.add(ingredientPump);
    }

    public void addOrUpdate(SQLRecipeIngredient recipeIngredient) {
        if(recipeIngredient.isSaved() && recipeIngredient.needsUpdate()){
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(this.getWritableDatabase(), recipeIngredient);
            this.recipeIngredients.remove(recipeIngredient);
        }else{
            Tables.TABLE_RECIPE_INGREDIENT.addElement(this.getWritableDatabase(), recipeIngredient);
        }
        this.recipeIngredients.add(recipeIngredient);
    }

    public void addOrUpdate(SQLRecipeImageUrlElement recipeImageUrlElement) {
        if(recipeImageUrlElement.isSaved() && recipeImageUrlElement.needsUpdate()){
            Tables.TABLE_RECIPE_URL.updateElement(this.getWritableDatabase(), recipeImageUrlElement);
        }else{
            Tables.TABLE_RECIPE_URL.addElement(this.getWritableDatabase(), recipeImageUrlElement);
        }
    }

    public void addOrUpdate(SQLIngredientImageUrlElement ingredientImageUrlElement) {
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Tables.TABLE_INGREDIENT_URL.updateElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }else{
            Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }
    }



    //REMOVE in DB and buffer
    public void removeRecipe(long id) {
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), id);
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.recipes.removeIf(i->i.getID()==id);
        }else {
            this.recipes = Helper.getRecipeHelper().removeIf(this.recipes, id);
        }
    }

    public void removeIngredient(long id) {
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

    public void removePump(long id) {
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
        }
    }


    public void remove(Ingredient ingredient) {
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), ingredient.getID());
        this.ingredients.remove(ingredient);
    }

    public void remove(Recipe recipe) {
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), recipe.getID());
        this.recipes.remove(recipe);
    }

    public void remove(Pump pump) {
        Tables.TABLE_PUMP.deleteElement(this.getWritableDatabase(), pump.getID());
        Tables.TABLE_INGREDIENT_PUMP.deletePump(this.getWritableDatabase(), pump.getID());
        this.pumps.remove(pump);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.ingredientPumps.removeIf(ip->ip.getPumpID()==pump.getID());
        }else {
            this.ingredientPumps = Helper.removeIfPumpID(this.ingredientPumps, pump.getID());
        }

    }


    public void remove(SQLRecipeTopic newSQLRecipeTopic) {
        Tables.TABLE_RECIPE_TOPIC.deleteElement(this.getWritableDatabase(), newSQLRecipeTopic);
    }

    public void remove(SQLTopic topic) {
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

    public void remove(SQLRecipeIngredient recipeIngredient) {
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(this.getWritableDatabase(),recipeIngredient);
        this.recipeIngredients.remove(recipeIngredient);
    }

    public void remove(SQLIngredientPump ingredientPump) {
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(this.getWritableDatabase(), ingredientPump);
        this.ingredientPumps.remove(ingredientPump);
    }

    public void remove(SQLRecipeImageUrlElement recipeImageUrlElement) {
        Tables.TABLE_RECIPE_URL.deleteElement(this.getWritableDatabase(),recipeImageUrlElement);
    }

    public void remove(SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Tables.TABLE_INGREDIENT_URL.deleteElement(this.getWritableDatabase(),ingredientImageUrlElement);
    }



    // Helper Over rides

    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        db.enableWriteAheadLogging();
    }

    public void onCreate(SQLiteDatabase db) {
        for (String createCmd : Tables.getCreates()) {
            db.execSQL(createCmd);
        }


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.enableWriteAheadLogging();
        if(oldVersion == DatabaseConnection.version) {
            for (String deleteCmd : Tables.getDeletes()) {
               db.execSQL(deleteCmd);
            }
            onCreate(db);
            DatabaseConnection.version = newVersion;
        }
    }
}
