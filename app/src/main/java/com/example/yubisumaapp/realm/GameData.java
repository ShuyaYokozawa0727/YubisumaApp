package com.example.yubisumaapp.realm;

import io.realm.RealmObject;

public class GameData extends RealmObject {
    private String name;
    private int count;
    private int score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
