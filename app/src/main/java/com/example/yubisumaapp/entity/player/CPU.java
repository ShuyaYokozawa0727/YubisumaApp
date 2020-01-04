package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.skill.SkillManager;

import java.util.Random;

/*
 * スキルポイントが2以上あればちょうちょか土踏まずが発動できる
 * スキルポイントが1以上あればつちふまずが発動できる
 * しかし、トラップを発動される可能性があるので、すぐには発動しないほうがいい。
 * 自分のfingerStockが2のときにちょうちょを発動すると勝てるが、相手がトラップを発動する可能性が高い。
 * それを読んで、自分は何も挙げずに0とコールするのも戦略。
 *
 * 相手のスキルポイントが2以上あればちょうちょを発動される可能性がある
 * 相手のスキルポイントが1以上あればつちふまずを発動される可能性がある
 * 各状況に重み付けが必要
 *
 */
public class CPU extends Player {

    private Random random = new Random();
    private Player player;

    public CPU(int skillPoint, int fingerStock, int playerIndex) {
        super(skillPoint, fingerStock, playerIndex);
    }

    public void createCPUMotion(Player player, int totalFingerCount) {
        this.player = player;
        motion = null;
        if (isParent) {
            parentMotion(totalFingerCount);
        } else {
            childMotion();
        }
    }

    private void parentMotion(int totalFingerCount) {
        if (skillPoint == 0) {
            // スキルポイントが0なのでCallするしかない
            randomCall(totalFingerCount);
        } else if (4 < skillPoint) {
            // 70%の確率でスキル発動
            if (3 < random.nextInt() % 10) {
                randomSkill();
            } else {
                randomCall(totalFingerCount);
            }
        } else {
            // 50%の確率でSkill発動
            boolean useCall = random.nextBoolean();
            if (useCall) {
                randomCall(totalFingerCount);
            } else {
                // 発動可能なSkillをランダムに設定
                randomSkill();
            }
        }
    }
    private void childMotion() {
        // もし今の親のスキルポイントが0ならば
        if(player.skillPoint == 0) {
            this.setMotion(randomAction());
        } else  {
            // 今の親のスキルポイントが1以上
            // ランダムな確率でActionか、トラップ発動
            boolean isAction = random.nextBoolean();
            if(isAction) {
                this.setMotion(randomAction());
            } else {
                setSkill(SkillManager.TRAP);
            }
        }
    }

    private Action randomAction() {
        return new Action(random.nextInt(1+getMyFingerCount())); // 0~2までの3つの乱数
    }

    private void randomCall(int totalFingerCount) {
        Action action = randomAction();
        int othersFingersSize = totalFingerCount - getMyFingerCount(); // 自分以外の指の本数
        int myCallCount = action.getStandCount() + random.nextInt(1+othersFingersSize); // コールする数（自分+自分以外の本数を最大値としたランダムな数）
        setMotion(new Call(action, myCallCount));
    }

    private void randomSkill() {
        // 発動可能だったSkillをランダムにセット
        int randomNumber = random.nextInt(this.getAvailableSkillList().size());
        setSkill(randomNumber);
    }
}
