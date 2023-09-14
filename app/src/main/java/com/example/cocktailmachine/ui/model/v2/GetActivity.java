package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    private static final String TAG = "GetActivity";

    public static void goToDisplay(Activity activity, FragmentType fragmentType, ModelType modelType){
        Log.i(TAG, "goToDisplay: "+fragmentType.toString()+"  "+modelType.toString());
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
        Log.i(TAG, "goToDisplay: "+fragmentType.toString()+"  "+modelType.toString()+ "   "+id.toString());
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

    public static void goToAdd(Activity activity, ModelType modelType){
        Log.i(TAG, "goToAdd: "+modelType.toString());
        Intent intent = new Intent(activity, AddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MODELTYPE, modelType.toString());
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void startAgain(Activity activity) {
        Log.i(TAG, "startAgain");
        //TODO: go back to device scan
        if(Dummy.isDummy) {
            Log.i(TAG, "startAgain: dummy->menu");
            goToMenu(activity);
        }else{
            Log.i(TAG, "startAgain: real->scan");
            goToScan(activity);
        }
    }

    public static void goToScan(Activity activity){
        Log.i(TAG, "goToScan");
        Intent intent = new Intent(activity, DeviceScanActivity.class);
        activity.startActivity(intent);
    }

    public static void goToFill(Activity activity, Recipe recipe){
        Log.i(TAG, "goToFill");
        Intent intent = new Intent(activity, FillAnimation.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ID, recipe.getID());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void goToMenu(Activity activity) {
        Log.i(TAG, "goToMenu");
        Intent intent = new Intent(activity, Menue.class);
        activity.startActivity(intent);
        //activity.finish();
    }

    public static void waitNotSet(Activity activity) {
        Log.i(TAG, "waitNotSet");
        Intent intent = new Intent(activity, WaitNotSetActivity.class);
        activity.startActivity(intent);
    }

    public static void goBack(Activity activity) {
        Log.i(TAG, "goBack");
        if(!activity.moveTaskToBack(true)){
            error(activity);
        }
    }

    public static void error(Activity activity){
        Log.i(TAG, "error");
        Toast.makeText(activity, "Fehler!", Toast.LENGTH_SHORT).show();
        GetActivity.goToMenu(activity);
    }
}
