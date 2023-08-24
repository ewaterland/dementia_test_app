package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestResultActivity extends AppCompatActivity {

    private String TAG = "TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        Log.w(TAG, "--- TestResultActivity ---");

        // 임시 데이터 공간
        int score = getIntent().getIntExtra("Score", 0);

        TextView text_score = findViewById(R.id.text_score);
        text_score.setText(String.format("%d점", score));

        Button btn_main = (Button) findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}