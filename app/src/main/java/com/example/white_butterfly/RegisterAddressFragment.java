package com.example.white_butterfly;

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

public class RegisterAddressFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 이전, 다음 버튼
    TextView text_before;
    TextView text_next;

    // 입력 있으면 1, 입력 없으면 0
    int address_active = 0;

    // 정보 입력칸
    EditText editText_address;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;

    // 태그
    private static final String TAG = "RegisterAddressFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_address, container, false);

        Log.w(TAG, "--- RegisterAddressFragment ---");

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
        String savedAddress = infoModel.getInputName();

        // 입력 데이터 선언
        editText_address = rootView.findViewById(R.id.editText_Address);

        // 가져온 데이터 사용
        editText_address.setText(savedAddress);

        // 이전 버튼 누른 경우
        TextView text_before = rootView.findViewById(R.id.textView_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "이전 버튼 누름");

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterGenderFragment());
                transaction.commit();

                rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
            }
        });

        // 다음 버튼 누른 경우
        text_next.setOnClickListener(v -> {
            Log.w(TAG, "다음 버튼 누름");

            int textColor = text_next.getCurrentTextColor();
            if (textColor == ContextCompat.getColor(requireContext(), R.color.main)) {
                onNextButtonClick();
            }
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) { address_active = 1; check(); }
                else           { address_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Address - Next");

        String inputAddress = editText_address.getText().toString();
        infoModel.setInputAddress(inputAddress);

        if (inputAddress.length() > 0) { docRef.update("Address", inputAddress); navigateToNextFragment(); }
        else { Toast.makeText(getContext(), "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show(); }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Fragment fragment2 = new RegisterSchoolFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_fragment, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_Address - onPause");

        // 회원가입을 위한 정보 전달
        String inputAddress = editText_address.getText().toString();

        infoModel.setInputAddress(inputAddress);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Address - onResume");

        // 회원가입을 위한 정보 읽기
        String inputAddress = infoModel.getInputAddress();

        editText_address.setText(inputAddress);

        if (inputAddress != null)
        {
            editText_address.setText(inputAddress);
        }
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (address_active == 1) {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.main));
        } else {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.unable));
        }
    }
}
