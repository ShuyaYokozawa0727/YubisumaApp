package com.example.yubisumaapp.entity.motion;

import java.io.Serializable;

public class Action extends Motion implements Serializable {
    protected int standCount;

    public Action(int standCount){
        this.standCount = standCount;
    }

    public int getStandCount() {
        return standCount;
    }
}
