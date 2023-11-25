package com.example.cocktailmachine.data.enums;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminRights {
    private static final String TAG = "AdminRights";
    public static final String PASSWORD = "admin";
    private static AdminRights singleton = null;

    private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
    private int userId = -1;

    private AdminRights(){

    }

    public static AdminRights getSingleton(){
        Log.i(TAG, "getSingleton");
        if(singleton == null){
            singleton = new AdminRights();
        }
        return singleton;
    }

    //USer ID handling
    public static int getUserId(){
        Log.i(TAG, "getUserId");
        //TO DO: USE THIS AMIR *OK*
        return getSingleton().userId;
    }
    public static void setUserId(int userId){
        Log.i(TAG, "setUserId");
        getSingleton().userId = userId;
        Log.i(TAG, "setUserId: userId");
    }

    /**
     * set user wit {"user": 4}
     * @author Johanna Reidt
     * @param jsonObject
     */
    public static void setUser(JSONObject jsonObject){
        Log.i(TAG, "setUser");
        //TO DO: USE THIS AMIR **DONE**

        if(jsonObject == null){
            Log.w(TAG, "setUser: jsonObject null");
            return;
        }

        try {
            setUserId(jsonObject.getInt("user"));
            Log.i(TAG, "setUser: done");
        } catch (JSONException e) {
            Log.i(TAG, "setUser: failed");
            Log.e(TAG, "error: ",e);
            ////Log.e(TAG, "error", e);
        }
    }

    /**
     * get JSON Object to init user
     * @return
     */
    private static JSONObject getUserIdAsMessage(){
        Log.i(TAG, "getUserIdAsMessage");

        JSONObject json = new JSONObject();
        try {
            json.put("name", String.valueOf(System.currentTimeMillis()));
            json.put("cmd", "init_user");
            Log.i(TAG, "getUserIdAsMessage: done");
        } catch (JSONException e) {
            Log.i(TAG, "getUserIdAsMessage: failed");
            Log.e(TAG, "error: ",e);
            ////Log.e(TAG, "error", e);
        }
        return json;
    }

    /**
     * init user with bluetooth
     * @return
     */
    public static void initUser(Activity activity, String name){
        //TO DO: DUMMY
        Log.i(TAG, "initUser");
        if(Dummy.isDummy) {
            Log.i(TAG, "initUser dummy");
            getSingleton().userId = 3;
            return;
        }
        try{
            BluetoothSingleton.getInstance().userInitUser(name,activity);
            Log.i(TAG, "initUser done");
        } catch (JSONException | InterruptedException e) {
            //throw new RuntimeException(e);
            Log.i(TAG, "init User failed");
            Log.e(TAG, "error",e);
            //Log.e(TAG, "error", e);
        }
    }

    /**
     * {"cmd": "abort", "user": 483}
     * @return
     */
    private static JSONObject getUserAbortMessage(){
        Log.i(TAG, "getUserAbortMessage");
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "abort");
            json.put("user", getUserId());
            Log.i(TAG, "getUserAbortMessage done");
        } catch (JSONException e) {
            Log.i(TAG, "getUserAbortMessage User failed");
            Log.e(TAG, "error",e);
            //Log.e(TAG, "error", e);
        }
        return json;
    }


    static void saveMCAdresse(Context context){

        BluetoothSingleton.getInstance().getEspDeviceAddress();
    }

    static String loadMCAdresseFromDB(Context context){
        return "";
    }





    //Admin/ User status
    public static UserPrivilegeLevel getUserPrivilegeLevel(){
        Log.i(TAG, "getUserPrivilegeLevel");
        return getSingleton().privilege;
    }

    public static void setUserPrivilegeLevel(UserPrivilegeLevel privilege){
        Log.i(TAG, "setUserPrivilegeLevel");
        getSingleton().privilege = privilege;
        Log.i(TAG, "setUserPrivilegeLevel: "+privilege);
    }

    public static boolean isAdmin(){
        Log.i(TAG, "isAdmin");
        return getUserPrivilegeLevel().equals(UserPrivilegeLevel.Admin);
    }

    public static void login(Context getContext,
                             LayoutInflater getLayoutInflater,
                             DialogInterface.OnDismissListener dismissListener){
        Log.i(TAG, "login" );

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext);
        builder.setTitle("Login");
        View v = getLayoutInflater.inflate(R.layout.layout_login, null);
        AdminRights.LoginView loginView = new AdminRights.LoginView(getContext, v);

        builder.setView(v);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "login: Weiter");
                if(loginView.check()){
                    Log.i(TAG, "login: admin");
                    AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
                    Toast.makeText(getContext,"Eingeloggt!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "login: abbruch");
            }
        });
        builder.setOnDismissListener(dismissListener);
        builder.show();
    }

    public static void logout(){
        Log.i(TAG, "logout");
        AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.User);
        //Toast.makeText(getContext,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
    }


    private static class LoginView{
        private final TextView t;
        private final EditText e;
        private final View v;
        public LoginView(Context context, View v) {
            Log.i(TAG, "LoginView");
            this.v = v;
            t = v.findViewById(R.id.textView_edit_text);
            e = v.findViewById(R.id.editText_edit_text);
            e.setHint("");
            t.setText("Passwort: ");
            e.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }
        public boolean check(){
            Log.i(TAG, "LoginView: check");
            return e.getText().toString().equals("admin");
        }


    }
}
