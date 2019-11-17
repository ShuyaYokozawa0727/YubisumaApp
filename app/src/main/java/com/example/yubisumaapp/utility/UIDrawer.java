package com.example.yubisumaapp.utility;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityBattleBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.player.Player;

import java.util.ArrayList;

public class UIDrawer {

    public static final int ICON_SIZE = 7;
    private static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static LinearLayout.LayoutParams layoutWCParams = new LinearLayout.LayoutParams(WC, WC);

    private ArrayList<ImageView> playerFingerStockIconList=null;
    private ArrayList<ImageView> playerSkillPointIconList=null;
    private ArrayList<ImageView> opponentFingerStockIconList=null;
    private ArrayList<ImageView> opponentSkillPointIconList=null;

    private Context context;
    private ActivityBattleBinding binding;

    public UIDrawer(Context context, ActivityBattleBinding binding) {
        this.context = context;
        this.binding = binding;
        createPlayerFingerStockIconList();
        createPlayerSkillPointIconList();
        createOpponentFingerStockIconList();
        createOpponentSkillPointIconList();
    }

    /*
     * BattleActivityで使われます
     *
     */
    public void setUpPlayerUI(Player player) {
        setUpPlayerIconList(player.fingerStock, player.skillPoint);
        if(player.getMotion()!=null) {
           setPlayerMotionTextView(player.getMotion());
        }
    }

    public void setUpOpponentUI(Player opponent) {
        setUpOpponentIconList(opponent.fingerStock, opponent.skillPoint);
        if(opponent.getMotion()!=null) {
            setOpponentMotionTextView(opponent.getMotion());
        }
    }

    public void setUpPlayerIconList(int fingerStock, int skillPoint) {
        /*
         * 色々問題点が多いこの子たち。
         * まずgetPlayerはクリア済みになるとExceptionが発生します
         * さらに3人以上になるとUI的に厳しい。
         */
        binding.playerFingerStockLayout.removeAllViews();
        binding.playerSkillPointLayout.removeAllViews();
        setPlayerFingerStockIconList(fingerStock);
        setPlayerSkillPointIconList(skillPoint);
    }

    public void setUpOpponentIconList(int fingerStock, int skillPoint) {
        /*
         * 色々問題点が多いこの子。
         * まずgetOpponentはクリア済みになるとExceptionが発生します
         * さらに3人以上になるとUI的に厳しい。
         */
        binding.opponentFingerStockLayout.removeAllViews();
        binding.opponentSkillPointLayout.removeAllViews();
        setOpponentFingerStockIcon(fingerStock);
        setOpponentSkillPointIcon(skillPoint);
    }

    public void setPlayerMotionTextView(Motion playerMotion) {
        String text = "";
        if(playerMotion instanceof Call) {
            Call playerCall = (Call)playerMotion;
            text = "Action : "+ playerCall.getStandCount() + "\nCall : " +playerCall.getCallCount();
        } else if(playerMotion instanceof Action) {
            Action playerAction = (Action)playerMotion;
            text = "Action : " + playerAction.getStandCount();
        } else if(playerMotion instanceof Skill) {
            Skill playerSkill = (Skill)playerMotion;
            text = "Skill : " + playerSkill.getSkillName();
        }
        binding.playerMotionTextView.setText(text);
    }

    public void setOpponentMotionTextView(Motion opponentMotion) {
        String text = "";
        if(opponentMotion instanceof Call) {
            Call opponentCall = (Call)opponentMotion;
            text = "Action : "+ opponentCall.getStandCount() + "\nCall : " +opponentCall.getCallCount();
        } else if(opponentMotion instanceof Action) {
            Action opponentAction = (Action)opponentMotion;
            text = "Action : " + opponentAction.getStandCount();
        } else if(opponentMotion instanceof Skill) {
            Skill opponentSkill = (Skill)opponentMotion;
            text = "Skill : " + opponentSkill.getSkillName();
        }
        binding.opponentMotionTextView.setText(text);
    }

    public  void setPlayerFingerStockIconList(int fingerStock) {
        // fingerStockの数だけセット
        for(int index=0; index < fingerStock; index++) {
            binding.playerFingerStockLayout.addView(playerFingerStockIconList.get(index));
        }
    }

    public  void setPlayerSkillPointIconList(int skillPoint) {
        // fingerStockの数だけセット
        for(int index=0; index < skillPoint; index++) {
            binding.playerSkillPointLayout.addView(playerSkillPointIconList.get(index));
        }
    }

    public void setOpponentFingerStockIcon(int fingerStock) {
        // fingerStockの数だけセット
        for(int index=0; index < fingerStock; index++) {
            binding.opponentFingerStockLayout.addView(opponentFingerStockIconList.get(index));
        }
    }

    public void setOpponentSkillPointIcon(int skillPoint) {
        // fingerStockの数だけセット
        for(int index=0; index < skillPoint; index++) {
            binding.opponentSkillPointLayout.addView(opponentSkillPointIconList.get(index));
        }
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
            imageView.setImageResource(R.drawable.ic_btn_speak_now);
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
            imageView.setImageResource(R.drawable.ic_btn_speak_now);
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
