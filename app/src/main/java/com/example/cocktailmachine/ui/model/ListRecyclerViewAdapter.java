package com.example.cocktailmachine.ui.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<RowViews.RowView> {

    private RowViews.RowType type;

    public ListRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public RowViews.RowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_name_list, parent, false);
        return RowViews.getInstance(type, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViews.RowView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public void loadDelete(){
    }

    public void finishDelete(){

    }


}
