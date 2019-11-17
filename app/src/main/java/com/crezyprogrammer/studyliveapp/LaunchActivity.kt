package com.crezyprogrammer.studyliveapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.rbddevs.splashy.Splashy

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        try {
            setSplashy()
        } catch (e: Exception) {
            val intent = Intent(this@LaunchActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun setSplashy() {
        Splashy(this)         // For JAVA : new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle(R.string.app_name)
                .setTitleColor("#FFFFFF")
                .setSubTitle("Largest English Learning Hub In Bangladesh")
                .setSubTitleColor("#FFFFFF")
                .setProgressColor(R.color.white)
                .setAnimation(Splashy.Animation.GLOW_LOGO, 200)
                .setFullScreen(true)
                .setTime(2000)
                .show()

        Splashy.onComplete(object : Splashy.OnComplete {
            override fun onComplete() {
                if (FirebaseAuth.getInstance().currentUser==null) {

                    val intent = Intent(this@LaunchActivity, SignInActivity::class.java)
                    startActivity(intent)

                }
                else{
                    val intent = Intent(this@LaunchActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })


    }

}