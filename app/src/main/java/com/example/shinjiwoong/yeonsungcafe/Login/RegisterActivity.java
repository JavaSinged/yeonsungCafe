package com.example.shinjiwoong.yeonsungcafe.Login;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    public Toolbar toolBar;
    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 타이틀 바에 뒤로가기 추가와 타이틀 바 이름 변경
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //이름,학번,비밀번호 주소값 가져오기
        EditText name = findViewById(R.id.edit_name);
        EditText num = findViewById(R.id.edit_num);
        EditText pwd = findViewById(R.id.edit_pwd);
        EditText pwdcheck = findViewById(R.id.edit_pwdcheck);

        name.setFilters(new InputFilter[]{filterKor});

        Button btnok = findViewById(R.id.btn_ok);
        //완료 버튼 실행시 이름,학번,비밀번호의 데이터를 putExtra로 담음
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE);

                if (name.getText().toString().trim().equals("")
                        || num.getText().toString().trim().equals("")
                        || pwd.getText().toString().trim().equals("")
                        || pwdcheck.getText().toString().trim().equals("")) {
                    dialog.setTitleText("입력란을 모두 입력해주세요.");
                    dialog.setCancelable(false);
                    dialog.show();
                } else if (!pwd.getText().toString().equals(pwdcheck.getText().toString())) {
                    dialog.setTitleText("비밀번호 란에 오류가 있습니다.");
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    signUp(name.getText().toString(), num.getText().toString(), pwd.getText().toString());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signUp(String name, String num, String password) {

        SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity.this);
        HashMap<String, String> error = new HashMap<>();

        error.put("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.", "이미 존재하는 아이디입니다.");
        error.put("com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]", "비밀번호가 6자 이하입니다.");
        error.put("com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.", "인터넷이 연결되어 있지 않습니다.");

        mAuth.createUserWithEmailAndPassword(num + "@ys.com", password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText(name + "님의<br>아이디 " + num + "이(가)<br>활성화 되었습니다.");
                            dialog.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            if (error.containsKey(task.getException().toString())) {
                                dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText(error.get(task.getException().toString()));
                                dialog.setCancelable(false);
                                dialog.show();
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void Logout() {
        //로그아웃
        FirebaseAuth.getInstance().signOut();
    }

    //한글만 입력처리
    public InputFilter filterKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
