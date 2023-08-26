package com.example.white_butterfly;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestCogActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // Firebase
    FirebaseFirestore user_db;
    DocumentReference docRef;
    String path;  // Firebase DB 메인 주소
    private FirebaseDatabase question_db;
    private FirebaseAuth mAuth;

    // 검사 페이지 진행바
    public ProgressModel progressModel;
    public ProgressBar progressBar;
    private ProgressBar loadBar;

    // 페이지 추적
    private int currentPage = 1;  // 현재 페이지 (1~25)
    private int currentProgress = 0; // (0~24)

    // TTS
    private TextToSpeech textToSpeech;

    // 변수
    TextView text_q_num; // 현재 질문 개수
    TextView text_question;  // 질문 텍스트뷰
    Button btn_reply1;
    Button btn_reply2;
    Button btn_reply3;
    ImageView image_speak;
    private List<Integer> numberList;  // 아직 안 한 질문 리스트
    //int now;  // 현재 출력된 질문 넘버
    int score_cog = 0;

    // TAG
    private static final String TAG = "TestCogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cog);

        Log.w(TAG, "--- TestCogActivity ---");

        initializeViews();

        // Firebase
        FirebaseApp.initializeApp(TestCogActivity.this);
        question_db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String id = currentUser.getEmail();
            docRef = db.collection("Users").document(id);
        }

        btn_reply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextQuestion();
            }
        });

        btn_reply2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score_cog += 1;

                loadNextQuestion();
            }
        });

        btn_reply3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score_cog += 2;

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
        progressModel = new ViewModelProvider(TestCogActivity.this).get(ProgressModel.class);
    }

    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        text_question = findViewById(R.id.text_question);

        btn_reply1 = findViewById(R.id.btn_reply_yes);
        btn_reply2 = findViewById(R.id.btn_reply_no);
        btn_reply3 = findViewById(R.id.btn_reply3);

        textToSpeech = new TextToSpeech(this, this);
        image_speak = findViewById(R.id.image_speaker);

        loadBar = findViewById(R.id.loadBar);

        //numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    private class getDataTask extends AsyncTask<Void, Void, Void> {
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
            Log.w(TAG, "# question: " + currentPage);

            // 질문 불러오기
            try {
                question_db.getReference(path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String value = dataSnapshot.getValue(String.class);
                            value = value.replace("\\n", "\n");
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

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(View.GONE);
        }
    }

    // "question()" 함수를 AsyncTask로 호출하는 부분
    private void question() {
        // AsyncTask 실행
        new getDataTask().execute();
    }

    public void test() {
        //Log.w(TAG, "numberList: " + numberList);
        //now = random.nextInt(14) + 1;
        //numberList.remove(Integer.valueOf(now));
        //Log.w(TAG, "numberList: " + numberList);

        path = "C" + String.format("%02d", currentPage);
        Log.w(TAG, "# test " + currentPage + " / 현재 질문 " + path);

        // 데이터 로드할 때 로딩 중 표시, 완료 후 숨김
        loadBar.setVisibility(View.VISIBLE);

        // 질문 표시
        question();
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

    // 다음 질문 페이지를 표시하는 메서드
    public void loadNextQuestion() {
        Log.w(TAG, "loadNextQuestion");

        //onPageUpdated(++currentPage);
        currentPage++;
        currentProgress++;

        if (currentPage <= 15)
        {
            test();

            text_q_num.setText(String.valueOf(currentPage));

            progressModel.getProgressLiveData().observe(TestCogActivity.this, progress -> {
                progressBar.setProgress(currentProgress);
                Log.w(TAG, "프로그레스: " + currentProgress);
            });

            updateDataAndProgress();
        }
        else
        {
            Log.w(TAG, "score_cog: " + score_cog);

            docRef.update("Score_cog", score_cog);

            // 우울증 검사 페이지 출력
            Intent intent_dep = new Intent(getApplication(), TestDepMainActivity.class);
            intent_dep.putExtra("score_cog", score_cog);
            startActivity(intent_dep);
            finish();
        }
    }

    private void updateDataAndProgress() {
        progressModel.updateProgress(currentPage);
    }
}
