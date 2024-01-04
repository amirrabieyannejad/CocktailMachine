package com.example.cocktailmachine.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.BasicRecipes;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredientImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipe;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.tables.Tables;
import com.opencsv.CSVReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class DatabaseConnection extends SQLiteOpenHelper {
    private static DatabaseConnection singleton = null;
    private static final String DBname = "DB.db";
    private static final String TAG = "DatabaseConnection";
    private static int version = 1;
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static boolean loaded = false;


    private DatabaseConnection(@Nullable Context context) {
        super(context,
                DatabaseConnection.DBname,
                DatabaseConnection.factory,
                DatabaseConnection.version);

       // Log.v(TAG, "DatabaseConnection");
    }

    static synchronized DatabaseConnection getSingleton() throws NotInitializedDBException {

       // Log.v(TAG, "getSingleton");
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
       // Log.v(TAG, "is_initialized");
        return DatabaseConnection.singleton != null;
    }

    static synchronized DatabaseConnection init(Context context){
       // Log.v(TAG, "init");
        if(DatabaseConnection.isInitialized()){
            DatabaseConnection.singleton.close();
        }
        DatabaseConnection.singleton = new DatabaseConnection(context);
        return  DatabaseConnection.singleton;
    }

    static boolean checkDataBaseFile(Context context) {
        String db_path;
        if(context!=null) {
            db_path = "/data/data/" + context.getPackageName() + "/databases/";
        }else{
            db_path = null;
        }
        File dbFile = new File(db_path + DBname);
       // Log.v(TAG, "checkDataBaseFile: DBFILE existence: "+dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    private static void copyFromAssets(Context context){

        //InputStream inputStream = context.getResources().openRawResource(R.raw.prepared);
        /*
        InputStream mInput = mContext.getAssets().open(DB_NAME);
                String outFileName = DB_PATH + DB_NAME;
                OutputStream mOutput = new FileOutputStream(outFileName);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = mInput.read(mBuffer))>0)
                {
                    mOutput.write(mBuffer, 0, mLength);
                }
                mOutput.flush();
                mOutput.close();
                mInput.close();
         */
        if(context == null){
           // Log.v(TAG, "copyFromAssets: no context to work with");
            return;
        }
       // Log.w(TAG, "copyFromAssets: copying neccessary");
        String db_path;
        //db_path = context.getFilesDir().getPath()+DBname;//"/databases/";
        db_path = "/data/data/" + context.getPackageName() + "/databases/";
        try {
            File dir = new File(db_path);
            if(dir.mkdirs()){
               // Log.v(TAG, "copyFromAssets: mkdirs was needed and successful");
            }
            InputStream myinput = context.getResources().openRawResource(R.raw.prepared);
            //context.getAssets().open(DBname);
            String outfilename = db_path + DBname;
           // Log.v(TAG, "DB Path : " + outfilename);
            OutputStream myoutput = new FileOutputStream(outfilename);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer)) > 0) {
                myoutput.write(buffer, 0, length);
            }
            myoutput.flush();
            myoutput.close();
            myinput.close();
           // Log.v(TAG, "copyFromAssets: copying done");
        } catch (IOException e) {
           // Log.v(TAG, "copyFromAssets: copying interrrupted Error");
            Log.e(TAG, "error", e);
           // Log.getStackTraceString(e);
            //throw new RuntimeException(e);
        }
    }

    public static void loadIfNotDoneDBFromAssets(Context context){
        //InputStream inputStream = context.getAssets().open(DBname);

        if(!checkDataBaseFile(context)){
            copyFromAssets(context);
        }
    }


    /**
     * load liquid csv
     * @author Johanna Reidt
     * @param context
     */
    static void loadLiquid(Context context){
       // Log.v(TAG, "loadLiquid" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
        try {
            /*
            //URL path = Buffer.class.getResource("liquid.csv");
            //URL path = ClassLoader.getSystemResource("liquid.csv");
            //Log.v(TAG, "loadLiquid: url path: "+path );
            //if(path == null){
            //   // Log.v(TAG, "loadLiquid: path is null" );
            //    return;
            //}
            //File f = new File(path.getFile());
            //File csvfile = new File("liquid.csv");

           // Log.v(TAG, "loadLiquid: "+ClassLoader.getSystemClassLoader().toString());
           // Log.v(TAG, "loadLiquid: "+Buffer.class);
           // Log.v(TAG, "loadLiquid: "+Buffer.class.getPackage());
           // Log.v(TAG, "loadLiquid: "+Buffer.class.getPackage());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();
               // Log.v(TAG, "loadLiquid: "+"Current absolute path is: " + s);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Path path = FileSystems.getDefault().getPath(".");
               // Log.v(TAG, "loadLiquid: "+"Current dir " + path);
                path = FileSystems.getDefault().getPath(".").toAbsolutePath();
               // Log.v(TAG, "loadLiquid: "+"Current absolute path is: " + path);
            }
            //String cwd = Path.of("").toAbsolutePath().toString();
            File currentDir = new File("");
           // Log.v(TAG, "loadLiquid: "+"Current dir: " + currentDir);
            currentDir = new File("").getAbsoluteFile();
           // Log.v(TAG, "loadLiquid: "+"Current dir, absolute path: " + currentDir);
            File[] files = currentDir.listFiles();
            if(files != null) {
               // Log.v(TAG, "loadLiquid: Size: " + files.length);
                for (File file : files) {
                   // Log.v(TAG, "loadLiquid: FileName:" + file.getName());
                }
            }else{
               // Log.v(TAG, "loadLiquid: no files in dir");
            }

             */
            /*
           // Log.v(TAG, "loadLiquid: data "+Environment.getDataDirectory());
           // Log.v(TAG, "loadLiquid: ex storage "+Environment.getExternalStorageState());
           // Log.v(TAG, "loadLiquid: root "+Environment.getRootDirectory());
           // Log.v(TAG, "loadLiquid: download "+Environment.getDownloadCacheDirectory());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
               // Log.v(TAG, "loadLiquid: storage "+Environment.getStorageDirectory());
            }

             */
            /*
            File currentDir = context.getFilesDir();
           // Log.v(TAG, "loadLiquid: currentDir "+currentDir);


             */
            /*
            File currentDir = new File("./");
           // Log.v(TAG, "loadLiquid: "+"Current dir: " + currentDir);
            //currentDir = new File("./").getAbsoluteFile();
           // Log.v(TAG, "loadLiquid: "+"Current dir, absolute path: " + currentDir);

             */
            /*
            File[] files = currentDir.listFiles();
            if(files != null) {
               // Log.v(TAG, "loadLiquid: Size: " + files.length);
                for (File file : files) {
                   // Log.v(TAG, "loadLiquid: FileName:" + file.getName());
                }
            }else{
               // Log.v(TAG, "loadLiquid: no files in dir");
            }

            String[] file_names = context.fileList();
           // Log.v(TAG, "loadLiquid: fileList: "+ Arrays.toString(file_names));
           // Log.v(TAG, "loadLiquid: getPackageCodePath: "+ context.getPackageCodePath());
           // Log.v(TAG, "loadLiquid: getPackageResourcePath: "+ context.getPackageResourcePath());

             */
            InputStream is = context.getResources().openRawResource(R.raw.liquid);
           // Log.v(TAG, "loadLiquid: get raw: "+ is.toString());

            //File csvfile = new File("./liquid.csv");
            //Log.v(TAG, "loadLiquid: file: "+csvfile );
            //new File(Environment.getExternalStorageDirectory() + "/csvfile.csv");
            //CSVReader reader = new CSVReader(new FileReader(csvfile));
            CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            //CSVReader reader = new CSVReader(new FileReader(f.getAbsolutePath()));
           // Log.v(TAG, "loadLiquid: opening file successful");
            String[] nextLine;
            reader.readNext();//skip first line
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //System.out.println(nextLine[0] + nextLine[1] + "etc...");
               // Log.v(TAG, "loadLiquid: next line: "+ Arrays.toString(nextLine));
                String name = nextLine[0];
                boolean alcoholic = false;
                int colour = new Random().nextInt();
                try {
                    alcoholic = Integer.parseInt(nextLine[1]) == 1;
                }catch (NumberFormatException e){
                   // Log.v(TAG, "loadLiquid: failed to read alcoholic, alcoholic set to false");
                    Log.e(TAG, "error: ",e);
                    // Log.e(TAG, "error", e);
                }
                try {
                    colour = Integer.parseInt(nextLine[2]);
                }catch (NumberFormatException e){
                   // Log.v(TAG, "loadLiquid: failed to read colour, use random");
                    Log.e(TAG, "error: ",e);
                    // Log.e(TAG, "error", e);
                }
                Ingredient.makeNew(name, alcoholic, colour).save(context);
            }
            reader.close();
            is.close();
        } catch (FileNotFoundException e) {
           // Log.v(TAG,"loadLiquid: file not found" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        } catch (IOException e) {
           // Log.v(TAG,"loadLiquid: io error" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        }
        Toast.makeText(context, "Zutaten geladen!", Toast.LENGTH_SHORT).show();

    }

    /**
     * load recipe json
     * @author Johanna Reidt
     * @param context
     */
    static void loadPrepedRecipes(Context context){
       // Log.v(TAG, "loadPrepedRecipes" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
        try {

            InputStream is = context.getResources().openRawResource(R.raw.recipe);
           // Log.v(TAG, "loadPrepedRecipes: get raw: "+ is.toString());
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
           // Log.v(TAG, "loadPrepedRecipes: opening file successful");

            Iterator<String> names =  json.keys();
            while(names.hasNext()){
                String name = names.next();
               // Log.v(TAG, "loadPrepedRecipes: next recipe: "+name);
                Recipe r = Recipe.searchOrNew(context,name);
                r.removeIngredients(context, r.getIngredients(context));
                JSONObject ingVol = json.getJSONObject(name);
                Iterator<String> ings = ingVol.keys();
                while(ings.hasNext()){
                    String ingName = ings.next();
                   // Log.v(TAG, "loadPrepedRecipes: next recipe: add ing "+ingName);
                    r.add(context,
                            Ingredient.searchOrNew(context, ingName), //gets ing or new
                            ingVol.optInt(ingName));//given vol or 0
                    r.save(context);
                }
               // Log.v(TAG, "loadPrepedRecipes: next recipe: "+name+" done saving");
                r.save(context);

            }


        } catch (JSONException e) {
           // Log.v(TAG,"loadLiquid: JSONException" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        } catch (FileNotFoundException e) {
           // Log.v(TAG,"loadLiquid: no file" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        } catch (UnsupportedEncodingException e) {
           // Log.v(TAG,"loadLiquid: UnsupportedEncodingException" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        } catch (IOException e) {
           // Log.v(TAG,"loadLiquid: IOException" );
            Log.e(TAG, "error: ",e);
            // Log.e(TAG, "error", e);
        }
        Toast.makeText(context, "Rezepte geladen!", Toast.LENGTH_SHORT).show();
    }












    //NewDatabaseConnection Overrides

    /**
     * deletes all Tables and creates all newly empty
     * @author Johanna Reidt
     */
    void emptyAll(){
       // Log.v(TAG, "emptyAll");
        //resetAll();
        Tables.deleteAll(this.getWritableDatabase());
        Tables.createAll(this.getWritableDatabase());
    }
    /*
    private void resetAll(){
       // Log.v(TAG, "resetAll");
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
       // Log.v(TAG, "emptyUpPumps");
        Tables.TABLE_INGREDIENT_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_INGREDIENT_PUMP.createTable(this.getWritableDatabase());
    }

    void setUpEmptyPumps() {
       // Log.v(TAG, "setUpEmptyPumps");
        this.emptyUpPumps();
        Tables.TABLE_PUMP.deleteTable(this.getWritableDatabase());
        Tables.TABLE_PUMP.createTable(this.getWritableDatabase());
    }


















    //LOAD



    void loadForSetUp() {
       // Log.v(TAG, "loadForSetUp");
        this.setUpEmptyPumps();
        //this.ingredients = this.loadAllIngredients();
    }

    /**
     * Loads all ingredients from db
     * @author Johanna Reidt
     * @return
     */
    List<Ingredient> loadAllIngredients() {
       // Log.v(TAG, "loadAllIngredients");

        List<? extends Ingredient> res = Tables.TABLE_INGREDIENT.getAllElements(this.getReadableDatabase());
        return (List<Ingredient>) res;
    }



    /**
     * loads all recipes from db
     * @author Johanna Reidt
     * @return
     */
    List<Recipe> loadAllRecipes() {
       // Log.v(TAG, "loadAllRecipes");
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
       // Log.v(TAG, "loadIngredientPumps");
        // return IngredientTable.
        return Tables.TABLE_INGREDIENT_PUMP.getAllElements(this.getReadableDatabase());
    }

    /**
     * loads pumps from db
     * @author Johanna Reidt
     * @return
     */
    List<Pump> loadPumps() {
       // Log.v(TAG, "loadPumps");
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
       // Log.v(TAG, "loadTopics");
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
       // Log.v(TAG, "loadIngredientVolumes");
        //return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);
        return Tables.TABLE_RECIPE_INGREDIENT.getAllElements(this.getReadableDatabase());

    }

    List<SQLRecipeTopic> loadRecipeTopic(){
       // Log.v(TAG, "loadIngredientVolumes");
        //return Tables.TABLE_RECIPE_INGREDIENT.getAvailable(this.getReadableDatabase(), this.recipes);
        return Tables.TABLE_RECIPE_TOPIC.getAllElements(this.getReadableDatabase());
    }














    //GETTER //Not allowed to fetch from database !!!only!!! from buffer unless they are admins





    List<String> getUrls(SQLIngredient newSQLIngredient) {
       // Log.v(TAG, "getUrls");
        return Tables.TABLE_INGREDIENT_URL.getUrls(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    List<SQLIngredientImageUrlElement> getUrlElements(SQLIngredient newSQLIngredient) {
       // Log.v(TAG, "getUrlElements");
        return Tables.TABLE_INGREDIENT_URL.getElements(this.getReadableDatabase(), newSQLIngredient.getID());
    }

    List<String> getUrls(SQLRecipe newSQLRecipe) {
       // Log.v(TAG, "getUrls");
        return Tables.TABLE_RECIPE_URL.getUrls(this.getReadableDatabase(), newSQLRecipe.getID());
    }

    List<SQLRecipeImageUrlElement> getUrlElements(SQLRecipe newSQLRecipe) {
       // Log.v(TAG, "getUrlElements");
        return Tables.TABLE_RECIPE_URL.getElements(this.getReadableDatabase(), newSQLRecipe.getID());
    }



















    // Helper Over rides

    public void onConfigure(SQLiteDatabase db) {
       // Log.v(TAG, "onConfigure");
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        db.enableWriteAheadLogging();
    }

    public void onCreate(SQLiteDatabase db) {
       // Log.v(TAG, "onCreate");
        Tables.createAll(db);
        /*
        for (String createCmd : Tables.getCreateCmds()) {
            db.execSQL(createCmd);
        }

         */


    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // Log.v(TAG, "onUpgrade");
        //db.enableWriteAheadLoggvng();
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
