package com.example.yubisumaapp.entity.motion.skill;

import com.example.yubisumaapp.entity.motion.Motion;

import java.io.Serializable;

public abstract class Skill extends Motion implements Serializable {
    private String skillName;
    private int consumeSkillPoint;

    public Skill(String skillName, int consumeSkillPoint) {
        this.skillName = skillName;
        this.consumeSkillPoint = consumeSkillPoint;
    }

    public int getConsumeSkillPoint() {
        return consumeSkillPoint;
    }

    public String getSkillName() {
        return skillName;
    }

    public abstract int invokeEffect(boolean isSuccess);
}
