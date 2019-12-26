package com.example.yubisumaapp.entity;

import com.example.yubisumaapp.entity.motion.Action;
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
    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック
    private int playerSizeAtStart = 0; // 初期参加数
    public int parentIndex; // 親は誰か
    public boolean inGame = true; // ターン終了時にチェック
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> clearPlayers = new ArrayList<>();

    public GameMaster(int playerSize) {
        createPlayers(playerSize);
        parentIndex = new Random().nextInt(playerSize); // 初期値はランダム
        setupNewTurn();
     }

    // 一人対戦
    // プレイヤーを作成する
    private void createPlayers(int playerSize) {
        this.playerSizeAtStart = playerSize;
        // 初期設定
        int skillPoint = 2;
        int fingerCount = 5;
        players.add(new User(skillPoint, fingerCount, 0));
        players.add(new CPU(skillPoint, fingerCount, 1));
        /*for(int index=1; index < playerSize; index++) {
            players.add(new CPU(skillPoint, fingerCount, index));
        }*/
    }

    // Activityから呼び出される？
    public void setupNewTurn() {
        turnCount++;
        findNextParent();
        // プレイヤー達の開始処理
        for(Player player : players) {
            player.setupBattle();
        }
        setTotalFingerSize();
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
        parentIndex = (parentIndex +1) % players.size();
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
        // Skillを発動していない人はSPが回復する
        for (Player player : players) {
            if (!player.hasSkill()) {
                player.skillPoint++;
            }
        }
    }

    public void setupBattle() {
        // プレイヤー達のバトル開始準備
        for(Player player : players) {
            player.setupBattle();
        }
    }

    public static final int NOT_EVENT = -1;
    public static final int PLAYER_TRAP_FAULT = 0;
    public static final int PLAYER_CALL_FAULT = 1;
    public static final int PLAYER_SKILL_FAULT = 2;
    public static final int PLAYER_TRAP_SUCCESS = 3;
    public static final int PLAYER_CALL_SUCCESS = 4;
    public static final int PLAYER_SKILL_SUCCESS = 5;
    public static final int OPPONENT_TRAP_FAULT = 6;
    public static final int OPPONENT_CALL_FAULT = 7;
    public static final int OPPONENT_SKILL_FAULT = 8;
    public static final int OPPONENT_TRAP_SUCCESS = 9;
    public static final int OPPONENT_CALL_SUCCESS = 10;
    public static final int OPPONENT_SKILL_SUCCESS = 11;

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
                        //getPlayer().setEventID(OPPONENT_TRAP_FAULT);
                        //getPlayer().setComment("相手のムダトラップ！！");
                        //getPlayer().setVoice("あぶねえ！！");
                    } else {
                        getPlayer().setEventID(PLAYER_TRAP_FAULT);
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
                getPlayer().setEventID(PLAYER_CALL_SUCCESS);
                getPlayer().setComment("コール成功！！");
                getPlayer().setVoice("おいしい！！"); // ラッキー！にしよう
            } else {
                getPlayer().setEventID(OPPONENT_CALL_SUCCESS);
                getPlayer().setComment("コール阻止失敗！！");
                getPlayer().setVoice("やるなあ！！");
            }
        } else {
            if(getParent().playerIndex == 0) {
                getPlayer().setEventID(PLAYER_CALL_FAULT);
                getPlayer().setComment("コール失敗！！");
                getPlayer().setVoice("どんまい！！");
            } else {
                getPlayer().setEventID(OPPONENT_CALL_FAULT);
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
                            getPlayer().setEventID(PLAYER_TRAP_SUCCESS);
                            getPlayer().setComment("トラップ成功！！");
                            getPlayer().setVoice("ざまぁぁ！！");
                        } else {
                            getPlayer().setEventID(PLAYER_SKILL_FAULT);
                            getPlayer().setComment("スキル失敗！！");
                            getPlayer().setVoice("うそだろう？！");
                        }
                        getParent().fingerStock += parentSkill.invokeEffect(false);
                    } else {
                        // TODO: Trap以外の防御スキル
                    }
                } else {
                    if (player.playerIndex == 0) {
                        getPlayer().setEventID(OPPONENT_SKILL_SUCCESS);
                        getPlayer().setComment("スキル阻止失敗！！");
                        getPlayer().setVoice("なにぃ？！");
                    } else {
                        getPlayer().setEventID(PLAYER_SKILL_SUCCESS);
                        getPlayer().setComment("スキル成功！！");
                        getPlayer().setVoice("よっしゃ！！");
                    }
                    getParent().fingerStock += parentSkill.invokeEffect(true);
                }
            }
        }
    }
}
