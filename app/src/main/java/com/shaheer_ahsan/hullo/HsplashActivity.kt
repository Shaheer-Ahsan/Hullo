package com.shaheer_ahsan.hullo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.shaheer_ahsan.hullo.R.anim.abc_slide_in_bottom
import kotlinx.android.synthetic.main.activity_hsplash.*
import com.pawegio.kandroid.loadAnimation

class HsplashActivity : AppCompatActivity() {

    private val counts = 3500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hsplash)

        val logohullo = loadAnimation(R.anim.logotrans)
        hulloView.startAnimation(logohullo)

        val logochat = loadAnimation(abc_slide_in_bottom)
        chatView.startAnimation(logochat)

        Handler().postDelayed({

            val intent = Intent(this@HsplashActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        },counts.toLong())

    }
}
