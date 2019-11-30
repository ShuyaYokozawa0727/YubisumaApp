package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.yubisumaapp.R;

public class BaseCustomDialogFragment extends DialogFragment {
    protected Dialog dialog;
    protected TextView titleTextView;
    protected TextView messageTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        // タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        titleTextView = dialog.findViewById(R.id.title);
        messageTextView = dialog.findViewById(R.id.message);
        this.setCancelable(false);
        dialog.setContentView(R.layout.dialog_custom);

        // OK ボタンのリスナ
        dialog.findViewById(R.id.callImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.findViewById(R.id.skillImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

    public BaseCustomDialogFragment setMessage(String message) {
        messageTextView.setText(message);
        return this;
    }
    public BaseCustomDialogFragment setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }
}
