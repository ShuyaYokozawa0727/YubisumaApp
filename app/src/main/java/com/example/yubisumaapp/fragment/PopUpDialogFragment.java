package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.os.Bundle;

import android.widget.TextView;

import com.example.yubisumaapp.R;

public class PopUpDialogFragment extends BaseCustomDialogFragment {

    // バンドルから取り出すためのキー
    private static final String MESSAGE = "MESSAGE";
    private static final String TITLE = "TITLE";

    // 取り出したデータの受け口
    private String message;
    private String title;

    public static PopUpDialogFragment newInstance(String message, String title) {
        PopUpDialogFragment fragment = new PopUpDialogFragment();
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
