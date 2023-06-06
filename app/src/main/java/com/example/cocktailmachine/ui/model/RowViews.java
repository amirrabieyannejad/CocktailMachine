package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

public class RowViews {
    private static final String TAG = "RowViews";

    public enum RowType{
        recipe, topic, ingredient,pump, recipeIngredient, recipeTopic;
    }

    public static RowView getInstance(RowViews.RowType type, View view){
        switch (type){
            case recipe: return new RecipeRowView(view);
            case ingredient:return new IngredientRowView(view);
            case topic:return new TopicRowView(view);
            case pump:return new PumpRowView(view);
            case recipeIngredient:return new RecipeIngredientRowView(view);
            case recipeTopic:return new RecipeTopicRowView(view);

            default: return null;
        }
    }

    public static RecipeRowView getRecipeInstance(View view){
        return new RecipeRowView(view);
    }

    public static IngredientRowView getIngredientInstance(View view){
        return new IngredientRowView(view);
    }

    public static TopicRowView getTopicInstance(View view){
        return new TopicRowView(view);
    }

    public static PumpRowView getPumpInstance(View view){
        return new PumpRowView(view);
    }

    public static RecipeIngredientRowView getRecipeIngredientInstance(View view){
        return new RecipeIngredientRowView(view);
    }

    public static RecipeTopicRowView getRecipeTopicInstance(View view){
        return new RecipeTopicRowView(view);
    }




    public static abstract class RowView extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView name;
        private TextView desc;

        private View.OnLongClickListener longClickListener;
        private View.OnClickListener listener;

        protected Recipe recipe;

