package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class TestResultGoodActivity extends AppCompatActivity {

    // TAG
    private String TAG = "TestResultGoodActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_good);

        Log.w(TAG, "--- TestResultGoodActivity ---");

        // 치매안심센터 전화 연결 버튼 누른 경우


        // 돌아가기 버튼 누른 경우
        Button btn_main = (Button) findViewById(R.id.btn_done);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestResultGoodActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}