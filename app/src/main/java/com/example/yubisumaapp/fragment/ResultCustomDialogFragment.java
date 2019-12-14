package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.Trap;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultCustomDialogFragment extends BaseCustomDialogFragment {

    // バンドルから取り出すためのキー
    private static final String PARENT_INDEX = "PARENT_INDEX";
    private static final String MOTIONS = "MOTIONS";

    // 取り出したデータの受け口
    private int parentIndex;
    private ArrayList<Motion> motions;

    // Activityに返却するデータ
    private int playerChangeFS=0;
    private int playerChangeSP=0;
    private int opponentChangeFS=0;
    private int opponentChangeSP=0;

    // 必要なデータを用意する
    public static ResultCustomDialogFragment newInstance(int parentIndex, ArrayList<Motion> motions) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(PARENT_INDEX, parentIndex);
        args.putSerializable(MOTIONS, motions);
        // Fragmentの作成
        ResultCustomDialogFragment fragment = new ResultCustomDialogFragment();
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
        customDialog.setContentView(R.layout.dialog_custom_result);

        // バトル結果を計算
        calculateBattleResult();

        // 結果表示
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("+#;-#");

        String textPCFS = format.format(playerChangeFS);
        String textPCSP = format.format(playerChangeSP);
        String textOCFS = format.format(opponentChangeFS);
        String textOCSP = format.format(opponentChangeSP);

        ((TextView)customDialog.findViewById(R.id.changePlayerFingerStockTextView)).setText(textPCFS);
        ((TextView)customDialog.findViewById(R.id.changePlayerSkillPointTextView)).setText(textPCSP);
        ((TextView)customDialog.findViewById(R.id.changeOpponentFingerStockTextView)).setText(textOCFS);
        ((TextView)customDialog.findViewById(R.id.changeOpponentSkillPointTextView)).setText(textOCSP);

        return customDialog;
    }


    private void calculateBattleResult() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // 親のmotionを取得
        Motion parentMotion = motions.get(parentIndex);
        // 親はCallしていた
        if(parentMotion instanceof Call) {

            int standTotalFingerCount = 0;
            int callCount = ((Call)parentMotion).getCallCount();

            // Motionリストを検索
            for(int index=0; index<motions.size(); index++) {
                Motion motion = motions.get(index);
                if(motion instanceof Action) {
                    Action action = (Action) motion;
                    standTotalFingerCount += action.getStandCount();
                } else if(motion instanceof Call) {
                    Call call = (Call) motion;
                    standTotalFingerCount += call.getAction().getStandCount();
                } else if(motion instanceof Skill) {
                    Skill skill = (Skill) motion;
                    if(skill instanceof Trap) {
                        // Trap失敗
                        int trapInvoke = skill.invokeEffect(false);
                        if(index == 0) {
                            transaction.add(PopUpDialogFragment.newInstance("トラップ失敗", "残念！！"),"PopUp");
                            playerChangeFS = trapInvoke;
                        } else {
                            opponentChangeFS = trapInvoke;
                        }
                    } else {
                        // TODO: Trap以外の防御スキルができたら追加
                    }
                }
            }
            // コール成功
            if(callCount == standTotalFingerCount) {
                if(parentIndex == 0) {
                    transaction.add(PopUpDialogFragment.newInstance("コール成功！！", "やった！！"),"PopUp");
                    playerChangeFS = -1;
                } else {
                    transaction.add(PopUpDialogFragment.newInstance("コール失敗！！", "どんまい！！"),"PopUp");
                    opponentChangeFS = -1;
                }
            }
        } else if(parentMotion instanceof Skill) {
            Skill parentSkill = (Skill) parentMotion;
            for (int index = 0; index < motions.size(); index++) {
                if (index != parentIndex) {
                    Motion childMotion = motions.get(index);
                    if (childMotion instanceof Skill) {
                        if (childMotion instanceof Trap) {
                            if (parentIndex == 0) {
                                transaction.add(PopUpDialogFragment.newInstance("トラップ失敗！！", "まじか？！"),"PopUp");
                                playerChangeFS = parentSkill.invokeEffect(false);
                            } else {
                                transaction.add(PopUpDialogFragment.newInstance("トラップ成功！！", "ざまぁぁ！"),"PopUp");
                                opponentChangeFS = parentSkill.invokeEffect(false);
                            }
                        } else {
                            // Trap以外の防御スキル
                        }
                    } else {
                        if (parentIndex == 0) {
                            transaction.add(PopUpDialogFragment.newInstance("スキル成功！！", "やったね！"),"PopUp");
                            playerChangeFS = parentSkill.invokeEffect(true);
                        } else {
                            transaction.add(PopUpDialogFragment.newInstance("スキル失敗！！", "うそだろ？！"),"PopUp");
                            opponentChangeFS = parentSkill.invokeEffect(true);
                        }
                    }
                }
            }
        } else {
            // CallでもSkillでもない違う親のMotion
        }
        // Skillを発動していない人はSPが回復する
        for(int index=0; index<motions.size(); index++) {
            Motion motion = motions.get(index);
            if(!(motion instanceof Skill)) {
                if(index == 0) {
                    playerChangeSP = 1;
                } else {
                    opponentChangeSP = 1;
                }
            }
        }
        transaction.commit();
    }

    private ResultCustomDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultCustomDialogFragment.OnFragmentInteractionListener) {
            mListener = (ResultCustomDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mListener != null) {
            mListener.onDismissResultDialog(playerChangeFS, playerChangeSP, opponentChangeFS, opponentChangeSP);
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        void onDismissResultDialog(int playerChangeFS, int playerChangeSP, int opponentChangeFS, int opponentChangeSP);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
