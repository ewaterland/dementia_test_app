package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestMainActivity extends AppCompatActivity {
    // Firebase
    private FirebaseDatabase question_db;
    String path;  // Firebase DB 메인 주소

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // TAG
    private static final String TAG = "TestMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        Log.w(TAG, "--- TestMainActivity ---");

        getData();

        Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestExampleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        // Firebase
        FirebaseApp.initializeApp(TestMainActivity.this);
        question_db = FirebaseDatabase.getInstance();

        try {
            question_db.getReference("C01").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String value = dataSnapshot.getValue(String.class);

                        Log.w(TAG, "질문 세팅 완료");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "질문 세팅 실패");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, String.format("%s", e));
        }
    }

    /*
    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 테스트가 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

     */
}