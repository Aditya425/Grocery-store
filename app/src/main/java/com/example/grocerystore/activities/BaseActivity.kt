package com.example.grocerystore.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerystore.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {
    lateinit var dialog: Dialog
    var doubleBackToExit: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showErrorSnackBox(text: String?) {
        Snackbar.make(findViewById(android.R.id.content), text!!, Snackbar.LENGTH_SHORT).show()
    }

    fun showDialog() {
        dialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun hideDialog() {
        dialog.dismiss()
    }

    fun getCurrentUid(): String? {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun backButton(){
        if (doubleBackToExit){
            finish()
        }
        doubleBackToExit = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG).show()

        Handler().postDelayed({
            doubleBackToExit = false
        }, 2000)
    }
}
