package com.apps.szpansky.quizator.SimpleData;

import java.io.Serializable;

/**
 * Created by Marcin on 2017-11-25.
 */

public class UserData implements Serializable {
    private String cookie;
    private String userId;
    private String username;
    private String nicename;
    private String email;
    private String registered;
    private String nickname;
    private String userPoints;
    private String userPointsNext;
    private String rankName;
    private String rankNext;
    private String userAvatar;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNicename() {
        return nicename;
    }

    public void setNicename(String nicename) {
        this.nicename = nicename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(String userPoints) {
        this.userPoints = userPoints;
    }

    public String getUserPointsNext() {
        return userPointsNext;
    }

    public void setUserPointsNext(String userPointsNext) {
        this.userPointsNext = userPointsNext;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getRankNext() {
        return rankNext;
    }

    public void setRankNext(String rankNext) {
        this.rankNext = rankNext;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}
