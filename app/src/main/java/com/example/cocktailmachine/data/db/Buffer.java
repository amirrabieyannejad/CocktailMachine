package com.example.cocktailmachine.data.db;

import android.app.Activity;
import android.content.Context;
import android.media.MediaParser;
import android.os.Build;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.AccessDeniedException;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.example.cocktailmachine.ui.settings.SettingsActivity;
import com.opencsv.CSVReader;

import org.apache.commons.collections4.Get;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author Johanna Reidt
 * @created Mo. 18.Sep 2023 - 12:54
 * @project CocktailMachine
 */
public class Buffer {
    private static final String TAG = "Buffer";
    private final int CHUNK_SIZE = 150;
    private static Buffer singleton;
    public static boolean isLoaded = false;
    private List<Ingredient> ingredients;
    private List<Topic> topics;
    private List<Recipe> recipes;
    private List<Pump> pumps;
    private List<SQLIngredientPump> ingredientPumps;
    private List<SQLRecipeIngredient> recipeIngredients;
    private List<SQLRecipeTopic> recipeTopics;


    //Fast access
    private boolean isFast = false;
    private HashMap<Long, Ingredient> fastIDIngredient;
    private HashMap<Long, Recipe> fastIDRecipe;
    private HashMap<Long, Topic> fastIDTopic;
    private HashMap<Long, Pump> fastIDPump;


    private HashMap<String, Ingredient> fastNameIngredient;
    private HashMap<String, Recipe> fastNameRecipe;
    private HashMap<String, Topic> fastNameTopic;


    private List<Ingredient> fastAvailableIngredient;
    private List<Recipe> fastAvailableRecipe;


    private HashMap<String, Ingredient> fastNameAvailableIngredient;
    private HashMap<String, Recipe> fastNameAvailableRecipe;

    private HashMap<Long, Ingredient> fastIDAvailableIngredient;
    private HashMap<Long, Recipe> fastIDAvailableRecipe;




    private HashMap<Long, List<Long>> fastTopicRecipes;
    private HashMap<Long, List<Long>> fastRecipeTopics;
    private HashMap<Long, List<Long>> fastRecipeIngredient;
    private HashMap<Long, List<Long>> fastIngredientRecipes;

    private HashMap<Long, List<SQLRecipeIngredient>> fastRecipeRecipeIngredient;
    private HashMap<Long, List<SQLRecipeTopic>> fastRecipeRecipeTopic;
    private HashMap<Long, List<SQLRecipeIngredient>> fastIngredientRecipeIngredient;
    private HashMap<Long, List<SQLRecipeTopic>> fastTopicRecipeTopic;







    private Buffer(){}


    public static Buffer getSingleton(){
        if(singleton == null){
            singleton = new Buffer();
        }
        return singleton;
    }

