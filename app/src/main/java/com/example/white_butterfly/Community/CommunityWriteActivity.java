package com.example.white_butterfly.Community;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommunityWriteActivity extends AppCompatActivity {
    private EditText edit_title;
    private EditText edit_content;
    private Button writeButton;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference docRef;
    String id;
    private static final String TAG = "CommunityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        Log.w(TAG, "--- CommunityActivity ---");

        initializeViews();

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(CommunityWriteActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        docRef = firebaseDatabase.getReference();

        String kakao_email = getIntent().getStringExtra("Email");
        if (kakao_email != null && !kakao_email.isEmpty()) {
            // 이전 액티비티에서 받아온 값이 있는 경우
            id = kakao_email;
        } else {
            // 이전 액티비티에서 값이 없는 경우
            id = currentUser.getEmail();
        }
        Log.d(TAG, id);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFirebase();
            }
        });
    }

    private void initializeViews() {
        edit_title = findViewById(R.id.title);
        edit_content = findViewById(R.id.content);
        writeButton = findViewById(R.id.btn_add);
    }

    private void writeToFirebase() {
        String nodetitle = edit_title.getText().toString();
        String nodecontent = edit_content.getText().toString();

        // 빈 값이 아닐 경우에만 Firebase에 저장
        if (!nodetitle.isEmpty() && !nodecontent.isEmpty()) {
            // 이메일 주소에서 @ 앞까지를 사용
            int atIndex = id.indexOf('@');
            if (atIndex != -1) {
                String sanitizedId = id.substring(0, atIndex);

                DatabaseReference newNodeReference = docRef.child(sanitizedId).child(nodetitle);
                newNodeReference.setValue(nodecontent)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CommunityWriteActivity.this, "저장 성공", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "< 저장 성공 >");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommunityWriteActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "저장 실패: " + e.getMessage());
                            }
                        });
            } else {
                Log.e(TAG, "잘못된 형식의 이메일 주소");
            }
        }
    }
}