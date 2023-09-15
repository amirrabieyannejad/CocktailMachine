package com.example.cocktailmachine.ui.ListOfIngredience;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecyclerAdapterListIngredience extends RecyclerView.Adapter<RecyclerAdapterListIngredience.IngtedienceViewHolder> {

    Context context;
    List<Ingredient> ingredients;

    Ingredient chosenIngredient;

    RecyclerViewListenerListIngredience selectionListener;

    public RecyclerAdapterListIngredience(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
        this.selectionListener=(ListIngredience)context;

    }

    public RecyclerAdapterListIngredience(Ingredient chosenIngredient, List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
        this.selectionListener=(ListIngredience)context;
        this.chosenIngredient = chosenIngredient;

    }

    @NonNull
    @Override
    public IngtedienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_ingredients, parent, false);
        return new IngtedienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngtedienceViewHolder holder, int position) {
        holder.ingtedienceName.setText(this.ingredients.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.selectIngredience(ingredients.get(holder.getAdapterPosition()).getID());
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.ingredients.size();
    }

    public static class IngtedienceViewHolder extends RecyclerView.ViewHolder {
        public TextView ingtedienceName;



        public IngtedienceViewHolder(View itemView) {
            super(itemView);
            ingtedienceName = itemView.findViewById(R.id.textViewListElementIngredientName);
        }
    }
}
