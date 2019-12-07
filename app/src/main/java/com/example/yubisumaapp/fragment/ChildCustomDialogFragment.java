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

public class ChildCustomDialogFragment extends DialogFragment {

    // バンドルから取り出すためのキー
    private static final String AVAILABLE_SKILL_NAME_ARRAY = "availableSkillNameArray";

    private ChildCustomDialogFragment.OnFragmentInteractionListener mListener;

    public Dialog customDialog;

    private String[] availableSkillNameArray;

    private int usedSkillIndex=0;

    // 必要なデータを用意する
    public static ChildCustomDialogFragment newInstance(String[] availableSkillNameArray) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putStringArray(AVAILABLE_SKILL_NAME_ARRAY, availableSkillNameArray);
        // Fragmentの作成
        ChildCustomDialogFragment fragment = new ChildCustomDialogFragment();
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

            customDialog.setContentView(R.layout.dialog_custom_child);

            // ボタンにリスナーをセット
            customDialog.findViewById(R.id.rightFingerImageButton).setOnClickListener(skillEventListener);
        }
        return customDialog;
    }

    View.OnClickListener skillEventListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final List<Integer> checkedItems = new ArrayList<>();
            checkedItems.add(0);
            new AlertDialog.Builder(getActivity())
                    .setTitle("発動するスキル選択する.")
                    .setSingleChoiceItems(availableSkillNameArray, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                        }
                    })
                    .setPositiveButton("発動する！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            usedSkillIndex = checkedItems.get(0);
                            decidedMotion();
                        }
                    })
                    .setNeutralButton("☆チャージ！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            usedSkillIndex = -1;
                            decidedMotion();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChildCustomDialogFragment.OnFragmentInteractionListener) {
            mListener = (ChildCustomDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void decidedMotion() {
        customDialog.dismiss();
        if (mListener != null) {
            mListener.onChildCustomDialogFragmentInteraction(usedSkillIndex);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChildCustomDialogFragmentInteraction(int usedSkillIndex);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
