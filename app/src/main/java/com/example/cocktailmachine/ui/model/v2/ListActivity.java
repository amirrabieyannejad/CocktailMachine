package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.databinding.ActivityListBinding;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BasicActivity {
    private static final String TAG = "ListActivity" ;
    private ActivityListBinding binding;
    private GetAdapter.TitleListAdapter adapter;
    private final ArrayList<Long> IDs = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Buffer.load(this);
        //setContentView(R.layout.activity_list);
    }
    @Override
    void preSetUp() {
        this.IDs.clear();
        this.names.clear();
    }

    @Override
    void setUpPump() {
        binding.textViewListAcTitle.setText("Pumpen");
        List<Pump> pumps = Pump.getPumps(this);
        for(Pump pump: pumps){
            IDs.add(pump.getID());
            names.add("Pumpe: "+pump.getIngredientName(this));
        }
        /*
        adapter = new TitleListAdapter(
                this,
                IDs,
                names,
                getModelType()
        );

         */
    }

    @Override
    void setUpTopic() {
        binding.textViewListAcTitle.setText("Topics");
        List<Topic> elms = Topic.getTopics(this);
        for(Topic e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void setUpIngredient() {
        binding.textViewListAcTitle.setText("Zutaten");
        List<Ingredient> elms = Ingredient.getAllIngredients(this);
        for(Ingredient e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void setUpRecipe() {
        binding.textViewListAcTitle.setText("Rezepte");
        List<Recipe> elms = Recipe.getAllRecipes(this);
        for(Recipe e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void postSetUp() {
        adapter = new GetAdapter.TitleListAdapter(
            this,
            IDs,
            names,
            getModelType()
        );
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerViewListAc.setLayoutManager(llm);
        binding.recyclerViewListAc.setAdapter(adapter);


        Activity activity = this;
        binding.floatingActionButtonList.setOnClickListener(v -> GetActivity.goToAdd(activity, getModelType()));

        binding.textViewListAcTitle.setOnClickListener(v -> ListActivity.this.reload());
    }

    @Override
    public void reload() {
        setUp();
    }


}