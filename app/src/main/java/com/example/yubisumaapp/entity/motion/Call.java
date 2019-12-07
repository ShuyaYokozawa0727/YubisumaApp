package com.example.yubisumaapp.entity.motion;

public class Call extends Motion {
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
