package com.apps.szpansky.quizator.Tools;


public class TextChecker {

    public boolean isValidBase(String text){
        return !(text.contains("$") ||
                text.contains("*") ||
                text.contains("@") ||
                text.contains("\"") ||
                text.contains("\'") ||
                text.contains("&") ||
                text.contains("img"));
    }

}
