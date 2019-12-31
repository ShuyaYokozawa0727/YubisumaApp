package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.yubisumaapp.R;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yubisumaapp.realm.GameData;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SignInActivity extends AppCompatActivity {
    private Button gameStart;
    private TextView countText;
    private TextView nameText;
    private Realm realm;

    private GameData gameData;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        gameStart = findViewById(R.id.startGame);
        countText = findViewById(R.id.countTextView);
        nameText = findViewById(R.id.nameTextView);

        // マイグレーションが必要ならrealmファイルを削除
        // データを保持しながらスキーマ変更するならマイグレーション
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        RealmQuery<GameData> query = realm.where(GameData.class);
        RealmResults<GameData> result = query.findAll();
        gameData = result.get(0);

        count = gameData.getCount()+1;
        countText.setText(""+count);
        nameText.setText(gameData.getName());

        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // データの保存
                realm.beginTransaction();
                gameData.setCount(count);
                realm.commitTransaction();
                startActivity(new Intent(getApplicationContext(), YubisumaActivity.class));
            }
        });
    }
}
