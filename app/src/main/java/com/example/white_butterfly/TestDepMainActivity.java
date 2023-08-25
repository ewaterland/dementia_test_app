package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TestDepMainActivity extends AppCompatActivity {

    private static final String TAG = "TestDepMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dep_main);

        Log.w(TAG, "--- TestDepMainActivity ---");

        int score_cog = getIntent().getIntExtra("score_cog", 0);

        Button btn_testStart = (Button) findViewById(R.id.btn_testStart);
        btn_testStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_dep = new Intent(getApplication(), TestDepActivity.class);
                intent_dep.putExtra("score_cog", score_cog);
                startActivity(intent_dep);
            }
        });
    }
}