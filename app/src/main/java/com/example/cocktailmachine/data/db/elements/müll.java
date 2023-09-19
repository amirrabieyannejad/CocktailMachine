package com.example.cocktailmachine.data.db.elements;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.DeleteFromDB;
import com.example.cocktailmachine.data.db.Helper;
import com.example.cocktailmachine.data.db.exceptions.AlreadySetIngredientException;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Johanna Reidt
 * @created Di. 19.Sep 2023 - 11:01
 * @project CocktailMachine
 */
public class müll {

    //LOADER




/*
    //GETTER
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Long> getIngredientIds() {
        return Buffer.getSingleton().getIngredientIds(this);

    }

    @Override
    public List<String> getIngredientNames() {
        return Buffer.getSingleton().getIngredientNames(Buffer.getSingleton().getIngredientIds(this));
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Buffer.getSingleton().getIngredients(this);
    }

    @Override
    public HashMap<Long, Integer> getIngredientNameVolumes() {
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
    public HashMap<Ingredient, Integer> getIngredientVolumes() {
        HashMap<Ingredient, Integer> ingVol = new HashMap<>();
        for(SQLRecipeIngredient ri: this.ingredientVolumes){
            ingVol.put(ri.getIngredient(), ri.getVolume());
        }
        return ingVol;
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

    @Override
    public boolean loadAvailable(Context context) {
        this.loadAvailable();
        this.save(context);
        return this.available;
    }

 */

    /**
     * check for ingredient pump connection
     * true, if ingredient pump connection exists
     *
     * @return
     */
    /*
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


     */
    /**
     * @return if all ingredients available, with sufficient amounts of volume
     */
    /*
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




     */


/*

    @Override
    public List<String> getImageUrls() {
        Log.i(TAG, "getImageUrls");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return this.imageUrls.stream().map(SQLImageUrlElement::getUrl).collect(Collectors.toList());
        }
        return Helper.getUrls(this.imageUrls);
    }

    @Override
    public List<Long> getTopicIDs() {
        return Topic.getTopicIDs(this);
    }

    @Override
    public List<Topic> getTopics() {
        return Topic.getTopics(this);
    }








 */
















    //Setter

/*
    @Override
    public void setName(String name) {
        this.name = name;
    }


    //ADDER
    public void add(Ingredient ingredient, int volume)
            throws AlreadySetIngredientException {
        if(ingredient.getID()==-1L){
            ingredient.save();
        }
        this.add(ingredient.getID(), volume);
    }

    public void add(long ingredientId, int volume)
            throws AlreadySetIngredientException {
        if(this.getIngredientIds().contains(ingredientId)){
            throw new AlreadySetIngredientException(this, ingredientId);
        }
        Buffer.getSingleton().addToBuffer(new SQLRecipeIngredient(ingredientId, this.getID(), volume));
    }

    @Override
    public void addOrUpdate(Ingredient ingredient, int volume) {
        if(ingredient.getID()==-1L){

        }
        this.addOrUpdate(ingredient.getID(), volume);
    }


 */
    /*
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
            ri.save();
            this.ingredientVolumes.add(ri);
        }
        this.wasChanged();

     */
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
    /*
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

     */
        /*
        if(topic.getID()==-1L){
            topic.save();
        }

         */
    /*
        if(topic.getID()==-1){
            Buffer.getSingleton().addToBuffer();
        }
        if(this.getTopicIDs().contains(topic.getID())){
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

    @Override
    public void add(List<Topic> topics) {
        for(Topic t: topics){
            Buffer.getSingleton().addToBuffer(new SQLRecipeTopic(this.getID(), t.getID()));
        }
    }

    @Override
    public void add(List<Ingredient> ingredients) {
        for(Ingredient t: ingredients){
            Buffer.getSingleton().addToBuffer(new SQLRecipeIngredient(t.getID(),this.getID(),-1 ));
        }
    }

    @Override
    public void addTopics(List<Long> topics) {

        for(Long t: topics){
            Buffer.getSingleton().addToBuffer(new SQLRecipeTopic(this.getID(), t));
        }
    }

    @Override
    public void addIngredients(List<Long> ingredients) {
        for(Long t: ingredients){
            Buffer.getSingleton().addToBuffer(new SQLRecipeIngredient(t,this.getID(),-1 ));
        }

    }






     */












/*

    //REMOVER
    @Override
    public void remove(Context context, Ingredient ingredient) {
        if(ingredient!=null) {
            DeleteFromDB.remove(context, this, ingredient);
        }
    }

    @Override
    public void removeIngredient(Context context, long ingredientId) {
        remove(context, Ingredient.getIngredient(ingredientId));
    }

    @Override
    public void remove(Context context,Topic topic) {
        DeleteFromDB.remove(context, this, topic);
    }

    @Override
    public void removeTopic(Context context,long id) {
        //this.topics.remove(topicId);
        this.remove(context, Topic.getTopic(id));
    }

    @Override
    public void remove(Context context,SQLRecipeImageUrlElement url) {
        this.imageUrls.remove(url);
        url.delete(context);
    }

    @Override
    public void removeUrl(Context context,long urlId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.imageUrls.stream().filter(url-> url.getID()==urlId).forEach(url-> {
                url.delete(context);
                this.imageUrls.remove(url);
            });
        }else{
            this.imageUrls = (List<SQLRecipeImageUrlElement>) Helper.getImageUrlElementhelper().removeIfAndDeleteExtended(this.imageUrls, urlId);
        }
    }

 */

}
