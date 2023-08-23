package com.example.white_butterfly;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MypageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);


        //startActivity(new Intent(MypageActivity.this, MainActivity.class));
        //overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        //finish();

    }
}