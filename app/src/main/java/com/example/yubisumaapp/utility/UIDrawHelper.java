package com.example.yubisumaapp.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.player.Player;

import java.util.ArrayList;

public class UIDrawHelper {

    public static final int ICON_SIZE = 7;
    private static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static LinearLayout.LayoutParams layoutWCParams = new LinearLayout.LayoutParams(WC, WC);

    private ArrayList<ImageView> playerFingerStockIconList=null;
    private ArrayList<ImageView> playerSkillPointIconList=null;
    private ArrayList<ImageView> opponentFingerStockIconList=null;
    private ArrayList<ImageView> opponentSkillPointIconList=null;

    private Context context;
    private ActivityBattleBinding binding;

    private String logText="";

    public UIDrawHelper(Context context, ActivityBattleBinding binding) {
        this.context = context;
        this.binding = binding;
        createPlayerFingerStockIconList();
        createPlayerSkillPointIconList();
        createOpponentFingerStockIconList();
        createOpponentSkillPointIconList();
    }

    /*
     * BattleActivityで使われます
     */
    // プレイヤーごとではなく、必要なパラメータのみにする
    public void setTurnLog(int turn, Player player, Player opponent) {
        // ステータスの変化
        int changePlayerFingerStock = player.fingerStock - player.beforeFingerStock;
        int changePlayerSkillPoint = player.skillPoint - player.beforeSkillPoint;
        // ステータスの変化
        int changeOpponentFingerStock = opponent.fingerStock - opponent.beforeFingerStock;
        int changeOpponentSkillPoint = opponent.skillPoint - opponent.beforeSkillPoint;
        // ログをセット
        String playerChangeStatus = "[P F/S : "+changePlayerFingerStock+"/"+changePlayerSkillPoint+"("+player.getSkillName()+")] ";
        String opponentChangeStatus = "[O F/S : "+changeOpponentFingerStock+"/"+changeOpponentSkillPoint+"("+opponent.getSkillName()+")]";
        logText += (turn + ":" + playerChangeStatus+" - "+opponentChangeStatus + "\n") ;
        binding.logTextView.setText(logText);
    }

    // プレイヤーごとではなく、必要なパラメータのみにする
    public void setUpPlayerUI(Player player) {
        int fingerStock = player.fingerStock;
        int skillPoint = player.skillPoint;
        // IconListを初期化・セット
        binding.playerFingerStockLayout.removeAllViews();
        binding.playerSkillPointLayout.removeAllViews();
        for(int index=0; index < fingerStock; index++) {
            binding.playerFingerStockLayout.addView(playerFingerStockIconList.get(index));
        }
        for(int index=0; index < skillPoint; index++) {
            binding.playerSkillPointLayout.addView(playerSkillPointIconList.get(index));
        }
        // モーションテキストを初期化・セット
        if(player.getMotion()!=null) {
            Motion playerMotion = player.getMotion();
            String text = "";
            if(player.hasCall()) {
                Call playerCall = (Call)playerMotion;
                text = "Action : "+ playerCall.getStandCount() + " / Call : " +playerCall.getCallCount();
            } else if(player.hasAction()) {
                Action playerAction = (Action)playerMotion;
                text = "Action : " + playerAction.getStandCount();
            } else if(player.hasSkill()) {
                Skill playerSkill = (Skill)playerMotion;
                text = "Skill : " + playerSkill.getSkillName();
            }
            binding.playerMotionTextView.setText(text);
        }
    }

    public void setUpOpponentUI(Player opponent) {
        int fingerStock = opponent.fingerStock;
        int skillPoint = opponent.skillPoint;

        // アイコンの初期化
        binding.opponentFingerStockLayout.removeAllViews();
        binding.opponentSkillPointLayout.removeAllViews();
        for(int index=0; index < fingerStock; index++) {
            binding.opponentFingerStockLayout.addView(opponentFingerStockIconList.get(index));
        }
        for(int index=0; index < skillPoint; index++) {
            binding.opponentSkillPointLayout.addView(opponentSkillPointIconList.get(index));
        }
        if(opponent.getMotion()!=null) {
            Motion opponentMotion = opponent.getMotion();
            String text = "";
            if(opponent.hasCall()) {
                Call opponentCall = (Call)opponentMotion;
                text = "Action : "+ opponentCall.getStandCount() + " / Call : " +opponentCall.getCallCount();
            } else if(opponent.hasAction()) {
                Action opponentAction = (Action)opponentMotion;
                text = "Action : " + opponentAction.getStandCount();
            } else if(opponent.hasSkill()) {
                Skill opponentSkill = (Skill)opponentMotion;
                text = "Skill : " + opponentSkill.getSkillName();
            }
            binding.opponentMotionTextView.setText(text);
        }
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    /*
     * privateこのClass内だけ使われています
     */
    private void createPlayerFingerStockIconList() {
        playerFingerStockIconList = new ArrayList<>();
        for(int index = 0; index < ICON_SIZE; index++) {
            // ImageViewのインスタンス生成
            ImageView imageView = new ImageView(context);
            // 画像をセット
            imageView.setImageResource(R.drawable.ic_life_round);
            imageView.setLayoutParams(layoutWCParams);
            // とりあえずリストに追加
            playerFingerStockIconList.add(imageView);
        }
    }

    private void createPlayerSkillPointIconList() {
        playerSkillPointIconList = new ArrayList<>();
        for(int index = 0; index < ICON_SIZE; index++) {
            // ImageViewのインスタンス生成
            ImageView imageView = new ImageView(context);
            // 画像をセット
            imageView.setImageResource(R.drawable.btn_star_big_on);
            imageView.setLayoutParams(layoutWCParams);
            // とりあえずリストに追加
            playerSkillPointIconList.add(imageView);
        }
    }

    private void createOpponentFingerStockIconList() {
        opponentFingerStockIconList = new ArrayList<>();
        for(int index = 0; index < ICON_SIZE; index++) {
            // ImageViewのインスタンス生成
            ImageView imageView = new ImageView(context);
            // 画像をセット
            imageView.setImageResource(R.drawable.ic_life_round);
            imageView.setLayoutParams(layoutWCParams);
            // とりあえずリストに追加
            opponentFingerStockIconList.add(imageView);
        }
    }

    private void createOpponentSkillPointIconList() {
        opponentSkillPointIconList = new ArrayList<>();
        for(int index = 0; index < ICON_SIZE; index++) {
            // ImageViewのインスタンス生成
            ImageView imageView = new ImageView(context);
            // 画像をセット
            imageView.setImageResource(R.drawable.btn_star_big_on);
            imageView.setLayoutParams(layoutWCParams);
            // とりあえずリストに追加
            opponentSkillPointIconList.add(imageView);
        }
    }
}
