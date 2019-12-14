package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;

import java.util.ArrayList;
import java.util.Random;

public class GameMaster {
    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック
    // 初期参加数
    private int memberSizeAtStart = 0;
    // 親は誰か
    public int parentIndex;

    public boolean inGame = true; // ターン終了時にチェック

    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Member> clearMembers = new ArrayList<>();

    public GameMaster(int memberSize) {
        createMembers(memberSize);
        parentIndex = new Random().nextInt(memberSize); // 初期値はランダム
        setupNewTurn();
     }

    // 一人対戦
    // プレイヤーを作成する
    private void createMembers(int memberSize) {
        this.memberSizeAtStart = memberSize;
        // 初期設定
        int skillPoint = 2;
        int fingerCount = 5;
        members.add(new Player(skillPoint, fingerCount, 0));
        members.add(new CPU(skillPoint, fingerCount, 1));
        /*for(int index=1; index < playerSize; index++) {
            members.add(new CPU(skillPoint, fingerCount, index));
        }*/
    }

    // Activityから呼び出される？
    public void setupNewTurn() {
        turnCount++;
        findNextParent();
        // プレイヤー達の開始処理
        for(Member member : members) {
            member.setupTurn();
        }
        setTotalFingerSize();
    }

    public void endTurn() {
        for(Member member : members) {
            member.turnEnd();
        }
    }

    public void checkGameEnd() {
        // プレイヤーがクリアしたかチェック
        for(Member member : members) {
            if(member.isClear) clearMembers.add(member);
        }
        // クリアしたプレイヤーを削除
        for(Member clearPlayer : clearMembers) {
            members.remove(clearPlayer);
        }
        if(clearMembers.size() == memberSizeAtStart -1) {
            inGame = false;
        }
    }

    private void findNextParent() {
        parentIndex = (parentIndex +1) % members.size();
        for(Member member : members) {
            if(member.memberIndex == parentIndex) {
                if(member.isClear) {
                    // 複数プレイヤー対応したらどうなるかな～
                    findNextParent();
                } else {
                    member.isParent = true;
                }
            } else {
                member.isParent = false;
            }
        }
    }

    public void addAction(Action action) {

        if (getPlayer().getMotion() == null) {
            // 子だったらActionだけセット
            getPlayer().setMotion(action);
        } else if(getPlayer().getMotion() instanceof Call) {
            // CallしてたらActionを組み込む
            getPlayer().getCall().setAction(action);
        }
    }

    // CPUのMotionを決定する
    public void determineCPUMotion() {
        // CPUのMotionを決定する
        CPU cpu = (CPU) getOpponent();
        cpu.createCPUMotion(getPlayer(), totalFingerCount);
    }

    // Playerは必ずindex = 0
    public Player getPlayer() {
        return (Player) members.get(0);
    }

    public ArrayList<Motion> getMotionList() {
        ArrayList<Motion> motions = new ArrayList<>();
        for(Member member : members) {
            motions.add(member.getMotion());
        }
        return motions;
    }

    // 二人用専用
    public Player getOpponent() { return (Player)members.get(1); }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public ArrayList<Member> getClearMembers() {
        return clearMembers;
    }

    // 場の指の本数を数える
    private void setTotalFingerSize() {
        totalFingerCount = 0;
        for(Member member : members) {
            totalFingerCount += member.getMyFingerCount();
        }
    }

    // 一回計算すれば以降取得はO(1)
    public int getTotalFingerCount() {
        return totalFingerCount;
    }

    // ターン数を返す
    public int getTurnCount() {
        return turnCount;
    }
}