    public static Buffer getSingleton(Context context){
        if(singleton == null){
            singleton = new Buffer();
        }
        if(!isLoaded){
            try {
                singleton.setLoad(context);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        return singleton;
    }

    public static HashMap<String, Long> getIngredientPumpSet(Context context) {
        return GetFromDB.loadIngredientPumpSet(context);
    }


    private void setLoad(Context context) throws NotInitializedDBException {
        DatabaseConnection.init(context);
        this.pumps = DatabaseConnection.getSingleton().loadPumps();
        this.ingredientPumps = DatabaseConnection.getSingleton().loadIngredientPumps();
        this.loadAvailableIngredient(context);
        this.loadAvailableRecipeIngredient(context);
        this.loadAvailableRecipe(context);
        this.loadRecipeTopics(context);
        //this.ingredients = new ArrayList<>();
        //this.recipes = DatabaseConnection.getSingleton().loadAllRecipes();
        //this.pumps =  DatabaseConnection.getSingleton().loadPumps();
        this.topics =  DatabaseConnection.getSingleton().loadTopics();
        //this.recipeIngredients = DatabaseConnection.getSingleton().loadIngredientVolumes();
        //this.recipeTopics = DatabaseConnection.getSingleton().loadRecipeTopic();

        /*
        this.ingredients = DatabaseConnection.getSingleton().loadAllIngredients();
        this.recipes = DatabaseConnection.getSingleton().loadAllRecipes();
        this.pumps =  DatabaseConnection.getSingleton().loadPumps();
        this.topics =  DatabaseConnection.getSingleton().loadTopics();
        this.recipeIngredients = DatabaseConnection.getSingleton().loadIngredientVolumes();
        this.recipeTopics = DatabaseConnection.getSingleton().loadRecipeTopic();

         */
        //loadFast();
        isLoaded = true;
    }


    public void loadFast(){
        fastIDIngredient = new HashMap<>();
        fastNameIngredient = new HashMap<>();
        fastAvailableIngredient = new ArrayList<>();
        fastIDAvailableIngredient = new HashMap<>();
        fastNameAvailableIngredient = new HashMap<>();
        for(Ingredient i: ingredients){
            fastIDIngredient.put(i.getID() , i);
            fastNameIngredient.put(i.getName() , i);
            if(i.isAvailable()) {
                fastAvailableIngredient.add(i);
                fastIDAvailableIngredient.put(i.getID() , i);
                fastNameAvailableIngredient.put(i.getName() , i);
            }
        }

        fastIDRecipe = new HashMap<>();
        fastNameRecipe = new HashMap<>();
        fastAvailableRecipe = new ArrayList<>();
        fastIDAvailableRecipe = new HashMap<>();
        fastNameAvailableRecipe = new HashMap<>();
        for(Recipe i: recipes){
            fastIDRecipe.put(i.getID() , i);
            fastNameRecipe.put(i.getName() , i);
            if(i.isAvailable()) {
                fastAvailableRecipe.add(i);
                fastIDAvailableRecipe.put(i.getID() , i);
                fastNameAvailableRecipe.put(i.getName() , i);
            }
        }

        fastIDTopic = new HashMap<>();
        fastNameTopic = new HashMap<>();
        for(Topic i: topics){
            fastIDTopic.put(i.getID() , i);
            fastNameTopic.put(i.getName() , i);
        }

        fastIDPump = new HashMap<>();
        for(Pump i: pumps){
            fastIDPump.put(i.getID() , i);
        }

        fastTopicRecipes = new HashMap<>();
        fastRecipeTopics = new HashMap<>();
        fastTopicRecipeTopic = new HashMap<>();
        fastRecipeRecipeTopic = new HashMap<>();
        for(SQLRecipeTopic rt: this.recipeTopics){
            if(fastTopicRecipes.containsKey(rt.getTopicID())){
                Objects.requireNonNull(fastTopicRecipes.get(rt.getTopicID())).add(rt.getRecipeID());
            }else{
                List<Long> elms = new ArrayList<>();
                elms.add(rt.getRecipeID());
                fastTopicRecipes.put(rt.getTopicID(), elms);
            }
            if(fastRecipeTopics.containsKey(rt.getRecipeID())){
                Objects.requireNonNull(fastRecipeTopics.get(rt.getRecipeID())).add(rt.getTopicID());
            }else{
                List<Long> elms = new ArrayList<>();
                elms.add(rt.getTopicID());
                fastTopicRecipes.put(rt.getRecipeID(), elms);
            }
            if(fastTopicRecipeTopic.containsKey(rt.getTopicID())){
                Objects.requireNonNull(fastTopicRecipeTopic.get(rt.getTopicID())).add(rt);
            }else{
                List<SQLRecipeTopic> elms = new ArrayList<>();
                elms.add(rt);
                fastTopicRecipeTopic.put(rt.getTopicID(), elms);
            }
            if(fastRecipeRecipeTopic.containsKey(rt.getRecipeID())){
                Objects.requireNonNull(fastRecipeRecipeTopic.get(rt.getRecipeID())).add(rt);
            }else{
                List<SQLRecipeTopic> elms = new ArrayList<>();
                elms.add(rt);
                fastRecipeRecipeTopic.put(rt.getRecipeID(), elms);
            }
        }


        fastIngredientRecipes = new HashMap<>();
        fastRecipeIngredient = new HashMap<>();
        fastRecipeRecipeIngredient = new HashMap<>();
        fastIngredientRecipeIngredient = new HashMap<>();
        for(SQLRecipeIngredient rt: this.recipeIngredients){
            if(fastIngredientRecipes.containsKey(rt.getIngredientID())){
                Objects.requireNonNull(fastIngredientRecipes.get(rt.getIngredientID())).add(rt.getRecipeID());
            }else{
                List<Long> elms = new ArrayList<>();
                elms.add(rt.getRecipeID());
                fastIngredientRecipes.put(rt.getIngredientID(), elms);
            }
            if(fastRecipeIngredient.containsKey(rt.getRecipeID())){
                Objects.requireNonNull(fastRecipeIngredient.get(rt.getRecipeID())).add(rt.getIngredientID());
            }else{
                List<Long> elms = new ArrayList<>();
                elms.add(rt.getIngredientID());
                fastRecipeIngredient.put(rt.getRecipeID(), elms);
            }
            if(fastRecipeRecipeIngredient.containsKey(rt.getRecipeID())){
                Objects.requireNonNull(fastRecipeRecipeIngredient.get(rt.getRecipeID())).add(rt);
            }else{
                List<SQLRecipeIngredient> elms = new ArrayList<>();
                elms.add(rt);
                fastRecipeRecipeIngredient.put(rt.getRecipeID(), elms);
            }
            if(fastIngredientRecipeIngredient.containsKey(rt.getIngredientID())){
                Objects.requireNonNull(fastIngredientRecipeIngredient.get(rt.getIngredientID())).add(rt);
            }else{
                List<SQLRecipeIngredient> elms = new ArrayList<>();
                elms.add(rt);
                fastIngredientRecipeIngredient.put(rt.getIngredientID(), elms);
            }
        }





        isFast = true;




    }

    public void lowMemory(){
        isFast = false;
        fastIDPump = null;
        fastNameIngredient = null;
        fastNameTopic = null;
        fastNameRecipe = null;
        fastIDTopic = null;
        fastIDRecipe = null;
        fastIDIngredient = null;
        fastAvailableIngredient = null;
        fastAvailableRecipe = null;
        fastNameAvailableRecipe = null;
        fastIDAvailableRecipe = null;
        fastNameAvailableIngredient = null;
        fastIDAvailableIngredient = null;
    }

    public void noMemory(){
        lowMemory();
        this.ingredients = null;
        this.recipes = null;
        this.ingredientPumps = null;
        this.pumps = null;
        this.topics =  null;
        this.recipeIngredients = null;
        singleton = null;
        isLoaded = false;
    }

    /**
     * empties buffer and loads new
     * @author Johanna Reidt
     * @param context
     */
    public static void localRefresh(Context context){
        if(singleton != null) {
            singleton.noMemory();
        }
        getSingleton(context);
    }

    /**
     * load for setup with empty pumps, empty all pumps
     * @author Johanna Reidt
     * @param context
     */
    public static void loadForSetUp(Context context){
        //TODO:
        //load(context);

        Buffer.setUpEmptyPumps(context);
    }

    /**
     * load csv and json files prepared by phillip
     * @author Johanna Reidt
     * @param context
     */
    public static void loadPreped(Context context) {
        Log.i(TAG, "loadPreped" );
        loadLiquid(context);
        loadPrepedRecipes(context);
    }

    /**
     * load liquid csv
     * @author Johanna Reidt
     * @param context
     */
    private static void loadLiquid(Context context){
        Log.i(TAG, "loadLiquid" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
        try {
            /*
            //URL path = Buffer.class.getResource("liquid.csv");
            //URL path = ClassLoader.getSystemResource("liquid.csv");
            //Log.i(TAG, "loadLiquid: url path: "+path );
            //if(path == null){
            //    Log.i(TAG, "loadLiquid: path is null" );
            //    return;
            //}
            //File f = new File(path.getFile());
            //File csvfile = new File("liquid.csv");

            Log.i(TAG, "loadLiquid: "+ClassLoader.getSystemClassLoader().toString());
            Log.i(TAG, "loadLiquid: "+Buffer.class);
            Log.i(TAG, "loadLiquid: "+Buffer.class.getPackage());
            Log.i(TAG, "loadLiquid: "+Buffer.class.getPackage());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();
                Log.i(TAG, "loadLiquid: "+"Current absolute path is: " + s);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path path = FileSystems.getDefault().getPath(".");
                Log.i(TAG, "loadLiquid: "+"Current dir " + path);
                path = FileSystems.getDefault().getPath(".").toAbsolutePath();
                Log.i(TAG, "loadLiquid: "+"Current absolute path is: " + path);
            }
            //String cwd = Path.of("").toAbsolutePath().toString();
            File currentDir = new File("");
            Log.i(TAG, "loadLiquid: "+"Current dir: " + currentDir);
            currentDir = new File("").getAbsoluteFile();
            Log.i(TAG, "loadLiquid: "+"Current dir, absolute path: " + currentDir);
            File[] files = currentDir.listFiles();
            if(files != null) {
                Log.i(TAG, "loadLiquid: Size: " + files.length);
                for (File file : files) {
                    Log.i(TAG, "loadLiquid: FileName:" + file.getName());
                }
            }else{
                Log.i(TAG, "loadLiquid: no files in dir");
            }

             */
            /*
            Log.i(TAG, "loadLiquid: data "+Environment.getDataDirectory());
            Log.i(TAG, "loadLiquid: ex storage "+Environment.getExternalStorageState());
            Log.i(TAG, "loadLiquid: root "+Environment.getRootDirectory());
            Log.i(TAG, "loadLiquid: download "+Environment.getDownloadCacheDirectory());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.i(TAG, "loadLiquid: storage "+Environment.getStorageDirectory());
            }

             */
            /*
            File currentDir = context.getFilesDir();
            Log.i(TAG, "loadLiquid: currentDir "+currentDir);


             */
            /*
            File currentDir = new File("./");
            Log.i(TAG, "loadLiquid: "+"Current dir: " + currentDir);
            //currentDir = new File("./").getAbsoluteFile();
            Log.i(TAG, "loadLiquid: "+"Current dir, absolute path: " + currentDir);

             */
            /*
            File[] files = currentDir.listFiles();
            if(files != null) {
                Log.i(TAG, "loadLiquid: Size: " + files.length);
                for (File file : files) {
                    Log.i(TAG, "loadLiquid: FileName:" + file.getName());
                }
            }else{
                Log.i(TAG, "loadLiquid: no files in dir");
            }

            String[] file_names = context.fileList();
            Log.i(TAG, "loadLiquid: fileList: "+ Arrays.toString(file_names));
            Log.i(TAG, "loadLiquid: getPackageCodePath: "+ context.getPackageCodePath());
            Log.i(TAG, "loadLiquid: getPackageResourcePath: "+ context.getPackageResourcePath());

             */
            InputStream is = context.getResources().openRawResource(R.raw.liquid);
            Log.i(TAG, "loadLiquid: get raw: "+ is.toString());

            //File csvfile = new File("./liquid.csv");
            //Log.i(TAG, "loadLiquid: file: "+csvfile );
            //new File(Environment.getExternalStorageDirectory() + "/csvfile.csv");
            //CSVReader reader = new CSVReader(new FileReader(csvfile));
            CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            //CSVReader reader = new CSVReader(new FileReader(f.getAbsolutePath()));
            Log.i(TAG, "loadLiquid: opening file successful");
            String[] nextLine;
            reader.readNext();//skip first line
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                Log.i(TAG, "loadLiquid: next line: "+ Arrays.toString(nextLine));
                String name = nextLine[0];
                boolean alcoholic = false;
                int colour = new Random().nextInt();
                try {
                    alcoholic = Integer.parseInt(nextLine[1]) == 1;
                }catch (NumberFormatException e){
                    Log.i(TAG, "loadLiquid: failed to read alcoholic, alcoholic set to false");
                    Log.e(TAG, "error: "+e);
                    e.printStackTrace();
                }
                try {
                    colour = Integer.parseInt(nextLine[2]);
                }catch (NumberFormatException e){
                    Log.i(TAG, "loadLiquid: failed to read colour, use random");
                    Log.e(TAG, "error: "+e);
                    e.printStackTrace();
                }
                Ingredient.makeNew(name, alcoholic, colour).save(context);
            }
            reader.close();
            is.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG,"loadLiquid: file not found" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"loadLiquid: io error" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
        Toast.makeText(context, "Zutaten geladen!", Toast.LENGTH_SHORT).show();

    }

    /**
     * load recipe json
     * @author Johanna Reidt
     * @param context
     */
    private static void loadPrepedRecipes(Context context){
        Log.i(TAG, "loadPrepedRecipes" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
        try {

            InputStream is = context.getResources().openRawResource(R.raw.recipe);
            Log.i(TAG, "loadPrepedRecipes: get raw: "+ is.toString());
            //File jsonfile = new File("recipe.json");
            //InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(is,
                            StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            streamReader.close();
            is.close();

            //JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            JSONObject json = new JSONObject(responseStrBuilder.toString());
            Log.i(TAG, "loadPrepedRecipes: opening file successful");

            Iterator<String> names =  json.keys();
            while(names.hasNext()){
                String name = names.next();
                Log.i(TAG, "loadPrepedRecipes: next recipe: "+name);
                Recipe r = Recipe.searchOrNew(context,name);
                r.removeIngredients(context, r.getIngredients());
                JSONObject ingVol = json.getJSONObject(name);
                Iterator<String> ings = ingVol.keys();
                while(ings.hasNext()){
                    String ingName = ings.next();
                    Log.i(TAG, "loadPrepedRecipes: next recipe: add ing "+ingName);
                    r.add(context,
                            Ingredient.searchOrNew(context, ingName), //gets ing or new
                            ingVol.optInt(ingName));//given vol or 0
                    r.save(context);
                }
                Log.i(TAG, "loadPrepedRecipes: next recipe: "+name+" done saving");
                r.save(context);

            }


        } catch (JSONException e) {
            Log.i(TAG,"loadLiquid: JSONException" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.i(TAG,"loadLiquid: no file" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG,"loadLiquid: UnsupportedEncodingException" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"loadLiquid: IOException" );
            Log.e(TAG, "error: "+e);
            e.printStackTrace();
        }
        Toast.makeText(context, "Rezepte geladen!", Toast.LENGTH_SHORT).show();
    }

    public static void loadDummy(Activity activity){
        Log.i(TAG, "loadDummy");
        try {
            DatabaseConnection.init(activity).loadDummy(activity);
        } catch (NotInitializedDBException | MissingIngredientPumpException e) {
            Log.i(TAG, "loadDummy");
            Log.e(TAG, "error", e);
            Log.getStackTraceString(e);
        }
    }

    public static void loadPrepedDB(Context context){
        //InputStream inputStream = context.getResources().openRawResource(R.raw);
    }
















    public void addToBuffer(Ingredient e){
        this.ingredients.add(e);
        if(isFast){
            this.fastNameIngredient.put(e.getName(), e);
            this.fastIDIngredient.put(e.getID(),e );
            if(e.isAvailable()){

                this.fastNameAvailableIngredient.put(e.getName(), e);
                this.fastIDAvailableIngredient.put(e.getID(),e );
            }
        }
    }
    public void removeFromBuffer(Ingredient e){
        this.topics.remove(e);
        if(isFast){
            this.fastNameIngredient.remove(e.getName());
            this.fastIDIngredient.remove(e.getID());
            if(e.isAvailable()){

                this.fastNameAvailableIngredient.remove(e.getName());
                this.fastIDAvailableIngredient.remove(e.getID());
            }
        }
    }

    /**
     * get ingredient list form buffer
     * @author Johanna Reidt
     * @return
     */
    public List<Ingredient> getIngredients(){
        return this.ingredients;
    }

    /**
     * get ingredient list if empty from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    public List<Ingredient> getIngredients(Context context)  {
        if(this.ingredients == null || this.ingredients.isEmpty()) {
            DatabaseConnection.init(context);
            try {
                this.ingredients = DatabaseConnection.getSingleton().loadAllIngredients();
            } catch (NotInitializedDBException e) {
                this.ingredients = new ArrayList<>();
            }
        }
        return this.ingredients;
    }

    /**
     * get ingredient with id from buffer
     * @author Johanna Reidt
     * @param id
     */
    @Nullable
    public Ingredient getIngredient(long id){
        if(isFast){
            return this.fastIDIngredient.get(id);
        }
        for(Ingredient i: this.ingredients){
            if(i.getID() ==  id){
                return i;
            }
        }
        return null;
    }

    /**
     * get ingredient with needle in name from buffer
     * @author Johanna Reidt
     * @param needle
     */
    @Nullable
    public Ingredient getIngredient(String needle){
        if(isFast){
            for(String key: this.fastNameIngredient.keySet()){
                if(key.contains(needle)){
                    return this.fastNameIngredient.get(key);
                }
            }
        }
        for(Ingredient i: this.ingredients){
            if(i.getName().contains(needle)){
                return i;
            }
        }
        return null;
    }

    /**
     * get ingredient with needle in name from buffer or if not existent check db
     * @author Johanna Reidt
     * @param context
     * @param id
     */
    @Nullable
    public Ingredient getIngredient(Context context, long id){
        Ingredient res = getIngredient(id);
        if(res != null){
            return res;
        }
        return GetFromDB.loadIngredient(context, id);
    }

    /**
     * ingredient names
     * get topic names
     * @author Johanna Reidt
     * @return
     */
    public List<String> getIngredientNames(){
        if(isFast){
            return new ArrayList<>(this.fastNameIngredient.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Ingredient i:this.ingredients){
            names.add(i.getName());
        }
        return names;
    }
    /**
     * get ingredient names and current volume
     * @author Johanna Reidt
     * @return
     */
    public List<String> getIngredientNameAndVol(){
        if(isFast){
            return new ArrayList<>(this.fastNameIngredient.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Ingredient i:this.ingredients){
            names.add(i.getName()+": "+i.getVolume()+" ml");
        }
        return names;
    }
    public List<Ingredient> getAvailableIngredients(){
        if(isFast){
            return this.fastAvailableIngredient;
        }else{
            List<Ingredient> res = new ArrayList<>();
            for(Ingredient recipe: this.ingredients){
                if(recipe.isAvailable()){
                    res.add(recipe);
                }

            }
            return res;

        }
    }
    /**
     * ingredient names
     * get topic names
     * @author Johanna Reidt
     * @return
     */
    public List<String> getAvailableIngredientNames(){
        if(isFast){
            return new ArrayList<>(this.fastNameAvailableIngredient.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Ingredient i:this.ingredients){
            if(i.isAvailable()) {
                names.add(i.getName());
            }
        }
        return names;
    }


    public Ingredient getAvailableIngredient(Long id){
        if(isFast){
            return this.fastIDAvailableIngredient.get(id);
        }else{
            Ingredient res = getIngredient(id);
            if(res != null && res.isAvailable()){
                return res;
            }
            return null;
        }
    }


    /**
     * get recipes with ids
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<Ingredient> getAvailableIngredients(List<Long> ids){
        List<Ingredient> res = new ArrayList<>();
        if(isFast){
            for(Ingredient i: this.fastAvailableIngredient){
                if(ids.contains(i.getID())){
                    res.add(i);
                }
            }
        }else{
            for(Ingredient recipe: this.ingredients){
                if(recipe.isAvailable()) {
                    if (ids.contains(recipe.getID())) {
                        res.add(recipe);
                    }
                }
            }
        }
        return res;
    }



    /**
     * get recipes with ids
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<Ingredient> getIngredients(List<Long> ids){
        List<Ingredient> res = new ArrayList<>();
        if(ids == null){
            return res;
        }
        if(isFast){
            for(Long id: ids){
                Ingredient i = getIngredient(id);
                if(i!=null) {
                    res.add(i);
                }
            }
        }else{
            for(Ingredient recipe: this.ingredients){
                if(recipe.isAvailable()) {
                    if (ids.contains(recipe.getID())) {
                        res.add(recipe);
                    }
                }
            }
        }
        return res;
    }

    /**
     * ingredient names
     * get topic names
     * @author Johanna Reidt
     * @return
     */
    public List<String> getIngredientNames(List<Long> ids){
        List<String> names = new ArrayList<>();
        for(Ingredient i:getIngredients(ids)){
            names.add(i.getName());
        }
        return names;
    }
    /**
     * get ingredient names and current volume
     * @author Johanna Reidt
     * @return
     */
    public List<String> getIngredientNameAndVol(List<Long> ids){
        List<String> names = new ArrayList<>();
        for(Ingredient i:this.getIngredients(ids)){
            names.add(i.getName()+": "+i.getVolume()+" ml");
        }
        return names;
    }

    public List<Long> getIngredientIDs(){
        List<Long> res = new ArrayList<>();
        if(this.ingredients == null){
            this.ingredients = new ArrayList<>();
        }
        for(Ingredient i: this.ingredients){
            res.add(i.getID());
        }
        return res;
    }












    public void addToBuffer(Topic e){
        this.topics.add(e);
        if(isFast){
            this.fastNameTopic.put(e.getName(), e);
            this.fastIDTopic.put(e.getID(),e );
        }
    }
    public void removeFromBuffer(Topic e){
        this.topics.remove(e);
        if(isFast){
            this.fastNameTopic.remove(e.getName());
            this.fastIDTopic.remove(e.getID());
        }
    }

    /**
     * get topic list from buffer
     * @author Johanna Reidt
     * @return
     */
    public List<Topic> getTopics(){
        return this.topics;
    }

    /**
     * get topic list if empty load from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    public List<Topic> getTopics(Context context)  {
        if(this.topics == null || this.topics.isEmpty()) {
            DatabaseConnection.init(context);
            try {
                this.topics = DatabaseConnection.getSingleton().loadTopics();
            } catch (NotInitializedDBException e) {
                this.topics = new ArrayList<>();
            }
        }
        return this.topics;

    }

    /**
     * get topic with id from buffer
     * @author Johanna Reidt
     * @param id
     */
    @Nullable
    public Topic getTopic(long id){
        if(isFast){
            return this.fastIDTopic.get(id);
        }
        for(Topic i: this.topics){
            if(i.getID() ==  id){
                return i;
            }
        }
        return null;
    }

    /**
     * get topic with needle in name from buffer
     * @author Johanna Reidt
     * @param needle
     */
    @Nullable
    public Topic getTopic(String needle){
        if(isFast){
            for(String key: this.fastNameTopic.keySet()){
                if(key.contains(needle)){
                    return this.fastNameTopic.get(key);
                }
            }
        }
        for(Topic i: this.topics){
            if(i.getName().contains(needle)){
                return i;
            }
        }
        return null;
    }

    /**
     * get topic with needle in name from buffer or if not existent check db
     * @author Johanna Reidt
     * @param context
     * @param id
     */
    @Nullable
    public Topic getTopic(Context context, long id){
        Topic res = getTopic(id);
        if(res != null){
            return res;
        }
        return GetFromDB.loadTopic(context, id);
    }

    /**
     * get topic names
     * @author Johanna Reidt
     * @return
     */
    public List<String> getTopicNames(){
        if(isFast){
            return new ArrayList<>(this.fastNameTopic.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Topic i:this.topics){
            names.add(i.getName());
        }
        return names;
    }

    /**
     * get topics with ids
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<Topic> getTopics(List<Long> ids){
        ArrayList<Topic> res = new ArrayList<>();
        if(ids ==null){
            return res;
        }
        if(isFast) {//gets with hashmap
            for (Long id : ids) {
                res.add(getTopic(id));
            }
        }else{
            for(Topic topic: this.topics){
                if(ids.contains(topic.getID())){
                    res.add(topic);
                }
            }
        }
        return res;
    }

    /**
     * get topic names
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<String> getTopicNames(List<Long> ids){
        List<String> names = new ArrayList<>();
        for(Topic i:this.topics){
            if(ids.contains(i.getID())) {
                names.add(i.getName());
            }
        }
        return names;
    }
















    public void addToBuffer(Recipe e){
        this.recipes.add(e);
        if(isFast){
            this.fastNameRecipe.put(e.getName(), e);
            this.fastIDRecipe.put(e.getID(),e );
            if(e.isAvailable()){
                this.fastNameAvailableRecipe.put(e.getName(), e);
                this.fastIDAvailableRecipe.put(e.getID(),e );
            }
        }
    }
    public void removeFromBuffer(Recipe e){
        this.recipes.remove(e);
        if(isFast){
            this.fastNameRecipe.remove(e.getName());
            this.fastIDRecipe.remove(e.getID());
            if(e.isAvailable()){
                this.fastNameAvailableRecipe.remove(e.getName());
                this.fastIDAvailableRecipe.remove(e.getID());
            }
        }
    }
    /**
     * get all recipes in buffer
     * @author Johanna Reidt
     * @return
     */
    public List<Recipe> getRecipes(){
        if(this.recipes == null){
            this.recipes = new ArrayList<>();
        }
        return this.recipes;
    }

    /**
     * get all recipes if recipe list empty get from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    public List<Recipe> getRecipes(Context context)  {
        //if(this.recipes == null|| this.recipes.isEmpty()){
        if(this.getRecipes().isEmpty()){
            DatabaseConnection.init(context);
            try {
                this.recipes = DatabaseConnection.getSingleton().loadAllRecipes();
            } catch (NotInitializedDBException e) {
                this.recipes = new ArrayList<>();
            }
        }
        return this.recipes;
    }
    /**
     * get Recipe with id from buffer
     * @author Johanna Reidt
     * @param id
     */
    @Nullable
    public Recipe getRecipe(long id){
        if(this.recipes==null){
            return null;
        }
        if(isFast){
            Recipe res = this.fastIDRecipe.get(id);
            if(res != null){
                return res;
            }
        }
        for(Recipe i: this.recipes){
            if(i.getID() ==  id){
                return i;
            }
        }
        return null;
    }

    /**
     * get Recipe with needle in name from buffer
     * @author Johanna Reidt
     * @param needle
     */
    @Nullable
    public Recipe getRecipe(String needle){
        if(isFast){
            for(String key: this.fastNameRecipe.keySet()){
                if(key.contains(needle)){
                    return this.fastNameRecipe.get(key);
                }
            }
        }
        for(Recipe i: this.recipes){
            if(i.getName().contains(needle)){
                return i;
            }
        }
        return null;
    }

    /**
     * get Recipe with needle in name from buffer or if not existent check db
     * @author Johanna Reidt
     * @param context
     * @param id
     */
    @Nullable
    public Recipe getRecipe(Context context, long id){
        if(this.recipes == null){
            try {
                this.setLoad(context);
            } catch (NotInitializedDBException e) {
                //throw new RuntimeException(e);
                Log.e(TAG, "NotInitializedDBException");
                e.printStackTrace();
            }
        }
        Recipe res =getRecipe(id);
        if(res != null){
            return res;
        }
        return GetFromDB.loadRecipe(context, id);
    }

    /**
     * get recipe names
     * @author Johanna Reidt
     * @return
     */
    public List<String> getRecipeNames(){
        if(isFast){
            return new ArrayList<>(this.fastNameRecipe.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Recipe i:this.recipes){
            names.add(i.getName());
        }
        return names;
    }

    /**
     * get recipes with ids
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<Recipe> getRecipes(List<Long> ids){
        List<Recipe> res = new ArrayList<>();
        if(isFast){
            for(Long id: ids){
                res.add(getRecipe(id));
            }
        }else{
            for(Recipe recipe: this.recipes){
                if(ids.contains(recipe.getID())){
                    res.add(recipe);
                }
            }
        }
        return res;
    }

    public List<Recipe> getAvailableRecipes(){
        List<Recipe> res = new ArrayList<>();
        if(isFast){
            res = this.fastAvailableRecipe;
            if(res != null){
                return res;
            }
            res = new ArrayList<>();
        }
        for(Recipe recipe: this.recipes){
            if(recipe.isAvailable()){
                    res.add(recipe);
                }
        }
        return res;
    }

    public List<String> getAvailableRecipesNames(){
        if(isFast){
            return new ArrayList<>(this.fastNameAvailableRecipe.keySet());
        }
        List<String> names = new ArrayList<>();
        for(Recipe i:this.recipes){
            if(i.isAvailable()) {
                names.add(i.getName());
            }
        }
        return names;
    }

    public Recipe getAvailableRecipe(Long id){
        if(isFast){
            Recipe res = this.fastIDAvailableRecipe.get(id);
            if(res != null){
                return res;
            }
        }
        Recipe res = getRecipe(id);
        if(res != null && res.isAvailable()){
            return res;
        }
        return null;
    }


    /**
     * get recipes with ids
     * @author Johanna Reidt
     * @param ids
     * @return
     */
    public List<Recipe> getAvailableRecipes(List<Long> ids){
        List<Recipe> res = new ArrayList<>();
        if(isFast){
            for(Long id: ids){
                res.add(getAvailableRecipe(id));
            }
        }else{
            for(Recipe recipe: this.recipes){
                if(recipe.isAvailable()) {
                    if (ids.contains(recipe.getID())) {
                        res.add(recipe);
                    }
                }
            }
        }
        return res;
    }


    public List<Long> getIngredientIds(Recipe recipe){
        List<Long> res = new ArrayList<>();
        if(isFast){
            res= this.fastRecipeIngredient.get(recipe.getID());
            if(res != null){
                return res;
            }else{
                res = new ArrayList<>();
            }
        }
        if(this.recipeIngredients == null){
            this.recipeIngredients = new ArrayList<>();
        }
        for(SQLRecipeIngredient rt: this.recipeIngredients){
            if(rt.getRecipeID()==recipe.getID()){
                res.add(rt.getIngredientID());
            }
        }
        return res;
    }

    public List<Ingredient> getIngredients(Recipe recipe){
        return getIngredients(getIngredientIds(recipe));
    }

    public int getVolume(Recipe recipe, Ingredient ingredient){
        return get(recipe, ingredient).getVolume();
    }
    public HashMap<Long, Integer> getIngredientIDtoVol(Recipe recipe){
        HashMap<Long, Integer> res = new HashMap<>();
        if(isFast){
            if(this.fastRecipeRecipeIngredient.containsKey(recipe.getID())) {
                for (SQLRecipeIngredient ri : Objects.requireNonNull(this.fastRecipeRecipeIngredient.get(recipe.getID()))) {
                    res.put(ri.getIngredientID(), ri.getVolume());
                }
                return res;
            }
        }
        for(SQLRecipeIngredient ri: this.recipeIngredients){
            if(ri.getRecipeID()==recipe.getID()){
                res.put(ri.getIngredientID(), ri.getVolume());
            }
        }
        return res;
    }
    public HashMap<String, Integer> getIngredientNameToVol(Recipe recipe){
        HashMap<String, Integer> res = new HashMap<>();
        if(isFast){
            if(this.fastRecipeRecipeIngredient.containsKey(recipe.getID())) {
                for (SQLRecipeIngredient ri : Objects.requireNonNull(this.fastRecipeRecipeIngredient.get(recipe.getID()))) {
                    res.put(ri.getIngredient().getName(), ri.getVolume());
                }
                return res;
            }
        }
        for(SQLRecipeIngredient ri: this.recipeIngredients){
            if(ri.getRecipeID()==recipe.getID()){
                res.put(ri.getIngredient().getName(), ri.getVolume());
            }
        }
        return res;
    }
    public HashMap<Ingredient, Integer> getIngredientToVol(Recipe recipe) {
        HashMap<Ingredient, Integer> res = new HashMap<>();
        if(recipe == null){
            return res;
        }
        try{
            if(isFast){
                if(this.fastRecipeRecipeIngredient.containsKey(recipe.getID())) {
                    for (SQLRecipeIngredient ri : Objects.requireNonNull(this.fastRecipeRecipeIngredient.get(recipe.getID()))) {
                        res.put(ri.getIngredient(), ri.getVolume());
                    }
                    return res;
                }
            }
        }catch (NullPointerException e){
            Log.e(TAG, "getIngredientToVol NullPointerException");
            //Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        for(SQLRecipeIngredient ri: this.recipeIngredients){
            if(ri.getRecipeID()==recipe.getID()){
                res.put(ri.getIngredient(), ri.getVolume());
            }
        }
        return res;
    }

    public List<Long> getTopicIDs(Recipe recipe){
        List<Long> res = new ArrayList<>();
        if(isFast){
            res= this.fastRecipeTopics.get(recipe.getID());
            if(res != null){
                return res;
            }else{
                res = new ArrayList<>();
            }
        }
        for(SQLRecipeTopic rt: this.recipeTopics){
            if(rt.getRecipeID()==recipe.getID()){
                res.add(rt.getTopicID());
            }
        }
        return res;

    }

    public List<Topic> getTopics(Recipe recipe){
        return getTopics(getTopicIDs(recipe));
    }

    public SQLRecipeTopic get(Recipe recipe, Topic topic){
        if(isFast){
            if(this.fastRecipeRecipeTopic.containsKey(recipe.getID())){
                List<SQLRecipeTopic> res = this.fastRecipeRecipeTopic.get(recipe.getID());
                if (res != null) {
                    for(SQLRecipeTopic rt: res){
                        if(rt.getTopicID()==topic.getID()){
                            return rt;
                        }
                    }
                }
            }
        }
        for(SQLRecipeTopic rt: recipeTopics){
            if(rt.getTopicID() == topic.getID()){
                if(rt.getRecipeID() == recipe.getID()){
                    return rt;
                }
            }
        }
        return null;
    }

    public void removeFromBuffer(Recipe recipe, Topic topic){
        removeFromBuffer(get(recipe, topic));
    }

    public void removeFromBuffer(SQLRecipeTopic recipeTopic){
        if(recipeTopic == null){
            return;
        }
        this.recipeTopics.remove(recipeTopic);
        if(isFast){
            if(this.fastRecipeTopics.containsKey(recipeTopic.getRecipeID())){
                Objects.requireNonNull(this.fastRecipeTopics.get(recipeTopic.getRecipeID())).remove(recipeTopic.getTopicID());
            }
            //Objects.requireNonNullElse(this.fastRecipeTopics.get(recipeTopic.getRecipeID()), new ArrayList<>()).remove(recipeTopic.getTopicID());

            //Objects.requireNonNull(this.fastRecipeRecipeTopic.get(recipeTopic.getRecipeID())).remove(recipeTopic);
            if(this.fastRecipeRecipeTopic.containsKey(recipeTopic.getRecipeID())){
                Objects.requireNonNull(this.fastRecipeRecipeTopic.get(recipeTopic.getRecipeID())).remove(recipeTopic);
            }
            //Objects.requireNonNull(this.fastTopicRecipeTopic.get(recipeTopic.getTopicID())).remove(recipeTopic);
            if(this.fastTopicRecipeTopic.containsKey(recipeTopic.getTopicID())){
                Objects.requireNonNull(this.fastTopicRecipeTopic.get(recipeTopic.getRecipeID())).remove(recipeTopic);
            }
        }
    }

    public SQLRecipeIngredient get(Recipe recipe, Ingredient ingredient){
        if(isFast){
            if(this.fastRecipeRecipeIngredient.containsKey(recipe.getID())){
                List<SQLRecipeIngredient> res = this.fastRecipeRecipeIngredient.get(recipe.getID());
                if (res != null) {
                    for(SQLRecipeIngredient rt: res){
                        if(rt.getIngredientID()==ingredient.getID()){
                            return rt;
                        }
                    }
                }
            }
        }
        for(SQLRecipeIngredient rt: recipeIngredients){
            if(rt.getIngredientID() == ingredient.getID()){
                if(rt.getRecipeID() == recipe.getID()){
                    return rt;
                }
            }
        }
        return null;
    }

    public void removeFromBuffer(Recipe recipe, Ingredient ingredient){
        removeFromBuffer(get(recipe, ingredient));
    }

    public void removeFromBuffer(SQLRecipeIngredient recipeIngredient){
        /**
         * TODO:
         *
         Process: com.example.cocktailmachine, PID: 18283
         java.lang.NullPointerException: Attempt to invoke virtual method 'long com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient.getRecipeID()' on a null object reference
         at com.example.cocktailmachine.data.db.Buffer.removeFromBuffer(Buffer.java:1153)
         at com.example.cocktailmachine.data.db.DeleteFromDB.remove(DeleteFromDB.java:88)
         at com.example.cocktailmachine.data.db.DeleteFromDB.remove(DeleteFromDB.java:92)
         */
        if(recipeIngredient == null){
            return;
        }
        Long ri_id = recipeIngredient.getID();
        Long r_id = recipeIngredient.getRecipeID();
        Long i_id = recipeIngredient.getIngredientID();
        this.recipeIngredients.remove(recipeIngredient);
        if(isFast){
            if(this.fastRecipeIngredient.containsKey(r_id)) {
                Objects.requireNonNull(this.fastRecipeIngredient.get(r_id)).remove(i_id);
            }if(this.fastRecipeRecipeIngredient.containsKey(r_id)) {
                Objects.requireNonNull(this.fastRecipeRecipeIngredient.get(r_id)).remove(recipeIngredient);
            }if(this.fastIngredientRecipeIngredient.containsKey(i_id)) {
                Objects.requireNonNull(this.fastIngredientRecipeIngredient.get(i_id)).remove(recipeIngredient);
            }
        }
    }














    public void addToBuffer(Pump e){
        this.pumps.add(e);
        if(isFast){
            this.fastIDPump.put(e.getID(),e );
        }
    }
    public void removeFromBuffer(Pump e){
        this.pumps.remove(e);
        if(isFast){
            this.fastIDPump.remove(e.getID());
        }
    }
    /**
     * get pumps from buffer
     * @author Johanna Reidt
     * @return
     */
    public List<Pump> getPumps(){
        if(this.pumps==null){
            this.pumps = new ArrayList<>();
        }
        return this.pumps;
    }

    /**
     * get pumps if empty from db
     * @author Johanna Reidt
     * @param context
     * @return
     */
    public List<Pump> getPumps(Context context)  {
        if(this.pumps == null || this.pumps.isEmpty()) {
            DatabaseConnection.init(context);
            try {
                this.pumps = DatabaseConnection.getSingleton().loadPumps();
            } catch (NotInitializedDBException e) {
                this.pumps = new ArrayList<>();
            }
        }
        return this.pumps;
    }
    /**
     * get Pump with id from buffer
     * @author Johanna Reidt
     * @param id
     */
    @Nullable
    public Pump getPump(long id){
        if(isFast){
            return this.fastIDPump.get(id);
        }
        for(Pump i: this.pumps){
            if(i.getID() ==  id){
                return i;
            }
        }
        return null;
    }


    /**
     * get Pump with needle in name from buffer or if not existent check db
     * @author Johanna Reidt
     * @param context
     * @param id
     */
    @Nullable
    public Pump getPump(Context context, long id){
        Pump res =getPump(id);
        if(res != null){
            return res;
        }
        res= GetFromDB.loadPump(context, id);
        if(res == null){
                return null;
            }
        res.save(context);
        return res;
    }

    /**
     * get pump names as "<ID>:<ingredient name>"
     * @author Johanna Reidt
     * @return
     */
    public List<String> getPumpNames(){
        List<String> names = new ArrayList<>();
        for(Pump i:this.pumps){
            names.add(i.getID()+": "+i.getIngredientName());
        }
        return names;
    }
    public void addToBuffer(SQLIngredientPump e){
        Log.i(TAG, "addToBuffer"+e.toString());
        this.ingredientPumps.add(e);
    }
    public void removeFromBuffer(SQLIngredientPump e){
        this.ingredientPumps.remove(e);
    }
    public List<SQLIngredientPump> getIngredientPumps(){

        if(this.ingredientPumps == null){
            this.ingredientPumps = new ArrayList<>();
        }
        return this.ingredientPumps;
    }
    public SQLIngredientPump getIngredientPump(Long id) {
        if(id == null){
            return null;
        }
        List<SQLIngredientPump> ips = this.getIngredientPumps();
        for(SQLIngredientPump ip: ips){
            if(ip != null) {
                if (ip.getID() == id) {
                    return ip;
                }
            }
        }
        return null;
    }
    public SQLIngredientPump getIngredientPump(Pump pump) {
        if(pump == null){
            return null;
        }
        List<SQLIngredientPump> ips = this.getIngredientPumps();
        for(SQLIngredientPump ip: ips){
            if(ip != null) {
                if (ip.getPumpID() == pump.getID()) {
                    return ip;
                }
            }
        }
        return null;
    }

    public SQLIngredientPump getIngredientPump(Ingredient ingredient){
        if(ingredient == null){
            return null;
        }
        for(SQLIngredientPump ip: this.getIngredientPumps()){
            if(ip != null){
                if(ip.getIngredientID() == ingredient.getID()){
                    return ip;
                }
            }
        }
        return null;
    }

    public void deleteDoublePumpSettingsAndNulls(Context context){
        List<Long> pump_ids = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        Iterator<SQLIngredientPump> it = this.getIngredientPumps().iterator();
        while (it.hasNext()){
            if(it.next() == null){
                it.remove();
            }
        }
        for(SQLIngredientPump ip: this.getIngredientPumps()){
            if(ip != null){
                if(!pump_ids.contains(ip.getPumpID())){
                    pump_ids.add(ip.getPumpID());
                    continue;
                }
                toDelete.add(ip.getID());
            }
        }
        for(Long id: toDelete){
            this.getIngredientPump(id).delete(context);
        }
    }










    public void addToBuffer(SQLRecipeTopic e){
        this.recipeTopics.add(e);
        if(isFast){
            if(this.fastRecipeTopics.containsKey(e.getRecipeID())) {
                Objects.requireNonNull(this.fastRecipeTopics.get(e.getRecipeID())).add(e.getTopicID());
            }else {
                ArrayList<Long> ids = new ArrayList<>();
                ids.add(e.getTopicID());
                this.fastRecipeTopics.put(e.getRecipeID(),ids);
            }if(this.fastTopicRecipes.containsKey(e.getTopicID())){
                Objects.requireNonNull(this.fastTopicRecipes.get(e.getTopicID())).add(e.getRecipeID());
            }else{
                ArrayList<Long> ids = new ArrayList<>();
                ids.add(e.getRecipeID());
                this.fastTopicRecipes.put(e.getTopicID(), ids);
            }
        }
    }
    public List<SQLRecipeTopic> getRecipeTopics(){
        if(this.recipeTopics == null){
            this.recipeTopics = new ArrayList<>();
        }
        return this.recipeTopics;
    }

    public List<SQLRecipeTopic> getRecipeTopics(Recipe recipe){
        if(isFast){
            if(this.fastRecipeRecipeTopic.containsKey(recipe.getID())){
                return this.fastRecipeRecipeTopic.get(recipe.getID());
            }
        }
        List<SQLRecipeTopic> rts = new ArrayList<>();
        for(SQLRecipeTopic rt: this.getRecipeTopics()){
            if(rt.getRecipeID()==recipe.getID()){
                rts.add(rt);
            }
        }
        return rts;
    }









    public void addToBuffer(SQLRecipeIngredient e){
        if(e == null){
            return;
        }
        this.recipeIngredients.add(e);
        if(isFast){
           if(!this.fastRecipeIngredient.containsKey(e.getRecipeID())){
               List<Long> temp = new ArrayList<>();
               this.fastRecipeIngredient.put(e.getRecipeID(), temp);
           }
           Objects.requireNonNull(this.fastRecipeIngredient.get(e.getRecipeID())).add(e.getIngredientID());
           if(!this.fastRecipeRecipeIngredient.containsKey(e.getRecipeID())){
               List<SQLRecipeIngredient> temp = new ArrayList<>();
               this.fastRecipeRecipeIngredient.put(e.getRecipeID(), temp);
           }
           Objects.requireNonNull(this.fastRecipeRecipeIngredient.get(e.getRecipeID())).add(e);
           if(!this.fastIngredientRecipeIngredient.containsKey(e.getIngredientID())){
               List<SQLRecipeIngredient> temp = new ArrayList<>();
               this.fastIngredientRecipeIngredient.put(e.getIngredientID(), temp);
           }
           Objects.requireNonNull(this.fastIngredientRecipeIngredient.get(e.getIngredientID())).add(e);
        }
    }
    public List<SQLRecipeIngredient> getRecipeIngredients(){
        if(this.recipeIngredients == null){
            this.recipeIngredients = new ArrayList<>();
        }
        return this.recipeIngredients;
    }
    public List<SQLRecipeIngredient> getRecipeIngredients(Recipe recipe){
        if(isFast){
            if(this.fastRecipeRecipeIngredient.containsKey(recipe.getID())){
                return this.fastRecipeRecipeIngredient.get(recipe.getID());
            }
        }
        List<SQLRecipeIngredient> ris = new ArrayList<>();
        for(SQLRecipeIngredient ri: this.getRecipeIngredients()){
            if(ri.getRecipeID()==recipe.getID()) {
                ris.add(ri);
            }
        }
        return ris;
    }




    public void loadAvailableIngredient(Context context) {
        if(isFast){
            this.fastAvailableIngredient = new ArrayList<>();
            this.fastIDAvailableIngredient = new HashMap<>();
            this.fastNameAvailableIngredient = new HashMap<>();
        }
        /*
        if(isFast){
            this.fastAvailableIngredient = new ArrayList<>();
            this.fastIDAvailableIngredient = new HashMap<>();
            this.fastNameAvailableIngredient = new HashMap<>();
        }
        for(Ingredient i: this.ingredients){
            i.loadAvailable(context);
            if(i.isAvailable()){
                addAvailableToFast(i);
            }
        }

         */
        this.ingredients = new ArrayList<>();
        for(SQLIngredientPump ip: this.ingredientPumps){
            Ingredient i = GetFromDB.loadIngredient(
                    context,ip.getIngredientID());
            this.ingredients.add(i);
            addAvailableToFast(i);
        }
    }

    private void loadAvailableRecipeIngredient(Context context) {
        this.recipeIngredients =
                GetFromDB.loadRecipeIngredientFromIngredient(
                        context,
                        this.getIngredientIDs());
    }

    private void addAvailableToFast(Ingredient ingredient){
        if(isFast){
            if(this.fastAvailableIngredient == null) {
                this.fastAvailableIngredient = new ArrayList<>();
            }
            if(this.fastIDAvailableIngredient == null) {
                this.fastIDAvailableIngredient = new HashMap<>();
            }
            if(this.fastNameAvailableIngredient == null) {
                this.fastNameAvailableIngredient = new HashMap<>();
            }
            this.fastAvailableIngredient.add(ingredient);
            this.fastIDAvailableIngredient.put(ingredient.getID(), ingredient);
            this.fastNameAvailableIngredient.put(ingredient.getName(), ingredient);
        }
    }

    public void loadAvailableRecipe(Context context) {
        this.recipes = new ArrayList<>();
        if(this.recipeIngredients == null){
            this.recipeIngredients = new ArrayList<>();
        }
        for(SQLRecipeIngredient ri: this.recipeIngredients){
            Recipe i = GetFromDB.loadRecipe(context,
                    ri.getRecipeID());
            this.recipes.add(i);
            addAvailableToFast(i);
        }
    }


    private void addAvailableToFast(Recipe e){
        if(isFast){
            if(this.fastAvailableRecipe == null) {
                this.fastAvailableRecipe = new ArrayList<>();
            }
            if(this.fastIDAvailableRecipe == null) {
                this.fastIDAvailableRecipe = new HashMap<>();
            }
            if(this.fastNameAvailableRecipe == null) {
                this.fastNameAvailableRecipe = new HashMap<>();
            }
            this.fastAvailableRecipe.add(e);
            this.fastIDAvailableRecipe.put(e.getID(), e);
            this.fastNameAvailableRecipe.put(e.getName(), e);
        }
    }

    public void loadRecipeTopics(Context context){
        this.recipeTopics = GetFromDB.loadRecipeTopics(context, this.recipes);
        if(isFast){
            for(SQLRecipeTopic rt: this.recipeTopics){
                addToFast(rt);
            }
        }
    }

    private void addToFast(SQLRecipeTopic rt){
        if(isFast){
            if(this.fastRecipeRecipeTopic == null){
                this.fastRecipeRecipeTopic = new HashMap<>();
            }
            if(!this.fastRecipeRecipeTopic.containsKey(rt.getRecipeID())){
                this.fastRecipeRecipeTopic.put(rt.getRecipeID(), new ArrayList<>());
            }
            Objects.requireNonNull(this.fastRecipeRecipeTopic.get(rt.getRecipeID())).add(rt);


            if(this.fastRecipeTopics == null){
                this.fastRecipeTopics = new HashMap<>();
            }
            if(!this.fastRecipeTopics.containsKey(rt.getRecipeID())){
                this.fastRecipeTopics.put(rt.getRecipeID(), new ArrayList<>());
            }
            Objects.requireNonNull(this.fastRecipeTopics.get(rt.getRecipeID())).add(rt.getTopicID());


            if(this.fastTopicRecipeTopic == null){
                this.fastTopicRecipeTopic = new HashMap<>();
            }
            if(!this.fastTopicRecipeTopic.containsKey(rt.getTopicID())){
                this.fastTopicRecipeTopic.put(rt.getRecipeID(), new ArrayList<>());
            }
            Objects.requireNonNull(this.fastRecipeTopics.get(rt.getRecipeID())).add(rt.getRecipeID());

        }
    }


















    //Setup

    private static void setUpEmptyPumps(Context context) {
        Log.i(TAG, "setUpEmptyPumps");
        Buffer.getSingleton().noMemory();
        DatabaseConnection.init(context).setUpEmptyPumps(); //delete all pump Tables to be sure
        //no local refresh Buffer.localRefresh(context);
    }

    private void emptyUpPumps(Context context) {
        Log.i(TAG, "emptyUpPumps");
        if(this.pumps == null) {
            this.pumps = new ArrayList<>();
        }else{
            List<Pump> g = new ArrayList<>();
            for (Pump temp : this.pumps) {
                temp.empty(context);
                //temp.delete(context);
                //it.remove();
                //it.remove();
            }
            //DatabaseConnection.init(context).emptyUpPumps();
        }

        if(isFast){
            for(Ingredient i: this.fastAvailableIngredient){
                i.empty(context);
            }
            for(Recipe r: this.fastAvailableRecipe){
                r.loadAvailable(context);
            }
            this.fastAvailableIngredient = new ArrayList<>();
            this.fastAvailableRecipe = new ArrayList<>();
        }else{
            for(Ingredient i: this.getIngredients()){
                i.empty(context);
            }
            for(Recipe r: this.getRecipes()){
                r.loadAvailable(context);
            }
        }
        for(SQLIngredientPump ip: this.getIngredientPumps()){
            ip.delete(context);
        }
        this.ingredientPumps = new ArrayList<>();
    }
























    public void available(Recipe recipe, boolean available) {
        if (isFast) {
            if(available) {
                if (!this.fastIDAvailableRecipe.containsKey(recipe.getID())) {
                    this.fastAvailableRecipe.add(recipe);
                    this.fastIDAvailableRecipe.put(recipe.getID(), recipe);
                }
                return;
            }
            if(this.fastIDAvailableRecipe.containsKey(recipe.getID())){
                this.fastIDAvailableRecipe.remove(recipe.getID());
                this.fastAvailableRecipe.remove(recipe);
            }
        }
    }

    public void available(Ingredient ingredient, boolean available) {
        if (isFast) {
            if(available) {
                if (!this.fastIDAvailableIngredient.containsKey(ingredient.getID())) {
                    this.fastAvailableIngredient.add(ingredient);
                    this.fastIDAvailableIngredient.put(ingredient.getID(), ingredient);
                }
                return;
            }
            if(this.fastIDAvailableIngredient.containsKey(ingredient.getID())){
                this.fastIDAvailableIngredient.remove(ingredient.getID());
                this.fastAvailableIngredient.remove(ingredient);
            }
        }
    }


    public Pump getPumpWithSlot(int slot) {
        for(Pump p: this.getPumps()){
            if(p.getSlot() == slot){
                return p;
            }
        }
        return null;
    }
}
