package com.example.yubisumaapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.example.yubisumaapp.R;

import java.text.DecimalFormat;

public class EndGameDialogFragment extends BaseDialogFragment {

    // バンドルから取り出すためのキー
    private static final String MESSAGE = "MESSAGE";
    private static final String TITLE = "TITLE";
    private static final String BEFORE = "Before";
    private static final String AFTER = "After";
    private static final String DIFF = "Diff";

    // 取り出したデータの受け口
    private String message;
    private String title;
    private int before;
    private int after;
    private int diff;

    public static EndGameDialogFragment newInstance(String message, String title, int before, int after, int diff) {
        EndGameDialogFragment fragment = new EndGameDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        args.putInt(BEFORE, before);
        args.putInt(AFTER, after);
        args.putInt(DIFF, diff);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
            title = getArguments().getString(TITLE);
            before = getArguments().getInt(BEFORE);
            after = getArguments().getInt(AFTER);
            diff = getArguments().getInt(DIFF);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        customDialog.setContentView(R.layout.dialog_game_end);

        // 結果表示
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("#;-#");

        if(0 < diff) {
            ((TextView)customDialog.findViewById(R.id.signWordTextView)).setText("GET!!");
        } else {
            ((TextView)customDialog.findViewById(R.id.diffScoreTetView)).setTextColor(0xFFD81B60); // 赤
            ((TextView)customDialog.findViewById(R.id.signWordTextView)).setText("LOST...");
        }

        String before = format.format(this.before);
        String after = format.format(this.after);
        String diff = format.format(this.diff);

        ((TextView)customDialog.findViewById(R.id.messagePopUp)).setText(message);
        ((TextView)customDialog.findViewById(R.id.titlePopUp)).setText(title);
        ((TextView)customDialog.findViewById(R.id.beforeScoreTextView)).setText(before);
        ((TextView)customDialog.findViewById(R.id.afterScoreTextView)).setText(after);
        ((TextView)customDialog.findViewById(R.id.diffScoreTetView)).setText(diff);

        return customDialog;
    }
}
