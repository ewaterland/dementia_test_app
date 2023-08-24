package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.w(TAG, "================== TEST FINISH ==================");

                //startActivity(new Intent(LoadingActivity.this, MemoryResultActivity.class));
                //finish();
                //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);


                Intent intent = new Intent(getApplicationContext(), MemoryResultActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}