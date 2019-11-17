package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.utility.UIDrawer;

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
    private UIDrawer UIDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // コンポーネントを自動でバインディングしてくれる偉い人。
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battle);

        // GameMaster生成
        gameMaster = new GameMaster(playerSize);
        // ゲーム設定
        gameMaster.startTurn();

        // アイコンリストを作っておく
        UIDrawer = new UIDrawer(this, binding);

        // UIのセット
        UIDrawer.setUpPlayerUI(gameMaster.getPlayer());
        UIDrawer.setUpOpponentUI(gameMaster.getOpponent());

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
        // 終了処理
        gameMaster.endTurn();
        if(gameMaster.inGame) {
            // Player
            UIDrawer.setUpPlayerUI(gameMaster.getPlayer());
            // Opponent
            UIDrawer.setUpOpponentUI(gameMaster.getOpponent());
            // 次のターン開始
            gameMaster.startTurn();
        }
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
                    .setSingleChoiceItems(createNumberLabel(gameMaster.getPlayer().getStandableFingerCount()), DEFAULT_CHECKED, new DialogInterface.OnClickListener() {
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
        final int minNumber = ((Action)gameMaster.getPlayer().getMotion()).getStandCount();
        int maxNumber =  gameMaster.getOpponent().getStandableFingerCount() + minNumber;
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
                            int myFingerUpCount = ((Action)gameMaster.getPlayer().getMotion()).getStandCount();
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
            String[] availableSkillNameArray = gameMaster.getPlayer().getAvailableSkillNameArray();
            new AlertDialog.Builder(context)
                    .setTitle("発動するスキル選択する. \nSP : " + gameMaster.getPlayer().skillPoint)
                    .setSingleChoiceItems(availableSkillNameArray, DEFAULT_CHECKED, new DialogInterface.OnClickListener() {
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
                                int skillIndex = checkedItems.get(0);
                                if(gameMaster.getPlayer().isParent) {
                                    if(gameMaster.getPlayer().isAvailableAttackSkill(skillIndex)) {
                                        gameMaster.getPlayer().setAttackSkill(skillIndex);
                                    }
                                } else {
                                    gameMaster.getPlayer().setDefenceSkill(skillIndex);
                                }
                                changeTurnProcess();
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

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
