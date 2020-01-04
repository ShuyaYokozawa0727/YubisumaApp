package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yubisumaapp.R;
import com.example.yubisumaapp.realm.GameData;
import com.example.yubisumaapp.realm.RealmHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SignUpActivity extends AppCompatActivity {
    private Button gameStart;
    private EditText nameEditText;
    private RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        gameStart = findViewById(R.id.startGame);
        nameEditText = findViewById(R.id.nameEditText);

        realmHelper = new RealmHelper();

        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!v.getText().toString().equals("")) {
                    gameStart.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // データの保存
                realmHelper.realm.beginTransaction();
                GameData data = realmHelper.realm.createObject(GameData.class);
                data.setCount(0);
                data.setName(nameEditText.getText().toString());
                realmHelper.realm.commitTransaction();
                startActivity(new Intent(getApplicationContext(), YubisumaActivity.class));
            }
        });
    }
}
