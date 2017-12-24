package com.shaheer_ahsan.hullo


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView



class UsersActivity : AppCompatActivity() {

    private var mUsersDatabase: DatabaseReference? = null

    private var mLayoutManager: LinearLayoutManager? = null

    private lateinit var mUsersList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        //Toolbar
        val toolbar = findViewById<View>(R.id.userActivityToolbar) as android.support.v7.widget.Toolbar
        toolbar.title = "All Users"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users")

        mLayoutManager = LinearLayoutManager(this)

        mUsersList = findViewById<View>(R.id.users_list) as RecyclerView
        mUsersList.setHasFixedSize(true)
        mUsersList.layoutManager = mLayoutManager


    }// on create method


    override fun onStart() {
        super.onStart()


        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users::class.java,
                R.layout.users_single_layout,
                UsersViewHolder::class.java,
                mUsersDatabase

        ) {
            override fun populateViewHolder(usersViewHolder: UsersViewHolder, users: Users, position: Int) {

                usersViewHolder.setDisplayName(users.name)
                usersViewHolder.setUserStatus(users.status)
                usersViewHolder.setUserImage(users.thumb_image, applicationContext)

                val user_id = getRef(position).key

                usersViewHolder.mView.setOnClickListener {
                    val profileIntent = Intent(this@UsersActivity, ProfileActivity::class.java)
                    profileIntent.putExtra("user_id", user_id)
                    startActivity(profileIntent)
                }

            }
        }

        mUsersList.adapter = firebaseRecyclerAdapter

    }


    class UsersViewHolder(internal var mView: View) : RecyclerView.ViewHolder(mView) {

        fun setDisplayName(name: String) {

            val userNameView = mView.findViewById<View>(R.id.user_single_name) as TextView
            userNameView.text = name

        }

        fun setUserStatus(status: String) {

            val userStatusView = mView.findViewById<View>(R.id.user_single_status) as TextView
            userStatusView.text = status

        }

        fun setUserImage(thumb_image: String, ctx: Context) {

            val userImageView = mView.findViewById<View>(R.id.user_single_image) as CircleImageView

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defaultavatar).into(userImageView)

        }


    }





}//Main class


















/* todo not a good way to show the users (Custom Adapter should be deleted)



  <ListView
        android:id="@+id/listView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="55dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_bias="0.52"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/userActivityToolbar"
        app:layout_constraintVertical_bias="0.51" />





lateinit var listView:ListView
lateinit var mUsersList: MutableList<Users>
lateinit var mUserDatabase:DatabaseReference

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_users)

    //Toolbar
    val toolbar = findViewById<View>(R.id.userActivityToolbar) as android.support.v7.widget.Toolbar
    toolbar.title = "All Users"
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    //List of Users
    listView = findViewById(R.id.listView)
    mUsersList = mutableListOf()
    //DatabaseReference todo
    mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")
    // adding values
    mUserDatabase.addValueEventListener(object: ValueEventListener{
        override fun onCancelled(p0: DatabaseError?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(p0: DataSnapshot?) {

            if(p0!!.exists()){

                p0.children
                        .map { it.getValue(Users::class.java) }
                        .forEach { mUsersList.add(it!!) } // for end


                val adapter = CustomAdapter(applicationContext,R.layout.users_single_layout,mUsersList)
                listView.adapter = adapter

                listView.setOnItemClickListener{ adapterView: AdapterView<*>, view: View, position: Int, l: Long ->



                }
            }// if end
        }//onData change end
    }) // value event Listener Ends




}// on create method

*/

