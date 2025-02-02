package com.example.cocktailmachine.data.enums;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Pump;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author Johanna Reidt
 * @created Do. 31.Aug 2023 - 14:35
 * @project CocktailMachine
 *
 * Fehler-Rückgaben der Cocktail-Maschine
 *
 * Mögliche Fehler, die vom ESP zurückgegeben werden können. (s. Befehle.md)
 *
 *     init: ESP wird noch initialisiert
 *     ok: alles in Ordnung
 *     processing: Befehl wird geparset (kein Fehler; kommt nur, wenn der Wert zu früh ausgelesen wird)
 *     unsupported: Befehl noch nicht implementiert
 *     unauthorized: Befehl nur für Admin verfügbar
 *     invalid json: ungültiges JSON
 *     message too big: JSON-Nachricht zu lang
 *     missing arguments: Argumente im JSON-Befehl fehlen
 *     unknown command: unbekannter Befehl
 *     command missing even though it parsed right: Befehl wurde im ESP fehlerhaft implementiert :)
 *     wrong comm channel: falscher Channel für den Befehl (Admin vs. User)
 *     invalid pump slot: ungültige Pumpe ausgewählt
 *     invalid volume: ungültige Menge (z.B. -5)
 *     invalid weight: ungültiges Gewicht (z.B. -5)
 *     invalid times: ungültige Zeit (z.B. -5)
 *     insufficient amounts of liquid available: nicht genug Flüssigkeit vorhanden
 *     liquid unavailable: Flüssigkeit fehlt im ESP
 *     recipe not found: unbekanntes Rezept
 *     recipe already exists: Rezept mit dem gleichen Namen existiert bereits
 *     missing ingredients: Zutaten fehlen im Rezept
 *     invalid calibration data: Kalibrierungs-Werte sind ungültig (z.B. 2x die gleichen Werte)
 *     can't start recipe yet: Rezept ist noch nicht bereit
 *     can't take cocktail yet: Cocktail ist noch nicht fertig
 *     calibration command invalid at this time: Kalibrierungsbefehl zur falschen Zeit geschickt
 */
public enum ErrorStatus {
    not,
    init,
    ok,
    processing,
    unsupported,
    unauthorized,
    invalid_json,
    message_too_big, missing_arguments, unknown_command,
    command_missing_even_though_it_parsed_right,
    wrong_comm_channel,
    invalid_pump_slot,
    invalid_volume,
    invalid_weight,
    invalid_times,
    insufficient_amounts_of_liquid_available,
    liquid_unavailable,
    recipe_not_found,
    recipe_already_exists,
    missing_ingredients,
    invalid_calibration_data,
    cant_start_recipe_yet,
    cant_take_cocktail_yet,
    calibration_command_invalid_at_this_time
    ;






    private static final String TAG = "ErrorStatus";
    static String error;

    static ErrorStatus status;

    private static ErrorStatus valueOfString(String n){
        n.replace(" ", "_");
        n.replace("'", "");
        for(ErrorStatus s: ErrorStatus.values()){
            if(s.name()==n){
                return s;
            }
        }
        return ErrorStatus.not;
    }

    public static void setError(String msg){
        if(msg == null){
            error = "null";
            status = ErrorStatus.not;
        }else {
            error = msg;
            status = valueOfString(error);
        }
    }


    private static boolean isError(){
        if(Dummy.isDummy){
            return false;
        }
        if(status==ErrorStatus.ok){
            return false;
        }
        if(status == ErrorStatus.init){
            return false;
        }
        return status != ErrorStatus.processing;
    }

    public static ErrorStatus getErrorStatus(){
        return status;
    }

    public static String getError(){
        return error;
    }










    //Error


    public static void updateError(Activity activity){
        if(Dummy.isDummy){
            Log.i(TAG, "getErrorMessage: dummy");
            setError("not");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadErrorStatus(activity);
        } catch (JSONException | InterruptedException | NullPointerException e) {

            Log.i(TAG, "getErrorMessage: errored");
            Log.e(TAG, "error", e);
            Log.e(TAG, e.getMessage());
            setError("invalid_json");
        }
    }

