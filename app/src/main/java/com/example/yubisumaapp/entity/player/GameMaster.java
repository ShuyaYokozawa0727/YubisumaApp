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
    private int playerSizeAtStart = 0;
    // 親は誰か
    public int parentIndex;

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
            player.setupTurn();
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
    public User getOpponent() { return (User) players.get(1); }

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
}
