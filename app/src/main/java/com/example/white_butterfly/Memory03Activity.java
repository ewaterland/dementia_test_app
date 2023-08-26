package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Memory03Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ProgressBar progressBar;
    private int progress = 0; // 진행 상태 변수 추가
    private TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 200;
    private FirebaseDatabase memory_db, temporarily_db, empty_db, answer_db;

    private List<String> startingWithQDataList;
    private Map<String, Boolean> usedQuestionsMap;

    private TextView dataTextView;
    private Button startButton;
    private ImageButton speak;
    private ImageButton microphone;
    private Button endButton;
    private String answerValue;
    int questionCounter = 1; // 답변
    int questionCounter01 = 0; // 질문
    int one = 0; // 1
    int zero = 0; // 0
    TextView text_q_num; // 현재 질문 개수
    String path_m = "";
    private static final String TAG = "Memory03Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory03);

        Log.w(TAG, "--- Memory03Activity ---");

        // Firebase 초기화
        FirebaseApp.initializeApp(Memory03Activity.this);
        memory_db = FirebaseDatabase.getInstance();
        temporarily_db = FirebaseDatabase.getInstance();
        empty_db = FirebaseDatabase.getInstance();
        answer_db = FirebaseDatabase.getInstance();

        initializeViews();

        progressBar = findViewById(R.id.progress);

        startButton.setOnClickListener(new View.OnClickListener() {
            boolean isFirstClick = true; // 처음 클릭 여부를 추적하기 위한 변수
            final String path_t = "T" + String.format("%02d", questionCounter);
            final String path_e = "E" + String.format("%02d", questionCounter);
            final String path_a = "A" + String.format("%02d", questionCounter);

            @Override
            public void onClick(View v) {
                Log.w(TAG, "next");

                memory(questionCounter);

                // 증가된 questionCounter01 값을 저장
                questionCounter01++;
                text_q_num.setText(String.valueOf(questionCounter01));
                Log.d("questionCounter01", "questionCounter01 : " + questionCounter01);
                Log.d("path_t", "path_t : " + path_t);
                Log.d("path_e", "path_e : " + path_e);
                Log.d("path_a", "path_a : " + path_a);
                Log.w(TAG, "path_e: " + path_e);

                if (questionCounter01 == 6) {
                    startButton.setEnabled(false);
                    endButton.setEnabled(true);

                    if (questionCounter01 <= 5) {
                        progress++; // 진행 상태 증가
                        progressBar.setProgress(progress); // 프로그래스바 갱신
                    }
                }

                // 처음 클릭 시 버튼 이름을 '다음'으로 변경
                if (isFirstClick) {
                    startButton.setText("다음");
                    isFirstClick = false;
                }

                // '다음' 버튼이면 실행되는 코드
                if (startButton.getText().toString().equals("다음")) {
                    // Retrieve the value from Temporarily node
                    temporarily_db.getReference(path_t).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String temporaryValue = dataSnapshot.getValue(String.class);
                            Log.d("temporaryValue", "temporaryValue : " + temporaryValue);
                            if (temporaryValue != null) {
                                memory_db.getReference(path_m).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String memoryValue = dataSnapshot.getValue(String.class);
                                        Log.d("memoryValue", "memoryValue : " + memoryValue);
                                        if (memoryValue != null && memoryValue.equals(temporaryValue)) {
                                            String questionName = path_m;
                                            Log.d("QuestionMatch", "Match found for value: " + temporaryValue + ", question name: " + questionName);

                                            empty_db.getReference(path_e).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot emptySnapshot) {
                                                    String emptyValue = emptySnapshot.getValue(String.class);
                                                    Log.d("emptyValue", "emptyValue : " + emptyValue);
                                                    if (emptyValue != null) {
                                                        dataTextView.setText(emptyValue);
                                                        // Q랑 A비교
                                                        answer_db.getReference(path_a).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot answerSnapshot) {
                                                                String answerValue = answerSnapshot.getValue(String.class);
                                                                Log.d("answerValue", "answerValue : " + answerValue);

                                                                EditText editText_ex = findViewById(R.id.editText_ex);
                                                                String edit = editText_ex.getText().toString();
                                                                if (answerValue != null) {
                                                                    Log.d("Answer", "정답 : " + answerValue);
                                                                    Log.w(TAG, "edit: " + edit);

                                                                    // 에디트텍스트에 값이 있을 때만 결과 저장
                                                                    if (!edit.isEmpty()) {
                                                                        startButton.setEnabled(true);
                                                                        Log.d("활성화", "활성화");

                                                                        check(answerValue, edit);

                                                                    }
                                                                    else {
                                                                        startButton.setEnabled(false);
                                                                        Log.d("비활성화", "비활성화");
                                                                    }

                                                                    // 에디트텍스트 내용 지우기
                                                                    editText_ex.setText("");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Handle error
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Handle error
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
                else {
                    Log.w(TAG, "버튼 이름이 '다음'이 아님");
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

        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "mic");
                startSpeechToText();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int one_score = one * 10; // 점수

                Log.w(TAG, "one: " + one);
                Log.w(TAG, "one * 10: " + one_score);

                Intent intent = new Intent(Memory03Activity.this, MemoryLoadingActivity.class);
                intent.putExtra("score", one_score); // 데이터를 Intent에 첨부
                startActivity(intent);
            }
        });

        // 초기에는 endButton 비활성화 상태로 시작
        endButton.setEnabled(false);
    }

    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        dataTextView = findViewById(R.id.dataTextView);
        startButton = findViewById(R.id.startButton);
        endButton = findViewById(R.id.endButton);
        speak = findViewById(R.id.speak);
        microphone = findViewById(R.id.microphone);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);
    }

    private void memory(int questionCounter) {
        path_m = "M" + String.format("%02d", questionCounter);
        Log.d("path_m", "path_m : " + path_m);

        // 질문 가져오기
        memory_db.getReference(path_m).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void check(final String answerValue, String edit) {
        // 증가된 questionCounter 값을 저장
        questionCounter++;
        Log.d("questionCounter", "questionCounter : " + questionCounter);

        if (answerValue.equals(edit)) {
            // 1
            //answerReference.child("A").child("A" + String.format(Locale.getDefault(), "%02d", questionCounter)).setValue(1);
            one++;
            Log.d("editText", "editText : " + edit);
            Log.d("맞음", "맞음 : " + 1);
            Log.d("one", "one : " + one);
        } else {
            // 0
            //answerReference.child("A").child("A" + String.format(Locale.getDefault(), "%02d", questionCounter)).setValue(0);
            zero++;
            Log.d("editText", "editText : " + edit);
            Log.d("틀림", "틀림 : " + 0);
            Log.d("zero", "zero : " + zero);
        }
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

        EditText editText_ex = findViewById(R.id.editText_ex);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String recognizedText = result.get(0);
                    // 음성 인식 결과를 두 번째 화면의 텍스트뷰에 표시
                    editText_ex.setText(recognizedText);
                }
            }
        }
    }
}