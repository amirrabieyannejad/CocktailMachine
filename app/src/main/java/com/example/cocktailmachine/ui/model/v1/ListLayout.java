package com.example.cocktailmachine.ui.model.v1;

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

import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;

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
            ImageButton imageButtonListEdit,
            ImageButton imageButtonListAdd

    ) {
        Log.i(TAG, "set");
        setTitle(textViewNameListTitle, title);
        if (adapter.getItemCount() > 0) {
            Log.i(TAG, "set: list with " + adapter.getItemCount() + " items");
            setRecyclerView(recylerViewNames, getContext, adapter, fragment);
            setButtons(recylerViewNames, adapter, getContext,
                    imageButtonListDelete, imageButtonListEdit);
            imageButtonListAdd.setVisibility(View.GONE);
        } else {
            //The list is empty.
            Log.i(TAG, "set: empty list");
            recylerViewNames.setVisibility(View.GONE);
            imageButtonListDelete.setVisibility(View.GONE);
            imageButtonListEdit.setVisibility(View.GONE);
            imageButtonListAdd.setVisibility(View.VISIBLE);
            setAddB(recylerViewNames, getContext, adapter, fragment,
                    imageButtonListDelete, imageButtonListEdit, imageButtonListAdd);
        }
        if(!AdminRights.isAdmin()) {
            imageButtonListAdd.setVisibility(View.INVISIBLE);
            imageButtonListDelete.setVisibility(View.INVISIBLE);
            imageButtonListEdit.setVisibility(View.INVISIBLE);
        }

    }

    private static void littlset(
            //Recycler
            RecyclerView recylerViewNames,
            Context getContext,
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Fragment fragment,
            //Buttons,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit,
            ImageButton imageButtonListAdd

    ) {
        Log.i(TAG, "littleset");
        if (adapter.getItemCount() > 0) {
            Log.i(TAG, "set: list with " + adapter.getItemCount() + " items");
            swapRecyclerView(recylerViewNames, getContext, adapter);
            setButtons(recylerViewNames, adapter, getContext, imageButtonListDelete, imageButtonListEdit);
            imageButtonListAdd.setVisibility(View.GONE);
        } else {
            //The list is empty.
            Log.i(TAG, "set: empty list");
            recylerViewNames.setVisibility(View.GONE);
            imageButtonListDelete.setVisibility(View.GONE);
            imageButtonListEdit.setVisibility(View.GONE);
            imageButtonListAdd.setVisibility(View.VISIBLE);
            setAddB(recylerViewNames, getContext, adapter, fragment, imageButtonListDelete, imageButtonListEdit, imageButtonListAdd);
        }

    }

    private static void setTitle(
            TextView textViewNameListTitle,
            String title) {
        Log.i(TAG, "setTitle");
        textViewNameListTitle.setText(title);
    }

    private static void swapRecyclerView(RecyclerView recylerViewNames,
                                        Context getContext,
                                        ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter) {
        Log.i(TAG, "swapRecyclerView");
        //adapter.addGoToModelListener(fragment);
        recylerViewNames.setLayoutManager(adapter.getManager(getContext));
        recylerViewNames.setAdapter(adapter);
    }



    private static void setRecyclerView(RecyclerView recylerViewNames, Context getContext,
                                        ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
                                        Fragment fragment) {
        Log.i(TAG, "setRecyclerView");
        //adapter.addGoToModelListener(fragment);
        adapter.setFragment(fragment);
        recylerViewNames.setLayoutManager(adapter.getManager(getContext));
        recylerViewNames.setAdapter(adapter);
    }


    private static void setButtons(
            RecyclerView recylerViewNames,
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Context getContext,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit) {
        Log.i(TAG, "setButtons");
        imageButtonListDelete.setOnClickListener(v -> {
            Log.i(TAG, "delete: imageButtonListDelete clicked");
            if (adapter.isCurrentlyDeleting()) {
                Log.i(TAG, "delete: isCurrentlyAdding, finishDelete");
                adapter.finishDelete(getContext);
                adapter.notifyDataSetChanged();
                adapter.reload();
                swapRecyclerView(recylerViewNames,getContext,adapter);
            } else {
                if (adapter.isCurrentlyAdding()) {
                    Log.i(TAG, "delete: isCurrentlyDeleting, Toast");
                    Toast.makeText(getContext,
                                    "Kein Löschen möglich!",
                                    Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Log.i(TAG, "delete: noadding/deleting, loadDelete");
                    adapter.loadDelete();
                }
            }
        });
        if (adapter.type.equals(RowViews.RowType.recipeIngredient)
                || adapter.type.equals(RowViews.RowType.recipeTopic)) {
            imageButtonListEdit.setOnClickListener(v -> {
                Log.i(TAG, "delete: imageButtonListEdit clicked");
                if (adapter.type.equals(RowViews.RowType.recipeIngredient)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
                    builder.setTitle("Wähle neue Zutaten!");
                    builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
                    final List<Ingredient> ingredients;
                    ingredients = Ingredient.getAllIngredients();
                    ingredients.removeAll(adapter.recipe.getIngredients());
                    String[] temp = new String[ingredients.size()];
                    boolean[] tempB = new boolean[ingredients.size()];
                    for (int i = 0; i < ingredients.size(); i++) {
                        temp[i] = ingredients.get(i).getName();
                        tempB[i] = false;
                    }
                    List<Ingredient> chosenIngredients = new ArrayList<>();
                    builder.setMultiChoiceItems(
                            temp,
                            tempB,
                            (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    chosenIngredients.add(ingredients.get(which));
                                } else {
                                    chosenIngredients.remove(ingredients.get(which));
                                }
                            });
                    builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Ingredient i : chosenIngredients) {
                                adapter.recipe.add(getContext,i, -1);
                                adapter.recipe.save(getContext);
                                adapter.reload();
                            }
                        }
                    });
                    builder.show();
                    //recylerViewNames.setAdapter(adapter);
                    swapRecyclerView(recylerViewNames,getContext,adapter);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
                    builder.setTitle("Wähle neue Serviervorschläge!");
                    builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

                    final List<Topic> topics;
                    topics = Topic.getTopics();
                    topics.removeAll(Topic.getTopics(adapter.recipe));
                    String[] temp = new String[topics.size()];
                    boolean[] tempB = new boolean[topics.size()];

                    for (int i = 0; i < topics.size(); i++) {
                        temp[i] = topics.get(i).getName();
                        tempB[i] = false;
                    }
                    List<Topic> chosenTopics = new ArrayList<>();
                    builder.setMultiChoiceItems(
                            temp,
                            tempB,
                            (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    chosenTopics.add(topics.get(which));

                                } else {
                                    chosenTopics.remove(topics.get(which));
                                }
                            });
                    builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Topic t : chosenTopics) {
                                adapter.recipe.add(getContext,t);
                                adapter.recipe.save(getContext);
                                adapter.reload();
                            }
                        }
                    });
                    builder.show();
                    //recylerViewNames.setAdapter(adapter);
                    swapRecyclerView(recylerViewNames,getContext,adapter);

                }

            });
        } else {
            imageButtonListEdit.setVisibility(View.INVISIBLE);
        }
    }

    private static void setAddB(
            RecyclerView recylerViewNames,
                                Context getContext,
                                ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
                                Fragment fragment,
                                //Buttons,
                                ImageButton imageButtonListDelete,
                                ImageButton imageButtonListEdit,
                                ImageButton imageButtonListAdd) {
        if (adapter.type.equals(RowViews.RowType.recipeIngredient) || adapter.type.equals(RowViews.RowType.recipeTopic)) {
            imageButtonListAdd.setOnClickListener(v -> {
                Log.i(TAG, "delete: imageButtonListEdit clicked");
                if (adapter.type.equals(RowViews.RowType.recipeIngredient)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
                    builder.setTitle("Wähle neue Zutaten!");
                    builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
                    final List<Ingredient> ingredients;
                    ingredients = Ingredient.getAllIngredients();
                    ingredients.removeAll(adapter.recipe.getIngredients());
                    String[] temp = new String[ingredients.size()];
                    boolean[] tempB = new boolean[ingredients.size()];
                    for (int i = 0; i < ingredients.size(); i++) {
                        temp[i] = ingredients.get(i).getName();
                        tempB[i] = false;
                    }
                    List<Ingredient> chosenIngredients = new ArrayList<>();
                    builder.setMultiChoiceItems(
                            temp,
                            tempB,
                            (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    chosenIngredients.add(ingredients.get(which));
                                } else {
                                    chosenIngredients.remove(ingredients.get(which));
                                }
                            });
                    builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Ingredient i : chosenIngredients) {
                                adapter.recipe.add(getContext,i, -1);
                                adapter.recipe.save(getContext);
                                adapter.reload();
                            }
                        }
                    });
                    builder.show();
                    //recylerViewNames.setAdapter(adapter);
                    littlset(recylerViewNames,
                            getContext,
                            adapter,
                            fragment,
                            imageButtonListDelete,
                            imageButtonListEdit,
                            imageButtonListAdd);


                } else if (adapter.type.equals(RowViews.RowType.recipeTopic)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
                    builder.setTitle("Wähle neue Serviervorschläge!");
                    builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

                    final List<Topic> topics;
                    topics = Topic.getTopics();
                    topics.removeAll(Topic.getTopics(adapter.recipe));
                    String[] temp = new String[topics.size()];
                    boolean[] tempB = new boolean[topics.size()];

                    for (int i = 0; i < topics.size(); i++) {
                        temp[i] = topics.get(i).getName();
                        tempB[i] = false;
                    }
                    List<Topic> chosenTopics = new ArrayList<>();
                    builder.setMultiChoiceItems(
                            temp,
                            tempB,
                            (dialog, which, isChecked) -> {
                                if (isChecked) {
                                    chosenTopics.add(topics.get(which));

                                } else {
                                    chosenTopics.remove(topics.get(which));
                                }
                            });
                    builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Topic t : chosenTopics) {
                                adapter.recipe.add(getContext,t);
                                adapter.recipe.save(getContext);
                                adapter.reload();
                            }
                        }
                    });
                    builder.show();
                    //recylerViewNames.setAdapter(adapter);
                    littlset(recylerViewNames,
                            getContext,
                            adapter,
                            fragment,
                            imageButtonListDelete,
                            imageButtonListEdit,
                            imageButtonListAdd);

                }

            });

        }


    }
}
