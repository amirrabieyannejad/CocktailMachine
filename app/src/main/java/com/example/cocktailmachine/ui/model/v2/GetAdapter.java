package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.enums.Postexecute;

import java.util.HashMap;

public class GetAdapter {
    private static final String TAG = "GetAdapter";

    /**
     * get a new vertical LinearLayoutManager
     * @return LinearLayoutManager
     */
    static LinearLayoutManager getNewLinearLayoutManager(Context context){
        Log.i(TAG, "getNewLinearLayoutManager");

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        return llm;
    }

    /**
     * basic string view, ergo row element of topic and ingredient diplay
     */
    private static class StringView extends RecyclerView.ViewHolder {
        //for layout item_little_title
        private final TextView txt;
        private final Activity activity;

        public StringView(@NonNull View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            Log.i(TAG, "StringView");
            txt = itemView.findViewById(R.id.textView_item_little_title);
        }

        private void setTxt(String txt){
            this.txt.setText(txt);

        }

        private void setTxt(String txt, View.OnLongClickListener longClickListener){
            this.txt.setText(txt);
            this.txt.setOnLongClickListener(longClickListener);
        }




    }

    /**
     * topic row
     */
    static class TopicAdapter extends RecyclerView.Adapter<GetAdapter.StringView> {
        private final Activity activity;
        private final Recipe recipe;
        private final Postexecute afterDeleteUpdate;
        public TopicAdapter(Activity activity, Recipe recipe, Postexecute afterDeleteUpdate) {
            this.activity = activity;
            this.recipe = recipe;
            this.afterDeleteUpdate = afterDeleteUpdate;
        }

        @NonNull
        @Override
        public GetAdapter.StringView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i(TAG, "TopicAdapter: onCreateViewHolder");
            return new GetAdapter.StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false),
                    this.activity);
        }

        @Override
        public void onBindViewHolder(@NonNull GetAdapter.StringView holder, int position) {
            Log.i(TAG, "TopicAdapter: onBindViewHolder");
            Topic topic = this.recipe.getTopics().get(position);
            holder.setTxt(topic.getName(), v -> {
                Log.i(TAG, "StringView: setTxt topic clicked");
                GetDialog.deleteAddElement(this.activity, "den Serviervorschlag " + topic.getName(), new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG, "StringView: setTxt choose to delete");
                        TopicAdapter.this.recipe.getTopics().remove(topic);
                        Log.i(TAG, "StringView: setTxt remove from list");
                        //this.activity.updateTopics();
                        TopicAdapter.this.afterDeleteUpdate.post();
                        Log.i(TAG, "StringView: setTxt updateTopics");
                    }
                });
                return true;
            });
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "TopicAdapter: getItemCount");
            return this.recipe.getTopics().size();
        }


    }

    /**
     * ingredient row
     */
    static class IngredientVolAdapter extends RecyclerView.Adapter<GetAdapter.StringView> {
        private final Activity activity;
        private final Recipe recipe;
        private final Postexecute afterDeleteUpdate;

        private final HashMap<Ingredient, Integer> ingredientVol;
        public IngredientVolAdapter(Activity activity, Recipe recipe, Postexecute afterDeleteUpdate) {
            this.activity = activity;
            this.recipe = recipe;
            this.afterDeleteUpdate = afterDeleteUpdate;
            this.ingredientVol = recipe.getIngredientToVolume();
        }

        @NonNull
        @Override
        public GetAdapter.StringView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i(TAG, "IngredientVolAdapter: onCreateViewHolder");
            return new GetAdapter.StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false),
                    this.activity);
        }

        @Override
        public void onBindViewHolder(@NonNull GetAdapter.StringView holder, int position) {
            Log.i(TAG, "IngredientVolAdapter: onBindViewHolder");
            Ingredient i = this.ingredientVol.keySet().toArray(new Ingredient[]{})[position];
            if (i == null) {
                Log.e(TAG, "IngredientVolAdapter:onBindViewHolder getting ingredient failed");
                return;
            }
            Integer vol = this.ingredientVol.get(i);
            if (vol == null) {
                Log.e(TAG, "IngredientVolAdapter:onBindViewHolder getting vol failed");
                vol = -1;
            }
            holder.setTxt(String.format("%s: %s", i.getName(), vol)
                    , v -> {
                        Log.i(TAG, "StringView: setTxt ingredient clicked");
                        GetDialog.deleteAddElement(this.activity, "die Zutat " + i.getName(), new Postexecute() {
                            @Override
                            public void post() {
                                Log.i(TAG, "StringView: setTxt choose to delete");
                                IngredientVolAdapter.this.ingredientVol.remove(i);
                                Log.i(TAG, "StringView: setTxt remove from hashmap");
                                IngredientVolAdapter.this.afterDeleteUpdate.post();
                                //IngredientVolAdapter.this.activity.updateIngredients();
                                Log.i(TAG, "StringView: setTxt afterDeleteUpdate updateIngredients");
                            }
                        });
                        return true;
            });
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "IngredientVolAdapter: getItemCount" +this.ingredientVol.size());
            return this.ingredientVol.size();
        }

        public void save(){
            this.recipe.replaceIngredients(this.activity, this.ingredientVol);
        }

        public boolean isEmpty(){
            return this.ingredientVol.isEmpty();
        }

        public boolean isAlcoholic(){
            boolean alcoholic = false;
            for(Ingredient i: this.ingredientVol.keySet()){
                if(i!= null){
                    alcoholic = alcoholic || i.isAlcoholic();
                }
            }
            return alcoholic;
        }

    }
}