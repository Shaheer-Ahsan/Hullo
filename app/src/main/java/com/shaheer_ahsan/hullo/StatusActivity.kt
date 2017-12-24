package com.shaheer_ahsan.hullo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_status.*
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.toast

class StatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

//Firebase
        val current_user = FirebaseAuth.getInstance().currentUser
        val uId = current_user!!.uid
//Database
        val mStatusDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(uId) as DatabaseReference

//Toolbar
        val toolbar = findViewById<View>(R.id.changeStatusToolbar) as android.support.v7.widget.Toolbar
        toolbar.title="Account Status"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//get value from intent putExtra
        var status_value = intent.getStringExtra("status_value")
        status_input.editText!!.setText(status_value)
//onClick btn save for Status
        status_save_button.setOnClickListener {
            //progressDialog
            val dialog = progressDialog(message = "Please wait while we save the changes", title = "Saving changes")
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            //getStatus and setStatus
            var status = status_input.editText!!.text.toString()
            mStatusDatabase.child("status").setValue(status).addOnCompleteListener(this) { task ->

                if(task.isSuccessful){
                    dialog.dismiss()
                } else {
                    toast("There was some error in saving changes")
                }
            }
        } // status save button on click listener en.

    }// on Create Method
}//MainClass
