package com.example.shinjiwoong.yeonsungcafe.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shinjiwoong.yeonsungcafe.Cafe.MainActivity;
import com.example.shinjiwoong.yeonsungcafe.Cafe.SelectCafe;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    EditText editLoginNum;
    EditText editLoginPwd;

    CheckBox autoLogin;
    SharedPreferences auto;
    SharedPreferences.Editor editor;

    private boolean saveLoginData;

    String id, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        editor = auto.edit();
//        load();

        autoLogin = findViewById(R.id.autoLogin);
        autoLogin.setChecked(saveLoginData);
        Button registBtn = findViewById(R.id.skipBtn);
        Button btnlogin = findViewById(R.id.btn_login_ok);

        editLoginNum = findViewById(R.id.edit_login_num);
        editLoginPwd = findViewById(R.id.edit_login_pwd);

        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnlogin.setOnClickListener(view -> {
            SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);

            if (editLoginNum.getText().toString().trim().equals("")
                    || editLoginPwd.getText().toString().trim().equals("")) {
                dialog.setTitleText("입력란을 모두 입력해주세요.");
                dialog.setCancelable(false);
                dialog.show();
            } else {
                Login(editLoginNum.getText().toString(), editLoginPwd.getText().toString());
            }
        });
    }


    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = auto.getBoolean("SAVE_LOGIN_DATA", false);
        id = auto.getString("ID", "");
        password = auto.getString("PWD", "");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void Login(String num, String psd) {

        mAuth.signInWithEmailAndPassword(num + "@ys.com", psd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (autoLogin.isChecked()) {
                                editor.putBoolean("SAVE_LOGIN_DATA", true);
                                editor.putString("ID", num);
                                editor.putString("PWD", psd);
                            } else {
                                editor.clear();
                            }
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), SelectCafe.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}