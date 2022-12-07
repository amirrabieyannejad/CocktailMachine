package com.cocktailmachine.logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.core.content.res.ResourcesCompat;

import com.example.cocktailmachine.R;

public class BildgeneratorGlas {

    public static Bitmap BildgenerationGlas(){
        Resources res = this.getResources();

        String file = "res/drawable/Glas/Flüssigkeit/glasFlüssigkeit01.xml";
        //XmlResourceParser parser = getContext().getAssets().openXmlResourceParser(file);
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.glasFlüssigkeit01, null);
        Drawable myImage2 = ResourcesCompat.getDrawable(res, R.drawable.ic_launcher_background, null);

        Bitmap bm = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        myImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        myImage.setTint(11);
        myImage.draw(canvas);
        myImage2.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        myImage2.draw(canvas);
    }

}
