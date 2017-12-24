package com.shaheer_ahsan.hullo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        signinbutton.setOnClickListener{
            startActivity(intentFor<LoginActivity>("id" to 5).singleTop())
        }

        signupbutton.setOnClickListener {
            startActivity(intentFor<RegisterActivity>("id" to 5).singleTop())
        }

    }
}
