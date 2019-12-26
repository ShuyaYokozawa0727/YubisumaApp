package com.example.yubisumaapp.utility;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.databinding.ActivityYubisumaBinding;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.player.CPU;
import com.example.yubisumaapp.entity.player.Player;
import com.example.yubisumaapp.entity.player.User;

import java.util.ArrayList;

import static com.example.yubisumaapp.activity.YubisumaActivity.ICON_SIZE;

public class UIDrawer {

    private static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static LinearLayout.LayoutParams layoutWCParams = new LinearLayout.LayoutParams(WC, WC);

    private ArrayList<ImageView> playerFingerStockIconList=null;
    private ArrayList<ImageView> playerSkillPointIconList=null;
    private ArrayList<ImageView> opponentFingerStockIconList=null;
    private ArrayList<ImageView> opponentSkillPointIconList=null;

    private Context context;
    private ActivityYubisumaBinding binding;



    public UIDrawer(Context context, ActivityYubisumaBinding binding) {
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
    public int checkFingerStock(int fingerStock) {
        // 2以下
        int defaultFinger = 0;

        if(fingerStock <= 0) {
            binding.rightFingerImageButton.setVisibility(View.INVISIBLE);
            binding.wantToDoRightTextView.setText("");
            binding.leftFingerImageButton.setVisibility(View.INVISIBLE);
            binding.wantToDoLeftTextView.setText("");
        } else if(fingerStock == 1) {
            binding.leftFingerImageButton.setVisibility(View.INVISIBLE);
            binding.wantToDoLeftTextView.setText("");
        } else {
            binding.leftFingerImageButton.setVisibility(View.VISIBLE);
            defaultFinger = 1;
        }
        return defaultFinger;
    }

    public void setUpUI(ArrayList<Player> players) {
        // レイアウト初期化
        binding.playerFingerStockLayout.removeAllViews();
        binding.playerSkillPointLayout.removeAllViews();
        binding.opponentFingerStockLayout.removeAllViews();
        binding.opponentSkillPointLayout.removeAllViews();

        // やってほしいことTextをセット
        binding.wantToDoRightTextView.setText(R.string.tap_start);
        binding.wantToDoLeftTextView.setText(R.string.tap_start);
        if(binding.rightFingerImageButton.getVisibility() == View.INVISIBLE) {
            binding.wantToDoRightTextView.setText("");
        }
        if(binding.leftFingerImageButton.getVisibility() == View.INVISIBLE) {
            binding.wantToDoLeftTextView.setText("");
        }

        // プレイヤーごとのUIを更新
        for(Player player : players) {
            int fingerStock = player.fingerStock;
            int skillPoint = player.skillPoint;
            // モーションログテキストを初期化・セット
            String text = "";
            if(player.getMotion()!=null) {
                if(player.hasAction()) {
                    Action playerAction = player.getAction();
                    text = "Action : " + playerAction.getStandCount();
                } else if(player.hasCall()) {
                    Call playerCall = player.getCall();
                    text = "Action : "+ playerCall.getAction().getStandCount() + " / Call : " +playerCall.getCallCount();
                } else if(player.hasSkill()) {
                    Skill playerSkill = player.getSkill();
                    text = "Skill : " + playerSkill.getSkillName();
                }
            }
            // アイコンリストを作成
            if(player instanceof CPU) {
                binding.opponentMotionTextView.setText(text);
                for(int index=0; index < fingerStock; index++) binding.opponentFingerStockLayout.addView(opponentFingerStockIconList.get(index));
                for(int index=0; index < skillPoint; index++) binding.opponentSkillPointLayout.addView(opponentSkillPointIconList.get(index));
            } else {
                binding.playerMotionTextView.setText(text);
                for(int index=0; index < fingerStock; index++) binding.playerFingerStockLayout.addView(playerFingerStockIconList.get(index));
                for(int index=0; index < skillPoint; index++) binding.playerSkillPointLayout.addView(playerSkillPointIconList.get(index));
            }
        }
    }

    private String logText="Turn,P_M_AC_CC_FS_SP_CFS_CSP,O_M_AC_CC_FS_SP_CFS_CSP\n";
    // プレイヤーごとではなく、必要なパラメータのみにする
    public void setTurnLog(int turn, Player user, Player opponent) {
        int playerCall = 0, playerAction = 0;
        String playerMotion = "";
        int opponentCall = 0, opponentAction = 0;
        String opponentMotion = "";

        if (user.hasAction()) {
            playerMotion = "Action";
            playerAction = user.getAction().getStandCount();
        } else if (user.hasCall()) {
            playerMotion = "Call";
            playerCall = user.getCall().getCallCount();
            playerAction = user.getCall().getAction().getStandCount();
        } else if (user.hasSkill()) {
            playerMotion = user.getSkillName();
        } else {
            playerMotion = "???";
        }

        if (opponent.hasAction()) {
            opponentMotion = "Action";
            opponentAction = opponent.getAction().getStandCount();
        } else if (opponent.hasCall()) {
            opponentMotion = "Call";
            opponentCall = opponent.getCall().getCallCount();
            opponentAction = opponent.getCall().getAction().getStandCount();
        } else if (opponent.hasSkill()) {
            opponentMotion = opponent.getSkillName();
        } else {
            opponentMotion = "???";
        }

        // ステータスの変化
        int changePlayerFingerStock = user.fingerStock - user.beforeFingerStock;
        int changePlayerSkillPoint = user.skillPoint - user.beforeSkillPoint;
        // ステータスの変化
        int changeOpponentFingerStock = opponent.fingerStock - opponent.beforeFingerStock;
        int changeOpponentSkillPoint = opponent.skillPoint - opponent.beforeSkillPoint;

        // ログをセット(DBを意識！)
        String playerLog = "P_"+playerMotion+"_"+playerAction+"_"+playerCall+"_";
        playerLog += user.fingerStock+"_"+ user.skillPoint+"_"+changePlayerFingerStock+"_"+changePlayerSkillPoint;
        String opponentLog = "O_"+opponentMotion+"_"+opponentAction+"_"+opponentCall+"_";
        opponentLog += opponent.fingerStock+"_"+opponent.skillPoint+"_"+changeOpponentFingerStock+"_"+changeOpponentSkillPoint;
        logText += (turn + "," + playerLog+","+opponentLog + "\n") ;
        binding.logTextView.setText(logText);
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
            imageView.setImageResource(R.drawable.good_right_icon);
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
            imageView.setImageResource(R.drawable.skill_point);
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
            imageView.setImageResource(R.drawable.good_left_icon);
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
            imageView.setImageResource(R.drawable.skill_point);
            imageView.setLayoutParams(layoutWCParams);
            // とりあえずリストに追加
            opponentSkillPointIconList.add(imageView);
        }
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
