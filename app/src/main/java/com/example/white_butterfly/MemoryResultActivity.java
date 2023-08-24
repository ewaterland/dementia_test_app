package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MemoryResultActivity extends AppCompatActivity {

    private String TAG = "MemoryResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_result);

        Log.w(TAG, "--- MemoryResultActivity ---");

        // 임시 데이터 공간
        int score = getIntent().getIntExtra("Score", 0);

        TextView text_score = findViewById(R.id.text_score);
        text_score.setText(String.format("%d점", score));

        Button btn_main = (Button) findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoryResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}