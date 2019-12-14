package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.Motion;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.entity.motion.skill.Trap;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String PARENT_INDEX = "PARENT_INDEX";
    private static final String MOTIONS = "MOTIONS";

    private FragmentTransaction transaction;

    // 取り出したデータの受け口
    private int parentIndex;
    private ArrayList<Motion> motions;

    // Activityに返却するデータ
    private int playerChangeFS=0;
    private int playerChangeSP=0;
    private int opponentChangeFS=0;
    private int opponentChangeSP=0;

    // 必要なデータを用意する
    public static ResultDialogFragment newInstance(int parentIndex, ArrayList<Motion> motions) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(PARENT_INDEX, parentIndex);
        args.putSerializable(MOTIONS, motions);
        // Fragmentの作成
        ResultDialogFragment fragment = new ResultDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        motions = null;
        // Bundleからデータ取り出し
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
        customDialog.setContentView(R.layout.dialog_result);

        // バトル結果を計算
        calculateBattleResult();

        // 結果表示
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("+#;-#");

        String textPCFS = format.format(playerChangeFS);
        String textOCFS = format.format(opponentChangeFS);

        ((TextView)customDialog.findViewById(R.id.changePlayerFingerStockTextView)).setText(textPCFS);
        ((TextView)customDialog.findViewById(R.id.changeOpponentFingerStockTextView)).setText(textOCFS);

        return customDialog;
    }

    private void calculateBattleResult() {
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // 親のmotionを取得
        Motion parentMotion = motions.get(parentIndex);
        // 親はCallしていた
        if(parentMotion instanceof Call) {
            parentCallProcess(parentMotion);
        } else if(parentMotion instanceof Skill) {
            parentSkillProcess(parentMotion);
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

    private void parentCallProcess(Motion parentMotion) {
        int standTotalFingerCount = 0;
        int callCount = ((Call)parentMotion).getCallCount();

        // Motionリストを検索
        for(int index=0; index<motions.size(); index++) {
            Motion motion = motions.get(index);
            if(motion instanceof Action) {
                standTotalFingerCount += ((Action) motion).getStandCount();
            } else if(motion instanceof Call) {
                standTotalFingerCount += ((Call) motion).getAction().getStandCount();
            } else if(motion instanceof Skill) {
                Skill skill = (Skill) motion;
                if(skill instanceof Trap) {
                    // 親はコールしているのでTrap失敗
                    if(index == 0) {
                        transaction.add(PopUpDialogFragment.newInstance("トラップ失敗...", "残念！！"),"PopUp");
                        playerChangeFS = skill.invokeEffect(false);
                    } else {
                        transaction.add(PopUpDialogFragment.newInstance("相手のムダトラップ！！", "あぶねえ！！"),"PopUp");
                        opponentChangeFS = skill.invokeEffect(false);
                    }
                } else {
                    // TODO: Trap以外の防御スキルができたら追加
                }
            }
        }
        // コール成功・失敗
        if(callCount == standTotalFingerCount) {
            if(parentIndex == 0) {
                transaction.add(PopUpDialogFragment.newInstance("コール成功！！", "おいしい！！"),"PopUp");
                playerChangeFS = -1;
            } else {
                transaction.add(PopUpDialogFragment.newInstance("コール阻止失敗！！", "やるなあ！！"),"PopUp");
                opponentChangeFS = -1;
            }
        } else {
            if(parentIndex == 0) {
                transaction.add(PopUpDialogFragment.newInstance("コール失敗！！", "どんまい！！"), "PopUp");
            } else {
                transaction.add(PopUpDialogFragment.newInstance("コール阻止成功！！", "いいね！！"), "PopUp");
            }
        }
    }

    private void parentSkillProcess(Motion parentMotion) {
        Skill parentSkill = (Skill) parentMotion;
        for (int index = 0; index < motions.size(); index++) {
            if (index != parentIndex) {
                Motion childMotion = motions.get(index);
                if (childMotion instanceof Skill) {
                    if (childMotion instanceof Trap) {
                        if (parentIndex == 0) {
                            transaction.add(PopUpDialogFragment.newInstance("スキル失敗！！", "うそだろ？！"),"PopUp");
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
                        transaction.add(PopUpDialogFragment.newInstance("スキル成功！！", "よっしゃ！"),"PopUp");
                        playerChangeFS = parentSkill.invokeEffect(true);
                    } else {
                        transaction.add(PopUpDialogFragment.newInstance("スキル阻止失敗！！", "なにぃ？！"),"PopUp");
                        opponentChangeFS = parentSkill.invokeEffect(true);
                    }
                }
            }
        }
    }

    private ResultDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultDialogFragment.OnFragmentInteractionListener) {
            mListener = (ResultDialogFragment.OnFragmentInteractionListener) context;
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
}
