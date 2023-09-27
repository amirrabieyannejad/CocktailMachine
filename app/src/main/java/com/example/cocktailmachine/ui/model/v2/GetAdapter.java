package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.elements.DataBaseElement;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

        private void setTxt(String txt, View.OnClickListener clickListener){
            this.txt.setText(txt);
            this.txt.setOnClickListener(clickListener);
        }

        private void setTxt(String txt, View.OnClickListener clickListener,  View.OnLongClickListener longClickListener){
            this.txt.setText(txt);
            this.txt.setOnClickListener(clickListener);
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
        private final boolean withDisplay;
        private final List<Topic> topics;
        public TopicAdapter(Activity activity,Recipe recipe, boolean withDelete, boolean withDisplay) {
            this.activity = activity;
            this.recipe = recipe;
            this.withDelete = withDelete;
            this.withDisplay = withDisplay;
            this.topics = new ArrayList<>(this.recipe.getTopics());
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
            Log.i(TAG, "TopicAdapter: onBindViewHolder position: "+position);
            Topic topic = this.topics.get(position);
            Log.i(TAG, "TopicAdapter: onBindViewHolder topic: "+topic.toString());
            View.OnLongClickListener delete =  v -> {
                Log.i(TAG, "TopicAdapter: setTxt topic clicked");
                GetDialog.deleteAddElement(this.activity, "den Serviervorschlag " + topic.getName(), new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG, "TopicAdapter: setTxt choose to delete");
                        TopicAdapter.this.remove(topic);
                        Log.i(TAG, "TopicAdapter: setTxt remove from list");
                        //this.activity.updateTopics();
                    }
                });
                return true;
            };
            View.OnClickListener goTo = v->{
                GetActivity.goToLook(this.activity, ModelType.TOPIC, topic.getID());
            };
            if(this.withDelete && !this.withDisplay) {
                holder.setTxt(topic.getName(),delete);
            }if(!this.withDelete && this.withDisplay) {
                holder.setTxt(topic.getName(),goTo);
            }if(this.withDelete && this.withDisplay) {
                holder.setTxt(topic.getName(),goTo, delete);
            }if(!this.withDelete && !this.withDisplay) {
                holder.setTxt(topic.getName());
            }else{
                holder.setTxt(topic.getName());
            }
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "TopicAdapter: getItemCount");
            return this.topics.size();
        }

        public void add(Topic topic){
            Log.i(TAG, "add");
            if(topic != null) {
                if(!this.topics.contains(topic)) {
                    this.topics.add(topic);
                    this.notifyItemInserted(this.topics.indexOf(topic));
                }
            }
            Log.i(TAG, "add: "+this.topics);
        }


        public void remove(Topic topic){
            Log.i(TAG, "remove t");
            this.remove(this.topics.indexOf(topic));
            Log.i(TAG, "remove t: "+this.topics);
        }

        public void remove(int position){
            Log.i(TAG, "remove position");
            if(position != -1) {
                this.topics.remove(position);
                this.notifyItemRemoved(position);
            }
            Log.i(TAG, "remove position: "+this.topics);
        }

        public void save(){
            Iterator<Topic> iterator = this.topics.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    iterator.remove();
                }
            }
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
        private final boolean withDisplay;


        private final HashMap<Ingredient, Integer> ingredientVol;
        private final List<Ingredient> ingredients;
        public IngredientVolAdapter(Activity activity,  Recipe recipe, boolean withDelete, boolean withDisplay) {
            this.activity = activity;
            this.recipe = recipe;
            this.ingredientVol = recipe.getIngredientToVolume();
            this.ingredients = new ArrayList<>(this.ingredientVol.keySet());
            this.withDelete = withDelete;
            this.withDisplay = withDisplay;
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
            Ingredient i = this.ingredients.get(position);
            if (i == null) {
                Log.e(TAG, "onBindViewHolder getting ingredient failed");
                return;
            }
            Integer vol = this.ingredientVol.get(i);
            if (vol == null) {
                Log.e(TAG, "onBindViewHolder getting vol failed");
                vol = -1;
            }
            View.OnLongClickListener delete =  v -> {
                            Log.i(TAG, "setTxt ingredient clicked");
                            GetDialog.deleteAddElement(this.activity, "die Zutat " + i.getName(), new Postexecute() {
                                @Override
                                public void post() {
                                    Log.i(TAG, "setTxt choose to delete");
                                    IngredientVolAdapter.this.remove(i);

                                }
                            });
                            return true;
                        };
            View.OnClickListener goTo =  v -> {
                Log.i(TAG, "setTxt ingredient clicked");
                GetActivity.goToLook(this.activity, ModelType.INGREDIENT, i.getID());
            };

            String txt = String.format("%s: %s", i.getName(), vol);


            if(this.withDelete && !this.withDisplay) {
                holder.setTxt(txt,delete);
            }if(!this.withDelete && this.withDisplay) {
                holder.setTxt(txt,goTo);
            }if(this.withDelete && this.withDisplay) {
                holder.setTxt(txt,goTo, delete);
            }if(!this.withDelete && !this.withDisplay) {
                holder.setTxt(txt);
            }else{
                holder.setTxt(txt);
            }
        }


        @Override
        public int getItemCount() {
            Log.i(TAG, "IngredientVolAdapter: getItemCount" +this.ingredientVol.size());
            return this.ingredientVol.size();
        }

        public void save(){
            Iterator<Ingredient> iterator = this.ingredientVol.keySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    this.ingredientVol.remove(null);
                    iterator.remove();
                }
            }
            this.recipe.replaceIngredients(this.activity, this.ingredientVol);
        }
        public void add(Ingredient ingredient, Integer volume) {
            Log.i(TAG, "add");
            if(ingredient != null) {
                this.ingredients.add(ingredient);
                this.ingredientVol.put(ingredient, volume);
                this.notifyItemInserted(this.getItemCount());
            }
        }

        public void remove(Ingredient ingredient) {
            Log.i(TAG, "remove");
            int position = this.ingredients.indexOf(ingredient);
            this.ingredients.remove(ingredient);
            this.ingredientVol.remove(ingredient);
            this.notifyItemRemoved(position);
        }
    }

    /*
    public static class IngredientVolumeAdapter extends  ElementListAdapter<Ingredient>{
        private final static String TAG = "IngredientVolumeAdapter";
        HashMap<Ingredient,Integer> ingVols;
        Recipe recipe;
        protected IngredientVolumeAdapter(@NonNull Activity activity, @NonNull Recipe recipe, boolean withDelete, boolean withDisplay) {
            super(activity, recipe.getIngredients(), withDelete, withDisplay);
            this.recipe = recipe;
            this.ingVols = this.recipe.getIngredientToVolume();
        }

        @Override
        public String toString(int position) {
            Log.i(TAG,"toString");
            Ingredient i = this.get(position);
            if (i == null) {
                Log.e(TAG, "toString getting ingredient failed");
                return "Fehler: Zutat nicht gefunden!";
            }
            Integer vol = this.ingVols.get(i);
            if (vol == null) {
                Log.e(TAG, "toString getting vol failed");
                vol = -1;
            }
            return String.format("%s: %s", i.getName(), vol);
        }

        @Override
        public void save() {
            Log.i(TAG,"toString");
            this.recipe.replaceIngredients(
                    this.getActivity(),
                    this.ingVols);
        }

        public void add(Ingredient element, Integer vol){
            if(element==null){
                return;
            }
            if(vol == null){
                vol = -1;
            }
            this.ingVols.remove(element);//if existing vol remove it
            this.add(element);
            this.ingVols.put(element, vol);
        }

        @Override
        public void remove(int position) {
            this.ingVols.remove(this.get(position));
            super.remove(position);
        }

        @Override
        public View.OnLongClickListener delete(int position) {
            return v -> {
                Log.i(TAG, "setTxt ingredient clicked");
                GetDialog.deleteAddElement(this.getActivity(), "die Zutat " + this.get(position).getName(), new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG, "setTxt choose to delete");
                        IngredientVolumeAdapter.this.remove(position);
                    }
                });
                return true;
            };
        }

        @Override
        public View.OnClickListener goTo(int position) {
            return  v -> {
                Log.i(TAG, "setTxt ingredient clicked");
                GetActivity.goToLook(this.getActivity(), ModelType.INGREDIENT, this.get(position).getID());
            };
        }
    }



    */
    /*
    private static abstract class ElementListAdapter<T extends DataBaseElement> extends ListAdapter<T, GetAdapter.StringView> {

     */

        /*
        {
        private final static String TAG = "IngredientVolAdapter";
        private final Activity activity;
        private final Recipe recipe;
        private final boolean withDelete;
        private final boolean withDisplay;


        private final HashMap<Ingredient, Integer> ingredientVol;
        public IngredientVolAdapter(Activity activity, Recipe recipe, boolean withDelete, boolean withDisplay)
         */
    /*
        private final static String TAG = "ElementListAdapter";
        private final Activity activity;
        //private final Recipe recipe;
        private final boolean withDelete;
        private final boolean withDisplay;
        private final List<T> elements;// = new ArrayList<>();
        protected ElementListAdapter(@NonNull Activity activity,
                                     //@NonNull Recipe recipe,
                                     @NonNull List<T> elements,
                                     boolean withDelete,
                                     boolean withDisplay) {
            super(new ElementItemCallBack<T>());
            this.activity = activity;
            //this.recipe = recipe;
            this.elements = elements;
            this.withDelete = withDelete;
            this.withDisplay = withDisplay;
        }

        @NonNull
        @Override
        public StringView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new GetAdapter.StringView(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_little_title, parent, false),
                    this.activity);
        }

        @Override
        public void onBindViewHolder(@NonNull StringView holder, int position) {
            String txt = this.toString(position);
            View.OnLongClickListener delete = this.delete(position);
            View.OnClickListener goTo = this.goTo(position);
            if(this.withDelete && !this.withDisplay) {
                holder.setTxt(txt,delete);
            }if(!this.withDelete && this.withDisplay) {
                holder.setTxt(txt,goTo);
            }if(this.withDelete && this.withDisplay) {
                holder.setTxt(txt,goTo, delete);
            }if(!this.withDelete && !this.withDisplay) {
                holder.setTxt(txt);
            }else{
                holder.setTxt(txt);
            }
        }

        Activity getActivity(){
            return this.activity;
        }

        public List<T> getElements(){
            return this.elements;
        }
        public boolean isEmpty(){
            return this.elements.isEmpty();
        }
        public void add(@NonNull T element){
            if(!has(element)){
                this.elements.add(element);
                this.notifyItemInserted(this.elements.size()-1);
            }
        }

        public boolean has(@NonNull T element){
            for(T e: this.elements){
                if(element.getID() == e.getID()){
                    return true;
                }
            }
            return false;
        }

        public T get(int position){
            return this.elements.get(position);
        }

        public abstract String toString(int position);

        public void remove(@NonNull T element){
            this.remove(this.elements.indexOf(element));
        }

        public void remove(int position){
            this.elements.remove(position);
            this.notifyItemRemoved(position);
        }

        public abstract void save();

        public abstract View.OnLongClickListener delete(int position);
        public abstract View.OnClickListener goTo(int position);

        static class ElementItemCallBack<T extends DataBaseElement> extends DiffUtil.ItemCallback<T> {

            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return (Objects.equals(oldItem.getClassName(), newItem.getClassName()))
                        && oldItem.getID() == newItem.getID();
            }

            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return (Objects.equals(oldItem.getClassName(), newItem.getClassName()))
                        && oldItem.areContentsTheSame(newItem);
            }
        }
    }

 */
}