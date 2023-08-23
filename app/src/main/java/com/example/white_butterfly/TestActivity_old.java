package com.example.white_butterfly;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
//CurrentPageListener,
public class TestActivity_old extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // Firebase
    FirebaseFirestore user_db;
    DocumentReference docRef;

    // 검사 페이지 진행바
    public ProgressModel progressModel;
    public ProgressBar progressBar;
    private ProgressBar loadBar;

    // Firebase Database 인스턴스 생성
    private FirebaseDatabase question_db;
    private FirebaseAuth mAuth;
    String path;  // Firebase DB 메인 주소
    String temp;  // Firebase DB 서브 주소

    private int currentPage = 1;  // 테스트 페이지 추적 (1~25)
    private int currentProgress = 0; // (0~24)
    int button_number = 0;  // 정답 외의 내용이 들어갈 버튼 랜덤 선택
    int button_number_answer = 0;  // 정답이 들어갈 버튼 랜덤 선택

    TextView text_q_num; // 현재 질문 개수
    TextView text_question;  // 질문 텍스트뷰
    ImageView image_question; // 질문 이미지뷰
    Button btn_reply1;
    Button btn_reply2;
    Button btn_reply3;
    ImageView image_speak;
    Random random;
    TextView text_remember;
    TextView text_pass;
    int R_randomNumber1; // 정답 외의 선택지 답변1 랜덤
    int R_randomNumber2; // 정답 외의 선택지 답변2 랜덤

    // 마이크
    private TextToSpeech textToSpeech;
    private EditText editText_mic;

    // 스피커
    private static final int REQUEST_CODE_SPEECH_INPUT = 200;

    private List<Integer> numberList;
    String value = "";  // 정답 임시값
    String value1 = "";  // 답변1 임시값
    String value2 = "";  // 답변2 임시값
    String address = "";
    int year = 0;
    int month = 0;
    int day = 0;
    int click = 0;
    int score = 0;

    private static final String TAG = "Test1Activity";

    /*
    @Override
    public void onPageUpdated(int currentPage) {
        this.currentPage = currentPage;
        Log.w(TAG, "Test1Activity currentPage: " + currentPage);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Log.w(TAG, "--- Test1Activity ---");

        initializeViews();

        random = new Random();

        // Firebase
        FirebaseApp.initializeApp(TestActivity_old.this);
        question_db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String id = currentUser.getEmail();
            docRef = db.collection("Users").document(id);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    address = documentSnapshot.getString("Address");
                }
            });
        }

        btn_reply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 1;
                loadNextQuestion();
            }
        });

        btn_reply2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 2;
                loadNextQuestion();
            }
        });

        btn_reply3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 3;
                button_enable();
                editText_mic.setBackgroundResource(R.color.back); // 배경 설정
                editText_mic.setFocusableInTouchMode(false);
                editText_mic.setFocusable(false);
                loadNextQuestion();
            }
        });

        image_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = text_question.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });

        test();

        progressBar = findViewById(R.id.progress);
        progressModel = new ViewModelProvider(TestActivity_old.this).get(ProgressModel.class);
    }

    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        text_question = findViewById(R.id.text_question);
        image_question = findViewById(R.id.image_animal);
        text_remember = findViewById(R.id.text_remember);
        text_pass = findViewById(R.id.text_pass);
        btn_reply1 = findViewById(R.id.btn_reply1);
        btn_reply2 = findViewById(R.id.btn_reply2);
        btn_reply3 = findViewById(R.id.btn_reply3);
        textToSpeech = new TextToSpeech(this, this);
        image_speak = findViewById(R.id.image_speak);
        editText_mic = findViewById(R.id.editText_mic);
        loadBar = findViewById(R.id.loadBar);
    }

    private class QuestionChangeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩 화면 표시
            loadBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 백그라운드 작업 수행
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.w(TAG, "case: " + currentPage);
            switch (currentPage) {
                case 1:
                    answer_01();
                    break;
                case 2:
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
                    answer_06();
                    break;
                case 7:
                    image_question.setImageResource(R.drawable.image_cat);
                    answer_n5();
                    break;
                case 8:
                    image_question.setImageResource(R.drawable.image_lion);
                    answer_n5();
                    break;
                case 9:
                case 10:
                case 11:
                    image_question.setAlpha(0f); // 이미지 투명하게 함
                    answer_n5();
                    break;
                case 12:
                    image_question.setImageResource(R.drawable.microphone);
                    image_question.setAlpha(1f); // 이미지 보이게 함
                    answer_12();
                    break;
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                    answer_1314151617();
                    break;
                case 18:
                case 19:
                case 20:
                    answer_n8();
                    break;
                case 21:
                    image_question.setImageResource(R.drawable.image_chair);
                    image_question.setAlpha(1f);
                    answer_n5();
                    break;
                case 22:
                    image_question.setImageResource(R.drawable.image_scissors);
                    answer_n5();
                    break;
                case 23:
                    image_question.setImageResource(R.drawable.image_laptop);
                    answer_n5();
                    break;
                case 24:
                    image_question.setImageResource(R.drawable.image_bottle);
                    answer_n5();
                    break;
                case 25:
                    image_question.setImageResource(R.drawable.image_phone);
                    answer_n5();
                    break;
                default:
                    answer_n11();
                    break;
            }
            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(View.GONE);
        }
    }

    // "question_change()" 함수를 AsyncTask로 호출하는 부분
    private void question_change() {
        // AsyncTask 실행
        new QuestionChangeTask().execute();
    }

    public void test() {
        Log.w(TAG, "# answer " + currentPage);

        path = "Q" + String.format("%02d", currentPage);

        // 데이터 로드할 때 로딩 중 표시, 완료 후 숨김
        loadBar.setVisibility(View.VISIBLE);

        // 질문 표시
        question();

        // 페이지에 따라 답변 표시
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        question_change();
    }

    // 스피커
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.KOREA);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported or missing data");
            } else {
                // 초기화 성공, 필요한 추가 설정 가능
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


    private void question() {
        Log.w(TAG, "# question: " + currentPage);

        // 질문 불러오기
        try {
            question_db.getReference(path).child("Q").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String value = dataSnapshot.getValue(String.class);
                        text_question.setText(value);
                        Log.w(TAG, currentPage + " 질문 세팅 완료");
                    } else {
                        Log.w(TAG, currentPage + " 질문 경로가 존재하지 않음. path: " + path);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터 읽기 실패 시 처리
                    Log.w(TAG, currentPage + " 질문 데이터 읽기 실패");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, String.format("%s", e));
        }

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

    private void answer()  // 정답 데이터 추출
    {
        Log.w(TAG, currentPage + " 정답 세팅 중");
        Log.w(TAG, "path: " + path);
        // 정답 불러오기
        question_db.getReference(path).child("A").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    switch (currentPage)
                    {
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                            value = String.valueOf(dataSnapshot.getValue(Long.class));
                            break;
                        case 18:
                            break;
                        case 19:
                            break;
                        case 20:
                            break;
                        case 21:
                            image_question.setAlpha(1f);
                            image_question.setImageResource(R.drawable.image_chair);
                            answer_n5();
                            break;
                        case 22:
                            image_question.setAlpha(1f);
                            image_question.setImageResource(R.drawable.image_scissors);
                            answer_n5();
                            break;
                        case 23:
                            image_question.setImageResource(R.drawable.image_laptop);
                            answer_n5();
                            break;
                        case 24:
                            image_question.setImageResource(R.drawable.image_bottle);
                            answer_n5();
                            break;
                        case 25:
                            image_question.setImageResource(R.drawable.image_phone);
                            answer_n5();
                            break;
                        default:
                            value = dataSnapshot.getValue(String.class);
                            break;
                    }
                } else {
                    Log.w(TAG, currentPage + " 정답 경로가 존재하지 않음. path: " + path);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 읽기 실패 시 처리
                Log.w(TAG, " 정답 데이터 읽기 실패");
            }
        });
        Log.w(TAG, "# answer - " + value);

        button_number_answer = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number_answer));

        button_set(button_number_answer, value);
        Log.w(TAG, currentPage + " 정답 세팅 완료");
    }

    private void answer1(int NumberOfAnswer)  // 나머지 답변1에 들어갈 데이터 추출
    {
        Log.w(TAG, "path: " + path);
        do {
            R_randomNumber1 = random.nextInt(NumberOfAnswer) + 1; // 1~NumberOfAnswer 중 하나를 랜덤으로 선택
            temp = "R" + String.format("%02d", R_randomNumber1);
            question_db.getReference(path).child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        value1 = dataSnapshot.getValue(String.class);
                    } else {
                        Log.w(TAG, " 답변1 경로가 존재하지 않음. path: " + path);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터 읽기 실패 시 처리
                    Log.w(TAG, " 답변1 데이터 읽기 실패");
                }
            });
        } while (value1.equals(value));  // 정답과 다를 때까지

        Log.w(TAG, "# answer1 - " + value1);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));

        button_set(button_number, value1);
        Log.w(TAG, currentPage + " 답변1 세팅 완료");
    }

    private void answer2(int NumberOfAnswer)  // 나머지 답변2에 들어갈 데이터 추출
    {
        do {
            R_randomNumber2 = random.nextInt(NumberOfAnswer) + 1; // 1~NumberOfAnswer 중 하나를 랜덤으로 선택
            temp = "R" + String.format("%02d", R_randomNumber2);
            question_db.getReference(path).child(temp).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        value2 = dataSnapshot.getValue(String.class);
                    } else {
                        Log.w(TAG, " 답변2 경로가 존재하지 않음. path: " + path);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터 읽기 실패 시 처리
                    Log.w(TAG, " 답변2 데이터 읽기 실패");
                }
            });
        } while ((value2.equals(value)) && (value2.equals(value1)));  // 정답과 다를 때까지

        Log.w(TAG, "# answer2 - " + value2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));

        button_set(button_number, value2);
        Log.w(TAG, currentPage + " 답변2 세팅 완료");
    }

    private void answer_06()
    {
        Log.w(TAG, path);
        path = "Q" + String.format("%02d", currentPage);
        Log.w(TAG, path);

        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        answer1(5);
        //btn_reply1.setText("1");
        //btn_reply2.setText("2");
        //btn_reply3.setText("3");


        // 나머지 답변2 불러오기
        answer2(5);

        image_question.setImageResource(R.drawable.image_dog);
        image_question.setAlpha(1f); // 이미지 보이게 함
    }

    // 객관식 답 5개
    private void answer_n5()
    {
        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        answer1(5);

        // 나머지 답변2 불러오기
        answer2(5);
    }

    // 객관식 답 8개
    private void answer_n8()
    {
        // 정답 불러오기
        value = address;
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value);

        // 나머지 답변1 불러오기
        answer1(8);

        // 나머지 답변2 불러오기
        answer2(8);
    }

    private void answer_n11()
    {
        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        answer1(11);

        // 나머지 답변2 불러오기
        answer2(11);
    }

    // 아까 기억했던 단어 3개를 말해주세요.
    private void answer_12()
    {
        button_disable();
        btn_reply3.setEnabled(true);
        btn_reply3.setTextColor(ContextCompat.getColor(this, R.color.white));
        btn_reply3.setText("다음");

        editText_mic.setBackgroundResource(R.drawable.edittext_rounded_corner_rectangle);
        editText_mic.setFocusableInTouchMode(true); // 터치 모드에서 포커스 가능하게 설정
        editText_mic.setFocusable(true); // 포커스 가능하게 설정

        // 이미지 뷰를 클릭했을 때 실행되는 메소드
        image_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이크 눌렀을 때 실행되는 메소드
                startSpeechToText();
            }
        });
    }

    // 마이크
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // 여러 언어 지원을 위한 문자열 배열로 설정
        String[] supportedLanguages = {"en-US", "ko-KR"};
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, supportedLanguages);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String recognizedText = result.get(0);
                    // 음성 인식 결과를 두 번째 화면의 텍스트뷰에 표시
                    editText_mic.setText(recognizedText);
                }
            }
        }
    }

    // 계산 문제
    private void answer_1314151617()
    {
        // 정답 불러오기
        answer();

        // 나머지 답변1 불러오기
        do {
            value1 = String.valueOf(random.nextInt(50) + 50);  // 50~100 사이의 랜덤값
        } while (value1.equals(value));

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value1);
        Log.w(TAG, "답변1 세팅 완료: " + value1);


        // 나머지 답변2 불러오기
        do {
            value2 = String.valueOf(random.nextInt(50) + 50);  // 50~100 사이의 랜덤값
        } while (value2.equals(value) && value2.equals(value1));

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value2);
        Log.w(TAG, "답변2 세팅 완료: " + value2);
    }

    // 다음 질문 페이지를 표시하는 메서드
    public void loadNextQuestion() {
        Log.w(TAG, "loadNextQuestion");
        //onPageUpdated(++currentPage);
        currentPage++;
        currentProgress++;

        if (button_number_answer == click)
        {
            score++;
        }

        if (currentPage <= 24)
        {
            test();

            text_q_num.setText(String.valueOf(currentPage));

            progressModel.getProgressLiveData().observe(TestActivity_old.this, progress -> {
                progressBar.setProgress(currentProgress);
                Log.w(TAG, "프로그레스: " + currentProgress);
            });

            updateDataAndProgress();
        }
        else
        {
            Log.w(TAG, "score: " + score);

            docRef.update("Score", score);

            // 결과 페이지로 점수 전송
            Intent intent_result = new Intent(getApplication(), ResultActivity.class);
            intent_result.putExtra("Score", score);

            // 로딩 페이지 출력
            Intent intent_loading = new Intent(getApplication(), LoadingActivity.class);
            startActivity(intent_loading);
        }
    }

    private void today() {
        LocalDate currentDate = LocalDate.now();

        year = currentDate.getYear();        // 2023
        month = currentDate.getMonthValue(); // 8
        day = currentDate.getDayOfMonth();   // 21
    }

    private void answer_01() {
        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        today();
        value = String.valueOf(year);
        button_set(button_number, value);
        Log.w(TAG, currentPage + " 정답 세팅 완료: " + value);

        // 나머지 답변1 불러오기
        int year1;
        do {
            year1 = random.nextInt(35) + 1990;  // 1990년부터 2025년까지 랜덤 뽑기
        } while (year1 == year);  // 정답과 다를 때까지
        value1 = String.valueOf(year1);

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value1);
        Log.w(TAG, currentPage + " 답변1 세팅 완료: " + value1);

        //// 나머지 답변2 불러오기
        int year2;
        do {
            year2 = random.nextInt(35) + 1990;  // 1990년부터 2025년까지 랜덤 뽑기
        } while ((year2 == year) && (year2 == year1));  // 정답과 다를 때까지
        value2 = String.valueOf(year2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));

        button_set(button_number, value2);
        Log.w(TAG, currentPage + " 답변2 세팅 완료: " + value2);
    }

    private void answer_02() {
        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1;  // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        today();
        value = String.valueOf(month);
        button_set(button_number, value);
        Log.w(TAG, currentPage + " 정답 세팅 완료: " + value);

        // 나머지 답변1 불러오기
        do {
            R_randomNumber1 = random.nextInt(12) + 1;  // 1월부터 12월까지 랜덤 뽑기
        } while (R_randomNumber1 == month);  // 정답과 다를 때까지

        value1 = String.valueOf(R_randomNumber1);

        button_number = numberList.get(0);  // 어느 버튼에 들어갈지 결정
        numberList.remove(Integer.valueOf(button_number));  // 이제 해당 버튼은 제외

        button_set(button_number, value1);
        Log.w(TAG, currentPage + " 답변1 세팅 완료: " + value1);

        // 나머지 답변2 불러오기
        do {
            R_randomNumber2 = random.nextInt(12) + 1;
        } while ((R_randomNumber2 == month) && (R_randomNumber2 == R_randomNumber1));

        value2 = String.valueOf(R_randomNumber2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));

        button_set(button_number, value2);
        Log.w(TAG, currentPage + " 답변2 세팅 완료: " + value2);
    }

    private void answer_03() {
        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        today();
        value = String.format("%d년 %d월 %d일", year, month, day);
        button_set(button_number, value);
        Log.w(TAG, currentPage + " 정답 세팅 완료");


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
        Log.w(TAG, currentPage + " 답변1 세팅 완료");


        // 나머지 답변2 불러오기
        int year2;
        int month2;
        int day2;

        do {
            year2 = random.nextInt(25) + 15;  // 2015년부터 2025년까지 랜덤 뽑기
        } while (year2 == year && year2 == year1);  // 정답과 다를 때까지

        do {
            month2 = random.nextInt(12) + 1;  // 1월부터 12월까지 랜덤 뽑기
        } while (month2 == month && month2 == month1);  // 정답과 다를 때까지

        do {
            day2 = random.nextInt(30) + 1;  // 1일부터 30일까지 랜덤 뽑기
        } while (day2 == day && day2 == day1);  // 정답과 다를 때까지


        value2 = String.format("20%d년 %d월 %d일", year2, month2, day2);

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value2);
        Log.w(TAG, currentPage + " 답변2 세팅 완료");
    }

    // 계절 문제
    private void answer_04() {
        // 정답 넣을 버튼 랜덤 결정
        button_number = random.nextInt(3) + 1; // 1, 2, 3 중 하나를 랜덤으로 선택
        numberList.remove(Integer.valueOf(button_number)); // 이제 해당 버튼은 제외

        // 오늘 날짜 구하기
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        int month = Integer.parseInt(currentDate.format(monthFormatter));

        value = season(month);

        button_set(button_number, value);
        Log.w(TAG, currentPage + " 정답 세팅 완료: " + value);


        // 나머지 답변1 불러오기
        do {
            R_randomNumber1 = random.nextInt(12) + 1;
            value1 = season(R_randomNumber1);
        } while (value1.equals(value));

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value1);
        Log.w(TAG, currentPage + " 답변1 세팅 완료: " + value1);


        // 나머지 답변2 불러오기
        do {
            R_randomNumber2 = random.nextInt(12) + 1;
            value2 = season(R_randomNumber2);
        } while ((value2.equals(value)) && (value2.equals(value1)));

        button_number = numberList.get(0);
        numberList.remove(Integer.valueOf(button_number));
        button_set(button_number, value2);
        Log.w(TAG, currentPage + " 답변2 세팅 완료: " + value2);
    }

    // 월에 따른 계절 판단 함수
    private String season(int num)
    {
        String season = "";

        switch (num) {
            case 12:
            case 1:
            case 2:
                season = "겨울";
                break;
            case 3:
            case 4:
            case 5:
                season = "봄";
                break;
            case 6:
            case 7:
            case 8:
                season = "여름";
                break;
            case 9:
            case 10:
            case 11:
                season = "가을";
                break;
            default:
                break;
        }

        return season;
    }

    // 버튼 비활성화
    private void button_disable()
    {
        btn_reply1.setEnabled(false);
        btn_reply1.setTextColor(ContextCompat.getColor(this, R.color.back));
        btn_reply2.setEnabled(false);
        btn_reply2.setTextColor(ContextCompat.getColor(this, R.color.back));
        btn_reply3.setEnabled(false);
        btn_reply3.setTextColor(ContextCompat.getColor(this, R.color.back));
    }

    // 버튼 활성화
    private void button_enable()
    {
        btn_reply1.setEnabled(true);
        btn_reply1.setTextColor(ContextCompat.getColor(this, R.color.white));
        btn_reply2.setEnabled(true);
        btn_reply2.setTextColor(ContextCompat.getColor(this, R.color.white));
        btn_reply3.setEnabled(true);
        btn_reply3.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    // 5초동안 기억하는 퀴즈 5번
    private void answer_05() {
        // 버튼 비활성화
        button_disable();

        String rememberText = getResources().getString(R.string.remember_text); // 나무 자동차 모자
        text_remember.setText(rememberText);

        String passText = getResources().getString(R.string.pass_text); // 5초 후에 자동으로 넘어갑니다.
        text_pass.setText(passText);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "5초 딜레이 후 넘어갑니다.");

                button_enable();
                text_remember.setText("");
                text_pass.setText("");
                loadNextQuestion();
            }
        }, 5000); // 5초 (5000 밀리초) 지연
    }

    private void updateDataAndProgress() {
        progressModel.updateProgress(currentPage);
    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", currentPage);
    }

     */
}