        RowView(@NonNull View itemView) {
            super(itemView);
            if(itemView == null){
                Log.i(TAG, "itemView is null");
            }if(itemView.getContext()==null){
                Log.i(TAG, "itemView is null");
            }
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_item);
            checkBox.setVisibility(View.GONE);
            if(checkBox == null){
                Log.i(TAG, "checkBox is null");
            }
            name = (TextView) itemView.findViewById(R.id.textView_item_name);
            if(name == null){
                Log.i(TAG, "name is null");
            }
            desc = (TextView) itemView.findViewById(R.id.textView_item_desc);
            desc.setVisibility(View.GONE);
            if(desc == null){
                Log.i(TAG, "desc is null");
            }
        }

        void setName(String name){
            this.name.setText(name);
        }

        void setDesc(String desc){
            this.desc.setText(desc);
            this.desc.setVisibility(View.VISIBLE);
        }

        public void loadCheck(){
            checkBox.setVisibility(View.VISIBLE);
        }

        public void setRecipe(Long recipe_id){
            setRecipe(Recipe.getRecipe(recipe_id));
        }

        public void setRecipe(Recipe recipe){
            this.recipe = recipe;
        }


        void finishAdd(){
            if(checkBox.isChecked()){
                try {
                    add();
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                    error();
                }
            }else{
                try {
                    delete();
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                    error();
                }
            }
            checkBox.setVisibility(View.GONE);
            loadListener();
            loadLongListener();
        }

        public boolean finishDelete(){
            if(checkBox.isChecked()){
                try {
                    delete();
                    checkBox.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                    loadListener();
                    loadLongListener();
                    return true;
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                    error();
                    return false;
                }
            }
            return false;
        }

        abstract Bundle getGoToModelBundle();

        void addGoToListener(Fragment fragment){
            Log.i(TAG, "goToListener");
            if(fragment instanceof ModelFragment) {
                addListener(v -> {
                    Log.i(TAG, "goto: ModelFragment");
                    NavHostFragment
                        .findNavController(fragment)
                        .navigate(R.id.action_modelFragment_self,
                                getGoToModelBundle());
                });
            }else if(fragment instanceof ListFragment) {
                addListener(v -> {
                    Log.i(TAG, "goto: ListFragment");
                    NavHostFragment
                        .findNavController(fragment)
                        .navigate(R.id.action_listFragment_to_modelFragment,
                                getGoToModelBundle());
                });
            }
        }

        public void addListener(View.OnClickListener listener){
            Log.i(TAG, "addListener");
            this.listener = listener;
            super.itemView.setOnClickListener(listener);
            this.name.setOnClickListener(listener);
            this.desc.setOnClickListener(listener);
        }

        public void loadListener(){
            Log.i(TAG, "loadListener");
            super.itemView.setOnClickListener(listener);
            this.name.setOnClickListener(listener);
            this.desc.setOnClickListener(listener);
        }

        public void deleteListener(){
            Log.i(TAG, "deleteListener");
            super.itemView.setOnClickListener(null);
            this.name.setOnClickListener(null);
            this.desc.setOnClickListener(null);
        }

        public void addLongListener(View.OnLongClickListener listener){
            Log.i(TAG, "addLongListener");
            this.longClickListener = listener;
            if(super.itemView == null){
                Log.i(TAG, "HERE HERE, super.itemView is null");
            }else {
                loadLongListener();
            }
        }

        public void loadLongListener(){
            Log.i(TAG, "loadLongListener");
            super.itemView.setOnLongClickListener(longClickListener);
            this.name.setOnLongClickListener(longClickListener);
            this.desc.setOnLongClickListener(longClickListener);
        }

        public void deleteLongListener(){
            Log.i(TAG, "deleteLongListener");
            super.itemView.setOnLongClickListener(null);
            this.name.setOnLongClickListener(null);
            this.desc.setOnLongClickListener(null);
        }





        private void error(){
            Toast.makeText(itemView.getContext(), "Datenbankverbindungsfehler", Toast.LENGTH_SHORT).show();
        }

        abstract void delete() throws NotInitializedDBException;

        abstract void add() throws NotInitializedDBException;
    }

    public static class RecipeRowView extends RowView {
        RecipeRowView(@NonNull View itemView) {
            super(itemView);
        }

        public void setRecipe(Recipe recipe){
            this.recipe = recipe;
            super.setName(recipe.getName());
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.RECIPE.toString());
            b.putLong("ID", recipe.getID());
            return b;
        }

        void delete() throws NotInitializedDBException{
            this.recipe.delete();
            this.recipe.save();
        }

        @Override
        void add() throws NotInitializedDBException {
            error();
        }

        private void error(){
            Log.i(TAG, "should not reach");
        }

    }

    public static class TopicRowView extends RowView {
        private Topic topic;

        TopicRowView(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.TOPIC.toString());
            b.putLong("ID", topic.getID());
            return b;
        }

        public void setTopic(Topic topic){
            this.topic = topic;
            super.setName(topic.getName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.topic.delete();
        }

        @Override
        void add() throws NotInitializedDBException {
            if(super.recipe!= null) {
                super.recipe.addOrUpdate(topic);
            }
        }
    }

    public static class RecipeTopicRowView extends RowView {
        //HashMap<Long, Integer>
        private Topic topic;

        RecipeTopicRowView(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.TOPIC.toString());
            b.putLong("ID", topic.getID());
            return b;
        }

        public void setTopic(Topic topic){
            this.topic = topic;
            super.setName(this.topic.getName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.recipe.remove(topic);
        }

        @Override
        void add() throws NotInitializedDBException {
            Log.i(TAG, "should not reach");
            if(super.recipe!= null) {
                super.recipe.addOrUpdate(topic);
            }
        }
    }

    public static class IngredientRowView extends RowView {
        private Ingredient ingredient;

        IngredientRowView(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.INGREDIENT.toString());
            b.putLong("ID", ingredient.getID());
            return b;
        }

        public void setIngredient(Ingredient ingredient){
            this.ingredient = ingredient;
            super.setName(ingredient.getName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.ingredient.delete();
        }

        @Override
        void add() throws NotInitializedDBException {
            if(super.recipe!= null) {
                super.recipe.addOrUpdate(ingredient, -1);
            }
        }
    }

    public static class RecipeIngredientRowView extends RowView {
        private Ingredient ingredient;
        private int volume;

        RecipeIngredientRowView(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.INGREDIENT.toString());
            b.putLong("ID", ingredient.getID());
            return b;
        }

        public void setIngredientVolume(Ingredient ingredient, int volume){
            this.ingredient = ingredient;
            this.volume = volume;
            super.setName(this.ingredient.getName());
            super.setDesc(this.volume+" ml");
        }

        public void setIngredientVolume(Ingredient ingredient){
            this.ingredient = ingredient;
            super.setName(this.ingredient.getName());
        }

        @Override
        void delete() throws NotInitializedDBException {
            this.recipe.remove(ingredient);
        }

        @Override
        void add() throws NotInitializedDBException {
            if(super.recipe!= null) {
                super.recipe.addOrUpdate(ingredient, volume);
            }
        }
    }

    public static class PumpRowView extends RowView {
        private Pump pump;

        PumpRowView(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        Bundle getGoToModelBundle() {
            Bundle b = new Bundle();
            b.putString("Type", ModelFragment.ModelType.PUMP.toString());
            b.putLong("ID", pump.getID());
            return b;
        }

        public void setIngredient(Pump pump){
            this.pump = pump;
            super.setName("Pumpe "+pump.getIngredientName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.pump.delete();
            this.pump.save();
        }

        @Override
        void add() throws NotInitializedDBException {
            Log.i(TAG, "should not reach");
        }
    }
}
