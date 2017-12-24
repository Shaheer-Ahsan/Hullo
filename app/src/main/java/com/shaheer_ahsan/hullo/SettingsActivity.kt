package com.shaheer_ahsan.hullo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_settings.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import org.jetbrains.anko.toast
import java.io.File
import java.io.ByteArrayOutputStream


open class SettingsActivity : AppCompatActivity() {


    private var GALLERY_PICK: Int = 1
    lateinit var dialog:KProgressHUD
    //FireBase DB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mCurrentUser = FirebaseAuth.getInstance().currentUser
        val current_uid = mCurrentUser!!.uid

         val mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(current_uid) as DatabaseReference //todo
         mUserDatabase.keepSynced(true)

        mUserDatabase.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {

                val name = dataSnapshot!!.child("name").value.toString()
                val image = dataSnapshot.child("image").value.toString()
                val status = dataSnapshot.child("status").value.toString()
                var thumb_image = dataSnapshot.child("thumb_image").value.toString()

                // retrieving data
                settings_display_name.text = name
                settings_status.text = status
                if(image!="default") {             // agar to image equal nai hai default k tb picture set hogi warna nai
                 //  Picasso.with(this@SettingsActivity).load(image).placeholder(R.drawable.defaultavatar).into(settings_image) // testing purpose

                   Picasso.with(this@SettingsActivity).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.defaultavatar).into(settings_image, object : Callback {
                       override fun onSuccess() {
                       }

                        override fun onError() {
                           Picasso.with(this@SettingsActivity).load(image).placeholder(R.drawable.defaultavatar).into(settings_image)
                        }
                    })

                }

            }

            override fun onCancelled(databaseError: DatabaseError?) {
                // Todo: will fill this function later
            }
        })

        settings_status_button.setOnClickListener {

            var status_value = settings_status.text.toString()

            val intent = Intent(this@SettingsActivity,StatusActivity::class.java)
            intent.putExtra("status_value",status_value)
            startActivity(intent)
        }// on click listener End

        settings_image_button.setOnClickListener {

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, GALLERY_PICK)
                    }

        }// on click listener End



    }//on Create

    fun ProgressDialogFunction(){

        dialog = KProgressHUD.create(this@SettingsActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Uploading Image...")
                .setDetailsLabel("Please wait while we upload & process the image.")
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .setCancellable(false)
                .show()
    }


    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    override public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==GALLERY_PICK && resultCode== Activity.RESULT_OK){

                //Cropping image
            val imageUri = data!!.data as Uri
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this)

        } // if end

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode === Activity.RESULT_OK) {

                ProgressDialogFunction() // Progress Dialog Running

                val resultUri = result.uri
                val thumb_filepPath = File(resultUri.path)

                val mCurrentUser = FirebaseAuth.getInstance().currentUser
                val current_user_id = mCurrentUser!!.uid
                //Storage Reference
                val mImageStorage = FirebaseStorage.getInstance().reference
                //Bitmap image todo
                var  thumb_bitmap : Bitmap =  Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepPath)

                val boas = ByteArrayOutputStream()
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas)
                val Thumb_Byte = boas.toByteArray()

                val filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg")
                val thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg")


                filepath.putFile(resultUri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {

                        if(task.isSuccessful){
                            val download_url = task.result.downloadUrl.toString()
                            //ThumbFile handling
                            val uploadTask:UploadTask = thumb_filepath.putBytes(Thumb_Byte)

                                uploadTask.addOnCompleteListener{ thumb_task ->

                                    var thumb_downloadurl = thumb_task.result.downloadUrl.toString()

                                    if (thumb_task.isSuccessful){

                                        var update_hashmap = mutableMapOf<String,Any>()
                                        update_hashmap.put("image",download_url)
                                        update_hashmap.put("thumb_image",thumb_downloadurl)

           /*todo check*/               val mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(current_user_id) as DatabaseReference
                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener {

                                            if(task.isSuccessful) {
                                                dialog.dismiss()
                                                toast("successfully uploaded")
                                                    //todo: yahan se kam shuru krna hai
                                            }//nested if end

                                        }// on complete listener end

                                    } //ifEnd
                                    else{
                                        toast("Error in uploading Thumbnail")
                                        dialog.dismiss()
                                    } //else end

                                } // uploadTask On cOmplete Listener end

                        }else{
                            toast("Error in uploading")
                            dialog.dismiss()
                        }
                    }//if End

                }) //on complete Listener Ends

            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

               toast("Error")

            }//nested if end
        }//if end

    } //overide fun ends

}//ClassMain
