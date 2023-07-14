package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.security.AppUriAuthenticationPolicy;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.Objects;

/**
 * @author Johanna Reidt
 * @created Di. 27.Jun 2023 - 15:51
 * @project CocktailMachine
 */
public class GetDialog {

    //Title
    public static void setTitle(Activity activity, ModelType modelType, long ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Änderung");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.TitleChangeView titleChangeView =
                new GetDialog.TitleChangeView(
                        modelType,
                        ID,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            titleChangeView.save();
        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }
    public static class TitleChangeView{

        private final TextView t;
        private final EditText e;
        private final long ID;
        private final ModelType modelType;
        private final View v;
        private TitleChangeView(ModelType modelType, long ID, View v) {
            this.t = (TextView) v.findViewById(R.id.textView_edit_text);
            this.e = (EditText) v.findViewById(R.id.editText_edit_text);
            String name = setName();
            this.e.setHint(name);
            this.e.setText(name);
            this.t.setText("Name: ");
            this.e.setInputType(InputType.TYPE_CLASS_TEXT);
            this.modelType = modelType;
            this.ID = ID;
            this.v = v;

        }

        private String setName(){
            switch(modelType){
                case INGREDIENT:
                    return Ingredient.getIngredient(ID).getName();
                case RECIPE:
                    return Recipe.getRecipe(ID).getName();
                case TOPIC:
                    return Topic.getTopic(ID).getName();
            }
            return "";
        }
        private String getName(){
            return e.getText().toString();
        }
        public boolean save(){
            switch(modelType){
                case INGREDIENT:
                    Ingredient ingredient = Ingredient.getIngredient(ID);
                    ingredient.setName(getName());
                    try {
                        ingredient.save();
                        return true;
                    } catch (NotInitializedDBException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case RECIPE:
                    Recipe recipe = Recipe.getRecipe(ID);
                    recipe.setName(getName());
                    try {
                        recipe.save();
                        return true;
                    } catch (NotInitializedDBException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case TOPIC:
                    Topic topic = Topic.getTopic(ID);
                    topic.setName(getName());
                    try {
                        topic.save();
                        return true;
                    } catch (NotInitializedDBException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    }




    //Pump

    public static void setPumpVolume(Activity activity, Pump pump){
        int vol = 0;
        if (pump != null) {
            try {
                pump.fill(vol);

            } catch (MissingIngredientPumpException e) {
                e.printStackTrace();
            }
            pump.sendRefill(activity);
        }//TODO: pump is null
    }


    public static void setPumpMinVolume(Activity activity, Pump pump){
        int vol = 0;
        if (pump != null) {
            pump.setMinimumPumpVolume(vol);
            //pump.se(activity);
            //CocktailMachine.
            pump.calibrate(activity, 0,0,0,0);
        }//TODO: pump is null
    }


    //DELETE FROM RECIPE
    /**
     * delete element with id from type modeltype is deleted
     * @author Johanna Reidt
     * @param activity
     * @param recipe
     * @param modelType
     */
    public static void deleteFromRecipe(Activity activity, Recipe recipe, ModelType modelType, Long ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        StringBuilder b = new StringBuilder();
        b.append("Möchtest du ");
        switch (modelType){
            case TOPIC:
                b.append("den Serviervorschlag "+ Objects.requireNonNull(Topic.getTopic(ID)).getName());
                break;
            case INGREDIENT:
                b.append("die Zutat "+ Objects.requireNonNull(Ingredient.getIngredient(ID)).getName());
                break;
        }
        b.append(" aus dem Rezept "+recipe.getName()+" entfernen?");
        builder.setTitle(b.toString());
        builder.setPositiveButton("Bitte löschen!", (dialog, which) -> {
            deleteElementFromRecipe(recipe,
                    modelType,
                    ID);
        });
        builder.setNeutralButton("Nein.", (dialog, which) -> {

        });
        builder.show();
    }


    //DELETE FOR ALL

    /**
     * check if element with id from type modeltype is deleted
     * @author Johanna Reidt
     * @param modelType
     * @param ID
     * @return
     */
    public static boolean checkDeleted(ModelType modelType, Long ID){
        switch (modelType){
            case RECIPE:
                return Recipe.getRecipe(ID)==null;
            case PUMP:
                return Pump.getPump(ID)==null;
            case TOPIC:
                return Topic.getTopic(ID)==null;
            case INGREDIENT:
                return Ingredient.getIngredient(ID)==null;
        }
        return false;
    }


    /**
     * delete element with id from type modeltype is deleted
     * @author Johanna Reidt
     * @param activity
     * @param modelType
     * @param title
     * @param ID
     */
    public static void delete(Activity activity, ModelType modelType, String title, Long ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getDeleteTitle(modelType, title));
        builder.setPositiveButton("Bitte löschen!", (dialog, which) -> {
            try {
                deleteElement(modelType, ID);
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
            }
        });
        builder.setNeutralButton("Nein.", (dialog, which) -> {

        });
        builder.show();
    }

    private static String getDeleteTitle(ModelType modelType, String title){
        StringBuilder builder = new StringBuilder();
        builder.append("Möchtest du ");
        switch (modelType) {
            case RECIPE:
                builder.append("das Rezept ");
                break;
            case PUMP:
                builder.append("die Pumpe ");
                break;
            case TOPIC:
                builder.append("den Serviervorschlag ");
                break;
            case INGREDIENT:
                builder.append("die Zutat ");
                break;
        }
        builder.append(title);
        builder.append(" löschen?");
        return builder.toString();
    }

    private static void deleteElement(ModelType modelType,
                                      Long ID) throws NotInitializedDBException {
        switch (modelType){
            case RECIPE:
                Recipe.getRecipe(ID).delete();
            case PUMP:
                Pump.getPump(ID).delete();
            case TOPIC:
                Topic.getTopic(ID).delete();
            case INGREDIENT:
                Ingredient.getIngredient(ID).delete();
        }
    }

    private static void deleteElementFromRecipe(Recipe recipe,
                                                ModelType modelType,
                                                Long ID){
        switch (modelType){
            case TOPIC:
                recipe.removeTopic(ID);
            case INGREDIENT:
                recipe.removeIngredient(ID);
        }
    }
}
