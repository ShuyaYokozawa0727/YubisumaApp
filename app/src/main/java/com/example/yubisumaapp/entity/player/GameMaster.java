package com.example.yubisumaapp.entity.player;

import android.util.Log;

import com.example.yubisumaapp.entity.motion.skill.Trap;

import java.util.ArrayList;

public class GameMaster {
    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック
    private int playerSizeAtStart = 0;
    private int parentPlayerIndex = 0;

    public boolean inGame = true; // ターン終了時にチェック

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> clearPlayers = new ArrayList<>();

    private Player parentPlayer;

    public GameMaster(int playerSizeAtStart) {
        this.playerSizeAtStart = playerSizeAtStart;
        createPlayers(playerSizeAtStart);
        // 0が子, 1が親
        parentPlayerIndex = 1;//new Random().nextInt(playerSizeAtStart);
        setupNextTurn();
     }
    // 一人対戦
    // プレイヤーを作成する
    private void createPlayers(int playerSize) {
        int skillPoint = 2;
        int fingerCount = 5;
        players.add(new Player(skillPoint, fingerCount, 0));
        for(int index=1; index < playerSize; index++) {
            players.add(new CPU(skillPoint, fingerCount, index));
        }
    }
    // Activityから呼び出される？
    public void setupNextTurn() {
        turnCount++;
        findNextParent();
        // プレイヤー達の開始処理
        for(Player player : players) {
            player.turnStart();
        }
        setTotalFingerSize();
        cacheParentPlayer();
    }

    // これはActivityでPlayerのMotionが確定したら呼び出される
    public void startBattle() {
        determineCPUMotion();
        // 親がどんな動きをしたか
        if(parentPlayer.hasCall()) {
            parentCallProcess();
        } else if (parentPlayer.hasSkill()) {
            parentSkillProcess();
        }
    }

    public void endTurn() {
        for(Player player : players) {
            player.turnEnd();
        }
    }

    public void checkPlayers() {
        // プレイヤーがクリアしたかチェック
        for(Player player : players) {
            if(player.isClear) clearPlayers.add(player);
        }
        // クリアしたプレイヤーを削除
        for(Player clearPlayer : clearPlayers) {
            players.remove(clearPlayer);
        }
        if(clearPlayers.size() == playerSizeAtStart-1) {
            inGame = false;
        }
    }

    private void findNextParent() {
        parentPlayerIndex = (parentPlayerIndex+1) % players.size();
        for(Player player : players) {
            if(player.playerIndex == parentPlayerIndex) {
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

    // parentPlayerをキャッシュする
    // 以降はメンバの参照だけ
    private void cacheParentPlayer() {
        // parentPlayerをキャッシュ
        for(Player player : players) {
            if (player.isParent) {
                // 親プレイヤーをセット
                parentPlayer = player;
            }
        }
    }

    // CPUのMotionを決定する
    private void determineCPUMotion() {
        // CPUのMotionを決定する
        CPU cpu = (CPU) getOpponent();
        cpu.createCPUMotion(getPlayer(), totalFingerCount);
        Log.v("CPU", cpu.toString());
    }

    // parentがCallだったときの処理
    private void parentCallProcess() {
        int standTotalFingerCount = 0;
        for(Player player : players) {
            if(player.hasAction()) {
                standTotalFingerCount += player.getAction().getStandCount();
            } else if(player.hasCall()) {
                standTotalFingerCount += player.getCall().getAction().getStandCount();
            } else if(player.hasSkill()) {
                if(player.motion instanceof Trap) {
                    player.skillResult(false);
                } else {
                    // TODO: Trap以外の防御スキルができたら追加
                }
            }
        }
        parentPlayer.callResult(standTotalFingerCount);
    }

    // parentがSkillを発動したときの処理
    private void parentSkillProcess() {
        boolean isSuccess = true;
        for(Player childPlayer : players) {
            if(!childPlayer.equals(parentPlayer)) {
                if(childPlayer.motion instanceof Trap) {
                    isSuccess = false;
                }
            }
        }
        parentPlayer.skillResult(isSuccess);
    }

    // Playerは必ずindex = 0
    public Player getPlayer() {
        return players.get(0);
    }

    // 二人用専用
    public Player getOpponent() {
        return players.get(1);
    }

    // playerリストを返す
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
