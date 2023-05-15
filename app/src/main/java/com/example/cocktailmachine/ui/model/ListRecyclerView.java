package com.example.cocktailmachine.ui.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListRecyclerView extends RecyclerView {
    private RowViews.RowType type;

    public ListRecyclerView(@NonNull Context context) {
        super(context);
    }

    public void setUp(RowViews.RowType type){
        this.type = type;
        switch (type){
            case recipe:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter());
            case ingredient:
                this.setAdapter(new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter());
            case topic:
                this.setAdapter(new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter());
            case pump:
                this.setAdapter(new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter());

            default: return;
        }
    }

    public void setUp(RowViews.RowType type, List<Long> ids){
        this.type = type;
        switch (type){
            case recipe:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter(ids));
            case ingredient:
                this.setAdapter(new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter(ids));
            case topic:
                this.setAdapter(new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter(ids));
            case pump:
                this.setAdapter(new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter());

            default: return;
        }
    }

    public void setUp(RowViews.RowType type, Long recipeId, List<Long> ids){
        this.type = type;
        switch (type){
            case recipe:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter(ids));
            case ingredient:
                this.setAdapter(new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter(ids));
            case topic:
                this.setAdapter(new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter(ids));
            case pump:
                this.setAdapter(new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter());
            case recipeIngredient:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipeId));
            case recipeTopic:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipeId));

            default: return;
        }
    }

    public void setUp(RowViews.RowType type, Long recipeId){
        this.type = type;
        switch (type){
            case recipe:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeListRecyclerViewAdapter());
            case ingredient:
                this.setAdapter(new ListRecyclerViewAdapters.IngredientListRecyclerViewAdapter());
            case topic:
                this.setAdapter(new ListRecyclerViewAdapters.TopicListRecyclerViewAdapter());
            case pump:
                this.setAdapter(new ListRecyclerViewAdapters.PumpListRecyclerViewAdapter());
            case recipeIngredient:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipeId));
            case recipeTopic:
                this.setAdapter(new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipeId));

            default: return;
        }
    }

    public void finish(){
        ((ListRecyclerViewAdapters.ListRecyclerViewAdapter)this.getAdapter()).finishDelete();

    }


}
