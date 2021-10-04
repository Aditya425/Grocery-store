package com.example.grocerystore.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.User
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private lateinit var userEmail: String
    private var mSelectedImageDownloadUri: String = ""
    private var mSelectedImageUri: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent.hasExtra(Constants.EMAIL)){
            userEmail = intent.getStringExtra(Constants.EMAIL) as String
        }

        showDialog()
        Firebase().getUserProfileActivity(FirebaseAuth.getInstance().currentUser!!.uid, this)

        tv_user_number.keyListener = null
        tv_user_email.keyListener = null

        iv_profile.setOnClickListener {
            pickImage()
        }

        btn_update.setOnClickListener {
            uploadData()
        }
    }

    private fun uploadData(){
        if (tv_user_name.text.toString().isNotEmpty()) {
            showDialog()
            if (mSelectedImageUri.isNotEmpty()) {
                FirebaseStorage.getInstance().reference.child("${System.currentTimeMillis()}." + getFileExtension())
                    .putFile(mSelectedImageUri.toUri())
                    .addOnSuccessListener {
                        it.storage.downloadUrl
                            .addOnSuccessListener { uri ->
                                hideDialog()
                                mSelectedImageDownloadUri = uri.toString()
                                uploadUser()
                            }.addOnFailureListener {
                                    hideDialog()
                                    showErrorSnackBox(it.message)
                                }
                    }.addOnFailureListener {
                            hideDialog()
                            showErrorSnackBox(it.message)
                        }
            }else{
                hideDialog()
                showErrorSnackBox("Please select an image")
            }
        }
    }

    private fun uploadUser(){
        showDialog()
        val user = User(tv_user_email.text.toString(), tv_user_name.text.toString(),
                tv_user_number.text.toString(), mSelectedImageDownloadUri,
                FirebaseAuth.getInstance().currentUser!!.uid)
        Firebase().uploadUserProfileActivity(user, this)
    }

    private fun getFileExtension(): String{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(mSelectedImageUri.toUri()))!!
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, USER_PROFILE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_PROFILE_CODE && resultCode == Activity.RESULT_OK && data !=null){
            Glide.with(this)
                .load(MediaStore.Images.Media.getBitmap(contentResolver!!, data.data))
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_profile)
            mSelectedImageUri = data.data.toString()
            Log.i("image extension", getFileExtension())
        }
    }

    fun getUserProfileSuccess(user: User){
        hideDialog()
        Glide.with(this)
            .load(user.image)
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile)
        tv_user_name.setText(user.name)
        tv_user_number.setText(user.mobile)
        tv_user_email.setText(user.email)
        mSelectedImageDownloadUri = user.image
    }

    fun uploadUserSuccess(){
        hideDialog()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object{
        const val USER_PROFILE_CODE = 1
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}