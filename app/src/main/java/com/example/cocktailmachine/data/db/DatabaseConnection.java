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
//import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class DatabaseConnection extends SQLiteOpenHelper {
    private static DatabaseConnection singleton = null;
    private static final String DBname = "DB.db";
    private static final String TAG = "DatabaseConnection";
    private static int version = 2;
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static boolean loading = false;


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

    public static boolean isDBFileExistentAndNotLoading(Context context) {
        return checkDataBaseFile(context) && !loading;
    }



    /**
     * checks if singleton is not null
     * @author Johanna Reidt
     * @return
     */
    static synchronized boolean isInitialized(){
       // Log.v(TAG, "is_initialized");
        return DatabaseConnection.singleton != null;
    }

    /**
     * init new DB singelton, close if old still running
     * @author Johanna Reidt
     * @param context
     * @return
     */
    static synchronized DatabaseConnection init(Context context){
       // Log.v(TAG, "init");
        if(DatabaseConnection.isInitialized()){
            DatabaseConnection.singleton.close();
        }
        DatabaseConnection.singleton = new DatabaseConnection(context);
        return  DatabaseConnection.singleton;
    }

    /**
     * checks if db file exists
     * @author Johanna Reidt
     * @param context
     * @return
     */
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

    /**
     * copies prepared DB file from res
     * @author Johanna Reidt
     * @param context
     */
    private static void copyFromAssets(Context context){
        Log.i(TAG, "copyFromAssets");
        Log.i(TAG, "copyFromAssets: do runnables");
        Runnable r = () -> {
            copyFromAssetsDirect(context);
        };
        try {
            getSingleton().doOnDB(context,r);
        } catch (NotInitializedDBException e) {
            Log.e(TAG, "copyFromAssets: error: ", e);
        }
    }


    /**
     * copies prepared DB file from res
     * @author Johanna Reidt
     * @param context
     */
    private static void copyFromAssetsDirect(Context context){

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

        Log.i(TAG, "copyFromAssetsDirect");

            String db_path;
            //db_path = context.getFilesDir().getPath()+DBname;//"/databases/";
            db_path = "/data/data/" + context.getPackageName() + "/databases/";
            try {
                File dir = new File(db_path);
                if (dir.mkdirs()) {
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
                loading = false;
                Log.i(TAG, "copyFromAssetsDirect: copying done");
            } catch (IOException e) {
                // Log.v(TAG, "copyFromAssets: copying interrrupted Error");
                Log.e(TAG, "copyFromAssetsDirect: error", e);
                loading = false;
                // Log.getStackTraceString(e);
                //throw new RuntimeException(e);
            }
    }

    /**
     * checks if DB file exist, if not copy preped from res
     * @author Johanna Reidt
     * @param context
     */
    public static void loadIfNotDoneDBFromAssets(Context context){
        //InputStream inputStream = context.getAssets().open(DBname);
        if(!checkDataBaseFile(context)){
            Log.i(TAG, "loadIfNotDoneDBFromAssets: has NO DB");
            Log.i(TAG, "loadIfNotDoneDBFromAssets: start copy");
            copyFromAssets(context);
            return;
        }
        Log.i(TAG, "loadIfNotDoneDBFromAssets: has DB");
    }

    public static Runnable toLoadIfNotDoneDBFromAssets(Context context) {
        return () ->
        {
            if (!checkDataBaseFile(context)) {
                Log.i(TAG, "loadIfNotDoneDBFromAssets: has NO DB");
                Log.i(TAG, "loadIfNotDoneDBFromAssets: start copy");
                copyFromAssetsDirect(context);
                return;
            }
            Log.i(TAG, "loadIfNotDoneDBFromAssets: has DB");
        };
    }

    /**
     * load liquid csv
     * @author Johanna Reidt
     * @param context
     */
    private static void loadLiquid(Context context){
       // Log.v(TAG, "loadLiquid" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
       Log.i(TAG,"loadLiquid: start" );
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
                Log.i(TAG,"loadLiquid: done" );
                Toast.makeText(context, "Zutaten geladen!", Toast.LENGTH_SHORT).show();
                return;
       } catch (FileNotFoundException e) {
                Log.i(TAG,"loadLiquid: file not found" );
                Log.e(TAG, "loadLiquid: error: ",e);
                // Log.e(TAG, "error", e);
       } catch (IOException e) {
                Log.i(TAG,"loadLiquid: io error" );
                Log.e(TAG, "loadLiquid: error: ",e);
                // Log.e(TAG, "error", e);
       }
       Log.i(TAG,"loadLiquid: end" );
       Toast.makeText(context, "Fehler beim Laden von Zutaten!", Toast.LENGTH_SHORT).show();
    }

    /**
     * load recipe json
     * @author Johanna Reidt
     * @param context
     */
    private static void loadPrepedRecipes(Context context){
       // Log.v(TAG, "loadPrepedRecipes" );
        //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app

        Log.i(TAG,"loadPrepedRecipes: start" );
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

            Toast.makeText(context, "Rezepte geladen!", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"loadPrepedRecipes: end" );
            return;

        } catch (JSONException e) {
            Log.i(TAG,"loadPrepedRecipes: JSONException" );
            Log.e(TAG, "loadPrepedRecipes:error: ",e);
        } catch (FileNotFoundException e) {
            Log.i(TAG,"loadPrepedRecipes: no file" );
            Log.e(TAG, "loadPrepedRecipes:error: ",e);
            // Log.e(TAG, "error", e);
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG,"loadPrepedRecipes: UnsupportedEncodingException" );
            Log.e(TAG, "loadPrepedRecipes: error: ",e);
            // Log.e(TAG, "error", e);
        } catch (IOException e) {
            Log.i(TAG,"loadPrepedRecipes: IOException" );
            Log.e(TAG, "loadPrepedRecipes: error: ",e);
            // Log.e(TAG, "error", e);
        }
        Log.i(TAG,"loadPrepedRecipes: end" );
        Toast.makeText(context, "Fehler beim Laden von Rezepten!", Toast.LENGTH_SHORT).show();
    }

    /**
     * load csv files instead of preped DB
     * @author Johanna Reidt
     * @param context
     */
    static void loadFromCSVFiles(Context context){
        if(context == null){
            // Log.v(TAG, "copyFromAssets: no context to work with");
            Log.e(TAG,"loadFromCSVFiles: NO CONTEXT");
            return;
        }
        Log.i(TAG,"loadFromCSVFiles" );

        List<Runnable> runnables = new LinkedList<>();
        runnables.add(() -> DatabaseConnection.loadLiquid(context));
        runnables.add(() -> DatabaseConnection.loadPrepedRecipes(context));
        try {
            getSingleton().doOnDB(context, runnables);
        } catch (NotInitializedDBException e) {
            Log.e(TAG, "loadFromCSVFiles: error:", e);
        }


    }



    public void loadDummy(Context context) throws NotInitializedDBException, MissingIngredientPumpException {

        if(Dummy.isDummy) {
            if(!Dummy.withSetCalibration) {
                this.loadForSetUp();
                BasicRecipes.loadPumps(context);
            }
            //DatabaseConnection.singleton.emptyAll();
            BasicRecipes.loadTopics(context);
            BasicRecipes.loadIngredients(context);

            BasicRecipes.loadMargarita(context);
            BasicRecipes.loadLongIslandIceTea(context);


        }
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
        this.ingredients = new LinkedList<>();
        this.ingredientPumps = new LinkedList<>();
        this.topics = new LinkedList<>();
        this.pumps = new LinkedList<>();
        this.recipeIngredients = new LinkedList<>();
        this.recipes = new LinkedList<>();
    }


     */
    /**
     * complety deletes table with ingredient pump connection
     */
    void emptyUpPumps() {
       // Log.v(TAG, "emptyUpPumps");
        try {
            Tables.TABLE_INGREDIENT_PUMP.deleteTable(this.getWritableDatabase());
        }catch (Exception e){
            Log.e(TAG,"emptyUpPumps", e);
        }
        //Tables.TABLE_INGREDIENT_PUMP.createTable(this.getWritableDatabase());
    }

    /**
     * completly delete ingeredient pump table and pump table and create empty
     * @author Johanna Reidt
     */
    void setUpEmptyPumps() {
       // Log.v(TAG, "setUpEmptyPumps");
        this.emptyUpPumps();
        try {
            Tables.TABLE_PUMP.deleteTable(this.getWritableDatabase());
        }catch (Exception e){
            Log.e(TAG,"setUpEmptyPumps", e);
        }
        //Tables.TABLE_PUMP.createTable(this.getWritableDatabase());
        try {
            Tables.TABLE_NEW_PUMP.deleteTable(this.getWritableDatabase());
        }catch (Exception e){
            Log.e(TAG,"setUpEmptyPumps", e);
        }
        Tables.TABLE_NEW_PUMP.createTable(this.getWritableDatabase());
    }


















    //LOAD

    void loadForSetUp() {
       // Log.v(TAG, "loadForSetUp");
        this.setUpEmptyPumps();
        //this.ingredients = this.loadAllIngredients();
    }







    //DOs
    void doOnDB(Context context, List<Runnable> runnables){
        if(context == null){
            // Log.v(TAG, "copyFromAssets: no context to work with");
            return;
        }
        //loading = true;
        // Log.w(TAG, "copyFromAssets: copying neccessary");
        ExecutorService pool = Executors.newFixedThreadPool(1);;
        //Future current;

        Runnable wait = () -> {
            Log.i(TAG, "doOnDB: wait:");
            while(loading){
                try {
                    TimeUnit.SECONDS.sleep(1);
                    Log.i(TAG, "doOnDB: wait: waited 1 Second");
                } catch (InterruptedException e) {
                    Log.e(TAG, "doOnDB: wait: error", e);
                }
            }
            loading = true;
        };
        pool.submit(wait);
        Runnable done = () -> {
            Log.i(TAG, "doOnDB: done:");
            pool.shutdown();
            Log.i(TAG, "doOnDB: pool shutted done:");
        };

        for(Runnable r: runnables) {
            pool.submit(r);
        }

        pool.submit(done);
    }

    void doOnDB(Context context, Runnable r){
        List<Runnable> runnables = new LinkedList<>();
        runnables.add(r);
        this.doOnDB(context, runnables);
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
        Log.i(TAG, "onUpgrade");
        if(oldVersion == DatabaseConnection.version) {
            /*
            for (String deleteCmd : Tables.getDeleteCmds()) {
               db.execSQL(deleteCmd);
            }

             */
            Tables.deleteAll(db);
            onCreate(db);
            DatabaseConnection.version = newVersion;
            //DatabaseConnection.copyFromAssets(db);
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
