package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.motion.skill.Skill;

import java.util.ArrayList;

public class User extends Player {

    User(int skillPoint, int fingerStock, int playerIndex) {
        super(skillPoint, fingerStock, playerIndex);
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

    public String getSkillName() {
        if(motion instanceof Skill) {
            return getSkill().getSkillName();
        } else {
            return "";
        }
    }
}
