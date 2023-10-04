package com.example.cocktailmachine.data.db.elements;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.AddOrUpdateToDB;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.ui.model.v2.GetDialog;
import com.example.cocktailmachine.ui.model.v2.WaitingQueueCountDown;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLRecipe extends SQLDataBaseElement implements Recipe {
    private static final String TAG = "SQLRecipe";
    private String name = "";
    //private List<Long> ingredientIds;
    //private HashMap<Long, Integer> ingredientVolume;
    private boolean alcoholic = false;
    private boolean available = true;
    private List<SQLRecipeImageUrlElement> imageUrls = new ArrayList<>();
    //private List<Long> topics = new ArrayList<>();
    //private List<SQLRecipeIngredient> ingredientVolumes = new ArrayList<>();




    private WaitingQueueCountDown waitingQueueCountDown = null;




    public SQLRecipe(String name) {
        super();
        this.name = name;
    }

    public SQLRecipe(long ID,
                     String name,
                     boolean alcoholic,
                     boolean available){
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
    }

    public SQLRecipe(Context context,
                     long ID,
                     String name,
                     HashMap<Ingredient, Integer> ingredientIDtoVolumes,
                     boolean alcoholic,
                     boolean available,
                     List<SQLRecipeImageUrlElement> imageUrls,
                     List<Long> topics) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;

        this.addTopics(context,Buffer.getSingleton(context).getTopics(topics));
        this.addIngredients(context, ingredientIDtoVolumes);
        this.loadAvailable(context);
    }

    public SQLRecipe(Context context,
                     long ID,
                     String name,
                     boolean alcoholic,
                     boolean available,
                     List<SQLRecipeImageUrlElement> imageUrls,
                     List<Long> topics,
                     HashMap<Ingredient, Integer> ingredientVolumes) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;

        this.addTopics(context,Buffer.getSingleton(context).getTopics(topics));
        this.addIngredients(context, ingredientVolumes);
        this.loadAvailable(context);
    }






    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public List<Topic> getTopics() {
        return Buffer.getSingleton().getTopics(this);
    }

    @Override
    public List<Long> getTopicIDs() {
        return Buffer.getSingleton().getTopicIDs(this);
    }

    @Override
    public List<String> getTopicNames() {
        return Buffer.getSingleton().getTopicNames(Buffer.getSingleton().getTopicIDs(this));
    }

    @Override
    public List<SQLRecipeTopic> getRecipeTopic() {
        return Buffer.getSingleton().getRecipeTopics(this);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Buffer.getSingleton().getIngredients(this);
    }

    @Override
    public List<Long> getIngredientIDs() {
        return Buffer.getSingleton().getIngredientIds(this);
    }

    @Override
    public List<String> getIngredientNames() {
        return Buffer.getSingleton().getIngredientNames(Buffer.getSingleton().getIngredientIds(this));
    }

    @Override
    public List<String> getIngredientNameNVolumes() {
        return Buffer.getSingleton().getIngredientNameAndVol(Buffer.getSingleton().getIngredientIds(this));
    }

    @Override
    public HashMap<Ingredient, Integer> getIngredientToVolume() {
        return Buffer.getSingleton().getIngredientToVol(this);
    }

    @Override
    public HashMap<Long, Integer> getIngredientIDToVolume() {
        return Buffer.getSingleton().getIngredientIDtoVol(this);
    }

    @Override
    public HashMap<String, Integer> getIngredientNameToVolume() {
        return Buffer.getSingleton().getIngredientNameToVol(this);
    }

    @Override
    public int getVolume(Ingredient ingredient) {
        return Buffer.getSingleton().getVolume(this, ingredient);
    }

    @Override
    public int getVolume(long ingredientID) {
        return this.getVolume(Ingredient.getIngredient(ingredientID));
    }


    @Override
    public List<SQLRecipeIngredient> getRecipeIngredient() {
        return Buffer.getSingleton().getRecipeIngredients(this);
    }



    @Override
    public void setName(Context context, String name) {
        this.name = name;
        this.wasChanged();
        this.save(context);
    }

    @Override
    public void add(Context context, Topic topic) {
        topic.save(context);
        SQLRecipeTopic st = new SQLRecipeTopic(this.getID(), topic.getID());
        st.save(context);
        AddOrUpdateToDB.addOrUpdate(context, st );
        //Buffer.getSingleton().addToBuffer(st);
    }

    @Override
    public void add(Context context, Ingredient ingredient) {
        this.add(context, ingredient, -1);

    }

    @Override
    public void add(Context context, Ingredient ingredient, int volume) {
        ingredient.save(context);
        SQLRecipeIngredient st = new SQLRecipeIngredient( ingredient.getID(),this.getID(), volume);
        st.save(context);
        AddOrUpdateToDB.addOrUpdate(context, st );
        //Buffer.getSingleton().addToBuffer(st);
        this.alcoholic = this.alcoholic || ingredient.isAlcoholic();
        this.available = this.available && ingredient.isAvailable();
        this.save(context);
    }















    //general
    @Override
    public void delete(Context context) {
        Log.i(TAG, "delete");
        DeleteFromDB.remove(context, this);
    }

    @Override
    public String getClassName() {
        return "SQLRecipe";
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public boolean loadAvailable(Context context) {
        this.available = true;
        for(Ingredient i: this.getIngredients()){
            this.available = this.available && i.isAvailable();
        }
        Buffer.getSingleton().available(this, this.available);
        return this.available;
    }

    @Override
    public boolean loadAlcoholic(Context context) {
        this.alcoholic = false;
        for(Ingredient i: this.getIngredients()){
            this.alcoholic = this.alcoholic || i.isAlcoholic();
        }
        return this.alcoholic;
    }

    @Override
    public void save(Context context) {
        Log.i(TAG, "save");
        AddOrUpdateToDB.addOrUpdate(context, this);
        //Buffer.getSingleton().addToBuffer(this);
    }

    public JSONObject asJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("ID", this.getID());
            json.put("name", this.name);
            json.put("alcoholic", this.alcoholic);
            json.put("available", this.available);
            json.put("imageUrls", this.imageUrls.toString());
            json.put("ingredient", this.getIngredientIDs());
            return json;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }












    @Override
    public WaitingQueueCountDown getWaitingQueueCountDown() {
        return this.waitingQueueCountDown;
    }

    @Override
    public void setWaitingQueueCountDown(Activity activity) {
        final Recipe recipe = this;
        if(this.waitingQueueCountDown != null){
            this.waitingQueueCountDown.cancel();
            this.waitingQueueCountDown = null;
        }
        this.waitingQueueCountDown = new WaitingQueueCountDown(5000) {
            @Override
            public void onTick() {
                Log.i("WaitingQueueCountDown","onTick"+getTick() );
            }

            @Override
            public void reduceTick() {
                Log.i("WaitingQueueCountDown","reduceTick");
                setTick(CocktailMachine.getNumberOfUsersUntilThisUsersTurn(activity,getTick()));
            }

            @Override
            public void onNext() {
                Log.i("WaitingQueueCountDown","onNext");
                //TODO: Notification
                //TODO: ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2,150);
                Toast.makeText(activity, "Der nächste Cocktail ist deiner!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                Log.i("WaitingQueueCountDown","onFinish");

                //TODO: ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,300);
                GetDialog.isUsersTurn(activity, recipe);
            }
        };
        this.waitingQueueCountDown.start();

    }

    @Override
    public void addDialogWaitingQueueCountDown(Activity activity, AlertDialog alertDialog) {
        final Recipe recipe = this;

        if(this.waitingQueueCountDown != null){
            this.waitingQueueCountDown.cancel();
            this.waitingQueueCountDown = null;
        }

        alertDialog.setOnDismissListener(dialog -> {
            setWaitingQueueCountDown(activity);
            dialog.cancel();
        });

        this.waitingQueueCountDown = new WaitingQueueCountDown(5000) {
            @Override
            public void onTick() {
                Log.i("WaitingQueueCountDown","Dialog  onTick"+getTick());
                alertDialog.setMessage("noch: "+getTick());
                Log.i("WaitingQueueCountDown","Dialog  onTick"+getTick());
            }

            @Override
            public void reduceTick() {
                Log.i("WaitingQueueCountDown","Dialog  reduceTick");
                setTick(CocktailMachine.getNumberOfUsersUntilThisUsersTurn(activity,getTick()));
            }

            @Override
            public void onNext() {
                Log.i("WaitingQueueCountDown","Dialog  onNext");
                //TODO: Notification
                Toast.makeText(activity, "Der nächste Cocktail ist deiner!", Toast.LENGTH_LONG).show();
                //TODO: ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2,150);
                //Toast.makeText(activity, "Der nächste Cocktail ist deiner!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                Log.i("WaitingQueueCountDown","Dialog  onFinish");
                //TODO: ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,300);

                /*
                alertDialog.setMessage("Bitte stellen sie ihr Glas unter die Cocktailmaschine!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        "Los!",
                        (dialog, which) -> {
                            GetActivity.goToFill(activity,recipe);
                            dialog.dismiss();
                        });

                 */
                alertDialog.dismiss();
                GetDialog.isUsersTurn(activity, recipe);
            }
        };
        this.waitingQueueCountDown.start();
    }
















    //Comparable
    /**
    @Override
    public boolean equals(Recipe recipe) {
        if(recipe == null){
            return false;
        }
        return this.compareTo(recipe)==0;
    }**/

    @Override
    public boolean equals(@Nullable Object obj) {
        //is instance of
        if(obj instanceof Recipe){
            return this.compareTo((Recipe) obj)==0;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return this.asJSON().toString();
    }

    @Override
    public int compareTo(Recipe o) {
        return Long.compare(this.getID(), o.getID());
    }
}
