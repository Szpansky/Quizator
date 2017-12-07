package com.apps.szpansky.quizator.Tools;


import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    static public boolean getSaveLoginDataIsSet(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("global",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("saveLoginData", false);
    }

    static public boolean getMainTutorialWasShown(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("mainInfo",false);
    }

    static public String getLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("login", "");
    }

    static public String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("password", "");
    }

    static public void setSaveLoginData(Context context, boolean remember){
        SharedPreferences sharedPreferences = context.getSharedPreferences("global", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("saveLoginData", remember);
        editor.apply();
    }

    static public void setMainTutorialWasShown(Context context, boolean infoStatus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("mainInfo", infoStatus);
        editor.apply();
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
