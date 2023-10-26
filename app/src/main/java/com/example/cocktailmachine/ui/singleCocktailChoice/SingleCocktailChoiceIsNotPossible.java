package com.example.cocktailmachine.ui.singleCocktailChoice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.ui.Menue;

public class SingleCocktailChoiceIsNotPossible extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cocktail_choice_is_not_possible);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Intent intent = new Intent(this, Menue.class);
        startActivity(intent);
    }
}