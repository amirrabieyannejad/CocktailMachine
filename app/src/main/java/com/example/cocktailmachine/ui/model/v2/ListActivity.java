package com.example.cocktailmachine.ui.model.v2;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.databinding.ActivityListBinding;
import com.example.cocktailmachine.ui.model.FragmentType;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BasicActivity {
    private ActivityListBinding binding;
    private TitleListAdapter adapter;
    private final ArrayList<Long> IDs = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseConnection.initializeSingleton(this);
        //setContentView(R.layout.activity_list);
    }
    @Override
    void preSetUp() {
    }

    @Override
    void setUpPump() {
        binding.textViewListAcTitle.setText("Pumpen");
        List<Pump> pumps = Pump.getPumps();
        for(Pump pump: pumps){
            IDs.add(pump.getID());
            names.add("Pumpe: "+pump.getIngredientName());
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
        List<Topic> elms = Topic.getTopics();
        for(Topic e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void setUpIngredient() {
        binding.textViewListAcTitle.setText("Zutaten");
        List<Ingredient> elms = Ingredient.getAllIngredients();
        for(Ingredient e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void setUpRecipe() {
        binding.textViewListAcTitle.setText("Rezepte");
        List<Recipe> elms = Recipe.getRecipes();
        for(Recipe e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
    }

    @Override
    void postSetUp() {
        adapter = new TitleListAdapter(
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
        binding.floatingActionButtonList.setOnClickListener(v -> GetActivity.goToDisplay(activity, FragmentType.Edit, getModelType()));
    }


}