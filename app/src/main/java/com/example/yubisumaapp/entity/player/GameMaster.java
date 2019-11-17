package com.example.yubisumaapp.entity.player;

import android.util.Log;

import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.Trap;

import java.util.ArrayList;
import java.util.EventListener;

public class GameMaster {
    private int turnCount = 0; // ターン開始時にインクリメント
    private int totalFingerCount = 0; // ターン開始時にチェック

    private int playerSize = 0;
    private int parentPlayerIndex = 0;

    public boolean inGame = false; // ターン終了時にチェック

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> clearedPlayer = new ArrayList<>();

    private Player parentPlayer;

    public GameMaster(int playerSize) {
        inGame = true;
        this.playerSize = playerSize;
        createPlayers(playerSize);
        parentPlayerIndex = 0;
        //parentPlayerIndex = new Random().nextInt(playerSize);
        players.get(parentPlayerIndex).isParent = true;
    }

    // Activityから呼び出される？
    public void startTurn() {
        turnCount++;
        countTotalFingerSize();
        cacheParentPlayer();
        // プレイヤー達の開始処理
        for(Player player : players) {
            player.turnStart();
        }
    }

    // これはActivityでPlayerのMotionが確定したら呼び出される
    public void startBattle() {
        Log.v("GameMaster.startBattle", "バトルスタート");
        determineCPUMotion();
        // 親がどんな動きをしたか
        if(parentPlayer.motion instanceof Call) {
            parentCallProcess();

        } else if (parentPlayer.motion instanceof Skill) {
            parentSkillProcess();

        } else {
            // これはおかしいけどめっちゃ来そう
            Log.v("うわああ", "現在の親のMotionがCallでもSkillでもない。。。");
        }
    }

    public void endTurn() {
        // isParent初期化
        for(Player player : players) {
            player.isParent = false;
        }

        // Clearしたプレイヤーを検索
        // TODO : fingerStockが 0 になった瞬間に playerをクリアさせるEventリスナーを作成する。
        findClearPlayer();

        // isParentをセット
        int nowParentIndex = parentPlayerIndex;
        // 次のParentが見つからなかったらゲーム終了
        if(!findNextParent(nowParentIndex)) {
            // リスナーのメソッドから変更する
            inGame = false;
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
        int standTotalFingerCount = 0;
        for(Player childPlayer : players) {
            // 親プレイヤー以外
            if(!childPlayer.equals(parentPlayer)) {
                if(childPlayer.motion instanceof Action) {
                    standTotalFingerCount+=((Action)childPlayer.motion).getStandCount();
                } else if(childPlayer.motion instanceof Skill) {
                    if(childPlayer.motion instanceof Trap) {
                        // Trap失敗(fingerStockを変更)
                        Log.v("ゲームマスタ", "Trap失敗");
                        childPlayer.skillResult(false);
                    } else {
                        // GTD
                        // Trap以外の防御スキルが実装されたら増える
                    }
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
                    /* TODO : 【仕様の確定】1つでもTrapがあったら失敗か、Trapの数=失敗数かを決める.
                                    前者の場合、成功時(Trap:0)はSKillの1回分の効力発動、失敗時(Trap>1)でも1回分の失敗
                                    後者の場合、多数決的にTrap:1, Action:2なら結果的にfingerStockは1減る(成功)
                     */
                    isSuccess = false;
                }
            }
        }
        parentPlayer.skillResult(isSuccess);
    }

    // クリアしたプレイヤーを見つける
    private void findClearPlayer() {
        for(Player player : players) {
            if(player.fingerStock <= 0) {
                clearedPlayer.add(player);
                players.remove(player);
            }
        }
    }

    // 次の親プレイヤーを見つける
    private boolean findNextParent(int beforeParentIndex) {
        // parentIndexを変更
        parentPlayerIndex = (parentPlayerIndex+1) % playerSize;
        boolean foundNextParent = false;
        // 次の親プレイヤーがいればisParentをセット
        for(Player player : players) {
            if (player.playerIndex == parentPlayerIndex) {
                player.isParent = true;
                foundNextParent = true;
            }
        }
        // いなければ（clearPlayerリストに移動していたら）
        if(!foundNextParent) {
            if(beforeParentIndex != parentPlayerIndex) {
                // 次の親プレイヤーを検索
                foundNextParent = findNextParent(beforeParentIndex);
            }
            // Indexが戻ってきたら、プレイヤーは一人しかいない。
            // そのままfalseを返す
        }
        return foundNextParent;
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
    private void countTotalFingerSize() {
        totalFingerCount = 0;
        for(Player player : players) {
            totalFingerCount += player.getStandableFingerCount();
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
