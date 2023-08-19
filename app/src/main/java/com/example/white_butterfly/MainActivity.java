package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final long finishtimeed = 1000;
    private long presstime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestMainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_chatbot = (Button) findViewById(R.id.btn_chatbot);
        btn_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ChatbotActivity.class);
                startActivity(intent);
            }
        });

        Button btn_mypage = (Button) findViewById(R.id.btn_mypage);
        btn_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MypageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            finish();
        } else {
            presstime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
