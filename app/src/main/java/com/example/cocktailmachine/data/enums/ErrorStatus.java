package com.example.cocktailmachine.data.enums;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.ui.model.v2.GetActivity;
import com.example.cocktailmachine.ui.model.v2.GetDialog;

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
        error = msg;
        status = valueOfString(error);
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
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            setError("not");
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
            e.printStackTrace();
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
     * @param afterReading
     * @return
     * @author Johanna Reidt
     */
    public static ErrorStatus getErrorStatus(Postexecute afterReading,Activity activity){
        updateError(afterReading,activity);
        return status;
    }






    //reset error -----Error handling-------


    /**
     * called to get out of error mode into handling
     * @author Johanna Reidt
     */
    private static void reset(Activity activity){
        if(Dummy.isDummy){
            setError("not");
            return;
        }
        try {
            BluetoothSingleton.getInstance().adminResetError(activity);
            Log.i(TAG, "resetted");
        } catch (JSONException | InterruptedException| NullPointerException e) {
            Log.i(TAG, "reset: errored");
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            setError("not");
        }
    }


    public static void handleIfExistingError(Activity activity){
        reset(activity);
        if(isCmdError()){
            GetDialog.handleCmdError(activity, status);
        } else if (!statusRequestWorked()) {
            GetDialog.handleBluetoothFailed(activity);
        } else if (isRecipeError()) {
            handleRecipeError(activity);
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
        }


    }

    private static void handleRecipeError(Activity activity){
        //TODO: handle all cases
    }





    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @param dialog
     * @author Johanna Reidt
     */
    public static void handleAutomaticCalibrationNotReady(Activity activity,
                                                          DialogInterface dialog,
                                                          Postexecute doAgain,
                                                          Postexecute continueHere){
        Log.i(TAG, "handleAutomaticCalibrationNotReady");
        handleSpecificErrorRepeat(activity, dialog, calibration_command_invalid_at_this_time, doAgain, continueHere);
    }










    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @param dialog
     * @author Johanna Reidt
     */
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


    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @author Johanna Reidt
     */
    public static void handleSpecificErrorRepeat(Activity activity,
                                                 ErrorStatus specificError,
                                                 Postexecute doAgain,
                                                 Postexecute continueHere){
        HashMap<ErrorStatus, Postexecute> errorHandle = new HashMap<>();
        errorHandle.put(specificError, doAgain);
        handleSpecificErrorMethod(activity, doAgain, continueHere, errorHandle);
    }



    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @param dialog
     * @author Johanna Reidt
     */
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
                    reset(activity);
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


    /**
     * if esp not ready for calibration
     * ERROR CODE: calibration_command_invalid_at_this_time
     * show Dialog
     * @param activity
     * @author Johanna Reidt
     */
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
                    reset(activity);
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
