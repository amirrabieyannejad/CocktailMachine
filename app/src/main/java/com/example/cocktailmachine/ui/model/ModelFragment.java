package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.AdminRights;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.databinding.FragmentModelBinding;
import com.example.cocktailmachine.ui.ModelActivity;

import java.util.Objects;

public class ModelFragment extends Fragment {
    private FragmentModelBinding binding;
    private ModelActivity activity;
    private static final String TAG = "ModelFragment";
    private ModelType type;
    private Long id;

    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;
    private Recipe recipe;

    public ModelFragment(){
    }

    protected static enum ModelType{
        TOPIC, INGREDIENT, PUMP,RECIPE
    }

    private void setUP(){
        Log.i(TAG, "setUP: "+type+" "+id.toString());
        binding.textViewDescription.setVisibility(View.GONE);
        binding.imageViewEdit.setVisibility(View.GONE);
        //binding.includePump

        binding.includeAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeNotAvailable.getRoot().setVisibility(View.GONE);
        binding.includeAlcoholic.getRoot().setVisibility(View.GONE);
        binding.includePump.getRoot().setVisibility(View.GONE);
        binding.includeIngredientAdmin.getRoot().setVisibility(View.GONE);
        binding.includeIngredients.getRoot().setVisibility(View.GONE);
        binding.includeTopics.getRoot().setVisibility(View.GONE);




        switch (type){
            case TOPIC:setTopic();return;
            case INGREDIENT:setIngredient();return;
            case PUMP:setPump();return;
            case RECIPE: setRecipe();return;
            default: error();return;
        }
    }

    private void setTopic(){
        Log.i(TAG, "setTopic: "+id.toString());
        topic = Topic.getTopic(id);
        binding.textViewTitle.setText(topic.getName());
        binding.textViewDescription.setText(topic.getDescription());
        binding.textViewDescription.setVisibility(View.VISIBLE);
    }

    private void setPump(){
        Log.i(TAG, "setPump: "+id.toString());
        pump = Pump.getPump(id);
        if(pump != null) {
            binding.textViewTitle.setText(
                    new StringBuilder()
                            .append("Pumpe: ")
                            .append(pump.getIngredientName())
                            .toString());

            //activity.getLayoutInflater().inflate(R.layout.layout_pump, null);
            binding.includePump.getRoot().setVisibility(View.VISIBLE);
            View.OnClickListener listener = v -> {
                Bundle b = new Bundle();
                b.putString("Type", ModelType.INGREDIENT.name());
                b.putLong("ID", pump.getCurrentIngredient().getID());
                NavHostFragment
                        .findNavController(ModelFragment.this)
                        .navigate(R.id.action_modelFragment_self,
                                b);
            };

            binding.includePump.textViewMinPumpVolume
                    .setText(String.valueOf(pump.getMinimumPumpVolume()));
            binding.includePump.textViewPumpIngredientName.setOnClickListener(listener);
            binding.textViewTitle.setOnClickListener(listener);
            binding.includePump.textViewPumpVolume
                    .setText(String.valueOf(pump.getVolume()));
            binding.includePump.textViewPumpIngredientName
                    .setText(pump.getIngredientName());
            if (pump.isAvailable()) {
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            } else {
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }
        }else{
            error();
        }
    }

