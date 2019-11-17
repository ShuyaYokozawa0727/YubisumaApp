package com.example.yubisumaapp.entity.motion.skill;

public class Trap extends Skill {
    public Trap(String skillName, int consumeSkillPoint) {
        super(skillName, consumeSkillPoint);
    }
    @Override
    public int invokeEffect(boolean isSuccess) {
        if(isSuccess) {
            // スキル効果を反転する
            // スキル使用者のfingerStockをスキルの効力分減らす
            // これは各スキルのFailに定義されるはず
            return 0;
        } else {
            // 使用者のfingerStockを1増やす
            return 1;
        }
    }
}
