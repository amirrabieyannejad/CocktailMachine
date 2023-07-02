package com.example.cocktailmachine.ui.model.v1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;

import java.util.ArrayList;
import java.util.List;

public class ListRecyclerViewAdapters  {
    public abstract static class ListRecyclerViewAdapter<E extends RowViews.RowView> extends RecyclerView.Adapter<E> {

        RowViews.RowType type;
        private static final String TAG = "ListRecyclerViewAdapter";
        private boolean lock = false;
        private List<E> views = new ArrayList<>();
        protected Recipe recipe=null;

        private boolean delete=false;
        private boolean add=false;

        private Fragment fragment;

        public ListRecyclerViewAdapter(RowViews.RowType type) {
            this.type = type;
        }

        public LinearLayoutManager getManager(android.content.Context getContext){
            LinearLayoutManager llm = new LinearLayoutManager(getContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            return llm;
        }

        public void reload(){
            Log.i(TAG, "reload");
            //views = new ArrayList<>();
            if(recipe!= null) {
                recipe = Recipe.getRecipe(recipe.getID());
            }
            lock = false;
            delete = false;
            add = false;
            putIds();
            notifyDataSetChanged();
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
                add = false;
                for (RowViews.RowView view: views) {
                    view.loadCheck();
                    view.deleteLongListener();
                    view.deleteListener();
                }
                return true;
            }
            return false;
        }

        public void loadAdd(){
            if(!lock && recipe!=null ){
                lock = true;
                add = true;
                delete = false;
                for (RowViews.RowView view: views) {
                    view.loadCheck();
                    view.deleteLongListener();
                    view.deleteListener();
                }
            }
        }

        public void finishDelete(){
            List<RowViews.RowView> trash = new ArrayList<>();
            for (RowViews.RowView view: views) {
                if(view.finishDelete()){
                    trash.add(view);
                }
            }
            views.removeAll(trash);
            lock = false;
            delete = false;
            add = false;
        }

        public void finishAdd(){
            for (RowViews.RowView view: views) {
                view.finishAdd();
                //view.addLongListener(v -> loadDelete());
            }
            lock = false;
            delete = false;
            add = false;
        }

        /*
        void addGoToModelListener(Fragment fragment){
            Log.i(TAG, "addGoToModelListener");
            for(RowViews.RowView view: views){
                view.addGoToListener(fragment);
            }
        }

         */

        protected E addRowView(E view){

            view.addLongListener(
                    v1 -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.itemView.getContext());
                        String typus = "";
                        if(type== RowViews.RowType.pump){
                            typus="diese Pumpe";
                        }else if(type== RowViews.RowType.ingredient){
                            typus="diese Zutat";
                        }else if(type== RowViews.RowType.recipeIngredient){
                            typus="diese Zutat aus dem Rezept";
                        }else if(type== RowViews.RowType.recipeTopic){
                            typus="diesen Serviervorschlag zu dem Rezept";
                        }else if(type== RowViews.RowType.topic){
                            typus="diesen Serviervorschlag";
                        }else if(type== RowViews.RowType.recipe){
                            typus="dieses Rezept";
                        }
                        builder.setMessage("Möchten Sie "+typus+" wirklich löschen?");
                        builder.setTitle("Warnung");
                        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    view.delete();
                                    views.remove(view);
                                } catch (NotInitializedDBException e) {
                                    e.printStackTrace();
                                }
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return false;
                    });

            view.addGoToListener(fragment);
            views.add(view);
            return view;
        }

        public abstract void putIds(List<Long> ids);

        public abstract void putIds();

        public ListRecyclerViewAdapter() {
            super();
        }

        public void putRecipe(Long id){
            this.recipe = Recipe.getRecipe(id);
        }

        public void putRecipe(Recipe recipe){
            this.recipe = recipe;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }

    public static class RecipeListRecyclerViewAdapter extends ListRecyclerViewAdapter<RowViews.RecipeRowView> {
        private List<Recipe> recipes;
        private static final String TAG = "RecipeListRecyclerViewA";

        public RecipeListRecyclerViewAdapter(List<Long> ids) {
            super(RowViews.RowType.recipe);
            Log.i(TAG, "constructor");
            this.putIds(ids);
            Log.i(TAG, "recipes");
            for(Recipe r: recipes){
                Log.i(TAG, "Rezept: "+r.getName());
            }
        }

/*
        public RecipeListRecyclerViewAdapter(List<Recipe> recipes) {
            super(RowViews.RowType.recipe);
            this.recipes = recipes;
        }

 */

        public RecipeListRecyclerViewAdapter() {
            super(RowViews.RowType.recipe);
            Log.i(TAG, "constructor");
            this.putIds();
            Log.i(TAG, "recipes");
            for(Recipe r: recipes){
                Log.i(TAG, "Rezept: "+r.getName());
            }
        }


        public void replaceRecipes(List<Recipe> recipes){
            this.recipes = recipes;
        }

        @NonNull
        @Override
        public RowViews.RecipeRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_name, parent, false);
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
            if(AdminRights.isAdmin()){
                recipes =Recipe.getAllRecipes();
            }else {
                recipes = Recipe.getRecipes();
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
                    .inflate(R.layout.item_name, parent, false);
            return addRowView(RowViews.getTopicInstance(view));
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
            topics = Topic.getTopics();
        }

        @Override
        public void putIds() {
            topics = Topic.getTopics();
        }

        @Override
        public void putRecipe(Long id) {
            super.putRecipe(id);
            topics = Topic.getTopics(super.recipe);
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            topics = Topic.getTopics(super.recipe);
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
                    .inflate(R.layout.item_name, parent, false);
            return addRowView(RowViews.getPumpInstance(view));
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
            pumps = Pump.getPumps();
        }

        @Override
        public void putIds() {
            pumps = Pump.getPumps();
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
                    .inflate(R.layout.item_name, parent, false);
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
            if(AdminRights.isAdmin()){
                data  = Ingredient.getAllIngredients();
            }else {
                data  = Ingredient.getIngredientWithIds();
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

        public RecipeIngredientListRecyclerViewAdapter(Recipe recipe) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(recipe);
        }

        @NonNull
        @Override
        public RowViews.RecipeIngredientRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_name, parent, false);
            return addRowView(RowViews.getRecipeIngredientInstance(view));
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
                data  = Ingredient.getIngredientWithIds();
            }else{
                data = Ingredient.getIngredientWithIds(ids);
            }
        }

        @Override
        public void putIds() {
            data = super.recipe.getIngredients();
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
        public RecipeTopicListRecyclerViewAdapter(Recipe recipe) {
            super(RowViews.RowType.recipeIngredient);
            this.putRecipe(recipe);
        }

        @NonNull
        @Override
        public RowViews.RecipeTopicRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_name, parent, false);
            return addRowView(RowViews.getRecipeTopicInstance(view));
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
            data  = Topic.getTopics();
        }

        @Override
        public void putIds() {
            data  = Topic.getTopics(super.recipe);
        }

        @Override
        public void putRecipe(Long id) {
            super.putRecipe(id);
            data  = Topic.getTopics(super.recipe);
        }

        @Override
        public void putRecipe(Recipe recipe) {
            super.putRecipe(recipe);
            data  = Topic.getTopics(super.recipe);
        }


    }



}
