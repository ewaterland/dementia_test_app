package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HospitalListActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "ReservationListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        this.InitializeHospitalData();

        ListView listView = (ListView)findViewById(R.id.hospital_list);
        final HospitalAdapter hospitalAdapter = new HospitalAdapter(this, hospitalDataList);

        listView.setAdapter(hospitalAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Intent intent = new Intent(getApplication(), HospitalInfoActivity.class);
                intent.putExtra("count", String.valueOf(hospitalAdapter.getItem(position)));
                Log.w(TAG, "count: " + hospitalAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    public void InitializeHospitalData()
    {
        hospitalDataList = new ArrayList<HospitalData>();

        hospitalDataList.add(new HospitalData("청심병원", "정신건강의학과","광주광역시 동구 운림동"));
        hospitalDataList.add(new HospitalData("북구치매주간병원", "요양병원","광주광역시 북구 태봉로"));
        hospitalDataList.add(new HospitalData("해피뷰병원", "종합병원","광주광역시 북구 유동"));
        hospitalDataList.add(new HospitalData("맑은머리 김동욱 신경과 의원", "신경과","광주광역시 북구 누문동"));
        hospitalDataList.add(new HospitalData("중앙신경과의원", "신경과","광주광역시 서구 금호동"));
        hospitalDataList.add(new HospitalData("하나로신경과의원", "신경과","광주광역시 남구 진월동"));
    }
}
