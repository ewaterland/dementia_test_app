package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MemoryResultActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MemoryResultActivity";
    private ImageView imageView;
    private TextView text_score, text_result2, text_result3;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_result);

        Log.w(TAG, "--- MemoryResultActivity ---");

        // Intent에서 데이터 가져오기
        int score = getIntent().getIntExtra("score", 0);

        Button btn_main = (Button) findViewById(R.id.btn_main);
        imageView = findViewById(R.id.image_score);
        text_score = findViewById(R.id.text_score);
        text_result2 = findViewById(R.id.text_result2);
        text_result3 = findViewById(R.id.text_result3);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(MemoryResultActivity.this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("memory_result");

        Log.w(TAG, "score: " + score);
        text_score.setText(String.format("%d점" + "점!", score));

        if(score == 100)
        {
            text_result2.setText("신의 경지");
            text_result3.setText("당신의 기억력은 \n" + "감히 측정할 수 없습니다.\n");
            onPageTransition(100);
        } else if (score >= 90) {
            text_result2.setText("천재이신가요?");
            text_result3.setText("멘사 들어가도 되겠어요!");
            onPageTransition(90);
        } else if (score >= 80) {
            text_result2.setText("대단하시네요!");
            text_result3.setText("상위 10% 기억력이에요!");
            onPageTransition(80);
        } else if (score >= 70) {
            text_result2.setText("나쁘지않아요");
            text_result3.setText("평균이상의 기억력입니다!");
            onPageTransition(70);
        } else if (score >= 60) {
            text_result2.setText("그냥 그래요");
            text_result3.setText("평균적인 기억력입니다.");
            onPageTransition(60);
        } else if (score >= 50) {
            text_result2.setText("힘내세요");
            text_result3.setText("평균 이하의 기억력이에요!");
            onPageTransition(50);
        } else if (score >= 40) {
            text_result2.setText("아이고, 저런");
            text_result3.setText("분발하셔야겠어요!");
            onPageTransition(40);
        } else {
            text_result2.setText("이럴수가");
            text_result3.setText("다 찍으셨나요?");
            onPageTransition(0);
        }

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoryResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void onPageTransition(int score) {
        // StorageReference를 통해 이미지 파일을 가져옵니다.
        StorageReference fileReference = storageReference.child(String.valueOf(score)).child("image.png");

        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // 이미지가 존재하는 경우, 이미지 다운로드 URL을 가져와 ImageView에 표시
            Glide.with(this).load(uri).into(imageView);
            Log.d(TAG, "결과 이미지 : " + score);
        }).addOnFailureListener(urlFailure -> {
            // 이미지가 존재하지 않는 경우
            Log.d(TAG, "사진 없음");
        });
    }
}