package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.fragment.ChildCustomDialogFragment;
import com.example.yubisumaapp.fragment.ParentCustomDialogFragment;
import com.example.yubisumaapp.utility.UIDrawHelper;

public class BattleActivity extends AppCompatActivity implements ParentCustomDialogFragment.OnFragmentInteractionListener, ChildCustomDialogFragment.OnFragmentInteractionListener {

    private GameMaster gameMaster;
    private static int playerSize = 2;

    private ActivityBattleBinding binding;
    private UIDrawHelper UIDrawHelper;

    private MediaPlayer soundOne, soundTwo;

    private int rightFinger=1, leftFinger=1;

    private boolean playingSound = false;
    private boolean leaveFingers = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // コンポーネントを自動でバインディングしてくれる偉い人。
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battle);

        // GameMaster生成
        gameMaster = new GameMaster(playerSize);

        // UIのセットアップ
        UIDrawHelper = new UIDrawHelper(this, binding);

        // 音声ファイルをロード
        soundOne = MediaPlayer.create(this, R.raw.conch1);
        soundTwo = MediaPlayer.create(this, R.raw.roll_finish1);

        // Touchイベントリスナーのセット
        binding.leftFingerImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // キーから指が離されたら連打をオフにする
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    leftFinger = 0;
                    binding.leftFingerImageButton.setImageResource(R.drawable.guu);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftFinger = 1;
                    binding.leftFingerImageButton.setImageResource(R.drawable.good);
                }
                return false;
            }
        });

        // DialogFragment表示
        binding.rightFingerImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightFinger = 0;
                    binding.rightFingerImageButton.setImageResource(R.drawable.guu_rev);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    rightFinger = 1;
                    binding.rightFingerImageButton.setImageResource(R.drawable.good_rev);
                }

                /* この辺結構めんどいな整理しよう */
                boolean showDialog = false;
                if(!playingSound) {
                    if(binding.leftFingerImageButton.getVisibility() == View.INVISIBLE) {
                        if (rightFinger==0 && leaveFingers) {
                            showDialog = true;
                        }
                        if (rightFinger==1) {
                            leaveFingers = true;
                        }
                    } else if(binding.rightFingerImageButton.getVisibility() == View.INVISIBLE) {
                        if (leftFinger==0 && leaveFingers) {
                            showDialog = true;
                        }if (leftFinger==1) {
                            leaveFingers = true;
                        }
                    } else {
                        if (rightFinger==0 && leftFinger==0 && leaveFingers) {
                            showDialog = true;
                        }
                        if(rightFinger==1 && leftFinger==1) {
                            leaveFingers = true;
                        }
                    }
                }
                if(showDialog) {
                    leaveFingers = false;
                    showDialogFragment(); // フラグメントから音声再生は呼び出される
                }
                return false;
            }
        });

        // 再生終了イベントリスナー（Actionを確定する）
        soundOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 音声再生
                soundTwo.start();
                // Actionだけがここで決定する
                Action action = new Action(rightFinger+leftFinger);
                if (gameMaster.getPlayer().getMotion() == null) {
                    gameMaster.getPlayer().setMotion(action);
                } else if(gameMaster.getPlayer().hasCall()) {
                    gameMaster.getPlayer().getCall().setAction(action);
                }
                // バトル開始
                gameMaster.startBattle();
                changeTurn();
            }
        });
        // ディスプレイ表示処理
        UIDrawHelper.setUpUI(gameMaster.getPlayers());
    }

    private void changeTurn() {
        // ターンエンド
        playingSound = false;
        gameMaster.endTurn();
        // 変化後のステータスをUIに反映(引数リファクタリング候補)
        UIDrawHelper.setUpUI(gameMaster.getPlayers());
        leftFinger = UIDrawHelper.checkFingerStock(gameMaster.getPlayer().fingerStock);
        UIDrawHelper.setTurnLog(gameMaster.getTurnCount(), gameMaster.getPlayer(), gameMaster.getOpponent());
        // ゲーム終了チェック
        gameMaster.checkPlayers();
        if(!gameMaster.inGame) {
            showResult();
        } else {
            // スタート準備
            gameMaster.setupNextTurn();
        }
    }

    // CustomDialog表示
    private void showDialogFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String[] availableSkillNameArray = gameMaster.getPlayer().getAvailableSkillNameArray();
        // 親の場合
        if(gameMaster.getPlayer().isParent) {
            // 必要なステータスをFragmentに渡す
            int totalFingerCount = gameMaster.getTotalFingerCount();
            int skillPoint = gameMaster.getPlayer().skillPoint;
            transaction.add(ParentCustomDialogFragment.newInstance(totalFingerCount, skillPoint, availableSkillNameArray),"Parent");
        } else {
            transaction.add(ChildCustomDialogFragment.newInstance(availableSkillNameArray),"Child");
        }
        transaction.commit();
    }

    // Fragmentから呼び出されます。
    @Override
    public void onParentCustomDialogFragmentInteraction(int motionMode, int motionCount) {
        // 前のターンでのステータスを保存
        gameMaster.getPlayer().rememberBeforeStatus();
        // 0: Callを選択
        // 1: Skillを選択
        if(motionMode == 0) gameMaster.getPlayer().setMotion(new Call(motionCount));
        if(motionMode == 1) gameMaster.getPlayer().setSkillFromUI(motionCount);
        soundOne.start();
        playingSound = true;
    }

    @Override
    public void onChildCustomDialogFragmentInteraction(int usedSkillIndex) {
        gameMaster.getPlayer().rememberBeforeStatus();
        // -1はスキルを発動しない
        gameMaster.getPlayer().setSkillFromUI(usedSkillIndex);
        soundOne.start();
        playingSound = true;
    }

    // ゲーム終了
    private void showResult() {
        String message = "";
        // TODO:複数人対応
        for(Player clearPlayers : gameMaster.getClearPlayers()) {
            if (clearPlayers.isCPU()) {
                message = "おれじぇねぇぇぇえ！！";
            } else {
                message = "俺！！！！１";
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("【バトル終了】")
                .setMessage("勝者は" + message)
                .setPositiveButton("再開する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("終了する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 遷移先のActivityを指定して、Intentを作成する
                        //Intent intent = new Intent(context, MainActivity.class);
                        // 遷移先のアクティビティを起動させる
                        //startActivity( intent );
                        //finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
