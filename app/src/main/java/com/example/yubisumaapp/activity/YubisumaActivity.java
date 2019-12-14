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
import android.view.MotionEvent;
import android.view.View;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityYubisumaBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.skill.ChouChou;
import com.example.yubisumaapp.entity.motion.skill.TsuchiFumazu;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.entity.player.Member;
import com.example.yubisumaapp.fragment.BattleDialogFragment;
import com.example.yubisumaapp.fragment.ChildDialogFragment;
import com.example.yubisumaapp.fragment.ParentDialogFragment;
import com.example.yubisumaapp.fragment.PopUpDialogFragment;
import com.example.yubisumaapp.fragment.ResultDialogFragment;
import com.example.yubisumaapp.utility.UIDrawer;

public class YubisumaActivity
        extends AppCompatActivity
        implements ParentDialogFragment.OnFragmentInteractionListener
                 , ChildDialogFragment.OnFragmentInteractionListener
                 , ResultDialogFragment.OnFragmentInteractionListener
{
    // こっから本文
    public static final int ICON_SIZE = 7;

    private GameMaster gameMaster;
    private static int playerSize = 2;

    private ActivityYubisumaBinding binding;
    private UIDrawer UIDrawer;

    private MediaPlayer soundYubisuma
            , soundZero, soundOne, soundTwo, soundThree, soundFour
            , soundTuti, soundChocho;

    private int rightFinger=1, leftFinger=1;

    private boolean playingSound = false;
    private boolean leaveFingers = true;

    private void loadSounds() {
        // 音声ファイルをロード
        soundYubisuma = MediaPlayer.create(this, R.raw.yubisuma);
        soundZero = MediaPlayer.create(this, R.raw.zero);
        soundOne = MediaPlayer.create(this, R.raw.one);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundFour = MediaPlayer.create(this, R.raw.four);
        soundTuti = MediaPlayer.create(this, R.raw.tuti);
        soundChocho = MediaPlayer.create(this, R.raw.chocho);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // コンポーネントを自動でバインディングしてくれる偉い人。
        binding = DataBindingUtil.setContentView(this, R.layout.activity_yubisuma);

        // GameMaster生成
        gameMaster = new GameMaster(playerSize);
        // UIDrawHelper生成
        UIDrawer = new UIDrawer(this, binding);

        // UI関係のセットアップ
        leftFinger = UIDrawer.checkFingerStock(gameMaster.getPlayer().fingerStock);
        UIDrawer.setUpUI(gameMaster.getMembers());

        // ここで一気にロードしておく
        loadSounds();

        // Touchイベントリスナーのセット
        binding.leftFingerImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // キーから指が離されたら連打をオフにする
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftFinger = 0;
                    binding.leftFingerImageButton.setImageResource(R.drawable.guu);
                    if (!playingSound) {
                        binding.wantToDoLeftTextView.setText("");
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftFinger = 1;
                    binding.leftFingerImageButton.setImageResource(R.drawable.good);
                    if (!playingSound) {
                        // 非表示ではなければ
                        if (binding.leftFingerImageButton.getVisibility() != View.INVISIBLE) {
                            binding.wantToDoLeftTextView.setText(R.string.tap_start);
                        }
                    }
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
                    if(!playingSound) {
                        binding.wantToDoRightTextView.setText("");
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    rightFinger = 1;
                    binding.rightFingerImageButton.setImageResource(R.drawable.good_rev);
                    if(!playingSound) {
                        if(binding.rightFingerImageButton.getVisibility() != View.INVISIBLE) {
                            binding.wantToDoRightTextView.setText(R.string.tap_start);
                        }
                    }
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
                        }
                        if (leftFinger==1) {
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
                    showMotionSelectDialogFragment(); // フラグメントから音声再生は呼び出される
                }
                return false;
            }
        });

        // 再生終了イベントリスナー（Actionを確定する）
        soundYubisuma.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 音声終了
                playingSound = false;

                // Actionを決定する
                gameMaster.addAction(new Action(rightFinger+leftFinger));
                gameMaster.determineCPUMotion();

                playSounds();

                // 両者のMotionは確定したのでバトル開始（Fragment表示）
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(BattleDialogFragment.newInstance(gameMaster.parentIndex, gameMaster.getMotionList()),"Battle");
                transaction.commit();
            }

            private void playSounds() {
                // 数のボイス再生
                if(gameMaster.getParent().hasCall()) {
                    switch (gameMaster.getParent().getCall().getCallCount()) {
                        case 0 : soundZero.start(); break;
                        case 1 : soundOne.start(); break;
                        case 2 : soundTwo.start(); break;
                        case 3 : soundThree.start(); break;
                        case 4 : soundFour.start(); break;
                    }
                } else if (gameMaster.getParent().hasSkill()){
                    if(gameMaster.getParent().getSkill() instanceof TsuchiFumazu) {
                        soundTuti.start();
                    } else if(gameMaster.getParent().getSkill() instanceof ChouChou) {
                        soundChocho.start();
                    }
                }
            }

        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(PopUpDialogFragment.newInstance("枠外をタップしてね！", "ポップアップを消したい時"), "PopUp");
        transaction.commit();
    }

    // Motion選択ダイアログ表示
    private void showMotionSelectDialogFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 発動可能なスキル名一覧
        String[] availableSkillNameArray = gameMaster.getPlayer().getAvailableSkillNameArray();
        // 親の場合
        if(gameMaster.getPlayer().isParent) {
            // 必要なステータスをFragmentに渡す
            int totalFingerCount = gameMaster.getTotalFingerCount();
            int skillPoint = gameMaster.getPlayer().skillPoint;
            transaction.add(ParentDialogFragment.newInstance(totalFingerCount, skillPoint, availableSkillNameArray),"Parent");
        } else {
            transaction.add(ChildDialogFragment.newInstance(availableSkillNameArray),"Child");
        }
        transaction.commit();
    }

    // ChildCustomDialogFragmentから呼び出されます。
    // 子としてのMotionが決まった
    @Override
    public void onDecidedChildMotion(int usedSkillIndex) {
        playYubisuma();
        // -1はスキルを発動しない
        gameMaster.getPlayer().setSkill(usedSkillIndex);
    }

    // ParentCustomDialogFragmentから呼び出されます。
    // 親としてのMotionが決まった
    @Override
    public void onDecidedParentMotion(int motionMode, int motionCount) {
        playYubisuma();
        // 0: Callを選択したパターン
        // 1: Skillを選択したパターン
        if(motionMode == 0) gameMaster.getPlayer().setMotion(new Call(motionCount));
        if(motionMode == 1) gameMaster.getPlayer().setSkill(motionCount);
    }

    private void playYubisuma() {
        // 音声再生
        soundYubisuma.start();
        playingSound = true;
        binding.playerMotionTextView.setText("音が流れているよ！");
        binding.opponentMotionTextView.setText("音が流れているよ！");
        // 再生中は消しておく
        binding.wantToDoRightTextView.setText("");
        binding.wantToDoLeftTextView.setText("");
    }

    // ResultDialogFragmentから呼ばれます
    @Override
    public void onDismissResultDialog(int playerChangeFS, int playerChangeSP, int opponentChangeFS, int opponentChangeSP) {
        // 変化を反映
        gameMaster.getPlayer().fingerStock += playerChangeFS;
        gameMaster.getPlayer().skillPoint += playerChangeSP;
        gameMaster.getOpponent().fingerStock += opponentChangeFS;
        gameMaster.getOpponent().skillPoint += opponentChangeSP;

        gameMaster.endTurn();
        // UI更新
        leftFinger = UIDrawer.checkFingerStock(gameMaster.getPlayer().fingerStock);
        UIDrawer.setUpUI(gameMaster.getMembers());
        UIDrawer.setTurnLog(gameMaster.getTurnCount(), gameMaster.getPlayer(), gameMaster.getOpponent());

        // ゲーム終了チェック
        gameMaster.checkGameEnd();

        if(!gameMaster.inGame) {
            showResult();
        } else {
            // 次のターンスタート準備
            gameMaster.setupNewTurn();
        }
    }

    // ゲーム終了
    private void showResult() {
        String message = "";
        // TODO:複数人対応
        for(Member clearMember : gameMaster.getClearMembers()) {
            if (clearMember.isCPU()) {
                message = " おれじぇねぇぇぇえ！！";
            } else {
                message = " 俺！！！";
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("【バトル終了】")
                .setMessage("勝者は" + message)
                .setPositiveButton("バトル再開！", new DialogInterface.OnClickListener() {
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
                .setNeutralButton("アプリ終了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
