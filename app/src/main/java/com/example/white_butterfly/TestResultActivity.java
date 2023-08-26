package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class TestResultActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 변수
    String Score_cog = "";
    String Score_dep = "";
    String Score = "";

    // TAG
    private String TAG = "TestResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        Log.w(TAG, "--- TestResultActivity ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        int score_cog = getIntent().getIntExtra("score_cog", 0);
        int score_dep = getIntent().getIntExtra("score_dep", 0);

        Log.w(TAG, "치매 점수: " + score_cog);
        Log.w(TAG, "우울증 점수: " + score_dep);

        // 텍스트 뷰
        TextView text_cog_result1 = findViewById(R.id.text_cog_result1);
        TextView text_cog_result2 = findViewById(R.id.text_cog_result2);
        TextView text_dep_result1 = findViewById(R.id.text_dep_result1);
        TextView text_dep_result2 = findViewById(R.id.text_dep_result2);

        // 점수에 따른 결과 표시
        if (score_cog >= 6) // 치매 의심
        {
            Score = "치매가 의심돼요";
            text_cog_result1.setText("치매");
            text_cog_result2.setText("가 의심돼요");
            text_cog_result1.setTextColor(ContextCompat.getColor(this, R.color.mint));
            text_cog_result2.setTextColor(ContextCompat.getColor(this, R.color.black));

            if (score_dep >= 5) // 우울증 의심
            {
                Score = "치매와 우울증이 의심돼요";
            }
            else  // 우울증 아님
            {
                text_dep_result1.setText("");
                text_dep_result2.setText("");
            }
        }
        else  // 치매 아님
        {
            if (score_dep >= 5)  // 우울증 의심
            {
                Score = "우울증이 의심돼요";
            }
            else  // 우울증 아님
            {
                text_dep_result1.setText("");
                text_dep_result2.setText("");
                Score = "아주 건강한 상태예요";
            }
        }

        docRef.update("Score", Score);
        Log.w(TAG, "Score: " + Score);

        // 마지막 검사일
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();        // 2023
        int month = currentDate.getMonthValue(); // 8
        int day = currentDate.getDayOfMonth();   // 21

        docRef.update("year", year);
        docRef.update("month", month);
        docRef.update("day", day);

        //docRef.update("Date", String.format("%s년 %s월 %s일", year, month, day));


        // 돌아가기 버튼 누른 경우
        Button btn_main = (Button) findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}