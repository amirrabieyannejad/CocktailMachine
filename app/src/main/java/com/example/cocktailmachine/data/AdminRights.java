package com.example.cocktailmachine.data;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminRights {
    private static AdminRights singleton = null;
    private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;
    private Long userId;

    private AdminRights(){

    }

    public static AdminRights getSingleton(){
        if(singleton == null){
            singleton = new AdminRights();
        }
        return singleton;
    }

    public static UserPrivilegeLevel getUserPrivilegeLevel(){
        return getSingleton().privilege;
    }

    public static void setUserPrivilegeLevel(UserPrivilegeLevel privilege){
        getSingleton().privilege = privilege;
    }

    public static boolean isAdmin(){
        return getUserPrivilegeLevel().equals(UserPrivilegeLevel.Admin);
    }

    public static Long getUserId(){
        return getSingleton().userId;
    }
    public static void setUserId(Long userId){
        getSingleton().userId = userId;
    }

    public static void setUser(JSONObject jsonObject){
        try {
            getSingleton().userId  = jsonObject.getLong("user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject sendUserId(){

        JSONObject json = new JSONObject();
        try {
            json.put("name", String.valueOf(System.currentTimeMillis()));
            json.put("cmd", "init_user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
