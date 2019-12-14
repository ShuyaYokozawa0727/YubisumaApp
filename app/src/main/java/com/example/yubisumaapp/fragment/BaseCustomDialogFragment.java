package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

public class BaseCustomDialogFragment extends DialogFragment {
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
        }
        return customDialog;
    }
}
