package com.crezyprogrammer.studyliveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.rbddevs.splashy.Splashy;
import com.rbddevs.splashy.Splashy.Companion;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Do something after 100ms
            if (FirebaseAuth.getInstance().getCurrentUser()==null) {
                startActivity(new Intent(this, SignInActivity.class));
                
            }
            else
                startActivity(new Intent(this, MainActivity.class));
            
        }, 2000);

        }

}