    private static void updateError(Postexecute afterReading, Activity activity){
        if(Dummy.isDummy){
            Log.i(TAG, "getErrorMessage: dummy");
            setError("not");
            afterReading.post();
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminReadErrorStatus(activity, afterReading);
        } catch (JSONException | InterruptedException | NullPointerException e) {

            Log.i(TAG, "getErrorMessage: errored");
            Log.e(TAG, "error", e);
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            setError("not");
            afterReading.post();
        }
    }

    private static void updateErrorDoIfError(Postexecute ifErrored, Activity activity){
        updateError(new Postexecute() {
            @Override
            public void post() {
                if(isError()){
                    ifErrored.post();
                }
            }
        },activity);
    }




    /**
     * get error message
     * @return
     * @author Johanna Reidt
     */
    private static String getErrorMessage(Activity activity){
        Log.i(TAG, "getErrorMessage");
        updateError(activity);
        return error;
    }

    /**
     * get error message
     * an do afterwards
     * @param afterReading
     * @return
     * @author Johanna Reidt
     */
    public static String getErrorMessage(Postexecute afterReading, Activity activity){
        Log.i(TAG, "getErrorMessage");
        updateError(afterReading,activity);
        return error;
    }


    /**
     * checks if cocktailmachine is erroring
     * @return
     * @author Johanna Reidt
     */
    public static boolean getErrorBoolean(Activity activity){
        updateError(activity);
        return isError();
    }

    /**
     * checks if cocktailmachine is erroring
     * if erroring do, what is in toDoIfErrored's post
     * @param toDoIfErrored
     * @return
     * @author Johanna Reidt
     */
    public static boolean getErrorBoolean(Postexecute toDoIfErrored,Activity activity){
        updateErrorDoIfError(toDoIfErrored,activity);
        return isError();
    }


    /**
     * checks if cocktailmachine is erroring
     * @return
     * @author Johanna Reidt
     */
    public static ErrorStatus getErrorStatus(Activity activity){
        updateError(activity);
        return status;
    }

    /**
     * checks if cocktailmachine is erroring
     * afterwards do: afterReading
     *
     * @param afterReading
     * @author Johanna Reidt
     */
    public static void getErrorStatus(Postexecute afterReading, Activity activity){
        updateError(afterReading,activity);
    }






    //reset error -----Error handling-------


    /**
     * called to get out of error mode into handling
     * @author Johanna Reidt
     */
    private static void reset(Activity activity, Postexecute postexecute){
        if(Dummy.isDummy){
            Log.i(TAG, "reset: dummy not");
            setError("not");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminResetError(activity, postexecute);
            Log.i(TAG, "reset: resetted");
        } catch (JSONException | InterruptedException| NullPointerException e) {
            Log.i(TAG, "reset: errored");
            Log.e(TAG, "reset: error", e);
            Log.e(TAG, e.getMessage());
            setError("not");
        }
    }

