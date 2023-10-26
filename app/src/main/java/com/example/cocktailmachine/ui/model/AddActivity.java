package com.example.cocktailmachine.ui.model;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetAdapter;
import com.example.cocktailmachine.ui.model.helper.GetDialog;
import com.mrudultora.colorpicker.ColorPickerPopUp;

import java.util.Random;

public class AddActivity extends BasicActivity {
    //TODO: ConfigurePumps für add ingredients
    private static final String TAG = "AddActivity";

    private ActivityAddBinding binding;
    private Pump pump;
    private Topic topic;
    private Recipe recipe;
    private Ingredient ingredient;

    //private HashMap<Ingredient, Integer> ingredientVolumeHashMap;
    //private List<Topic> topics;

    private GetAdapter.IngredientVolAdapter ingVolAdapter;
    private GetAdapter.TopicAdapter topicAdapter;

    final private Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    void preSetUp() {
        Log.v(TAG, "preSetUp");
        binding.textViewAddTitle.setVisibility(View.GONE);
        binding.editTextAddTitle.setVisibility(View.GONE);
        binding.textViewError.setVisibility(View.GONE);

        binding.editTextDescription.setVisibility(View.GONE);


        binding.subLayoutAlcohol.setVisibility(View.GONE);
        binding.switchAlcohol.setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
        //binding.includeNotAlcoholic


        binding.buttonChangeColor.setVisibility(View.GONE);


        binding.subLayoutAddIngredient.setVisibility(View.GONE);
        binding.subLayoutAddTopic.setVisibility(View.GONE);

        //binding.includePump;//.getRoot().setVisibility(View.GONE);

        binding.includePump.getRoot().setVisibility(View.GONE);



    }

