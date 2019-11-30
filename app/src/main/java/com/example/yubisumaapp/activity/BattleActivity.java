package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.fragment.BaseCustomDialogFragment;
import com.example.yubisumaapp.utility.UIDrawHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.yubisumaapp.utility.YubiSumaUtility.createNumberLabel;
import static com.example.yubisumaapp.utility.YubiSumaUtility.createRangeLabel;

public class BattleActivity extends AppCompatActivity {
    public static final int DEFAULT_CHECKED = 0;

    private Context context;
    private GameMaster gameMaster;
    private static int playerSize = 2;

    private ActivityBattleBinding binding;
    private UIDrawHelper UIDrawHelper;

    private BaseCustomDialogFragment parentCustomDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // コンポーネントを自動でバインディングしてくれる偉い人。
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battle);

        parentCustomDialogFragment = new BaseCustomDialogFragment();
        // GameMaster生成
        gameMaster = new GameMaster(playerSize);
        gameMaster.startTurn();

        // UIのセットアップ
        UIDrawHelper = new UIDrawHelper(this, binding);
        setUpDisplay();

        binding.fingerUpImageButton.setOnClickListener(fingerUpEventHandler);
        binding.skillImageButton.setOnClickListener(skillEventHandler);
    }

    private void changeTurnProcess() {
        parentCustomDialogFragment.show(getSupportFragmentManager(), "aaa");//setMessage("aa").setTitle("aa")
        gameMaster.startBattle();
        setUpDisplay();
        // ステータスの変化をセット
        UIDrawHelper.setTurnLog(gameMaster.getTurnCount(), gameMaster.getPlayer(), gameMaster.getOpponent());
        // 終了処理
        gameMaster.endTurn();

        // 開始処理
        if(gameMaster.inGame) {
            // 次のターン開始
            gameMaster.startTurn();
        } else {
            showResult();
        }
    }

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

        new AlertDialog.Builder(context)
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
                .setNegativeButton("終了する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 遷移先のActivityを指定して、Intentを作成する
                        Intent intent = new Intent(context, MainActivity.class);
                        // 遷移先のアクティビティを起動させる
                        startActivity( intent );
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void setUpDisplay() {
        UIDrawHelper.setUpPlayerUI(gameMaster.getPlayer());
        UIDrawHelper.setUpOpponentUI(gameMaster.getOpponent());
    }

    private View.OnClickListener fingerUpEventHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            binding.fingerUpImageButton.setBackgroundColor(Color.YELLOW);
            final List<Integer> checkedItems = new ArrayList<>();
            checkedItems.add(DEFAULT_CHECKED);
            new AlertDialog.Builder(context)
                    .setTitle("指を上げる数を選択")
                    .setSingleChoiceItems(createNumberLabel(gameMaster.getPlayer().getMyFingerCount()), DEFAULT_CHECKED, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!checkedItems.isEmpty()) {
                                int actionCount = checkedItems.get(0);
                                gameMaster.getPlayer().setMotion(new Action(actionCount));
                                // 親ならコールも
                                if(gameMaster.getPlayer().isParent) {
                                    showCallDialog();
                                } else {
                                    changeTurnProcess();
                                }
                            }
                        }
                    })
                    .setNegativeButton("Cancel", UIDrawHelper.fingerCancelListener)
                    .setCancelable(false)
                    .show();
        }
    };

    private void showCallDialog() {
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(DEFAULT_CHECKED);
        final int minNumber = gameMaster.getPlayer().takeAction().getStandCount();
        int maxNumber =  gameMaster.getOpponent().getMyFingerCount() + minNumber;
        new AlertDialog.Builder(context)
                .setTitle("コールする数を選択")
                .setSingleChoiceItems(createRangeLabel(minNumber,maxNumber), DEFAULT_CHECKED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            int callCount = checkedItems.get(0) + minNumber;
                            int myFingerUpCount = gameMaster.getPlayer().takeAction().getStandCount();
                            gameMaster.getPlayer().setMotion(new Call(myFingerUpCount, callCount));
                            changeTurnProcess();
                        }
                    }
                })
                .setNegativeButton("Cancel", UIDrawHelper.fingerCancelListener)
                .setCancelable(false)
                .show();
    }

    private View.OnClickListener skillEventHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final List<Integer> checkedItems = new ArrayList<>();
            binding.skillImageButton.setBackgroundColor(Color.YELLOW);
            checkedItems.add(DEFAULT_CHECKED);
            new AlertDialog.Builder(context)
                    .setTitle("発動するスキル選択する. \nSP : " + gameMaster.getPlayer().skillPoint)
                    .setSingleChoiceItems(gameMaster.getPlayer().getAvailableSkillNameArray(), DEFAULT_CHECKED, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(gameMaster.getPlayer().isParent && gameMaster.getPlayer().skillPoint == 0) {
                                UIDrawHelper.showAlertDialog("スキルがありません", "");
                                UIDrawHelper.initColorSkill();
                            } else {
                                gameMaster.getPlayer().setSkillFromUI(checkedItems.get(0));
                                changeTurnProcess();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", UIDrawHelper.skillCancelListener)
                    .setCancelable(false)
                    .show();
        }
    };
}
