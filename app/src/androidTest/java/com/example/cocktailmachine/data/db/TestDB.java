package com.example.cocktailmachine.data.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestDB {

    @Before
    public void setUp(){
        //InstrumentationRegistry.registerInstance(new Instrumentation(), new Bundle());

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertNotNull("cant be", appContext);
        DatabaseConnection.initialize_singleton(appContext);
        try {
            if(DatabaseConnection.getDataBase().getWritableDatabase()==null){
                System.out.println("no writable");
            }
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
            System.out.println("no writable");
        }
        try {
            if(DatabaseConnection.getDataBase().getReadableDatabase()==null){
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
            DatabaseConnection.getDataBase().close();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPreConditions() {
        try {
            assertNotNull(DatabaseConnection.getDataBase());
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
            System.out.println(DatabaseConnection.getDataBase().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        try {
            assertNotNull("DB", DatabaseConnection.getDataBase());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(DatabaseConnection.getDataBase().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        try {
            assertNotNull("DB", DatabaseConnection.getDataBase());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void new_recipe(){
        Recipe recipe = Recipe.makeNew("Magarita");
        System.out.println(recipe.toString());
        try {
            recipe.save();
            System.out.println(DatabaseConnection.getDataBase().toString());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        Recipe db_recipe = null;
        try {
            db_recipe = DatabaseConnection.getDataBase().getRecipe(recipe.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe.toString());
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void new_topics(){
        Topic topic = Topic.makeNew("Eis", "gefrorenes Wasser");
        System.out.println(topic.toString());
        Topic db_topic = null;
        try {
            topic.save();
            db_topic = DatabaseConnection.getDataBase().getTopic(topic.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_topic == null){
            System.out.println("db_topic failed");
        }else {
            System.out.println(db_topic.toString());
        }
        assert topic.equals(db_topic);
    }

    @Test
    public void recipe_add_topics(){
        Recipe recipe = Recipe.makeNew("Magarita_topics");
        recipe.addOrUpdate(Topic.makeNew("Eis2", "gefrorenes Wasser, 3 Würfel"));
        System.out.println(recipe.toString());
        Recipe db_recipe = null;
        try {
            recipe.save();
            db_recipe = DatabaseConnection.getDataBase().getRecipe(recipe.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe.toString());
        }
        assert recipe.equals(db_recipe);
    }


    @Test
    public void new_ingredient(){
        Ingredient ingredient = Ingredient.makeNew("Kokosmilch", false, 67);
        System.out.println(ingredient.toString());
        Ingredient db_ingredient = null;
        try {
            ingredient.save();
            db_ingredient = DatabaseConnection.getDataBase().getIngredient(ingredient.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_ingredient == null){
            System.out.println("db_ingredient failed");
        }else {
            System.out.println(db_ingredient.toString());
        }
        assert ingredient.equals(db_ingredient);
    }

    @Test
    public void ingredient_add_image_url(){
        Ingredient ingredient = Ingredient.makeNew("Pfeffi", true, 46);
        ingredient.addImageUrl("test_ingredient.png");

        System.out.println(ingredient.toString());
        Ingredient db_ingredient = null;
        try {
            ingredient.save();
            db_ingredient = DatabaseConnection.getDataBase().getIngredient(ingredient.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_ingredient == null){
            System.out.println("db_ingredient failed");
        }else {
            System.out.println(db_ingredient.toString());
        }
        assert ingredient.equals(db_ingredient);
        try {
            DatabaseConnection.getDataBase().close();
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void recipe_add_ingredient(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient");
        recipe.addOrUpdate(
                Ingredient.makeNew("Wodka", true, 123),
                3);

        System.out.println(recipe.toString());
        Recipe db_recipe = null;
        try {
            recipe.save();
            db_recipe = DatabaseConnection.getDataBase().getRecipe(recipe.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe.toString());
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_ingredient_with_id(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient_id");
        recipe.addOrUpdate(
                Ingredient.makeNew("Grapefruit-Saft", false, 324).getID(),
                7);

        System.out.println(recipe.toString());
        Recipe db_recipe = null;
        try {
            recipe.save();
            db_recipe = DatabaseConnection.getDataBase().getRecipe(recipe.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe.toString());
        }
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_imageUrl(){
        Recipe recipe = Recipe.makeNew("Magarita_url");
        recipe.addOrUpdate("test.png");

        System.out.println(recipe.toString());
        Recipe db_recipe = null;
        try {
            recipe.save();
            db_recipe = DatabaseConnection.getDataBase().getRecipe(recipe.getID());
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }
        if(db_recipe == null){
            System.out.println("db_recipe failed");
        }else {
            System.out.println(db_recipe.toString());
        }
        assert recipe.equals(db_recipe);
    }


}
