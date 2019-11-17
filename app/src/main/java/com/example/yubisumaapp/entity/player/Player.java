package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.SkillManager;
import com.example.yubisumaapp.utility.UIDrawer;

import java.util.ArrayList;

public class Player {
    public int skillPoint;
    public int fingerStock;
    public int playerIndex;
    public boolean isParent;
    public boolean inBattleProcess; // バトル処理中か？（スキルなどの処理が終わったプレイヤーからfalseになる）

    protected Motion motion;

    Player(int skillPoint, int fingerStock, int playerIndex) {
        this.skillPoint = skillPoint;
        this.fingerStock = fingerStock;
        this.playerIndex = playerIndex;
    }

    public int getStandableFingerCount() {
        return (fingerStock>2) ? 2 : fingerStock;
    }

    public void turnStart() {
        inBattleProcess = true;
        this.motion = null;
        // 最大値に補正
        if(UIDrawer.ICON_SIZE < skillPoint) {
            skillPoint = UIDrawer.ICON_SIZE;
        }
        if(UIDrawer.ICON_SIZE < fingerStock) {
            fingerStock = UIDrawer.ICON_SIZE;
        }
    }

    public boolean isAvailableAttackSkill(int skillIndex) {
        Skill useSkill = SkillManager.attackSkillList.get(skillIndex);
        if(skillPoint < useSkill.getConsumeSkillPoint()) {
            // 発動できない
            return false;
        } else {
            return true;
        }
    }

    public Skill setAttackSkill(int skillIndex) {
        Skill useSkill = SkillManager.attackSkillList.get(skillIndex);
        setMotion(useSkill);
        return useSkill;
    }

    public Skill setDefenceSkill(int skillIndex) {
        Skill useSkill = SkillManager.defenceSkillList.get(skillIndex);
        setMotion(useSkill);
        return useSkill;
    }

    public String[] getAvailableSkillNameArray() {
        // スキルリストをスイッチ
        ArrayList<Skill> skillList;
        if(isParent) {
            skillList = getAvailableAttackSkillList();
        } else {
            // availableなものにするかは後々
            skillList = SkillManager.defenceSkillList;
        }
        // スキルネームリスト作成
        ArrayList<String> skillNameList = new ArrayList<>();
        for(Skill skill : skillList) {
            skillNameList.add(skill.getSkillName());
        }
        // 配列に変換
        String[] activeSkill = new String[skillNameList.size()];
        for(int index=0; index<skillList.size(); index++) {
            activeSkill[index] = skillNameList.get(index);
        }
        return activeSkill;
    }

    public ArrayList<Skill> getAvailableAttackSkillList() {
        ArrayList<Skill> availableSkill = new ArrayList<>();
        for(Skill skill : SkillManager.attackSkillList) {
            if(skill.getConsumeSkillPoint() <= skillPoint) {
                availableSkill.add(skill);
            }
        }
        return availableSkill;
    }

    public void skillResult(boolean isSuccess) {
        // TODO : Skillでキャストしているが、ここでTrapとか土踏まずやちょうちょのメソッドが呼び出されているか確認
        this.fingerStock += ((Skill)this.motion).invokeEffect(isSuccess);
    }

    public void callResult(int standTotalFingerCount) {
        // コール成功
        if(standTotalFingerCount == ((Call)this.motion).getCallCount()) {
            this.fingerStock -= 1;
        }
    }

    public void setMotion(Action action) {
        // ダブらないならskillPoint追加
        if(this.motion == null) {
            this.skillPoint++;
        }
        this.motion = action;
    }

    public void setMotion(Skill skill) {
        this.motion = skill;
        this.skillPoint -= skill.getConsumeSkillPoint();
    }

    public Motion getMotion() {
        return this.motion;
    }

    public GameMaster.FingerStockListenerInterface listener = new GameMaster.FingerStockListenerInterface() {
        @Override
        public void isPlayerGameOver() {
            inBattleProcess = false;
        }
    };
}
