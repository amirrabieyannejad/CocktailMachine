package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.databinding.FragmentModelBinding;
import com.example.cocktailmachine.ui.SecondFragment;

class ModelFragment extends Fragment {
    private FragmentModelBinding binding;
    private static final String TAG = "ModelFragment";


    private void setUP(String type, Long id){

    }

    private void setTopic(Long id){
        
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        binding = FragmentModelBinding.inflate(inflater, container, false);
        if(savedInstanceState != null) {
            String type = savedInstanceState.getString("Type");
            Long id = savedInstanceState.getLong("ID");
            setUP(type, id);

        }else{
            NavHostFragment.findNavController(ModelFragment.this)
                    .navigate(R.id.action_modelFragment_to_mainActivity);
        }
        return binding.getRoot();
    }
}