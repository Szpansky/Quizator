package com.apps.szpansky.quizator.SimpleData;

import java.io.Serializable;


public class QuestionData implements Serializable {

    private String id;
    private String text;
    private String link;
    private String points;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
