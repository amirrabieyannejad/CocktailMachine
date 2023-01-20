package com.example.cocktailmachine.data.db.elements;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SQLRecipe extends SQLDataBaseElement implements Recipe {
    private String name = "";
    //private List<Long> ingredientIds;
    //private HashMap<Long, Integer> ingredientPumpTime;
    private boolean alcoholic;
    private boolean available = false;
    private List<SQLRecipeImageUrlElement> imageUrls = new ArrayList<>();
    private List<Long> topics = new ArrayList<>();
    private List<SQLRecipeIngredient> pumpTimes = new ArrayList<>();
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
                     HashMap<Long, Integer> ingredientPumpTimes,
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
        this.addOrUpdateIDs(ingredientPumpTimes);
        this.loaded = true;
    }

    public SQLRecipe(long ID,
                     String name,
                     boolean alcoholic,
                     boolean available,
                     List<SQLRecipeImageUrlElement> imageUrls,
                     List<Long> topics,
                     HashMap<Ingredient, Integer> ingredientPumpTimes) {
        super(ID);
        this.name = name;
        this.alcoholic = alcoholic;
        this.available = available;
        this.imageUrls = imageUrls;
        this.topics = topics;
        this.addOrUpdateElements(ingredientPumpTimes);
        this.loaded = true;
    }

    //LOADER


    private void load() throws NotInitializedDBException {
        this.imageUrls = DatabaseConnection.getDataBase().getUrlElements(this);
        this.topics = DatabaseConnection.getDataBase().getTopicIDs(this);
        this.pumpTimes = DatabaseConnection.getDataBase().getPumpTimes(this);
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
            return this.pumpTimes
                    .stream()
                    .map(SQLRecipeIngredient::getIngredientID)
                    .collect(Collectors.toList());
        }
        return Helper.getrecipeingredienthelper().getIds(this.pumpTimes);

    }

    @Override
    public List<Ingredient> getIngredients() {
        return Ingredient.getIngredientWithIds(this.getIngredientIds());
    }

    @Override
    public HashMap<Long, Integer> getIngredientPumpTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (HashMap<Long, Integer>)
                    this.pumpTimes.stream()
                            .collect( Collectors
                                    .toMap(SQLRecipeIngredient::getID,
                                            SQLRecipeIngredient::getPumpTime));
        }
        return Helper.getrecipeingredienthelper().getIngredientPumpTime(this.pumpTimes);
    }

    private List<SQLRecipeIngredient> getRecipeIngredient(long ingredientID)
            throws NoSuchIngredientSettedException, TooManyTimesSettedIngredientEcxception {
        List<SQLRecipeIngredient> res = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            res = this.pumpTimes.stream().filter(ri-> ri.getIngredientID()==ingredientID).collect(Collectors.toList());
        }else{
            res = Helper.getrecipeingredienthelper().getWithIdAsList(this.pumpTimes, ingredientID);
        }
        if(res.size()==0){
            throw new NoSuchIngredientSettedException(this, ingredientID);
        }else if(res.size()>1){
            throw new TooManyTimesSettedIngredientEcxception(this, ingredientID);
        }
        return res;
    }

    @Override
    public int getSpecificIngredientPumpTime(long ingredientId)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getRecipeIngredient(ingredientId).get(0).getPumpTime();
    }

    @Override
    public int getSpecificIngredientPumpTime(Ingredient ingredient)
            throws TooManyTimesSettedIngredientEcxception, NoSuchIngredientSettedException {
        return this.getSpecificIngredientPumpTime(ingredient.getID());
    }

    @Override
    public boolean isAlcoholic() {
        return this.alcoholic;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    @Override
    public List<String> getImageUrls() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.imageUrls.stream().map(SQLImageUrlElement::getUrl).collect(Collectors.toList());
        }
        return Helper.getUrls(this.imageUrls);
    }

    @Override
    public List<Long> getTopics() {
        return this.getTopics();
    }


    //ADDER
    public void add(Ingredient ingredient, int timeInMilliseconds)
            throws AlreadySetIngredientException {
        if(ingredient.getID()==-1L){
            try {
                ingredient.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        this.add(ingredient.getID(), timeInMilliseconds);
    }

    public void add(long ingredientId, int timeInMilliseconds)
            throws AlreadySetIngredientException {
        if(this.getIngredientIds().contains(ingredientId)){
            throw new AlreadySetIngredientException(this, ingredientId);
        }
        this.pumpTimes.add(new SQLRecipeIngredient(ingredientId, this.getID(), timeInMilliseconds));
    }

    @Override
    public void addOrUpdate(Ingredient ingredient, int timeInMilliseconds) {
        if(ingredient.getID()==-1L){
            try {
                ingredient.save();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
        this.addOrUpdate(ingredient.getID(), timeInMilliseconds);
    }

    @Override
    public void addOrUpdate(long ingredientId, int timeInMilliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(this.pumpTimes.stream()
                    .filter(pt -> pt.getIngredientID() == ingredientId)
                    .peek(pt -> pt.setPumpTime(timeInMilliseconds)).count() == 0){
                this.pumpTimes.add(new SQLRecipeIngredient(ingredientId, this.getID(), timeInMilliseconds));
            }
        }
        this.wasChanged();
    }

    public void addOrUpdateIDs(HashMap<Long, Integer> pumpTimes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pumpTimes.forEach(this::addOrUpdate);
        }else{
            this.pumpTimes = Helper.updateWithIds(this.getID(), this.pumpTimes, pumpTimes);
        }
    }

    public void addOrUpdateElements(HashMap<Ingredient, Integer> pumpTimes){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pumpTimes.forEach(this::addOrUpdate);
        }else{
            this.pumpTimes = Helper.updateWithIngredients(this.getID(), this.pumpTimes, pumpTimes);
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
        this.removeIngredient(ingredient.getID());
    }

    @Override
    public void removeIngredient(long ingredientId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            this.pumpTimes.removeAll(this.pumpTimes.stream()
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
            this.pumpTimes.removeAll(
                    Helper.getrecipeingredienthelper()
                            .getDeleteWithIdAsList(
                                    this.pumpTimes,
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
