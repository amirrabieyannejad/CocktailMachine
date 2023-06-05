package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

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
    private static final String TAG = "DatabaseConnection";
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
        if(!DatabaseConnection.is_initialized()) {
            throw new NotInitializedDBException();
        }
        return DatabaseConnection.singleton;
    }

    public static synchronized DatabaseConnection getDataBase() throws NotInitializedDBException {
        Log.i(TAG, "getDataBase");
        return DatabaseConnection.getSingleton();
    }

    public static synchronized void initialize_singleton(Context context){
        Log.i(TAG, "initialize_singleton");
        DatabaseConnection.singleton = new DatabaseConnection(context);
        try {
            Log.i(TAG, "initialize_singleton: start loading");
            DatabaseConnection.singleton.emptyAll();
            BasicRecipes.loadTopics();
            BasicRecipes.loadIngredients();
            BasicRecipes.loadPumps();
            BasicRecipes.loadMargarita();
            BasicRecipes.loadLongIslandIceTea();
            Log.i(TAG, "initialize_singleton: finished loading");
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "initialize_singleton: NotInitializedDBException");
        }
    }

    public static synchronized void initialize_singleton(Context context, UserPrivilegeLevel privilege){
        Log.i(TAG, "initialize_singleton");
        DatabaseConnection.singleton = new DatabaseConnection(context, privilege);
        try {
            Log.i(TAG, "initialize_singleton: start loading");
            DatabaseConnection.singleton.emptyAll();
            BasicRecipes.loadTopics();
            BasicRecipes.loadIngredients();
            BasicRecipes.loadPumps();
            BasicRecipes.loadMargarita();
            BasicRecipes.loadLongIslandIceTea();
            Log.i(TAG, "initialize_singleton: finished loading");
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            Log.i(TAG, "initialize_singleton: NotInitializedDBException");
        }
    }

    public static synchronized boolean is_initialized(){
        Log.i(TAG, "is_initialized");
        return DatabaseConnection.singleton != null;
    }


    //Refresher
    //Local
    public static synchronized void localRefresh() throws NotInitializedDBException {
        Log.i(TAG, "localRefresh");
        getDataBase().loadBufferWithAvailable();
    }







    //NewDatabaseConnection Overrides

    private void emptyAll(){
        Log.i(TAG, "emptyAll");
        resetAll();
        Tables.deleteAll(this.getWritableDatabase());
        Tables.createAll(this.getWritableDatabase());
    }
    private void resetAll(){
        Log.i(TAG, "resetAll");
        this.ingredients = new ArrayList<>();
        this.ingredientPumps = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.pumps = new ArrayList<>();
        this.recipeIngredients = new ArrayList<>();
        this.recipes = new ArrayList<>();
    }

    public void emptyUpPumps() {
        Log.i(TAG, "emptyUpPumps");
        Tables.TABLE_INGREDIENT_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_INGREDIENT_PUMP.createTable(this.getWritableDatabase());
        this.ingredientPumps = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.pumps.forEach(Pump::empty);
        }else {
            Helper.emptyPump(this.pumps);
        }
    }

    public void setUpEmptyPumps() {
        Log.i(TAG, "setUpEmptyPumps");
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_PUMP.createTable(this.getWritableDatabase());
        this.pumps = new ArrayList<>();
    }

    //LOAD

    public void loadForSetUp() {
        Log.i(TAG, "loadForSetUp");
        this.emptyUpPumps();
        this.ingredients = this.loadAllIngredients();

    }

    private List<Ingredient> loadAllIngredients() {
        Log.i(TAG, "loadAllAvailableIngredients");

        List<? extends Ingredient> res = Tables.TABLE_INGREDIENT.getAllElements(this.getReadableDatabase());
        return (List<Ingredient>) res;
    }

    public void loadBufferWithAvailable() {
        resetAll();
        Log.i(TAG, "loadBufferWithAvailable");
        this.ingredients = this.loadAllIngredients();
        Log.i(TAG, "loadBufferWithAvailable Ingredients: "+this.ingredients.toString());
        this.pumps = this.loadPumps();
        Log.i(TAG, "loadBufferWithAvailable Pumps: "+this.pumps.toString());
        this.ingredientPumps = this.loadIngredientPumps();
        Log.i(TAG, "loadBufferWithAvailable IngredientPumps: "+this.ingredientPumps.toString());
        if(!AdminRights.isAdmin()) {
            this.ingredients = this.loadAvailableIngredients();
            Log.i(TAG, "loadBufferWithAvailable no admin Ingredients: "+this.ingredients.toString());
            this.recipes = this.loadAvailableRecipes();
            Log.i(TAG, "loadBufferWithAvailable AvailableRecipes: "+this.recipes.toString());
        }else{
            this.recipes = this.loadAllRecipes();
            Log.i(TAG, "loadBufferWithAvailable AllRecipes: "+this.recipes.toString());
        }
        this.topics = this.loadTopics();
        Log.i(TAG, "loadBufferWithAvailable Topics: "+this.topics.toString());
        this.recipeIngredients = this.loadIngredientVolumes();
        Log.i(TAG, "loadBufferWithAvailable recipeIngredients: "+this.recipeIngredients.toString());
        Log.i(TAG, "loadBufferWithAvailable finished");
    }

    public List<Recipe> loadAvailableRecipes() {
        Log.i(TAG, "loadAvailableRecipes");
        return (List<Recipe>) Tables.TABLE_RECIPE.getAvailable(this.getReadableDatabase() );
    }

    public List<Recipe> loadAllRecipes() {
        Log.i(TAG, "loadAvailableRecipes");
        List<? extends Recipe> res =  Tables.TABLE_RECIPE.getAllElements(this.getReadableDatabase());
        return (List<Recipe>) res;
    }

    public List<Ingredient> loadAvailableIngredients() {
        Log.i(TAG, "loadAvailableIngredients");
       // return IngredientTable.
        List<? extends Ingredient> res =  Tables.TABLE_INGREDIENT.getElements(this.getReadableDatabase(),
                this.getAvailableIngredientIDs());
        return (List<Ingredient>) res;
    }

    private List<SQLIngredientPump> loadIngredientPumps() {
        Log.i(TAG, "loadIngredientPumps");
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(this.getReadableDatabase());
    }

    private List<Pump> loadPumps() {
        Log.i(TAG, "loadPumps");
        // return IngredientTable.
        List<? extends Pump> res = Tables.TABLE_PUMP.getAllElements(this.getReadableDatabase());
        return (List<Pump>) res;
    }

    private List<Topic> loadTopics() {
        Log.i(TAG, "loadTopics");
        //return
        List<? extends Topic> res = Tables.TABLE_TOPIC.getAllElements(this.getReadableDatabase());
        return (List<Topic>) res;
    }

    private List<SQLRecipeIngredient> loadIngredientVolumes(){
        Log.i(TAG, "loadIngredientVolumes");
        //return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);
        return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);

    }

    public Ingredient loadIngredient(long id) throws AccessDeniedException {
        Log.i(TAG, "loadIngredient");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_INGREDIENT.getElement(this.getReadableDatabase(), id);
    }

    public Ingredient loadIngredientForPump(long id) {
        Log.i(TAG, "loadIngredientForPump");
        return Tables.TABLE_INGREDIENT.getElement(this.getReadableDatabase(), id);
    }

    public Recipe loadRecipe(long id) throws AccessDeniedException {
        Log.i(TAG, "loadRecipe");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_RECIPE.getElement(this.getReadableDatabase(), id);
    }

    public Topic loadTopic(long id) throws AccessDeniedException {
        Log.i(TAG, "loadTopic");
        if(!AdminRights.isAdmin()){
            throw  new AccessDeniedException();
        }
        return Tables.TABLE_TOPIC.getElement(this.getReadableDatabase(), id);
    }


    //CHECKER

    public boolean checkAvailabilityOfAllIngredients(HashMap<Long, Integer> ingredientVolume) {
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

    public List<SQLIngredientPump> getIngredientPumps() {
        Log.i(TAG, "getIngredientPumps");
        return this.ingredientPumps;
    }

    public List<Pump> getPumps() {
        Log.i(TAG, "getPumps");
        return this.pumps;
    }

    public Pump getPump(Long id){
        Log.i(TAG, "getPump");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.pumps.stream().filter(p->p.getID()==id).findFirst().orElse(null);
        }
        Helper<Pump> h = Helper.getPumpHelper();
        return h.getWithId(this.pumps, id);
    }

    public Ingredient getIngredient(Long id) {
        Log.i(TAG, "getIngredient");
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
        Log.i(TAG, "getIngredients");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(i->ingredients.contains(i.getID())).collect(Collectors.toList());
        }

        Helper<Ingredient> h = Helper.getIngredientHelper();
        return h.getWithIds(this.ingredients, ingredients);
    }

    public Recipe getRecipe(Long id) {
        Log.i(TAG, "getRecipe");
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
        Log.i(TAG, "getRecipes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(i->recipeIds.contains(i.getID())).collect(Collectors.toList());
        }

        Helper<Recipe> h = Helper.getRecipeHelper();
        return h.getWithIds(this.recipes, recipeIds);
    }

    public List<Recipe> getRecipeWith(String needle) {
        Log.i(TAG, "getRecipeWith");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.recipesWithNeedleInName(this.recipes, needle);
    }

    public Recipe getRecipeWithExact(String name) {
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

    public List<Ingredient> getIngredientWith(String needle) {
        Log.i(TAG, "getIngredientWith");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(i->i.getName().contains(needle)).collect(Collectors.toList());
        }
        return Helper.ingredientWithNeedleInName(this.ingredients, needle);
    }

    public List<Ingredient> getIngredientsWithExact(String name) {
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

    public Ingredient getIngredientWithExact(String name) {
        Log.i(TAG, "getIngredientWithExact");
        List<Ingredient> res = getIngredientsWithExact(name);
        if(res.isEmpty()){
            Log.i(TAG, "getIngredientWithExact ingredients is empty");
            return null;
        }
        return res.get(0);
    }


    public List<? extends Ingredient> getAllIngredients() {
        Log.i(TAG, "getAllIngredients");
        return ingredients;
    }


    public List<? extends Ingredient> getAvailableIngredients() {
        Log.i(TAG, "getAvailableIngredients");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredients.stream().filter(Ingredient::isAvailable).collect(Collectors.toList());
        }
        return Helper.getIngredientHelper().getAvailable(this.ingredients);
    }

    public List<? extends Recipe> getRecipes() {
        Log.i(TAG, "getRecipes");
        if (AdminRights.isAdmin()) {
            return this.recipes;
        }
        return this.getAvailableRecipes();
    }


    public List<? extends Recipe> getAvailableRecipes() {
        Log.i(TAG, "getAvailableRecipes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.recipes.stream().filter(Recipe::isAvailable).collect(Collectors.toList());
        }
        return Helper.getRecipeHelper().getAvailable(this.recipes);
    }

    public List<String> getUrls(SQLIngredient newSQLIngredient) {
        Log.i(TAG, "getUrls");
        return Tables.TABLE_INGREDIENT_URL.getUrls(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    public List<SQLIngredientImageUrlElement> getUrlElements(SQLIngredient newSQLIngredient) {
        Log.i(TAG, "getUrlElements");
        return Tables.TABLE_INGREDIENT_URL.getElements(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    public List<String> getUrls(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getUrls");
        return Tables.TABLE_RECIPE_URL.getUrls(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    public List<SQLRecipeImageUrlElement> getUrlElements(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getUrlElements");
        return Tables.TABLE_RECIPE_URL.getElements(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    private List<Topic> getTopics(List<Long> t_ids) {
        Log.i(TAG, "getTopics");
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
        Log.i(TAG, "getTopics");
        return this.getTopics(newSQLRecipe.getTopics());
    }

    public Topic getTopic(long id) {
        Log.i(TAG, "getTopic");
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

    public List<Topic> getTopicsWith(String needle) {
        Log.i(TAG, "getTopicsWith");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Log.i(TAG, "getTopicsWith high version");
            return this.topics.stream().filter(t -> t.getName().contains(needle)).collect(Collectors.toList());
        } else {
            Log.i(TAG, "getTopicsWith low version");
            return Helper.topicWithNeedleInName(this.topics, needle);
        }
    }

    public List<Topic> getTopicsWithExact(String name) {
        Log.i(TAG, "getTopicsWithExact");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return this.topics.stream().filter(t -> t.getName().equals(name)).collect(Collectors.toList());
        } else {
            return Helper.topicWithName(this.topics, name);
        }
    }

    /**
     * get topic wit needle
     * @param needle
     * @return
     */
    public Topic getTopicWith(String needle) {
        Log.i(TAG, "getTopicWith");
        return getTopicWith(needle, false);
    }

    /**
     * get topic wit needle or make new, if true
     * @param needle
     * @return
     */
    public Topic getTopicWith(String needle, boolean makeNew) {
        Log.i(TAG, "getTopicWith");
        List<Topic> ts = getTopicsWith(needle);
        if(!ts.isEmpty()){
            Log.i(TAG, "getTopicWith not isEmpty");
            return ts.get(0);
        }
        if(makeNew) {
            Log.i(TAG, "getTopicWith makeNew");
            return Topic.makeNew(needle, "Füll bitte bei Gelegenheit aus!");
        }else{
            Log.i(TAG, "getTopicWith return null");
            return null;
        }
    }

    /**
     * get topic with exact this name or null
     * @param name
     * @return
     */
    public Topic getTopicWithExact(String name) {
        Log.i(TAG, "getTopicWithExact");
        return getTopicWithExact(name, false);
    }

    /**
     * get topic with exact this name or make one, if makeNew
     * @param name
     * @param makeNew
     * @return
     */
    public Topic getTopicWithExact(String name, boolean makeNew) {
        Log.i(TAG, "getTopicWithExact");
        List<Topic> ts = getTopicsWithExact(name);
        if(!ts.isEmpty()){
            return ts.get(0);
        }
        if(makeNew) {
            return Topic.makeNew(name, "Füll bitte bei Gelegenheit aus!");
        }
        return null;
    }

    public List<Topic> getTopics(Recipe recipe) {
        Log.i(TAG, "getTopics");
        return this.getTopics(recipe.getTopics());
    }

    public List<Topic> getTopics() {
        Log.i(TAG, "getTopics");
        return this.topics;
    }

    public List<Long> getTopicIDs(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getTopicIDs");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.getTopics(newSQLRecipe).stream().map(Topic::getID).collect(Collectors.toList());
        }
        return Helper.gettopichelper().getIds(this.getTopics(newSQLRecipe));
    }

    public List<SQLRecipeIngredient> getIngredientVolumes(SQLRecipe newSQLRecipe) {
        Log.i(TAG, "getIngredientVolumes");
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
        Log.i(TAG, "addIngredientImageUrl");
        // this.getWritableDatabase().
        Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(),ingredientId, url);
    }

    public void addOrUpdate(SQLIngredient ingredient) {
        Log.i(TAG, "addOrUpdate");
        if(ingredient.isSaved() && ingredient.needsUpdate()){
            Tables.TABLE_INGREDIENT.updateElement(this.getWritableDatabase(), ingredient);
            this.ingredients.remove(ingredient);
        }else{
            ingredient.setID(Tables.TABLE_INGREDIENT.addElement(this.getWritableDatabase(), ingredient));
        }
        this.ingredients.add(ingredient);

    }

    public void addOrUpdate(SQLRecipe recipe) {
        Log.i(TAG, "addOrUpdate");
        if(recipe.isSaved() && recipe.needsUpdate()){
            this.recipes.remove(recipe);
            Tables.TABLE_RECIPE.updateElement(this.getWritableDatabase(), recipe);
        }else{
            recipe.setID(Tables.TABLE_RECIPE.addElement(this.getWritableDatabase(), recipe));
        }
        this.recipes.add(recipe);
    }

    public void addOrUpdate(SQLTopic topic) {
        Log.i(TAG, "addOrUpdate");
        if(topic.isSaved() && topic.needsUpdate()){
            this.topics.remove(topic);
            Tables.TABLE_TOPIC.updateElement(this.getWritableDatabase(), topic);
        }else{
            topic.setID(Tables.TABLE_TOPIC.addElement(this.getWritableDatabase(), topic));
        }
        this.topics.add(topic);
    }

    public void addOrUpdate(SQLPump pump) {
        Log.i(TAG, "addOrUpdate");
        if(pump.isSaved() && pump.needsUpdate()){
            Tables.TABLE_PUMP.updateElement(this.getWritableDatabase(), pump);
            this.pumps.remove(pump);
        }else{
            pump.setID(Tables.TABLE_PUMP.addElement(this.getWritableDatabase(), pump));
        }
        this.pumps.add(pump);
    }

    public void addOrUpdate(SQLRecipeTopic recipeTopic) {
        Log.i(TAG, "addOrUpdate");
        if(recipeTopic.isSaved() && recipeTopic.needsUpdate()){
            Tables.TABLE_RECIPE_TOPIC.updateElement(this.getWritableDatabase(), recipeTopic);
        }else{
            recipeTopic.setID(Tables.TABLE_RECIPE_TOPIC.addElement(this.getWritableDatabase(), recipeTopic));
        }
    }

    public void addOrUpdate(SQLIngredientPump ingredientPump) {
        Log.i(TAG, "addOrUpdate");
        if(ingredientPump.isSaved() && ingredientPump.needsUpdate()){
            Tables.TABLE_INGREDIENT_PUMP.updateElement(this.getWritableDatabase(), ingredientPump);
            //this.ingredientPumps.remove(ingredientPump);
        }else{
            ingredientPump.setID(Tables.TABLE_INGREDIENT_PUMP.addElement(this.getWritableDatabase(), ingredientPump));
        }
        this.ingredientPumps.add(ingredientPump);
    }

    public void addOrUpdate(SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "addOrUpdate");
        if(recipeIngredient.isSaved() && recipeIngredient.needsUpdate()){
            Tables.TABLE_RECIPE_INGREDIENT.updateElement(this.getWritableDatabase(), recipeIngredient);
            //this.recipeIngredients.remove(recipeIngredient);
        }else{
            recipeIngredient.setID(Tables.TABLE_RECIPE_INGREDIENT.addElement(this.getWritableDatabase(), recipeIngredient));
        }
        this.recipeIngredients.add(recipeIngredient);
    }

    public void addOrUpdate(SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "addOrUpdate");
        if(recipeImageUrlElement.isSaved() && recipeImageUrlElement.needsUpdate()){
            Tables.TABLE_RECIPE_URL.updateElement(this.getWritableDatabase(), recipeImageUrlElement);
        }else{
            recipeImageUrlElement.setID(Tables.TABLE_RECIPE_URL.addElement(this.getWritableDatabase(), recipeImageUrlElement));
        }
    }

    public void addOrUpdate(SQLIngredientImageUrlElement ingredientImageUrlElement) {
        Log.i(TAG, "addOrUpdate");
        if(ingredientImageUrlElement.isSaved() && ingredientImageUrlElement.needsUpdate()){
            Tables.TABLE_INGREDIENT_URL.updateElement(this.getWritableDatabase(), ingredientImageUrlElement);
        }else{
            ingredientImageUrlElement.setID(
                    Tables.TABLE_INGREDIENT_URL.addElement(this.getWritableDatabase(), ingredientImageUrlElement));
        }
    }



    //REMOVE in DB and buffer
    public void removeRecipe(long id) {
        Log.i(TAG, "removeRecipe");
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), id);
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.recipes.removeIf(i->i.getID()==id);
        }else {
            this.recipes = Helper.getRecipeHelper().removeIf(this.recipes, id);
        }
    }

    public void removeIngredient(long id) {
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

    public void removePump(long id) {
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
        }
    }


    public void remove(Ingredient ingredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT.deleteElement(this.getWritableDatabase(), ingredient.getID());
        this.ingredients.remove(ingredient);
    }

    public void remove(Recipe recipe) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE.deleteElement(this.getWritableDatabase(), recipe.getID());
        Tables.TABLE_RECIPE_URL.deleteWithOwnerId(this.getWritableDatabase(), recipe.getID());
        this.recipes.remove(recipe);
    }

    public void remove(Pump pump) {
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


    public void remove(SQLRecipeTopic newSQLRecipeTopic) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_TOPIC.deleteElement(this.getWritableDatabase(), newSQLRecipeTopic);
    }

    public void remove(SQLTopic topic) {
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

    public void remove(SQLRecipeIngredient recipeIngredient) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_INGREDIENT.deleteElement(this.getWritableDatabase(),recipeIngredient);
        this.recipeIngredients.remove(recipeIngredient);
    }

    public void remove(SQLIngredientPump ingredientPump) {
        Log.i(TAG, "remove");
        Tables.TABLE_INGREDIENT_PUMP.deleteElement(this.getWritableDatabase(), ingredientPump);
        this.ingredientPumps.remove(ingredientPump);
    }

    public void remove(SQLRecipeImageUrlElement recipeImageUrlElement) {
        Log.i(TAG, "remove");
        Tables.TABLE_RECIPE_URL.deleteElement(this.getWritableDatabase(),recipeImageUrlElement);
    }

    public void remove(SQLIngredientImageUrlElement ingredientImageUrlElement) {
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
        for (String createCmd : Tables.getCreateCmds()) {
            db.execSQL(createCmd);
        }


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
        //db.enableWriteAheadLogging();
        if(oldVersion == DatabaseConnection.version) {
            for (String deleteCmd : Tables.getDeleteCmds()) {
               db.execSQL(deleteCmd);
            }
            onCreate(db);
            DatabaseConnection.version = newVersion;
        }
    }
}
