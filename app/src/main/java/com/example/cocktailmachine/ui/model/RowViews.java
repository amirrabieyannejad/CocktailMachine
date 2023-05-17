package com.example.cocktailmachine.ui.model;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class RowViews {
    private static final String TAG = "RowViews";

    public static enum RowType{
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

    public static TopicRowView gettopicInstance(View view){
        return new TopicRowView(view);
    }

    public static PumpRowView getpumpInstance(View view){
        return new PumpRowView(view);
    }

    public static RecipeIngredientRowView getrecipeIngredientInstance(View view){
        return new RecipeIngredientRowView(view);
    }

    public static RecipeTopicRowView getrecipeTopicInstance(View view){
        return new RecipeTopicRowView(view);
    }




    public static abstract class RowView extends RecyclerView.ViewHolder {

        private ConstraintLayout layout;
        private CheckBox checkBox;
        private TextView name;
        private TextView desc;

        //private View.OnLongClickListener longClickListener;

        protected Recipe recipe;

        RowView(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraintLayout_item);
            checkBox = itemView.findViewById(R.id.checkBox_item);
            name = itemView.findViewById(R.id.textView_item_name);
            desc = itemView.findViewById(R.id.textView_item_desc);
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
        }

        public void finishDelete(){
            if(checkBox.isChecked()){
                try {
                    delete();
                } catch (NotInitializedDBException e) {
                    e.printStackTrace();
                    error();
                }
            }
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }

        public void addListener(View.OnClickListener listener){
            layout.setOnClickListener(listener);
        }

        public void deleteListener(){
            layout.setOnClickListener(null);
        }

        public void addLongListener(View.OnLongClickListener listener){
            layout.setOnLongClickListener(listener);
        }

        public void deleteLongListener(){
            layout.setOnLongClickListener(null);
        }

        private void error(){
            Toast.makeText(itemView.getContext(), "Datenbankverbindungsfehler", Toast.LENGTH_SHORT).show();
        }

        abstract void delete() throws NotInitializedDBException;

        abstract void add() throws NotInitializedDBException;
    }

    public static class RecipeRowView extends RowView {
        private Recipe recipe;

        RecipeRowView(@NonNull View itemView) {
            super(itemView);
        }

        public void setRecipe(Recipe recipe){
            this.recipe = recipe;
            super.setName(recipe.getName());
        }

        void delete() throws NotInitializedDBException{
            this.recipe.delete();
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

        public void setTopic(Topic topic){
            this.recipe = recipe;
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

        public void setIngredientVolume(Ingredient ingredient,int volume){
            this.recipe = recipe;
            this.ingredient = ingredient;
            this.volume = volume;
            super.setName(this.ingredient.getName());
            super.setDesc(this.volume+" ml");
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

        public void setIngredient(Pump pump){
            this.pump = pump;
            super.setName("Pumpe "+pump.getIngredientName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.pump.delete();
        }

        @Override
        void add() throws NotInitializedDBException {
            Log.i(TAG, "should not reach");
        }
    }
}
