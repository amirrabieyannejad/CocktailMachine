package com.example.cocktailmachine.ui.ListOfIngredience;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterListIngredience {




    public static class IngtedienceViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView bearbeiter;
        public TextView verwalter;


        public IngtedienceViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textViewProjektBearbeitenNameDesNutzers);
            bearbeiter = itemView.findViewById(R.id.textViewProjektBearbeitenBearbeiter);
            verwalter = itemView.findViewById(R.id.textViewProjektBearbeitenVerwalter);
        }
    }
}
