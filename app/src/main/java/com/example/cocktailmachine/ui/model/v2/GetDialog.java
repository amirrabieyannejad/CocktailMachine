package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
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
                Recipe.syncRecipeDBWithCocktailmachine(activity);
                Toast.makeText(activity,"Synchronisierung läuft!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }




    //Recipe Send
    public static void sendRecipe(Activity activity, Recipe recipe){
        countDown(activity, recipe);
    }
    //Count down
    public static void countDown(Activity activity, Recipe recipe){
        //Better solution
        //https://stackoverflow.com/questions/10780651/display-a-countdown-timer-in-the-alert-dialog-box
        //CountDownRun countDownThread = new CountDownRun();
        //new Thread(countDownThread).start();
        //countDown(activity, recipe, countDownThread);
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Warteschlangen-Countdown");
        alertDialog.setMessage("...");
        alertDialog.show();

        recipe.addDialogWaitingQueueCountDown(activity, alertDialog);
    }

    public static void isUsersTurn(Activity activity, Recipe recipe){
        //Better solution
        //https://stackoverflow.com/questions/10780651/display-a-countdown-timer-in-the-alert-dialog-box
        //CountDownRun countDownThread = new CountDownRun();
        //new Thread(countDownThread).start();
        //countDown(activity, recipe, countDownThread);
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Du bist dran!");
        alertDialog.setMessage("Bitte, geh zur Cocktailmaschine und stelle dein Glas unter die MAschine. ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Los!", (dialog, which) -> {
            //TODO: send force start bluetooth thing
            GetActivity.goToFill(activity, recipe);
            dialog.dismiss();
        });
        alertDialog.show();   //
    }

    public static void isDone(Activity activity, Recipe recipe){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Fertig!");
        alertDialog.setMessage("Hole deinen Cocktail ab! ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Abgeholt!", (dialog, which) -> {
            int count = 4;
            try {
                BluetoothSingleton.getInstance().reset();
                GetDialog.showTopics( activity,  recipe);
                dialog.dismiss();
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
                count--;
                if(count == 0){
                    GetDialog.showTopics( activity,  recipe);
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();   //
    }

    public static void showTopics(Activity activity, Recipe recipe){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Serviervorschläge!");
        alertDialog.setMessage("Füge noch einen oder mehrer der Serviervorschläge hinzu!");
        alertDialog.setOnDismissListener(dialog -> GetActivity.goToDisplay(activity,
                FragmentType.List,
                ModelType.RECIPE));
        List<Topic> topics = Topic.getTopics(recipe);
        ArrayList<String> topicsName = new ArrayList<>();
        for(Topic t: topics){
            topicsName.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(
                activity,
                android.R.layout.simple_list_item_1,
                topicsName);
        alertDialog.setAdapter(adapter,
                (dialog, which) -> {
            GetActivity.goToDisplay(activity,
                        FragmentType.Model,
                        ModelType.TOPIC,
                        topics.get(which).getID());
            dialog.dismiss();
        });
        alertDialog.show();
    }







    //Los Button+ Notification
    public static void startPointCocktailMixing(Activity activity, Recipe recipe){


        GetActivity.goToFill(activity, recipe);
    }
    //Fill Simulation

    //Abholen
    public static void getCocktail(Activity activity, Recipe recipe){
        doneMixing(activity);
    }

    public static void doneMixing(Activity activity){
        //pick new
        GetActivity.goToDisplay(activity, FragmentType.List, ModelType.RECIPE);
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

    public static void setPumpVolume(Activity activity, Pump pump, boolean setPumpMinVol){
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
                if(setPumpMinVol){
                    GetDialog.setPumpMinVolume(activity, pump);
                }

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



    //pump ingredients
    public static void setPumpIngredient(Activity activity, Pump pump, boolean setPumpVol, boolean setPumpMinVol){
        if (pump != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze die Zutat:");

            List<Ingredient> ingredients = Ingredient.getAllIngredients();
            ArrayList<String> names = new ArrayList<>();
            for(Ingredient ingredient: ingredients){
                names.add(ingredient.getName());
            }

            ArrayAdapter adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    names);

            builder.setAdapter(adapter,
                    (dialog, which) -> {
                        pump.setCurrentIngredient(ingredients.get(which));
                        Toast.makeText(activity, names.get(which)+" gewählt.",Toast.LENGTH_SHORT).show();
                    });

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                pump.sendSave(activity);
                if(setPumpVol) {
                    GetDialog.setPumpVolume(activity, pump, setPumpMinVol);
                }
            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }












    //Pumpenkalibrierung

    public static void calibratePump(Activity activity, Pump pump){
        if (pump != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze die Kalibrierungenwerte:");

            View v = activity.getLayoutInflater().inflate(R.layout.layout_calibrate_pump, null);
            GetDialog.CalibratePumpChangeView calibratePumpChangeView =
                    new GetDialog.CalibratePumpChangeView(
                            activity,
                            pump,
                            v);

            builder.setView(v);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                calibratePumpChangeView.send();
                Toast.makeText(activity, "Kalibrierung von Pumpe fertig.", Toast.LENGTH_SHORT).show();

            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

    public static class CalibratePumpChangeView{
        private final EditText time1;
        private final EditText time2;
        private final EditText vol1;
        private final EditText vol2;
        private final Pump pump;
        private final View v;
        private final Activity activity;
        private CalibratePumpChangeView(Activity activity, Pump pump, View v) {
            this.activity = activity;
            this.time1 = (EditText) v.findViewById(R.id.editText_time1);
            this.time2 = (EditText) v.findViewById(R.id.editText_time2);
            this.vol1 = (EditText) v.findViewById(R.id.editText_vol1);
            this.vol2 = (EditText) v.findViewById(R.id.editText_vol2);

            this.pump = pump;
            this.v = v;
        }

        private int getTime1(){
            return Integer.getInteger(this.time1.getText().toString());
        }

        private int getTime2(){
            return Integer.getInteger(this.time2.getText().toString());
        }

        private float getVolume1(){
            return Float.valueOf(this.vol1.getText().toString());
        }

        private float getVolume2(){
            return Float.valueOf(this.vol2.getText().toString());
        }

        public void send(){
            pump.sendCalibrate(activity,
                    getTime1(),
                    getTime2(),
                    getVolume1(),
                    getVolume2());
        }
    }


    public static void calibrateAllPumpsAndTimes(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze die Kalibrierungenwerte:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_calibrate_pump, null);
        GetDialog.CalibrateAllPumpChangeView calibrateAllPumpChangeView =
                new GetDialog.CalibrateAllPumpChangeView(
                        activity,
                        v);

        builder.setView(v);
        builder.setPositiveButton("Speichern", (dialog, which) -> {
            calibrateAllPumpChangeView.send();
            Toast.makeText(activity, "Kalibrierung von Pumpe fertig.", Toast.LENGTH_SHORT).show();
            GetDialog.calibrateAllPumpTimes(activity);

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();

    }

    public static void calibrateAllPumps(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze die Kalibrierungenwerte:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_calibrate_pump, null);
        GetDialog.CalibrateAllPumpChangeView calibrateAllPumpChangeView =
                new GetDialog.CalibrateAllPumpChangeView(
                        activity,
                        v);

        builder.setView(v);
        builder.setPositiveButton("Speichern", (dialog, which) -> {
                calibrateAllPumpChangeView.send();
                Toast.makeText(activity, "Kalibrierung von Pumpe fertig.", Toast.LENGTH_SHORT).show();

            });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
        builder.show();

    }

    public static class CalibrateAllPumpChangeView{
        private final EditText time1;
        private final EditText time2;
        private final EditText vol1;
        private final EditText vol2;
        private final View v;
        private final Activity activity;
        private CalibrateAllPumpChangeView(Activity activity, View v) {
            this.activity = activity;
            this.time1 = (EditText) v.findViewById(R.id.editText_time1);
            this.time2 = (EditText) v.findViewById(R.id.editText_time2);
            this.vol1 = (EditText) v.findViewById(R.id.editText_vol1);
            this.vol2 = (EditText) v.findViewById(R.id.editText_vol2);
            this.v = v;
        }

        private int getTime1(){
            try {
                return Integer.getInteger(this.time1.getText().toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
        }

        private int getTime2(){
            try {
            return Integer.getInteger(this.time2.getText().toString());
        }catch (NullPointerException e){
            e.printStackTrace();
            return 0;
        }
        }

        private float getVolume1(){
            try {
            return Float.valueOf(this.vol1.getText().toString());
        }catch (NullPointerException e){
        e.printStackTrace();
        return 0;
    }
        }

        private float getVolume2(){
            try {
            return Float.valueOf(this.vol2.getText().toString());
        }catch (NullPointerException e){
        e.printStackTrace();
        return 0;
        }
        }

        public void send(){
            for(Pump p: Pump.getPumps()) {
                p.sendCalibrate(activity,
                        getTime1(),
                        getTime2(),
                        getVolume1(),
                        getVolume2());
            }
        }
    }





    //Pumpen times
    public static void calibratePumpTimes(Activity activity, Pump pump){
        if (pump != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze die Kalibrierungenwerte:");

            View v = activity.getLayoutInflater().inflate(R.layout.layout_pump_times, null);
            GetDialog.CalibratePumpTimesChangeView calibratePumpTimesChangeView =
                    new GetDialog.CalibratePumpTimesChangeView(
                            activity,
                            pump,
                            v);

            builder.setView(v);

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                calibratePumpTimesChangeView.send();
                Toast.makeText(activity, "Kalibrierung von Pumpzeiten fertig.", Toast.LENGTH_SHORT).show();

            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

    public static class CalibratePumpTimesChangeView{
        private final EditText timeInit;
        private final EditText timeRev;
        private final EditText rate;
        private final Pump pump;
        private final View v;
        private final Activity activity;
        private CalibratePumpTimesChangeView(Activity activity, Pump pump, View v) {
            this.activity = activity;
            this.timeInit = (EditText) v.findViewById(R.id.editText_times_timeInit);
            this.timeRev = (EditText) v.findViewById(R.id.editText_times_timeRev);
            this.rate = (EditText) v.findViewById(R.id.editText_times_rate);

            this.pump = pump;
            this.v = v;
        }

        private int getTimeInit(){
            return Integer.getInteger(this.timeInit.getText().toString());
        }

        private int getTimeRev(){
            return Integer.getInteger(this.timeRev.getText().toString());
        }

        private float getRate(){
            return Float.valueOf(this.rate.getText().toString());
        }

        public void send(){
            pump.sendPumpTimes(
                    activity,
                    getTimeInit(),
                    getTimeRev(),
                    getRate());
        }
    }


    public static void calibrateAllPumpTimes(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze die Kalibrierungenwerte, die für alle Pumpen verwendet werden:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_pump_times, null);
        GetDialog.CalibrateAllPumpTimesChangeView calibrateAllPumpTimesChangeView =
                new GetDialog.CalibrateAllPumpTimesChangeView(
                        activity,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            calibrateAllPumpTimesChangeView.send();
            Toast.makeText(activity, "Kalibrierung von Pumpzeiten fertig.", Toast.LENGTH_SHORT).show();
            });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
        builder.show();

    }

    public static class CalibrateAllPumpTimesChangeView{
        private final EditText timeInit;
        private final EditText timeRev;
        private final EditText rate;
        private final View v;
        private final Activity activity;
        private CalibrateAllPumpTimesChangeView(Activity activity, View v) {
            this.activity = activity;
            this.timeInit = (EditText) v.findViewById(R.id.editText_times_timeInit);
            this.timeRev = (EditText) v.findViewById(R.id.editText_times_timeRev);
            this.rate = (EditText) v.findViewById(R.id.editText_times_rate);
            this.v = v;
        }

        private int getTimeInit(){
            try {

                return Integer.getInteger(this.timeInit.getText().toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
        }

        private int getTimeRev(){
            try {

                return Integer.getInteger(this.timeRev.getText().toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
        }

        private float getRate(){
            try {

                return Float.valueOf(this.rate.getText().toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                return 0;
            }
            //return Float.valueOf(this.rate.getText().toString());
        }

        public void send(){
            for(Pump pump: Pump.getPumps()) {
                pump.sendPumpTimes(
                        activity,
                        getTimeInit(),
                        getTimeRev(),
                        getRate());
            }
        }
    }





    //Alle Pumpenkalibrieren

    public static void setPumpNumber(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze die Anzahl der Pumpen:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.PumpNumberChangeView pumpNumberChangeView =
                new GetDialog.PumpNumberChangeView(
                        activity,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            pumpNumberChangeView.save();
            Pump.calibratePumpsAndTimes(activity);

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }

    public static class PumpNumberChangeView extends FloatChangeView{
        private PumpNumberChangeView(Activity activity, View v) {
            super(activity, v, "Anzahl");
        }
        public void save(){
            DatabaseConnection.initializeSingleton(super.activity);

            Pump.setOverrideEmptyPumps((int) super.getFloat());
        }

        @Override
        public void send() {
        }
    }











    //Calibrate Scale

    /**
     * calibrate scale
     * @author Johanna Reidt
     * @param activity
     */
    public static void calibrateScale(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze jetzt das jetzige Gewicht auf der Waage:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.ScaleChangeView scaleChangeView =
                new GetDialog.ScaleChangeView(
                        activity,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            scaleChangeView.send();

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }

    public static class ScaleChangeView extends FloatChangeView{
        private ScaleChangeView(Activity activity, View v) {
            super(activity, v, "Gewicht");
        }
        public void send(){
            CocktailMachine.sendCalibrateScale(super.activity, super.getFloat() );
        }
    }




    /**
     * calibrate scale
     * @author Johanna Reidt
     * @param activity
     */
    public static void calibrateScaleFactor(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze den Skalierungsfaktor:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.ScaleFactorChangeView scaleFactorChangeView =
                new GetDialog.ScaleFactorChangeView(
                        activity,
                        v);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            scaleFactorChangeView.send();

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }

    public static class ScaleFactorChangeView extends FloatChangeView{
        private ScaleFactorChangeView(Activity activity, View v) {
            super(activity, v, "Faktor");
        }
        public void send(){
            CocktailMachine.sendScaleFactor(super.activity, super.getFloat() );
        }
    }

    public abstract static class FloatChangeView{

        private final TextView t;
        private final EditText e;
        private final View v;
        final Activity activity;
        private FloatChangeView(Activity activity, View v, String title ) {
            this.activity = activity;
            this.t = (TextView) v.findViewById(R.id.textView_edit_text);
            this.e = (EditText) v.findViewById(R.id.editText_edit_text);

            this.t.setText(title+": ");
            this.e.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            this.v = v;
            String name = getPreFloat();
            this.e.setHint(name);
            //this.e.setText(name);
        }

        private String getPreFloat(){
            return String.valueOf("9738");
        }
        public float getFloat(){
            return Float.valueOf(e.getText().toString());
        }

        public abstract void send();

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







    //Pick a ingredient for pump
    public static void chooseIngredient(Activity activity, Pump pump){
        List<Ingredient> ingredients= Ingredient.getAllIngredients();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Änderung");

        builder.setMessage("Wähle eine Zutat!");

        ArrayList<String> displayValues=new ArrayList<>();
        for (Ingredient entity : ingredients) {
            displayValues.add(entity.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity.getApplicationContext(),
                android.R.layout.simple_list_item_1,
                displayValues);
        builder.setSingleChoiceItems(
                adapter,
                0,
                (dialog, which) -> {
            pump.setCurrentIngredient(ingredients.get(which));
            Toast.makeText(activity,"Gewählte Zutat: "+displayValues.get(which),Toast.LENGTH_SHORT).show();
        });



        builder.setPositiveButton("Speichern", (dialog, which) -> {
            pump.save();
            pump.sendSave(activity);
            Toast.makeText(activity,"Cocktailmaschine wird informiert.",Toast.LENGTH_SHORT).show();
            DatabaseConnection.localRefresh();
            Toast.makeText(activity,"DB-Synchronisation läuft!",Toast.LENGTH_SHORT).show();

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
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
