package com.example.yubisumaapp.entity;

import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.Trap;
import com.example.yubisumaapp.entity.player.CPU;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.entity.player.User;

import java.util.ArrayList;
import java.util.Random;

public class GameMaster {
    private static final int USER_INDEX = 0;
    private static final int OPPONENT_INDEX = 1;

    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック
    private int playerSizeAtStart = 0; // 初期参加数
    public int parentIndex; // 親は誰か
    public boolean inGame = true; // ターン終了時にチェック
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> clearPlayers = new ArrayList<>();

    private static final int SKILL_POINT = 2;
    private static final int FINGER_COUNT = 5;

    public GameMaster(int playerSize) {
        createPlayers(playerSize);
        parentIndex = new Random().nextInt(playerSize); // 初期値はランダム
        //parentIndex = USER_INDEX;
        setupNewTurn();
     }

    // 一人対戦
    // プレイヤーを作成する
    private void createPlayers(int playerSize) {
        this.playerSizeAtStart = playerSize;
        players.add(new User(SKILL_POINT, FINGER_COUNT, USER_INDEX));
        players.add(new CPU(SKILL_POINT, FINGER_COUNT, OPPONENT_INDEX));
        /*for(int index=1; index < playerSize; index++) {
            players.add(new CPU(skillPoint, fingerCount, index));
        }*/
    }

    // Activityから呼び出される？
    public void setupNewTurn() {
        turnCount++;
        findNextParent();
        setTotalFingerSize();
        for(Player player: players) {
            player.startNewTurn();
        }
    }

    public void endTurn() {

        for(Player player : players) {
            player.turnEnd();
        }
    }

    public void checkGameEnd() {
        // プレイヤーがクリアしたかチェック
        for(Player player : players) {
            if(player.isClear) clearPlayers.add(player);
        }
        // クリアしたプレイヤーを削除
        for(Player clearPlayer : clearPlayers) {
            players.remove(clearPlayer);
        }
        if(clearPlayers.size() == playerSizeAtStart -1) {
            inGame = false;
        }
    }

    private void findNextParent() {
        parentIndex = (parentIndex + 1) % players.size();
        for(Player player : players) {
            if(player.playerIndex == parentIndex) {
                if(player.isClear) {
                    // 複数プレイヤー対応したらどうなるかな～
                    findNextParent();
                } else {
                    player.isParent = true;
                }
            } else {
                player.isParent = false;
            }
        }
    }

    // CPUのMotionを決定する
    public void determineCPUMotion() {
        // CPUのMotionを決定する
        CPU cpu = (CPU) getOpponent();
        cpu.createCPUMotion(getPlayer(), totalFingerCount);
    }

    // Playerは必ずindex = 0
    public User getPlayer() {
        return (User) players.get(0);
    }

    public Player getParent() {
        Player parentPlayer = null;
        for(Player player : players) {
            if(player.isParent) {
                parentPlayer = player;
            }
        }
        return parentPlayer;
    }

    public ArrayList<Motion> getMotionList() {
        ArrayList<Motion> motions = new ArrayList<>();
        for(Player player : players) {
            motions.add(player.getMotion());
        }
        return motions;
    }

    // 二人用専用
    public Player getOpponent() { return players.get(1); }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getClearPlayers() {
        return clearPlayers;
    }

