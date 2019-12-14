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

public class ChildDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String AVAILABLE_SKILL_NAME_ARRAY = "availableSkillNameArray";

    private ChildDialogFragment.OnFragmentInteractionListener mListener;

    private String[] availableSkillNameArray;

    private int usedSkillIndex=0;

    // 必要なデータを用意する
    public static ChildDialogFragment newInstance(String[] availableSkillNameArray) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putStringArray(AVAILABLE_SKILL_NAME_ARRAY, availableSkillNameArray);
        // Fragmentの作成
        ChildDialogFragment fragment = new ChildDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            availableSkillNameArray = getArguments().getStringArray(AVAILABLE_SKILL_NAME_ARRAY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // ボタンにリスナーをセット
        customDialog.setContentView(R.layout.dialog_child);
        customDialog.findViewById(R.id.skillImageButton).setOnTouchListener(skillEventListener);
        customDialog.findViewById(R.id.chargeImageButton).setOnTouchListener(chargeEventListener);
        return customDialog;
    }

    private View.OnTouchListener chargeEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                customDialog.findViewById(R.id.chargeImageButton).setBackgroundColor(0xFFCCFF90);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                customDialog.findViewById(R.id.chargeImageButton).setBackgroundColor(Color.WHITE);
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(0);
                new AlertDialog.Builder(getActivity())
                        .setTitle("☆チャージ")
                        .setMessage("☆1 チャージ！")
                        .setSingleChoiceItems(availableSkillNameArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("チャージ！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usedSkillIndex = -1;
                                decidedMotion();
                            }
                        })
                        .show();
            }
            return false;
        }
    };

    private View.OnTouchListener skillEventListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                customDialog.findViewById(R.id.skillImageButton).setBackgroundColor(0xFFCCFF90);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                customDialog.findViewById(R.id.skillImageButton).setBackgroundColor(Color.WHITE);

                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(0);
                new AlertDialog.Builder(getActivity())
                        .setTitle("スキル選択")
                        .setMessage("◆トラップ: 敵のスキルを反射します")
                        .setSingleChoiceItems(availableSkillNameArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("スキルを発動！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usedSkillIndex = checkedItems.get(0);
                                decidedMotion();
                            }
                        })
                        .show();
            }
            return false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChildDialogFragment.OnFragmentInteractionListener) {
            mListener = (ChildDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void decidedMotion() {
        customDialog.dismiss();
        if (mListener != null) {
            mListener.onDecidedChildMotion(usedSkillIndex);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDecidedChildMotion(int usedSkillIndex);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
