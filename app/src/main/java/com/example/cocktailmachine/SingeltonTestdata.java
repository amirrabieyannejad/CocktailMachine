package com.example.cocktailmachine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.SQLIngredientPump;
import com.example.cocktailmachine.data.db.elements.SQLRecipeIngredient;
import com.example.cocktailmachine.data.db.elements.SQLRecipeTopic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.ui.model.v2.WaitingQueueCountDown;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SingeltonTestdata {

    private static SingeltonTestdata singelton;

    private SingeltonTestdata(){

    }

    public static SingeltonTestdata getSingelton(){
        if (singelton == null){
            SingeltonTestdata.singelton = new SingeltonTestdata();

        }
        return (singelton);
    }

    public Recipe getRecipe(){
        Recipe recipe = new Recipe() {
            @Override
            public int compareTo(Recipe o) {
                return 0;
            }

            @Override
            public WaitingQueueCountDown getWaitingQueueCountDown() {
                 return null;
            }

            @Override
            public void setWaitingQueueCountDown(Activity activity) {


            }

            @Override
            public void addDialogWaitingQueueCountDown(Activity activity, AlertDialog alertDialog) {


            }

            @Override
            public String getClassName() {
                return null;
            }

            @Override
            public long getID() {
                return 0;
            }

            @Override
            public void setID(long id) {

            }

            @Override
            public String getName() {
                return null;
            }


            @Override
            public List<String> getIngredientNames() {
                return null;
            }

            @Override
            public List<String> getIngredientNameNVolumes() {
                return null;
            }

            @Override
            public List<Ingredient> getIngredients() {
                List<Ingredient> list = new LinkedList();
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public String getClassName() {
                        return null;
                    }

                    @Override
                    public long getID() {
                        return 1;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "ROT";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean loadAvailable(Context context) {
                        return false;
                    }

                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public void save(Context context) {

                    }

                    @Override
                    public void delete(Context context) {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public Long getPumpId() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.RED;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void removeImageUrl(String url) {

                    }



                    @Override
                    public void setColor(int color) {

                    }

                    @Override
                    public void setAlcoholic(boolean alcoholic) {

                    }

                    @Override
                    public void setName(String name) {

                    }

                    @Override
                    public void setIngredientPump(SQLIngredientPump ingredientPump) {

                    }

                    @Override
                    public void setPump(Context context, Long pump, int volume) {

                    }

                    @Override
                    public void empty(Context context) {

                    }

                    @Override
                    public void pump(int millimeters) {

                    }


                });
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public String getClassName() {
                        return null;
                    }

                    @Override
                    public long getID() {
                        return 2;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "Blau";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean loadAvailable(Context context) {
                        return false;
                    }

                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public void save(Context context) {

                    }

                    @Override
                    public void delete(Context context) {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public Long getPumpId() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.BLUE;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void removeImageUrl(String url) {

                    }


                    @Override
                    public void setColor(int color) {

                    }

                    @Override
                    public void setAlcoholic(boolean alcoholic) {

                    }

                    @Override
                    public void setName(String name) {

                    }

                    @Override
                    public void setIngredientPump(SQLIngredientPump ingredientPump) {

                    }

                    @Override
                    public void setPump(Context context, Long pump, int volume) {

                    }

                    @Override
                    public void empty(Context context) {

                    }

                    @Override
                    public void pump(int millimeters)  {

                    }

                });
                return list;
            }

            @Override
            public List<Long> getIngredientIDs() {
                return null;
            }



            @Override
            public HashMap<Ingredient, Integer> getIngredientToVolume() {
                return null;
            }

            @Override
            public HashMap<Long, Integer> getIngredientIDToVolume() {
                return null;
            }

            @Override
            public HashMap<String, Integer> getIngredientNameToVolume() {
                return null;
            }



            @Override
            public int getVolume(long ingredientID) {
                return 0;
            }



            @Override
            public int getVolume(Ingredient ingredient)  {
                if (ingredient.getID() == 1){
                    return 30;
                }
                if (ingredient.getID() == 2){
                    return 60;
                }
                return 80;
            }

            @Override
            public boolean isAlcoholic() {
                return false;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public boolean loadAvailable(Context context) {
                return false;
            }

            @Override
            public void setName(Context context, String name) {

            }

            @Override
            public void add(Context context, Topic topic) {

            }

            @Override
            public void add(Context context, Ingredient ingredient) {

            }

            @Override
            public void add(Context context, Ingredient ingredient, int volume) {

            }

            @Override
            public boolean loadAlcoholic(Context context) {
                return false;
            }

            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean needsUpdate() {
                return false;
            }

            @Override
            public void wasSaved() {

            }

            @Override
            public void wasChanged() {

            }

            @Override
            public void save(Context context) {

            }

            @Override
            public void delete(Context context) {

            }


            @Override
            public List<Long> getTopicIDs() {
                return null;
            }

            @Override
            public List<String> getTopicNames() {
                return null;
            }

            @Override
            public List<Topic> getTopics() {
                return null;
            }

            @Override
            public JSONArray getLiquidsJSON() {
                return Recipe.super.getLiquidsJSON();
            }

            @Override
            public JSONObject asMessage() throws JSONException {
                return Recipe.super.asMessage();
            }

            @Override
            public boolean sendSave(Activity activity) {
                return Recipe.super.sendSave(activity);
            }

            @Override
            public List<SQLRecipeIngredient> getRecipeIngredient() {
                return null;
            }

            @Override
            public List<SQLRecipeTopic> getRecipeTopic() {
                return null;
            }


            @Override
            public void send(Activity activity) {

            }
        };


        return(recipe);
    }

    public Recipe getRecipe2(){
        Recipe recipe = new Recipe() {
            @Override
            public int compareTo(Recipe o) {
                return 0;
            }

            @Override
            public WaitingQueueCountDown getWaitingQueueCountDown() {
                return null;
            }

            @Override
            public void setWaitingQueueCountDown(Activity activity) {


            }

            @Override
            public void addDialogWaitingQueueCountDown(Activity activity, AlertDialog alertDialog) {


            }

            @Override
            public String getClassName() {
                return null;
            }

            @Override
            public long getID() {
                return 0;
            }

            @Override
            public void setID(long id) {

            }

            @Override
            public String getName() {
                return null;
            }


            @Override
            public List<String> getIngredientNames() {
                return null;
            }

            @Override
            public List<String> getIngredientNameNVolumes() {
                return null;
            }

            @Override
            public HashMap<Ingredient, Integer> getIngredientToVolume() {
                return null;
            }

            @Override
            public HashMap<Long, Integer> getIngredientIDToVolume() {
                return null;
            }

            @Override
            public HashMap<String, Integer> getIngredientNameToVolume() {
                return null;
            }

            @Override
            public List<Ingredient> getIngredients() {
                List<Ingredient> list = new LinkedList();
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public String getClassName() {
                        return null;
                    }

                    @Override
                    public long getID() {
                        return 1;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "ROT";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean loadAvailable(Context context) {
                        return false;
                    }


                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public void save(Context context) {

                    }

                    @Override
                    public void delete(Context context) {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public Long getPumpId() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.RED;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void removeImageUrl(String url) {

                    }


                    @Override
                    public void setColor(int color) {

                    }

                    @Override
                    public void setAlcoholic(boolean alcoholic) {

                    }

                    @Override
                    public void setName(String name) {

                    }

                    @Override
                    public void setIngredientPump(SQLIngredientPump ingredientPump) {

                    }

                    @Override
                    public void setPump(Context context, Long pump, int volume) {

                    }

                    @Override
                    public void empty(Context context) {

                    }

                    @Override
                    public void pump(int millimeters) {

                    }


                });
                list.add(new Ingredient() {
                    @Override
                    public int compareTo(Ingredient o) {
                        return 0;
                    }

                    @Override
                    public String getClassName() {
                        return null;
                    }

                    @Override
                    public long getID() {
                        return 2;
                    }

                    @Override
                    public void setID(long id) {

                    }

                    @Override
                    public String getName() {
                        return "Blau";
                    }

                    @Override
                    public List<String> getImageUrls() {
                        return null;
                    }

                    @Override
                    public boolean isAlcoholic() {
                        return false;
                    }

                    @Override
                    public boolean isAvailable() {
                        return false;
                    }

                    @Override
                    public boolean loadAvailable(Context context) {
                        return false;
                    }


                    @Override
                    public boolean isSaved() {
                        return false;
                    }

                    @Override
                    public boolean needsUpdate() {
                        return false;
                    }

                    @Override
                    public void wasSaved() {

                    }

                    @Override
                    public void wasChanged() {

                    }

                    @Override
                    public void save(Context context) {

                    }

                    @Override
                    public void delete(Context context) {

                    }

                    @Override
                    public int getVolume() {
                        return 0;
                    }

                    @Override
                    public Pump getPump() {
                        return null;
                    }

                    @Override
                    public Long getPumpId() {
                        return null;
                    }

                    @Override
                    public int getColor() {
                        return Color.BLUE;
                    }

                    @Override
                    public void addImageUrl(String url) {

                    }

                    @Override
                    public void removeImageUrl(String url) {

                    }


                    @Override
                    public void setColor(int color) {

                    }

                    @Override
                    public void setAlcoholic(boolean alcoholic) {

                    }

                    @Override
                    public void setName(String name) {

                    }

                    @Override
                    public void setIngredientPump(SQLIngredientPump ingredientPump) {

                    }

                    @Override
                    public void setPump(Context context, Long pump, int volume) {

                    }

                    @Override
                    public void empty(Context context) {

                    }

                    @Override
                    public void pump(int millimeters)  {

                    }
                });
                return list;
            }

            @Override
            public List<Long> getIngredientIDs() {
                return null;
            }


            @Override
            public int getVolume(Ingredient ingredient) {
                if (ingredient.getID() == 1){
                    return 30;
                }
                if (ingredient.getID() == 2){
                    return 60;
                }
                return 80;
            }

            @Override
            public int getVolume(long ingredientID) {
                return 0;
            }

            @Override
            public boolean isAlcoholic() {
                return false;
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public boolean loadAvailable(Context context) {
                return false;
            }

            @Override
            public void setName(Context context, String name) {

            }

            @Override
            public void add(Context context, Topic topic) {

            }

            @Override
            public void add(Context context, Ingredient ingredient) {

            }

            @Override
            public void add(Context context, Ingredient ingredient, int volume) {

            }

            @Override
            public boolean loadAlcoholic(Context context) {
                return false;
            }


            @Override
            public boolean isSaved() {
                return false;
            }

            @Override
            public boolean needsUpdate() {
                return false;
            }

            @Override
            public void wasSaved() {

            }

            @Override
            public void wasChanged() {

            }

            @Override
            public void save(Context context) {

            }

            @Override
            public void delete(Context context) {

            }

            @Override
            public List<Long> getTopicIDs() {
                return null;
            }

            @Override
            public List<String> getTopicNames() {
                return null;
            }

            @Override
            public List<Topic> getTopics() {
                return null;
            }



            @Override
            public JSONArray getLiquidsJSON() {
                return Recipe.super.getLiquidsJSON();
            }

            @Override
            public JSONObject asMessage() throws JSONException {
                return Recipe.super.asMessage();
            }

            @Override
            public boolean sendSave(Activity activity) {
                return Recipe.super.sendSave(activity);
            }

            @Override
            public List<SQLRecipeIngredient> getRecipeIngredient() {
                return null;
            }

            @Override
            public List<SQLRecipeTopic> getRecipeTopic() {
                return null;
            }


            @Override
            public void send(Activity activity) {

            }


        };


        return(recipe);
    }

}
