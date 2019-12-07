package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.SkillManager;
import com.example.yubisumaapp.utility.UIDrawHelper;

import java.util.ArrayList;

public class Player {
    public int skillPoint;
    public int fingerStock;
    public int playerIndex;
    public boolean isParent;
    public boolean isClear = false;
    public int beforeFingerStock = 0;
    public int beforeSkillPoint = 0;

    ArrayList<Player> LogList = new ArrayList<>();

    protected Motion motion;

    Player(int skillPoint, int fingerStock, int playerIndex) {
        this.skillPoint = skillPoint;
        this.fingerStock = fingerStock;
        this.playerIndex = playerIndex;
    }

    public void turnStart() {
        this.motion = null;
        // 最大値に補正
        if(UIDrawHelper.ICON_SIZE < skillPoint) {
            skillPoint = UIDrawHelper.ICON_SIZE;
        }
        if(UIDrawHelper.ICON_SIZE < fingerStock) {
            fingerStock = UIDrawHelper.ICON_SIZE;
        }
        LogList.add(this);
    }

    public void turnEnd() {
        isParent = false;
        if(!hasSkill()) {
            skillPoint++;
        }
        // クリアしたら
        if(fingerStock <= 0) {
            isClear = true;
        }

    }

    public void skillResult(boolean isSuccess) {
        this.fingerStock += takeSkill().invokeEffect(isSuccess);
    }

    public void callResult(int standTotalFingerCount) {
        // コール成功
        if(standTotalFingerCount == takeCall().getCallCount()) {
            this.fingerStock -= 1;
        }
    }

    public String[] getAvailableSkillNameArray() {
        // スキルリストをスイッチ
        ArrayList<Skill> skillList = getAvailableSkillList();
        // スキルネームリスト作成
        ArrayList<String> skillNameList = new ArrayList<>();
        for(Skill skill : skillList) {
            skillNameList.add(skill.getSkillName());
        }
        // 配列に変換
        String[] activeSkillArray = new String[skillNameList.size()];
        for(int index=0; index<skillList.size(); index++) {
            activeSkillArray[index] = skillNameList.get(index);
        }
        return activeSkillArray;
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

    public void setSkillFromUI(int skillIndex) {
        if(isParent) {
            setMotion(SkillManager.attackSkillList.get(skillIndex));
        } else {
            if(skillIndex != -1) {
                setMotion(SkillManager.defenceSkillList.get(skillIndex));
            }
        }
    }

    public int getMyFingerCount() {
        return (fingerStock>2) ? 2 : fingerStock;
    }

    public Motion getMotion() {
        return this.motion;
    }

    public String getSkillName() {
        if(hasSkill()) {
            return ((Skill)motion).getSkillName();
        } else {
            return "";
        }
    }

    public void rememberBeforeStatus() {
        beforeFingerStock = this.fingerStock;
        beforeSkillPoint = this.skillPoint;
    }

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

    public boolean isCPU() {
        return this instanceof CPU;
    }

    public boolean hasAction() {
        return motion instanceof Action;
    }

    public boolean hasCall() {
        return motion instanceof Call;
    }

    public boolean hasSkill() {
        return motion instanceof Skill;
    }

    public Action takeAction() {
        return (Action)motion;
    }

    public Call takeCall() {
        return (Call)motion;
    }

    public Skill takeSkill() {
        return (Skill)motion;
    }
}
