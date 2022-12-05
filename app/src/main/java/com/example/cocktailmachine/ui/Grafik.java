package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
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

        VectorDrawable vec = (VectorDrawable) myImage;
    }


}