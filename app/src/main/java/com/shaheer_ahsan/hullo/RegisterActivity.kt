package com.shaheer_ahsan.hullo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.kaopiz.kprogresshud.KProgressHUD


class RegisterActivity : AppCompatActivity() {

    private var rname: String? = null
    private var remail: String? = null
    private var rpass: String? = null
    // Firebase
    private var mAuth: FirebaseAuth? = null
    //Progress bar
    lateinit var dialog:KProgressHUD


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registor_btn = findViewById<View>(R.id.registor_btn) as Button
        val displayname_reg = findViewById<View>(R.id.displayname_reg) as TextView
        val email_reg = findViewById<View>(R.id.email_reg) as TextView
        val password_log = findViewById<View>(R.id.password_log) as TextView

        //Firebase
        mAuth = FirebaseAuth.getInstance()
        //register button
        registor_btn.setOnClickListener {

            rname = displayname_reg.text.toString().trim()
            remail = email_reg.text.toString().trim()
            rpass = password_log.text.toString().trim()

                if(rname!!.isNotEmpty() || remail!!.isNotEmpty() || rpass!!.isNotEmpty()) {
                    ProgressDialogFunction()
                    registerUser(rname!!, remail!!, rpass!!)
                } else {
                    toast("Please fill the fields")
                }

        }//on click btn
        //Toolbar
            val toolbar = findViewById<View>(R.id.register_toolbar) as android.support.v7.widget.Toolbar
            toolbar.title="Create Account"
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }//oncreate

        fun ProgressDialogFunction(){

            dialog = KProgressHUD.create(this@RegisterActivity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Registering User")
                    .setDetailsLabel("Please wait while we create your account !")
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .setCancellable(false)
                    .show()
        }

    private fun registerUser(rname: String, remail: String, rpass: String) {
        mAuth!!.createUserWithEmailAndPassword(remail, rpass)
                .addOnCompleteListener(this) { task ->

                    val uId: String?

                    if (task.isSuccessful) {

                        val current_user = FirebaseAuth.getInstance().currentUser
                        uId = current_user!!.uid

                        val mdatabase = FirebaseDatabase.getInstance().reference.child("Users").child(uId) as DatabaseReference

                        val device_token = FirebaseInstanceId.getInstance().token

                        val userMap = HashMap<String,String>()
                        userMap.put("name",rname)
                        userMap.put("status","Hi there!, I am using Hullo.")
                        userMap.put("image","default")
                        userMap.put("thumb_image", "default")
                        userMap.put("device_token", device_token!!)

                        //pasing value to dB
                        mdatabase.setValue(userMap).addOnCompleteListener{

                            if(task.isSuccessful){
                                dialog.dismiss()

                                val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                       dialog.dismiss()
                        Toast.makeText(this@RegisterActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}//appcompact