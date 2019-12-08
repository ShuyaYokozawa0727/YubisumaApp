package com.example.yubisumaapp.entity.motion.skill;

public class ChouChou extends Skill {
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
