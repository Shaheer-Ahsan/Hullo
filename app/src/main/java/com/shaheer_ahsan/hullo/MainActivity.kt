package com.shaheer_ahsan.hullo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.kaopiz.kprogresshud.KProgressHUD
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    //ProgressDialog
    lateinit var dialog:KProgressHUD
    lateinit var mUserRef : DatabaseReference

    //class
    private var mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Firebase
         mAuth = FirebaseAuth.getInstance()

        //Toolbar
        val toolbar = findViewById<View>(R.id.main_toolbar) as android.support.v7.widget.Toolbar
        // toolbar.title="Hullo"
        toolbar.setNavigationIcon(R.drawable.toolbaricon)
        setSupportActionBar(toolbar)

        //User Reference
        if (mAuth.currentUser != null) {

            mUserRef = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)

        }

        //Tabs
        val mViewPage = findViewById<View>(R.id.main_tabpager) as ViewPager
        mViewPage.adapter = mSectionsPagerAdapter

        val mTabLayout = findViewById<View>(R.id.main_tabs) as TabLayout
        mTabLayout.setupWithViewPager(mViewPage)


}

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser

        if (currentUser == null){
            sendToStart()
        } else {

            mUserRef.child("online").setValue("true")

        }

    }


    override fun onStop() {
        super.onStop()

        val currentUser = mAuth.currentUser

        if (currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP)

        }

    }


    private fun sendToStart() {
        val intent = Intent(this@MainActivity,StartActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.main_logout_btn -> {
                ProgressDialogFunction()
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP)
                FirebaseAuth.getInstance().signOut()
                sendToStart()
            }
            R.id.main_setting_btn -> {
                val settingIntent = Intent(this@MainActivity,SettingsActivity::class.java)
                startActivity(settingIntent)
            }
            R.id.all_users_btn -> {
                val allUserIntent = Intent (this@MainActivity,UsersActivity::class.java)
                startActivity(allUserIntent)
            }
    }
        return super.onOptionsItemSelected(item)
    }

    fun ProgressDialogFunction(){

        dialog = KProgressHUD.create(this@MainActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Logging out")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .setCancellable(false)
                .show()
    }


}
