package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashAcitivty extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        Intent intent;
        if (mAuth.getCurrentUser() != null) {
            intent = new Intent(SplashAcitivty.this, HomeActivity.class);
        } else {
            intent = new Intent(SplashAcitivty.this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}