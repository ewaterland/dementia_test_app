package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class Register2Fragment extends Fragment {

    private FirebaseAuth mAuth;
    public String id;

    // 입력 있으면 1, 입력 없으면 0
    int name_active = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean famaleisSelected = false;
    private boolean maleisSelected = false;

    TextView text_before;

    private TextView text_next;

    EditText editText_name;
    EditText editText_gender;
    EditText editText_birth;

    String name;
    String gender;
    String birth;

    Button btn_register;
    Button btn_female;
    Button btn_male;

    public InfoModel infoModel;
    private View  rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register2, container, false);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);


        // 저장된 데이터 가져오기
        String savedName = infoModel.getInputName();
        //Number savedGender = infoModel.getInputGender();
        //Number savedBirth = infoModel.getInputBirth();

        // 입력 데이터 선언
        editText_name = rootView.findViewById(R.id.editText_Name);
        //editText_gender = rootView.findViewById(R.id.editText_Gender);
        //editText_birth = rootView.findViewById(R.id.editText_Birth);

        // 가져온 데이터 사용
        editText_name.setText(savedName);
        //editText_gender.setText(savedGender);
        //editText_birth.setText(savedBirth);

        /*

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    email_active = 1;
                    check();
                } else {
                    email_active = 0;
                    check();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


         */
        /*
        editText_gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    pw_active = 1;
                    check();
                } else {
                    pw_active = 0;
                    check();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editText_birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    pwc_active = 1;
                    check();
                } else {
                    pwc_active = 0;
                    check();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
         */

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

        // 회원가입 버튼 활성화 단계
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

        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleisSelected) // 만약 남성 버튼이 선택되어 있다면 비활성화
                {
                    maleisSelected = !maleisSelected;
                    btn_male.setSelected(maleisSelected);
                }
                famaleisSelected = !famaleisSelected;
                btn_female.setSelected(famaleisSelected);
            }
        });

        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (famaleisSelected) // 만약 여성 버튼이 선택되어 있다면 비활성화
                {
                    famaleisSelected = !famaleisSelected;
                    btn_female.setSelected(famaleisSelected);
                }
                maleisSelected = !maleisSelected;
                btn_male.setSelected(maleisSelected);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register2 - onPause");

        // 회원가입을 위한 정보 전달
        String inputName = editText_name.getText().toString();
        //String inputGender = editText_gender.getText().toString();
        //String inputBirth = editText_birth.getText().toString();

        infoModel.setInputName(inputName);
        //infoModel.setInputGender(inputGender);
        //infoModel.setInputBirth(inputBirth);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register2 - onResume");

        // 회원가입을 위한 정보 읽기
        String inputName = infoModel.getInputName();
        //String inputGender = infoModel.getInputGender();
        //String inputBirth = infoModel.getInputBirth();

        editText_name.setText(inputName);
        //editText_gender.setText(inputGender);
        //editText_birth.setText(inputBirth);
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {

        btn_register = (Button) rootView.findViewById(R.id.btn_signup);

        if (name_active == 1) {
            btn_register.setEnabled(true);
        } else {
            btn_register.setEnabled(false);
        }
    }
}
