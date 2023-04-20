package com.example.cocktailmachine.ui.singleCocktailChoice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.databinding.FragmentFragment1Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment1 extends Fragment implements FragmentListenerSingleCocktailChoice {
    ImageView imageView;
    TextView textView;
    View view;
    Context context;
    private FragmentListenerSingleCocktailChoice listener;

    FragmentFragment1Binding binding;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment1 newInstance(String param1, String param2) {
        fragment1 fragment = new fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static fragment1 newInstance(){
        fragment1 fragment = new fragment1();
        //Drawable drawable = context.getResources().getDrawable(R.drawable.glas2);
        //fragment.imageView.setImageDrawable(drawable);

        return fragment;
    }

    public void updateImage(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    @Override
    public void updateTextView(String text){
        binding.textViewCocktail.setText(text);
        //this.textView.setText(text);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*this.view =  inflater.inflate(R.layout.fragment_fragment1, container, false);
        this.imageView = this.view.findViewById(R.id.imageCocktail);
        this.textView = this.view.findViewById(R.id.textViewCocktail);
        return this.view;*/
        this.binding = FragmentFragment1Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

    }


    /*
    @Override

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListenerSingleCocktailChoice) {
            listener = (FragmentListenerSingleCocktailChoice) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentAListener");
        }
    }
    */


}