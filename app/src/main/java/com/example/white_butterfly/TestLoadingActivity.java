package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

public class TestLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_loading);

        Log.w(TAG, "--- TestLoadingActivity ---");

        // 애니메이션 리소스 로드
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // 뷰에 애니메이션 적용
        View viewToRotate = findViewById(R.id.image_loading);
        viewToRotate.startAnimation(rotateAnimation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.w(TAG, "================== TEST FINISH ==================");

                Intent intent_result = new Intent(getApplicationContext(), TestResultActivity.class);
                int score_cog = getIntent().getIntExtra("score_cog", 0);
                int score_dep = getIntent().getIntExtra("score_dep", 0);
                intent_result.putExtra("score_cog", score_cog);
                intent_result.putExtra("score_dep", score_dep);
                startActivity(intent_result);
                finish();
            }
        }, 3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}