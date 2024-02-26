package com.example.cocktailmachine.ui.model.helper.ListConfigurePumps;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.ui.model.helper.ConfigurePumps;

import java.util.HashMap;
import java.util.List;

public class RecyclerAdapterListIngrediente extends RecyclerView.Adapter<RecyclerAdapterListIngrediente.IngtedienceViewHolder> {

    Context context;
    List<Ingredient> ingredients;

    HashMap<String, Long> ingredientsMap;

    Ingredient chosenIngredient;
    String chosenIngredientString;

    RecyclerViewListenerListIngrediente selectionListener;

    public RecyclerAdapterListIngrediente(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
        this.selectionListener=(ListIngrediente)context;

    }

    public RecyclerAdapterListIngrediente(Ingredient chosenIngredient, List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;

        //Todo Philipp des Listener muss wieder eingerichtet werden
        //this.selectionListener=(ListIngredience)context;
        this.chosenIngredient = chosenIngredient;

    }



    public RecyclerAdapterListIngrediente(String chosenIngredient, HashMap<String, Long> ingredients, Context context) {
        this.ingredientsMap = ingredients;
        this.context = context;

        //Todo Philipp des Listener muss wieder eingerichtet werden
        //this.selectionListener=(ListIngredience)context;
        this.chosenIngredientString = chosenIngredient;

    }

    public RecyclerAdapterListIngrediente(Ingredient chosenIngredient, List<Ingredient> ingredients, ConfigurePumps configurePumps) {
        this.ingredients = ingredients;

        //Todo Philipp des Listener muss wieder eingerichtet werden
        this.selectionListener=configurePumps;
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
        if (chosenIngredient != null && ingredients.get(position).getName().equals(chosenIngredient.getName())){
            holder.cardElement.setCardBackgroundColor(Color.parseColor("#6750A3"));
            holder.ingtedienceName.setTextColor(Color.WHITE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.selectIngredience(ingredients.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.ingredients.size();
    }

    public static class IngtedienceViewHolder extends RecyclerView.ViewHolder {
        public TextView ingtedienceName;
        public CardView cardElement;



        public IngtedienceViewHolder(View itemView) {
            super(itemView);
            ingtedienceName = itemView.findViewById(R.id.textViewListElementIngredientName);
            cardElement = itemView.findViewById(R.id.cardViewListElementIngredient);
        }
    }
}
