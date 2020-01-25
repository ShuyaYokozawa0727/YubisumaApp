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
import com.example.yubisumaapp.entity.GameMaster;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.entity.player.User;
import com.example.yubisumaapp.fragment.BattleDialogFragment;
import com.example.yubisumaapp.fragment.ChildDialogFragment;
import com.example.yubisumaapp.fragment.EndGameDialogFragment;
import com.example.yubisumaapp.fragment.InfomationDialogFragment;
import com.example.yubisumaapp.fragment.ParentDialogFragment;
import com.example.yubisumaapp.fragment.PopUpDialogFragment;
import com.example.yubisumaapp.fragment.ResultDialogFragment;
import com.example.yubisumaapp.utility.UIDrawer;

public class YubisumaActivity
         extends AppCompatActivity
         implements ParentDialogFragment.OnFragmentInteractionListener
                  , ChildDialogFragment.OnFragmentInteractionListener
                  , BattleDialogFragment.OnFragmentInteractionListener
                  , ResultDialogFragment.OnFragmentInteractionListener
                  , PopUpDialogFragment.OnFragmentInteractionListener
{
    // こっから本文
    public static final int ICON_SIZE = 7;
    private static int playerSize = 2;

    private ActivityYubisumaBinding binding;
    private GameMaster gameMaster;
    private UIDrawer UIDrawer;

    private int rightFinger=1, leftFinger=1;
    private boolean playingSound = false;
    private boolean leaveFingers = true;

    private MediaPlayer
            soundYubisuma, soundBGM
            , soundZero, soundOne, soundTwo, soundThree, soundFour
            , soundTuti, soundChocho
            , abunee, donmai, iine, nanii, oishi, usodaro, uwa, yaruna, yossya, zamaa;

    private void loadSounds() {
        // 音声ファイルをロード
        // BGM
        soundBGM = MediaPlayer.create(this, R.raw.bgm_dropout);
        soundBGM.setVolume(0.6f, 0.6f);
        soundBGM.setLooping(true);
        // Voice
        soundYubisuma = MediaPlayer.create(this, R.raw.yubisuma);
        soundZero = MediaPlayer.create(this, R.raw.zero);
        soundOne = MediaPlayer.create(this, R.raw.one);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundFour = MediaPlayer.create(this, R.raw.four);
        soundTuti = MediaPlayer.create(this, R.raw.tuti);
        soundChocho = MediaPlayer.create(this, R.raw.chocho);
        // \PopUp/
        abunee = MediaPlayer.create(this, R.raw.abune);
        donmai = MediaPlayer.create(this, R.raw.donmai);
        iine = MediaPlayer.create(this, R.raw.iine);
        nanii = MediaPlayer.create(this, R.raw.nanii);
        oishi = MediaPlayer.create(this, R.raw.oishi);
        usodaro = MediaPlayer.create(this, R.raw.usodaro);
        uwa = MediaPlayer.create(this, R.raw.uwa);
        yaruna = MediaPlayer.create(this, R.raw.yaruna);
        yossya = MediaPlayer.create(this, R.raw.yossya);
        zamaa = MediaPlayer.create(this, R.raw.zamaa);
    }

    @Override
    protected void onStart() {
        super.onStart();
        soundBGM.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundBGM.pause();
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
        UIDrawer.setUpUI(gameMaster.getPlayers());

        // ここで一気にロードしておく
        loadSounds();

        // Touchイベントリスナーのセット
        binding.leftFingerImageButton.setOnTouchListener(setOnLeftTouchListener());
        binding.rightFingerImageButton.setOnTouchListener(setOnRightTouchListener());

        // 再生終了イベントリスナー（Actionを確定する）
        soundYubisuma.setOnCompletionListener(setOnCompletionListener());

        // おしらせフラグメントを新しく作成
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(InfomationDialogFragment.newInstance("popupを消すときは枠外をタップしてね！", "お知らせ"), "PopUp");
        transaction.commit();
    }

    /* ここから両手の処理 */
    private View.OnTouchListener setOnLeftTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // キーから指が離されたら連打をオフにする
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftFinger = 0;
                    binding.leftFingerImageButton.setImageResource(R.drawable.guu_right);
                    if (!playingSound) {
                        binding.wantToDoLeftTextView.setText("");
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    leftFinger = 1;
                    binding.leftFingerImageButton.setImageResource(R.drawable.good_right);
                    if (!playingSound) {
                        // 非表示ではなければ
                        if (binding.leftFingerImageButton.getVisibility() != View.INVISIBLE) {
                            binding.wantToDoLeftTextView.setText(R.string.tap_start);
                        }
                    }
                }
                return false;
            }
        };
    }

    private View.OnTouchListener setOnRightTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightFinger = 0;
                    binding.rightFingerImageButton.setImageResource(R.drawable.guu_left);
                    if(!playingSound) {
                        binding.wantToDoRightTextView.setText("");
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    rightFinger = 1;
                    binding.rightFingerImageButton.setImageResource(R.drawable.good_left);
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
                if(showDialog && (!showingBattleDialog && !showingPopUpDialog && !showingResultDialog)) {
                    leaveFingers = false;
                    showMotionSelectDialogFragment(); // フラグメントから音声再生は呼び出される
                }
                return false;
            }
        };
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

    private void playVoices() {
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

    private boolean showingBattleDialog = false;
    private boolean showingPopUpDialog = false;
    private boolean showingResultDialog = false;

    public MediaPlayer.OnCompletionListener setOnCompletionListener() {
        return new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // バトル結果を生成
                // Actionを決定する
                gameMaster.getPlayer().addAction(new Action(rightFinger+leftFinger));
                gameMaster.determineCPUMotion();
                playVoices();
                // 両者のMotionは確定したのでバトル開始（Fragment表示）
                gameMaster.createBattleResult();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(BattleDialogFragment.newInstance(gameMaster.parentIndex, gameMaster.getMotionList()),"Battle");
                transaction.commit();
                showingBattleDialog = true;
                // 音声終了
                playingSound = false;
            }
        };
    }

    @Override
    public void onDismissBattleDialog() {
        showingBattleDialog = false;
        // ポップアップ表示
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(PopUpDialogFragment.newInstance(gameMaster.getPlayer().getVoice(), gameMaster.getPlayer().getComment()), "PopUp");
        // ここで例外発生
        transaction.commit();
        showingPopUpDialog = true;
        // ボイス再生
        switch (gameMaster.getPlayer().getEventID()) {
            case GameMaster.P_TRAP_FAULT: uwa.start(); break; // OK
            case GameMaster.O_TRAP_FAULT: abunee.start(); break; // 呼び出されない？
            case GameMaster.P_CALL_SUCCESS: oishi.start(); break; // OK
            case GameMaster.O_CALL_SUCCESS: yaruna.start(); break; // OK
            case GameMaster.P_CALL_FAULT: donmai.start(); break; // OK
            case GameMaster.O_CALL_FAULT: iine.start(); break; // OK
            case GameMaster.P_SKILL_FAULT: usodaro.start(); break; // OK
            case GameMaster.P_TRAP_SUCCESS: zamaa.start(); break; // OK
            case GameMaster.P_SKILL_SUCCESS: yossya.start(); break; // OK
            case GameMaster.O_SKILL_SUCCESS: nanii.start(); break; // OK
        }
    }

    @Override
    public void onDismissPopUpDialog() {
        showingPopUpDialog = false;
        // おしらせフラグメントを新しく作成
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int changePlayerFingerStock = gameMaster.getPlayer().getDiffFingerStock();
        int changeOpponentFingerStock = gameMaster.getOpponent().getDiffFingerStock();
        transaction.add(ResultDialogFragment.newInstance(changePlayerFingerStock, changeOpponentFingerStock, gameMaster.getPlayer().getDiffTurnScore()), "Result");
        transaction.commit();
        showingResultDialog = true;
    }

    // ResultDialogFragmentから呼ばれます
    @Override
    public void onDismissResultDialog() {
        showingResultDialog = false;

        // 各プレイヤーターン終了処理
        gameMaster.endTurn();
        // UI更新
        leftFinger = UIDrawer.checkFingerStock(gameMaster.getPlayer().fingerStock);
        UIDrawer.setUpUI(gameMaster.getPlayers());
        UIDrawer.setTurnLog(gameMaster.getTurnCount(), gameMaster.getPlayer(), gameMaster.getOpponent());

        // ゲーム終了チェック
        gameMaster.checkGameEnd();
        if (!gameMaster.inGame) {
            showResult();
        } else {
            // 次のターンスタート準備
            gameMaster.setupNewTurn();
        }
    }

    // ゲーム終了
    private void showResult() {
        String message = "";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // TODO:複数人対応
        for(Player clearPlayer : gameMaster.getClearPlayers()) {
            if (clearPlayer.isCPU()) {
                message = " おれじぇねぇぇぇえ！！";
                User user = gameMaster.getPlayer();
                transaction.add(EndGameDialogFragment.newInstance("勝者は" + message, "【バトル終了】", user.startScore, user.getScore(), user.getDiffGameScore()), "EndGame");
            } else {
                message = " 俺！！！";
                User user = (User) clearPlayer;
                transaction.add(EndGameDialogFragment.newInstance("勝者は" + message, "【バトル終了】", user.startScore, user.getScore(), user.getDiffGameScore()), "EndGame");
            }
        }
        transaction.commit();

        new AlertDialog.Builder(this)
                .setTitle("運命の選択")
                .setMessage("ゲームを続けますか？")
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
