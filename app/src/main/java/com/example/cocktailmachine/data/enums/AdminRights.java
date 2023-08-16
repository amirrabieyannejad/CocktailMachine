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

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminRights {
    private static final String TAG = "AdminRights";
    public static final String PASSWORD = "admin";
    private static AdminRights singleton = null;
    private static BluetoothSingleton bluetoothSingleton = BluetoothSingleton.getInstance();
    private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
    private int userId = -1;

    private AdminRights(){

    }

    public static AdminRights getSingleton(){
        if(singleton == null){
            singleton = new AdminRights();
        }
        return singleton;
    }

    //USer ID handling
    public static int getUserId(){
        //TODO: USE THIS AMIR *OK*
        return getSingleton().userId;
    }
    public static void setUserId(int userId){
        getSingleton().userId = userId;
    }

    public static void setUser(JSONObject jsonObject){
        //TODO: USE THIS AMIR **DONE**

        try {
            setUserId(jsonObject.getInt("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get JSON Object to init user
     * @return
     */
    private static JSONObject getUserIdAsMessage(){

        JSONObject json = new JSONObject();
        try {
            json.put("name", String.valueOf(System.currentTimeMillis()));
            json.put("cmd", "init_user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * init user with bluetooth
     * @return
     */
    public static void initUser(Activity activity, String name)
            throws JSONException, InterruptedException {
        //TODO: init user  Dummy Function **OK**
        //TODO: AMIR  ** JOHANNA bitte kontrollieren **
        bluetoothSingleton.userInitUser(name,activity);

        /*

        JSONObject getQuestion = getUserIdAsMessage();
        JSONObject answer = new JSONObject();
        setUser(answer);

         */
    }

    /**
     * {"cmd": "abort", "user": 483}
     * @return
     */
    private static JSONObject getUserAbortMessage(){
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "abort");
            json.put("user", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * init user with bluetooth
     * @return
     */
    public static void abortUser(Activity acitvity, String name){
        //TODO: abort user

        JSONObject getQuestion = getUserAbortMessage();
        JSONObject answer = new JSONObject();
        //setUser(answer);
        setUserId(-1);
    }

    public static boolean isUserIntialized(){
        return getUserId()>-1;
    }




    //Admin/ User status
    public static UserPrivilegeLevel getUserPrivilegeLevel(){
        return getSingleton().privilege;
    }

    public static void setUserPrivilegeLevel(UserPrivilegeLevel privilege){
        getSingleton().privilege = privilege;
    }

    public static boolean isAdmin(){
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
                if(loginView.check()){
                    AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
                    Toast.makeText(getContext,"Eingeloggt!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(dismissListener);
        builder.show();
    }

    public static void logout(){
        AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.User);
        //Toast.makeText(getContext,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
    }


    private static class LoginView{
        private final TextView t;
        private final EditText e;
        private final View v;
        public LoginView(Context context, View v) {
            this.v = v;
            t = (TextView) v.findViewById(R.id.textView_edit_text);
            e = (EditText) v.findViewById(R.id.editText_edit_text);
            e.setHint("");
            t.setText("Passwort: ");
            e.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }
        public boolean check(){
            return e.getText().toString().equals("admin");
        }


    }
}
