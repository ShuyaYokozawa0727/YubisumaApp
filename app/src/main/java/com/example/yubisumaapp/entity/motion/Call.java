package com.example.yubisumaapp.entity.motion;

import java.io.Serializable;

public class Call extends Motion implements Serializable {
    private int callCount;
    private Action action;

    public Call(int callCount) {
        this.callCount = callCount;
    }

    public Call(Action action, int callCount) {
        this.action = action;
        this.callCount = callCount;
    }

    public void setAction(Action action) { this.action = action; }

    public int getCallCount() {
        return callCount;
    }

    public Action getAction() { return action; }
}
