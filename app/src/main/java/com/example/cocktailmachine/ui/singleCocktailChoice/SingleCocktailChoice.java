package com.example.cocktailmachine.ui.singleCocktailChoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Orientation;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.db.elements.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.elements.SQLRecipeImageUrlElement;
import com.example.cocktailmachine.data.db.elements.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.logic.FlingAnalysis;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleCocktailChoice extends AppCompatActivity {

    private GestureDetector mDetector;
    private int counter = 0;
    fragment1 fragment1;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_cocktail_choice);

        fragmentManager = getSupportFragmentManager();


        this.fragment1 = new fragment1();
        //fragment.updateImage(getResources().getDrawable(R.drawable.glas2));

        replaceFragment(fragment1);
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
                fragment1.updateTextView(""+(++counter));
                replaceFragment(fragment1);
            }
            if(FlingAnalysis.getOrientationFromVelocity(velocityX,velocityY)==(Orientation.LEFT)){
                //fragment1 fragment  = fragment1.newInstance();
                fragment2 fragment = new fragment2();
                //fragment.updateTextView(String.valueOf(""+(--counter)));
                replaceFragment(fragment);
            }
            return true;
        }
    }

    private void replaceFragment(Fragment fragment){

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
               R.anim.slide_in,  // enter
                R.anim.fade_out  // popExit
        );

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