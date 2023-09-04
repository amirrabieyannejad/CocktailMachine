package com.example.cocktailmachine.data.db;

import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Loader {
    public void load(String fileName, SQLiteDatabase db, String tableName) throws IOException {
        FileReader file = new FileReader(fileName);
        BufferedReader buffer = new BufferedReader(file);
        String line = "";
        String columns = "_id, name, dt1, dt2, dt3";
        String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
        String str2 = ");";

        db.beginTransaction();
        while ((line = buffer.readLine()) != null) {
            StringBuilder sb = new StringBuilder(str1);
            String[] str = line.split(",");
            sb.append("'" + str[0] + "',");
            sb.append(str[1] + "',");
            sb.append(str[2] + "',");
            sb.append(str[3] + "'");
            sb.append(str[4] + "'");
            sb.append(str2);
            db.execSQL(sb.toString());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
