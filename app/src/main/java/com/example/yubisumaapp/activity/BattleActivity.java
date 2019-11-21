package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.entity.player.Player;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // コンポーネントを自動でバインディングしてくれる偉い人。
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battle);

        // GameMaster生成
        gameMaster = new GameMaster(playerSize);
        gameMaster.startTurn();

        // UIのセットアップ
        UIDrawHelper = new UIDrawHelper(this, binding);
        setUpPlayerAndOpponentUI();

        binding.motionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // アクションダイアログ表示
                showActionSelectDialog();
            }
        });
    }

    private void changeTurnProcess() {
        gameMaster.startBattle();
        setUpPlayerAndOpponentUI();
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
                .setTitle("【バトル終了】バトルを再開しますか？")
                .setMessage("勝者は" + message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.motionImageButton.setVisibility(View.INVISIBLE);
                    }
                })
                .show();
    }

    private void setUpPlayerAndOpponentUI() {
        UIDrawHelper.setUpPlayerUI(gameMaster.getPlayer());
        UIDrawHelper.setUpOpponentUI(gameMaster.getOpponent());
    }

    private void showActionSelectDialog() {
        String message = "俺じゃねえ";
        if(gameMaster.getPlayer().isParent) {
            message = "俺！！";
        }
        new AlertDialog.Builder(context)
                .setTitle("アクション")
                .setMessage("親 : " + message) // クラス変数で保持
                .setPositiveButton("アクション", fingerUpEventHandler)
                .setNeutralButton("スキル", skillEventHandler)
                .show();
    }

    private DialogInterface.OnClickListener fingerUpEventHandler = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
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
                    .setNegativeButton("Cancel", cancelListener)
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
                .setNegativeButton("Cancel", cancelListener)
                .show();
    }

    private DialogInterface.OnClickListener skillEventHandler = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final List<Integer> checkedItems = new ArrayList<>();
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
                            if (!checkedItems.isEmpty()) {
                                gameMaster.getPlayer().setSkillFromUI(checkedItems.get(0));
                                changeTurnProcess();
                            } else {
                                UIDrawHelper.showAlertDialog("スキルがありません", "");
                            }
                        }
                    })
                    .setNegativeButton("Cancel", cancelListener)
                    .show();
        }
    };

    private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            showActionSelectDialog();
        }
    };
}
