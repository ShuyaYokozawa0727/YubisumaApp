package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.yubisumaapp.R;
import java.util.ArrayList;
import java.util.List;

import static com.example.yubisumaapp.utility.YubiSumaUtility.createRangeLabel;

public class ParentCustomDialogFragment extends DialogFragment {

    // 必要なデータを用意する
    public static ParentCustomDialogFragment newInstance(int maxCallCount, int skillPoint, String[] availableSkillNameArray) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(MAX_CALL_COUNT, maxCallCount);
        args.putInt(SKILL_POINT, skillPoint);
        args.putStringArray(AVAILABLE_SKILL_NAME_ARRAY, availableSkillNameArray);
        // Fragmentの作成
        ParentCustomDialogFragment fragment = new ParentCustomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // バンドルから取り出すためのキー
    private static final String MAX_CALL_COUNT = "maxCallCount";
    private static final String SKILL_POINT = "skillPoint";
    private static final String AVAILABLE_SKILL_NAME_ARRAY = "availableSkillNameArray";

    private int maxCallCount;
    private int skillPoint;
    private String[] availableSkillNameArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxCallCount = getArguments().getInt(MAX_CALL_COUNT);
            skillPoint = getArguments().getInt(SKILL_POINT);
            availableSkillNameArray = getArguments().getStringArray(AVAILABLE_SKILL_NAME_ARRAY);
        }
    }

    public Dialog customDialog;

    // ダイアログが表示された時点で呼び出される？？
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customDialog = null;
        if (getActivity() != null) {
            customDialog = new Dialog(getActivity());

            // タイトル非表示
            customDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            // フルスクリーン
            customDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            // 背景を透明にする
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.setCancelable(false);

            customDialog.setContentView(R.layout.dialog_custom_parent);

            // ボタンにリスナーをセット
            customDialog.findViewById(R.id.callImageButton).setOnClickListener(callEventListener);
            customDialog.findViewById(R.id.skillImageButton).setOnClickListener(skillEventListener);

            ((TextView)customDialog.findViewById(R.id.titleCustomDialog)).setText("行動を選択してください");
            ((TextView)customDialog.findViewById(R.id.messageCustomDialog)).setText("マイクボタン : コール \n星ボタン : スキル");
        }
        return customDialog;
    }

    private int callCount=0;
    private int usedSkillIndex=0;

    View.OnClickListener callEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final List<Integer> checkedItems = new ArrayList<>();
            checkedItems.add(0);
            new AlertDialog.Builder(getActivity())
                    .setTitle("コールする数を選択")
                    .setSingleChoiceItems(createRangeLabel(0, maxCallCount), 0, new DialogInterface.OnClickListener() {
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
                                // callCountをActivityに戻す
                                callCount = checkedItems.get(0);
                                // 0: Callモード
                                decidedMotion(0);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show();
        }
    };

    View.OnClickListener skillEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final List<Integer> checkedItems = new ArrayList<>();
            checkedItems.add(0);
            new AlertDialog.Builder(getActivity())
                    .setTitle("発動するスキル選択する. \nSP : " + skillPoint)
                    .setSingleChoiceItems(availableSkillNameArray, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (skillPoint == 0) {
                                // showDialog「選択できません」

                            } else {
                                // メンバ変数に保管
                                usedSkillIndex = checkedItems.get(0);
                                // 1: Skillモード
                                decidedMotion(1);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show();
        }
    };

    private ParentCustomDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentCustomDialogFragment.OnFragmentInteractionListener) {
            mListener = (ParentCustomDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // Acitivityで定義されているonParentCustomDialogFragmentInteractionをフラグメントから実行できる
    // 多分Activityの値をゲットしてこれんじゃないか？？！？！
    public void decidedMotion(int mode) {
        customDialog.dismiss();
        if (mListener != null) {
            mListener.onParentCustomDialogFragmentInteraction(mode, callCount, usedSkillIndex);
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onParentCustomDialogFragmentInteraction(int motionMode, int callCount, int usedSkillIndex);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
