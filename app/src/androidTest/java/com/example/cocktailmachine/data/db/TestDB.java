package com.example.cocktailmachine.data.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cocktailmachine.data.BasicRecipes;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestDB {
    Context appContext;

    @Before
    public void setUp(){
        //InstrumentationRegistry.registerInstance(new Instrumentation(), new Bundle());

        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertNotNull("cant be", appContext);
        DatabaseConnection.init(appContext);
        try {
            if(DatabaseConnection.getSingleton().getWritableDatabase()==null){
                System.out.println("no writable");
            }
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            System.out.println("no writable");
        }
        try {
            if(DatabaseConnection.getSingleton().getReadableDatabase()==null){
                System.out.println("no readable");
            }

            
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            System.out.println("no readable");
        }

        assertEquals("com.example.cocktailmachine", appContext.getPackageName());
    }

    @After
    public void finish() {
        try {
            DatabaseConnection.getSingleton().close();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreConditions() {
        try {
            assertNotNull(DatabaseConnection.getSingleton());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connection(){
        try {
            System.out.println(DatabaseConnection.getSingleton().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        try {
            assertNotNull("DB", DatabaseConnection.getSingleton());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(DatabaseConnection.getSingleton().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        try {
            assertNotNull("DB", DatabaseConnection.getSingleton());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void new_recipe(){
        Recipe recipe = Recipe.makeNew("Magarita");
        System.out.println(recipe);
        try {
            recipe.save(appContext);
            System.out.println(DatabaseConnection.getSingleton().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        Recipe db_recipe = null;
        db_recipe = Recipe.getRecipe(appContext, recipe.getID());
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe);
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void new_topics(){
        Topic topic = Topic.makeNew("Eis", "gefrorenes Wasser");
        System.out.println(topic);
        Topic db_topic = null;
        db_topic= Topic.getTopic(appContext, "Eis");
        if(db_topic == null){
            System.out.println("db_topic failed");
        }else {
            System.out.println(db_topic);
        }
        assert topic.equals(db_topic);
    }

    @Test
    public void recipe_add_topics(){
        Recipe recipe = Recipe.makeNew("Magarita_topics");
        recipe.add(appContext, Topic.makeNew("Eis2", "gefrorenes Wasser, 3 Würfel"));
        System.out.println(recipe);
        Recipe db_recipe = null;
        recipe.save(appContext);
        db_recipe = Recipe.getRecipe(appContext, recipe.getID());
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe);
        }
        assert recipe.equals(db_recipe);
    }


    @Test
    public void new_ingredient(){
        Ingredient ingredient = Ingredient.makeNew("Kokosmilch", false, 67);
        System.out.println(ingredient);
        Ingredient db_ingredient = null;
        ingredient.save(null);
        db_ingredient = Ingredient.getIngredient(appContext, ingredient.getID());

        if(db_ingredient == null){
            System.out.println("db_ingredient failed");
        }else {
            System.out.println(db_ingredient);
        }
        assert ingredient.equals(db_ingredient);
    }

    @Test
    public void ingredient_add_image_url(){
        Ingredient ingredient = Ingredient.makeNew("Pfeffi", true, 46);
        ingredient.addImageUrl("test_ingredient.png");

        System.out.println(ingredient);
        Ingredient db_ingredient = null;
        ingredient.save(null);
        db_ingredient = Ingredient.getIngredient(appContext, ingredient.getID());

        if(db_ingredient == null){
            System.out.println("db_ingredient failed");
        }else {
            System.out.println(db_ingredient);
        }
        assert ingredient.equals(db_ingredient);
        try {
            DatabaseConnection.getSingleton().close();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void recipe_add_ingredient(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient");
        recipe.add(appContext,
                Ingredient.makeNew("Wodka", true, 123),
                3);

        System.out.println(recipe);
        Recipe db_recipe = null;
        recipe.save(appContext);
        db_recipe = Recipe.getRecipe(appContext, recipe.getID());
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe);
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_ingredient_with_id(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient_id");
        recipe.add(appContext,
                Ingredient.makeNew("Grapefruit-Saft", false, 324),
                7);

        System.out.println(recipe);
        Recipe db_recipe = null;
        recipe.save(appContext);
        db_recipe = Recipe.getRecipe(appContext, recipe.getID());
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe);
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_imageUrl(){
        Recipe recipe = Recipe.makeNew("Magarita_url");
        //recipe.add("test.png");

        System.out.println(recipe);
        Recipe db_recipe = null;
        recipe.save(appContext);
        db_recipe = Recipe.getRecipe(appContext, recipe.getID());
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe);
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void loadTestRecipes() {
        try {
            BasicRecipes.loadMargarita(appContext);
            Recipe magarita = Recipe.getRecipe(appContext,"Margarita");
            assertNotNull(magarita);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
