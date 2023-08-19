package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(SplashActivity.this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "================== APP START ==================");

                // 이미 로그인 한 적이 있는지 확인
                if (mAuth.getCurrentUser() != null)
                {
                    Toast.makeText(SplashActivity.this, "자동 로그인 되었습니다.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);

                    Log.d(TAG, "< 로그인 기록 있음 > Email: " + currentUser.getEmail());
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    Log.d(TAG, "< 로그인 기록 없음 >");
                }
                finish();
            }
        },3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
