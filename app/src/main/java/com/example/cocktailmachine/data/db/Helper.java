package com.example.cocktailmachine.data.db;

import android.util.Log;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.db.elements.SQLImageUrlElement;
import com.example.cocktailmachine.data.db.elements.SQLIngredient;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper<T extends DataBaseElement> {
    private static final String TAG = "Helper";



    public List<T> getAvailable(List<T> elements){
        List<T> res = new ArrayList<>();
        for(T e:elements){
            if(e.isAvailable()){
                res.add(e);
            }
        }
        return res;
    }

    public List<T> getWithIds(List<T> elements, List<Long> ids){
        List<T> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            if(ids.contains(elements.get(i).getID())){
                res.add(elements.get(i));
            }
        }
        return res;
    }

    public List<T> getDeleteWithIds(List<T> elements, List<Long> ids){
        List<T> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            if(ids.contains(elements.get(i).getID())){
                elements.get(i).delete();
                res.add(elements.get(i));
            }
        }
        return res;
    }

    public List<T> getDeleteWithIdAsList(List<T> elements, Long id) {
        List<T> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            if(id ==elements.get(i).getID()){
                elements.get(i).delete();
                res.add(elements.get(i));
            }
        }
        return res;
    }

    public List<T> getWithIdAsList(List<T> elements, Long id){
        List<T> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            if(id==elements.get(i).getID()){
                res.add(elements.get(i));
            }
        }
        return res;
    }

    public T getWithId(List<T> elements, Long id){
        for(int i=0; i<elements.size(); i++){
            if(id==elements.get(i).getID()){
                return elements.get(i);
            }
        }
        return null;
    }

    public List<Long> getIds(List<T> elements){
        List<Long> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            res.add(elements.get(i).getID());
        }
        return res;
    }

    public List<Object> getIdsExtend(List<? extends T> elements) {
        List<Object> res = new ArrayList<>();
        for(int i=0; i<elements.size(); i++){
            res.add(elements.get(i).getID());
        }
        return res;
    }

    public HashMap<Long, Integer> getIngredientVolumes(List<SQLRecipeIngredient> recipeIngredients){
        HashMap<Long, Integer> res = new HashMap<>();
        for(SQLRecipeIngredient ri :recipeIngredients) {
            res.put(ri.getIngredientID(),ri.getVolume());
        }
        return res;
    }

    public  List<T> removeIf(
            List<T> elements,
            Long id){
        for(T e: elements){
            if(e.getID() == id) {
                elements.remove(e);
            }
        }
        return elements;
    }

    public  List<T> removeIfAndDelete(
            List<T> elements,
            Long id){
        for(T e: elements){
            if(e.getID() == id) {
                elements.remove(e);
                e.delete();
            }
        }
        return elements;
    }

    public  List<? extends T> removeIfAndDeleteExtended(
            List<? extends T> elements,
            Long id){
        for(T e: elements){
            if(e.getID() == id) {
                elements.remove(e);
                e.delete();
            }
        }
        return elements;
    }



    //Statics

    public static List<String> getUrls(List<? extends SQLImageUrlElement> urls){
        List<String> res = new ArrayList<>();
        for(SQLImageUrlElement u: urls){
            res.add(u.getUrl());
        }
        return res;
    }

    public static List<SQLIngredientPump> removeIfPumpID(
            List<SQLIngredientPump> elements,
            Long id){
        List<SQLIngredientPump> toBeDeleted = new ArrayList<>();
        for(SQLIngredientPump ip: elements){
            if(ip.getPumpID() == id) {
                toBeDeleted.add(ip);
            }
        }
        for(SQLIngredientPump ip: toBeDeleted){
            ip.delete();
        }
        elements.removeAll(toBeDeleted);
        return elements;
    }

    public static List<SQLIngredientPump> removeIfIngredientID(
            List<SQLIngredientPump> elements,
            Long id){
        for(SQLIngredientPump e: elements){
            if(e.getIngredientID() == id) {
                elements.remove(e);
                e.delete();
            }
        }
        return elements;
    }

    public static SQLIngredientPump getWithPumpID(List<SQLIngredientPump>  items,long pump){
        for(SQLIngredientPump item: items){
            if(item.getPumpID()==pump){
                return item;
            }
        }
        return null;
    }

    public static List<SQLRecipeIngredient> getWithRecipeID(List<SQLRecipeIngredient>  items,long recipe){
        List<SQLRecipeIngredient> res = new ArrayList<>();
        for(SQLRecipeIngredient item: items){
            if(item.getRecipeID()==recipe){
                res.add(item);
            }
        }
        return res;
    }

    public static SQLIngredientPump getWithIngredientID(List<SQLIngredientPump>  items,long ingredient){
        for(SQLIngredientPump item: items){
            if(item.getIngredientID()==ingredient){
                return item;
            }
        }
        return null;
    }

    public static String stringAppender(HashMap<String, String> map){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<String, String> entry: map.entrySet()){
            res.append(entry.getKey());
            res.append(" ");
            res.append(entry.getValue());
            res.append(",");
        }
        return res.toString();
    }

    public static List<Long> getIngredientIds(List<SQLIngredientPump> items){
        List<Long> ids = new ArrayList<>();
        for(SQLIngredientPump ip: items){
            ids.add(ip.getIngredientID());
        }
        return ids;
    }

    public static List<SQLRecipeIngredient> updateWithIds(long recipe_id, List<SQLRecipeIngredient> old, HashMap<Long, Integer> updates){
        for(SQLRecipeIngredient ri : old){
            if(updates.containsKey(ri.getID())){
                try {
                    int volume = updates.get(ri.getID());
                    ri.setVolume(volume);
                    updates.remove(ri.getID());
                    ri.save();
                }catch (NullPointerException e){
                    e.printStackTrace();
                    System.out.println("Das sollte nicht passieren!!!");
                }
            }
        }
        for( Map.Entry<Long, Integer> entry: updates.entrySet()){
            old.add(new SQLRecipeIngredient(entry.getKey(),recipe_id,entry.getValue()));
        }
        return old;
    }

    public static List<SQLRecipeIngredient> updateWithIngredients(long recipe_id, List<SQLRecipeIngredient> old, HashMap<Ingredient, Integer> updates){
        HashMap<Long, Integer> temp = new HashMap<>();
        for(Map.Entry<Ingredient, Integer> entry: updates.entrySet()){
            entry.getKey().save();
            temp.put(entry.getKey().getID(), entry.getValue());
        }
        return Helper.updateWithIds(recipe_id, old, temp);
    }

    public static String[] objToString(List<? extends Object> objs){
        ArrayList<String> res = new ArrayList<>();
        for(Object obj: objs){
            res.add(obj.toString());
        }
        return res.toArray(res.toArray(new String[0]));
    }

    public static String objToString(List<? extends Object> objs, String prefix, String joiner, String suffix){
        StringBuilder res = new StringBuilder();
        res.append(prefix);
        for(Object obj: objs){
            res.append(obj.toString());
            res.append(joiner);
        }
        res.append(suffix);
        return res.toString();
    }

    public static void emptyPump(List<? extends Pump> pumps){
        for (Pump p: pumps){
            p.empty();
        }
    }

    public static void emptyIngredient(List<? extends Ingredient> ingredients) {
        for(Ingredient ingredient: ingredients){
            ingredient.empty();
        }
    }

    public static boolean ingredientAvailable(HashMap<Long, Integer> ingredientVolume){
        List<Long> ingredientIds = new ArrayList<>(ingredientVolume.keySet());
        List<Ingredient> ingredients = null;
        try {
            ingredients = DatabaseConnection.getDataBase().getIngredients(ingredientIds);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            return false;
        }
        for(Ingredient ingredient: ingredients){
            Long id = ingredient.getID();
            try {
                int volume = ingredientVolume.get(id);
                if(ingredient.getVolume()<=volume){
                    return false;
                }
            }catch (NullPointerException ignored){
                return false;
            }
        }
        return true;
    }

    public static List<Topic> topicWithNeedleInName(List<Topic> topics, String needle){
        List<Topic> res = new ArrayList<>();
        for(Topic r: topics){
            if(r.getName().contains(needle)) {
                res.add(r);
            }
        }
        return res;
    }

    public static List<Topic> topicWithName(List<Topic> topics, String name){
        List<Topic> res = new ArrayList<>();
        for(Topic r: topics){
            if(r.getName().equals(name)) {
                res.add(r);
            }
        }
        return res;
    }

    public static List<Ingredient> ingredientWithNeedleInName(List<Ingredient> ingredients, String needle){
        List<Ingredient> res = new ArrayList<>();
        for(Ingredient r: ingredients){
            if(r.getName().contains(needle)) {
                res.add(r);
            }
        }
        return res;
    }

    /**
     * get ingredients from list with exact name name
     * @param ingredients
     * @param name
     * @return list
     */
    public static List<Ingredient> ingredientWitName(List<Ingredient> ingredients, String name){
        Log.i(TAG, "ingredientWitName");
        List<Ingredient> res = new ArrayList<>();
        for(Ingredient r: ingredients){
            Log.i(TAG, "ingredientWitName "+r.getName()+name+r.getName().equals(name));
            if(r.getName().equals(name)) {
                res.add(r);
            }
        }
        return res;
    }

    public static List<Recipe> recipesWithNeedleInName(List<Recipe> recipes, String needle){
        List<Recipe> res = new ArrayList<>();
        for(Recipe r: recipes){
            if(r.getName().contains(needle)) {
                res.add(r);
            }
        }
        return res;
    }

    public static List<Recipe> recipesWithName(List<Recipe> recipes, String name){
        List<Recipe> res = new ArrayList<>();
        for(Recipe r: recipes){
            if(r.getName().equals(name)) {
                res.add(r);
            }
        }
        return res;
    }



    //get helpers

    public static Helper<SQLIngredient> getingredienthelper(){
        return new Helper<SQLIngredient>();
    }

    public static Helper<SQLIngredientPump> getingredientpumphelper(){
        return new Helper<SQLIngredientPump>();
    }

    public static Helper<SQLRecipeIngredient> getrecipeingredienthelper(){
        return new Helper<SQLRecipeIngredient>();
    }

    public static Helper<Topic> gettopichelper(){
        return new Helper<Topic>();
    }

    public static Helper<SQLImageUrlElement> getImageUrlElementhelper(){
        return new Helper<SQLImageUrlElement>();
    }

    public static Helper<Pump> getPumpHelper(){
        return new Helper<Pump>();
    }

    public static Helper<Ingredient> getIngredientHelper(){
        return new Helper<Ingredient>();
    }

    public static Helper<Recipe> getRecipeHelper(){
        return new Helper<Recipe>();
    }



}
