package com.example.cocktailmachine.ui.ListOfPumps;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Pump;

import java.util.List;

public class RecyclerAdapterListPumps extends RecyclerView.Adapter<RecyclerAdapterListPumps.IngtedienceViewHolder> {

    Context context;
    List<Pump> listPumps;

    RecyclerViewListenerListPumps selectionListener;

    public RecyclerAdapterListPumps(List<Pump> listPumps, Context context,RecyclerViewListenerListPumps recyclerViewListener) {
        this.listPumps = listPumps;
        this.context = context;
        this.selectionListener=recyclerViewListener;

    }


    @NonNull
    @Override
    public IngtedienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_pumps, parent, false);
        return new IngtedienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngtedienceViewHolder holder, int position) {
        Pump pump = this.listPumps.get(position);
        int icon = 0;
        int color = 0;

        if(pump.getIngredientName()=="" || pump.getVolume(this.context)<=0){
            color = ResourcesCompat.getColor(context.getResources(), R.color.color_warning, null);
            icon = R.drawable.ic_attention;
        }else{
            color = ResourcesCompat.getColor(context.getResources(), R.color.color_ok, null);
            icon = R.drawable.ic_ok;
        }
        holder.cardElement.setCardBackgroundColor(color);
        holder.ingtedienceName.setCompoundDrawables(getResicedDrawable(icon,75), null, null, null);
        holder.ingtedienceName.setText("Pumpe " + pump.getSlot());

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

    Drawable getResicedDrawable(int idOfDraweble, int size){
        Drawable drawable = ContextCompat.getDrawable(context,idOfDraweble);
        drawable.setBounds(0,0,size,size);
        return drawable;
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
