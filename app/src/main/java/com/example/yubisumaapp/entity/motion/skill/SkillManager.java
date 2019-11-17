package com.example.yubisumaapp.entity.motion.skill;

import com.example.yubisumaapp.entity.motion.skill.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkillManager {
    // defence
    public static final int TRAP = 0;
    // attack
    public static final int TSUCHI_FUMAZU = 0;
    public static final int CHOU_CHOU = 1;

    public static ArrayList<Skill> attackSkillList = new ArrayList<>();
    public static ArrayList<Skill> defenceSkillList = new ArrayList<>();

    // ゲーム開始時に初期化される。
    static {
        // 防御スキル
        defenceSkillList.add(new Trap("トラップ", 0));
        // 攻撃スキル
        attackSkillList.add(new TsuchiFumazu("土踏まず", 1));
        attackSkillList.add(new ChouChou("ちょうちょ", 2));
    }
}
