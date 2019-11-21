package com.example.yubisumaapp.entity.log;

import com.example.yubisumaapp.entity.motion.Motion;

public class MotionLog {
    public Motion motion;
    public int fingerStock;
    public int skillPoint;

    public MotionLog(Motion motion, int fingerStock, int skillPoint) {
        this.motion = motion;
        this.fingerStock = fingerStock;
        this.skillPoint = skillPoint;
    }
}
