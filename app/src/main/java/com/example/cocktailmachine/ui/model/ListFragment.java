package com.example.cocktailmachine.ui.model;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.databinding.FragmentListBinding;
import com.example.cocktailmachine.databinding.FragmentModelBinding;

class ListFragment extends Fragment {
    private FragmentListBinding binding;
    private static final String TAG = "ListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = FragmentListBinding.inflate(inflater, container, false);
        if(savedInstanceState != null) {
            String type = savedInstanceState.getString("Type");
            Long id = savedInstanceState.getLong("ID");
            setUP(type, id);
        }else{
            error();
        }
        return binding.getRoot();
    }
}