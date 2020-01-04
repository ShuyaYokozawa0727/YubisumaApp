package com.example.yubisumaapp.realm;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmHelper {
    public Realm realm;
    public GameData gameData;

    public RealmHelper() {
        // マイグレーションが必要ならrealmファイルを削除
        // データを保持しながらスキーマ変更するならマイグレーション
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
        RealmQuery<GameData> query = realm.where(GameData.class);
        RealmResults<GameData> result = query.findAll();
        gameData = result.get(0);
    }
}
