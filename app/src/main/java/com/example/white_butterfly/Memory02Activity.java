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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Memory02Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ProgressBar progressBar;
    private int progress = 0; // 진행 상태 변수 추가
    private ProgressBar loadBar;
    private TextToSpeech textToSpeech;

    private FirebaseDatabase memory_db, temporarily_db;
    private TextView dataTextView;
    private Button nextButton;
    private ImageButton speak;
    private Button startButton;

    private List<String> startingWithQDataList;
    private Map<String, Boolean> usedQuestionsMap;

    int questionCounter = 1;
    //private int questionCounter01 = 1;

    TextView text_q_num; // 현재 질문 개수
    String path_m = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory02);

        progressBar = findViewById(R.id.progress);

        FirebaseApp.initializeApp(Memory02Activity.this);
        memory_db = FirebaseDatabase.getInstance();
        temporarily_db = FirebaseDatabase.getInstance();

        text_q_num = findViewById(R.id.text_q_num);
        dataTextView = findViewById(R.id.dataTextView);
        nextButton = findViewById(R.id.nextButton);
        startButton = findViewById(R.id.startButton);
        speak = findViewById(R.id.speak);
        loadBar = findViewById(R.id.loadBar);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);

        startingWithQDataList = new ArrayList<>();
        usedQuestionsMap = new HashMap<>();

        nextButton.setOnClickListener(new View.OnClickListener() {
            boolean isFirstClick = true; // 처음 클릭 여부를 추적하기 위한 변수

            @Override
            public void onClick(View v) {

                // 처음 클릭 시 버튼 이름을 '다음'으로 변경
                if (isFirstClick) {
                    nextButton.setText("다음");
                    isFirstClick = false;
                }

                if (questionCounter <= 5) {
                    progress++; // 진행 상태 증가
                    progressBar.setProgress(progress); // 프로그래스바 갱신

                    if (questionCounter == 5) {
                        nextButton.setEnabled(false);
                        startButton.setEnabled(true);
                    }
                }
                memory(questionCounter);
                //String randomValue = getRandomStartingWithQValue(questionCounter);
                //dataTextView.setText(randomValue);

                text_q_num.setText(String.valueOf(questionCounter));
                Log.d("questionCounter", "questionCounter : " + questionCounter);

                questionCounter++;
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

    private void memory(int questionCounter) {
        path_m = "M" + String.format("%02d", questionCounter);
        Log.d("path_m", "path_m : " + path_m);

        // 질문 가져오기
        memory_db.getReference(path_m).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startingWithQDataList.clear();
                usedQuestionsMap.clear();
                String value = dataSnapshot.getValue(String.class);
                startingWithQDataList.add(value);
                Log.w(TAG, "startingWithQDataList: " + startingWithQDataList);

                getRandomStartingWithQValue(questionCounter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void getRandomStartingWithQValue(int questionCounter) {
        if (startingWithQDataList.isEmpty()) {
            return;
        }

        List<String> unusedQuestions = new ArrayList<>();
        for (String question : startingWithQDataList) {
            if (!usedQuestionsMap.containsKey(question)) {
                unusedQuestions.add(question);
            }
        }

        if (unusedQuestions.isEmpty()) {
            usedQuestionsMap.clear();
            unusedQuestions.addAll(startingWithQDataList);
        }

        int randomIndex = new Random().nextInt(unusedQuestions.size());
        String selectedQuestion = unusedQuestions.get(randomIndex);
        usedQuestionsMap.put(selectedQuestion, true);

        // Save the selected question under "temporarily" with 'Q01', 'Q02', ...
        temporarily_db.getReference("T" + String.format("%02d", questionCounter)).setValue(selectedQuestion);

        dataTextView.setText(selectedQuestion);
        String data = dataTextView.getText().toString();
        Log.d("문제", "문제" + questionCounter + " : " + data);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.US);
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