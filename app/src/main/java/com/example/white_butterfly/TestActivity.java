package com.example.white_butterfly;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TestActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE_PERMISSION = 1001;

    // TAG
    private String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_good);

        Log.w(TAG, "--- TestActivity ---");

        // 치매안심센터 전화 연결 버튼 누른 경우
        Button btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                } else {
                    // 권한이 이미 있으면 전화 걸기 로직을 실행
                    String tel = "tel:18999988";
                    //startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
            }
        });
    }
}