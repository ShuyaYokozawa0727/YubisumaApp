package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yubisumaapp.R;

import java.text.DecimalFormat;

public class ResultCustomDialogFragment extends BaseCustomDialogFragment {

    // バンドルから取り出すためのキー
    private static final String PCFS = "PCFS";
    private static final String PCSP = "PCSP";
    private static final String OCFS = "OCFS";
    private static final String OCSP = "OCSP";

    // 取り出したデータの受け口
    private int playerChangeFS;
    private int playerChangeSP;
    private int opponentChangeFS;
    private int opponentChangeSP;

    // 必要なデータを用意する
    public static ResultCustomDialogFragment newInstance(int playerChangeFS, int playerChangeSP, int opponentChangeFS, int opponentChangeSP) {
        // 引数のセット
        Bundle args = new Bundle();
        args.putInt(PCFS, playerChangeFS);
        args.putInt(PCSP, playerChangeSP);
        args.putInt(OCFS, opponentChangeFS);
        args.putInt(OCSP, opponentChangeSP);
        // Fragmentの作成
        ResultCustomDialogFragment fragment = new ResultCustomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerChangeFS = getArguments().getInt(PCFS);
            playerChangeSP = getArguments().getInt(PCSP);
            opponentChangeFS = getArguments().getInt(OCFS);
            opponentChangeSP = getArguments().getInt(OCSP);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        customDialog.setContentView(R.layout.dialog_custom_result);

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

    private ResultCustomDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ParentCustomDialogFragment.OnFragmentInteractionListener) {
            mListener = (ResultCustomDialogFragment.OnFragmentInteractionListener) context;
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
        // TODO: Update argument type and name
        void onDismissResultDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
