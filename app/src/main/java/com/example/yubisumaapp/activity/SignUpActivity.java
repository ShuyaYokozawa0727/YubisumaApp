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

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SignUpActivity extends AppCompatActivity {
    private Button gameStart;
    private EditText nameEditText;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        gameStart = findViewById(R.id.startGame);
        nameEditText = findViewById(R.id.nameEditText);

        // マイグレーションが必要ならrealmファイルを削除
        // データを保持しながらスキーマ変更するならマイグレーション
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

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
                realm.beginTransaction();
                GameData data = realm.createObject(GameData.class);
                data.setCount(0);
                data.setName(nameEditText.getText().toString());
                realm.commitTransaction();
                startActivity(new Intent(getApplicationContext(), YubisumaActivity.class));
            }
        });
    }
}
