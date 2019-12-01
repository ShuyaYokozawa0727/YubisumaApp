package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.SkillManager;
import com.example.yubisumaapp.entity.player.GameMaster;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.fragment.ChildCustomDialogFragment;
import com.example.yubisumaapp.fragment.ParentCustomDialogFragment;
import com.example.yubisumaapp.utility.UIDrawHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.yubisumaapp.utility.YubiSumaUtility.createNumberLabel;
import static com.example.yubisumaapp.utility.YubiSumaUtility.createRangeLabel;

public class BattleActivity extends AppCompatActivity implements ParentCustomDialogFragment.OnFragmentInteractionListener, ChildCustomDialogFragment.OnFragmentInteractionListener {
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
        setUpDisplay();

        showParentDialogFragment();
    }

    private void showParentDialogFragment() {
        // 親の場合のフラグメントを発射するs
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int totalFingerCount = gameMaster.getTotalFingerCount();
        int skillPoint = gameMaster.getPlayer().skillPoint;
        String[] availableSkillNameArray = gameMaster.getPlayer().getAvailableSkillNameArray();
        transaction.add(ParentCustomDialogFragment.newInstance(totalFingerCount, skillPoint, availableSkillNameArray),"a");
        transaction.commit();
    }

    private void showChildDialogFragment() {
        // 親の場合のフラグメントを発射するs
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String[] availableSkillNameArray = gameMaster.getPlayer().getAvailableSkillNameArray();

        transaction.add(ChildCustomDialogFragment.newInstance(availableSkillNameArray),"a");
        transaction.commit();
    }

    private void changeTurnProcess() {
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
            if(gameMaster.getPlayer().isParent) {
                showParentDialogFragment();
            } else {
                showChildDialogFragment();
            }
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

    public void setUpDisplay() {
        UIDrawHelper.setUpPlayerUI(gameMaster.getPlayer());
        UIDrawHelper.setUpOpponentUI(gameMaster.getOpponent());
    }

    @Override
    public void onParentCustomDialogFragmentInteraction(int motionMode, int callCount, int usedSkillIndex) {
        // Call
        if(motionMode == 0){
            gameMaster.getPlayer().setMotion(new Call(0, callCount));
        } else if(motionMode == 1){
            gameMaster.getPlayer().setSkillFromUI(usedSkillIndex);
        } else {
            // やばいですよ！
            throw new IllegalArgumentException("From: BattleActivity.onParentCustomDialogFragmentInteraction\nMotionModeの値が0でも1でもありません。");
        }
        changeTurnProcess();
    }

    @Override
    public void onChildCustomDialogFragmentInteraction(int usedSkillIndex) {
        if(usedSkillIndex == 0) {
            gameMaster.getPlayer().setSkillFromUI(usedSkillIndex);
        }
        changeTurnProcess();
    }
}
