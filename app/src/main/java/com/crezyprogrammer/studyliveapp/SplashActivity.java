package com.crezyprogrammer.studyliveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.rbddevs.splashy.Splashy;
import com.rbddevs.splashy.Splashy.Companion;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       new Splashy(this).  // For JAVA : new Splashy(this)
        setLogo(R.drawable.splashy)
                .setTitle("Splashy")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Splash screen made easy")
                .setProgressColor(R.color.white)
                .setBackgroundResource(Color.RED)
                .setFullScreen(true)
                .setTime(5000)
                .show();

        }

}
