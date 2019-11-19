package com.example.yubisumaapp.entity.player;

import android.util.Log;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.skill.Trap;

import java.util.ArrayList;
import java.util.EventListener;

public class GameMaster {
    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック
    private int playerSizeAtStart = 0;
    private int parentPlayerIndex = 0;

    public boolean inGame = true; // ターン終了時にチェック

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> clearPlayer = new ArrayList<>();

    private Player parentPlayer;

    public GameMaster(int playerSizeAtStart) {
        this.playerSizeAtStart = playerSizeAtStart;
        createPlayers(playerSizeAtStart);
        //parentPlayerIndex = new Random().nextInt(playerSize);
        players.get(parentPlayerIndex).isParent = true;
    }

    // Activityから呼び出される？
    public void startTurn() {
        turnCount++;
        // プレイヤー達の開始処理
        for(Player player : players) {
            player.turnStart();
        }
        cacheTotalFingerSize();
        cacheParentPlayer();
    }

    // これはActivityでPlayerのMotionが確定したら呼び出される
    public void startBattle() {
        Log.v("GameMaster.startBattle", "バトルスタート");
        determineCPUMotion();
        // 親がどんな動きをしたか
        if(parentPlayer.hasCall()) {
            parentCallProcess();

        } else if (parentPlayer.hasSkill()) {
            parentSkillProcess();

        } else {
            // これはおかしいけどめっちゃ来そう
            Log.v("うわああ", "現在の親のMotionがCallでもSkillでもない。。。");
        }
    }

    public void endTurn() {
        for(Player player : players) {
            player.turnEnd();
        }
        for(Player player : players) {
            if(player.isClear) {
                clearPlayer.add(player);
                players.remove(player);
            }
        }
        if(clearPlayer.size() == playerSizeAtStart-1) {
            inGame = false;
        } else {
            findNextParent();
        }
    }

    private void findNextParent() {
        int nextParentIndex = parentPlayerIndex = (parentPlayerIndex+1) % players.size();
        for(Player player : players) {
            if(player.playerIndex == nextParentIndex) {
                if(player.isClear) {
                    // 再起？
                    findNextParent();
                } else {
                    player.isParent=true;
                }
            }
        }
    }

    // 一人対戦
    // プレイヤーを作成する
    private void createPlayers(int playerSize) {
        players.add(new Player(0, 2, 0));
        for(int index=1; index < playerSize; index++) {
            players.add(new CPU(0, 2, index));
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
        for(Player otherPlayer : players) {
            // 自分以外
            if (!otherPlayer.equals(getPlayer())) {
                CPU cpu = (CPU) otherPlayer;
                cpu.createCPUMotion(this);
                Log.v("CPU", cpu.toString());
            }
        }
    }

    // parentがCallだったときの処理
    private void parentCallProcess() {
        int standTotalFingerCount = parentPlayer.takeCall().getCallCount();
        for(Player childPlayer : players) {
            if(childPlayer.hasAction()) {
                standTotalFingerCount+=childPlayer.takeAction().getStandCount();
            } else if(childPlayer.hasSkill()) {
                if(childPlayer.motion instanceof Trap) {
                    childPlayer.skillResult(false);
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

    // 自分を見つける
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

    // 場の指の本数を数える
    private void cacheTotalFingerSize() {
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

    public interface FingerStockListenerInterface extends EventListener {
        public void isPlayerGameOver();
    }
}
