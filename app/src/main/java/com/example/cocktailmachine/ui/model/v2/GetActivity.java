package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.ui.Menue;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
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
    public static void goToDisplay(Activity activity, FragmentType fragmentType, ModelType modelType){
        //TO DO
        /*
        Intent intent = new Intent(this, ModelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("ModelType", ModelType.RECIPE.name());
        bundle.putString("FragmentType", FragmentType.List.name());
        startActivity(intent, bundle);
         */
        Intent intent;
        if(fragmentType.equals(FragmentType.List)){
            intent = new Intent(activity, ListActivity.class);
        }else {
            intent = new Intent(activity, DisplayActivity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putLong(ID, -1L);
        bundle.putString(FRAGMENTTYPE, fragmentType.toString());
        bundle.putString(MODELTYPE, modelType.toString());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void goToDisplay(Activity activity, FragmentType fragmentType, ModelType modelType, Long id){
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
        Intent intent = new Intent(activity, DisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ID, id);
        bundle.putString(MODELTYPE, modelType.toString());
        bundle.putString(FRAGMENTTYPE, fragmentType.toString());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void startAgain(Activity activity) {
        //TODO: go back to device scan
        if(Dummy.isDummy) {
            goToMenu(activity);
        }else{
            Intent intent = new Intent(activity, DeviceScanActivity.class);
            activity.startActivity(intent);
        }
    }

    public static void goToFill(Activity activity, Recipe recipe){
        Intent intent = new Intent(activity, FillAnimation.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ID, recipe.getID());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void goToMenu(Activity activity) {
        Intent intent = new Intent(activity, Menue.class);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void waitNotSet(Activity activity) {
        Intent intent = new Intent(activity, WaitNotSetActivity.class);
        activity.startActivity(intent);
    }
}
