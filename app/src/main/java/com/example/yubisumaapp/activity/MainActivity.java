package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.yubisumaapp.realm.GameData;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        //realm = Realm.getDefaultInstance();

        // マイグレーションが必要ならrealmファイルを削除
        // データを保持しながらスキーマ変更するならマイグレーション
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);

        RealmQuery<GameData> query = realm.where(GameData.class);
        RealmResults<GameData> result = query.findAll();

        if(result.size() == 0) {
            // 初回
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }
}
