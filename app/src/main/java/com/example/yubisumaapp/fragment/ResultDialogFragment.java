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
    private static final String PCFS = "PCFS";
    private static final String OCFS = "OCFS";

    private FragmentTransaction transaction;

    // 取り出したデータの受け口
    private int playerChangeFS;
    private int opponentChangeFS;

    // 必要なデータを用意する
    public static ResultDialogFragment newInstance(int playerChangeFS, int opponentChangeFS) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(PCFS, playerChangeFS);
        args.putInt(OCFS, opponentChangeFS);
        // Fragmentの作成
        ResultDialogFragment fragment = new ResultDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerChangeFS = getArguments().getInt(PCFS);
            opponentChangeFS = getArguments().getInt(OCFS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        customDialog.setContentView(R.layout.dialog_result);

        // 結果表示
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("+#;-#");

        String textPCFS = format.format(playerChangeFS);
        String textOCFS = format.format(opponentChangeFS);

        ((TextView)customDialog.findViewById(R.id.changePlayerFingerStockTextView)).setText(textPCFS);
        ((TextView)customDialog.findViewById(R.id.changeOpponentFingerStockTextView)).setText(textOCFS);

        return customDialog;
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
            mListener.onDismissResultDialog();
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        void onDismissResultDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
