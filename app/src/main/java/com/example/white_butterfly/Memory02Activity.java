package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Memory02Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ProgressBar progressBar;
    private int progress = 1; // 진행 상태 변수 추가
    private TextToSpeech textToSpeech;

    private FirebaseDatabase memory_db, temporarily_db;
    private TextView dataTextView;
    private Button nextButton;
    private ImageButton speak;
    private Button startButton;
    int questionCounter = 1;

    TextView text_q_num; // 현재 질문 개수
    String path_m = "";
    Random random;
    private List<Integer> numberList;  // 아직 안 한 질문 리스트
    private static final String TAG = "Memory02Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory02);

        Log.w(TAG, "--- Memory02Activity ---");

        progressBar = findViewById(R.id.progress);

        FirebaseApp.initializeApp(Memory02Activity.this);
        memory_db = FirebaseDatabase.getInstance();
        temporarily_db = FirebaseDatabase.getInstance();

        initializeViews();

        random_question();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ++questionCounter;

                if (questionCounter == 10) {
                    nextButton.setEnabled(false);
                    startButton.setEnabled(true);
                }

                if (questionCounter < 11) {
                    progress++; // 진행 상태 증가
                    progressBar.setProgress(progress); // 프로그래스바 갱신

                    random_question();

                    text_q_num.setText(String.valueOf(questionCounter));
                    Log.d("questionCounter", "questionCounter : " + questionCounter);
                }
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NextButton을 눌렀을 때 수행할 작업
                Intent intent = new Intent(Memory02Activity.this, Memory03Activity.class);
                startActivity(intent);
            }
        });

        // 초기에는 NextButton은 비활성화 상태로 시작
        startButton.setEnabled(false);
    }
    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        dataTextView = findViewById(R.id.dataTextView);
        nextButton = findViewById(R.id.nextButton);
        startButton = findViewById(R.id.startButton);
        speak = findViewById(R.id.speak);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);

        // 아직 안 한 질문 리스트
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // 랜덤 인덱스
        random = new Random();
    }

    private void random_question() {
        // 랜덤한 인덱스 생성
        int randomIndex = random.nextInt(numberList.size());
        int randomValue = numberList.get(randomIndex);
        numberList.remove(Integer.valueOf(randomValue));

        Log.d(TAG, "randomValue : " + randomValue);

        memory(randomValue);
    }

    private void memory(int randomNumber) {
        path_m = "M" + String.format("%02d", randomNumber);
        String path_t = "T" + String.format("%02d", questionCounter);
        Log.d("path_m", "path_m : " + path_m);

        // 질문 가져오기
        memory_db.getReference(path_m).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                dataTextView.setText(value);

                temporarily_db.getReference(path_t).setValue(value);

                Log.w(TAG, "numberList: " + numberList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

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
}