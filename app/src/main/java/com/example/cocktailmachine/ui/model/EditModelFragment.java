package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.databinding.FragmentEditModelBinding;
import com.example.cocktailmachine.databinding.FragmentListBinding;
import com.example.cocktailmachine.ui.MainActivity;

public class EditModelFragment extends Fragment {
    private FragmentEditModelBinding binding;
    private MainActivity activity;
    private static final String TAG = "EditModelFragment";

    //data
    private Recipe recipe;
    private Topic topic;
    private Ingredient ingredient;
    private Pump pump;

    public EditModelFragment(){}


    private void setFAB(){
        activity.setFAB(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recipe != null){
                    Bundle b = new Bundle();
                    try {
                        recipe.save();

                        b.putString("Type", ModelFragment.ModelType.RECIPE.name());
                        b.putLong("ID", recipe.getID());
                        NavHostFragment
                                .findNavController(EditModelFragment.this)
                                .navigate(R.id.action_modelFragment_self,
                                        b);
                        return;
                    } catch (NotInitializedDBException e) {
                        e.printStackTrace();
                        return;
                    }
                }else if(topic != null){
                    Bundle b = new Bundle();
                    try {
                        topic.save();

                        b.putString("Type", ModelFragment.ModelType.TOPIC.name());
                        b.putLong("ID", topic.getID());
                        NavHostFragment
                                .findNavController(EditModelFragment.this)
                                .navigate(R.id.action_modelFragment_self,
                                        b);
                        return;
                    } catch (NotInitializedDBException e) {
                        e.printStackTrace();
                        return;
                    }
                }else if(ingredient != null){
                    Bundle b = new Bundle();
                    try {
                        ingredient.save();

                        b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                        b.putLong("ID", ingredient.getID());
                        NavHostFragment
                                .findNavController(EditModelFragment.this)
                                .navigate(R.id.action_modelFragment_self,
                                        b);
                        return;
                    } catch (NotInitializedDBException e) {
                        e.printStackTrace();
                        return;
                    }
                }else if(pump != null){
                    Bundle b = new Bundle();
                    try {
                        pump.save();

                        b.putString("Type", ModelFragment.ModelType.INGREDIENT.name());
                        b.putLong("ID", pump.getID());
                        NavHostFragment
                                .findNavController(EditModelFragment.this)
                                .navigate(R.id.action_modelFragment_self,
                                        b);
                        return;
                    } catch (NotInitializedDBException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
    }







    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_edit_model, container, false);

        Log.i(TAG, "onCreateView");
        binding = FragmentEditModelBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();

        if(savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState != null");
            String type = savedInstanceState.getString("Type");
            if(savedInstanceState.containsKey("ID")){
                Log.i(TAG, "savedInstanceState has ID -> Edit");
                Long id = savedInstanceState.getLong("ID");
                setUpEdit(type, id);
            }else{
                Log.i(TAG, "savedInstanceState has no ID -> New");
                setUpNew(type);
            }
        }else{
            Log.i(TAG, "savedInstanceState == null");
            setUp("Recipe");
        }
        return binding.getRoot();
    }
}