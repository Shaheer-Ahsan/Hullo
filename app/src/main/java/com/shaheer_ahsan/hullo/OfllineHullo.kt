package com.shaheer_ahsan.hullo

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

/*
 * Created by shaheer on 01/12/2017. 03056251032
 */

class OfflineHullo: Application() {

    lateinit var mUserDatabase: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(){
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true) //adding offline capability of FireBase.

        /* Picasso */
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttpDownloader(this, Integer.MAX_VALUE.toLong()))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {

            mUserDatabase = FirebaseDatabase.getInstance()
                    .reference.child("Users").child(mAuth.currentUser!!.uid)
            mUserDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot?) {

                    if (dataSnapshot != null) {

                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        }


    }


}
