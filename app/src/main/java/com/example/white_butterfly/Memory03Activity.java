package com.example.white_butterfly;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Memory03Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 200;
    private DatabaseReference databaseReference;
    private DatabaseReference temporarilyReference;
    private DatabaseReference emptyReference;
    private DatabaseReference answerReference;

    private List<String> startingWithQDataList;
    private Map<String, Boolean> usedQuestionsMap;

    private TextView dataTextView;
    private Button randomAButton;
    private ImageButton speak;
    private ImageButton microphone;
    private Button endButton;
    private String answerValue;
    private int questionCounter = 0; // 답변
    int questionCounter01 = 0; // 질문

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory03);

        databaseReference = FirebaseDatabase.getInstance().getReference("Question");
        temporarilyReference = FirebaseDatabase.getInstance().getReference("Temporarily");
        emptyReference = FirebaseDatabase.getInstance().getReference("Empty");
        answerReference = FirebaseDatabase.getInstance().getReference("Answer");

        dataTextView = findViewById(R.id.dataTextView);
        randomAButton = findViewById(R.id.randomAButton);
        speak = findViewById(R.id.speak);
        microphone = findViewById(R.id.microphone);
        endButton = findViewById(R.id.end);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);

        startingWithQDataList = new ArrayList<>();
        usedQuestionsMap = new HashMap<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startingWithQDataList.clear();
                usedQuestionsMap.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String value = snapshot.getValue(String.class);
                    assert key != null;
                    if (key.startsWith("Q")) {
                        startingWithQDataList.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        randomAButton.setOnClickListener(new View.OnClickListener() {
            boolean isFirstClick = true; // 처음 클릭 여부를 추적하기 위한 변수

            @Override
            public void onClick(View v) {
                Log.w(TAG, "next");

                // 증가된 questionCounter01 값을 저장
                questionCounter01++;
                Log.d("questionCounter01", "questionCounter01 : " + questionCounter01);

                if (questionCounter01 > 5) {
                    randomAButton.setEnabled(false);
                    endButton.setEnabled(true);
                }

                // 처음 클릭 시 버튼 이름을 '다음'으로 변경
                if (isFirstClick) {
                    randomAButton.setText("다음");
                    isFirstClick = false;
                }

                // Retrieve the value from Temporarily node
                temporarilyReference.child("Q" + String.format(Locale.getDefault(), "%02d", questionCounter01)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String temporaryValue = dataSnapshot.getValue(String.class);
                        if (temporaryValue != null) {
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String questionValue = snapshot.getValue(String.class);
                                        if (questionValue != null && questionValue.equals(temporaryValue)) {
                                            String questionName = snapshot.getKey();
                                            Log.d("QuestionMatch", "Match found for value: " + temporaryValue + ", question name: " + questionName);

                                            // Check if the matched questionName exists as a child under "Empty"
                                            assert questionName != null;
                                            emptyReference.child(questionName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot emptySnapshot) {
                                                    String emptyValue = emptySnapshot.getValue(String.class);
                                                    if (emptyValue != null) {
                                                        dataTextView.setText(emptyValue);

                                                        // Q랑 A비교
                                                        answerReference.child(questionName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot answerSnapshot) {
                                                                answerValue = answerSnapshot.getValue(String.class);
                                                                if (answerValue != null) {
                                                                    Log.d("Answer", "정답 : " + answerValue);
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

        // 종료 버튼 누를 경우
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Memory03Activity.this, TestLoadingActivity.class);
                startActivity(intent);
            }
        });

        // 초기에는 endButton 비활성화 상태로 시작
        endButton.setEnabled(false);
    }

    private void check(final String answerValue, String edit) {
        EditText editText_ex = findViewById(R.id.editText_ex);

        // 증가된 questionCounter 값을 저장
        questionCounter++;
        Log.d("questionCounter", "questionCounter : " + questionCounter);

        if (answerValue.equals(edit)) {
            // 1
            answerReference.child("A").child("A" + String.format(Locale.getDefault(), "%02d", questionCounter)).setValue(1);
            Log.d("editText", "editText : " + edit);
            Log.d("맞음", "맞음 : " + 1);
        } else {
            // 0
            answerReference.child("A").child("A" + String.format(Locale.getDefault(), "%02d", questionCounter)).setValue(0);
            Log.d("editText", "editText : " + edit);
            Log.d("틀림", "틀림 : " + 0);
        }

        // 에디트텍스트 내용 지우기
        editText_ex.setText("");
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
                    String edit = editText_ex.getText().toString();

                    Log.w(TAG, "recognizedText: " + recognizedText);
                    Log.w(TAG, "edit: " + edit);

                    // 에디트텍스트에 값이 있을 때만 결과 저장
                    if (!edit.isEmpty()) {
                        randomAButton.setEnabled(true);
                        Log.d("활성화", "활성화");

                        check(answerValue, edit);

                    /*
                    if (questionCounter > 5) {
                        randomAButton.setEnabled(false);
                        endButton.setEnabled(true);
                    }*/

                    }
                    else {
                        randomAButton.setEnabled(false);
                        Log.d("비활성화", "비활성화");
                    }
                }
            }
        }
    }
}