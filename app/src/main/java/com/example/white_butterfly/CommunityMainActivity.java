package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityMainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference docRef;
    private Button writeButton;
    private ListView communitylistView;
    String id;
    private static final String TAG = "CommunityMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_main);

        Log.w(TAG, "--- CommunityMainActivity ---");

        FirebaseApp.initializeApp(CommunityMainActivity.this);
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

        communitylistView = findViewById(R.id.community_list);
        writeButton = findViewById(R.id.btn_add);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityMainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        // 리스트뷰 초기화
        ArrayList<String> nodeNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nodeNames);
        communitylistView.setAdapter(adapter);
        int atIndex = id.indexOf('@');
        if (atIndex != -1) {
            String sanitizedId = id.substring(0, atIndex);

            // 데이터베이스에서 하위 노드 이름을 가져와서 리스트뷰에 추가
            docRef.child(sanitizedId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    nodeNames.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String nodeName = snapshot.getKey();
                        if (nodeName != null) {
                            nodeNames.add(nodeName);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "데이터 가져오기 실패: " + databaseError.getMessage());
                }
            });
        }
    }
}