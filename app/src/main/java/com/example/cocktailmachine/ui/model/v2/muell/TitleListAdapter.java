package com.example.cocktailmachine.ui.model.v2.muell;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johanna Reidt
 * @created Di. 27.Jun 2023 - 15:21
 * @project CocktailMachine
 */
/*
public class TitleListAdapter extends RecyclerView.Adapter<TitleListAdapter.TitleRow>{
    private static final String TAG = "TitleListAdapter";

    List<Long> IDs = new ArrayList<>();
    List<String> names = new ArrayList<>();
    ModelType modelType = ModelType.RECIPE;
    Activity activity;


    TitleListAdapter(Activity activity, List<Long> IDs, List<String> names, ModelType modelType){
        this.IDs = IDs;
        this.modelType = modelType;
        this.activity = activity;
        this.names = names;
        Log.i(TAG, "TitleListAdapter: ");
        Log.i(TAG, "IDs: "+IDs.toString());
        Log.i(TAG, "modelType: "+modelType.toString());
        Log.i(TAG, "activity: "+activity.toString());
        Log.i(TAG, "names: "+names.toString());
    }


    @NonNull
    @Override
    public TitleRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TitleRow(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_title,
                                parent,
                                false));
    }

    @Override
    public void onBindViewHolder(@NonNull TitleRow holder, int position) {
        holder.set(activity, this.names.get(position),this.IDs.get(position),this.modelType);
    }

    @Override
    public int getItemCount() {
         return IDs.size();
    }

    public static class TitleRow extends RecyclerView.ViewHolder{

        private static final String TAG = "TitleRow";
        private ModelType modelType;
        private long ID;
        private String text;

        private final TextView title;

        public TitleRow(@NonNull View itemView) {
            super(itemView);
            if(itemView == null){
                Log.w(TAG, "itemView is null");
            }if(itemView.getContext()==null){
                Log.w(TAG, "itemView is null");
            }
            title = itemView.findViewById(R.id.textView_item_title);
            if(title == null){
                Log.w(TAG, "TextView title is null");
            }
        }

        private void set(Activity activity, String text, long ID, ModelType modelType){
            this.text = text;
            this.ID = ID;
            this.modelType = modelType;
            this.title.setText(text);
            this.title.setOnClickListener(v ->
                    GetActivity.goToLook(
                            activity,
                            modelType,
                            ID));
            if(AdminRights.isAdmin()){
                //TO DO: open AlertDialog to delete
                this.title.setOnLongClickListener(v -> {
                    GetDialog.delete(
                            activity,
                            modelType,
                            text,
                            ID);
                    return GetDialog.checkDeleted(modelType, ID);
                });
            }
        }
    }
}

 */
