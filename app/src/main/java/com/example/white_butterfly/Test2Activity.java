package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Test2Activity extends AppCompatActivity implements CurrentPageListener {

    private ProgressModel progressModel;
    private ProgressBar progressBar;

    // Firebase Database 인스턴스 생성
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    public int currentPage = 0;  // 테스트 페이지 추적 (0~24)
    private int currentQuestion = 1; // (1~25)

    String path;
    int button_number = 0;  // 정답이 들어갈 버튼 랜덤 선택
    String temp;

    TextView text_q_num; // 현재 질문 개수
    TextView text_question;  // 질문 텍스트뷰
    ImageView image_question; // 질문 이미지뷰
    Button btn_reply1;
    Button btn_reply2;
    Button btn_reply3;
    Random random;
    TextView text_remember;
    int R_randomNumber1; // 정답 외의 선택지 답변1 랜덤
    int R_randomNumber2; // 정답 외의 선택지 답변2 랜덤
    List<Integer> numberList;
    String value = "";  // 정답 임시값
    String value1 = "";  // 답변1 임시값
    String value2 = "";  // 답변2 임시값
    String address = "";

    @Override
    public void onPageUpdated(int currentPage) {
        this.currentPage = currentPage;
        Log.w(TAG, "TestActivity currentPage: " + currentPage);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.w(TAG, "--- Test2Activity ---");

        text_q_num = findViewById(R.id.text_q_num);
        text_question = findViewById(R.id.text_question);
        image_question = findViewById(R.id.image_animal);
        text_remember = findViewById(R.id.text_remember);

        btn_reply1 = findViewById(R.id.btn_reply1);
        btn_reply2 = findViewById(R.id.btn_reply2);
        btn_reply3 = findViewById(R.id.btn_reply3);

        random = new Random();

        // Firebase
        FirebaseApp.initializeApp(Test2Activity.this);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 현재 로그인 된 유저 정보 읽기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String id = currentUser.getEmail();
        DocumentReference docRef = db.collection("Users").document(id);

        currentQuestion = currentPage+1;

        String path = "Q" + String.format("%02d", currentQuestion);
        Log.w(TAG, "질문 " + path);

        db_q();

        progressBar = findViewById(R.id.progress);
        progressModel = new ViewModelProvider(Test2Activity.this).get(ProgressModel.class);

        // 사용자의 주소 가져오기
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                {
                    address = documentSnapshot.getString("Address");
                }
                else {
                    Log.w(TAG, "Address 경로가 존재하지 않음");
                }
            }
        });
    }

    public void db_q() {
        Log.w(TAG, "# db_q");
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3));

        path = "Q" + String.format("%02d", currentQuestion);
        Log.w(TAG, "질문 " + path);

        // 질문 표시
        question();

        switch (currentQuestion)
        {
            case 9:
            case 10:
            case 11:
                image_question.setAlpha(0f);
                answer_n5();
                break;
            case 12:
                answer_12();
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                answer_1314151617();
                break;
            default:
                answer_n11();
                break;
        }
    }

    private void question() {
        // 질문 불러오기
        database.getReference(path).child("Q").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    text_question.setText(value);
                    Log.w(TAG, "질문 세팅 완료");
                } else {
                    Log.w(TAG, "질문 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "질문 데이터 읽기 실패");
            }
        });
    }

    private void answer()  // 정답 데이터 추출
    {
        // 정답 불러오기
        database.getReference(path).child("A").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value = dataSnapshot.getValue(String.class);
                    button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
                    numberList.remove(Integer.valueOf(button_number));
                    Log.w(TAG, "정답 numberList: " + numberList);

                    button_set(button_number, value);

                    Log.w(TAG, "정답 세팅 완료");
                } else {
                    Log.w(TAG, "정답 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "답변 데이터 읽기 실패");
            }
        });
    }
    private void answer1()  // 나머지 답변1에 들어갈 데이터 추출
    {
        temp = "R" + String.format("%02d", R_randomNumber1);
        database.getReference(path).child(temp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value1 = dataSnapshot.getValue(String.class);
                    button_number = numberList.get(0);
                    numberList.remove(Integer.valueOf(button_number));
                    Log.w(TAG, "답변1 numberList: " + numberList);
                    button_set(button_number, value1);
                } else {
                    Log.w(TAG, "답변1 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "답변1 데이터 읽기 실패");
            }
        });
    }

    private void answer2()  // 나머지 답변1에 들어갈 데이터 추출
    {
        temp = "R" + String.format("%02d", R_randomNumber2);
        database.getReference(path).child(temp).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value1 = dataSnapshot.getValue(String.class);
                    button_number = numberList.get(0);
                    numberList.remove(Integer.valueOf(button_number));
                    Log.w(TAG, "답변1 numberList: " + numberList);
                    button_set(button_number, value2);
                } else {
                    Log.w(TAG, "답변2 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "답변2 데이터 읽기 실패");
            }
        });
    }

    private void button_set(int button_number, String value)
    {
        switch (button_number)
        {
            case 1:
                btn_reply1.setText(value);
                break;
            case 2:
                btn_reply2.setText(value);
                break;
            case 3:
                btn_reply3.setText(value);
                break;
        }
    }

    private void answer_n11()
    {
        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        do {
            R_randomNumber1 = random.nextInt(11) + 1; // 1~11 중 하나를 랜덤으로 선택
            answer1();

        } while (value1.equals(value));  // 정답과 다를 때까지


        // 나머지 답변2 불러오기
        do {
            R_randomNumber2 = random.nextInt(11) + 1; // 1~11 중 하나를 랜덤으로 선택
            answer2();
        } while ((value2.equals(value)) || (value2.equals(value1)));  // 정답과 다를 때까지
    }

    // 객관식 답 5개
    private void answer_n5()
    {
        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        R_randomNumber1 = random.nextInt(5) + 1; // 1~5 중 하나를 랜덤으로 선택
        answer1();

        // 나머지 답변2 불러오기
        R_randomNumber2 = random.nextInt(5) + 1; // 1~5 중 하나를 랜덤으로 선택
        answer2();
    }

    // 아까 기억했던 단어 3개를 말해주세요.
    private void answer_12()
    {

    }

    // 계산 문제
    private void answer_1314151617()
    {
        // 정답 불러오기
        database.getReference(path).child("A").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    value = dataSnapshot.getValue(String.class);
                    button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
                    numberList.remove(Integer.valueOf(button_number));
                    Log.w(TAG, "정답 numberList: " + numberList);

                    if (button_number == 1)
                    {
                        btn_reply1.setText(value);
                    }
                    else if (button_number == 2)
                    {
                        btn_reply2.setText(value);
                    }
                    else if (button_number == 3)
                    {
                        btn_reply3.setText(value);
                    }
                    Log.w(TAG, "정답 세팅 완료");
                } else {
                    Log.w(TAG, "정답 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "답변 데이터 읽기 실패");
            }
        });


        // 나머지 답변1 불러오기
        do {
            value1 = String.valueOf(random.nextInt(50) + 50);  // 50~100 사이의 랜덤값
        } while (value1.equals(value));

        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        if (button_number == 1)
        {
            btn_reply1.setText(value1);
        }
        else if (button_number == 2)
        {
            btn_reply2.setText(value1);
        }
        else if (button_number == 3)
        {
            btn_reply3.setText(value1);
        }
        Log.w(TAG, "답변1 세팅 완료");


        // 나머지 답변2 불러오기
        do {
            value2 = String.valueOf(random.nextInt(50) + 50);  // 50~100 사이의 랜덤값
        } while (value2.equals(value) || value2.equals(value1));

        button_number = numberList.get(0); // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        if (button_number == 1)
        {
            btn_reply1.setText(value2);
        }
        else if (button_number == 2)
        {
            btn_reply2.setText(value2);
        }
        else if (button_number == 3)
        {
            btn_reply3.setText(value2);
        }
        Log.w(TAG, "답변2 세팅 완료");
    }

    private void loadNextQuestion() {
        Log.w(TAG, "loadNextQuestion");

        if (currentPage <= 23) {
            onPageUpdated(++currentPage);
            Log.w(TAG, "TestActivity 현재 페이지: " + currentPage);

            if (currentPage <= 9) {
                Intent intent = new Intent(getApplication(), Test3Activity.class);
                startActivity(intent);
                finish();
            }

            currentQuestion = currentPage + 1;
            db_q();

            text_q_num.setText(String.valueOf(currentPage + 1));
            progressModel.getProgressLiveData().observe(Test2Activity.this, progress -> {
                progressBar.setProgress(currentPage);
                Log.w(TAG, "TestActivity 프로그레스: " + currentPage);
            });

            updateDataAndProgress();

        } else {
            Intent intent = new Intent(getApplication(), LoadingActivity.class);
            startActivity(intent);
        }
    }


    // 다음 질문 페이지를 표시하는 메서드
    public void loadNextQuestion(View view) {
                Log.w(TAG, "loadNextQuestion");

                if (currentPage <= 23) {
                    //currentPage++; // 페이지 증가
                    onPageUpdated(++currentPage); // 페이지 증가
                    Log.w(TAG, "TestActivity 현재 페이지: " + currentPage);
                    currentQuestion = currentPage + 1;

                    if (currentPage <= 9) {
                        Intent intent = new Intent(getApplication(), Test3Activity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // Firebase Realtime Database에서 질문 가져오기
                        db_q();

                        // 페이지 업데이트
                        text_q_num.setText(String.valueOf(currentPage + 1));
                        progressModel.getProgressLiveData().observe(Test2Activity.this, progress -> {
                            // 프로그레스바 업데이트
                            progressBar.setProgress(currentPage);
                            Log.w(TAG, "TestActivity 프로그레스: " + currentPage);
                        });

                        updateDataAndProgress();
                    }
                } else {
                    // 테스트 종료 또는 결과 페이지로 이동
                    Intent intent = new Intent(getApplication(), LoadingActivity.class);
                    startActivity(intent);
                }
            }

    private void updateDataAndProgress() {
        progressModel.updateProgress(currentPage);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", currentPage);
    }
}
