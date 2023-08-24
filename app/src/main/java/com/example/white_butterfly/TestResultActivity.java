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

        int score_cog = getIntent().getIntExtra("score_cog", 0);
        int score_dep = getIntent().getIntExtra("score_dep", 0);

        Log.w(TAG, "치매 점수: " + score_cog);
        Log.w(TAG, "우울증 점수: " + score_dep);

        // 텍스트 뷰
        TextView text_cog_result = findViewById(R.id.text_cog_result);
        TextView text_dep_result = findViewById(R.id.text_dep_result);

        // 점수에 따른 결과 표시
        if (score_cog >= 6)
        {
            text_cog_result.setText("치매 의심");
        }

        if (score_dep >= 5)
        {
            text_dep_result.setText("우울증 의심");
        }


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