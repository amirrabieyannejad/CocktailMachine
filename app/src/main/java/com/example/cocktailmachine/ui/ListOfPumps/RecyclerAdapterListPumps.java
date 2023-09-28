package com.example.cocktailmachine.ui.ListOfPumps;

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
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.ListOfIngredience.ListIngredience;

import java.util.List;

public class RecyclerAdapterListPumps extends RecyclerView.Adapter<RecyclerAdapterListPumps.IngtedienceViewHolder> {

    Context context;
    List<Pump> listPumps;

    RecyclerViewListenerListPumps selectionListener;

    public RecyclerAdapterListPumps(List<Pump> listPumps, Context context) {
        this.listPumps = listPumps;
        this.context = context;
        this.selectionListener=(ListOfPumps)context;

    }


    @NonNull
    @Override
    public IngtedienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_pumps, parent, false);
        return new IngtedienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngtedienceViewHolder holder, int position) {
        holder.ingtedienceName.setText("Pumpe " + this.listPumps.get(position).getSlot());
        /**if (chosenIngredient != null && listPumps.get(position).getName().equals(chosenIngredient.getName())){
            holder.cardElement.setCardBackgroundColor(Color.parseColor("#6750A3"));
            holder.ingtedienceName.setTextColor(Color.WHITE);

        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionListener.selectedPump(listPumps.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.listPumps.size();
    }

    public static class IngtedienceViewHolder extends RecyclerView.ViewHolder {
        public TextView ingtedienceName;
        public CardView cardElement;



        public IngtedienceViewHolder(View itemView) {
            super(itemView);
            ingtedienceName = itemView.findViewById(R.id.textViewListElementPumpName);
            cardElement = itemView.findViewById(R.id.cardViewListElementPumps);
        }
    }
}
