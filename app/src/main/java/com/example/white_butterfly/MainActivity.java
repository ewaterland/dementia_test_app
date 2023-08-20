package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 광고 배너
    private RecyclerView adRecyclerView;
    private AdAdapter adAdapter;
    private List<Integer> adList;

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 태그
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(TAG, "--- MainActivity ---");

        adRecyclerView = findViewById(R.id.view_ad);
        adList = new ArrayList<>();
        adList.add(R.drawable.icon);
        adList.add(R.drawable.bg_splash);
        adList.add(R.drawable.image_cat);
        adList.add(R.drawable.image_dog);

        adAdapter = new AdAdapter(adList);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adRecyclerView.setAdapter(adAdapter);

        Button btn_dementiaTest = (Button) findViewById(R.id.btn_dementiaTest);
        btn_dementiaTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestMainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_rememberTest = (Button) findViewById(R.id.btn_rememberTest);
        btn_rememberTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Memory01Activity.class);
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
                Intent intent = new Intent(getApplication(), UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            super.onBackPressed(); // 앱 종료
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
