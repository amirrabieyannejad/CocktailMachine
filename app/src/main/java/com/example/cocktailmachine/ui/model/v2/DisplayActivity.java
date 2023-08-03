package com.example.cocktailmachine.ui.model.v2;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.databinding.ActivityDisplayBinding;
import com.example.cocktailmachine.ui.model.ModelType;

public class DisplayActivity extends BasicActivity {
    ActivityDisplayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//calls read Intent
        binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseConnection.initializeSingleton(this);
    }

    @Override
    void preSetUp() {
        //TOPIC
        binding.textViewDisplayDescription.setVisibility(View.GONE);
        //PUMP
        binding.includeDisplayPump.getRoot().setVisibility(View.GONE);
        //Availabilities //Alcoholic
        binding.includeDisplayAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeDisplayNotAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeDisplayAvailable.getRoot().setVisibility(View.GONE);
        binding.includeDisplayNotAvailable.getRoot().setVisibility(View.GONE);
        //Ingredient
        binding.includeDisplayIngredientAdmin.getRoot().setVisibility(View.GONE);
        //RECIPE
        binding.includeRecipeIngredientsList.getRoot().setVisibility(View.GONE);
        binding.includeRecipeTopicsList.getRoot().setVisibility(View.GONE);

    }


    @Override
    void postSetUp() {
    }

    @Override
    void setUpRecipe(){
        Recipe recipe = Recipe.getRecipe(getID());
        assert recipe != null;
        recipe.loadAvailable();
        binding.textViewDisplayTitle.setText(recipe.getName());;
        //TO DO: AlertDialog to change title if admin
        setChangeTitleDialog();
        if(recipe.isAvailable()){
            binding.includeDisplayAvailable.getRoot().setVisibility(View.VISIBLE);
        }else{
            binding.includeDisplayNotAvailable.getRoot().setVisibility(View.VISIBLE);
        }
        if (recipe.isAlcoholic()) {
            binding.includeDisplayAlcoholic.getRoot().setVisibility(View.VISIBLE);
        }
        binding.includeRecipeIngredientsList.getRoot().setVisibility(View.VISIBLE);
        binding.includeRecipeTopicsList.getRoot().setVisibility(View.VISIBLE);
        binding.includeRecipeIngredientsList.textViewListTitle.setText("Zutaten");
        binding.includeRecipeTopicsList.textViewListTitle.setText("Serviervorschläge");

        TitleListAdapter titleadapter = new TitleListAdapter(
                this,
                recipe.getIngredientIds(),
                recipe.getIngredientNames(),
                ModelType.INGREDIENT);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeRecipeIngredientsList.recyclerViewList.setLayoutManager(llm);
        binding.includeRecipeIngredientsList.recyclerViewList.setAdapter(titleadapter);


        TitleVolumeListAdapter titlevoladapter = new TitleVolumeListAdapter(
                this,
                recipe);
        LinearLayoutManager llmvol = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeRecipeIngredientsList.recyclerViewList.setLayoutManager(llmvol);
        binding.includeRecipeIngredientsList.recyclerViewList.setAdapter(titlevoladapter);


/*
        TitleListAdapter adapter = new TitleListAdapter(
                this,
                recipe.getIngredientIds(),
                recipe.getIngredientNames(),
                ModelType.INGREDIENT);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeRecipeIngredientsList.recyclerViewList.setLayoutManager(llm);
        binding.includeRecipeIngredientsList.recyclerViewList.setAdapter(adapter);


 */

    }



    @Override
    void setUpIngredient(){
        Ingredient ingredient = Ingredient.getIngredient(getID());
        binding.textViewDisplayTitle.setText(ingredient.getName());;
        //TO DO: AlertDialog to change title if admin
        binding.textViewDisplayTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setChangeTitleDialog();
                return false;
            }
        });

        if(ingredient.isAvailable()){
            binding.includeDisplayAvailable.getRoot().setVisibility(View.VISIBLE);
        }else{
            binding.includeDisplayNotAvailable.getRoot().setVisibility(View.VISIBLE);
        }
        if (ingredient.isAlcoholic()) {
            binding.includeDisplayAlcoholic.getRoot().setVisibility(View.VISIBLE);

            final Activity activity = this;
            binding.includeDisplayAlcoholic.getRoot().setOnLongClickListener(v -> {
                GetDialog.setAlcoholic(activity, ingredient);
                return true;
            });
        }else{
            binding.includeDisplayNotAlcoholic.getRoot().setVisibility(View.VISIBLE);
            final Activity activity = this;
            binding.includeDisplayAlcoholic.getRoot().setOnLongClickListener(v -> {
                GetDialog.setAlcoholic(activity, ingredient);
                return true;
            });
        }
        if(AdminRights.isAdmin()){
            binding.includeDisplayIngredientAdmin.getRoot().setVisibility(View.VISIBLE);
            String vol = ingredient.getVolume()+" ml";
            binding.includeDisplayIngredientAdmin.textViewIngredientVolume.setText(vol);
            final Activity activity = this;
            binding.includeDisplayIngredientAdmin.textViewIngredientVolume.setOnLongClickListener(
                    //GetActivity.goTo(activity, FragmentType.Model, );
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    }
            );
        }

    }

    @Override
    void setUpTopic(){
        Topic topic = Topic.getTopic(getID());
        binding.textViewDisplayTitle.setText(topic.getName());;
        //TO DO: AlertDialog to change title if admin
        setChangeTitleDialog();
        binding.textViewDisplayDescription.setText(topic.getDescription());
        binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
        //TO DO: AlertDialog to change description if admin
        GetDialog.setDescribtion(this, topic);
    }

    /**
     * adds change title Dialog to text view of title, if admin
     * @author Johanna Reidt
     */
    private void setChangeTitleDialog(){
        DisplayActivity activity = this;
        if(AdminRights.isAdmin()) {
            binding.textViewDisplayTitle.setOnLongClickListener(
                    v -> {
                        GetDialog.setTitle(activity, activity.getModelType(), activity.getID());
                        return true;
                    }
            );
        }
    }


    @Override
    void setUpPump(){
        Pump pump = Pump.getPump(getID());
        binding.textViewDisplayTitle.setText(String.valueOf(pump.getID()));;
        //TO DO: AlertDialog to change title if admin ----NOT BECAUSE PUMP NO NAME
        binding.includeDisplayPump.getRoot().setVisibility(View.VISIBLE);
        binding.includeDisplayPump.textViewPumpIngredientName.setText(pump.getIngredientName());
        //TO DO: AlertDialog to change ingredient if admin
        final Activity activity = this;
        binding.includeDisplayPump.textViewPumpIngredientName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GetDialog.chooseIngredient(activity, pump);
                return true;
            }
        });
        String vol = pump.getVolume() +" ml";
        binding.includeDisplayPump.textViewPumpVolume.setText(vol);
        //final Activity activity = this;

        //TO DO: AlertDialog to change volume if admin
        //is always admin
        binding.includeDisplayPump.textViewPumpVolume.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GetDialog.setPumpVolume(activity, pump, false);
                return true;
            }
        });
        vol = pump.getMinimumPumpVolume() +" ml";
        binding.includeDisplayPump.textViewMinPumpVolume.setText(vol);
        //TO DO: AlertDialog to change min pump vol if admin
        //is always admin
        binding.includeDisplayPump.textViewPumpVolume.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GetDialog.setPumpMinVolume(activity, pump);
                return true;
            }
        });
    }



}