package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // 정보 입력 받을 공간
    TextView Text_Name;
    TextView Text_Birthday;
    EditText EditText_Address;
    EditText EditText_My;
    EditText EditText_Guardian;
    TextView Text_Date;
    TextView Text_Score;
    TextView Text_School;

    // 입력 받은 정보를 저장할 공간
    public String name;
    public String address;
    public String my;
    public String guardian;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Text_Name = findViewById(R.id.text_Name);
        Text_Birthday = findViewById(R.id.text_birthday);
        EditText_Address = findViewById(R.id.editText_address);
        EditText_My = findViewById(R.id.editText_my);
        EditText_Guardian = findViewById(R.id.editText_guardian);
        Text_Date = findViewById(R.id.text_date);
        Text_Score = findViewById(R.id.text_score);
        Text_School = findViewById(R.id.text_school);
        imageView = findViewById(R.id.profileImageView);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(UserActivity.this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView Text_Email = findViewById(R.id.text_email);
        Text_Email.setText(id);

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String name = documentSnapshot.getString("Name");
                    String fullName = name + "님";
                    Text_Name.setText(fullName);
                    Text_Birthday.setText(String.valueOf(documentSnapshot.getString("Birth")));
                    EditText_Address.setText(documentSnapshot.getString("Address"));
                    EditText_My.setText(documentSnapshot.getString("My"));
                    EditText_Guardian.setText(documentSnapshot.getString("Guardian"));
                    Text_Date.setText(documentSnapshot.getString("Date"));
                    Text_Score.setText(String.valueOf(documentSnapshot.getLong("Score")));

                    switch (Integer.parseInt(documentSnapshot.getLong("School").toString()))
                    {
                        case 0:
                            Text_School.setText("졸업한 학교가 없어요");
                            break;
                        case 1:
                            Text_School.setText("초등학교를 졸업했어요");
                            break;
                        case 2:
                            Text_School.setText("중학교를 졸업했어요");
                            break;
                        case 3:
                            Text_School.setText("고등학교를 졸업했어요");
                            break;
                        case 4:
                            Text_School.setText("대학교를 졸업했어요");
                            break;
                        case 5:
                            Text_School.setText("대학원을 졸업했어요");
                            break;
                        default:
                            break;
                    }
/*
                    // 이미지 URL 가져와서 이미지뷰에 표시
                    String imageUrl = documentSnapshot.getString("images");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(UserActivity.this)
                                .load(imageUrl)
                                .into(imageView); // imageView: 이미지를 표시할 ImageView 객체
                    }*/
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("images");

        onPageTransition();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
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
            Toast.makeText(this, "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
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
        address = ((EditText) findViewById(R.id.editText_address)).getText().toString();
        my = ((EditText) findViewById(R.id.editText_my)).getText().toString();
        guardian = ((EditText) findViewById(R.id.editText_guardian)).getText().toString();
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
                Intent intent = new Intent(getApplication(), LoginActivity.class);
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
        builder.setMessage("정말 회원을 탈퇴 하시겠습니까?");

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
                                Toast.makeText(UserActivity.this, "회원을 탈퇴하셨습니다.", Toast.LENGTH_SHORT).show();

                                // 로그인 화면으로 이동
                                Intent intent = new Intent(getApplication(), LoginActivity.class);
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