package com.example.yubisumaapp.entity.motion.skill;

import java.io.Serializable;

public class TsuchiFumazu extends Skill implements Serializable {
    public TsuchiFumazu(String skillName, int consumeSkillPoint) {
        super(skillName, consumeSkillPoint);
    }
    @Override
    public int invokeEffect(boolean isSuccess) {
        if(isSuccess) {
            // 使用者のfingerStockを1減らす
            return -1;
        } else {
            // 使用者のfingerStockを1増やす
            return 1;
        }
    }
}
