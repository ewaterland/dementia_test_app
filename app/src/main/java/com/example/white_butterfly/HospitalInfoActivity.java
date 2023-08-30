package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HospitalInfoActivity extends AppCompatActivity {
    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "HospitalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        TextView text_title = findViewById(R.id.text_title);
        TextView text_sub = findViewById(R.id.text_sub);
        TextView text_adr = findViewById(R.id.text_adr);

        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalSub = getIntent().getStringExtra("hospitalSub");
        String hospitalAddress = getIntent().getStringExtra("hospitalAddress");

        text_title.setText(hospitalName);
        text_sub.setText(hospitalSub);
        text_adr.setText(hospitalAddress);
    }

    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            Intent intent = new Intent(getApplication(), HospitalListActivity.class);
            startActivity(intent);
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 예약이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
