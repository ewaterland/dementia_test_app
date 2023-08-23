package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TestMainActivity extends AppCompatActivity {

    private static final String TAG = "TestMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        Log.w(TAG, "--- TestMainActivity ---");

        Button btn_testStart = (Button) findViewById(R.id.btn_testStart);
        btn_testStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestActivity_old.class);
                startActivity(intent);
            }
        });
    }
}