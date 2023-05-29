package com.example.cocktailmachine.ui.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;

import java.util.ArrayList;
import java.util.List;

public class ListLayout {
    private static final String TAG = "ListLayout";

    protected static void set(
            //Title
            TextView textViewNameListTitle,
            String title,
            //Recycler
            RecyclerView recylerViewNames,
            Context getContext,
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Fragment fragment,
            //Buttons,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit

    ){
        Log.i(TAG, "set");
        setTitle(textViewNameListTitle, title);
        if(adapter.getItemCount()>0) {
            Log.i(TAG, "set: list with "+adapter.getItemCount()+" items");
            setRecyclerView(recylerViewNames, getContext, adapter, fragment);
            setButtons(adapter, getContext, imageButtonListDelete, imageButtonListEdit);
        }else{
            //The list is empty.
            Log.i(TAG, "set: empty list");
            recylerViewNames.setVisibility(View.GONE);
            imageButtonListDelete.setVisibility(View.GONE);
            imageButtonListEdit.setVisibility(View.GONE);
        }

    }

    private static void setTitle(
            TextView textViewNameListTitle,
            String title){
        Log.i(TAG, "setTitle");
        textViewNameListTitle.setText(title);
    }

    private static void setRecyclerView(RecyclerView recylerViewNames, Context getContext,
                                 ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
                                 Fragment fragment){
        Log.i(TAG, "setRecyclerView");
        //adapter.addGoToModelListener(fragment);
        adapter.setFragment(fragment);
        recylerViewNames.setLayoutManager(adapter.getManager(getContext));
        recylerViewNames.setAdapter(adapter);
    }


    private static void setButtons(
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Context getContext,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit){
        Log.i(TAG, "setButtons");
        imageButtonListDelete.setOnClickListener(v -> {
            Log.i(TAG,"delete: imageButtonListDelete clicked");
            if(adapter.isCurrentlyDeleting()){
                Log.i(TAG,"delete: isCurrentlyAdding, finishDelete");
                adapter.finishDelete();
            }else{
                if(adapter.isCurrentlyAdding()){
                    Log.i(TAG,"delete: isCurrentlyDeleting, Toast");
                    Toast.makeText(getContext,
                                    "Kein Löschen möglich!",
                                    Toast.LENGTH_SHORT)
                            .show();
                }else {
                    Log.i(TAG,"delete: noadding/deleting, loadDelete");
                    adapter.loadDelete();
                }
            }
        });
        if(adapter.type.equals(RowViews.RowType.recipeIngredient)||adapter.type.equals(RowViews.RowType.recipeTopic)) {
            imageButtonListEdit.setOnClickListener(v -> {
                Log.i(TAG, "delete: imageButtonListEdit clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
                builder.setTitle("Wähle neue Zutaten!");
                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final List<Ingredient> ingredients;
                final List<Topic> topics;
                if(adapter.type.equals(RowViews.RowType.recipeIngredient)) {
                    topics = new ArrayList<>();
                    ingredients = Ingredient.getIngredientWithIds();
                    ingredients.removeAll(adapter.recipe.getIngredients());
                }else{
                    ingredients = new ArrayList<>();
                    topics = Topic.getTopics();
                    topics.removeAll(Topic.getTopics(adapter.recipe));
                }
                String[] temp = new String[ingredients.size()];
                boolean[] tempB = new boolean[ingredients.size()];
                for (int i = 0; i < ingredients.size(); i++) {
                    temp[i] = ingredients.get(i).getName();
                    tempB[i] = false;
                }
                List<Ingredient> chosenIngredients = new ArrayList<>();
                List<Topic> chosenTopics = new ArrayList<>();
                builder.setMultiChoiceItems(
                        temp,
                        tempB,
                        (DialogInterface.OnMultiChoiceClickListener) (dialog, which, isChecked) -> {
                            if (isChecked) {
                                if(!ingredients.isEmpty()){
                                    chosenIngredients.add(ingredients.get(which));
                                }else{
                                    chosenTopics.add(topics.get(which));
                                }
                            } else {
                                if(!ingredients.isEmpty()){
                                    chosenIngredients.remove(ingredients.get(which));
                                }else{
                                    chosenTopics.remove(topics.get(which));
                                }
                            }
                        });
                builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Ingredient i : chosenIngredients) {
                            adapter.recipe.addOrUpdate(i, -1);
                        }
                        for (Topic t : chosenTopics) {
                            adapter.recipe.addOrUpdate(t);
                        }
                    }
                });
            });
        }else{
            imageButtonListEdit.setVisibility(View.INVISIBLE);
        }
    }




}
