package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.activity.YubisumaActivity;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.SkillManager;

import java.util.ArrayList;

public class Member {
    public int skillPoint;
    public int fingerStock;
    public int memberIndex;

    public boolean isParent;
    public boolean isClear = false;

    public int beforeFingerStock = 0;
    public int beforeSkillPoint = 0;

    protected Motion motion;
    ArrayList<Member> LogList = new ArrayList<>();

    public Member(int skillPoint, int fingerStock, int memberIndex) {
        this.skillPoint = this.beforeSkillPoint = skillPoint;
        this.fingerStock = this.beforeFingerStock = fingerStock;
        this.memberIndex = memberIndex;
    }

    public int getMyFingerCount() {
        return (fingerStock>2) ? 2 : fingerStock;
    }

    public void setupTurn() {
        LogList.add(this);
        rememberBeforeStatus();
        this.motion = null;
    }

    public void turnEnd() {
        isParent = false;

        // 最大値に補正
        if(YubisumaActivity.ICON_SIZE < skillPoint) {
            skillPoint = YubisumaActivity.ICON_SIZE;
        }
        if(YubisumaActivity.ICON_SIZE < fingerStock) {
            fingerStock = YubisumaActivity.ICON_SIZE;
        }
        // クリアしたら
        if(fingerStock <= 0) {
            isClear = true;
        }
    }

    public void rememberBeforeStatus() {
        beforeFingerStock = this.fingerStock;
        beforeSkillPoint = this.skillPoint;
    }

    public ArrayList<Skill> getAvailableSkillList() {
        ArrayList<Skill> skillList;
        if(isParent) {
            skillList = SkillManager.attackSkillList;
        } else {
            skillList = SkillManager.defenceSkillList;
        }
        ArrayList<Skill> availableSkillList = new ArrayList<>();
        for(Skill skill : skillList) {
            if(skill.getConsumeSkillPoint() <= skillPoint) {
                availableSkillList.add(skill);
            }
        }
        return availableSkillList;
    }

    public boolean isPlayer() {return this instanceof Player; }

    public boolean isCPU() {
        return this instanceof CPU;
    }

    public boolean hasAction() { return motion instanceof Action; }

    public boolean hasCall() { return motion instanceof Call; }

    public boolean hasSkill() { return motion instanceof Skill; }

    // オーバーロード
    public void setMotion(Action action) {
        this.motion = action;
    }

    // オーバーロード
    public void setMotion(Call call) {
        this.motion = call;
    }

    // オーバーロード
    public void setMotion(Skill skill) {
        this.motion = skill;
        this.skillPoint -= skill.getConsumeSkillPoint();
    }

    public void setSkill(int skillIndex) {
        // 前のターンでのステータスを保存
        if(isParent) {
            setMotion(SkillManager.attackSkillList.get(skillIndex));
        } else {
            if(skillIndex != -1) {
                setMotion(SkillManager.defenceSkillList.get(skillIndex));
            }
        }
    }

    public Motion getMotion() {
        return this.motion;
    }

    public Action getAction() {
        return (Action)motion;
    }

    public Call getCall() {
        return (Call)motion;
    }

    public Skill getSkill() {
        return (Skill)motion;
    }
}
