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
    private GameMaster gameMaster;

    public CPU(int skillPoint, int fingerStock, int playerIndex) {
        super(skillPoint, fingerStock, playerIndex);
    }

    void createCPUMotion(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
        motion = null;
        if (isParent) {
            parentMotion();
        }else {
            childMotion();
        }
    }

    private void parentMotion() {
        if (skillPoint == 0) {
            // スキルポイントが0なのでCallするしかない
            randomCall();
        } else {
            // ランダムな確率でCallかSkillか
            boolean isCall = random.nextBoolean();
            if (isCall) {
                randomCall();
            } else {
                // 発動可能なSkillをランダムに設定
                randomSkill();
            }
        }
    }

    private void randomCall() {
        // 自分以外の指の本数
        int othersFingersSize = gameMaster.getTotalFingerCount() - getMyFingerCount();
        // 自分が上げる指の本数
        int myStandFingerCount = random.nextInt(getMyFingerCount());
        // コールする数（自分+自分以外の本数を最大値としたランダムな数）
        int myCallCount = myStandFingerCount + random.nextInt(othersFingersSize);
        this.setMotion(new Call(myStandFingerCount, myCallCount));
    }

    private void randomSkill() {
        // 発動可能だったSkillをランダムにセット
        int randomNumber = random.nextInt(this.getAvailableSkillList().size());
        setSkillFromUI(randomNumber);
    }

    private void childMotion() {
        // もし今の親のスキルポイントが0ならば
        if(searchParentPlayer().skillPoint == 0) {
            this.setMotion(new Action(random.nextInt(getMyFingerCount())));
        } else  {
            // 今の親のスキルポイントが1以上
            // ランダムな確率でActionか、トラップ発動
            boolean isAction = random.nextBoolean();
            if(isAction) {
                this.setMotion(new Action(random.nextInt(getMyFingerCount())));
            } else {
                this.setMotion(SkillManager.defenceSkillList.get(SkillManager.TRAP));
            }
        }
    }

    private Player searchParentPlayer() {
        // 親のskillPoint, fingerStockを取得
        Player parent = null;
        for(Player otherPlayer : gameMaster.getPlayers()) {
            if(otherPlayer.isParent) {
                parent = otherPlayer;
            }
        }
        return parent;
    }
}