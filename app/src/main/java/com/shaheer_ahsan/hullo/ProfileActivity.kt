package com.shaheer_ahsan.hullo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {
    //Variables for Views
    lateinit var mProfileImage: ImageView
    lateinit var mProfileName: TextView
    lateinit var mProfileStatus:TextView
    lateinit var mProfileFriendsCount:TextView
    //Variables for buttons
    lateinit var mProfileSendReqBtn: Button
    lateinit var mDeclineBtn: Button
    //DB ref
    lateinit var mUsersDatabase: DatabaseReference
    // progress Dialog
    lateinit var dialog:  KProgressHUD
    //Friend Req DataBase
    lateinit var mFriendReqDatabase: DatabaseReference
    lateinit var mFriendDatabase: DatabaseReference
    lateinit var mNotificationDatabase: DatabaseReference

    lateinit var mRootRef: DatabaseReference

    lateinit var mCurrent_user: FirebaseUser

    lateinit var mCurrent_state: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user_id = intent.getStringExtra("user_id")

        mRootRef = FirebaseDatabase.getInstance().reference

        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(user_id)
        mFriendReqDatabase = FirebaseDatabase.getInstance().reference.child("Friend_req")
        mFriendDatabase = FirebaseDatabase.getInstance().reference.child("Friends")
        mNotificationDatabase = FirebaseDatabase.getInstance().reference.child("notifications")
        mCurrent_user = FirebaseAuth.getInstance().currentUser as FirebaseUser

        mProfileImage = findViewById<View>(R.id.profile_image) as ImageView
        mProfileName = findViewById<View>(R.id.profile_displayName) as TextView
        mProfileStatus = findViewById<View>(R.id.profile_status) as TextView
        mProfileFriendsCount = findViewById<View>(R.id.profile_totalFriends) as TextView
        mProfileSendReqBtn = findViewById<View>(R.id.profile_send_req_btn) as Button
        mDeclineBtn = findViewById<View>(R.id.profile_decline_btn) as Button


        mCurrent_state = "not_friends"

        mDeclineBtn.visibility = View.INVISIBLE
        mDeclineBtn.isEnabled = false

        ProgressDialogFunction()

        mUsersDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val display_name = dataSnapshot.child("name").value!!.toString()
                val status = dataSnapshot.child("status").value!!.toString()
                val image = dataSnapshot.child("image").value!!.toString()

                mProfileName.text = display_name
                mProfileStatus.text = status

                Picasso.with(this@ProfileActivity).load(image).placeholder(R.drawable.defaultavatar).into(mProfileImage)

                if (mCurrent_user.uid == user_id) {

                    mDeclineBtn.isEnabled = false
                    mDeclineBtn.visibility = View.INVISIBLE

                    mProfileSendReqBtn.isEnabled = false
                    mProfileSendReqBtn.visibility = View.INVISIBLE

                }


                //--------------- FRIENDS LIST / REQUEST FEATURE -----

                mFriendReqDatabase.child(mCurrent_user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            val req_type = dataSnapshot.child(user_id).child("request_type").value!!.toString()

                            if (req_type == "received") {

                                mCurrent_state = "req_received"
                                mProfileSendReqBtn.text = "Accept Friend Request"

                                mDeclineBtn.visibility = View.VISIBLE
                                mDeclineBtn.isEnabled = true

                            } else if (req_type == "sent") {

                                mCurrent_state = "req_sent"
                                mProfileSendReqBtn.text = "Cancel Friend Request"

                                mDeclineBtn.visibility = View.INVISIBLE
                                mDeclineBtn.isEnabled = false

                            }

                               dialog.dismiss()

                        } else {


                            mFriendDatabase.child(mCurrent_user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends"
                                        mProfileSendReqBtn.text = "Unfriend this Person"

                                        mDeclineBtn.visibility = View.INVISIBLE
                                        mDeclineBtn.isEnabled = false

                                    }
                                       dialog.dismiss()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    dialog.dismiss()
                                }
                            })

                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        mProfileSendReqBtn.setOnClickListener({     // todo need to remove if worked with remove one
            mProfileSendReqBtn.isEnabled = false

            // --------------- NOT FRIENDS STATE ------------

            if (mCurrent_state == "not_friends") {


                val newNotificationref = mRootRef.child("notifications").child(user_id).push()
                val newNotificationId = newNotificationref.key

                val notificationData = HashMap<String, String>()
                notificationData.put("from", mCurrent_user.uid)
                notificationData.put("type", "request")

                val requestMap = HashMap<String,Any>()
                requestMap.put("Friend_req/" + mCurrent_user.uid + "/" + user_id + "/request_type", "sent")
                requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.uid + "/request_type", "received")
                requestMap.put("notifications/$user_id/$newNotificationId", notificationData)

                mRootRef.updateChildren(requestMap, { databaseError, _ -> //todo instead of dash use Db reference to avoid problems
                    if (databaseError != null) {

                        Toast.makeText(this@ProfileActivity, "There was some error in sending request", Toast.LENGTH_SHORT).show()   //todo Change toast to anko one

                    } else {

                        mCurrent_state = "req_sent"
                        mProfileSendReqBtn.text = "Cancel Friend Request"

                        mDeclineBtn.visibility = View.INVISIBLE
                        mDeclineBtn.isEnabled = false

                    }

                    mProfileSendReqBtn.isEnabled = true
                })

            }


            // - -------------- CANCEL REQUEST STATE ------------

            if (mCurrent_state == "req_sent") {

                mFriendReqDatabase.child(mCurrent_user.uid).child(user_id).removeValue().addOnSuccessListener {
                    mFriendReqDatabase.child(user_id).child(mCurrent_user.uid).removeValue().addOnSuccessListener {
                        mProfileSendReqBtn.isEnabled = true
                        mCurrent_state = "not_friends"
                        mProfileSendReqBtn.text = "Send Friend Request"

                        mDeclineBtn.visibility = View.INVISIBLE
                        mDeclineBtn.isEnabled = false
                    }
                }

            }


            // ------------ REQ RECEIVED STATE ----------

            if (mCurrent_state == "req_received") {

                val currentDate = DateFormat.getDateTimeInstance().format(Date())

                val friendsMap = HashMap<String,Any>()
                friendsMap.put("Friends/" + mCurrent_user.uid + "/" + user_id + "/date", currentDate)
                friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.uid + "/date", currentDate)


                friendsMap.put("Friend_req/" + mCurrent_user.uid + "/" + user_id, parent)
                friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.uid, parent)


                mRootRef.updateChildren(friendsMap, { databaseError, _ ->
                    if (databaseError == null) {

                        mProfileSendReqBtn.isEnabled = true
                        mCurrent_state = "friends"
                        mProfileSendReqBtn.text = "Unfriend this Person"

                        mDeclineBtn.visibility = View.INVISIBLE
                        mDeclineBtn.isEnabled = false

                    } else {

                        val error = databaseError.message

                        Toast.makeText(this@ProfileActivity, error, Toast.LENGTH_SHORT).show()


                    }
                })

            }


            // ------------ UNFRIEND ---------

            if (mCurrent_state == "friends") {

                val unfriendMap = HashMap<String,Any>()
                unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, parent)
                unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), parent)

                mRootRef.updateChildren(unfriendMap, { databaseError, _ ->
                    if (databaseError == null) {

                        mCurrent_state = "not_friends"
                        mProfileSendReqBtn.text = "Send Friend Request"

                        mDeclineBtn.visibility = View.INVISIBLE
                        mDeclineBtn.isEnabled = false

                    } else {

                        val error = databaseError.message

                        Toast.makeText(this@ProfileActivity, error, Toast.LENGTH_SHORT).show() //todo change toast
                    }

                    mProfileSendReqBtn.isEnabled = true
                })

            }
        })


    }

    fun ProgressDialogFunction(){

        dialog = KProgressHUD.create(this@ProfileActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading User Data")
                .setDetailsLabel("Please wait while we load the user data.")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show()
    }

}
