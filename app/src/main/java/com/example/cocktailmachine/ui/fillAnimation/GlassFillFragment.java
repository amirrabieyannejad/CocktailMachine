package com.example.cocktailmachine.ui.fillAnimation;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cocktailmachine.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GlassFillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GlassFillFragment extends Fragment {

    private String text = "";
    private Bitmap image = null;


    ImageView imageView;
    TextView textView;
    View view;


    public static GlassFillFragment newInstance(String text, Bitmap image) {
        GlassFillFragment fragment = new GlassFillFragment();
        fragment.text = text;
        fragment.image = image;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view =  inflater.inflate(R.layout.fragment_glass_fill, container, false);
        this.textView = this.view.findViewById(R.id.textViewCocktail);
        this.textView.setText(this.text);
        this.imageView = this.view.findViewById(R.id.imageCocktail);
        this.imageView.setImageBitmap(this.image);
        return this.view;
    }
}