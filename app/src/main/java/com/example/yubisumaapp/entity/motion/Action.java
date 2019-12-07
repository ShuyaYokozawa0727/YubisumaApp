package com.example.yubisumaapp.entity.motion;
public class Action extends Motion {
    protected int standCount;

    public Action(int standCount){
        this.standCount = standCount;
    }

    public int getStandCount() {
        return standCount;
    }
}
