package com.example.cocktailmachine.data.db;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;

public class TestDB {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connection(){
        assertNotNull("DB", NewDatabaseConnection.getDataBase());
    }

    @Test
    public void new_recipe(){
        Recipe recipe = Recipe.makeNew("Magarita");
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }

    @Test
    public void new_topics(){
        Topic topic = Topic.makeNew("Eis", "gefrorenes Wasser");
        Topic db_topic = NewDatabaseConnection.getDataBase().getTopic(topic.getID());
        assert topic.equals(db_topic);
    }

    @Test
    public void recipe_add_topics(){
        Recipe recipe = Recipe.makeNew("Magarita_topics");
        recipe.addOrUpdate(Topic.makeNew("Eis2", "gefrorenes Wasser, 3 Würfel"));
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }


    @Test
    public void new_ingredient(){
        Ingredient ingredient = Ingredient.makeNew("Kokosmilch", false, 67);
        Ingredient db_ingredient = NewDatabaseConnection.getDataBase().getIngredient(ingredient.getID());
        assert ingredient.equals(db_ingredient);
    }

    @Test
    public void ingredient_add_image_url(){
        Ingredient ingredient = Ingredient.makeNew("Pfeffi", true, 46);
        ingredient.addImageUrl("test_ingredient.png");
        Ingredient db_ingredient = NewDatabaseConnection.getDataBase().getIngredient(ingredient.getID());
        assert ingredient.equals(db_ingredient);
    }

    @Test
    public void recipe_add_ingredient(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient");
        recipe.addOrUpdate(
                Ingredient.makeNew("Wodka", true, 123),
                3);
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_ingredient_with_id(){
        Recipe recipe = Recipe.makeNew("Magarita_ingredient_id");
        recipe.addOrUpdate(
                Ingredient.makeNew("Grapefruit-Saft", false, 324).getID(),
                7);
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }

    @Test
    public void recipe_add_imageUrl(){
        Recipe recipe = Recipe.makeNew("Magarita_url");
        recipe.addOrUpdate("test.png");
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }

}
