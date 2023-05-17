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

import java.util.ArrayList;
import java.util.List;

public class ListRecyclerViewAdapters  {
    public abstract static class ListRecyclerViewAdapter<E extends RowViews.RowView> extends RecyclerView.Adapter<E> {
        private RowViews.RowType type;
        private boolean lock = false;
        private boolean add = false;
        private boolean delete = false;
        private List<E> views = new ArrayList<>();
        protected Recipe recipe=null;

        public ListRecyclerViewAdapter(RowViews.RowType type) {
            this.type = type;
        }

        public LinearLayoutManager getManager(android.content.Context getContext){
            LinearLayoutManager llm = new LinearLayoutManager(getContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            return llm;
        }

        public boolean isCurrentlyDeleting(){
            return delete;
        }

        public boolean isCurrentlyAdding(){
            return add;
        }

        public boolean loadDelete(){
            if(!lock){
                lock = true;
                delete = true;
                for (RowViews.RowView view: views) {
                    view.loadCheck();
                    view.deleteLongListener();
                }
                return true;
            }
            return false;
        }

        public void loadAdd(){
            if(!lock && recipe!=null ){
                lock = true;
                add = true;
                for (RowViews.RowView view: views) {
                    view.loadCheck();
                    view.deleteLongListener();
                }
            }
        }

        public void finishDelete(){
            for (RowViews.RowView view: views) {
                view.finishDelete();
            }
            lock = false;
            delete = false;
        }

        public void finishAdd(){
            for (RowViews.RowView view: views) {
                view.finishAdd();
                view.addLongListener(v -> loadDelete());
            }
            lock = false;
            add = false;
        }

        protected E addRowView(E view){
            view.addLongListener(v -> loadDelete());
            views.add(view);
            return view;
        }

        public abstract void putIds(List<Long> ids);

        public abstract void putIds();

        public void putRecipe(Long id){
            this.recipe = Recipe.getRecipe(id);
        }

        public void putRecipe(Recipe recipe){
            this.recipe = recipe;
        }
    }

    public static class RecipeListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeRowView> {
        private List<Recipe> recipes;

        public RecipeListRecyclerViewAdapter(List<Long> ids) {
            super(RowViews.RowType.recipe);
            this.putIds(ids);
        }

/*
        public RecipeListRecyclerViewAdapter(List<Recipe> recipes) {
            super(RowViews.RowType.recipe);
            this.recipes = recipes;
        }

 */

        public RecipeListRecyclerViewAdapter() {
            super(RowViews.RowType.recipe);
            this.putIds();
        }


        public void replaceRecipes(List<Recipe> recipes){
            this.recipes = recipes;
        }

        @NonNull
        @Override
        public RowViews.RecipeRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return addRowView(RowViews.getRecipeInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeRowView holder, int position) {
            holder.setRecipe(recipes.get(position));
        }

        @Override
        public int getItemCount() {
            return recipes.size();
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
            return addRowView(RowViews.gettopicInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.TopicRowView holder, int position) {
            holder.setTopic(topics.get(position));
        }

        @Override
        public int getItemCount() {
            return topics.size();
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
            super.putRecipe(id);
            try {
                topics = Topic.getTopics(super.recipe);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            try {
                topics = Topic.getTopics(super.recipe);
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
            return addRowView(RowViews.getpumpInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.PumpRowView holder, int position) {
            holder.setIngredient(pumps.get(position));
        }

        @Override
        public int getItemCount() {
            return pumps.size();
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
            return addRowView(RowViews.getIngredientInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.IngredientRowView holder, int position) {
            holder.setIngredient(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
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
            super.putRecipe(id);
            data = Recipe.getRecipe(id).getIngredients();
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            data = super.recipe.getIngredients();
        }

    }

    public static class RecipeIngredientListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeIngredientRowView> {
        private List<Ingredient> data;

        public RecipeIngredientListRecyclerViewAdapter(Long id) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(id);
        }

        @NonNull
        @Override
        public RowViews.RecipeIngredientRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return addRowView(RowViews.getrecipeIngredientInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeIngredientRowView holder, int position) {
            try {
                holder.setRecipe(recipe);
                holder.setIngredientVolume(data.get(position), recipe.getSpecificIngredientVolume(data.get(position)));
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
            super.putRecipe(id);
            data = super.recipe.getIngredients();
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            data = super.recipe.getIngredients();
        }


    }

    public static class RecipeTopicListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeTopicRowView> {
        private List<Topic> data;

        public RecipeTopicListRecyclerViewAdapter(Long id) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(id);
        }

        @NonNull
        @Override
        public RowViews.RecipeTopicRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_name_list, parent, false);
            return addRowView(RowViews.getrecipeTopicInstance(view));
        }

        @Override
        public void onBindViewHolder(@NonNull RowViews.RecipeTopicRowView holder, int position) {
            holder.setRecipe(recipe);
            holder.setTopic(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
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
            super.putRecipe(id);
            try {
                data = Topic.getTopics(recipe);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            try {
                data = Topic.getTopics(recipe);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        }


    }



}
