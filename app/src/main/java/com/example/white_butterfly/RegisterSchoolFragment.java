package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterSchoolFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    String id;

    // 페이지 이전, 다음 버튼
    TextView text_before;
    Button btn_next;

    // 정보 선택 버튼
    RadioButton radio_no;
    RadioButton radio_ele;
    RadioButton radio_mid;
    RadioButton radio_high;
    RadioButton radio_univ;
    int SchoolSelect = 0;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;

    // 태그
    private static final String TAG = "RegisterSchoolFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_school, container, false);

        Log.w(TAG, "--- RegisterSchoolFragment ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        Log.w(TAG, "User: " + id);

        btn_next = rootView.findViewById(R.id.btn_next);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
        infoModel.setInitialValues();

        // 저장된 데이터 가져오기
        int savedSchool = infoModel.getInputSchool();

        // 입력 데이터 선언
        radio_no = rootView.findViewById(R.id.radio_NoSchool);
        radio_ele = rootView.findViewById(R.id.radio_Elementary);
        radio_mid = rootView.findViewById(R.id.radio_Middle);
        radio_high = rootView.findViewById(R.id.radio_High);
        radio_univ = rootView.findViewById(R.id.radio_Univ);

        // 가져온 데이터 사용
        switch (savedSchool)
        {
            case 1:
                radio_no.setSelected(true);
                break;
            case 2:
                radio_ele.setSelected(true);
                break;
            case 3:
                radio_mid.setSelected(true);
                break;
            case 4:
                radio_high.setSelected(true);
                break;
            case 5:
                radio_univ.setSelected(true);
                break;
            default:
                break;
        }

        // 라디오 버튼 선택
        radio_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolSelect = 1;
            }
        });

        radio_ele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolSelect = 2;
            }
        });

        radio_mid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolSelect = 3;
            }
        });

        radio_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolSelect = 4;
            }
        });

        radio_univ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SchoolSelect = 5;
            }
        });

        RadioGroup radioGroup = rootView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    btn_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.mint));
                } else {
                    btn_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.unable));
                }
            }
        });

        // 이전 버튼 누른 경우
        text_before = rootView.findViewById(R.id.textView_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterAddressFragment());
                transaction.commit();

                rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
            }
        });

        // 다음 버튼 누른 경우
        btn_next.setOnClickListener(v -> {
            Log.w(TAG, "다음 버튼 누름");

            onNextButtonClick();
        });

        return rootView;
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_School - Next");

        int inputSchool = SchoolSelect;
        infoModel.setInputSchool(inputSchool);
        Number school = infoModel.getInputSchool();

        // Firebase에 업로드
        docRef.update("School", school);

        Intent intent = new Intent(requireActivity(), RegisterFinishActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_School - onPause");

        // 회원가입을 위한 정보 전달
        int inputSchool = SchoolSelect;

        infoModel.setInputSchool(inputSchool);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_School - onResume");

        // 회원가입을 위한 정보 읽기
        int inputSchool = infoModel.getInputSchool();

        // 가져온 데이터 사용
        switch (inputSchool)
        {
            case 1:
                radio_no.setSelected(true);
                break;
            case 2:
                radio_ele.setSelected(true);
                break;
            case 3:
                radio_mid.setSelected(true);
                break;
            case 4:
                radio_high.setSelected(true);
                break;
            case 5:
                radio_univ.setSelected(true);
                break;
            default:
                break;
        }
    }
}