    @Override
    void setUpPump() {
        Log.v(TAG, "setUpPump");

        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Pumpe");

        //maybe needed
        pump = Pump.getPump(this, this.getID());
        if(pump == null){
            error("???");
            return;
        }

        binding.includePump.textViewSearchIngredientIng.setText(pump.getIngredientName(this));
        binding.includePump.editTextSearchIngredientIng.setText(pump.getIngredientName(this));
        binding.includePump.editTextNumberSearchIngredientVol.setText(String.valueOf(pump.getVolume(this)));
        binding.includePump.getRoot().setVisibility(View.VISIBLE);
        //binding.includePump.editTextNumberSearchIngredientVol.setVisibility(View.VISIBLE);
        search();

        binding.buttonSave.setOnClickListener(v -> {
            Log.v(TAG, "setUpPump: buttonSave clicked");
            if(binding.includePump.editTextSearchIngredientIng.getText().toString().length()>0) {
                Log.v(TAG, "setUpPump: buttonSave has title");
                getIngredient();
            }else{
                Log.v(TAG, "setUpPump: buttonSave has no title -> toast & stop");
                Toast.makeText(AddActivity.this, "Nenne die Zutat!", Toast.LENGTH_SHORT).show();
                return;
            }
            AddActivity.this.pump.setCurrentIngredient(this.activity, AddActivity.this.ingredient);
            //set volume
            String vol = binding.includePump.editTextNumberSearchIngredientVol.getText().toString();
            if(vol.length()==0){
                Log.v(TAG, "setUpPump: buttonSave has no vol -> toast & stop");
                Toast.makeText(AddActivity.this, "Gib das Volumen an!", Toast.LENGTH_SHORT).show();
                return;
            }
            int level = -1;
            try {
                level = Integer.parseInt(vol);
                Log.v(TAG, "setUpPump: buttonSave parsed vol");
            }catch (NumberFormatException e){
                Log.v(TAG, "setUpPump: buttonSave error parsing vol -> toast & stop");
                Toast.makeText(AddActivity.this, "Gib eine ganze Zahl für das Volumen an!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                AddActivity.this.pump.fill(this,level);
            } catch (MissingIngredientPumpException e) {
                Log.e(TAG, "setUpPump: buttonSave error missing ingredient -> toast & stop");
                Log.e(TAG, "setUpPump: buttonSave error missing ingredient: "+e.getMessage());
                e.printStackTrace();
                Toast.makeText(AddActivity.this, "Nochmal!", Toast.LENGTH_SHORT).show();
                return;
            }

            /*
            //set min volume
            vol = binding.includePump.editTextNumberSearchIngredientMinVol.getText().toString();
            if(vol.length()==0){
                Log.v(TAG, "setUpPump: buttonSave has no min vol -> toast & stop");
                //Toast.makeText(AddActivity.this, "Gib das minimal Volumen an!", Toast.LENGTH_SHORT).show();
                vol = "1";
                return;
            }
            level = -1;
            try {
                level = Integer.parseInt(vol);
                Log.v(TAG, "setUpPump: buttonSave parsed vol");
            }catch (NumberFormatException e){
                Log.v(TAG, "setUpPump: buttonSave error parsing vol -> toast & stop");
                Toast.makeText(AddActivity.this, "Gib eine ganze Zahl für das minimale Volumen an!", Toast.LENGTH_SHORT).show();
                return;
            }
            AddActivity.this.pump.setMinimumPumpVolume(level);
            */


            AddActivity.this.pump.sendSave(AddActivity.this);
            Log.v(TAG, "Pump sendsave: "+AddActivity.this.pump.toString());
            GetActivity.goToLook(AddActivity.this, ModelType.PUMP, AddActivity.this.pump.getID());
            Log.v(TAG, "setUpPump:done");
        });
    }

    /**
     * set up topic
     */
    @Override
    void setUpTopic() {
        Log.v(TAG, "setUpTopic");
        String title_tag = "Serviervorschlag";

        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.editTextDescription.setVisibility(View.VISIBLE);

        binding.textViewAddTitle.setText(title_tag);

        topic = Topic.getTopic(this, this.getID());
        if(topic == null){
            Log.v(TAG, "setUpPump: new topic");
            binding.buttonSave.setOnClickListener(v -> {
                Log.v(TAG, "setUpPump: buttonSave: clicked");
                topic = Topic.makeNew(binding.editTextAddTitle.getText().toString(), binding.editTextDescription.getText().toString());
                topic.save(activity);
                GetActivity.goToLook(activity, ModelType.TOPIC, topic.getID());
                Log.v(TAG, "setUpPump: done");
            });

        }else{
            Log.v(TAG, "setUpPump: buttonSave: old topic");
            binding.editTextAddTitle.setText(topic.getName());
            binding.editTextDescription.setText(topic.getDescription());
            binding.buttonSave.setOnClickListener(v -> {
                Log.v(TAG, "setUpPump: buttonSave: clicked");
                topic.setName(binding.editTextAddTitle.getText().toString());
                topic.setDescription(binding.editTextDescription.getText().toString());
                topic.save(activity);
                GetActivity.goToLook(activity, ModelType.TOPIC, topic.getID());
                Log.v(TAG, "setUpPump: done");
            });
        }



    }

    /**
     * setup ingredient
     */
    @Override
    void setUpIngredient() {
        Log.v(TAG, "setUpIngredient");
        ingredient = Ingredient.getIngredient(this, this.getID());
        //nedded
        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.subLayoutAlcohol.setVisibility(View.VISIBLE);
        binding.switchAlcohol.setVisibility(View.VISIBLE);
        binding.buttonChangeColor.setVisibility(View.VISIBLE);

        //maybe needed
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);


        binding.textViewAddTitle.setText("Zutat");
        binding.switchAlcohol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
                binding.includeNotAlcoholic.getRoot().setVisibility(View.GONE);
            }else{
                binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
                binding.includeNotAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }
        });

        //saving instances
        final int[] set_color = {new Random().nextInt()};
        if(ingredient != null){
            set_color[0] = ingredient.getColor();
            binding.buttonChangeColor.setBackgroundColor(set_color[0]);
        }

        binding.buttonChangeColor.setClickable(true);
        binding.buttonChangeColor.setVisibility(View.VISIBLE);
        //binding.buttonChangeColor.setBackgroundColor(set_color[0]);

        binding.buttonChangeColor.setOnClickListener(v -> {
            Log.v(TAG, "setUpIngredient: color picker clicked");
            ColorPickerPopUp colorPickerPopUp = new ColorPickerPopUp(activity);	// Pass the context.
            colorPickerPopUp.setShowAlpha(true)			// By default show alpha is true.
                    //.setDefaultColor(ingredient.getColor())
                    .setDialogTitle("Wähle eine Farbe!")
                    .setDefaultColor(set_color[0])
                    .setNegativeButtonText("Abbruch")
                    .setPositiveButtonText("Speichern")
                    .setOnPickColorListener(new ColorPickerPopUp.OnPickColorListener() {
                        @Override
                        public void onColorPicked(int color) {
                            // handle the use of color
                            set_color[0] = color;
                            binding.buttonChangeColor.setBackgroundColor(color);
                            Log.v(TAG, "setUpIngredient: color picked "+ set_color[0]);
                        }

                        @Override
                        public void onCancel() {
                            Log.v(TAG, "setUpIngredient: color picker canceled");
                            colorPickerPopUp.dismissDialog();	// Dismiss the dialog.
                        }
                    })
                    .show();
        });


        if(ingredient == null){
            Log.v(TAG, "setUpIngredient: new ingredient");
            //binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.buttonSave.setOnClickListener(v -> {
                Log.v(TAG, "setUpIngredient:buttonSave: clicked");
                ingredient = Ingredient.makeNew(
                        binding.editTextAddTitle.getText().toString(),
                        binding.switchAlcohol.isChecked(),
                        set_color[0]
                );
                ingredient.save(activity);
                GetActivity.goToLook(activity,  ModelType.INGREDIENT, ingredient.getID());
            });

        }else{
            Log.v(TAG, "setUpIngredient: old ingredient");
            set_color[0] = ingredient.getColor();
            //binding.imageViewColorShow.setColorFilter(set_color[0]);
            binding.editTextAddTitle.setText(ingredient.getName());
            binding.switchAlcohol.setChecked(ingredient.isAlcoholic());

            binding.buttonSave.setOnClickListener(v -> {
                Log.v(TAG, "setUpIngredient:buttonSave: clicked");
                ingredient.setName(binding.editTextAddTitle.getText().toString());
                ingredient.setAlcoholic(binding.switchAlcohol.isChecked());
                ingredient.setColor(set_color[0]);
                ingredient.save(activity);
                GetActivity.goToLook(activity,  ModelType.INGREDIENT, ingredient.getID());
            });
        }
    }

    /**
     * set up recipe
     */
    @Override
    void setUpRecipe() {
        Log.v(TAG, "setUpRecipe");
        this.recipe = Recipe.getRecipe(this, this.getID());
        if(this.getID()==-1 || this.recipe == null){
            this.recipe = Recipe.makeNew();
            this.recipe.clean(this);
            //this.ingredientVolumeHashMap = new HashMap<>();
            //this.topics = new ArrayList<>();
        } else {
            binding.editTextAddTitle.setText(this.recipe.getName());
            //this.ingredientVolumeHashMap = this.recipe.getIngredientToVolume();
            //this.topics = this.recipe.getTopics();
        }


        //nedded
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Rezept");

        binding.editTextAddTitle.setVisibility(View.VISIBLE);
        binding.editTextAddTitle.setHint("Name des Cocktails");



        binding.recyclerViewIngredients.setVisibility(View.VISIBLE);
        binding.recyclerViewIngredients.setLayoutManager(
                GetAdapter.getNewLinearLayoutManager(this));
        this.ingVolAdapter = new GetAdapter.IngredientVolAdapter(
                AddActivity.this,
                this.recipe,
                true,
                false);

        binding.recyclerViewIngredients.setAdapter(this.ingVolAdapter);

        View.OnClickListener ingAdd = v ->{
            Log.v(TAG, "subLayoutAddIngredient: clicked");
            GetDialog.getIngVol(activity,
                    !AdminRights.isAdmin(),
                    new GetDialog.IngredientVolumeSaver() {
                        private Ingredient ing;
                        private int vol;

                        @Override
                        public void save(Ingredient ingredient, String tippedName) {
                            this.ing = ingredient;
                            Log.v(TAG,"IngredientVolumeSaver: save: "+this.ing.toString() + tippedName);
                        }

                        @Override
                        public void save(Integer volume) {
                            this.vol = volume;
                            Log.v(TAG,"IngredientVolumeSaver: save: "+this.vol);
                        }

                        @Override
                        public void post() {
                            Log.v(TAG,"IngredientVolumeSaver: post: ");
                            AddActivity.this.ingVolAdapter.add(ing, vol);
                            Log.v(TAG, "subLayoutAddIngredient: ing vol added");
                            Log.v(TAG, AddActivity.this.ingVolAdapter.toString());
                            //AddActivity.this.updateIngredients();
                            //Log.i(TAG, "subLayoutAddIngredient: updateIngredients");
                        }
                    });

        };
        binding.subLayoutAddIngredient.setVisibility(View.VISIBLE);
        binding.subLayoutAddIngredientAdd.setOnClickListener(ingAdd);
        binding.ButtonAddIngredient.setOnClickListener(ingAdd);
        Log.v(TAG, "setIngredients");
        /*
        if((this.ingVolAdapter== null
                && this.recipe.getIngredientToVolume().size()>0)
                ||(this.ingVolAdapter!= null
                && this.ingVolAdapter.getItemCount()>0)) {
            Log.v(TAG, "setIngredients size> 0");
            //Log.i(TAG, AddActivity.this.ingredientVolumeHashMap.toString());

            binding.recyclerViewIngredients.setVisibility(View.VISIBLE);
            if(binding.recyclerViewIngredients.getAdapter() == null) {
                binding.recyclerViewIngredients.setLayoutManager(GetAdapter.getNewLinearLayoutManager(this));
                //this.ingVolAdapter = new GetAdapter.IngredientVolAdapter(AddActivity.this, this.recipe);
                binding.recyclerViewIngredients.setAdapter(this.getIngVolAdapter());
            }
        }else{
            Log.v(TAG, "setIngredients size<= 0");
            binding.recyclerViewIngredients.setVisibility(View.GONE);
        }

         */
        //setAlcoholic();



        Log.v(TAG, "setTopics");
        binding.recyclerViewTopics.setVisibility(View.VISIBLE);
        binding.recyclerViewTopics.setLayoutManager(
                GetAdapter.getNewLinearLayoutManager(this));
        this.topicAdapter = new GetAdapter.TopicAdapter(
                AddActivity.this,
                this.recipe,
                true,
                false);
        View.OnClickListener topAdd =v ->{
            Log.v(TAG, "subLayoutAddTopic: clicked");
            GetDialog.addTopic(activity,
                    (t, d) -> {
                        Log.v(TAG, "subLayoutAddTopic: topic added");
                        AddActivity.this.topicAdapter.add(t);
                        Log.v(TAG, "subLayoutAddTopic: topic added to adapter");
                        d.dismiss();
                        //Log.i(TAG, "subLayoutAddTopic: dialog dismiss");
                        //AddActivity.this.setTopics();
                        //Log.i(TAG, "subLayoutAddTopic: updateTopics");
                    });
        };
        binding.recyclerViewTopics.setAdapter(this.topicAdapter);

        binding.subLayoutAddTopic.setVisibility(View.VISIBLE);
        binding.subLayoutAddTopicAdd.setOnClickListener(topAdd);
        binding.ButtonAddTopic.setOnClickListener(topAdd);
        //setTopics();


        //save
        binding.buttonSave.setOnClickListener(v -> {
            Log.v(TAG, "buttonSave: clicked");
            //Log.i(TAG, "ingvol "+AddActivity.this.ingredientVolumeHashMap.toString());
            Log.v(TAG, "topics "+AddActivity.this.topicAdapter.toString());
            AddActivity.this.recipe.setName(activity, binding.editTextAddTitle.getText().toString());
            AddActivity.this.recipe.save(activity);
            //AddActivity.this.recipe.replaceIngredients(activity,
             //       AddActivity.this.ingredientVolumeHashMap);
            AddActivity.this.ingVolAdapter.save();
            AddActivity.this.topicAdapter.save();
            if(AddActivity.this.recipe.sendSave(
                    activity)){
                Log.v(TAG, "buttonSave: show recipe");
                GetActivity.goToLook(
                        activity,
                        ModelType.RECIPE,
                        AddActivity.this.recipe.getID());
            }else {
                Log.v(TAG, "buttonSave: saving or sending failed -> toast \"nochmal\"");
                Toast.makeText(activity, "Speicher nochmal!", Toast.LENGTH_SHORT).show();
            }
            Log.v(TAG, "buttonSave: done");
        });
    }

    @Override
    void postSetUp() {
        Log.v(TAG, "postSetUp");


        //for all
        binding.buttonStop.setOnClickListener(v -> GetActivity.goToList(this.activity, this.getModelType()));
        //binding.textViewAddTitle.setOnClickListener(v -> AddActivity.this.reload());

    }

    @Override
    public void reload() {
    }

    /**
     * Fehler
     * @param msg Error Message
     */
    void error(String msg){
        Log.v(TAG, "error");
        preSetUp();
        binding.textViewAddTitle.setVisibility(View.VISIBLE);
        binding.textViewAddTitle.setText("Fehler!");
        if(msg != null) {
            binding.textViewError.setVisibility(View.VISIBLE);
            binding.textViewError.setText(msg);
        }
        binding.buttonSave.setVisibility(View.GONE);
    }






    //no Name Twice Policie

    /*
     *
     * @author Johanna Reidt
     * @return
     */
    private boolean checkName(){
        //TODO: ????

        return true;
    }














    //pump helper

    private void search(){
        Log.v(TAG, "search");
        binding.includePump.editTextSearchIngredientIng.setVisibility(View.VISIBLE);
        binding.includePump.imageButtonSearchIngredientDone.setVisibility(View.VISIBLE);
        binding.includePump.textViewSearchIngredientIng.setVisibility(View.GONE);

        binding.includePump.imageButtonSearchIngredientDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.includePump.editTextSearchIngredientIng.getText().toString().length()>0) {
                    searchDone();
                }else{
                    Toast.makeText(AddActivity.this, "Nenne die Zutat!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    private void getIngredient(){
        Log.v(TAG, "getIngredient");
        this.ingredient = Ingredient.searchOrNew(activity,
                binding.includePump.editTextSearchIngredientIng.getText().toString());
    }
    private void searchDone(){
        Log.v(TAG, "searchDone");
        getIngredient();
        binding.includePump.editTextSearchIngredientIng.setVisibility(View.GONE);
        binding.includePump.imageButtonSearchIngredientDone.setVisibility(View.GONE);
        binding.includePump.textViewSearchIngredientIng.setVisibility(View.VISIBLE);
        binding.includePump.textViewSearchIngredientIng.setText(this.ingredient.getName());
        binding.includePump.textViewSearchIngredientIng.setOnClickListener(v -> search());


    }



















}