package com.example.cocktailmachine.ui.model;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.ui.model.enums.FragmentType;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.example.cocktailmachine.ui.model.helper.GetActivity;

/**
 * @author Johanna Reidt
 * @created Di. 27.Jun 2023 - 13:16
 * @project CocktailMachine
 */
public abstract class BasicActivity extends AppCompatActivity {

    private static final String TAG = "BasicActivity" ;
    private Long id;
    private FragmentType fragmentType;
    private ModelType modelType;

    private boolean showAll = false;


    FragmentType getFragmentType(){
        return fragmentType;
    }
    ModelType getModelType(){
        return modelType;
    }

    Long getID(){
        return id;
    }

    boolean getShowAll(){
        return this.showAll;
    }

    abstract void preSetUp();

    abstract void setUpPump();
    abstract void setUpTopic();
    abstract void setUpIngredient();
    abstract void setUpRecipe();

    protected void setUp(){
        preSetUp();
        switch (getModelType()){
            case RECIPE: setUpRecipe(); break;
            case INGREDIENT: setUpIngredient();break;
            case TOPIC: setUpTopic();break;
            case PUMP: setUpPump();break;
        }
        postSetUp();
    }

    abstract void postSetUp();

    protected void load(long ID, FragmentType fragmentType, ModelType modelType){
        this.id = ID;
        this.modelType = modelType;
        this.fragmentType = fragmentType;
        setUp();
    }

    public abstract void reload();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display);
        readIntent();
    }
    private void readIntent(){
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle == null){
            Log.e(TAG, "readIntent: bundle null");
            return;
        }
        id = bundle.getLong(
                GetActivity.ID,
                -1L);
        modelType = ModelType.valueOf(
                bundle.getString(
                        GetActivity.MODELTYPE,
                        ModelType.RECIPE.toString()));
        fragmentType = FragmentType.valueOf(
                bundle.getString(
                        GetActivity.FRAGMENTTYPE,
                        FragmentType.List.toString()));
        showAll = bundle.getBoolean(
                        GetActivity.DISPLAYALL);
        Log.v(TAG, "readIntent: id "+id );
        Log.v(TAG, "readIntent: modelType "+modelType );
        Log.v(TAG, "readIntent: fragmentType "+fragmentType );
        Log.v(TAG, "readIntent: showAll "+showAll );
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUp();
    }






    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }



}
