package com.example.yubisumaapp.entity.motion.skill;

import java.io.Serializable;

public class ChouChou extends Skill implements Serializable {
    public ChouChou(String skillName, int consumeSkillPoint) {
        super(skillName, consumeSkillPoint);
    }
    @Override
    public int invokeEffect(boolean isSuccess) {
        if(isSuccess) {
            // fingerStockを2減らす
            return -2;
        } else {
            // fingerStockを2増やす
            return 2;
        }
    }
}
