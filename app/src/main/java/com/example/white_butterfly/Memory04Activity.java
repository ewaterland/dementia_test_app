package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import java.util.Map;

public class Memory04Activity extends AppCompatActivity {
    private DatabaseReference answerReference;
    private TextView resultTextView;
    private Button endButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory04);

        resultTextView = findViewById(R.id.resultTextView);
        endButton = findViewById(R.id.end);

        int questionCounter = 1;

        answerReference = FirebaseDatabase.getInstance().getReference("Answer");

        answerReference.child("A").child("A" + String.format("%02d", questionCounter)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count1 = 0;
                int count0 = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    if (value != null) {
                        if (value.equals("1")) {
                            count1++;
                        } else {
                            count0++;
                        }
                    }
                }

                // Log the counts of 1 and 0 values
                Log.d("Counts", "Count of 1 values: " + count1);
                Log.d("Counts", "Count of 0 values: " + count0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Memory04Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}