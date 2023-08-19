package com.example.white_butterfly;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class AccessFragment extends DialogFragment {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 123;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("'하얀 나비'에서 다음 권한을 요청합니다.")
                .setMessage("마이크 사용")
                .setNegativeButton("허용 안 함", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 권한 허용 안 함 버튼 클릭 시 동작 (원하는 코드 추가)
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "확인" 버튼 클릭 시 동작 (원하는 코드 추가)
                        checkAndRequestMicrophonePermission();
                    }
                });

        return builder.create();
    }

    private void checkAndRequestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            startListening(); // 마이크 활성화
        }
    }

    private void startListening() {
        // 마이크 활성화 코드 구현


    }
}
