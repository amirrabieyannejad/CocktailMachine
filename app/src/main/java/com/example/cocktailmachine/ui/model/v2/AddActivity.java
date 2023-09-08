package com.example.cocktailmachine.ui.model.v2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.databinding.ActivityAddBinding;
import com.example.cocktailmachine.databinding.ActivityDisplayBinding;

public class AddActivity extends BasicActivity {
    ActivityAddBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DatabaseConnection.initializeSingleton(this);
    }

    @Override
    void preSetUp() {

    }

    @Override
    void setUpPump() {

    }

    @Override
    void setUpTopic() {

    }

    @Override
    void setUpIngredient() {

    }

    @Override
    void setUpRecipe() {

    }

    @Override
    void postSetUp() {

    }


}