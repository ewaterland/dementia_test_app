package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db;
    FirebaseUser currentUser;
    DocumentReference docRef;

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 광고 배너
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    //private CircleIndicator3 mIndicator;
    String id = "";

    // 뷰
    TextView text_UserName;
    TextView text_FinalDay;
    private ProgressBar loadBar;

    // 태그
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(TAG, "--- MainActivity ---");

        initializeViews();

        getToken();

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = currentUser.getEmail();

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        getData();

        //ViewPager2
        mPager = findViewById(R.id.viewpager);

        //Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        /*
        //Indicator
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page,0);
         */

        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
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

            /*
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%num_page);
            }

             */
        });

        ConstraintLayout btn_dementiaTest = findViewById(R.id.btn_dementia);
        btn_dementiaTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), TestMainActivity.class);
                startActivity(intent);
            }
        });

        ConstraintLayout btn_memoryTest = findViewById(R.id.btn_memory);
        btn_memoryTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Memory01Activity.class);
                startActivity(intent);
            }
        });

        ConstraintLayout btn_chatbot = findViewById(R.id.btn_chatbot);
        btn_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ChatbotMainActivity.class);
                startActivity(intent);
            }
        });

        TextView text_myPage = findViewById(R.id.text_myPage);
        text_myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), UserActivity.class);
                startActivity(intent);
            }
        });
    }

    void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("My Token",token);

                /* 로그인 된 계정에 업로드 된다는 가정으로 코드를 작성함 */
                // id: 해당 유저의 이메일(아이디)
                FirebaseFirestore.getInstance().collection("Users").document(id).update("fcmToken",token);
            }
        });
    }

    ///////////////////////////////// 뷰 관련

    private void initializeViews() {
        text_UserName = findViewById(R.id.text_UserName);
        text_FinalDay = findViewById(R.id.text_finalDay);
        loadBar = findViewById(R.id.loadBar);

        // 현재 로그인 된 유저 정보 읽기
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);
    }

    private class getDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩 화면 표시
            loadBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 백그라운드 작업 수행
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        text_UserName.setText(documentSnapshot.getString("Name"));
                        int year = Integer.parseInt(documentSnapshot.getLong("year").toString());
                        int month = Integer.parseInt(documentSnapshot.getLong("month").toString());
                        int day = Integer.parseInt(documentSnapshot.getLong("day").toString());

                        setData(year, month, day);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error getting document", e);
                }
            });

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(View.GONE);
        }
    }

    private void getData() {
        // AsyncTask 실행
        new MainActivity.getDataTask().execute();
    }

    private void setData(int year, int month, int day) {
        try {
            // 테스트 디데이 계산
            LocalDate today = LocalDate.now();
            LocalDate Testday;
            Testday = LocalDate.of(year, month, day);

            long daysBetween = ChronoUnit.DAYS.between(Testday, today);
            text_FinalDay.setText(String.valueOf(daysBetween));
        } catch (Exception e) {
            Log.w(TAG, "Error: " + e);
        }
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