    public static void handleIfExistingError(Activity activity, Postexecute continueHere){
        //TODO: add continues
        reset(activity,
                new Postexecute() {
                    @Override
                    public void post() {
                        if(isCmdError()){
                            GetDialog.handleCmdError(activity, status);
                        } else if (!statusRequestWorked()) {
                            GetDialog.handleBluetoothFailed(activity);
                        } else if (isRecipeError()) {
                            handleRecipeError(activity, continueHere);
                        } else if (isCalibrationError()) {
                            if(status == invalid_calibration_data){
                                GetDialog.handleInvalidCalibrationData(activity);
                            } else if (status == calibration_command_invalid_at_this_time) {
                                Toast.makeText(activity, "Bitte warten!", Toast.LENGTH_SHORT).show();
                            }
                        } else if (isAccessTrouble()){
                            GetDialog.handleUnauthorized(activity);
                        } else {
                            Log.i(TAG, "handleIfExistingError: no error, status: "+status);
                            Toast.makeText(activity, "No error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


    public static void handleIfExistingError(Activity activity){
        handleIfExistingError(activity, Postexecute.doNothing());
    }

    private static void handleRecipeError(Activity activity){
        //TO DO: handle all cases
        Log.i(TAG, "handleRecipeError: "+ status.toString());
        handleRecipeError(activity, Postexecute.doNothing());
    }

    private static void handleRecipeError(Activity activity, Postexecute continueHere){
        //TODO: handle all cases
        Log.i(TAG, "handleRecipeError: "+ status.toString());
        AlertDialog wait = GetDialog.loadingBluetooth(activity);

        switch (status){
            case invalid_pump_slot:
                GetDialog.errorWrongPumpSlot(activity, continueHere);
                //GetDialog.errorMessage();
            case invalid_volume:
                GetDialog.doBluetooth(activity,
                        "Falsche Volumenangabe",
                        "Empfehlung: Synchronisiere die Pumpenangaben mit der PCocktailmaschine.",
                        "",
                        wait,
                        new Postexecute() {
                            @Override
                            public void post() {
                                Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                                Pump.sync(activity, new Postexecute() {
                                    @Override
                                    public void post() {
                                        wait.dismiss();
                                        continueHere.post();
                                    }
                                });
                            }
                        }

                );

                return;
            case invalid_weight:
                GetDialog.doBluetooth(activity,
                    "Falsche Gewichtsangabe",
                    "Empfehlung: Synchronisiere die Pumpenangaben mit der Cocktailmaschine.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            Pump.sync(activity, new Postexecute() {
                                @Override
                                public void post() {
                                    wait.dismiss();
                                    continueHere.post();
                                }
                            });
                        }
                    }

                );
                return;
            case invalid_times:GetDialog.doBluetooth(activity,
                    "Falsche Zeitangabe",
                    "Empfehlung: Synchronisiere die Pumpenangaben mit der Cocktailmaschine.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            Pump.sync(activity, new Postexecute() {
                                @Override
                                public void post() {
                                    wait.dismiss();
                                    continueHere.post();
                                }
                            });
                        }
                    }

                );
                return;
            case insufficient_amounts_of_liquid_available:
                GetDialog.doBluetooth(activity,
                    "Nicht genügend Flüssigkeit!",
                    "Empfehlung: Synchronisiere die Pumpenangaben mit der Cocktailmaschine, " +
                            "damit die Verfügbarkeitsdaten aktuell sind " +
                            "und benachrichtige der/die Administrator*in die Flüssigkeiten aufzufüllen.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            Pump.sync(activity, new Postexecute() {
                                @Override
                                public void post() {
                                    wait.dismiss();
                                    continueHere.post();
                                }
                            });
                        }
                    }

                );
                return;
            case liquid_unavailable:GetDialog.doBluetooth(activity,
                    "Flüssigkeit nicht vorhanden!",
                    "Empfehlung: Synchronisiere die Pumpenangaben mit der Cocktailmaschine, " +
                            "damit die Verfügbarkeitsdaten aktuell sind " +
                            "und benachrichtige der/die Administrator*in die Flüssigkeiten aufzufüllen.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            Pump.sync(activity, new Postexecute() {
                                @Override
                                public void post() {
                                    wait.dismiss();
                                    continueHere.post();
                                }
                            });
                        }
                    }

                );
                return;
            case recipe_not_found:GetDialog.doBluetooth(activity,
                    "Rezept nicht vorhanden!",
                    "Empfehlung: Speichern Sie das neue Rezept in der Cocktailmachine.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            wait.dismiss();
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            try {
                                Objects.requireNonNull(CocktailMachine.getCurrentRecipe()).sendSave(activity,
                                        new Postexecute() {
                                            @Override
                                            public void post() {
                                                //Log.i(TAG, "");
                                                wait.dismiss();
                                                continueHere.post();
                                            }
                                        }
                                );
                            }catch (NullPointerException e){
                                Log.e(TAG, "no current recipe", e);
                                Log.i(TAG, "no current recipe");
                                Toast.makeText(activity, "Speicher das Rezept manuell!", Toast.LENGTH_SHORT).show();
                                wait.dismiss();
                            }
                        }
                    }

                );
                return;
            case recipe_already_exists:GetDialog.doBluetooth(activity,
                    "Rezept bereits vorhanden!",
                    "Empfehlung: :)",
                    "Aller klar!",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            wait.dismiss();
                        }
                    }

                );
                return;
            case missing_ingredients:GetDialog.doBluetooth(activity,
                    "Zutaten im Rezept fehlen!",
                    "Empfehlung: Speichere das Rezept nochmal in der Cocktailmachine.",
                    "Synchronisation",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Die Synchronisation läuft.", Toast.LENGTH_SHORT).show();
                            try {
                                Objects.requireNonNull(CocktailMachine.getCurrentRecipe()).sendSave(activity,
                                        new Postexecute() {
                                            @Override
                                            public void post() {
                                                //Log.i(TAG, "");
                                                wait.dismiss();
                                                continueHere.post();
                                            }
                                        }
                                );
                            }catch (NullPointerException e){
                                Log.e(TAG, "no current recipe", e);
                                Log.i(TAG, "no current recipe");
                                Toast.makeText(activity, "Speicher das Rezept manuell!", Toast.LENGTH_SHORT).show();
                                wait.dismiss();
                            }
                        }
                    }

                );
                return;
            case cant_start_recipe_yet:
                GetDialog.doBluetooth(activity,
                    "Mixenstartverzögerung!",
                    "Empfehlung: Warte einen Augenblick und versuch es nochmal.",
                    "Nochmal",
                    wait,
                    new Postexecute() {
                        @Override
                        public void post() {
                            Toast.makeText(activity, "Mixbefehl wurde verschickt.", Toast.LENGTH_SHORT).show();
                            CocktailMachine.startMixing(activity,
                                    ErrorStatus.errorHandle(activity, continueHere),
                                    new Postexecute() {
                                            @Override
                                            public void post() {
                                                //Log.i(TAG, "");
                                                wait.dismiss();
                                                continueHere.post();
                                            }
                                        });
                        }
                    });
                return;

            case cant_take_cocktail_yet:
                GetDialog.doBluetooth(activity,
                        "Freigabe fehlgeschlagen!",
                        "Empfehlung: Warte einen Augenblick und versuch es nochmal.",
                        "Nochmal",
                        wait,
                        new Postexecute() {
                            @Override
                            public void post() {
                                Toast.makeText(activity, "Mixbefehl wurde verschickt.", Toast.LENGTH_SHORT).show();
                                CocktailMachine.takeCocktail(
                                        activity,
                                        ErrorStatus.errorHandle(activity, continueHere),
                                        new Postexecute() {
                                            @Override
                                            public void post() {
                                                //Log.i(TAG, "");
                                                wait.dismiss();
                                                continueHere.post();
                                            }
                                        });
                            }
                        });
                return;


        }

    }





    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @par am dialog
     * @author Johanna Reidt
     */
    /*
    public static void handleAutomaticCalibrationNotReady(Activity activity,
                                                          DialogInterface dialog,
                                                          Postexecute doAgain,
                                                          Postexecute continueHere){
        Log.i(TAG, "handleAutomaticCalibrationNotReady");
        handleSpecificErrorRepeat(activity, dialog, calibration_command_invalid_at_this_time, doAgain, continueHere);
    }

     */




    /*
    public static Postexecute checkHandle(Activity activity,
                                          HashMap<ErrorStatus, Postexecute> errorMap,
                                          Postexecute continueHere){
        Log.i(TAG, "checkHandle");
        if(Dummy.isDummy){
            return new Postexecute(){
                @Override
                public void post() {

                    Log.i(TAG, "checkHandle: dummy");
                    //start.post();
                    //Log.i(TAG, "handleSpecificErrorMethod: dummy: start");
                    continueHere.post();
                    Log.i(TAG, "checkHandle: dummy: continue");
                }
            };
            //return;
        }
        return new Postexecute() {
            @Override
            public void post() {
                    Log.i(TAG, "checkHandle: smooth run");
                    continueHere.post();
            }
        };
    }

     */

    /*
    public static Postexecute checkHandle(Activity activity, Postexecute continueHere){
        return checkHandle(activity,null,continueHere);
    }


     */
    public static Postexecute errorHandle(Activity activity){
        return errorHandle(activity, null, null);

    }

    public static Postexecute errorHandle(Activity activity, Postexecute continueHere){
        return errorHandle(activity, null, continueHere);

    }

    public static Postexecute errorHandle(Activity activity, HashMap<ErrorStatus, Postexecute> errorMap){
        return errorHandle(activity, errorMap, Postexecute.doNothing());

    }

    public static Postexecute errorHandle(Activity activity,
                                          HashMap<ErrorStatus, Postexecute> errorMap,
                                          Postexecute continueHere){

        return new Postexecute(){
            @Override
            public void post() {
                if(errorMap != null) {
                    if (errorMap.containsKey(ErrorStatus.getErrorStatus())) {
                        Log.i(TAG, "errorHandle: specific error");
                        Objects.requireNonNull(errorMap.get(ErrorStatus.getErrorStatus())).post();
                        return;
                    }
                }
                if(isError()){
                    Log.i(TAG, "errorHandle: different error");
                    Toast.makeText(activity, "Ein Fehler ist aufgetreten!", Toast.LENGTH_SHORT).show();
                    handleIfExistingError(activity, continueHere);
                }else{
                    Log.i(TAG, "errorHandle: no error");
                    continueHere.post();
                }
            }
        };

    }










    /**
     *
     * @author Johanna Reidt
     * @param activity
     * @param dialog
     * @param specificError
     * @param continueHere
     * @return
     */
    /*
    public static Postexecute handleSpecificErrorRepeat(Activity activity,
                                            DialogInterface dialog,
                                            ErrorStatus specificError,
                                            Postexecute continueHere){
        Log.i(TAG, "handleSpecificErrorMethod");
        if(Dummy.isDummy){
            return new Postexecute(){
                @Override
                public void post() {

                    Log.i(TAG, "handleSpecificErrorMethod: dummy");
                    //start.post();
                    Log.i(TAG, "handleSpecificErrorMethod: dummy: start");
                    continueHere.post();
                    Log.i(TAG, "handleSpecificErrorMethod: dummy: continue");
                }
            };
            //return;
        }

        //final Activity acc = activity;

        Log.i(TAG, "handleSpecificErrorMethod: no dummy");
        HashMap<ErrorStatus, Postexecute> errorHandle = new HashMap<>();
        errorHandle.put(specificError, doAgain);
        return new Postexecute(){
            @Override
            public void post() {

                Log.i(TAG, "handleSpecificErrorMethod: start called");

                getErrorStatus(new Postexecute() {
                    @Override
                    public void post() {
                        Log.i(TAG, "handleSpecificErrorMethod: post called");
                        if(errorHandle.containsKey(status)){
                            dialog.cancel();
                            reset(activity, new Postexecute(){
                                        @Override
                                        public void post() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                            builder.setTitle("Einen Moment...");
                                            builder.setMessage("Der ESP ist noch nicht bereit. Bitte versuche es erneut");
                                            builder.setPositiveButton("Nochmal", (dialog1, which) -> {
                                                Log.i(TAG, "handleSpecificError: Nochmal Button: call ESP calibration start");
                                                //CocktailMachine.automaticCalibration();
                                                //doAgain.post();
                                                Objects.requireNonNull(errorHandle.get(status)).post();
                                                Log.i(TAG, "handleSpecificError: Nochmal Button: check error");
                                                getErrorStatus(new Postexecute() {
                                                    @Override
                                                    public void post() {
                                                        if(status == calibration_command_invalid_at_this_time){
                                                            Log.i(TAG, "handleSpecificError: Nochmal Button: not ready esp");
                                                            Toast.makeText(activity,"Nicht bereit!", Toast.LENGTH_SHORT).show();
                                                        } else if (isError()) {
                                                            Log.i(TAG, "handleSpecificError: Nochmal Button: different error");
                                                            dialog1.cancel();
                                                            handleIfExistingError(activity);
                                                        }else {
                                                            Log.i(TAG, "handleSpecificError: Nochmal Button: continue dialog");
                                                            //GetDialog.firstAutomaticDialog(activity);
                                                            dialog1.cancel();
                                                            continueHere.post();
                                                        }
                                                    }
                                                }, activity);
                                            });
                                            builder.setNegativeButton("Abbrechen", (dialog12, which) -> {
                                                Log.i(TAG, "handleAutomaticCalibrationNotReady: Abbrechen Button");
                                                dialog12.cancel();
                                                GetActivity.waitNotSet(activity);
                                            });
                                            builder.show();
                                            Log.i(TAG, "handleSpecificErrorMethod: dialog show");
                                        }
                                    }
                            );
                        } else if(isError()){
                            Log.i(TAG, "handleSpecificError: different error");
                            dialog.cancel();
                            Toast.makeText(activity, "Ein Fehler ist aufgetreten!", Toast.LENGTH_SHORT).show();
                            handleIfExistingError(activity);
                        }else{
                            Log.i(TAG, "handleSpecificError: no error");
                        }
                    }
                }, activity);

            }
        };
    }

     */




    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @param dialog
     * @author Johanna Reidt
     */
    /*
    public static void handleSpecificErrorRepeat(Activity activity,
                                                 DialogInterface dialog,
                                                 ErrorStatus specificError,
                                                 Postexecute doAgain,
                                                 Postexecute continueHere){
        Log.i(TAG, "handleSpecificErrorRepeat");
        HashMap<ErrorStatus, Postexecute> errorHandle = new HashMap<>();
        errorHandle.put(specificError, doAgain);
        handleSpecificErrorMethod(activity,dialog, doAgain, continueHere, errorHandle);
    }

     */


    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @author Johanna Reidt
     */
    /*
    public static void handleSpecificErrorRepeat(Activity activity,
                                                 ErrorStatus specificError,
                                                 Postexecute doAgain,
                                                 Postexecute continueHere){
        HashMap<ErrorStatus, Postexecute> errorHandle = new HashMap<>();
        errorHandle.put(specificError, doAgain);
        handleSpecificErrorMethod(activity, doAgain, continueHere, errorHandle);
    }

     */



    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @param dialog
     * @author Johanna Reidt
     */
    /*
    public static void handleSpecificErrorMethod(Activity activity,
                                                 DialogInterface dialog,
                                                 Postexecute start,
                                                 Postexecute continueHere,
                                                 HashMap<ErrorStatus, Postexecute> errorHandle){
        Log.i(TAG, "handleSpecificErrorMethod");
        if(Dummy.isDummy){
            Log.i(TAG, "handleSpecificErrorMethod: dummy");
            start.post();
            Log.i(TAG, "handleSpecificErrorMethod: dummy: start");
            continueHere.post();
            Log.i(TAG, "handleSpecificErrorMethod: dummy: continue");
            return;
        }

        //final Activity acc = activity;

        Log.i(TAG, "handleSpecificErrorMethod: no dummy");
        start.post();
        Log.i(TAG, "handleSpecificErrorMethod: start called");
        getErrorStatus(new Postexecute() {
            @Override
            public void post() {
                Log.i(TAG, "handleSpecificErrorMethod: post called");
                if(errorHandle.containsKey(status)){
                    reset(activity, new Postexecute() {
                        @Override
                        public void post() {

                            dialog.cancel();
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Einen Moment...");
                            builder.setMessage("Der ESP ist noch nicht bereit. Bitte versuche es erneut");
                            builder.setPositiveButton("Nochmal", (dialog1, which) -> {
                                Log.i(TAG, "handleSpecificError: Nochmal Button: call ESP calibration start");
                                //CocktailMachine.automaticCalibration();
                                //doAgain.post();
                                Objects.requireNonNull(errorHandle.get(status)).post();
                                Log.i(TAG, "handleSpecificError: Nochmal Button: check error");
                                getErrorStatus(new Postexecute() {
                                    @Override
                                    public void post() {
                                        if(status == calibration_command_invalid_at_this_time){
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: not ready esp");
                                            Toast.makeText(activity,"Nicht bereit!", Toast.LENGTH_SHORT).show();
                                        } else if (isError()) {
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: different error");
                                            dialog1.cancel();
                                            handleIfExistingError(activity);
                                        }else {
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: continue dialog");
                                            //GetDialog.firstAutomaticDialog(activity);
                                            dialog1.cancel();
                                            continueHere.post();
                                        }
                                    }
                                }, activity);
                            });
                            builder.setNegativeButton("Abbrechen", (dialog12, which) -> {
                                Log.i(TAG, "handleAutomaticCalibrationNotReady: Abbrechen Button");
                                dialog12.cancel();
                                GetActivity.waitNotSet(activity);
                            });
                            builder.show();
                            Log.i(TAG, "handleSpecificErrorMethod: dialog show");
                        }
                    });
                } else if(isError()){
                    Log.i(TAG, "handleSpecificError: different error");
                    dialog.cancel();
                    Toast.makeText(activity, "Ein Fehler ist aufgetreten!", Toast.LENGTH_SHORT).show();
                    handleIfExistingError(activity);
                }else{
                    Log.i(TAG, "handleSpecificError: no error");
                }
            }
        }, activity);
    }


     */

    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @par am acti vity
     * @author Johanna Reidt
     */
    /*
    public static void handleSpecificErrorMethod(Activity activity,
                                                 Postexecute start,
                                                 Postexecute continueHere,
                                                 HashMap<ErrorStatus, Postexecute> errorHandle){
        Log.i(TAG, "handleSpecificErrorMethod");
        if(Dummy.isDummy){
            Log.i(TAG, "handleSpecificErrorMethod: dummy");
            start.post();
            Log.i(TAG, "handleSpecificErrorMethod: dummy: start");
            continueHere.post();
            Log.i(TAG, "handleSpecificErrorMethod: dummy: continue");
            return;
        }
        Log.i(TAG, "handleSpecificErrorMethod: no dummy");
        start.post();
        Log.i(TAG, "handleSpecificErrorMethod: start called");
        getErrorStatus(new Postexecute() {
            @Override
            public void post() {
                if(errorHandle.containsKey(status)){
                    reset(activity, new Postexecute() {
                        @Override
                        public void post() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Einen Moment...");
                            builder.setMessage("Der ESP ist noch nicht bereit. Bitte versuche es erneut");
                            builder.setPositiveButton("Nochmal", (dialog1, which) -> {
                                Log.i(TAG, "handleSpecificError: Nochmal Button: call ESP calibration start");
                                //CocktailMachine.automaticCalibration();
                                //doAgain.post();
                                Objects.requireNonNull(errorHandle.get(status)).post();
                                Log.i(TAG, "handleSpecificError: Nochmal Button: check error");
                                getErrorStatus(new Postexecute() {
                                    @Override
                                    public void post() {
                                        if(status == calibration_command_invalid_at_this_time){
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: not ready esp");
                                            Toast.makeText(activity,"Nicht bereit!", Toast.LENGTH_SHORT).show();
                                        } else if (isError()) {
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: different error");
                                            dialog1.cancel();
                                            handleIfExistingError(activity);
                                        }else {
                                            Log.i(TAG, "handleSpecificError: Nochmal Button: continue dialog");
                                            //GetDialog.firstAutomaticDialog(activity);
                                            dialog1.cancel();
                                            continueHere.post();
                                        }
                                    }
                                }, activity);
                            });
                            builder.setNegativeButton("Abbrechen", (dialog12, which) -> {
                                Log.i(TAG, "handleAutomaticCalibrationNotReady: Abbrechen Button");
                                dialog12.cancel();
                                GetActivity.waitNotSet(activity);
                            });
                            builder.show();
                        }
                    });

                } else if(isError()){
                    Log.i(TAG, "handleSpecificError: different error");
                    Toast.makeText(activity, "Ein Fehler ist aufgetreten!", Toast.LENGTH_SHORT).show();
                    handleIfExistingError(activity);
                }else{
                    Log.i(TAG, "handleSpecificError: no error");
                }
            }
        }, activity);
    }

     */
















    // Error
    public static boolean statusRequestWorked(){
        return status  != ErrorStatus.not;
    }

    public static boolean isInit(){
        return status  == ErrorStatus.init;
    }

    public static boolean isOk(){
        return status  == ErrorStatus.ok;
    }

    public static boolean isProcessing(){
        return status == ErrorStatus.processing;
    }

    /**
     * only accesable by user or admin
     * @return
     * @author Johanna Reidt
     */
    public static boolean isAccessTrouble(){
        return (status == ErrorStatus.unauthorized
                || status == ErrorStatus.wrong_comm_channel);

    }

    /**
     * is true if command to ESP is not valid
     * either not supported
     * missing arguments
     * etc.
     * @return
     * @author Johanna Reidt
     */
    public static boolean isCmdError(){
        return (status ==  unsupported
        || status == invalid_json
        || status ==  message_too_big
        || status == missing_arguments
        || status == unknown_command
        || status == command_missing_even_though_it_parsed_right);
    }

    /**
     * problems with recipe
     * either while mixing, saving
     * @return
     * @author Johanna Reidt
     */
    public static boolean isRecipeError(){
        return (status == invalid_pump_slot
        || status == invalid_volume
        || status == invalid_weight
        || status == invalid_times
        || status == insufficient_amounts_of_liquid_available
        || status == liquid_unavailable
        || status == recipe_not_found
        || status == recipe_already_exists
        || status == missing_ingredients
        || status == cant_start_recipe_yet
        || status == cant_take_cocktail_yet);
    }

    /**
     * problems with calibrations
     * either while or during the start of it
     * @return
     * @author Johanna Reidt
     */
    public static boolean isCalibrationError(){
        return (status == calibration_command_invalid_at_this_time
        || status==invalid_calibration_data);
    }





}
