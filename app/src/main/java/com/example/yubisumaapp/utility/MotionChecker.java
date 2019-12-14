package com.example.yubisumaapp.utility;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;

public class MotionChecker {
    public static final int ACTION = 1;
    public static final int CALL = 2;
    public static final int SKILL = 3;

    public static boolean isAction(Motion motion) {
        return motion instanceof Action;
    }

    public static boolean isCall(Motion motion) {
        return motion instanceof Call;
    }

    public static boolean isSkill(Motion motion) {
        return motion instanceof Skill;
    }

    public static Action asAction(Motion motion) {
        return (Action)motion;
    }

    public static Call asCall(Motion motion) {
        return (Call)motion;
    }

    public static Skill asSkill(Motion motion) {
        return (Skill)motion;
    }

    public static int checkMotion(Motion motion) {
        if(isAction(motion)) {
            return ACTION;
        } else if (isCall(motion)) {
            return CALL;
        } else if (isSkill(motion)) {
            return SKILL;
        } else {
            return -1;
        }
    }
}
