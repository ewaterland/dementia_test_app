package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 태그
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.w(TAG, "--- SplashActivity ---");

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(SplashActivity.this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "================== APP START ==================");

        // 이미 로그인 한 적이 있는지 확인
        if (mAuth.getCurrentUser() != null) {
            String id = currentUser.getEmail();
            DocumentReference docRef = db.collection("Users").document(id);

            Log.d(TAG, "< 로그인 기록 있음 > Email: " + id);
            Toast.makeText(SplashActivity.this, "자동 로그인 되었습니다.", Toast.LENGTH_LONG).show();

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("Name").equals("")) {
                        Log.d(TAG, "< 이름 입력 기록 없음 > Email: " + id);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_register, new RegisterNameFragment())
                                .commit();

                        findViewById(R.id.page_splash).setVisibility(View.GONE);
                    } else if (documentSnapshot.getString("My").equals("")) {
                        Log.d(TAG, "< 연락처 입력 기록 없음 > Email: " + id);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_register, new RegisterPhoneFragment())
                                .commit();

                        findViewById(R.id.page_splash).setVisibility(View.GONE);
                    } else if (documentSnapshot.getString("Birth").equals("")) {
                        Log.d(TAG, "< 생일 입력 기록 없음 > Email: " + id);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_register, new RegisterBirthFragment())
                                .commit();

                        findViewById(R.id.page_splash).setVisibility(View.GONE);
                    } else if (documentSnapshot.getString("Gender").equals("")) {
                        Log.d(TAG, "< 성별 입력 기록 없음 > Email: " + id);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_register, new RegisterGenderFragment())
                                .commit();

                        findViewById(R.id.page_splash).setVisibility(View.GONE);
                    } else if (documentSnapshot.getString("Address").equals("")) {
                        Log.d(TAG, "< 주소 입력 기록 없음 > Email: " + id);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_register, new RegisterAddressFragment())
                                .commit();

                        findViewById(R.id.page_splash).setVisibility(View.GONE);
                    } else if (documentSnapshot.getLong("School").equals(0)) {

                    }
                    else {
                        Log.d(TAG, "< 모든 정보 입력 완료 > Email: " + id);

                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else
        {
            Log.d(TAG, "< 로그인 기록 없음 >");

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                finish();
            }
        },3000);

         */
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

    /*
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

     */
}

