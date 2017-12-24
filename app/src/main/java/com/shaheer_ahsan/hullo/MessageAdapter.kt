package com.shaheer_ahsan.hullo

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.os.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import de.hdodenhof.circleimageview.CircleImageView

/**
 * Shaheer
 * */

class MessageAdapter(private var mMessageList: MutableList<Messages>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var mUserDatabase: DatabaseReference? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_single_layout, parent, false)

        return MessageViewHolder(v)

    }

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var messageText: TextView
        var profileImage: CircleImageView
        var displayName: TextView
        var messageImage: ImageView

        init {

            messageText = view.findViewById<View>(R.id.message_text_layout) as TextView
            profileImage = view.findViewById<View>(R.id.message_profile_layout) as CircleImageView
            displayName = view.findViewById<View>(R.id.name_text_layout) as TextView
            messageImage = view.findViewById<View>(R.id.message_image_layout) as ImageView

        }
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, i: Int) {

        val c = mMessageList[i]

        val from_user = c.from
        val message_type = c.type


        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(from_user)

        mUserDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val name = dataSnapshot.child("name").value!!.toString()
                val image = dataSnapshot.child("thumb_image").value!!.toString()

                viewHolder.displayName.text = name

                Picasso.with(viewHolder.profileImage.context).load(image)
                        .placeholder(R.drawable.defaultavatar).into(viewHolder.profileImage)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        if (message_type == "text") {

            viewHolder.messageText.text = c.message
            viewHolder.messageImage.visibility = View.INVISIBLE


        } else {

            viewHolder.messageText.visibility = View.INVISIBLE
            Picasso.with(viewHolder.profileImage.context).load(c.message)
                    .placeholder(R.drawable.defaultavatar).into(viewHolder.messageImage)

        }

    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }


}