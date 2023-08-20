package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
                Intent intent = new Intent(getApplication(), Test1Activity.class);
                startActivity(intent);
            }
        });
    }
}