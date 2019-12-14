package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.yubisumaapp.R;
import java.util.ArrayList;
import java.util.List;

import static com.example.yubisumaapp.utility.YubiSumaUtility.createRangeLabel;

public class ParentDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String MAX_CALL_COUNT = "maxCallCount";
    private static final String SKILL_POINT = "skillPoint";
    private static final String AVAILABLE_SKILL_NAME_ARRAY = "availableSkillNameArray";

    // Activityから取得してくるデータ
    private int maxCallCount;
    private int skillPoint;
    private String[] availableSkillNameArray;

    // Activityに返すデータ
    private int motionCount=0;

    private ParentDialogFragment.OnFragmentInteractionListener mListener;

    // 必要なデータを用意する
    public static ParentDialogFragment newInstance(int maxCallCount, int skillPoint, String[] availableSkillNameArray) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(MAX_CALL_COUNT, maxCallCount);
        args.putInt(SKILL_POINT, skillPoint);
        args.putStringArray(AVAILABLE_SKILL_NAME_ARRAY, availableSkillNameArray);
        // Fragmentの作成
        ParentDialogFragment fragment = new ParentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxCallCount = getArguments().getInt(MAX_CALL_COUNT);
            skillPoint = getArguments().getInt(SKILL_POINT);
            availableSkillNameArray = getArguments().getStringArray(AVAILABLE_SKILL_NAME_ARRAY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // ボタンにリスナーをセット
        customDialog.setContentView(R.layout.dialog_parent);
        customDialog.findViewById(R.id.callImageButton).setOnTouchListener(callEventListener);
        customDialog.findViewById(R.id.skillImageButton).setOnTouchListener(skillEventListener);
        return customDialog;
    }

    View.OnTouchListener callEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                customDialog.findViewById(R.id.callImageButton).setBackgroundColor(0xFFCCFF90);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                customDialog.findViewById(R.id.callImageButton).setBackgroundColor(Color.WHITE);
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(0);
                new AlertDialog.Builder(getActivity())
                        .setTitle("コールしたい数を選んでね！")
                        .setSingleChoiceItems(createRangeLabel(0, maxCallCount), 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("コール！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!checkedItems.isEmpty()) {
                                    // callCountをActivityに戻す
                                    motionCount = checkedItems.get(0);
                                    // 0: Callモード
                                    decidedMotion(0);
                                }
                            }
                        })
                        .show();
            }
            return false;
        }
    };

    View.OnTouchListener skillEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                customDialog.findViewById(R.id.skillImageButton).setBackgroundColor(0xFFCCFF90);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                customDialog.findViewById(R.id.skillImageButton).setBackgroundColor(Color.WHITE);
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(0);
                new AlertDialog.Builder(getActivity())
                        .setTitle("発動したいスキルを選んでね")
                        .setSingleChoiceItems(availableSkillNameArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("発動！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (skillPoint == 0) {
                                    showAlertDialog("選択できるスキルがありません！", "てなわけで！ごめんね！");
                                } else {
                                    // メンバ変数に保管
                                    motionCount = checkedItems.get(0);
                                    // 1: Skillモード
                                    decidedMotion(1);
                                }
                            }
                        })
                        .show();
            }
            return false;
        }
    };

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentDialogFragment.OnFragmentInteractionListener) {
            mListener = (ParentDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // Activityで定義されているonParentCustomDialogFragmentInteractionをフラグメントから実行できる
    // 多分Activityの値をゲットしてこれんじゃないか？？！？！
    // 引数にFragmentの値をセットしてあげればActivityに値を返すこともできるね！！素通りだ！
    public void decidedMotion(int mode) {
        customDialog.dismiss();
        if (mListener != null) {
            mListener.onDecidedParentMotion(mode, motionCount);
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDecidedParentMotion(int motionMode, int motionCount);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
