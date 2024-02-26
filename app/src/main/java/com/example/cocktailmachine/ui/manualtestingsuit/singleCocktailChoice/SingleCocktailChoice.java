package com.example.cocktailmachine.ui.manualtestingsuit.singleCocktailChoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.SingeltonTestdata;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.data.enums.Orientation;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.logic.FillingAnalysis;

import java.util.LinkedList;
import java.util.List;

public class SingleCocktailChoice extends AppCompatActivity {

    //TODO: wahrscheinlich löschbar
    private GestureDetector mDetector;
    private int counter = 0;
    private final int fragmentCounter = 0;



    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    SingeltonTestdata singeltonCocktail = SingeltonTestdata.getSingelton();
    List<Recipe> recipes = new LinkedList();

    private static final String TAG = "CocktailList";

    List<Recipe> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cocktail_choice);

        /*
        if(!DatabaseConnection.isInitialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(this, AdminRights.getUserPrivilegeLevel());// UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.i(TAG, "onCreate: DataBase is initialized");
                //Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
        }else{
            Log.i(TAG, "onCreate: DataBase is already initialized");
        }

         */



        testData = new LinkedList<>();
        counter = 0;
        while (testData.size() == 0 && counter++ < 9){
            testData = Recipe.getAllRecipes(this);
        }



        recipes.add(singeltonCocktail.getRecipe());
        recipes.add(singeltonCocktail.getRecipe2());

        //load List of available recipes
       /* try {
            this.recipes = loadRecipes(this);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }*/

        //initialise Fragment Manager
        fragmentManager = getSupportFragmentManager();

        String startMassage = getResources().getString(R.string.swipe);
        fragment1 f1 = fragment1.newInstance(startMassage);
        //f2 = fragment2.newInstance();
        //fragment.updateImage(getResources().getDrawable(R.drawable.glas2));

        replaceFragment(f1);
        //fragment.updateTextView(new Integer(this.counter).toString());
        // this is the view we will add the gesture detector to
        View myView = findViewById(R.id.frameLayout);

        // get the gesture detector
        mDetector = new GestureDetector(this, new MyGestureListener());

        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);

    }

    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);
        }

    };


    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            Log.d("TAG", "onFling: "+velocityX);
            Log.d("TAG", "onFling: "+velocityY);
            Log.d("TAG", "onFling: "+ FillingAnalysis.getOrientationFromVelocity(velocityX,velocityY));

            Orientation flingOrientation = FillingAnalysis.getOrientationFromVelocity(velocityX,velocityY);

            String oldText = testData.get(counter).getName();
            fragment1 oldFragment = fragment1.newInstance(oldText);
            setSlideOutAnimationFragment(oldFragment,flingOrientation);

            if(FillingAnalysis.getOrientationFromVelocity(velocityX,velocityY)==(Orientation.RIGHT)){
                //TODO Entfernung dieser Testzeilen
                if(++counter >= testData.size()){
                    counter = 0;
                }
            }
            if(FillingAnalysis.getOrientationFromVelocity(velocityX,velocityY)==(Orientation.LEFT)) {
                //TODO Entfernung dieser Testzeilen
                if (--counter < 0) {
                    counter = testData.size() - 1;
                }
            }

            String newText = testData.get(counter).getName();
            //fragment1 fragment = fragment1.newInstance(newText);
            fragment1 fragment = null;
            try {
                fragment = fragment1.newInstance(testData.get(counter));
            } catch (TooManyTimesSettedIngredientEcxception e) {
                throw new RuntimeException(e);
            } catch (NoSuchIngredientSettedException e) {
                throw new RuntimeException(e);
            }
            replaceFragmentWithOrientation(fragment,flingOrientation);

            return true;
        }
    }


    private void replaceFragment(Fragment fragment){

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragmentWithOrientation(Fragment fragment, Orientation orientation){

        fragmentTransaction = fragmentManager.beginTransaction();

        switch (orientation){
            case RIGHT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.fade_out  // popExit
                );
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left,  // enter
                        R.anim.fade_out  // popExit
                );
                break;
            default:
                fragmentTransaction.setCustomAnimations(
                        R.anim.fade_in,  // enter
                        R.anim.fade_out  // popExit
                );
        }
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private List<Recipe> loadRecipes(Context context) throws NotInitializedDBException {
        List<Recipe> recipes = Recipe.getRecipes(context);
        /*
        try {
            recipes = DatabaseConnection.getDataBase().loadAllRecipes();
        } catch (NotInitializedDBException e) {
            DatabaseConnection.initializeSingleton(context);
            recipes = DatabaseConnection.getDataBase().loadAllRecipes();
        }

         */
        return recipes;
    }


    private void setSlideOutAnimationFragment(Fragment fragment, Orientation orientation){

        fragmentTransaction = fragmentManager.beginTransaction();

        switch (orientation){
            case RIGHT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.fade_in_fast,
                        R.anim.slide_in_right  // enter
                );
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(
                        R.anim.fade_in_fast,
                        R.anim.slide_in_left  // enter
                );
                break;
            default:
                fragmentTransaction.setCustomAnimations(
                        R.anim.fade_in_fast,  // enter
                        R.anim.fade_out  // popExit
                );
        }
        fragmentTransaction.replace(R.id.frameLayout,fragment,"cocktail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static Orientation getOrientationFromVelocity(float velocityX, float velocityY){
        if(Math.abs(velocityX) >= Math.abs(velocityY)){
            if(velocityX>0){
                return(Orientation.RIGHT);
            }else{
                return(Orientation.LEFT) ;
            }
        }else{
            if(velocityY>0){
                return(Orientation.DOWN);
            }else{
                return (Orientation.TOP);
            }
        }
    }


}