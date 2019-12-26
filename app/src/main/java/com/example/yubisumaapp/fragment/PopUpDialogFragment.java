package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yubisumaapp.R;

public class PopUpDialogFragment extends InfomationDialogFragment {

    // バンドルから取り出すためのキー
    private static final String MESSAGE = "MESSAGE";
    private static final String TITLE = "TITLE";

    public static PopUpDialogFragment newInstance(String message, String title) {
        PopUpDialogFragment fragment = new PopUpDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    private PopUpDialogFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PopUpDialogFragment.OnFragmentInteractionListener) {
            mListener = (PopUpDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mListener != null) {
            mListener.onDismissPopUpDialog();
        }
    }

    // こいつをActivityで継承して
    // onParentCustomDialogFragmentInteractionをOverrideする
    public interface OnFragmentInteractionListener {
        void onDismissPopUpDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
