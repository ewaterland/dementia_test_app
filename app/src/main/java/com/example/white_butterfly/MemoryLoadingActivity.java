package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayoutStates;

public class MemoryLoadingActivity extends AppCompatActivity {
    private static final String TAG = "MemoryLoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_loading);

        Log.w(TAG, "--- MemoryLoadingActivity ---");

        // Intent에서 데이터 가져오기
        int score = getIntent().getIntExtra("score", 0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.w(ConstraintLayoutStates.TAG, "Loding one: " + score);

                Intent intent = new Intent(getApplicationContext(), MemoryResultActivity.class);
                intent.putExtra("score", score); // 데이터를 Intent에 첨부
                startActivity(intent);
            }
        }, 3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}