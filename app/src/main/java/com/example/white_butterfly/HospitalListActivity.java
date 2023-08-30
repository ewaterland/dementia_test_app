package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HospitalListActivity extends AppCompatActivity {

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "ReservationListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        this.InitializeHospitalData();

        ListView listView = (ListView)findViewById(R.id.hospital_list);
        final HospitalAdapter hospitalAdapter = new HospitalAdapter(this, hospitalDataList);

        listView.setAdapter(hospitalAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                HospitalData clickedItem = (HospitalData) parent.getItemAtPosition(position);

                String hospitalName = clickedItem.getHospital_name();
                String hospitalSub = clickedItem.getHospital_sub();
                String hospitalAddress = clickedItem.getHospital_adr();

                Log.w(TAG, "clickedItem: " + clickedItem);

                Intent intent = new Intent(getApplication(), HospitalInfoActivity.class);
                intent.putExtra("hospitalName", hospitalName);
                intent.putExtra("hospitalSub", hospitalSub);
                intent.putExtra("hospitalAddress", hospitalAddress);

                startActivity(intent);
            }
        });

        TextView text_reservation = findViewById(R.id.text_reservation);
        TextView text_name = findViewById(R.id.text_name);
        TextView text_sub = findViewById(R.id.text_sub);
        TextView text_adr = findViewById(R.id.text_address);
        text_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), HospitalInfoActivity.class);
                intent.putExtra("hospitalName", text_name.getText());
                intent.putExtra("hospitalSub", text_sub.getText());
                intent.putExtra("hospitalAddress", text_adr.getText());

                startActivity(intent);
            }
        });
    }

    public void InitializeHospitalData()
    {
        hospitalDataList = new ArrayList<HospitalData>();

        hospitalDataList.add(new HospitalData(R.drawable.icon_star,"해피뷰병원", "종합병원","광주광역시 북구 유동"));
        hospitalDataList.add(new HospitalData(R.drawable.icon_star,"북구치매주간병원", "요양병원","광주광역시 북구 태봉로"));

        hospitalDataList.add(new HospitalData(R.color.back,"중앙신경과의원", "신경과","광주광역시 서구 금호동"));
        hospitalDataList.add(new HospitalData(R.color.back,"허욱신경과의원", "신경과","광주광역시 서구 금호동"));
    }
}
