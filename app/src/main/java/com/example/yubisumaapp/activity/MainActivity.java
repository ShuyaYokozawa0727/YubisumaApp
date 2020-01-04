package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.yubisumaapp.realm.GameData;
import com.example.yubisumaapp.realm.RealmHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        RealmHelper realmHelper = new RealmHelper();

        RealmQuery<GameData> query = realmHelper.realm.where(GameData.class);
        RealmResults<GameData> result = query.findAll();

        if(result.size() == 0) {
            // 初回
            startActivity(new Intent(this, SignUpActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }
}
