package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.ChouChou;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.Trap;
import com.example.yubisumaapp.entity.motion.skill.TsuchiFumazu;

import java.util.ArrayList;

public class BattleDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String PARENT_INDEX = "PARENT_INDEX";
    private static final String MOTIONS = "MOTIONS";

    // 取り出したデータの受け口
    private int parentIndex;
    private ArrayList<Motion> motions;

    // 必要なデータを用意する
    public static BattleDialogFragment newInstance(int parentIndex, ArrayList<Motion> motions) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(PARENT_INDEX, parentIndex);
        args.putSerializable(MOTIONS, motions);
        // Fragmentの作成
        BattleDialogFragment fragment = new BattleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motions = null;
        if (getArguments() != null) {
            parentIndex = getArguments().getInt(PARENT_INDEX);
            // 一応型チェック
            if(getArguments().getSerializable(MOTIONS) != null) {
                if(getArguments().getSerializable(MOTIONS) instanceof ArrayList) {
                    try {
                        if (((ArrayList) getArguments().getSerializable(MOTIONS)).get(0) instanceof Motion) {
                            motions = (ArrayList<Motion>) getArguments().getSerializable(MOTIONS);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        customDialog.setContentView(R.layout.dialog_battle);
        String parentPlayer;
        if(parentIndex == 0) {
            parentPlayer = "あなた";
        } else {
            parentPlayer = "あいて";
        }
        ((TextView)customDialog.findViewById(R.id.parentPlayerTextView)).setText(parentPlayer);

        // TODO: カットイン 「君が親だ！」
        // なんかの処理

        // TODO: カットイン 「君のMotion!」
        // なんかの処理

        // playerのMotionを表示する
        showMotion(0, R.id.playerImageView, R.id.playerActionImageView);

        // ちょっち遅延したいけど～～～
        try{
            Thread.sleep(1);
        }catch(InterruptedException e){
            /* そのエラーのときにする処理 */
            System.err.println("エラーが起きたよ！！" );  // 例
        }

        // TODO: カットイン 「あいてのMotion!」
        // なんかの処理

        // opponentのMotionを表示する
        showMotion(1, R.id.opponentImageView, R.id.opponentActionImageView);

        return customDialog;
    }

    // それぞれのMotionを表示する
    private void showMotion(int playerIndex, int actorID, int countID) {
        Motion motion = motions.get(playerIndex);
        if(motion instanceof Call) {
            Call call = (Call) motion;
            switch(call.getCallCount()) {
                case 0: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.count0); break;
                case 1: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.count1); break;
                case 2: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.count2); break;
                case 3: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.count3); break;
                case 4: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.count4); break;
                default: ((ImageView)customDialog.findViewById(R.id.callImageView)).setImageResource(R.drawable.ic_btn_speak_now);
            }
            // Call内部のAction表示
            setActionImage(actorID, countID, call.getAction().getStandCount());

        } else if (motion instanceof Action) {
            Action action = (Action) motion;
            // Action表示
            setActionImage(actorID, countID, action.getStandCount());

        } else if(motion instanceof Skill) {
            Skill skill = (Skill)motion;
            if(skill instanceof Trap) {
                ((ImageView)customDialog.findViewById(actorID)).setImageResource(R.drawable.wana);
            } else if (skill instanceof TsuchiFumazu) {
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.ashi);
            } else if (skill instanceof ChouChou) {
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.cho);
            } else {
                // new Skill
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.skill_point);
            }
            // Actionの数字を非表示
            setVisibilityAction(playerIndex, countID, View.INVISIBLE);
        } else {
            // ここに入ってくるということはnullか新しいMotion派生クラス
        }
    }

    private void setActionImage(int actorID, int countID, int actionCount) {
        switch (actionCount) {
            case 0:
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.guu_right);
                ((ImageView)customDialog.findViewById(countID)).setImageResource(R.drawable.count0);
                break;
            case 1:
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.good_right);
                ((ImageView)customDialog.findViewById(countID)).setImageResource(R.drawable.count1);
                break;
            case 2:
                ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.two);
                ((ImageView) customDialog.findViewById(countID)).setImageResource(R.drawable.count2);
                break;
            default: ((ImageView) customDialog.findViewById(actorID)).setImageResource(R.drawable.charge);
        }
    }

    private void setVisibilityAction(int playerIndex, int countID, int visibility) {
        if(playerIndex == parentIndex) {
            // CustomDialogのsetVisibilityではないのでキャストは必要
            ((ImageView) customDialog.findViewById(R.id.callImageView)).setVisibility(visibility);
        }
        ((ImageView) customDialog.findViewById(countID)).setVisibility(visibility);
    }

    private BattleDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BattleDialogFragment.OnFragmentInteractionListener) {
            mListener = (BattleDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mListener != null) {
            // java.lang.RuntimeException: Unable to destroy activity {com.example.yubisumaapp/com.example.yubisumaapp.activity.YubisumaActivity}: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            mListener.onDismissBattleDialog();
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        void onDismissBattleDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