    // 場の指の本数を数える
    private void setTotalFingerSize() {
        totalFingerCount = 0;
        for(Player player : players) {
            totalFingerCount += player.getMyFingerCount();
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

    public void setupBattle() {
        // プレイヤー達のバトル開始準備
        for(Player player : players) {
            player.setupBattle();
        }
    }

    public static final int NOT_EVENT = -1;
    public static final int P_TRAP_FAULT = 0;
    public static final int P_CALL_FAULT = 1;
    public static final int P_SKILL_FAULT = 2;
    public static final int P_TRAP_SUCCESS = 3;
    public static final int P_CALL_SUCCESS = 4;
    public static final int P_SKILL_SUCCESS = 5;
    public static final int O_TRAP_FAULT = 6;
    public static final int O_CALL_FAULT = 7;
    public static final int O_SKILL_FAULT = 8;
    public static final int O_TRAP_SUCCESS = 9;
    public static final int O_CALL_SUCCESS = 10;
    public static final int O_SKILL_SUCCESS = 11;

    public void createBattleResult() {
        // 変化前の状態を保持
        setupBattle();
        // 親のモーションを確認
        if(getParent().hasCall()) {
            parentCallProcess(getParent().getCall());
        } else if(getParent().hasSkill()) {
            parentSkillProcess(getParent().getSkill());
        } else {
            // CallでもSkillでもない違う親のMotion
        }

        saveScore();

        // Skillを発動していない人はSPが回復する
        for (Player player : players) {
            if (!player.hasSkill()) {
                player.skillPoint++;
            }
        }
    }

    private void saveScore() {
        int score=0;
        switch (getPlayer().getEventID()) {
            case P_TRAP_FAULT:    score=-50; break;
            case O_TRAP_FAULT:    score=1  ; break;
            case P_CALL_SUCCESS:  score=100; break;
            case O_CALL_SUCCESS:  score=1  ; break;
            case P_CALL_FAULT:    score=1  ; break;
            case O_CALL_FAULT:    score=1  ; break;
            case P_SKILL_FAULT:   score=-50; break;
            case P_TRAP_SUCCESS:  score=300; break;
            case P_SKILL_SUCCESS: score=100; break;
            case O_SKILL_SUCCESS: score=1  ; break;
        }
        getPlayer().setScore(score);
    }

    private void parentCallProcess(Call parentCall) {
        int standTotalFingerCount = 0;
        int callCount = parentCall.getCallCount();

        for (Player player : players) {
            if (player.hasAction()) {
                standTotalFingerCount += player.getAction().getStandCount();
            } else if (player.hasCall()) {
                standTotalFingerCount += player.getCall().getAction().getStandCount();
            } else if (player.hasSkill()) {
                Skill skill = player.getSkill();
                // 親はコールなのにトラップを発動した（子の時）
                if (skill instanceof Trap) {
                    player.fingerStock += skill.invokeEffect(false);
                    if(getParent().playerIndex == 0) {
                        //getPlayer().setEventID(O_TRAP_FAULT);
                        //getPlayer().setComment("相手のムダトラップ！！");
                        //getPlayer().setVoice("あぶねえ！！");
                    } else {
                        getPlayer().setEventID(P_TRAP_FAULT);
                        getPlayer().setComment("トラップ失敗！！");
                        getPlayer().setVoice("うわ！！");
                    }
                } else {
                    // TODO: Trap以外の防御スキルができたら追加
                }
            }
        }

        // コール成功・失敗
        if(callCount == standTotalFingerCount) {
            getParent().fingerStock -= 1;
            if(getParent().playerIndex == 0) {
                getPlayer().setEventID(P_CALL_SUCCESS);
                getPlayer().setComment("コール成功！！");
                getPlayer().setVoice("おいしい！！"); // ラッキー！にしよう
            } else {
                getPlayer().setEventID(O_CALL_SUCCESS);
                getPlayer().setComment("コール阻止失敗！！");
                getPlayer().setVoice("やるなあ！！");
            }
        } else {
            if(getParent().playerIndex == 0) {
                getPlayer().setEventID(P_CALL_FAULT);
                getPlayer().setComment("コール失敗！！");
                getPlayer().setVoice("どんまい！！");
            } else {
                getPlayer().setEventID(O_CALL_FAULT);
                getPlayer().setComment("コール阻止成功！！");
                getPlayer().setVoice("いいね！！");
            }
        }
    }

    private void parentSkillProcess(Skill parentSkill) {
        for(Player player : players) {
            if (!player.isParent) {
                if (player.hasSkill()) {
                    if(player.getSkill() instanceof Trap) {
                        // コメント返還
                        if (player.playerIndex == 0) {
                            getPlayer().setEventID(P_TRAP_SUCCESS);
                            getPlayer().setComment("トラップ成功！！");
                            getPlayer().setVoice("ざまぁぁ！！");
                        } else {
                            getPlayer().setEventID(P_SKILL_FAULT);
                            getPlayer().setComment("スキル失敗！！");
                            getPlayer().setVoice("うそだろう？！");
                        }
                        getParent().fingerStock += parentSkill.invokeEffect(false);
                    } else {
                        // TODO: Trap以外の防御スキル
                    }
                } else {
                    if (player.playerIndex == 0) {
                        getPlayer().setEventID(O_SKILL_SUCCESS);
                        getPlayer().setComment("スキル阻止失敗！！");
                        getPlayer().setVoice("なにぃ？！");
                    } else {
                        getPlayer().setEventID(P_SKILL_SUCCESS);
                        getPlayer().setComment("スキル成功！！");
                        getPlayer().setVoice("よっしゃ！！");
                    }
                    getParent().fingerStock += parentSkill.invokeEffect(true);
                }
            }
        }
    }
}
