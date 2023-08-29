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

public class TestResultBadActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_test_result_bad);

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
        TextView text_result = findViewById(R.id.text_result);

        // 점수에 따른 결과 표시
        if (score_cog >= 6) // 치매 의심
        {
            if (score_dep >= 5) // 우울증 의심
            {
                Score = "치매와 우울증이 의심됩니다";
                //text_result.setText("치매와 우울증이 의심됩니다");  // (디폴트값)
            }
            else  // 우울증 아님
            {
                Score = "치매가 의심됩니다";
                text_result.setText("치매가 의심됩니다");
            }
        }
        else  // 치매 아님, 그럼 우울증
        {
            text_result.setText("우울증이 의심돼요");
            Score = "우울증이 의심됩니다";
        }

        docRef.update("Score", Score);
        Log.w(TAG, "Score: " + Score);


        // 치매안심센터와 전화 연결 버튼 누른 경우


        // 돌아가기 버튼 누른 경우
        Button btn_main = (Button) findViewById(R.id.btn_done);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestResultBadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}