    private void setIngredient(){
        Log.i(TAG, "setIngredient: "+id.toString());
        ingredient = Ingredient.getIngredient(id);
        if (ingredient != null) {
            binding.textViewTitle.setText(ingredient.getName());
            if(ingredient.isAlcoholic()){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }if(ingredient.isAvailable()){
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            }else{
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }
            if(AdminRights.isAdmin()){
                //binding.includeIngredientAdmin.getRoot().setVisibility(View.VISIBLE);
                binding.includeIngredientAdmin.getRoot().setVisibility(View.VISIBLE);
                TextView vol = (TextView) binding.includeIngredientAdmin
                        .getRoot().getViewById(R.id.textView_ingredient_volume);
                vol.setText(String.format("%d ml", ingredient.getVolume()));
                //binding.includeIngredientAdmin.textViewIngredientVolume.setVisibility(View.VISIBLE);
                //binding.includeIngredientAdmin.textViewIngredientVolume.setText(ingredient.getVolume());
                /*
                binding.includeIngredientAdmin.getRoot().getViewById(R.id.imageView_ingredient_pump)
                        .setOnClickListener(v -> {
                    Bundle b = new Bundle();
                    b.putString("Type","PUMP");
                    b.putLong("ID", ingredient.getPumpId());
                    if(ingredient.getPumpId()>0) {
                        NavHostFragment.findNavController(ModelFragment.this)
                                .navigate(R.id.action_modelFragment_self, b);
                    }else{
                        Toast.makeText(this.getContext(),"Keine Pumpe verbunden!", Toast.LENGTH_SHORT).show();
                    }
                });

                 */

                binding.includeIngredientAdmin.imageViewIngredientPump
                        .setOnClickListener(v -> {
                            Bundle b = new Bundle();
                            b.putString("Type","PUMP");
                            b.putLong("ID", ingredient.getPumpId());
                            if(ingredient.getPumpId()>0) {
                                NavHostFragment.findNavController(ModelFragment.this)
                                        .navigate(R.id.action_modelFragment_self, b);
                            }else{
                                Toast.makeText(this.getContext(),"Keine Pumpe verbunden!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }else{
            error();
        }

    }

    private void setRecipe(){
        Log.i(TAG, "setRecipe: "+id.toString());
        recipe = Recipe.getRecipe(id);
        if(recipe != null){
            binding.textViewTitle.setText(recipe.getName());
            if(recipe.isAlcoholic()){
                binding.includeAlcoholic.getRoot().setVisibility(View.VISIBLE);
            }if(recipe.isAvailable()){
                binding.includeAvailable.getRoot().setVisibility(View.VISIBLE);
            }else{
                binding.includeNotAvailable.getRoot().setVisibility(View.VISIBLE);
            }

            binding.includeIngredients.textViewNameListTitle.setText("Zutaten");


/*
            ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter ingredientListAdapter =
                    new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe.getID());
            binding.includeIngredients.textViewNameListTitle.setText("Zutaten");
            binding.includeIngredients.recylerViewNames.setLayoutManager(ingredientListAdapter.getManager(this.getContext()));
            binding.includeIngredients.recylerViewNames.setAdapter(ingredientListAdapter);
            binding.includeIngredients.getRoot().setVisibility(View.VISIBLE);

            ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter topicListAdapter =
                    new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe.getID());
            binding.includeTopics.textViewNameListTitle.setText("Servierempfehlungen");
            binding.includeTopics.recylerViewNames.setLayoutManager(topicListAdapter.getManager(this.getContext()));
            binding.includeTopics.recylerViewNames.setAdapter(topicListAdapter);
            binding.includeTopics.getRoot().setVisibility(View.VISIBLE);

 */


            ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter ingredientAdapter
                    = new ListRecyclerViewAdapters.RecipeIngredientListRecyclerViewAdapter(recipe);

            ListLayout.set(binding.includeIngredients.textViewNameListTitle,
                    "Zutaten",
                    binding.includeIngredients.recylerViewNames,
                    this.getContext(),
                    ingredientAdapter,
                    this,
                    binding.includeIngredients.imageButtonListDelete,
                    binding.includeIngredients.imageButtonListEdit,
                    binding.includeIngredients.imageButtonListAdd);
            binding.includeIngredients.getRoot().setVisibility(View.VISIBLE);

            ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter topicAdapter
                    = new ListRecyclerViewAdapters.RecipeTopicListRecyclerViewAdapter(recipe);

            ListLayout.set(binding.includeTopics.textViewNameListTitle,
                    "Serviervorschläge",
                    binding.includeTopics.recylerViewNames,
                    this.getContext(),
                    topicAdapter,
                    this,
                    binding.includeTopics.imageButtonListDelete,
                    binding.includeTopics.imageButtonListEdit,
                    binding.includeTopics.imageButtonListAdd);
            binding.includeTopics.getRoot().setVisibility(View.VISIBLE);

            




        }else{
            error();
        }
    }


    private void error(){
        Log.i(TAG, "error");
        NavHostFragment.findNavController(ModelFragment.this)
                .navigate(R.id.action_modelFragment_to_mainActivity);
        return;
    }

    private void setFAB(){
        Log.i(TAG, "setFAB");
        if(AdminRights.isAdmin()) {
            Log.i(TAG, "setFAB: is Admin: make edit possible");
            binding.imageViewEdit.setVisibility(View.VISIBLE);
            binding.imageViewEdit.setOnClickListener(v -> {
                Bundle b = new Bundle();
                b.putString("Type", type.name());
                b.putLong("ID", id);

                NavHostFragment
                        .findNavController(ModelFragment.this)
                        .navigate(R.id.action_modelFragment_to_editModelFragment,
                                b);
            });
        }else{
            Log.i(TAG, "setFAB: is no Admin: make edit impossible");
            binding.imageViewEdit.setVisibility(View.GONE);

        }
        /*
        activity.setFAB(v -> {
            Bundle b = new Bundle();
            b.putString("Type", type.name());
            b.putLong("ID", id);

            NavHostFragment
                    .findNavController(ModelFragment.this)
                    .navigate(R.id.action_modelFragment_to_editModelFragment,
                            b);
        }, R.drawable.ic_edit);
         */
        if(type.equals(ModelType.RECIPE)) {
            if(this.recipe!=null&&this.recipe.isAvailable()) {
                Log.i(TAG, "setFAB: recipe is shown and recipe is available");
                activity.setFAB(v -> {
                    recipe.send();
                    Toast.makeText(this.getContext(), "Cocktail in Bearbeitung.", Toast.LENGTH_SHORT).show();
                }, R.drawable.ic_send);
                return;
            }else if(this.recipe==null){
                Toast.makeText(this.getContext(), "Rezept nicht verfügbar.", Toast.LENGTH_SHORT).show();
                error();
            }
        }
        Log.i(TAG, "setFAB: no recipe is shown or recipe is not available");
        activity.invisibleFAB();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentModelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        activity = (ModelActivity) getActivity();
        if (activity != null) {
            Objects.requireNonNull(activity.getSupportActionBar()).setTitle("");
        }
        Bundle args = getArguments();

        if(args != null) {
            Log.i(TAG, "onViewCreated:getArguments!=null: "+ args);
            type = ModelType.valueOf(args.getString("Type"));
            id = args.getLong("ID");
            Log.i(TAG, "onViewCreated:type: "+ type);
            Log.i(TAG, "onViewCreated:type: "+ id);
            setUP();
        }else{
            Log.i(TAG, "onViewCreated:getArguments==null ");
            error();
        }
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onViewCreated");
        super.onStart();
        setFAB();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        if(!AdminRights.isAdmin()){
            if (type == ModelType.PUMP) {
                Toast.makeText(this.getContext(),
                                "Pumpen sind für normale Nutzer nicht zugreifbar.",
                                Toast.LENGTH_SHORT)
                        .show();
                NavHostFragment
                        .findNavController(ModelFragment.this)
                        .navigate(R.id.action_modelFragment_to_listFragment);
                return;
            }
            binding.imageViewEdit.setVisibility(View.GONE);
            binding.includeIngredientAdmin.getRoot().setVisibility(View.GONE);
            binding.includeIngredients.imageButtonListAdd.setVisibility(View.GONE);
            binding.includeIngredients.imageButtonListDelete.setVisibility(View.GONE);
            binding.includeIngredients.imageButtonListEdit.setVisibility(View.GONE);
            binding.includeTopics.imageButtonListAdd.setVisibility(View.GONE);
            binding.includeTopics.imageButtonListDelete.setVisibility(View.GONE);
            binding.includeTopics.imageButtonListEdit.setVisibility(View.GONE);
        }else{
            setUP();
            setFAB();
        }
    }


}