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
    public void add_topics(){
        Recipe recipe = Recipe.makeNew("Magarita_topics");
        recipe.addOrUpdate(Topic.makeNew("Eis2", "gefrorenes Wasser, 3 Würfel"));
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }


    @Test
    public void new_ingredient(){
        Ingredient ingredient = Ingredient.makeNew("Kokosmilch", true, 67);
        Ingredient db_ingredient = NewDatabaseConnection.getDataBase().getIngredient(ingredient.getID());
        assert ingredient.equals(db_ingredient);
    }

    @Test
    public void add_ingredient(){
        Recipe recipe = Recipe.makeNew("Magarita_topics");
        recipe.addOrUpdate(Ingredient.makeNew("Wodka", true, 123),3);
        Recipe db_recipe = NewDatabaseConnection.getDataBase().getRecipe(recipe.getID());
        assert recipe.equals(db_recipe);
    }


}
