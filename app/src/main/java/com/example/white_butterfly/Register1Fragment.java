package com.example.white_butterfly;

import android.content.Intent;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register1Fragment extends Fragment {

    private FirebaseAuth mAuth;
    private String id;

    private int email_active = 0;
    private int pw_active = 0;
    private int pwc_active = 0;
    private TextView text_next;

    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_password_2;

    private InfoModel infoModel;
    private static final String TAG = "Register1Fragment";
    private View rootView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(TAG, "--- Register1Fragment ---");

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(requireActivity());
        mAuth = FirebaseAuth.getInstance();

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register1, container, false);

        initializeViews();

        return rootView;
    }

    private void initializeViews() {
        // 입력 데이터 선언
        editText_email = rootView.findViewById(R.id.editText_EmailAddress);
        editText_password = rootView.findViewById(R.id.editText_Password);
        editText_password_2 = rootView.findViewById(R.id.editText_Password_2);

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_email.addTextChangedListener(textWatcher);
        editText_password.addTextChangedListener(textWatcher);
        editText_password_2.addTextChangedListener(textWatcher);

        // 이전 버튼 누른 경우
        TextView text_before = rootView.findViewById(R.id.text_before);
        text_before.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // 다음 버튼 누른 경우
        text_next = rootView.findViewById(R.id.text_next);
        text_next.setOnClickListener(v -> {
            int textColor = text_next.getCurrentTextColor();
            if (textColor == ContextCompat.getColor(requireContext(), R.color.Human)) {
                onNextButtonClick();
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            email_active = editText_email.length() > 0 ? 1 : 0;
            pw_active = editText_password.length() > 0 ? 1 : 0;
            pwc_active = editText_password_2.length() > 0 ? 1 : 0;

            check();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void onNextButtonClick() {
        Log.w(TAG, "### Register - Next");

        String inputEmail = editText_email.getText().toString();
        String inputPW = editText_password.getText().toString();
        String inputPW2 = editText_password_2.getText().toString();

        infoModel.setInputEmail(inputEmail);
        infoModel.setInputPW(inputPW);
        infoModel.setInputPW2(inputPW2);

        id = inputEmail;

        if (inputEmail.length() > 0 && inputPW.length() > 0 && inputPW2.length() > 0) {
            if (inputPW.equals(inputPW2)) {
                if (inputPW.length() >= 6) {
                    registerUser(inputEmail, inputPW);
                } else {
                    Toast.makeText(getContext(), "비밀번호를 6자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        saveUserInfoToDatabase();
                    } else {
                        Toast.makeText(getContext(), "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserInfoToDatabase() {
        Map<String, Object> user = new HashMap<>();
        user.put("Address", "");
        user.put("Name", "");
        user.put("Gender", "");
        user.put("Birth", "");
        user.put("Data", "");
        user.put("Score", "");
        user.put("Guardian", "");
        user.put("My", "");

        db.collection("Users").document(id).set(user)
                .addOnSuccessListener(unused -> Log.d(TAG, "< 데이터베이스에 유저 정보 저장 성공 >"))
                .addOnFailureListener(e -> Log.d(TAG, "< 데이터베이스에 유저 정보 저장 실패 >"));

        navigateToNextFragment();
    }

    private void navigateToNextFragment() {
        Fragment fragment2 = new Register2Fragment();
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

        Log.w(TAG, "### Register - Pause");

        String inputEmail = editText_email.getText().toString();
        String inputPW = editText_password.getText().toString();
        String inputPW2 = editText_password_2.getText().toString();

        infoModel.setInputEmail(inputEmail);
        infoModel.setInputPW(inputPW);
        infoModel.setInputPW2(inputPW2);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register - Resume");

        String inputEmail = infoModel.getInputEmail();
        String inputPW = infoModel.getInputPW();
        String inputPW2 = infoModel.getInputPW2();

        editText_email.setText(inputEmail);
        editText_password.setText(inputPW);
        editText_password_2.setText(inputPW2);
    }

    private void check() {
        if (email_active == 1 && pw_active == 1 && pwc_active == 1) {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.Human));
        } else {
            text_next.setTextColor(ContextCompat.getColor(requireContext(), R.color.unable));
        }
    }
}