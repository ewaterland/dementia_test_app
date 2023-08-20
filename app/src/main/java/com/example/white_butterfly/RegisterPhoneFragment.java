package com.example.white_butterfly;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Locale;

public class RegisterPhoneFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 이전, 다음 버튼
    TextView text_before;
    TextView text_next;

    // 입력 있으면 1, 입력 없으면 0
    int myfirst_active = 0;
    int mymiddle_active = 0;
    int mylast_active = 0;
    int guardfirst_active = 0;
    int guardmiddle_active = 0;
    int guardlast_active = 0;

    // 정보 입력칸
    EditText editText_MyFirst;
    EditText editText_MyMiddle;
    EditText editText_MyLast;
    EditText editText_GuardianFirst;
    EditText editText_GuardianMiddle;
    EditText editText_GuardianLast;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;
    private static final String TAG = "RegisterPhoneFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_phone, container, false);

        Log.w(TAG, "--- RegisterPhoneFragment ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        Log.w(TAG, "User: " + id);

        text_next = rootView.findViewById(R.id.text_next);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
        infoModel.setInitialValues();

        // 저장된 데이터 가져오기
        String savedMyFist = infoModel.getInputMyFirst();
        String savedMyMiddle = infoModel.getInputMyMiddle();
        String savedMyLast = infoModel.getInputMyLast();

        String savedGuardianFirst = infoModel.getInputGuardianFirst();
        String savedGuardianMiddle = infoModel.getInputGuardianMiddle();
        String savedGuardianLast = infoModel.getInputGuardianLast();

        // 입력 데이터 선언
        editText_MyFirst = rootView.findViewById(R.id.editText_MyFirst);
        editText_MyMiddle = rootView.findViewById(R.id.editText_MyMiddle);
        editText_MyLast = rootView.findViewById(R.id.editText_MyLast);

        editText_GuardianFirst = rootView.findViewById(R.id.editText_GuardianFirst);
        editText_GuardianMiddle = rootView.findViewById(R.id.editText_GuardianMiddle);
        editText_GuardianLast = rootView.findViewById(R.id.editText_GuardianLast);

        // 가져온 데이터 사용
        editText_MyFirst.setText(savedMyFist);
        editText_MyMiddle.setText(savedMyMiddle);
        editText_MyLast.setText(savedMyLast);

        editText_GuardianFirst.setText(savedGuardianFirst);
        editText_GuardianMiddle.setText(savedGuardianMiddle);
        editText_GuardianLast.setText(savedGuardianLast);

        if (savedMyLast.length() == 4)
        {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.main));
        }

        // 이전 버튼 누른 경우
        TextView text_before = rootView.findViewById(R.id.textView_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterNameFragment());
                transaction.commit();

                rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
            }
        });

        // 다음 버튼 누른 경우
        text_next.setOnClickListener(v -> {
            int textColor = text_next.getCurrentTextColor();
            if (textColor == ContextCompat.getColor(requireContext(), R.color.main)) {
                onNextButtonClick();
            }
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_MyFirst.addTextChangedListener(textWatcher);
        editText_MyMiddle.addTextChangedListener(textWatcher);
        editText_MyLast.addTextChangedListener(textWatcher);

        editText_GuardianFirst.addTextChangedListener(textWatcher);
        editText_GuardianMiddle.addTextChangedListener(textWatcher);
        editText_GuardianLast.addTextChangedListener(textWatcher);

        return rootView;
    }

    // 입력에 따라 다음 버튼 활성화
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            myfirst_active = editText_MyFirst.length() > 0 ? 1 : 0;
            mymiddle_active = editText_MyMiddle.length() > 0 ? 1 : 0;
            mylast_active = editText_MyLast.length() > 0 ? 1 : 0;

            guardfirst_active = editText_GuardianFirst.length() > 0 ? 1 : 0;
            guardmiddle_active = editText_GuardianMiddle.length() > 0 ? 1 : 0;
            guardlast_active = editText_GuardianLast.length() > 0 ? 1 : 0;

            check();
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Phone - Next");

        String inputMyFirst = editText_MyFirst.getText().toString();
        String inputMyMiddle = editText_MyMiddle.getText().toString();
        String inputMyLast = editText_MyLast.getText().toString();

        infoModel.setInputMyFirst(inputMyFirst);
        infoModel.setInputMyMiddle(inputMyMiddle);
        infoModel.setInputMyLast(inputMyLast);

        String inputGuardianFirst = editText_GuardianFirst.getText().toString();
        String inputGuardianMiddle = editText_GuardianMiddle.getText().toString();
        String inputGuardianLast = editText_GuardianLast.getText().toString();

        infoModel.setInputGuardianFirst(inputGuardianFirst);
        infoModel.setInputGuardianMiddle(inputGuardianMiddle);
        infoModel.setInputGuardianLast(inputGuardianLast);

        String my = String.format(Locale.KOREA,"%03d-%04d-%4d", Integer.parseInt(inputMyFirst), Integer.parseInt(inputMyMiddle), Integer.parseInt(inputMyLast));
        Log.w(TAG, "my: " + my);

        String guardian = String.format(Locale.KOREA,"%s-%s-%s", inputGuardianFirst, inputGuardianMiddle, inputGuardianLast);
        Log.w(TAG, "guardian: " + guardian);

        if (inputMyFirst.equals("010") || inputMyFirst.equals("011"))
        {
            docRef.update("My", my);
            docRef.update("Guardian", guardian);
            navigateToNextFragment();
        } else { Toast.makeText(getContext(), "연락처를 입력해 주세요.", Toast.LENGTH_SHORT).show(); }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Fragment fragment2 = new RegisterBirthFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_fragment, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        View myView = rootView.findViewById(R.id.page_register);

        if (myView != null) {
            myView.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "my_view_id not found");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_Phone - onPause");

        // 회원가입을 위한 정보 전달
        String inputMyFirst = editText_MyFirst.getText().toString();
        String inputMyMiddle = editText_MyMiddle.getText().toString();
        String inputMyLast = editText_MyLast.getText().toString();

        infoModel.setInputMyFirst(inputMyFirst);
        infoModel.setInputMyMiddle(inputMyMiddle);
        infoModel.setInputMyLast(inputMyLast);

        String inputGuardianFirst = editText_GuardianFirst.getText().toString();
        String inputGuardianMiddle = editText_GuardianMiddle.getText().toString();
        String inputGuardianLast = editText_GuardianLast.getText().toString();

        infoModel.setInputGuardianFirst(inputGuardianFirst);
        infoModel.setInputGuardianMiddle(inputGuardianMiddle);
        infoModel.setInputGuardianLast(inputGuardianLast);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Phone - onResume");

        //infoModel.setInitialValues();

        // 회원가입을 위한 정보 읽기
        String inputMyFirst = infoModel.getInputMyFirst();
        String inputMyMiddle = infoModel.getInputMyMiddle();
        String inputMyLast = infoModel.getInputMyLast();

        editText_MyFirst.setText(inputMyFirst);
        editText_MyMiddle.setText(inputMyMiddle);
        editText_MyLast.setText(inputMyLast);

        String inputGuardianFirst = infoModel.getInputGuardianFirst();
        String inputGuardianMiddle = infoModel.getInputGuardianMiddle();
        String inputGuardianLast = infoModel.getInputGuardianLast();

        editText_GuardianFirst.setText(inputGuardianFirst);
        editText_GuardianMiddle.setText(inputGuardianMiddle);
        editText_GuardianLast.setText(inputGuardianLast);
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (myfirst_active == 1 && mymiddle_active == 1 && mylast_active == 1) {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.main));
        } else {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.unable));
        }
    }
}
