package com.example.shinjiwoong.yeonsungcafe.Cafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shinjiwoong.yeonsungcafe.Login.LoginActivity;
import com.example.shinjiwoong.yeonsungcafe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends Activity {

    SharedPreferences saveLogin;
    String id, psd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        saveLogin = getSharedPreferences("autoLogin", MODE_PRIVATE);

        load();

        Button StartBtn = findViewById(R.id.startBtn);

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.equals("") || psd.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    login(id, psd);
                }
            }
        });
    }

    private void load() {
        id = saveLogin.getString("ID", "");
        psd = saveLogin.getString("PWD", "");
    }

    private void login(String id, String psd) {
        mAuth.signInWithEmailAndPassword(id, psd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent intent = new Intent(getApplicationContext(), SelectCafe.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(), "로그인 오류", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}