package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register2Fragment extends Fragment {

    // 입력 있으면 1, 입력 없으면 0
    int name_active = 0;
    int gender_active = 0;
    int yy_active = 0;
    int mm_active = 0;
    int dd_active = 0;
    private boolean femaleisSelected = false;
    private boolean maleisSelected = false;

    TextView text_before;

    private TextView text_next;

    EditText editText_name;
    EditText editText_yyyy;
    EditText editText_mm;
    EditText editText_dd;

    Button btn_register;
    Button btn_female;
    Button btn_male;

    public InfoModel infoModel;
    private View  rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register2, container, false);

        Log.w(TAG, "--- Register2Fragment ---");

        // Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        DocumentReference docRef = db.collection("Users").document(id);

        Log.w(TAG, "User: " + id);

        btn_register = (Button) rootView.findViewById(R.id.btn_signup);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);

        // 저장된 데이터 가져오기
        String savedName = infoModel.getInputName();
        Number savedGender = infoModel.getInputGender();
        String savedYear = infoModel.getInputYear();
        String savedMonth = infoModel.getInputMonth();
        String savedDay = infoModel.getInputDay();

        // 입력 데이터 선언
        editText_name = rootView.findViewById(R.id.editText_Name);
        editText_yyyy = rootView.findViewById(R.id.editText_year);
        editText_mm = rootView.findViewById(R.id.editText_month);
        editText_dd = rootView.findViewById(R.id.editText_day);

        // 가져온 데이터 사용
        editText_name.setText(savedName);
        editText_yyyy.setText(savedYear);
        editText_mm.setText(savedMonth);
        editText_dd.setText(savedDay);
        if (savedGender != null)
        {
            if (savedGender.equals(0)) { btn_male.setSelected(true); }
            else if (savedGender.equals(1)) { btn_female.setSelected(true); }
        }

        // 이전 버튼 누른 경우
        TextView text_before = rootView.findViewById(R.id.text_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(); // 이전 프래그먼트로 되돌아가기

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new Register1Fragment());
                transaction.commit();

                //rootView.findViewById(R.id.page_register2).setVisibility(View.GONE);
            }
        });

        // 회원가입 버튼 누른 경우
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) rootView.findViewById(R.id.editText_Name)).getText().toString();
                String year = ((EditText) rootView.findViewById(R.id.editText_year)).getText().toString();
                String month = ((EditText) rootView.findViewById(R.id.editText_month)).getText().toString();
                String day = ((EditText) rootView.findViewById(R.id.editText_day)).getText().toString();
                String Birth = String.format("%04d%02d%02d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                String gender = "";

                if (maleisSelected) { gender = "남성"; }
                else if (femaleisSelected) { gender = "여성"; }

                // Firebase에 업로드
                docRef.update("Name", name);
                docRef.update("Birth", Birth);
                docRef.update("Gender", gender);
                //docRef.update("My", My);
                //docRef.update("Guardian", Guardian);

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


        // 정보가 입력됐을 때 회원가입 버튼 활성화를 위함
        btn_female = (Button) rootView.findViewById(R.id.btn_female);
        btn_male = (Button) rootView.findViewById(R.id.btn_male);

        editText_name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { name_active = 1; check(); }
                else              { name_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_yyyy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) { yy_active = 1; check(); }
                else           { yy_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_mm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) { mm_active = 1; check(); }
                else           { mm_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_dd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    dd_active = 1;
                    check();
                } else {
                    dd_active = 0;
                    check();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleisSelected) // 만약 남성 버튼이 선택되어 있다면 비활성화
                {
                    maleisSelected = !maleisSelected;
                    btn_male.setSelected(maleisSelected);
                }
                femaleisSelected = !femaleisSelected;
                btn_female.setSelected(femaleisSelected);
                gender_state_check();
            }
        });

        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleisSelected) // 만약 여성 버튼이 선택되어 있다면 비활성화
                {
                    femaleisSelected = !femaleisSelected;
                    btn_female.setSelected(femaleisSelected);
                }
                maleisSelected = !maleisSelected;
                btn_male.setSelected(maleisSelected);
                gender_state_check();
            }
        });

        return rootView;
    }

    private void gender_state_check()
    {
        if (!femaleisSelected && !maleisSelected)
        {
            gender_active = 0;
        }
        else { gender_active = 1; }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register2 - onPause");

        // 회원가입을 위한 정보 전달
        String inputName = editText_name.getText().toString();
        Number inputGender = 3;
        String inputYear = editText_yyyy.getText().toString();
        String inputMonth = editText_mm.getText().toString();
        String inputDay = editText_dd.getText().toString();

        if (maleisSelected) { inputGender = 0; }
        else if (femaleisSelected) { inputGender = 1; }

        infoModel.setInputName(inputName);
        infoModel.setInputGender(inputGender);
        infoModel.setInputYear(inputYear);
        infoModel.setInputMonth(inputMonth);
        infoModel.setInputDay(inputDay);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register2 - onResume");

        // 회원가입을 위한 정보 읽기
        String inputName = infoModel.getInputName();
        Number inputGender = infoModel.getInputGender();
        String inputYear = infoModel.getInputYear();
        String inputMonth = infoModel.getInputMonth();
        String inputDay = infoModel.getInputDay();

        editText_name.setText(inputName);

        if (inputGender != null)
        {
            if (inputGender.equals(0)) { btn_male.setSelected(true); }
            else if (inputGender.equals(1)) { btn_female.setSelected(true); }
        }
        if (inputYear != null)
        {
            editText_yyyy.setText(inputYear);
        }
        if (inputMonth != null)
        {
            editText_mm.setText(inputMonth);
        }
        if (inputDay != null)
        {
            editText_dd.setText(inputDay);
        }
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (name_active == 1 && yy_active == 1 && mm_active == 1 && dd_active == 1 && gender_active == 1) {
            btn_register.setEnabled(true);
        } else {
            btn_register.setEnabled(false);
        }
    }
}
