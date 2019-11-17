package com.example.yubisumaapp.entity.motion.skill;

public class ChouChou extends Skill {
    public ChouChou(String skillName, int consumeSkillPoint) {
        super(skillName, consumeSkillPoint);
    }
    @Override
    public int invokeEffect(boolean isSuccess) {
        if(isSuccess) {
            // 使用者のfingerStockを2減らす
            return -2;
        } else {
            // 使用者のfingerStockを2増やす
            return 2;
        }
    }
}
