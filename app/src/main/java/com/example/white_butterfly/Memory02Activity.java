package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Random;

public class Memory02Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private DatabaseReference databaseReference;
    private DatabaseReference temporarilyReference;
    private TextView dataTextView;
    private Button randomAButton;
    private ImageButton speak;
    private Button nextButton;

    private List<String> startingWithQDataList;
    private Map<String, Boolean> usedQuestionsMap;

    private int questionCounter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory02);

        databaseReference = FirebaseDatabase.getInstance().getReference("Question");
        temporarilyReference = FirebaseDatabase.getInstance().getReference("Temporarily");

        dataTextView = findViewById(R.id.dataTextView);
        randomAButton = findViewById(R.id.randomAButton);
        speak = findViewById(R.id.speak);
        nextButton = findViewById(R.id.next);

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
            @Override
            public void onClick(View v) {
                String randomValue = getRandomStartingWithQValue();
                dataTextView.setText(randomValue);

                questionCounter++;

                if (questionCounter > 5) {
                    randomAButton.setEnabled(false);
                    nextButton.setEnabled(true);
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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NextButton을 눌렀을 때 수행할 작업
                Intent intent = new Intent(Memory02Activity.this, Memory03Activity.class);
                startActivity(intent);
            }
        });

        // 초기에는 NextButton은 비활성화 상태로 시작
        nextButton.setEnabled(false);
    }

    private String getRandomStartingWithQValue() {
        if (startingWithQDataList.isEmpty()) {
            return "질문을 찾을 수 없습니다.";
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
        temporarilyReference.child("Q" + String.format("%02d", questionCounter)).setValue(selectedQuestion);

        return selectedQuestion;
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