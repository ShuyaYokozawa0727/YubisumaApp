package com.example.yubisumaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yubisumaapp.R;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Button gameStart;
    private Button ruru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        gameStart = findViewById(R.id.startGame);

        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YubisumaActivity.class);
                startActivity(intent);
            }
        });
    }
}
