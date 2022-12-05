package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.cocktailmachine.R;

public class Grafik extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);
        Resources res = this.getResources();
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.glas8, null);
        Drawable myImage2 = ResourcesCompat.getDrawable(res, R.drawable.ic_launcher_background, null);
        VectorDrawable vec = (VectorDrawable) myImage;

        System.out.println("Can aply theme: "+ vec.canApplyTheme());
        Drawable.ConstantState cs = vec.getConstantState();
        System.out.println(cs);
        ValueAnimator va =  ValueAnimator.ofArgb(1111);
        Bitmap bm = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        myImage.draw(canvas);
        myImage2.setBounds(0, 0, canvas.getWidth()/2, canvas.getHeight()/2);
        myImage2.draw(canvas);
        ImageView iv =(ImageView) findViewById(R.id.ivtest);
        iv.draw(canvas);
        iv.setImageBitmap(bm);
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }


}