package com.shaheer_ahsan.hullo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.kaopiz.kprogresshud.KProgressHUD

class LoginActivity : AppCompatActivity() {

    //Firebase
    lateinit var mAuth: FirebaseAuth

    //Variables
    private var loggingEmail:String?=null
    private var loggingPassword:String?=null

    //progress Dialog
    lateinit var dialog:KProgressHUD

    //DbRef
    lateinit var mUserDatabase:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login_button = findViewById<View>(R.id.login_button) as Button
        val email_log = findViewById<View>(R.id.email_log) as TextView
        val password_log = findViewById<View>(R.id.password_log) as TextView
        //Firebase
        mAuth = FirebaseAuth.getInstance()

        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")

        //Toolbar for Login page
        val toolbar = findViewById<View>(R.id.login_toolbar) as android.support.v7.widget.Toolbar
        toolbar.title="Login Account"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Getting Data from user on the click button
        login_button.setOnClickListener{
            loggingEmail = email_log.text.toString()
            loggingPassword = password_log.text.toString()

            if(loggingEmail!!.isNotEmpty() || loggingPassword!!.isNotEmpty()){

                ProgressDialogFunction()

                loggingUser(loggingEmail!!, loggingPassword!!)
            }

        }

    }

    fun ProgressDialogFunction(){

        dialog = KProgressHUD.create(this@LoginActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Logging In")
                .setDetailsLabel("Checking your credentials…")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .setCancellable(false)
                .show()
    }

    private fun loggingUser(loggingEmail: String, loggingPassword: String) {

        mAuth.signInWithEmailAndPassword(loggingEmail,loggingPassword)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                         dialog.dismiss()

                            val current_user_id = mAuth.currentUser!!.uid
                            val deviceToken = FirebaseInstanceId.getInstance().token

                            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener {
                                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }

                    }else{
                        dialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Sign In failed, Please try again.",
                                Toast.LENGTH_SHORT).show()
                    }

                }
    }
}

