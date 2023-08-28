package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UserActivity extends AppCompatActivity {
    // Firebase
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    // 뷰
    TextView Text_Name;
    TextView Text_Birthday;
    EditText EditText_Address;
    EditText EditText_My;
    EditText EditText_Guardian;
    private ImageView imageView;
    private ProgressBar loadBar;

    // 입력 받은 정보를 저장할 공간
    public String name;
    public String address;
    public String my;
    public String guardian;

    // 변수
    Number school = 0;
    int year = 0;
    int month = 0;
    int day = 0;

    // 테스트 디데이 계산
    LocalDate today;
    LocalDate Testday;
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Log.w(TAG, "--- UserActivity ---");

        initializeViews();

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(UserActivity.this);

        TextView Text_Email = findViewById(R.id.text_userEmail);
        Text_Email.setText(id);

        today = LocalDate.now();

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        getData();

        storageReference = FirebaseStorage.getInstance().getReference().child("images");

        onPageTransition();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    ///////////////////////////////// 뷰 관련

    private void initializeViews() {
        Text_Name = findViewById(R.id.text_userName);
        Text_Birthday = findViewById(R.id.text_userBirth);

        imageView = findViewById(R.id.profileImageView);

        loadBar = findViewById(R.id.loadBar);
    }

    private class getDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩 화면 표시
            loadBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 백그라운드 작업 수행
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Text_Name.setText(documentSnapshot.getString("Name"));
                            Text_Birthday.setText(String.valueOf(documentSnapshot.getString("Birth")));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(View.GONE);
        }
    }

    private void getData() {
        // AsyncTask 실행
        new getDataTask().execute();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void onPageTransition() {
        // StorageReference를 통해 이미지 파일을 가져옵니다.
        StorageReference fileReference = storageReference.child("image.jpg");

        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // 이미지가 존재하는 경우, 이미지 다운로드 URL을 가져와 ImageView에 표시
            Glide.with(this)
                    .load(uri)
                    .into(imageView);
            Log.d(TAG, "저장된 프로필: " + "프로필 사진을 불러옴");
        }).addOnFailureListener(urlFailure -> {
            // 이미지가 존재하지 않는 경우, 토스트 메시지 띄우기
            Toast.makeText(this, "프로필 사진을 등록해 주세요.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            StorageReference fileReference = storageReference.child("image.jpg");

            // 이미지 업로드 수행
            UploadTask uploadTask = fileReference.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 이미지 업로드 성공 시, 이미지 다운로드 URL을 가져와 ImageView에 표시
                fileReference.getDownloadUrl().addOnSuccessListener(newDownloadUri -> {
                    if (newDownloadUri != null) {
                        // 이미지 다운로드 URL(uri)을 이용하여 이미지뷰에 이미지 설정
                        // Glide 라이브러리 등을 사용하여 이미지 로딩 가능
                        Glide.with(this)
                                .load(newDownloadUri)
                                .into(imageView);
                        Toast.makeText(this, "프로필 사진을 변경하셨습니다.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "프로필 변경: " + "프로필 사진을 변경함");
                    } else {
                        Log.d(TAG, "저장소에 이미지가 없습니다.");
                    }
                });

            }).addOnFailureListener(uploadFailure -> {
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // 현재 로그인 된 유저 정보 읽기
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String id = currentUser.getEmail();
    DocumentReference docRef = db.collection("Users").document(id);

    public void Save(View target)
    {
        // 입력한 정보 받기
        //address = ((EditText) findViewById(R.id.editText_address)).getText().toString();
        //my = ((EditText) findViewById(R.id.editText_my)).getText().toString();
        //guardian = ((EditText) findViewById(R.id.editText_guardian)).getText().toString();
        //imageView = ((ImageView) findViewById(R.id.profileImageView));

        // 입력한 정보 업데이트
        docRef.update("Address", address);
        docRef.update("My", my);
        docRef.update("Guardian", guardian);
        //docRef.update("ImageView", imageView);

        db.collection("Users")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                Toast.makeText(UserActivity.this, "저장되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                Intent intent = new Intent(getApplication(), UserActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(UserActivity.this, "실패하였습니다.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "No such document");
                            }
                        }
                        else
                        {
                            Toast.makeText(UserActivity.this, "실패하였습니다.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    // Logout 버튼 누를 경우
    public void Logout(View target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그아웃");
        builder.setMessage("정말 로그아웃을 하시겠습니까?");

        // '네' 버튼 설정
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 유저 로그아웃
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(UserActivity.this, "로그아웃 되었습니다.",
                        Toast.LENGTH_SHORT).show();

                // Login 화면으로 이동
                Intent intent = new Intent(getApplication(), LoginMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // '아니오' 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserActivity.this, "로그아웃을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 팝업 닫기
            }
        });

        // 팝업 표시
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 텍스트 색상 변경
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.BLACK);
        negativeButton.setTextColor(Color.BLACK);
    }

    // 회원 탈퇴 버튼 누를 경우
    public void withdraw(View target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원 탈퇴");
        builder.setMessage("정말 탈퇴하시겠습니까?");

        // '네' 버튼 설정
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 유저 삭제
                String email = currentUser.getEmail();
                currentUser.delete();

                // 유저 정보 삭제
                db.collection("Users").document(email)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserActivity.this, "탈퇴하셨습니다.", Toast.LENGTH_SHORT).show();

                                // 로그인 화면으로 이동
                                Intent intent = new Intent(getApplication(), LoginMainActivity.class);
                                startActivity(intent);
                                finish();

                                Log.d(TAG, "< 유저 정보 삭제 성공 >");
                                Log.d(TAG, "< 회원 탈퇴 > Email: " + email);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "< 유저 정보 삭제 실패 >", e);
                            }
                        });
            }
        });

        // '아니오' 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserActivity.this, "회원 탈퇴를 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 팝업 닫기
            }
        });

        // 팝업 표시
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 텍스트 색상 변경
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.BLACK);
        negativeButton.setTextColor(Color.BLACK);
    }
}