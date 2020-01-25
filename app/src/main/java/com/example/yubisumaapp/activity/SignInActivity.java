package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.yubisumaapp.R;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yubisumaapp.realm.RealmHelper;

public class SignInActivity extends AppCompatActivity {
    private Button gameStart;

    private RealmHelper realmHelper;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        gameStart = findViewById(R.id.startGame);
        TextView countText = findViewById(R.id.countTextView);
        TextView nameText = findViewById(R.id.nameTextView);
        TextView scoreText = findViewById(R.id.scoreTextView);

        realmHelper = new RealmHelper();

        count = realmHelper.gameData.getCount()+1;
        countText.setText(String.valueOf(count));
        nameText.setText(realmHelper.gameData.getName());
        scoreText.setText(String.valueOf(realmHelper.gameData.getScore()));

        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // データの保存
                realmHelper.realm.beginTransaction();
                realmHelper.gameData.setCount(count);
                realmHelper.realm.commitTransaction();
                startActivity(new Intent(getApplicationContext(), YubisumaActivity.class));
            }
        });
    }
}
