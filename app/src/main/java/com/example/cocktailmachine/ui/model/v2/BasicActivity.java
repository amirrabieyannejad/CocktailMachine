package com.example.cocktailmachine.ui.model.v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.data.db.Buffer;

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


    FragmentType getFragmentType(){
        return fragmentType;
    }
    ModelType getModelType(){
        return modelType;
    }

    Long getID(){
        return id;
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
        Log.i(TAG, "readIntent: id "+id );
        Log.i(TAG, "readIntent: modelType "+modelType );
        Log.i(TAG, "readIntent: fragmentType "+fragmentType );
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUp();
    }



    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Buffer.getSingleton(this).lowMemory();
        //TO DO: mostly close db cache
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Buffer.getSingleton(this).lowMemory();
        //TO DO: half close db cache
    }


    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }



}
