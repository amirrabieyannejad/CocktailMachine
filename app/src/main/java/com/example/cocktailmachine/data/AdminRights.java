package com.example.cocktailmachine.data;

import com.example.cocktailmachine.data.db.DatabaseConnection;
import com.example.cocktailmachine.data.db.NotInitializedDBException;
import com.example.cocktailmachine.data.model.UserPrivilegeLevel;

public class AdminRights {
    private static AdminRights singleton = null;
    private UserPrivilegeLevel privilege = UserPrivilegeLevel.User;

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
}
