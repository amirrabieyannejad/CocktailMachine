package com.example.cocktailmachine.data.db.elements;


import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.AlreadySetIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLRecipe extends SQLDataBaseElement implements Recipe {
    private static final String TAG = "SQLRecipe";
    private String name = "";
    //private List<Long> ingredientIds;
    //private HashMap<Long, Integer> ingredientVolume;
    private boolean alcoholic;
    private boolean available = false;
    private List<SQLRecipeImageUrlElement> imageUrls = new ArrayList<>();
    private List<Long> topics = new ArrayList<>();
    private List<SQLRecipeIngredient> ingredientVolumes = new ArrayList<>();
    private boolean loaded = false;

    public SQLRecipe(String name) {
        super();
        this.name = name;
        this.loaded = true;
    }

    public SQLRecipe(long ID,
                     String name,
                     boolean alcoholic,
                     boolean available){
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        try {
            this.load();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    public SQLRecipe(long ID,
                     String name,
                     HashMap<Long, Integer> ingredientVolumes,
                     boolean alcoholic,
                     boolean available,
                     List<SQLRecipeImageUrlElement> imageUrls,
                     List<Long> topics) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;
        this.topics = topics;
        this.addOrUpdateIDs(ingredientVolumes);
        this.loaded = true;
        this.loadAvailable();
    }

    public SQLRecipe(long ID,
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
        this.topics = topics;
        this.addOrUpdateElements(ingredientVolumes);
        this.loaded = true;
        this.loadAvailable();
    }

    //LOADER


    /**
     * laod recipe ingredient, image urls, topics
     * @throws NotInitializedDBException
     */
    private void load() throws NotInitializedDBException {
        this.imageUrls = DatabaseConnection.getDataBase().getUrlElements(this);
        this.topics = DatabaseConnection.getDataBase().getTopicIDs(this);
        this.ingredientVolumes = DatabaseConnection.getDataBase().getIngredientVolumes(this);
        this.loadAvailable();
        this.loaded = true;
    }




    //GETTER
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Long> getIngredientIds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.ingredientVolumes
                    .stream()
                    .map(SQLRecipeIngredient::getIngredientID)
                    .collect(Collectors.toList());
        }
        return Helper.getrecipeingredienthelper().getIds(this.ingredientVolumes);

    }

    @Override
    public List<String> getIngredientNames() {
        ArrayList<String> names = new ArrayList<>();
        for(SQLRecipeIngredient ri: this.ingredientVolumes){
            names.add(ri.getIngredient().getName());
        }
      return names;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Ingredient.getIngredientWithIds(this.getIngredientIds());
    }

    @Override
    public HashMap<Long, Integer> getIngredientVolumes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (HashMap<Long, Integer>)
                    this.ingredientVolumes.stream()
                            .collect( Collectors
                                    .toMap(SQLRecipeIngredient::getID,
                                            SQLRecipeIngredient::getVolume));
        }
        return Helper.getrecipeingredienthelper().getIngredientVolumes(this.ingredientVolumes);
    }

    @Override
    public List<Map.Entry<String, Integer>> getIngredientNameNVolumes() {
        List<Map.Entry<String, Integer>> res = new ArrayList<>();
        for(SQLRecipeIngredient ri:this.ingredientVolumes){
            res.add(
                    new AbstractMap.SimpleEntry<String, Integer>(
                            ri.getIngredient().getName(),
                            ri.getVolume()));
        }
        return res;
    }

    private List<SQLRecipeIngredient> getRecipeIngredient(long ingredientID)
            throws NoSuchIngredientSettedException, TooManyTimesSettedIngredientEcxception {
        List<SQLRecipeIngredient> res = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            res = this.ingredientVolumes.stream().filter(ri-> ri.getIngredientID()==ingredientID).collect(Collectors.toList());
        }else{
            res = Helper.getrecipeingredienthelper().getWithIdAsList(this.ingredientVolumes, ingredientID);
        }
        if(res.size()==0){
            throw new NoSuchIngredientSettedException(this, ingredientID);
        }else if(res.size()>1){
            throw new TooManyTimesSettedIngredientEcxception(this, ingredientID);
        }
        return res;
    }

    @Override
    public int getSpecificIngredientVolume(long ingredientId)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getRecipeIngredient(ingredientId).get(0).getVolume();
    }

    @Override
    public int getSpecificIngredientVolume(Ingredient ingredient)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getSpecificIngredientVolume(ingredient.getID());
    }

    @Override
    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * check for ingredient pump connection
     * true, if ingredient pump connection exists
     *
     * @return
     */
    @Override
    public boolean loadAvailable() {
        Log.i(TAG, "loadAvailable");
        boolean res = this.privateLoadAvailable();
        if(res != this.available){
            Log.i(TAG, "loadAvailable: has changed: "+res);
            this.available = res;
            this.wasChanged();
        }
        return this.available;
    }

    /**
     * @return if all ingredients available, with sufficient amounts of volume
     */
    boolean privateLoadAvailable(){
        Log.i(TAG, "privateLoadAvailable");

        for(SQLRecipeIngredient i: this.ingredientVolumes){
            i.loadAvailable();
        }
        for(Ingredient i: getIngredients()){
            if(i.isAvailable()){
                try {
                    if(i.getVolume()>this.getSpecificIngredientVolume(i)){
                        Log.i(TAG, "privateLoadAvailable: is available "+i);
                    }else{
                        Log.i(TAG, "privateLoadAvailable: is NOT available "+i);
                        return false;
                    }
                } catch (TooManyTimesSettedIngredientEcxception |
                         NoSuchIngredientSettedException e) {
                    e.printStackTrace();

                    Log.i(TAG, "privateLoadAvailable: is setted multiple times "+i);
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }




    @Override
    public List<String> getImageUrls() {
        Log.i(TAG, "getImageUrls");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.imageUrls.stream().map(SQLImageUrlElement::getUrl).collect(Collectors.toList());
        }
        return Helper.getUrls(this.imageUrls);
    }

    @Override
    public List<Long> getTopics() {
        return this.topics;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    //ADDER
    public void add(Ingredient ingredient, int volume)
            throws AlreadySetIngredientException {
        if(ingredient.getID()==-1L){
            try {
                ingredient.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        this.add(ingredient.getID(), volume);
    }

    public void add(long ingredientId, int volume)
            throws AlreadySetIngredientException {
        if(this.getIngredientIds().contains(ingredientId)){
            throw new AlreadySetIngredientException(this, ingredientId);
        }
        this.ingredientVolumes.add(new SQLRecipeIngredient(ingredientId, this.getID(), volume));
    }

    @Override
    public void addOrUpdate(Ingredient ingredient, int volume) {
        if(ingredient.getID()==-1L){
            try {
                ingredient.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        this.addOrUpdate(ingredient.getID(), volume);
    }

    @Override
    public void addOrUpdate(long ingredientId, int volume) {

        boolean newNeeded=true;
        for(SQLRecipeIngredient ri: ingredientVolumes){
            if(ri.getIngredientID()==ingredientId){
                ri.setVolume(volume);
                newNeeded = false;
            }
        }
        if(newNeeded){
            SQLRecipeIngredient ri = new SQLRecipeIngredient(ingredientId, this.getID(), volume);
            try {
                ri.save();
            } catch (NotInitializedDBException ignored) {
                ignored.printStackTrace();
                Log.i(TAG, "should not happen");
                //throw new RuntimeException(e);
            }
            this.ingredientVolumes.add(ri);
        }
        this.wasChanged();
        /*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(this.ingredientVolumes.stream()
                    .filter(pt -> pt.getIngredientID() == ingredientId)
                    .peek(pt -> pt.setVolume(volume))
                    .count()
                    == 0){
                this.ingredientVolumes.add(new SQLRecipeIngredient(ingredientId, this.getID(), volume));
            }
        }
        this.wasChanged();

         */
    }

    public void addOrUpdateIDs(HashMap<Long, Integer> ingredientVolumes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientVolumes.forEach(this::addOrUpdate);
        }else{
            this.ingredientVolumes = Helper.updateWithIds(this.getID(), this.ingredientVolumes, ingredientVolumes);
        }
    }

    public void addOrUpdateElements(HashMap<Ingredient, Integer> ingredientVolumes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ingredientVolumes.forEach(this::addOrUpdate);
        }else{
            this.ingredientVolumes = Helper.updateWithIngredients(this.getID(), this.ingredientVolumes, ingredientVolumes);
        }
    }

    @Override
    public void addOrUpdate(Topic topic) {
        if(topic.getID()==-1L){
            try {
                topic.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        if(this.topics.contains(topic.getID())){
            return;
        }
        this.topics.add(topic.getID());
        this.wasChanged();
    }

    @Override
    public void addOrUpdate(String imageUrls) {
        if(this.getImageUrls().contains(imageUrls)){
            return;
        }
        SQLRecipeImageUrlElement urlElement = new SQLRecipeImageUrlElement(imageUrls, this.getID());
        this.imageUrls.add(urlElement);
        this.wasChanged();
    }

    //REMOVER
    @Override
    public void remove(Ingredient ingredient) {
        if(ingredient!=null) {
            this.removeIngredient(ingredient.getID());
        }
    }

    @Override
    public void removeIngredient(long ingredientId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            this.ingredientVolumes.removeAll(this.ingredientVolumes.stream()
                    .filter(ri -> ri.getIngredientID() == ingredientId)
                    .peek(sqlRecipeIngredient -> {
                        try {
                            sqlRecipeIngredient.delete();
                        } catch (NotInitializedDBException e) {
                            e.printStackTrace();
                        }
                    })
                    .collect(Collectors.toList()));

        }else {
            this.ingredientVolumes.removeAll(
                    Helper.getrecipeingredienthelper()
                            .getDeleteWithIdAsList(
                                    this.ingredientVolumes,
                                    ingredientId));
        }
    }

    @Override
    public void remove(Topic topic) {
        this.topics.remove(topic.getID());
    }

    @Override
    public void removeTopic(long topicId) {
        this.topics.remove(topicId);
    }

    @Override
    public void remove(SQLRecipeImageUrlElement url) {
        this.imageUrls.remove(url);
        try {
            url.delete();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeUrl(long urlId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.imageUrls.stream().filter(url-> url.getID()==urlId).forEach(url-> {
                        try {
                            url.delete();
                        } catch (NotInitializedDBException e) {
                            e.printStackTrace();
                        }
                        this.imageUrls.remove(url);
            });
        }else{
            this.imageUrls = (List<SQLRecipeImageUrlElement>) Helper.getImageUrlElementhelper().removeIfAndDeleteExtended(this.imageUrls, urlId);
        }
    }

    //general
    @Override
    public void delete() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().remove(this);
    }

    @Override
    public void save() throws NotInitializedDBException {
        DatabaseConnection.getDataBase().addOrUpdate(this);
        for(SQLRecipeImageUrlElement url: this.imageUrls) {
            DatabaseConnection.getDataBase().addOrUpdate(url);
        }
        for(SQLRecipeIngredient ri: this.ingredientVolumes){
            if(ri.getRecipeID()!=this.getID()){
                ri.setRecipeID(this.getID());
                ri.save();
            }
            DatabaseConnection.getDataBase().addOrUpdate(ri);
        }
        this.wasSaved();
    }

    public JSONObject asJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("ID", this.getID());
            json.put("name", this.name);
            json.put("alcoholic", this.alcoholic);
            json.put("available", this.available);
            json.put("imageUrls", this.imageUrls.toString());
            json.put("ingredient", this.getIngredientIds());
            return json;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
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
