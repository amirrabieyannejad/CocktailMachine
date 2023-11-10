package com.example.cocktailmachine.logic.Animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class DirectAnimation extends Animation {
    private View view;
    private float cx, cy;           // center x,y position of circular path
    private float prevX, prevY;     // previous x,y position of image during animation
    private float length;                // length of the path
    private float prevDx, prevDy;

    private int width, height, parentWidth, parentHeight;


    /**
     * @param view - View that will be animated
     * @param length - length of the path
     */
    public DirectAnimation(View view, float length){
        this.view = view;
        this.length = length;
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {

        this.width = width;
        this.height = height;
        this.parentWidth= parentWidth;
        this.parentHeight= parentHeight;
        prevX = 0-width;
        prevY = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(interpolatedTime == 0){
            t.getMatrix().setTranslate(prevDx, prevDy);
            return;
        }


        float position = (interpolatedTime * length);


        prevDx = position;
        prevDy = prevDy;


        t.getMatrix().setTranslate(position, prevDy);
    }
}
