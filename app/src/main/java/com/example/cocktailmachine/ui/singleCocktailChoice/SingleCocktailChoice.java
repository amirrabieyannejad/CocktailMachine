package com.example.cocktailmachine.ui.singleCocktailChoice;

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
import com.example.cocktailmachine.data.Orientation;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.logic.FlingAnalysis;

import java.util.LinkedList;
import java.util.List;

public class SingleCocktailChoice extends AppCompatActivity {

    private GestureDetector mDetector;
    private int counter = 0;
    private int fragmentCounter = 0;

    fragment1 f1;
    fragment2 f2;


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    List<Recipe> recipes;

    List<String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cocktail_choice);

        testData = new LinkedList<>();
        testData.add("a");
        testData.add("b");
        testData.add("c");

        //load List of available recipes
       /* try {
            this.recipes = loadRecipes(this);
        } catch (NotInitializedDBException e) {
            e.printStackTrace();
        }*/

        //initialise Fragment Manager
        fragmentManager = getSupportFragmentManager();


        f1 = fragment1.newInstance();
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
            Log.d("TAG", "onFling: "+ FlingAnalysis.getOrientationFromVelocity(velocityX,velocityY));
            if(FlingAnalysis.getOrientationFromVelocity(velocityX,velocityY)==(Orientation.RIGHT)){
                //fragment1 fragment  = fragment1.newInstance();
                /*if(++counter >= recipes.size()){
                    counter = 0;
                }

                fragment1.updateTextView(recipes.get(counter).getName());
                replaceFragment(fragment1);*/

                //TODO Entfernung dieser Testzeilen

                fragment1 fragment = new fragment1();
                if(++counter >= testData.size()){
                    counter = 0;
                }

                String newText = testData.get(counter);
                f1 = fragment1.newInstance(newText);
                replaceFragment(f1);

                //fragment.updateTextView(testData.get(counter));
                //replaceFragment(fragment);
            }
            if(FlingAnalysis.getOrientationFromVelocity(velocityX,velocityY)==(Orientation.LEFT)){
                /*if(--counter < recipes.size()){
                    counter = recipes.size()-1;
                }

                //fragment1 fragment  = fragment1.newInstance();
                fragment2 fragment = new fragment2();
                //fragment.updateTextView(String.valueOf(""+(--counter)));
                replaceFragment(fragment);
                */

                //TODO Entfernung dieser Testzeilen


                if(--counter < 0){
                    counter = testData.size()-1;
                }

                fragmentCounter++;
                if(fragmentCounter%2 == 0){
                    String newText = testData.get(counter);
                    f1 = fragment1.newInstance(newText);
                    replaceFragment(f1);
                    //f1.updateTextView(newText);
                }else{
                    f2 = fragment2.newInstance();
                    replaceFragment(f2);
                    //f2.updateTextView(testData.get(counter));

                    //f2.updateTextView(testData.get(counter));
                }

                //Fragment fragment = getRightFragment().testData.get(counter);
                //replaceFragment(fragment);
            }
            return true;
        }
    }

    private Fragment getRightFragment(){
        fragmentCounter++;
        if(fragmentCounter%2 == 0){
            return(new fragment1());
        }
        return (new fragment2());
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
        List<Recipe> recipes;
        try {
            recipes = DatabaseConnection.getDataBase().loadAvailableRecipes();
        } catch (NotInitializedDBException e) {
            DatabaseConnection.initialize_singleton(context);
            recipes = DatabaseConnection.getDataBase().loadAvailableRecipes();
        }
        return(recipes);
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