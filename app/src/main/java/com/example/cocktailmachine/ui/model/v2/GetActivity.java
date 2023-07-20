package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;

/**
 * @author Johanna Reidt
 * @created Mo. 26.Jun 2023 - 15:00
 * @project CocktailMachine
 */
public class GetActivity {
    public static final String ID = "ID";
    public static final String MODELTYPE = "MODELTYPE";
    public static final String FRAGMENTTYPE = "FRAGMENTTYPE";
    public static void goTo(Activity activity, FragmentType fragmentType, ModelType modelType){
        //TO DO
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.RECIPE.name());
        bundle.putString("FragmentType", FragmentType.List.name());
        startActivity(intent, bundle);
         */
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong(ID, -1L);
        bundle.putString(FRAGMENTTYPE, fragmentType.toString());
        bundle.putString(MODELTYPE, modelType.toString());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goTo(Activity activity, FragmentType fragmentType, ModelType modelType, Long id){
        //TO DO
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.RECIPE.name());
        bundle.putString("FragmentType", FragmentType.List.name());
        startActivity(intent, bundle);
         */
        if(fragmentType.equals(FragmentType.List)){
            throw new IllegalArgumentException("has to be an edit or model.");
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putLong(ID, id);
        bundle.putString(MODELTYPE, modelType.toString());
        bundle.putString(FRAGMENTTYPE, fragmentType.toString());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startAgain(Activity activity) {
        //TODO: go back to device scan
    }
}
