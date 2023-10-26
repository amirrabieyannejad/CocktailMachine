package com.example.cocktailmachine.ui.model;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.databinding.ActivityListBinding;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetAdapter;

import java.util.List;

public class ListActivity extends BasicActivity {
    private static final String TAG = "ListActivity" ;
    private ActivityListBinding binding;
    private GetAdapter.NameAdapter adapter;
    //private final ArrayList<Long> IDs = new ArrayList<>();
    //private final ArrayList<String> names = new ArrayList<>();


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
        //this.IDs.clear();
        //this.names.clear();
    }

    @Override
    void setUpPump() {
        binding.textViewListAcTitle.setText("Pumpen");
        /*
        List<Pump> pumps = Pump.getPumps(this);
        for(Pump pump: pumps){
            IDs.add(pump.getID());
            names.add("Slot "+pump.getSlot()+": "+pump.getIngredientName(this));
        }

         */
        binding.floatingActionButtonList.setVisibility(View.GONE);
        adapter = new GetAdapter.NameAdapter<Pump>(
                this,
                getModelType()
        ) {
            @Override
            public String getTitle(Pump i) {
                return "Slot "+i.getSlot()+": "+i.getIngredientName(ListActivity.this);
            }

            @Override
            public List<Pump> getList() {
                return  Pump.getPumps(ListActivity.this);
            }
        };
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
        /*
        List<Topic> elms = Topic.getTopics(this);
        for(Topic e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }

         */

        adapter = new GetAdapter.NameAdapter<Topic>(this, ModelType.TOPIC) {
            @Override
            public String getTitle(Topic i) {
                return i.getName();
            }

            @Override
            public List<Topic> getList() {
                return Topic.getTopics(ListActivity.this);
            }
        };
    }

    @Override
    void setUpIngredient() {
        binding.textViewListAcTitle.setText("Zutaten");
        //List<Ingredient> elms = Ingredient.getAllIngredients(this);
        /*
        for(Ingredient e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }
         */
        adapter = new GetAdapter.NameAdapter<Ingredient>(this, ModelType.INGREDIENT) {
            @Override
            public String getTitle(Ingredient i) {
                return i.getName();
            }

            @Override
            public List<Ingredient> getList() {
                return Ingredient.getAllIngredients(ListActivity.this);
            }
        };
    }

    @Override
    void setUpRecipe() {
        binding.textViewListAcTitle.setText("Rezepte");
        /*
        List<Recipe> elms = Recipe.getAllRecipes(this);
        for(Recipe e: elms){
            IDs.add(e.getID());
            names.add(e.getName());
        }

         */
        adapter = new GetAdapter.NameAdapter<Recipe>(this, ModelType.RECIPE){
            @Override
            public String getTitle(Recipe i) {
                return i.getName();
            }

            @Override
            public List<Recipe> getList() {
                return Recipe.getAllRecipes(ListActivity.this);
            }
        };
    }

    @Override
    void postSetUp() {

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerViewListAc.setLayoutManager(llm);
        binding.recyclerViewListAc.setAdapter(adapter);


        Activity activity = this;
        binding.floatingActionButtonList.setOnClickListener(v -> GetActivity.goToAdd(activity, getModelType()));
        binding.imageButtonListReload.setOnClickListener(v -> ListActivity.this.reload());
        binding.imageButtonListToHome.setOnClickListener(v -> GetActivity.goToMenu(activity));
    }

    @Override
    public void reload() {
        //setUp();
    }


}