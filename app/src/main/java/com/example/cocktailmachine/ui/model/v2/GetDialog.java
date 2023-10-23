package com.example.cocktailmachine.ui.model.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.Topic;
import com.example.cocktailmachine.data.db.exceptions.MissingIngredientPumpException;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.CalibrateStatus;
import com.example.cocktailmachine.data.enums.ErrorStatus;
import com.example.cocktailmachine.data.enums.Postexecute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Johanna Reidt
 * @created Di. 27.Jun 2023 - 15:51
 * @project CocktailMachine
 */
public class GetDialog {

    private static final String TAG = "GetDialog";

    //ERROR
    public static void errorMessage(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("FEHLER");
        builder.setMessage("Es ist ein Fehler aufgetreten. Wir empfehlen die Daten zu aktualisieren.");

        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.setPositiveButton("Aktualisieren", (dialog, which) -> {
            Pump.sync(activity);
            Recipe.syncRecipeDBWithCocktailmachine(activity);
            Toast.makeText(activity,"Synchronisierung läuft!",Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }













    //Recipe Send
    public static void sendRecipe(Activity activity, Recipe recipe){

        Postexecute doAgain = new Postexecute() {
            @Override
            public void post() {
                CocktailMachine.queueRecipe(recipe, activity);
            }
        };
        Postexecute continueHere = new Postexecute() {
            @Override
            public void post() {
                GetDialog.countDown(activity, recipe);
            }
        };
        Postexecute notFound = new Postexecute(){

            @Override
            public void post() {
                recipe.sendSave(activity);
                CocktailMachine.queueRecipe(recipe, activity);
            }
        };

        HashMap<ErrorStatus, Postexecute> errorHandle=new HashMap<>();
        errorHandle.put(ErrorStatus.recipe_not_found, notFound);
        errorHandle.put(ErrorStatus.cant_start_recipe_yet, doAgain);
        ErrorStatus.handleSpecificErrorMethod(activity, doAgain, continueHere, errorHandle);

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

    /**
     * Let the user go to the Cocktailmachine.
     * @author Johanna Reidt
     * @param activity
     * @param recipe
     */
    public static void isUsersTurn(Activity activity, Recipe recipe){
        //Better solution
        //https://stackoverflow.com/questions/10780651/display-a-countdown-timer-in-the-alert-dialog-box
        //CountDownRun countDownThread = new CountDownRun();
        //new Thread(countDownThread).start();
        //countDown(activity, recipe, countDownThread);
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Du bist dran!");
        alertDialog.setMessage("Bitte, geh zur Cocktailmaschine und stelle dein Glas unter die Maschine. ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Los!", (dialog, which) -> {
            //TO DO: send force start bluetooth thing

            Postexecute doAgain = new Postexecute() {
                @Override
                public void post() {
                    CocktailMachine.startMixing(activity);
                }
            };
            Postexecute continueHere = new Postexecute() {
                @Override
                public void post() {
                    GetActivity.goToFill(activity, recipe);
                }
            };
            ErrorStatus.handleSpecificErrorRepeat(activity, dialog, ErrorStatus.cant_start_recipe_yet, doAgain, continueHere);

        });
        alertDialog.show();   //
    }

    /**
     * Let user get the mixed Cocktail.
     * @author Johanna Reidt
     * @param activity
     * @param recipe
     */
    public static void isDone(Activity activity, Recipe recipe){
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Fertig!");
        alertDialog.setMessage("Hole deinen Cocktail ab! ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Abgeholt!", (dialog, which) -> {
            //BluetoothSingleton.getInstance().adminReset();
            //CocktailMachine.isCollected();

            //GetActivity.goToMenu(activity);
            Postexecute doAgain = new Postexecute() {
                @Override
                public void post() {
                    CocktailMachine.takeCocktail(activity);
                }
            };
            Postexecute continueHere = new Postexecute() {
                @Override
                public void post() {
                    GetDialog.showTopics( activity,  recipe);
                    CocktailMachine.setCurrentRecipe(null);
                }
            };
            ErrorStatus.handleSpecificErrorRepeat(activity, dialog, ErrorStatus.cant_take_cocktail_yet, doAgain, continueHere);
        });
        alertDialog.show();   //

    }

    /**
     * show topics if there are any
     * @author Johanna Reidt
     * @param activity
     * @param recipe
     */
    public static void showTopics(Activity activity, Recipe recipe){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Serviervorschläge!");
        alertDialog.setMessage("Füge noch einen oder mehrer der Serviervorschläge hinzu!");
        alertDialog.setOnDismissListener(dialog -> {
                GetActivity.goToMenu(activity);
        });
        alertDialog.setOnCancelListener(dialog -> {
            GetActivity.goToMenu(activity);
        });
        alertDialog.setCancelable(true);
        List<Topic> topics = Topic.getTopics(recipe);
        if(topics.size()==0){
            GetActivity.goToMenu(activity);
            return;
        }
        ArrayList<String> topicsName = new ArrayList<>();
        for(Topic t: topics){
            topicsName.add(t.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_list_item_1,
                topicsName);
        alertDialog.setAdapter(adapter,
                (dialog, which) -> {
            dialog.dismiss();
            GetActivity.goToLook(activity,
                        ModelType.TOPIC,
                        topics.get(which).getID());
        });
        alertDialog.show();
    }

















    //Automatic calibration
    /**
     * a.	Bitte erst wasser beim ersten Durchgang
     * b.	Tarierung
     * c.	Gefässe mit 100 ml Wasser
     * d.	Pumpenanzahl
     * e.	Automatische Kalibrierung
     * f.	Warten auf fertig
     * g.	Angabe von Zutaten
     */
    public static void startAutomaticCalibration(Activity activity){
        Log.v(TAG, "startAutomaticCalibration");
        //CocktailMachine.automaticCalibration();
        enterNumberOfPumps(activity);
        //ErrorStatus.handleAutoCalNotReadyStart(activity, dialog);
    }


    private static void enterNumberOfPumps(Activity activity){
        Log.v(TAG, "enterNumberOfPumps");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Setze die Anzahl der Pumpen:");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.PumpNumberChangeView pumpNumberChangeView =
                new GetDialog.PumpNumberChangeView(
                        activity,
                        v);

        builder.setView(v);
        builder.setPositiveButton("Speichern", (dialog, which) -> {
            try {
                pumpNumberChangeView.save(); //set up n new Pumps
                //pumpNumberChangeView.send();
                //dialog.dismiss();
                //getGlass(activity);
                firstAutomaticDialog(activity);
            }catch (IllegalStateException e){
                Log.e(TAG, "enterNumberOfPumps pumpNumberChangeView save error");
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        });
        builder.show();
    }

    public static void firstAutomaticDialog(Activity activity){
        //DatabaseConnection.initializeSingleton(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Automatische Kalibrierung");
        builder.setMessage("Bitte folge den Anweisungen schrittweise. " +
                "Zur Kalibrierung der Pumpen darf zunächst nur Wasser angeschlossen sein. " +
                "Bitte stelle sicher, dass an allen Pumpen nur Wassergefässe angeschlossen sind." +
                "Die Wassermmenge je Pumpe sollte um die 150ml betragen.");
        builder.setPositiveButton("Erledigt!", (dialog, which) -> {
            Postexecute doAgain = new Postexecute() {
                @Override
                public void post() {
                    Log.v(TAG, "firstAutomaticDialog: automaticCalibration");
                    CocktailMachine.automaticCalibration(activity);
                }
            };
            Postexecute continueHere = new Postexecute(){

                @Override
                public void post() {
                    Log.v(TAG, "firstAutomaticDialog: firstTaring");
                    firstTaring(activity);
                }
            };
            ErrorStatus.handleAutomaticCalibrationNotReady(activity, dialog, doAgain, continueHere);
            //firstTaring(activity);
            //dialog.dismiss();
        });
        builder.show();
    }

    private static void firstTaring(Activity activity){
        Log.v(TAG, "firstTaring");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Waagentarierung");
        builder.setMessage("Bitte stelle sicher, dass keine Gefässe, Gewichte oder Ähnliches unter der Cocktailmaschine steht. " +
                "Gemeint ist der Bereich an dem später die Gläser stehen." +
                "Auch das Auffangbecken muss leer sein!");
        builder.setPositiveButton("Erledigt!", (dialog, which) -> {
            //CocktailMachine.tareScale(activity);


            //CocktailMachine.automaticEmpty();
            //TODO: TARIERUNG pflicht????

            //CocktailMachine.automaticEmpty(activity);
            //enterNumberOfPumps(activity);
            CocktailMachine.tareScale(activity);
            dialog.dismiss();
            //enterNumberOfPumps(activity);
            getGlass(activity);
        });
        builder.show();
    }




    private static void getGlass(Activity activity){
        Log.v("GetDialog", "getGlass");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Waagenkalibrierung");
        builder.setMessage("Bitte stelle ein Gefäss mit 100ml Flüssigkeit unter die Cocktailmaschine. ");
        builder.setPositiveButton("Erledigt!", (dialog, which) -> {
            Postexecute doAgain = new Postexecute() {
                @Override
                public void post() {
                    CocktailMachine.automaticWeight(activity);
                }
            };
            Postexecute continueHere = new Postexecute() {
                @Override
                public void post() {
                    emptyGlass(activity);
                }
            };
            ErrorStatus.handleAutomaticCalibrationNotReady(activity, dialog, doAgain, continueHere);
            //CocktailMachine.automaticCalibration(activity);
            //CocktailMachine.automaticWeight(activity);
            //waitingForPumps(activity);
        });
        builder.show();
    }

    private static void emptyGlass(Activity activity){
        Log.v("GetDialog", "emptyGlass");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Leere das Glass!");
        builder.setMessage("Leere das Glass und stell es wieder unter die Cocktailmaschine!");
        builder.setPositiveButton("Erledigt!", (dialog, which) -> {

            Postexecute doAgain = new Postexecute() {
                @Override
                public void post() {
                    CocktailMachine.automaticEmptyPumping(activity);
                }
            };
            Postexecute continueHere = new Postexecute() {
                @Override
                public void post() {
                    GetDialog.waitingForPumps(activity);
                }
            };
            ErrorStatus.handleAutomaticCalibrationNotReady(activity, dialog, doAgain, continueHere);

            //GetDialog.waitingForPumps(activity);
            //CocktailMachine.automaticEmptyPumping(activity);
        });
        builder.show();
    }

    private static void waitingForPumps(Activity activity){
        Log.v("GetDialog", "waitingForPumps");
        //setIngredientsForPumps(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bitte Warten!");
        builder.setMessage("Die automatische Kalibration der Pumpen läuft!");
        AlertDialog dialog = builder.create();
        dialog.show();


        WaitingQueueCountDown waitingQueueCountDown = new WaitingQueueCountDown(5000) {
            boolean isDone = false;
            @Override
            public void onTick() {
                Log.v("GetDialog", "waitingQueueCountDown:  isAutomaticCalibrationDone false");
                if(CalibrateStatus.getCurrent(activity)==CalibrateStatus.calibration_calculation){
                    dialog.setMessage("Die Pumpeneinstellungen werden kalkuliert! Bitte warten!");
                }
            }

            @Override
            public void reduceTick() {
                CocktailMachine.tickDummy(activity);
                isDone = CocktailMachine.isAutomaticCalibrationDone(activity)||CocktailMachine.needsEmptyingGlass(activity);
                /*
                if(Dummy.isDummy){
                    isDone = CocktailMachine.dummyCounter>=Pump.getPumps().size();
                }

                 */
                Log.v("GetDialog", "waitingQueueCountDown:  isDone: " +isDone);
                if(isDone){
                    setTick(0);
                }else {
                    setTick(1);
                }
            }

            @Override
            public void onNext() {

            }

            @Override
            public void onFinish() {
                Log.v("GetDialog", "waitingQueueCountDown: onFinish");
                this.cancel();
                //this.stop();
                Log.v("GetDialog", "waitingQueueCountDown: onFinish: this canceled");
                //ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,150);
                //toneGen1.release();
                dialog.dismiss();

                Log.v("GetDialog", "waitingQueueCountDown: onFinish: dialog dimissed");
                if(CocktailMachine.isAutomaticCalibrationDone(activity)) {
                    Toast.makeText(activity, "Das Setup ist vollständig!", Toast.LENGTH_LONG).show();
                    Postexecute doAgain = new Postexecute() {
                        @Override
                        public void post() {
                            CocktailMachine.automaticEnd(activity);

                        }
                    };
                    Postexecute continueHere = new Postexecute() {
                        @Override
                        public void post() {
                            new DialogListOfPumps(activity);
                            //GetDialog.setIngredientsForPumps(activity);
                        }
                    };
                    ErrorStatus.handleAutomaticCalibrationNotReady(activity, dialog, doAgain, continueHere);
                    //CocktailMachine.automaticEnd(activity);
                    //GetDialog.setIngredientsForPumps(activity);
                } else if (CocktailMachine.needsEmptyingGlass(activity)) {

                    GetDialog.emptyGlass(activity);
                }
                this.cancel();
            }
        };
        waitingQueueCountDown.start();
    }


    /**

    private static void waitingAutomaticCalibration(Activity activity){
        //TO DO
        Log.v("GetDialog", "waitingAutomaticCalibration");
        //setIngredientsForPumps(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bitte Warten!");
        builder.setMessage("Die automatische Kalibration der Pumpen läuft!");
        AlertDialog dialog = builder.create();
        dialog.show();

        WaitingQueueCountDown waitingQueueCountDown = new WaitingQueueCountDown(5000) {
            boolean isDone = false;
            @Override
            public void onTick() {
                Log.v("GetDialog", "waitingQueueCountDown:  isAutomaticCalibrationDone false");
            }

            @Override
            public void reduceTick() {
                isDone = CocktailMachine.isAutomaticCalibrationDone(activity);
                Log.v("GetDialog", "waitingQueueCountDown:  isDone: " +isDone);
                if(isDone){
                    setTick(0);
                }else {
                    setTick(1);
                }
            }

            @Override
            public void onNext() {

            }

            @Override
            public void onFinish() {
                Log.v("GetDialog", "waitingQueueCountDown: onFinish");
                this.cancel();
                //this.stop();
                Log.v("GetDialog", "waitingQueueCountDown: onFinish: this canceled");
                //ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,150);
                //toneGen1.release();
                Toast.makeText(activity, "Das Setup ist vollständig!", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                Log.v("GetDialog", "waitingQueueCountDown: onFinish: dialog dimissed");
                GetDialog.setIngredientsForPumps(activity);
                this.cancel();
            }
        };
        waitingQueueCountDown.start();


    }
            **/

    private static void setIngredientsForPumps(Activity activity){
        //TO DO

        Log.v("GetDialog", "setIngredientsForPumps");
        List<Pump> pumps = Pump.getPumps();
        Log.v(TAG, "setIngredientsForPumps: pumps len "+pumps.size());
        Log.v(TAG, "setIngredientsForPumps"+pumps);

        int position = 0;
        setFixedPumpIngredient(activity, pumps, position);

    }

    private static void setFixedPumpIngredient(Activity activity, List<Pump> pumps, int position){
        Log.v(TAG, "setFixedPumpIngredient");
        //Log.v(TAG, "setFixedPumpIngredient: next len ");
        if(position >= pumps.size()){
            GetActivity.goToMenu(activity);
        }
        Pump pump = pumps.get(position);
        if (pump != null) {
            Log.v(TAG, "setFixedPumpIngredient Slot "+pump.getSlot());
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze die Zutat für Slot "+pump.getSlot()+":");

            List<Ingredient> ingredients = Ingredient.getAllIngredients();
            ArrayList<String> names = new ArrayList<>();
            for(Ingredient ingredient: ingredients){
                names.add(ingredient.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    names);

            builder.setAdapter(adapter,
                    (dialog, which) -> {
                        pump.setCurrentIngredient(activity, ingredients.get(which));
                        Toast.makeText(activity, names.get(which)+" gewählt.",Toast.LENGTH_SHORT).show();
                        pump.sendSave(activity);
                        setFixedPumpVolume(activity, pumps, position);
                    });
            /*
            builder.setPositiveButton("Speichern", (dialog, which) -> {
                Log.v(TAG, "setFixedPumpIngredient: ingredient "+pump.getIngredientName());
                //dialog.dismiss();
                pump.sendSave(activity);
                //Log.v(TAG, "setFixedPumpIngredient: ingredient "+pump.getIngredientName());
                setFixedPumpVolume(activity, pumps, position);

            });

            */
            builder.show();
        }else{
            Log.v(TAG, "setFixedPumpIngredient: pump is null");
            errorMessage(activity);
        }
    }

    private static void setFixedPumpVolume(Activity activity, List<Pump> pumps, int position){
        Log.v(TAG, "setFixedPumpVolume");
        Pump pump = pumps.get(position);
        if (pump != null) {
            pump.sendRefill(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze das jetzt vorhandene Volumen für Slot "+pump.getSlot()+":");

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
                //dialog.dismiss();
                //setFixedPumpMinVolume(activity, pump, next);
                if(position+1 == pumps.size()){
                    GetActivity.goToMenu(activity);

                }else {
                    new DialogListOfPumps(activity);
                    //setFixedPumpIngredient(activity,pumps, position+1);
                }
            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

    public static void setFixedPumpVolume(Activity activity, Pump pump){
        Log.v(TAG, "setFixedPumpVolume");
        if (pump != null) {
            pump.sendRefill(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze das jetzt vorhandene Volumen für Pumpe "+pump.getSlot()+":");

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
                if(allPumpsConfigured(activity)){
                    GetActivity.goToMenu(activity);

                }else {
                    new DialogListOfPumps(activity);
                    //setFixedPumpIngredient(activity,pumps, position+1);
                }
            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

    private static boolean allPumpsConfigured(Activity activity){
        List<Pump> pumps = Pump.getPumps(activity);
        for (Pump pump : pumps){
            if(pump.getVolume()<=0 || pump.getIngredientName()==""){
                return false;
            }
        }
        return true;
    }


    /*
    private static void setFixedPumpMinVolume(Activity activity,  List<Pump> pumps, int position){
        Log.v(TAG, "setFixedPumpMinVolume");
        Pump pump = pumps.get(position);
        if (pump != null) {
            pump.sendRefill(activity);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze das Minimalvolumen für Slot "+pump.getSlot()+":");

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
                //dialog.dismiss();
                if(position == pumps.size()){
                    GetActivity.goToMenu(activity);
                }else {
                    setFixedPumpIngredient(activity,pumps, position);
                }
            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

     */









    static void deleteElement(Activity activity, String name, Postexecute pickedDeleted){
        Log.v(TAG, "deleteElement");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Löschen");
        builder.setMessage("Möchtest du wirklich "+name+" löschen?");
        builder.setNegativeButton("Nein", (dialog, which) -> {
            Log.v(TAG, "deleteAddElement: Nein");
            dialog.dismiss();
        });
        builder.setCancelable(false);
        builder.setPositiveButton("Ja", (dialog, which) -> {
            Log.v(TAG, "deleteAddElement: Ja");
            Log.v(TAG, "deleteAddElement: pickedDeleted post");
            pickedDeleted.post();
            Log.v(TAG, "deleteAddElement: pickedDeleted post done");
            dialog.dismiss();
        });
        builder.show();
    }


    static void deleteAddElement(Activity activity, String name, Postexecute pickedDeleted){
        Log.v(TAG, "deleteAddElement");
        deleteElement(activity, name+" aus dem Rezept", pickedDeleted );
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
            this.t = v.findViewById(R.id.textView_edit_text);
            this.e = v.findViewById(R.id.editText_edit_text);
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
        public void save(){
            switch(modelType){
                case INGREDIENT:
                    Ingredient ingredient = Ingredient.getIngredient(ID);
                    if(ingredient==null){
                        ingredient = Ingredient.makeNew(getName());
                    }else {
                        ingredient.setName(getName());
                    }
                    ingredient.save(activity);
                    return;
                case RECIPE:
                    Recipe recipe = Recipe.getRecipe(ID);
                    if(recipe==null){
                        recipe = Recipe.makeNew(getName());
                    }else {
                        recipe.setName(activity,getName());
                    }
                    recipe.save(activity);
                    return;
                case TOPIC:
                    Topic topic = Topic.getTopic(ID);
                    if(topic==null){
                        topic = Topic.makeNew(getName(), "");
                    }else {
                        topic.setName(getName());
                    }
                    topic.save(activity);
                    return;
            }
            return;
        }

        public void send(){
            if(ModelType.RECIPE == modelType) {
                Objects.requireNonNull(Recipe.getRecipe(this.ID)).send(activity);
            }
        }
    }














    //Get Ingredient + vol

    /*
    public static void getIngredientVolume(Activity activity, boolean available, IngredientVolumeSaver saver){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Wähle die Zutat und gib das Volumen an!");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_search_ingredient, null);
        IngredientVolumeView iv = new IngredientVolumeView(activity, v, available);
        builder.setView(v);
        builder.setPositiveButton("Speichern", (dialog, which) -> {
            saver.save(
                    iv.getIngredient(),
                    iv.getIngredientTippedName(),
                    iv.getVolume());
        });
        builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

     */
    interface IngredientVolumeSaver{
        //void save(Ingredient ingredient,String tippedName, Integer volume);
        void save(Ingredient ingredient, String tippedName);
        void save(Integer volume);
        void post();
    }
    /*
    private static class IngredientVolumeView{
        private final TextView ingredient;
        private final EditText search;
        private final ImageButton done;
        private final EditText volume;
        private final Activity activity;
        private final View view;
        private final List<String> ingredientNames;

        private IngredientVolumeView(Activity activity,
                                     View view,
                                     boolean only_available){
            this.activity = activity;
            this.view = view;
            this.search = activity.findViewById(R.id.editText_search_ingredient_ing);
            this.done = activity.findViewById(R.id.imageButton_search_ingredient_done);
            this.ingredient = activity.findViewById(R.id.textView_search_ingredient_ing);
            this.volume = activity.findViewById(R.id.editTextNumber_search_ingredient_vol);

            if(only_available) {
                this.ingredientNames = Ingredient.getAvailableIngredientNames();
            }else{
                this.ingredientNames = Ingredient.getAllIngredientNames();
            }
            setSearchItems();
            setSearch();
        }

        Ingredient getIngredient(){
            return Ingredient.searchOrNew(search.getText().toString());
        }

        String getIngredientTippedName(){
            return search.getText().toString();
        }

        int getVolume(){
            Log.v(TAG, "IngredientVolumeView: getVolume ");
            try {
                return Integer.parseInt(volume.getText().toString());
            }catch (NumberFormatException e){
                Log.e(TAG, "IngredientVolumeView: getVolume error");
                Log.e(TAG, "IngredientVolumeView: getVolume "+e.getMessage());
                e.printStackTrace();
            }
            return -1;
        }

        private void done(){
            Log.v(TAG, "IngredientVolumeView: done ");
            this.search.setVisibility(View.GONE);
            this.done.setVisibility(View.GONE);
            this.ingredient.setVisibility(View.VISIBLE);
            this.ingredient.setText(this.getIngredient().getName());
            this.ingredient.setOnClickListener(v -> setSearch());
        }

        private void setSearch(){
            Log.v(TAG, "IngredientVolumeView: setSearch ");
            this.search.setVisibility(View.VISIBLE);
            this.done.setVisibility(View.VISIBLE);
            this.ingredient.setVisibility(View.GONE);
            this.done.setOnClickListener(v -> done());

        }

        private void setSearchItems(){
            Log.v(TAG, "IngredientVolumeView: setSearchItems ");
            //this.search.setAutofillHints(this.ingredientNames);
            //TODO: set autotfill hints
        }

    }

    */



    public static void getIngVol(Activity activity, boolean available, IngredientVolumeSaver saver){
        //TODOsetTitle
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bitte wähle die Zutat!");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.GetIngView getIngView =
                new GetDialog.GetIngView(
                        activity,
                        v,
                        available,
                        saver);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            Log.v(TAG, "getIngVol : save" );
            getIngView.save();
            //getIngView.send();
            getIngVolVol(activity, saver);
            dialog.dismiss();
        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            Log.v(TAG, "getIngVol : stop" );
        });
        builder.show();
    }


    public static class GetIngView{

        private final TextView t;
        private final EditText e;
        private final boolean available;
        private final View v;
        private final IngredientVolumeSaver saver;
        private final Activity activity;
        private GetIngView(Activity activity, View v, boolean available,
                           IngredientVolumeSaver saver) {
            this.activity = activity;
            this.v = v;
            this.available = available;
            this.saver = saver;
            this.t = v.findViewById(R.id.textView_edit_text);
            this.e = v.findViewById(R.id.editText_edit_text);
            this.e.setHint("Wodka");
            this.t.setText("Zutat: ");
            this.e.setInputType(InputType.TYPE_CLASS_TEXT);

        }

        private String getName(){

            Log.v(TAG, "GetIngView: getName " );
            return e.getText().toString();
        }
        public void save(){
            Log.v(TAG, "GetIngView: save " );
            saver.save(Ingredient.searchOrNew(activity, getName()), getName());
        }

    }

    //public static void
    private static void getIngVolVol(Activity activity, IngredientVolumeSaver saver){
        //TO DO setTitle
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Gib das Volumen an!");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.GetIngVolView getIngVolView =
                new GetDialog.GetIngVolView(
                        activity,
                        v,
                        saver);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            Log.v(TAG, "getIngVolVol : save" );
            getIngVolView.save();
            //getIngView.send();
            saver.post();
            dialog.dismiss();
        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {
            Log.v(TAG, "getIngVolVol : stop" );

        });
        builder.show();
    }


    public static class GetIngVolView{

        private final TextView t;
        private final EditText e;
        private final View v;
        private final IngredientVolumeSaver saver;
        private final Activity activity;
        private GetIngVolView(Activity activity, View v,
                           IngredientVolumeSaver saver) {
            this.activity = activity;
            this.v = v;
            this.saver = saver;
            this.t = v.findViewById(R.id.textView_edit_text);
            this.e = v.findViewById(R.id.editText_edit_text);
            this.e.setHint("123 ml");
            this.t.setText("Volumen: ");
            this.e.setInputType(InputType.TYPE_CLASS_NUMBER);

        }

        private int getVol(){
            Log.v(TAG, "GetIngView: getVol " );
            try {
                return Integer.parseInt(e.getText().toString());
            }catch (NumberFormatException e){
                Log.v(TAG, "GetIngView: parse error " );
                e.printStackTrace();
            }
            return -1;
        }
        public void save(){
            Log.v(TAG, "GetIngView: save " );
            saver.save(getVol());
        }

    }









    //Get Topic
    public static void addTopic(Activity activity, TopicSaver topicSaver){
        Log.v(TAG, "addTopic");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Wähle einen Serviervorschlag!");
        String[] names = Topic.getTopicTitles().toArray(new String[0]);
        builder.setItems(names, (dialog, which) -> {
            Log.v(TAG, "addTopic: picked "+names[which]);

            //recipe.addOrUpdate(Topic.getTopic(names[which]));
            topicSaver.save(Topic.getTopic(names[which]), dialog);
        });
        builder.setNegativeButton("Abbrechen", (dialog, which) -> {
            Log.v(TAG, "addTopic: stop, none picked");
            dialog.dismiss();});
        builder.show();

    }

    interface TopicSaver{
        void save(Topic topic, DialogInterface dialogInterface);
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

            });
            builder.setNeutralButton("Abbrechen", (dialog, which) -> {

            });
            builder.show();
        }else{
            errorMessage(activity);
        }
    }

/*
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

 */


    public static class VolumeChangeView{

        private final TextView t;
        private final EditText e;
        private final Pump pump;
        private final View v;
        private final Activity activity;
        private VolumeChangeView(Activity activity, Pump pump, View v, boolean minimum) {
            this.activity = activity;
            this.t = v.findViewById(R.id.textView_edit_text);
            this.e = v.findViewById(R.id.editText_edit_text);
            this.t.setText("Volumen: ");
            this.e.setInputType(InputType.TYPE_CLASS_NUMBER);
            this.pump = pump;
            this.v = v;
            String name = getVolumeFromDB();
            this.e.setHint(name +" ml");
            this.e.setText(name);
        }

        private String getVolumeFromDB(){
            return String.valueOf(this.pump.getVolume());
        }
        private int getVolume(){
            try {
                int i = Integer.parseInt(e.getText().toString());
                if(i>0){
                    return i;
                }
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            Toast.makeText(this.activity, "Gib eine positive Zahl an!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        public void save(){
            Log.v(TAG, "save");
            try {
                pump.fill(this.activity, getVolume());
            } catch (MissingIngredientPumpException ex) {
                ex.printStackTrace();
            }
            pump.save(activity);
        }

        public void send(){
            Log.v(TAG, "send");
            pump.sendRefill(activity, getVolume());
        }
    }



    //pump ingredients
    public static void setPumpIngredient(Activity activity, Pump pump, boolean setPumpVol){//, boolean setPumpMinVol){
        if (pump != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Setze die Zutat für Slot"+pump.getSlot()+":");

            List<Ingredient> ingredients = Ingredient.getAllIngredients();
            ArrayList<String> names = new ArrayList<>();
            for(Ingredient ingredient: ingredients){
                names.add(ingredient.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    names);

            builder.setAdapter(adapter,
                    (dialog, which) -> {
                        pump.setCurrentIngredient(activity,ingredients.get(which));
                        Toast.makeText(activity, names.get(which)+" gewählt.",Toast.LENGTH_SHORT).show();
                    });

            builder.setPositiveButton("Speichern", (dialog, which) -> {
                pump.sendSave(activity);
                /*
                if(setPumpVol) {
                    GetDialog.setPumpVolume(activity, pump)//, setPumpMinVol);
                }

                 */
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
            this.time1 = v.findViewById(R.id.editText_time1);
            this.time2 = v.findViewById(R.id.editText_time2);
            this.vol1 = v.findViewById(R.id.editText_vol1);
            this.vol2 = v.findViewById(R.id.editText_vol2);

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
            this.time1 = v.findViewById(R.id.editText_time1);
            this.time2 = v.findViewById(R.id.editText_time2);
            this.vol1 = v.findViewById(R.id.editText_vol1);
            this.vol2 = v.findViewById(R.id.editText_vol2);
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
            this.timeInit = v.findViewById(R.id.editText_times_timeInit);
            this.timeRev = v.findViewById(R.id.editText_times_timeRev);
            this.rate = v.findViewById(R.id.editText_times_rate);

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
            this.timeInit = v.findViewById(R.id.editText_times_timeInit);
            this.timeRev = v.findViewById(R.id.editText_times_timeRev);
            this.rate = v.findViewById(R.id.editText_times_rate);
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











    public static void runPump(Activity activity, Pump pump) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Für wie lange soll die Pumpe laufen?");

        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        GetDialog.TimePumpingView pumpNumberChangeView =
                new GetDialog.TimePumpingView(
                        activity,
                        v, pump);

        builder.setView(v);

        builder.setPositiveButton("Speichern", (dialog, which) -> {
            try {

                pumpNumberChangeView.send();
                dialog.dismiss();
            }catch (IllegalStateException e){
                Log.e(TAG, "setPumpNumber pumpNumberChangeView save error");
                Log.e(TAG, "error"+e);
                e.printStackTrace();
            }

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }

    private static class TimePumpingView extends FloatChangeView{
        private final Pump pump;
        private TimePumpingView(Activity activity, View v, Pump pump) {
            super(activity, v, "Zeit");
            this.pump = pump;
        }

        @Override
        public void send() {
            int t = getInt();
            if(t>0) {
                pump.run(activity, getInt());
                return;
            }
            Toast.makeText(activity, "Die Pumpe läuft nicht wegen fehlerhafter Eingabe.", Toast.LENGTH_SHORT).show();
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
            try {
                pumpNumberChangeView.save();
                //Pump.calibratePumpsAndTimes(activity);
                dialog.dismiss();
                GetDialog.startAutomaticCalibration(activity);
            }catch (IllegalStateException e){
                Log.e(TAG, "setPumpNumber pumpNumberChangeView save error");
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }

        });
        builder.setNeutralButton("Abbrechen", (dialog, which) -> {

        });
        builder.show();
    }

    public static class PumpNumberChangeView extends FloatChangeView{
        private PumpNumberChangeView(Activity activity, View v) {
            super(activity, v, "Anzahl");
        }
        public void save() throws IllegalStateException{
            Log.v(TAG, "PumpNumberChangeView: save");
            int res = super.getInt();
            if(res == -1){
                Log.e(TAG, "PumpNumberChangeView: save res == -1 keine valide zahle");
                Toast.makeText(super.activity, "Gib bitte eine valide Zahle ein.", Toast.LENGTH_SHORT).show();
                throw new IllegalStateException("Missing number!");
            } else {
                Log.e(TAG, "PumpNumberChangeView: save setOverrideEmptyPumps");
                Pump.setOverrideEmptyPumps(activity,res);
            }
        }

        @Override
        public void send() {
            for(Pump p: Pump.getPumps()){
                p.sendSave(activity);
            }
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

        private final EditText e;
        final Activity activity;
        private FloatChangeView(Activity activity, View v, String title ) {
            this.activity = activity;
            TextView t = v.findViewById(R.id.textView_edit_text);
            this.e = v.findViewById(R.id.editText_edit_text);

            t.setText(title+": ");
            this.e.setInputType(InputType.TYPE_CLASS_NUMBER);
            String name = getPreFloat();
            this.e.setHint(name);
            //this.e.setText(name);
        }

        private String getPreFloat(){
            return "2";
        }
        public float getFloat(){
            try {
                return Float.parseFloat(e.getText().toString());
            }catch (NumberFormatException e){
                Log.e(TAG, "FloatChangeView getFloat error");
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            return -1;
        }
        public int getInt(){
            try {
                return Integer.parseInt(e.getText().toString());
            }catch (NumberFormatException e){
                Log.e(TAG, "FloatChangeView getInt error");
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            Toast.makeText(activity, "Es wurde keine valide Zahl eingefügt.", Toast.LENGTH_SHORT).show();
            return -1;
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
            ingredient.save(activity);

        });
        builder.setNegativeButton("Nein", (dialog, which) -> {
            ingredient.setAlcoholic(false);
            ingredient.save(activity);

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
            longEditChangeView.save(activity);
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
            this.t = v.findViewById(R.id.textView_long_edit_title);
            this.e = v.findViewById(R.id.editText_long_edit_txt);

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
        public void save(Activity ac){
            this.topic.setDescription(getDescription());
            topic.save(ac);
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
            pump.setCurrentIngredient(activity,ingredients.get(which));
            Toast.makeText(activity,"Gewählte Zutat: "+displayValues.get(which),Toast.LENGTH_SHORT).show();
        });



        builder.setPositiveButton("Speichern", (dialog, which) -> {
            pump.save(activity);
            pump.sendSave(activity);
            Toast.makeText(activity,"Cocktailmaschine wird informiert.",Toast.LENGTH_SHORT).show();
            //DatabaseConnection.localRefresh();
            Buffer.localRefresh(activity);
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
            Buffer.deleteElementFromRecipe(activity,
                    recipe,
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
                Buffer.deleteElement(activity,modelType, ID);
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
     *
     * @author Johanna Reidt
     * @param activity
     */
    public static void showWeight(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Das Gewicht");
        builder.setMessage("Es lädt...");
        CocktailMachine.getCurrentWeight(activity, new Postexecute() {
            @Override
            public void post() {
                builder.setMessage(
                        "Das derzeitige Gewicht auf der Waage beträgt: " +
                                CocktailMachine.getCurrentWeight());
            }
        });
        builder.setNeutralButton("Schließen", (dialog, which) -> {});
        builder.show();
    }

























    //ErrorHandling
    public static void handleCmdError(Activity activity, ErrorStatus status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Command Error");
        builder.setMessage("ESP gab folgende ErrorMessage zurück: "+status+
                " Bitte zeig diese Nachricht dem Adminstratoren-Team!");
        builder.setNeutralButton("Gezeigt!", (dialog, which) -> {});
        builder.show();
    }


    public static void handleBluetoothFailed(Activity activity) {
        if(Dummy.isDummy){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bluetoothverbindung fehlerhaft");
        builder.setMessage("Bitte überprüfe die Bluetoothverbindung. ");
        builder.setPositiveButton("Alles ok!", (dialog, which) -> {});
        builder.setNegativeButton("Neu verbinden!", (dialog, which) -> {
            GetActivity.startAgain(activity);
        });
        builder.show();
    }



    public static void handleInvalidCalibrationData(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Die Kalibrierung ist fehlerhaft.");
        builder.setMessage("Bitte kalibriere neu!");
        builder.setPositiveButton("Kalibrieren", (dialog, which) -> {
            CocktailMachine.isCocktailMachineSet(new Postexecute() {
                                                        @Override
                                                        public void post() {
                                                            dialog.dismiss();
                                                            GetActivity.goToMenu(activity);
                                                        }
                                                    },
                    new Postexecute() {
                        @Override
                        public void post() {
                            dialog.dismiss();
                        }
                    },activity);
        });
        builder.setNegativeButton("Abbrechen", (dialog, which) -> {
            CocktailMachine.isCocktailMachineSet(new Postexecute() {
                                                     @Override
                                                     public void post() {
                                                         dialog.dismiss();
                                                         GetActivity.waitNotSet(activity);
                                                     }
                                                 },
                    new Postexecute() {
                        @Override
                        public void post() {
                            dialog.dismiss();
                        }
                    },activity);
        });

        builder.show();
    }


    public static void handleUnauthorized(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Administratorenrechte erforderlich!");
        builder.setMessage("Bitte melde dich als Administrator an. ");
        builder.setPositiveButton("Login!", (dialog, which) -> {
            dialog.dismiss();
            Log.v(TAG, "handleUnauthorized: user choosed to login");
            AdminRights.login(activity, activity.getLayoutInflater(), dialog1 -> {        });
            });
        builder.setNegativeButton("Abbrechen!", (dialog, which) -> {
            Log.v(TAG, "handleUnauthorized: user choosed to do nothing");
        });
        builder.show();
    }



}
