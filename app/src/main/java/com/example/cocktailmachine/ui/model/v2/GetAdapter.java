package com.example.cocktailmachine.ui.model.v2;

import android.annotation.SuppressLint;
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
import java.util.List;

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
        private final boolean withDelete;
        private final List<Topic> topics;
        public TopicAdapter(Activity activity, Recipe recipe, boolean withDelete) {
            this.activity = activity;
            this.recipe = recipe;
            this.withDelete = withDelete;
            this.topics = this.recipe.getTopics();
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
            Topic topic = this.topics.get(position);
            if(this.withDelete) {
                holder.setTxt(topic.getName(), v -> {
                    Log.i(TAG, "StringView: setTxt topic clicked");
                    GetDialog.deleteAddElement(this.activity, "den Serviervorschlag " + topic.getName(), new Postexecute() {
                        @Override
                        public void post() {
                            Log.i(TAG, "StringView: setTxt choose to delete");
                            TopicAdapter.this.remove(topic);
                            Log.i(TAG, "StringView: setTxt remove from list");
                            //this.activity.updateTopics();
                        }
                    });
                    return true;
                });
            }else{
                holder.setTxt(topic.getName());
            }
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "TopicAdapter: getItemCount");
            return this.recipe.getTopics().size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void add(Topic topic){
            this.topics.add(topic);
            this.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void remove(Topic topic){
            this.topics.remove(topic);
            this.notifyDataSetChanged();
        }

        public void remove(int index){
            this.topics.remove(index);
        }

        public void save(){
            this.recipe.replaceTopics(this.activity, this.topics);
        }


    }

    /**
     * ingredient row
     */
    static class IngredientVolAdapter extends RecyclerView.Adapter<GetAdapter.StringView> {
        private final static String TAG = "IngredientVolAdapter";
        private final Activity activity;
        private final Recipe recipe;
        private final boolean withDelete;


        private final HashMap<Ingredient, Integer> ingredientVol;
        public IngredientVolAdapter(Activity activity, Recipe recipe, boolean withDelete) {
            this.activity = activity;
            this.recipe = recipe;
            this.ingredientVol = recipe.getIngredientToVolume();
            this.withDelete = withDelete;
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
            Log.i(TAG, "onBindViewHolder");
            Ingredient i = this.ingredientVol.keySet().toArray(new Ingredient[]{})[position];
            if (i == null) {
                Log.e(TAG, "onBindViewHolder getting ingredient failed");
                return;
            }
            Integer vol = this.ingredientVol.get(i);
            if (vol == null) {
                Log.e(TAG, "onBindViewHolder getting vol failed");
                vol = -1;
            }
            if(withDelete) {
                holder.setTxt(String.format("%s: %s", i.getName(), vol)
                        , v -> {
                            Log.i(TAG, "setTxt ingredient clicked");
                            GetDialog.deleteAddElement(this.activity, "die Zutat " + i.getName(), new Postexecute() {
                                @Override
                                public void post() {
                                    Log.i(TAG, "setTxt choose to delete");
                                    IngredientVolAdapter.this.remove(i);

                                }
                            });
                            return true;
                        });
            }else{
                holder.setTxt(String.format("%s: %s", i.getName(), vol));
            }
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

        @SuppressLint("NotifyDataSetChanged")
        public void add(Ingredient ingredient, Integer volume) {
            Log.i(TAG, "add");
            this.ingredientVol.put(ingredient, volume);
            this.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void remove(Ingredient ingredient) {
            Log.i(TAG, "remove");
            this.ingredientVol.remove(ingredient);
            this.notifyDataSetChanged();
        }
    }
}