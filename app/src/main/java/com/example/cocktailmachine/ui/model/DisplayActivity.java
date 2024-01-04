package com.example.cocktailmachine.ui.model;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.ExtraHandlingDB;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.databinding.ActivityDisplayBinding;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetAdapter;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

public class DisplayActivity extends BasicActivity {
    private static final String TAG = "DisplayActivity";
    ActivityDisplayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//calls read Intent
        binding = ActivityDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    void preSetUp() {
        //TOPIC
        binding.textViewDisplayDescription.setVisibility(View.GONE);
        //PUMP
        binding.includeDisplayPump.getRoot().setVisibility(View.GONE);
        //Availabilities //Alcoholic
        binding.includeDisplayAvailable.getRoot().setVisibility(View.GONE);
        binding.includeDisplayNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeDisplayAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeDisplayNotAlcoholic.getRoot().setVisibility(View.GONE);
        //Ingredient
        binding.includeDisplayIngredientAdmin.getRoot().setVisibility(View.GONE);
        binding.imageButtonShowColor.setVisibility(View.GONE);
        //RECIPE
        binding.buttonSendRecipe.setVisibility(View.GONE);
        binding.includeRecipeIngredientsList.getRoot().setVisibility(View.GONE);
        binding.includeRecipeTopicsList.getRoot().setVisibility(View.GONE);

    }


    @Override
    void postSetUp() {

    }

    @Override
    public void reload() {

    }

    @Override
    void setUpRecipe(){
        Recipe recipe = Recipe.getRecipe(this, getID());
        if(recipe == null){
            binding.textViewDisplayTitle.setText("Fehler");
            binding.textViewDisplayDescription.setText("Das Rezept konnte nicht gefunden werden.");
            binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
            binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
            return;
        }
        recipe.loadAvailable(this);
        binding.textViewDisplayTitle.setText(recipe.getName());
        binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
        //TO DO: AlertDialog to change title if admin
        //setChangeTitleDialog();
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


        Log.v(TAG, "setIngredients");
        //HashMap<Ingredient, Integer> ingredientVolumeHashMap = recipe.getIngredientToVolume();
        binding.includeRecipeIngredientsList.recyclerViewList.setLayoutManager(GetAdapter.getNewLinearLayoutManager(this));
        binding.includeRecipeIngredientsList.recyclerViewList.setAdapter(
                new GetAdapter.IngredientVolAdapter(
                        this,
                        recipe,
                        false, true));


        Log.v(TAG, "setTopics");
        //List<Topic> topics = recipe.getTopics();
        binding.includeRecipeTopicsList.recyclerViewList.setLayoutManager(
                GetAdapter.getNewLinearLayoutManager(this));
        binding.includeRecipeTopicsList.recyclerViewList.setAdapter(
                new GetAdapter.TopicAdapter(
                        this,
                        recipe,
                        false,
                        true));
        binding.buttonSendRecipe.setVisibility(View.VISIBLE);
        binding.buttonSendRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetDialog.sendRecipe(DisplayActivity.this, recipe);
            }
        });
        /*
        if(topics.size()>0) {
            Log.v(TAG, "setTopics size> 0");
            Log.v(TAG, topics.toString());
            binding.includeRecipeTopicsList.recyclerViewList.setVisibility(View.VISIBLE);
        }else{
            Log.v(TAG, "setTopics size<= 0");
            binding.includeRecipeTopicsList.recyclerViewList.setVisibility(View.GONE);
        }

         */


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
        Ingredient ingredient = Ingredient.getIngredient(this, getID());
        if(ingredient == null){
            binding.textViewDisplayTitle.setText("Fehler");
            binding.textViewDisplayDescription.setText("Die Zutat konnte nicht gefunden werden.");
            binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
            binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
            return;
        }
        binding.textViewDisplayTitle.setText(ingredient.getName());
        binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
        //TO DO: AlertDialog to change title if admin


        binding.imageButtonShowColor.setVisibility(View.VISIBLE);
        binding.imageButtonShowColor.setColorFilter(ingredient.getColor());

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
        Topic topic = Topic.getTopic(this,getID());
        if(topic == null){
            binding.textViewDisplayTitle.setText("Fehler");
            binding.textViewDisplayDescription.setText("Der Serviervorschlag konnte nicht gefunden werden.");
            binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
            binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
            return;
        }
        binding.textViewDisplayTitle.setText(topic.getName());
        binding.textViewDisplayTitle.setVisibility(View.VISIBLE);

        //TO DO: AlertDialog to change title if admin
        //setChangeTitleDialog();
        binding.textViewDisplayDescription.setText(topic.getDescription());
        binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
        //TO DO: AlertDialog to change description if admin
        if(AdminRights.isAdmin()) {
            binding.textViewDisplayDescription.setOnLongClickListener(v -> {
                GetDialog.setDescribtion(DisplayActivity.this, topic);
                return true;
            });
        }
    }

    /**
     * adds change title Dialog to text view of title, if admin
     * @author Johanna Reidt
     */
    /*
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

     */


    @Override
    void setUpPump(){
        Pump pump = Pump.getPump(this,getID());
        if(pump == null){
            binding.textViewDisplayTitle.setText("Fehler");
            binding.textViewDisplayDescription.setText("Der Pumpe konnte nicht gefunden werden.");
            binding.textViewDisplayTitle.setVisibility(View.VISIBLE);
            binding.textViewDisplayDescription.setVisibility(View.VISIBLE);
            return;
        }
        Log.i(TAG, "Pump: "+pump.toString());
        binding.textViewDisplayTitle.setText("Slot: "+ pump.getID());
        //TO DO: AlertDialog to change title if admin ----NOT BECAUSE PUMP NO NAME
        binding.includeDisplayPump.getRoot().setVisibility(View.VISIBLE);
        binding.includeDisplayPump.textViewPumpIngredientName.setText(pump.getIngredientName(this));
        //TO DO: AlertDialog to change ingredient if admin
        //final Activity activity = this;
        binding.includeDisplayPump.textViewPumpIngredientName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GetDialog.chooseIngredient(DisplayActivity.this, pump);
                return true;
            }
        });
        String vol = pump.getVolume(this) +" ml";
        binding.includeDisplayPump.textViewPumpVolume.setText(vol);
        binding.includeDisplayPump.buttonRunPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "run pump clicked");
                GetDialog.runPump(DisplayActivity.this, pump);
            }
        });
        //final Activity activity = this;

        //TO DO: AlertDialog to change volume if admin
        //is always admin
        /*
        vol = pump.getMinimumPumpVolume() +" ml";
        binding.includeDisplayPump.textViewMinPumpVolume.setText(vol);

         */
        //TO DO: AlertDialog to change min pump vol if admin
        //is always admin

    }










    //recipe helper














    //simple buttons


    public void list(View view) {
        GetActivity.goToList(this, getModelType());
    }

    public void reload(View view) {
        ExtraHandlingDB.localRefresh(this);
        DisplayActivity.this.reload();
        setUp();
    }

    public void edit(View view) {
        GetActivity.goToEdit(this, getModelType(), this.getID());
    }

    public void home(View view) {
        GetActivity.goToMenu(this);
    }



    /*
    private void goToList(){
        GetActivity.goToList(this, getModelType());
    }

     */



}