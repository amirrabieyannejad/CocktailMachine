package com.example.cocktailmachine.ui.model;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

public class RowViews {

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

        private CheckBox checkBox;
        private TextView name;
        private TextView desc;
        private View.OnLongClickListener longClickListener;

        RowView(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox_item);
            name = itemView.findViewById(R.id.textView_item_name);
            desc = itemView.findViewById(R.id.textView_item_desc);

            longClickListener = v -> {
                loadDelete();
                return true;
            };

            name.setOnLongClickListener(longClickListener);
            desc.setOnLongClickListener(longClickListener);
        }

        void setName(String name){
            this.name.setText(name);
        }

        void setDesc(String desc){
            this.desc.setText(desc);
            this.desc.setVisibility(View.VISIBLE);
        }

        private void loadDelete(){
            checkBox.setVisibility(View.VISIBLE);

            name.setOnLongClickListener(null);
            desc.setOnLongClickListener(null);
        }

        private void finishDelete(){
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
            name.setOnLongClickListener(longClickListener);
            desc.setOnLongClickListener(longClickListener);
        }

        private void error(){
            Toast.makeText(itemView.getContext(), "Datenbankverbindungsfehler", Toast.LENGTH_SHORT).show();
        }

        abstract void delete() throws NotInitializedDBException;

        abstract long getId();
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
        long getId() {
            return this.recipe.getID();
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
        long getId() {
            return this.topic.getID();
        }
    }

    public static class RecipeTopicRowView extends RowView {
        //HashMap<Long, Integer>
        private Recipe recipe;
        private Topic topic;

        RecipeTopicRowView(@NonNull View itemView) {
            super(itemView);
        }

        public void setRecipeTopic(Recipe recipe, Topic topic){
            this.recipe = recipe;
            this.topic = topic;
            super.setName(this.topic.getName());
        }


        @Override
        void delete() throws NotInitializedDBException {
            this.recipe.remove(topic);
        }
        @Override
        long getId() {
            return this.topic.getID();
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
        long getId() {
            return this.ingredient.getID();
        }
    }

    public static class RecipeIngredientRowView extends RowView {
        //HashMap<Long, Integer>
        private Recipe recipe;
        private Ingredient ingredient;
        private int volume;

        RecipeIngredientRowView(@NonNull View itemView) {
            super(itemView);
        }

        public void setIngredientVolume(Recipe recipe, Ingredient ingredient,int volume){
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
        long getId() {
            return this.ingredient.getID();
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
        long getId() {
            return this.pump.getID();
        }
    }
}
