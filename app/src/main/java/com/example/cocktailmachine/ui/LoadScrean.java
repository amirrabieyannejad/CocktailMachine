package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.logic.Animation.DirectAnimation;

public class LoadScrean extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screan);



        ImageView image = findViewById(R.id.activity_loadScrean_imageView2);
        //new AnimatedImage(this,image,-500,-500).startAnimation();
    }


    private class AnimatedImage{
        private int startX;
        private int startY;
        private ImageView image;

        private boolean readyForAnimation;

        private Activity activity;

        AnimatedImage(Activity activity,ImageView imageView, int startX,int startY){
            this.activity = activity;
            this.image = imageView;
            this.startX = startX;
            this.startY = startY;
        }

        public void startAnimation(){
            Animation anim = new DirectAnimation(image,1500);
            anim.setDuration(3000);
            //anim.setRepeatCount(Animation.ABSOLUTE);
            image.startAnimation(anim);
        }


    }
}