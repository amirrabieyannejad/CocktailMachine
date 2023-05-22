package com.example.cocktailmachine.ui.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ListLayout {
    private static final String TAG = "ListLayout";

    protected static void set(
            //Title
            TextView textViewNameListTitle, String title,
            //Recycler
            RecyclerView recylerViewNames, Context getContext,
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Fragment fragment,
            //Buttons,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit

    ){
        Log.i(TAG, "set");
        setTitle(textViewNameListTitle, title);
        setRecyclerView(recylerViewNames, getContext, adapter, fragment);
        setButtons(adapter, getContext,imageButtonListDelete, imageButtonListEdit);

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
        recylerViewNames.setLayoutManager(adapter.getManager(getContext));
        recylerViewNames.setAdapter(adapter);
        adapter.addGoToModelListener(fragment);
    }


    private static void setButtons(
            ListRecyclerViewAdapters.ListRecyclerViewAdapter adapter,
            Context getContext,
            ImageButton imageButtonListDelete,
            ImageButton imageButtonListEdit){
        Log.i(TAG, "setButtons");
        imageButtonListDelete.setOnClickListener(v -> {
            if(adapter.isCurrentlyDeleting()){
                adapter.finishDelete();
            }else{
                if(adapter.isCurrentlyAdding()){
                    Toast.makeText(getContext,
                                    "Kein Löschen möglich!",
                                    Toast.LENGTH_SHORT)
                            .show();
                }else {
                    adapter.loadDelete();
                }
            }
        });
        imageButtonListEdit.setOnClickListener(v -> {
            if(adapter.isCurrentlyAdding()){
                adapter.finishAdd();
            }else{
                if(adapter.isCurrentlyDeleting()){
                    Toast.makeText(
                                    getContext,
                                    "Kein Editieren möglich!",
                                    Toast.LENGTH_SHORT)
                            .show();
                }else {
                    adapter.loadAdd();
                }
            }
        });
    }




}
