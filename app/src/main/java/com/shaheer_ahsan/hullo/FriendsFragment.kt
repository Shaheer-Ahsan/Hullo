package com.shaheer_ahsan.hullo


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 */
class FriendsFragment : Fragment {

    lateinit var mFriendsList: RecyclerView

    lateinit var mFriendsDatabase: DatabaseReference
    lateinit var mUsersDatabase: DatabaseReference

    lateinit var mAuth: FirebaseAuth

    lateinit var mCurrent_user_id: String

    lateinit var mMainView: View

    constructor() {
        // Required empty public constructor
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false)

        mFriendsList = mMainView.findViewById<View>(R.id.friends_list) as RecyclerView
        mAuth = FirebaseAuth.getInstance()

        mCurrent_user_id = mAuth.getCurrentUser()!!.uid

        mFriendsDatabase = FirebaseDatabase.getInstance().reference.child("Friends").child(mCurrent_user_id)
        mFriendsDatabase.keepSynced(true)
        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        mUsersDatabase.keepSynced(true)


        mFriendsList.setHasFixedSize(true)
        mFriendsList.layoutManager = LinearLayoutManager(context)

        // Inflate the layout for this fragment
        return mMainView
    }


    override fun onStart() {
        super.onStart()

        val friendsRecyclerViewAdapter = object : FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends::class.java,
                R.layout.users_single_layout,
                FriendsViewHolder::class.java,
                mFriendsDatabase


        ) {
            override fun populateViewHolder(friendsViewHolder: FriendsViewHolder, friends: Friends, i: Int) {

                friendsViewHolder.setDate(friends.date)

                val list_user_id = getRef(i).key

                mUsersDatabase.child(list_user_id).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val userName = dataSnapshot.child("name").value!!.toString()
                        val userThumb = dataSnapshot.child("thumb_image").value!!.toString()

                        if (dataSnapshot.hasChild("online")) {

                            val userOnline = dataSnapshot.child("online").value!!.toString()
                            friendsViewHolder.setUserOnline(userOnline)

                        }

                        friendsViewHolder.setName(userName)
                        friendsViewHolder.setUserImage(userThumb, context)

                        friendsViewHolder.mView.setOnClickListener {
                            val options = arrayOf<CharSequence>("Open Profile", "Send message")

                            val builder = AlertDialog.Builder(context)

                            builder.setTitle("Select Options")
                            builder.setItems(options) { _, i ->
                                //Click Event for each item. todo instead of dash use dialogInterface
                                if (i == 0) {

                                    val profileIntent = Intent(context, ProfileActivity::class.java)
                                    profileIntent.putExtra("user_id", list_user_id)
                                    startActivity(profileIntent)

                                }

                                if (i == 1) {

                                    val chatIntent = Intent(context, ChatActivity::class.java)
                                    chatIntent.putExtra("user_id", list_user_id)
                                    chatIntent.putExtra("user_name", userName)
                                    startActivity(chatIntent)

                                }
                            }

                            builder.show()
                            builder.setCancelable(true)
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

            }
        }

        mFriendsList.adapter = friendsRecyclerViewAdapter


    }


    class FriendsViewHolder(internal var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setDate(date: String) {

            val userStatusView = mView.findViewById<View>(R.id.user_single_status) as TextView
            userStatusView.text = date

        }

        fun setName(name: String) {

            val userNameView = mView.findViewById<View>(R.id.user_single_name) as TextView
            userNameView.text = name

        }

        fun setUserImage(thumb_image: String, ctx: Context) {

            val userImageView = mView.findViewById<View>(R.id.user_single_image) as CircleImageView
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defaultavatar).into(userImageView)

        }

        fun setUserOnline(online_status: String) {

            val userOnlineView = mView.findViewById<View>(R.id.user_single_online_icon) as ImageView

            if (online_status == "true") {

                userOnlineView.visibility = View.VISIBLE

            } else {

                userOnlineView.visibility = View.INVISIBLE

            }

        }


    }


}
