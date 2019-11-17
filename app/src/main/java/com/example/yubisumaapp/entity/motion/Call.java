package com.example.yubisumaapp.entity.motion;

import com.example.yubisumaapp.entity.motion.Action;

public class Call extends Action {
    private int callCount;

    public Call(int standCount, int callCount) {
        super(standCount);
        this.callCount = callCount;
    }

    public int getCallCount() {
        return callCount;
    }
}
