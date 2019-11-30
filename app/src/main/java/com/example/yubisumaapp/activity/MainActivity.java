package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yubisumaapp.R;

public class MainActivity extends AppCompatActivity {

    private Button gameStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameStart = findViewById(R.id.button);
        final Intent intent = new Intent(this, BattleActivity.class);
        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }
}
