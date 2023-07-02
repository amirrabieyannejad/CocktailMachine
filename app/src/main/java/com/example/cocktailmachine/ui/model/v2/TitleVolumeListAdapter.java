package com.example.cocktailmachine.ui.model.v2;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johanna Reidt
 * @created Mi. 28.Jun 2023 - 11:03
 * @project CocktailMachine
 */
public class TitleVolumeListAdapter extends RecyclerView.Adapter<TitleVolumeListAdapter.TitleVolumeRow> {

    private static final String TAG = "TitleVolumeListAdapter";
    List<Ingredient> ingredients = new ArrayList<>();
    ModelType modelType = ModelType.INGREDIENT;
    Activity activity;
    Recipe recipe;

    TitleVolumeListAdapter(Activity activity,
                           Recipe recipe){
        this.activity = activity;
        this.ingredients = recipe.getIngredients();
        this.recipe = recipe;
    }


    @NonNull
    @Override
    public TitleVolumeListAdapter.TitleVolumeRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TitleVolumeListAdapter.TitleVolumeRow(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_title_volume,
                                parent,
                                false));
    }

    @Override
    public void onBindViewHolder(@NonNull TitleVolumeListAdapter.TitleVolumeRow holder, int position) {
        holder.set(activity,
                this.ingredients.get(position),
                this.modelType);
    }

    @Override
    public int getItemCount() {
        return this.ingredients.size();
    }



    public static class TitleVolumeRow extends RecyclerView.ViewHolder {

        private static final String TAG = "TitleVolumeRow";
        private ModelType modelType = ModelType.INGREDIENT;
        private Ingredient ingredient;

        private TextView title;
        private TextView vol;

        public TitleVolumeRow(@NonNull View itemView) {
            super(itemView);
            if (itemView == null) {
                Log.i(TAG, "itemView is null");
            }
            if (itemView.getContext() == null) {
                Log.i(TAG, "itemView is null");
            }
            title = (TextView) itemView.findViewById(R.id.textView_title_volume_titel);
            if (title == null) {
                Log.i(TAG, "TextView title is null");
            }
            vol = (TextView) itemView.findViewById(R.id.textView_title_volume_volume);
            if (vol == null) {
                Log.i(TAG, "TextView title is null");
            }
        }

        /**
         * set content
         * @author Johanna Reidt
         * @param activity
         * @param ingredient
         * @param modelType
         */
        private void set(Activity activity,
                         Ingredient ingredient,
                         ModelType modelType){
            this.ingredient = ingredient;
            this.modelType = modelType;
            this.title.setText(this.ingredient.getName());
            this.vol.setText(this.ingredient.getVolume());
            this.title.setOnClickListener(v ->
                    GetActivity.goTo(
                            activity,
                            FragmentType.Model,
                            ModelType.INGREDIENT,
                            this.ingredient.getID()));
            this.vol.setOnClickListener(v ->
                    GetActivity.goTo(
                            activity,
                            FragmentType.Model,
                            ModelType.PUMP,
                            this.ingredient.getID()));
            if (AdminRights.isAdmin()) {
                //TO DO: open AlertDialog to delete
                this.title.setOnLongClickListener(v -> {
                    GetDialog.delete(
                            activity,
                            modelType,
                            this.ingredient.getName(),
                            this.ingredient.getID());
                    return GetDialog.checkDeleted(modelType, this.ingredient.getID());
                });
                this.vol.setOnLongClickListener(v -> {
                    GetDialog.setPumpVolume(
                            activity,
                            this.ingredient.getPump());
                    return true;
                });
            }
        }

    }
}
