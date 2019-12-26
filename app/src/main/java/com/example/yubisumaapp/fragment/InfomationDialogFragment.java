package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yubisumaapp.R;

public class InfomationDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String MESSAGE = "MESSAGE";
    private static final String TITLE = "TITLE";

    // 取り出したデータの受け口
    private String message;
    private String title;

    public static InfomationDialogFragment newInstance(String message, String title) {
        InfomationDialogFragment fragment = new InfomationDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        customDialog.setContentView(R.layout.dialog_pop_up);
        ((TextView)customDialog.findViewById(R.id.messagePopUp)).setText(message);
        ((TextView)customDialog.findViewById(R.id.titlePopUp)).setText(title);
        return customDialog;
    }
}
