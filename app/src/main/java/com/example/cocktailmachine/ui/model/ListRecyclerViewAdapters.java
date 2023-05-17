package com.example.cocktailmachine.ui.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;

import java.util.List;

public class ListRecyclerViewAdapters  {



    public abstract static class ListRecyclerViewAdapter<E extends RowViews.RowView> extends RecyclerView.Adapter<E> {
        private RowViews.RowType type;

        public ListRecyclerViewAdapter(RowViews.RowType type) {
            this.type = type;
        }

        public LinearLayoutManager getManager(android.content.Context getContext){
            LinearLayoutManager llm = new LinearLayoutManager(getContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            return llm;
        }

        public abstract void loadDelete();

        public abstract void finishDelete();

        public abstract void putIds(List<Long> ids);
        public abstract void putIds();

        public abstract void putRecipe(Long id);
    }

    public static class RecipeListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeRowView> {
        private List<Recipe> recipes;

        public RecipeListRecyclerViewAdapter(List<Long> ids) {
            super(RowViews.RowType.recipe);
            this.putIds(ids);
        }

        public RecipeListRecyclerViewAdapter() {
            super(RowViews.RowType.recipe);
            this.putIds();
        }

        @NonNull
        @Override
        public RowViews.RecipeRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.getRecipeInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeRowView holder, int position) {
            holder.setRecipe(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {
            recipes = Recipe.getRecipes(ids);
        }

        @Override
        public void putIds() {
            try {
                recipes = Recipe.getRecipes();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Long id) {
            return;
        }
    }

    public static class TopicListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.TopicRowView> {
        private List<Topic> topics;

        public TopicListRecyclerViewAdapter() {
            super(RowViews.RowType.topic);
            this.putIds();
        }
        public TopicListRecyclerViewAdapter(List<Long> ids) {
            super(RowViews.RowType.topic);
            this.putIds(ids);
        }

        @NonNull
        @Override
        public RowViews.TopicRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.gettopicInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.TopicRowView holder, int position) {
            holder.setTopic(topics.get(position));
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {
            try {
                topics = Topic.getTopics();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putIds() {
            try {
                topics = Topic.getTopics();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Long id) {
            try {
                topics = Topic.getTopics(Recipe.getRecipe(id));
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }
    }

    public static class PumpListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.PumpRowView> {
        private List<Pump> pumps;

        public PumpListRecyclerViewAdapter() {
            super(RowViews.RowType.pump);
            this.putIds();
        }



        @NonNull
        @Override
        public RowViews.PumpRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.getpumpInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.PumpRowView holder, int position) {
            holder.setIngredient(pumps.get(position));
        }

        @Override
        public int getItemCount() {
            return pumps.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {
            try {
                pumps = Pump.getPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putIds() {
            try {
                pumps = Pump.getPumps();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Long id) {

        }
    }

    public static class IngredientListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.IngredientRowView> {
        private List<Ingredient> data;

        public IngredientListRecyclerViewAdapter() {
            super(RowViews.RowType.ingredient);
            this.putIds();
        }
        public IngredientListRecyclerViewAdapter(List<Long > ids) {
            super(RowViews.RowType.ingredient);
            this.putIds(ids);
        }

        @NonNull
        @Override
        public RowViews.IngredientRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.getIngredientInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.IngredientRowView holder, int position) {
            holder.setIngredient(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {

            data = Ingredient.getIngredientWithIds(ids);
        }

        @Override
        public void putIds() {
            try {
                data  = Ingredient.getIngredientWithIds();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Long id) {
            data = Recipe.getRecipe(id).getIngredients();
        }

    }

    public static class RecipeIngredientListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeIngredientRowView> {
        private List<Ingredient> data;
        private Recipe recipe;

        public RecipeIngredientListRecyclerViewAdapter(Long id) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(id);
        }

        @NonNull
        @Override
        public RowViews.RecipeIngredientRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.getrecipeIngredientInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeIngredientRowView holder, int position) {
            try {
                holder.setIngredientVolume(recipe, data.get(position), recipe.getSpecificIngredientVolume(data.get(position)));
            } catch (TooManyTimesSettedIngredientEcxception e) {
                e.printStackTrace();
            } catch (NoSuchIngredientSettedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {
            if(ids==null){
                try {
                    data  = Ingredient.getIngredientWithIds();
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                }
            }else{
                data = Ingredient.getIngredientWithIds(ids);}
        }

        @Override
        public void putIds() {

        }

        @Override
        public void putRecipe(Long id) {
            recipe = Recipe.getRecipe(id);
            data = recipe.getIngredients();
        }


    }

    public static class RecipeTopicListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeTopicRowView> {
        private List<Topic> data;
        private Recipe recipe;

        public RecipeTopicListRecyclerViewAdapter(Long id) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(id);
        }

        @NonNull
        @Override
        public RowViews.RecipeTopicRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return RowViews.getrecipeTopicInstance(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeTopicRowView holder, int position) {
            holder.setRecipeTopic(recipe, data.get(position));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        public void loadDelete(){

        }

        public void finishDelete(){

        }

        @Override
        public void putIds(List<Long> ids) {

            try {
                data  = Topic.getTopics();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putIds() {
            try {
                data  = Topic.getTopics();
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Long id) {
            recipe = Recipe.getRecipe(id);
            try {
                data = Topic.getTopics(recipe);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }


    }



}
