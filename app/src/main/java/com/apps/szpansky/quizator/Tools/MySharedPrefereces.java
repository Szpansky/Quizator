package com.apps.szpansky.quizator.Tools;


import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefereces {

    static public String getLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("login", "");
    }

    static public String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("password", "");
    }

    static public void setLogin(Context context, String login) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", login);
        editor.apply();
    }

    static public void setPassword(Context context, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", password);
        editor.apply();
    }
}
