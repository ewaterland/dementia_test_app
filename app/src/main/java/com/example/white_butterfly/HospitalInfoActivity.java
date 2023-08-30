package com.example.white_butterfly;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HospitalInfoActivity extends AppCompatActivity {

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "HospitalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        String count = getIntent().getStringExtra("count");
        TextView text_title = findViewById(R.id.text_title);
        TextView text_sub = findViewById(R.id.text_sub);
        TextView text_adr = findViewById(R.id.text_adr);

        switch (count)
        {
            case ("0"):
                text_title.setText("청심병원");
                text_sub.setText("정신건강의학과");
                text_adr.setText("광주광역시 동구 운림동");
                break;
            case ("1"):
                text_title.setText("북구치매주간병원");
                text_sub.setText("요양병원");
                text_adr.setText("광주광역시 북구 태봉로");
                break;
            case ("2"):
                text_title.setText("해피뷰병원");
                text_sub.setText("종합병원");
                text_adr.setText("광주광역시 북구 유동");
                break;
            case ("3"):
                text_title.setText("맑은머리 김동욱 신경과 의원");
                text_sub.setText("신경과");
                text_adr.setText("광주광역시 북구 누문동");
                break;
            case ("4"):
                text_title.setText("중앙신경과의원");
                text_sub.setText("신경과");
                text_adr.setText("광주광역시 서구 금호동");
                break;
            case ("5"):
                text_title.setText("하나로신경과의원");
                text_sub.setText("신경과");
                text_adr.setText("광주광역시 남구 진월동");
                break;
        }
    }
}
