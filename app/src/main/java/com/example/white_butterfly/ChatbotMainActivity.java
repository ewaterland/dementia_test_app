package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChatbotMainActivity extends AppCompatActivity {
    private static final String TAG = "ChatbotMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_main);

        Log.w(TAG, "--- ChatbotMainActivity ---");

        // 버튼
        Button boyButton = findViewById(R.id.btn_boy);
        Button girlButton = findViewById(R.id.btn_girl);
        Button professionalButton = findViewById(R.id.btn_professional);

        // 5살 남자 아이
        boyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Chatbot2Activity.class);
                startActivity(intent);
            }
        });

        // 5살 여자 아이
        girlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Chatbot2Activity.class);
                startActivity(intent);
            }
        });

        // 전문가

        professionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Chatbot2Activity.class);
                startActivity(intent);
            }
        });
    }
}