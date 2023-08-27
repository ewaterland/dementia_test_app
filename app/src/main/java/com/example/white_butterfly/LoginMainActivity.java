package com.example.white_butterfly;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginMainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;

    // 버튼
    private Button kakaologinButton;
    private Button emailloginButton;

    // TAG
    private static final String TAG = "LoginMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        Log.e("Debug", Utility.INSTANCE.getKeyHash(this));

        // SDK 초기화
        KakaoSdk.init(this, "7dbd9a5212706340ef14160f7b431a33");

        kakaologinButton = findViewById(R.id.kakaologin);
        emailloginButton = findViewById(R.id.emaillogin);

        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    // 홈 화면으로 이동
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();

                    Log.d("카카오 로그인 성공", "카카오 로그인 성공");
                } else {
                    Log.d("카카오 로그인 실패", "카카오 로그인 실패");
                }
                return null;
            }
        };

        // 카카오 로그인
        kakaologinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginMainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginMainActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginMainActivity.this, callback);
                }
            }
        });


        // 이메일 로그인
        emailloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                finish();

                Log.d(ContentValues.TAG, "< emain 로그인으로 이동 >");
            }
        });
    }
}
