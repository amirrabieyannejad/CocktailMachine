package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.ui.model.ModelType;

import java.util.Objects;

/**
 * @author Johanna Reidt
 * @created Di. 27.Jun 2023 - 15:51
 * @project CocktailMachine
 */
public class GetDialog {

    //ERROR
    public static void errorMessage(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("FEHLER");
        builder.setMessage("Es ist ein Fehler aufgetreten. Wir empfehlen die Daten zu aktualisieren.");

        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.setPositiveButton("Aktualisieren", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Pump.sync(activity);
                Recipe.sync(activity);
                Toast.makeText(activity,"Synchronisierung läuft!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    //Title

    /**
     * gets Title Change Dialog, saves and send to esp if required
     * @author Johanna Reidt
     * @param activity
     * @param modelType
     * @param ID
     */
    public static void setTitle(Activity activity, ModelType modelType, long ID){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Änderung");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.TitleChangeView titleChangeView =
                new GetDialog.TitleChangeView(
                        activity,
                        modelType,
                        ID,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            titleChangeView.save();
            titleChangeView.send();

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
        private final Activity activity;
        private TitleChangeView(Activity activity, ModelType modelType, long ID, View v) {
            this.activity = activity;
            this.t = (TextView) v.findViewById(R.id.textView_edit_text);
            this.e = (EditText) v.findViewById(R.id.editText_edit_text);
            String name = getNameFromDB();
            this.e.setHint(name);
            this.e.setText(name);
            this.t.setText("Name: ");
            this.e.setInputType(InputType.TYPE_CLASS_TEXT);
            this.modelType = modelType;
            this.ID = ID;
            this.v = v;

        }

        private String getNameFromDB(){
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
                    return ingredient.save();
                case RECIPE:
                    Recipe recipe = Recipe.getRecipe(ID);
                    recipe.setName(getName());
                    return recipe.save();
                case TOPIC:
                    Topic topic = Topic.getTopic(ID);
                    topic.setName(getName());
                    return topic.save();
            }
            return false;
        }

        public void send(){
            if(ModelType.RECIPE == modelType) {
                Objects.requireNonNull(Recipe.getRecipe(this.ID)).send(activity);
            }
        }
    }




    //Pump Volume

    public static void setPumpVolume(Activity activity, Pump pump){
        if (pump != null) {
            pump.sendRefill(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze das jetzt vorhandene Volumen:");

            View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
            GetDialog.VolumeChangeView volumeChangeView =
                    new GetDialog.VolumeChangeView(
                            activity,
                            pump,
                            v,
                            false);

            builder.setView(v);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                volumeChangeView.save();
                volumeChangeView.send();

            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }


    public static void setPumpMinVolume(Activity activity, Pump pump){
        if (pump != null) {
            pump.sendRefill(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze das Minimalvolumen:");

            View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
            GetDialog.VolumeChangeView volumeChangeView =
                    new GetDialog.VolumeChangeView(
                            activity,
                            pump,
                            v,
                            true);

            builder.setView(v);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                volumeChangeView.save();
                volumeChangeView.send();

            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }


    public static class VolumeChangeView{

        private final TextView t;
        private final EditText e;
        private final Pump pump;
        private final boolean minimum;
        private final View v;
        private final Activity activity;
        private VolumeChangeView(Activity activity, Pump pump, View v, boolean minimum) {
            this.activity = activity;
            this.t = (TextView) v.findViewById(R.id.textView_edit_text);
            this.e = (EditText) v.findViewById(R.id.editText_edit_text);

            this.t.setText("Volumen: ");
            this.e.setInputType(InputType.TYPE_CLASS_TEXT);
            this.minimum = minimum;
            this.pump = pump;
            this.v = v;
            String name = getVolumeFromDB();
            this.e.setHint(name +" ml");
            this.e.setText(name);
        }

        private String getVolumeFromDB(){
            if(this.minimum){
                return this.pump.getMinimumPumpVolume()+" ml";
            }
            return String.valueOf(this.pump.getVolume());
        }
        private int getVolume(){
            return Integer.getInteger(e.getText().toString());
        }
        public boolean save(){
            try {
                pump.fill(getVolume());
            } catch (MissingIngredientPumpException ex) {
                ex.printStackTrace();
            }
            return pump.save();
        }

        public void send(){
            pump.sendRefill(activity, getVolume());
        }
    }



    //Alcoholic
    public static void setAlcoholic(Activity activity, Ingredient ingredient){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Änderung");

        builder.setMessage("Ist die Zutat alkoholisch?");

        builder.setPositiveButton("Ja", (dialog, which) -> {
            ingredient.setAlcoholic(true);
            ingredient.save();

        });
        builder.setNegativeButton("Nein", (dialog, which) -> {
            ingredient.setAlcoholic(false);
            ingredient.save();

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }


    //Description
    public static void setDescribtion(Activity activity, Topic topic){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Änderung");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_long_edit_text, null);
        GetDialog.LongEditChangeView longEditChangeView =
                new GetDialog.LongEditChangeView(
                        topic,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            longEditChangeView.save();
        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }


    public static class LongEditChangeView{

        private final TextView t;
        private final EditText e;
        private final Topic topic;
        private final View v;
        private LongEditChangeView( Topic topic, View v) {
            this.t = (TextView) v.findViewById(R.id.textView_long_edit_title);
            this.e = (EditText) v.findViewById(R.id.editText_long_edit_txt);

            this.t.setText("Beschreibung: ");
            this.e.setInputType(InputType.TYPE_CLASS_TEXT);
            this.topic = topic;
            this.v = v;
            String name = getDescribtionFromDB();
            this.e.setHint(name);
            this.e.setText(name);
        }

        private String getDescribtionFromDB(){
            return this.topic.getDescription();
        }
        private String getDescription(){
            return e.getText().toString();
        }
        public boolean save(){
            this.topic.setDescription(getDescription());
            return topic.save();
        }
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

    /**
     * returns delete txt for dialog
     * @author Johanna Reidt
     * @param modelType
     * @param title
     * @return
     */
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

    /**
     * deletes element from db
     * @author Johanna Reidt
     * @param modelType
     * @param ID
     * @throws NotInitializedDBException
     */
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

    /**
     * delete either topic or ingredient from recipe
     * @author Johanna Reidt
     * @param recipe
     * @param modelType
     * @param ID
     */
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
