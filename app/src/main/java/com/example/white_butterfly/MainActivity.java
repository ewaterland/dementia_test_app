package com.example.white_butterfly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 광고 배너
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;

    // 태그
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(TAG, "--- MainActivity ---");

        /**
         * 가로 슬라이드 뷰 Fragment
         */

        //ViewPager2
        mPager = findViewById(R.id.viewpager);
        //Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        //Indicator
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page,0);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        /**
         * 이 부분 조정하여 처음 시작하는 이미지 설정.
         * 2000장 생성하였으니 현재위치 1002로 설정하여
         * 좌 우로 슬라이딩 할 수 있게 함. 거의 무한대로
         */

        mPager.setCurrentItem(999); //시작 지점
        mPager.setOffscreenPageLimit(3); //최대 이미지 수

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%num_page);
            }
        });

        /*
        adRecyclerView = findViewById(R.id.view_ad);
        adList = new ArrayList<>();
        adList.add(R.drawable.image_ad_1);
        adList.add(R.drawable.image_ad_2);
        adList.add(R.drawable.image_ad_3);

        adAdapter = new AdAdapter(adList);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adRecyclerView.setAdapter(adAdapter);

        ImageView ad_count_1 = (ImageView) findViewById(R.id.ad_count_1);
        ad_count_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageView ad_count_2 = (ImageView) findViewById(R.id.ad_count_2);
        ad_count_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageView ad_count_3 = (ImageView) findViewById(R.id.ad_count_3);
        ad_count_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         */

        Button btn_dementiaTest = (Button) findViewById(R.id.btn_dementiaTest);
        btn_dementiaTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestMainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_rememberTest = (Button) findViewById(R.id.btn_rememberTest);
        btn_rememberTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Memory01Activity.class);
                startActivity(intent);
            }
        });

        Button btn_chatbot = (Button) findViewById(R.id.btn_chatbot);
        btn_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ChatbotActivity.class);
                startActivity(intent);
            }
        });

        ImageView image_profile = (ImageView) findViewById(R.id.image_profile);
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            super.onBackPressed(); // 앱 종료
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void Center(View target)
    {
        Intent intent = new Intent(getApplication(), CenterActivity.class);
        startActivity(intent);
    }

    public void Naver(View target)
    {
        // 네이버 홈페이지 URL
        String naverUrl = "https://cafe.naver.com/whitebutterflys2";

        // 인텐트 생성 및 웹 브라우저로 이동
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(naverUrl));
        startActivity(intent);
    }
}
