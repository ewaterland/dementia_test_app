package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Test1Activity extends AppCompatActivity implements CurrentPageListener {

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
    private List<Integer> numberList;
    String value = "";  // 정답 임시값
    String value1 = "";  // 답변1 임시값
    String value2 = "";  // 답변2 임시값
    String address = "";

    @Override
    public void onPageUpdated(int currentPage) {
        this.currentPage = currentPage;
        Log.w(TAG, "Test1Activity currentPage: " + currentPage);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.w(TAG, "--- Test1Activity ---");

        initializeViews();

        random = new Random();

        // Firebase
        FirebaseApp.initializeApp(Test1Activity.this);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String id = currentUser.getEmail();
            DocumentReference docRef = db.collection("Users").document(id);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    address = documentSnapshot.getString("Address");
                }
            });
        }

        currentQuestion = currentPage+1;
        path = "Q" + String.format("%02d", currentQuestion);
        test();

        progressBar = findViewById(R.id.progress);
        progressModel = new ViewModelProvider(Test1Activity.this).get(ProgressModel.class);
    }

    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        text_question = findViewById(R.id.text_question);
        image_question = findViewById(R.id.image_animal);
        text_remember = findViewById(R.id.text_remember);
        btn_reply1 = findViewById(R.id.btn_reply1);
        btn_reply2 = findViewById(R.id.btn_reply2);
        btn_reply3 = findViewById(R.id.btn_reply3);
    }

    public void test() {
        Log.w(TAG, "# test");
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        path = "Q" + String.format("%02d", currentQuestion);

        // 질문 표시
        question();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "# Thread:" + currentQuestion);
                // 오래 걸리는 작업을 여기에서 처리
                switch (currentQuestion)
                {
                    case 2:
                        image_question.setAlpha(0f);
                        answer_02();
                        break;
                    case 3:
                        answer_03();
                        break;
                    case 4:
                        answer_04();
                        break;
                    case 5:
                        answer_05();
                        break;
                    case 6:
                        image_question.setImageResource(R.drawable.image_dog);
                        image_question.setAlpha(1f);
                        break;
                    case 7:
                        image_question.setImageResource(R.drawable.image_cat);
                        break;
                    case 8:
                        image_question.setImageResource(R.drawable.image_lion);
                        break;
                    default:
                        answer_n11();
                        break;
                }

                // 작업이 완료되면 메인 스레드에서 UI 업데이트
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // UI 업데이트 코드
                        //button_set(button_number, value);
                        Log.w(TAG, currentQuestion + "UI 업데이트 완료");
                    }
                });
            }
        }).start();
    }

    private void question() {
        Log.w(TAG, "# question:" + currentQuestion);

        // 질문 불러오기
        database.getReference(path).child("Q").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    text_question.setText(value);
                    Log.w(TAG, currentQuestion + "질문 세팅 완료");
                } else {
                    Log.w(TAG, currentQuestion + "질문 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, currentQuestion + "질문 데이터 읽기 실패");
            }
        });
    }

    private void answer()  // 정답 데이터 추출
    {
        Log.w(TAG, currentQuestion + "정답 세팅 중");
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

                    Log.w(TAG, currentQuestion + "정답 세팅 완료");
                } else {
                    Log.w(TAG, currentQuestion + "정답 경로가 존재하지 않음. path: " + path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, "답변 데이터 읽기 실패");
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

    private void loadNextQuestion() {
        Log.w(TAG, "loadNextQuestion");

        if (currentPage <= 23)
        {
            onPageUpdated(++currentPage);
            Log.w(TAG, "Test1Activity 현재 페이지: " + currentPage);
            currentQuestion = currentPage + 1;

            if (currentPage <= 9)
            {
                Intent intent = new Intent(getApplication(), Test2Activity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                test();

                text_q_num.setText(String.valueOf(currentPage + 1));
                progressModel.getProgressLiveData().observe(Test1Activity.this, progress -> {
                    progressBar.setProgress(currentPage);
                    Log.w(TAG, "Test1Activity 프로그레스: " + currentPage);
                });

                updateDataAndProgress();
            }
        }
        else
        {
            Intent intent = new Intent(getApplication(), LoadingActivity.class);
            startActivity(intent);
        }
    }

    // 다음 질문 페이지를 표시하는 메서드
    public void loadNextQuestion(View view) {
        Log.w(TAG, "loadNextQuestion");

        if (currentPage <= 23)
        {
            onPageUpdated(++currentPage);
            Log.w(TAG, "Test1Activity 현재 페이지: " + currentPage);
            currentQuestion = currentPage + 1;

            if (currentPage <= 9)
            {
                Intent intent = new Intent(getApplication(), Test2Activity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                test();

                text_q_num.setText(String.valueOf(currentPage + 1));
                progressModel.getProgressLiveData().observe(Test1Activity.this, progress -> {
                    progressBar.setProgress(currentPage);
                    Log.w(TAG, "Test1Activity 프로그레스: " + currentPage);
                });
                updateDataAndProgress();
            }
        }
        else
        {
            Intent intent = new Intent(getApplication(), LoadingActivity.class);
            startActivity(intent);
        }
    }

    private void answer_02() {
        Log.w(TAG, "# answer_02");

        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        int month = Integer.parseInt(currentDate.format(monthFormatter));
        value = currentDate.format(monthFormatter);
        button_set(button_number, value);
        Log.w(TAG, "정답 세팅 완료");

        // 나머지 답변1 불러오기
        do {
            R_randomNumber1 = random.nextInt(12) + 1;  // 1월부터 12월까지 랜덤 뽑기
        } while (R_randomNumber1 == month);  // 정답과 다를 때까지
        value1 = String.valueOf(R_randomNumber1);

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value1);
        Log.w(TAG, "답변1 세팅 완료");


        // 나머지 답변2 불러오기
        do {
            R_randomNumber2 = random.nextInt(12) + 1;
        } while ((R_randomNumber2 == month) || (R_randomNumber2 == R_randomNumber1));
        value2 = String.valueOf(R_randomNumber2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));

        button_set(button_number, value2);
        Log.w(TAG, "답변2 세팅 완료");
    }

    private void answer_03() {
        Log.w(TAG, "# answer_03");

        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
        value = String.format("%d년 %d월 %d일", year, month, day);
        button_set(button_number, value);
        Log.w(TAG, "정답 세팅 완료");


        // 나머지 답변1 불러오기
        int year1;
        int month1;
        int day1;

        do {
            year1 = random.nextInt(25) + 15;  // 2015년부터 2025년까지 랜덤 뽑기
        } while (year1 == year);  // 정답과 다를 때까지

        do {
            month1 = random.nextInt(12) + 1;  // 1월부터 12월까지 랜덤 뽑기
        } while (month1 == month);  // 정답과 다를 때까지

        do {
            day1 = random.nextInt(30) + 1;  // 1일부터 30일까지 랜덤 뽑기
        } while (day1 == day);  // 정답과 다를 때까지


        value1 = String.format("20%d년 %d월 %d일", year1, month1, day1);

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value1);
        Log.w(TAG, "답변1 세팅 완료");


        // 나머지 답변2 불러오기
        int year2;
        int month2;
        int day2;

        do {
            year2 = random.nextInt(25) + 15;  // 2015년부터 2025년까지 랜덤 뽑기
        } while (year2 == year || year2 == year1);  // 정답과 다를 때까지

        do {
            month2 = random.nextInt(12) + 1;  // 1월부터 12월까지 랜덤 뽑기
        } while (month2 == month || month2 == month1);  // 정답과 다를 때까지

        do {
            day2 = random.nextInt(30) + 1;  // 1일부터 30일까지 랜덤 뽑기
        } while (day2 == day || day2 == day1);  // 정답과 다를 때까지


        value2 = String.format("20%d년 %d월 %d일", year2, month2, day2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value2);
        Log.w(TAG, "답변2 세팅 완료");
    }

    // 계절 문제
    private void answer_04() {
        Log.w(TAG, "# answer_04");

        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        int month = Integer.parseInt(currentDate.format(monthFormatter));

        value = season(month);

        button_set(button_number, value);
        Log.w(TAG, "정답 세팅 완료");


        // 나머지 답변1 불러오기
        do {
            R_randomNumber1 = random.nextInt(12) + 1;
        } while (R_randomNumber1 == month);

        value1 = season(R_randomNumber1);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value1);
        Log.w(TAG, "답변1 세팅 완료");


        // 나머지 답변2 불러오기
        do {
            R_randomNumber2 = random.nextInt(12) + 1;
        } while ((R_randomNumber2 == month) || (R_randomNumber2 == R_randomNumber1));

        value2 = season(R_randomNumber2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value2);
        Log.w(TAG, "답변2 세팅 완료");
    }

    // 월에 따른 계절 판단 함수
    private String season(int num)
    {
        switch (num) {
            case 12:
            case 1:
            case 2:
                value2 = "겨울";

                break;
            case 3:
            case 4:
            case 5:
                value2 = "봄";
                break;
            case 6:
            case 7:
            case 8:
                value2 = "여름";
                break;
            case 9:
            case 10:
            case 11:
                value2 = "가을";
                break;
            default:
                break;
        }

        return value;
    }


    // 5초동안 기억하는 퀴즈 5번
    private void answer_05() {
        Log.w(TAG, "# answer_05");

        text_remember.setText("나무 자동차 모자");

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                text_remember.setText("");
                loadNextQuestion();
            }
        }, 5000); // 5초 (5000 밀리초) 지연
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
